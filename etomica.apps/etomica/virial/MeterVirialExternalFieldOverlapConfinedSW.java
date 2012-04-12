package etomica.virial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import etomica.api.IAtomList;
import etomica.data.DataTag;
import etomica.data.IData;
import etomica.data.IEtomicaDataInfo;
import etomica.data.types.DataDoubleArray;
import etomica.data.types.DataDoubleArray.DataInfoDoubleArray;
import etomica.graph.model.Graph;
import etomica.graph.property.HasSimpleArticulationPoint;
import etomica.graph.traversal.BCVisitor;
import etomica.graph.traversal.Biconnected;
import etomica.units.Null;
import etomica.util.DoubleRange;
import etomica.virial.cluster.ExternalVirialDiagrams;

/**
 * Measures value of clusters in a box and returns the values
 * divided by the sampling bias from the sampling cluster.
 */
public class MeterVirialExternalFieldOverlapConfinedSW implements ClusterWeightSumWall.DataSourceClusterWall, java.io.Serializable {
	
	private double lambdaWF;
	private double temperature;
	private double epsilonWF;
    /**
	 * Constructor for MeterVirial.
	 */
	public MeterVirialExternalFieldOverlapConfinedSW(ExternalVirialDiagrams diagrams, MayerFunction f, double [] walldistance, double lambdaWF, double temperature, double epsilonWF) {
		Set<Graph> gset = diagrams.getMSMCGraphsEX(true);
		clusters = new ArrayList<ClusterAbstract>(gset.size());
		listsPoint = new ArrayList<List<List<Byte>>>(gset.size());
		comparator = new RangeComparator();
	    		
		for(Graph g : gset){

			ArrayList<ClusterBonds> allBonds = new ArrayList<ClusterBonds>();
            ArrayList<Double> weights = new ArrayList<Double>();
            diagrams.populateEFBonds(g, allBonds, weights, false);  
            double [] w = new double[]{weights.get(0)};            

            clusters.add(new ClusterSum(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{f}));
            ArrayList<List<Byte>> listComponent = new ArrayList<List<Byte>>();
            ArrayList<Byte> listPointG = new ArrayList<Byte>();
            listPointG.add((byte)0);
            listComponent.add(listPointG);
            List<List<Byte>> biComponents = new ArrayList<List<Byte>>();
            BCVisitor v = new BCVisitor(biComponents);
            new Biconnected().traverseAll(g, v);
            HasSimpleArticulationPoint hap = new HasSimpleArticulationPoint();
            hap.check(g);
            
            List<List<Byte>> prvLayerList = new ArrayList<List<Byte>>();
            List<Byte> prvApList = new ArrayList<Byte>();
            prvApList.add((byte)0);
            while (!prvApList.isEmpty()){
            	List<List<Byte>> layerList = new ArrayList<List<Byte>>();
	            List<Byte> apList = new ArrayList<Byte>();
		        for(byte prvAP : prvApList){
		            
		            for(List<Byte> biComponent : biComponents){
		            	if(!biComponent.contains(prvAP) || prvLayerList.contains(biComponent)){
		            		continue;
		            	}
		        		boolean isterminal = true;
		        		
		        		for(byte b : biComponent){
		        			if(b == prvAP){
		        				continue;
		        			}
		        			if(hap.getArticulationPoints().contains(b)){
		        				isterminal = false;
		        				apList.add(b);
		        				layerList.add(biComponent);
		        					
		        			}
		        			
		        		}
		        		List<Byte> listAll = new ArrayList<Byte>();
		        		listAll.addAll(biComponent);
		        		listAll.remove((Byte)prvAP);
		        		if(isterminal){
		        			listComponent.add(listAll);
		        		}
		        		else {
		        			listPointG.addAll(listAll);
		    			}
		            }
		        }
		        prvApList = apList;
		        prvLayerList = layerList;
            }
            listsPoint.add(listComponent);
            
		}
		
		for (ClusterAbstract cluster : clusters){
			cluster.setTemperature(1);
		}
		
		this.wallDistance = walldistance;
		this.lambdaWF = lambdaWF;
		this.temperature =temperature;
		this.epsilonWF =epsilonWF;
        data = new DataDoubleArray(walldistance.length + 2);
        dataInfo = new DataInfoDoubleArray("Cluster Value",Null.DIMENSION, new int[]{walldistance.length + 2});
        tag = new DataTag();
        dataInfo.addTag(tag);
	}

	public IEtomicaDataInfo getDataInfo() {
        return dataInfo;
    }
    
    public DataTag getTag() {
        return tag;
    }
    
    public IData getData() {
    	
        double x[] = data.getData();
        IAtomList atoms = box.getLeafList();
        for(int i=0; i<x.length; i++){
        	x[i]=0;
        }                    
            
    	for(int c=0; c<clusters.size();c++){
    		double v =clusters.get(c).value(box);        			
    		if (v==0){
    	    	continue;
    	    }
    		      
    	    List<List<Byte>> cList = listsPoint.get(c);
    	    List<Byte> gList = cList.get(0);    	    
    	 
    	    double glowestatom = 0.0;
    	    double ghighestatom = 0.0;     	   	    
    	    double [] z = new double [atoms.getAtomCount()];       	   
    	    
    	    for(byte g : gList){
    	    	z[g] = atoms.getAtom(g).getPosition().getX(2);
    	    	if (z[g] < glowestatom){
    	    		glowestatom = z[g];    	    			
    	    	}    	 
    	    	else if (z[g] > ghighestatom){
    	    		ghighestatom = z[g];
    	    	}
    	    }  
    	   	     	  
    	    x[0] += v;
    	    
    	    for (int j=0; j<wallDistance.length; j++){
    
    	    	int istart = (int)Math.ceil(((ghighestatom + 0.5) - wallDistance[j])/0.01);
    	    	int ilast = (int)Math.floor((glowestatom - 0.5)/0.01);
    	    	for (int iwallPosition = istart; iwallPosition < ilast; iwallPosition++){    	    		
    	    		double wallPosition = iwallPosition*0.01;
    	    		double g1 = 1;
    	    		for(byte g : gList){    	    		
    	    				if (z[g] < wallPosition + 0.5 + lambdaWF){
    	    					g1 *= Math.exp(epsilonWF / temperature);    	    				
    	    				}
    	    				if (z[g] < wallPosition + wallDistance[j] - 0.5 - lambdaWF){
    	    					g1 *= Math.exp(epsilonWF / temperature); 
    	    				}    	    				
    	    			}    	    	
    	    		
    	    		double g4 = 1;	    		
    	    		for(int i=0; i<cList.size()-1; i++){
    	    			List<Byte> gm1List = cList.get(i+1);
    	    			double g2 = 1;
    	    			
    	    			for(byte gm1 :gm1List){ 
    	    				if (atoms.getAtom(gm1).getPosition().getX(2) < wallPosition + 0.5 || wallPosition + wallDistance[j] - 0.5 < atoms.getAtom(gm1).getPosition().getX(2)){
    	    					g2 *= 0;
    	    				}    	   
    	    				else if (atoms.getAtom(gm1).getPosition().getX(2) < wallPosition + 0.5 + lambdaWF){
    	    					g2 *= Math.exp(epsilonWF / temperature); 
    	    				}
    	    				if (atoms.getAtom(gm1).getPosition().getX(2) >  wallPosition + wallDistance[j] - 0.5 - lambdaWF){
    	    					g2 *= Math.exp(epsilonWF / temperature); 
    	    				}
    	    					    			
    	    			}
    	    			double g3 = g2 - 1; 
    	    			g4 *= g3;
    	    		}    	    		
    	    		 x[j+1] += 0.01*g1*g4*v;
    	    	}
    	    	
    	    	 x[x.length-1] += Math.abs(x[j+1]);      
    	    }
    	  
      	}   
        return data; 
        
    }
    
   
    public BoxCluster getBox() {
        return box;
    }
    
    public void setBox(BoxCluster newBox) {
        box = newBox;
    }
    protected final List<List<List<Byte>>> listsPoint;
    protected final List<ClusterAbstract> clusters;
	private final DataDoubleArray data;
	private final IEtomicaDataInfo dataInfo;
    private final DataTag tag;
    protected final RangeComparator comparator;
    private final double [] wallDistance;
    private BoxCluster box;
    private static final long serialVersionUID = 1L;
    
    protected static class RangeComparator implements Comparator<DoubleRange>{

		public int compare(DoubleRange o1, DoubleRange o2) {
			if(o1.maximum() < o2.maximum()){
			// TODO Auto-generated method stub
			return -1;
			}
			if(o1.maximum() > o2.maximum()){
				return +1;
			}
			return 0;
		}
    	
    }
}