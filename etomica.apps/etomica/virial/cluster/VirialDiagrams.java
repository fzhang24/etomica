package etomica.virial.cluster;

import static etomica.graph.model.Metadata.COLOR_CODE_0;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import etomica.graph.iterators.IteratorWrapper;
import etomica.graph.iterators.StoredIterator;
import etomica.graph.iterators.filters.IdenticalGraphFilter;
import etomica.graph.iterators.filters.PropertyFilter;
import etomica.graph.model.BitmapFactory;
import etomica.graph.model.Edge;
import etomica.graph.model.Graph;
import etomica.graph.model.GraphFactory;
import etomica.graph.model.GraphIterator;
import etomica.graph.model.GraphList;
import etomica.graph.model.Metadata;
import etomica.graph.model.Node;
import etomica.graph.model.comparators.ComparatorBiConnected;
import etomica.graph.model.comparators.ComparatorChain;
import etomica.graph.model.comparators.ComparatorNumEdges;
import etomica.graph.model.comparators.ComparatorNumFieldNodes;
import etomica.graph.model.comparators.ComparatorNumNodes;
import etomica.graph.model.impl.CoefficientImpl;
import etomica.graph.model.impl.MetadataImpl;
import etomica.graph.operations.Decorate;
import etomica.graph.operations.Decorate.DecorateParameters;
import etomica.graph.operations.DeleteEdge;
import etomica.graph.operations.DeleteEdgeParameters;
import etomica.graph.operations.DifByConstant;
import etomica.graph.operations.DifByConstant.DifByConstantParameters;
import etomica.graph.operations.DifByNode;
import etomica.graph.operations.DifParameters;
import etomica.graph.operations.Factor;
import etomica.graph.operations.FactorOnce;
import etomica.graph.operations.FactorOnce.FactorOnceParameters;
import etomica.graph.operations.IsoFree;
import etomica.graph.operations.MaxIsomorph;
import etomica.graph.operations.MulFlexible;
import etomica.graph.operations.MulFlexible.MulFlexibleParameters;
import etomica.graph.operations.MulScalar;
import etomica.graph.operations.MulScalarParameters;
import etomica.graph.operations.PCopy;
import etomica.graph.operations.ReeHoover;
import etomica.graph.operations.ReeHoover.ReeHooverParameters;
import etomica.graph.operations.Relabel;
import etomica.graph.operations.RelabelParameters;
import etomica.graph.operations.Split;
import etomica.graph.operations.SplitGraph;
import etomica.graph.operations.SplitOne;
import etomica.graph.operations.SplitOne.SplitOneParameters;
import etomica.graph.operations.SplitParameters;
import etomica.graph.property.HasSimpleArticulationPoint;
import etomica.graph.property.IsBiconnected;
import etomica.graph.property.IsConnected;
import etomica.graph.viewer.ClusterViewer;
import etomica.math.SpecialFunctions;
import etomica.virial.ClusterBonds;
import etomica.virial.ClusterSum;
import etomica.virial.ClusterSumEF;
import etomica.virial.ClusterSumShell;
import etomica.virial.MayerFunction;

public class VirialDiagrams {

    protected final int n;
    protected final boolean flex;
    protected final boolean multibody;
    protected final boolean isInteractive;
    protected boolean doReeHoover;
    protected Set<Graph> p, cancelP, disconnectedP;
    protected Set<Graph> rho;
    protected Set<Graph> lnfXi;
    protected Map<Graph,Set<Graph>> cancelMap;
    protected boolean doShortcut;
    protected char fBond, eBond, mBond;

    protected static int[][] tripletStart = new int[0][0];
    protected static int[][] quadStart = new int[0][0];
    protected static int[][] quintStart = new int[0][0];
    protected static int[][] sixStart = new int[0][0];

    public static void main(String[] args) {
        final int n = 5;
        boolean multibody = false;
        boolean flex = true;
        VirialDiagrams virialDiagrams = new VirialDiagrams(n, multibody, flex, true);
        virialDiagrams.setDoReeHoover(false);
        virialDiagrams.setDoShortcut(false);
        virialDiagrams.makeVirialDiagrams();
    }
    
    public VirialDiagrams(int n, boolean multibody, boolean flex) {
        this(n, multibody, flex, false);
    }
    
    public VirialDiagrams(int n, boolean multibody, boolean flex, boolean interactive) {
        this.multibody = multibody;
        this.flex = flex;
        this.n = n;
        this.isInteractive = interactive;
        doReeHoover = true;
        doShortcut = false;
        ComparatorChain comp = new ComparatorChain();
        comp.addComparator(new ComparatorNumFieldNodes());
        comp.addComparator(new ComparatorBiConnected());
        comp.addComparator(new ComparatorNumEdges());
        comp.addComparator(new ComparatorNumNodes());
    }
    
    public void setDoReeHoover(boolean newDoReeHoover) {
        doReeHoover = newDoReeHoover;
    }
    
    public void setDoShortcut(boolean newDoShortcut) {
        doShortcut = newDoShortcut;
        if (multibody && doShortcut) {
            throw new RuntimeException("shortcut only works for pairwise models");
        }
    }

    public Set<Graph> getVirialGraphs() {
        if (p == null) {
            makeVirialDiagrams();
        }
        return p;
    }

    public Set<Graph> getMSMCGraphs(boolean connectedOnly) {
        if (p == null) {
            makeVirialDiagrams();
        }
        GraphList<Graph> pn = makeGraphList();
        GraphList<Graph> allP = makeGraphList();
        allP.addAll(p);
        if (!connectedOnly) {
            allP.addAll(cancelP);
        }
        for (Graph g : allP) {
            int fieldCount = 0;
            for (Node node : g.nodes()) {
              if (node.getType() == 'F') {
                fieldCount++;
              }
            }
            if (fieldCount == n) {
                pn.add(g);
            }
        }
        return pn;
    }
    
    public Map<Graph,Set<Graph>> getCancelMap() {
        if (p == null) {
            makeVirialDiagrams();
        }
        return cancelMap;
    }

    public ClusterSum makeVirialCluster(MayerFunction f, MayerFunction e) {
        if (p == null) {
            makeVirialDiagrams();
        }
        ArrayList<ClusterBonds> allBonds = new ArrayList<ClusterBonds>();
        ArrayList<Double> weights = new ArrayList<Double>();
        Set<Graph> pn = getMSMCGraphs(false);
        for (Graph g : pn) {
            populateEFBonds(g, allBonds, false);
            if (flex) {
                populateEFBonds(g, allBonds, true);
            }
            double w = ((double)g.coefficient().getNumerator())/g.coefficient().getDenominator();
            if (flex) {
                w *= 0.5;
            }
            weights.add(w);
            if (flex) {
                weights.add(w);
            }
        }
        double[] w = new double[weights.size()];
        for (int i=0; i<w.length; i++) {
            w[i] = weights.get(i);
        }
        if (n > 3 && !flex && !multibody) {
            return new ClusterSumEF(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{e});
        }
        else if (!multibody) {
            return new ClusterSum(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{f});
        }
        return null;
    }
    
    public ClusterSum makeVirialClusterTempDeriv(MayerFunction f, MayerFunction e, MayerFunction dfdT) {
        if (p == null) {
            makeVirialDiagrams();
        }
        ArrayList<ClusterBonds> allBonds = new ArrayList<ClusterBonds>();
        ArrayList<Double> weights = new ArrayList<Double>();
        Set<Graph> pn = getMSMCGraphs(false);
        char dfdTBond = 'a';
        DifByConstantParameters params = new DifByConstantParameters(fBond,dfdTBond);
        DifByConstant dif = new DifByConstant();
        Set<Graph> pn2 = dif.apply(pn, params);
        DifByConstantParameters params2 = new DifByConstantParameters(eBond,dfdTBond);
        Set<Graph> pn3 = dif.apply(pn2, params2);
        for (Graph g : pn3) {
        	populateEFdfdTBonds(g, allBonds, false);
            if (flex) {
            	populateEFdfdTBonds(g, allBonds, true);
            }
            double w = ((double)g.coefficient().getNumerator())/g.coefficient().getDenominator();
            if (flex) {
                w *= 0.5;
            }
            weights.add(w);
            if (flex) {
                weights.add(w);
            }
        }
        double[] w = new double[weights.size()];
        for (int i=0; i<w.length; i++) {
            w[i] = weights.get(i);
        }
        if (n > 3 && !flex && !multibody) {
            return new ClusterSum(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{f,e,dfdT});
        }
        else if (!multibody) {
            return new ClusterSum(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{f,dfdT});
        }
        return null;
    }
    
    
    public ClusterSum makeVirialClusterSecondTemperatureDerivative(MayerFunction f, MayerFunction e, MayerFunction dfdT, MayerFunction d2fdT2) {
        if (p == null) {
            makeVirialDiagrams();
        }
        ArrayList<ClusterBonds> allBonds = new ArrayList<ClusterBonds>();
        ArrayList<Double> weights = new ArrayList<Double>();
        Set<Graph> pn = getMSMCGraphs(false);
        char dfdTBond = 'a';
        char df2dT2Bond = 'b';
        DifByConstantParameters params = new DifByConstantParameters(fBond,dfdTBond);
        DifByConstant dif = new DifByConstant();
        Set<Graph> pn2 = dif.apply(pn, params);
        DifByConstantParameters params2 = new DifByConstantParameters(eBond,dfdTBond);
        Set<Graph> pn3 = dif.apply(pn2, params2);
        
        
        
        DifByConstantParameters params3 = new DifByConstantParameters(fBond,dfdTBond);
        Set<Graph> pn4 = dif.apply(pn3, params3);
        DifByConstantParameters params4 = new DifByConstantParameters(eBond,dfdTBond);
        Set<Graph> pn5 = dif.apply(pn4, params4);
        
        DifByConstantParameters params5 = new DifByConstantParameters(dfdTBond,df2dT2Bond);
        Set<Graph> pn6 = dif.apply(pn3, params5);
        
        pn6.addAll(pn5);       
        
        for (Graph g : pn6) {
        	populateEFdfdTdf2dT2Bonds(g, allBonds, false);
            if (flex) {
            	populateEFdfdTdf2dT2Bonds(g, allBonds, true);
            }
            double w = ((double)g.coefficient().getNumerator())/g.coefficient().getDenominator();
            if (flex) {
                w *= 0.5;
            }
            weights.add(w);
            if (flex) {
                weights.add(w);
            }
        }
        double[] w = new double[weights.size()];
        for (int i=0; i<w.length; i++) {
            w[i] = weights.get(i);
        }
        if (n > 3 && !flex && !multibody) {
            return new ClusterSum(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{f,e,dfdT,d2fdT2});
        }
        else if (!multibody) {
            return new ClusterSum(allBonds.toArray(new ClusterBonds[0]), w, new MayerFunction[]{f,dfdT,d2fdT2});
        }
        return null;
    }
    
    public void populateEFBonds(Graph g, ArrayList<ClusterBonds> allBonds, boolean swap) {
        ArrayList<int[]> fbonds = new ArrayList<int[]>();
        ArrayList<int[]> ebonds = new ArrayList<int[]>();
        for (Node node1 : g.nodes()) {
            for (Node node2 : g.nodes()) {
                if (node1.getId() > node2.getId()) continue;
                if (g.hasEdge(node1.getId(), node2.getId())) {
                    byte n1 = node1.getId();
                    byte n2 = node2.getId();
                    if (swap) {
                        if (n1 == 0) n1 = (byte)n;
                        else if (n1 == n) n1 = (byte)0;
                        else if (n2 == 0) n2 = (byte)n;
                        else if (n2 == n) n2 = (byte)0;
                    }
                    char edgeColor = g.getEdge(node1.getId(), node2.getId()).getColor();
                    if (edgeColor == fBond) {
                        fbonds.add(new int[]{n1,n2});
                    }
                    else if (edgeColor == eBond) {
                        ebonds.add(new int[]{n1,n2});
                    }
                    else {
                        throw new RuntimeException("oops, unknown bond "+edgeColor);
                    }
                }
            }
        }
        if (!flex && n > 3) {
            allBonds.add(new ClusterBonds(flex ? n+1 : n, new int[][][]{fbonds.toArray(new int[0][0]),ebonds.toArray(new int[0][0])}));
        }
        else {
            allBonds.add(new ClusterBonds(flex ? n+1 : n, new int[][][]{fbonds.toArray(new int[0][0])}));
        }

    }
    
    public void populateEFdfdTBonds(Graph g, ArrayList<ClusterBonds> allBonds, boolean swap) {
        ArrayList<int[]> fbonds = new ArrayList<int[]>();
        ArrayList<int[]> ebonds = new ArrayList<int[]>();
        ArrayList<int[]> dfdTbonds = new ArrayList<int[]>();
        for (Node node1 : g.nodes()) {
            for (Node node2 : g.nodes()) {
                if (node1.getId() > node2.getId()) continue;
                if (g.hasEdge(node1.getId(), node2.getId())) {
                    byte n1 = node1.getId();
                    byte n2 = node2.getId();
                    if (swap) {
                        if (n1 == 0) n1 = (byte)n;
                        else if (n1 == n) n1 = (byte)0;
                        else if (n2 == 0) n2 = (byte)n;
                        else if (n2 == n) n2 = (byte)0;
                    }
                    char edgeColor = g.getEdge(node1.getId(), node2.getId()).getColor();
                    if (edgeColor == fBond) {
                        fbonds.add(new int[]{n1,n2});
                    }
                    else if (edgeColor == eBond) {
                        ebonds.add(new int[]{n1,n2});
                    }
                    else if (edgeColor == 'a') {
                        dfdTbonds.add(new int[]{n1,n2});
                    }
                    else {
                        throw new RuntimeException("oops, unknown bond "+edgeColor);
                    }
                }
            }
        }
        if (!flex && n > 3) {
            allBonds.add(new ClusterBonds(flex ? n+1 : n, new int[][][]{fbonds.toArray(new int[0][0]),ebonds.toArray(new int[0][0]),dfdTbonds.toArray(new int[0][0])}));
        }
        else {
            allBonds.add(new ClusterBonds(flex ? n+1 : n, new int[][][]{fbonds.toArray(new int[0][0]),dfdTbonds.toArray(new int[0][0])}));
        }

    }
    
    public void populateEFdfdTdf2dT2Bonds(Graph g, ArrayList<ClusterBonds> allBonds, boolean swap) {
        ArrayList<int[]> fbonds = new ArrayList<int[]>();
        ArrayList<int[]> ebonds = new ArrayList<int[]>();
        ArrayList<int[]> dfdTbonds = new ArrayList<int[]>();
        ArrayList<int[]> d2fdT2bonds = new ArrayList<int[]>();
        for (Node node1 : g.nodes()) {
            for (Node node2 : g.nodes()) {
                if (node1.getId() > node2.getId()) continue;
                if (g.hasEdge(node1.getId(), node2.getId())) {
                    byte n1 = node1.getId();
                    byte n2 = node2.getId();
                    if (swap) {
                        if (n1 == 0) n1 = (byte)n;
                        else if (n1 == n) n1 = (byte)0;
                        else if (n2 == 0) n2 = (byte)n;
                        else if (n2 == n) n2 = (byte)0;
                    }
                    char edgeColor = g.getEdge(node1.getId(), node2.getId()).getColor();
                    if (edgeColor == fBond) {
                        fbonds.add(new int[]{n1,n2});
                    }
                    else if (edgeColor == eBond) {
                        ebonds.add(new int[]{n1,n2});
                    }
                    else if (edgeColor == 'a') {
                        dfdTbonds.add(new int[]{n1,n2});
                    }
                    else if (edgeColor == 'b') {
                        d2fdT2bonds.add(new int[]{n1,n2});
                    }
                    else {
                        throw new RuntimeException("oops, unknown bond "+edgeColor);
                    }
                }
            }
        }
        if (!flex && n > 3) {
            allBonds.add(new ClusterBonds(flex ? n+1 : n, new int[][][]{fbonds.toArray(new int[0][0]),ebonds.toArray(new int[0][0]),dfdTbonds.toArray(new int[0][0]),d2fdT2bonds.toArray(new int[0][0])}));
        }
        else {
            allBonds.add(new ClusterBonds(flex ? n+1 : n, new int[][][]{fbonds.toArray(new int[0][0]),dfdTbonds.toArray(new int[0][0]),d2fdT2bonds.toArray(new int[0][0])}));
        }

    }

    public ClusterSumShell[] makeSingleVirialClusters(ClusterSum coreCluster, MayerFunction e, MayerFunction f) {
        if (p == null) {
            makeVirialDiagrams();
        }
        ArrayList<ClusterSumShell> allClusters = new ArrayList<ClusterSumShell>();
        double[] w = new double[]{1};
        if (flex) {
            w = new double[]{0.5,0.5};
        }
        Set<Graph> pn = getMSMCGraphs(true);
        for (Graph g : pn) {
            double[] thisW = w;
            ArrayList<ClusterBonds> allBonds = new ArrayList<ClusterBonds>();
            populateEFBonds(g, allBonds, false);
            if (flex) {
                populateEFBonds(g, allBonds, true);
            }
            if (flex && cancelMap.get(g) != null) {
                Set<Graph> cgSet = cancelMap.get(g);
                for (Graph cg : cgSet) {
                    populateEFBonds(cg, allBonds, false);
                    populateEFBonds(cg, allBonds, true);
                }
                thisW = new double[2+2*cgSet.size()];
                thisW[0] = thisW[1] = 0.5;
                for (int i=2; i<thisW.length; i++) {
                    thisW[i] = -0.5/cgSet.size();
                }
            }
            if (n > 3 && !flex && !multibody) {
                allClusters.add(new ClusterSumShell(coreCluster, allBonds.toArray(new ClusterBonds[0]), thisW, new MayerFunction[]{e,f}));
            }
            else if (!multibody) {
                allClusters.add(new ClusterSumShell(coreCluster, allBonds.toArray(new ClusterBonds[0]), thisW, new MayerFunction[]{f}));
            }
        }
        return allClusters.toArray(new ClusterSumShell[0]);
    }
    
    public Set<Graph> getExtraDisconnectedVirialGraphs() {
        if (p == null) {
            makeVirialDiagrams();
        }
        GraphList<Graph> dpn = makeGraphList();
        for (Graph g : disconnectedP) {
            int fieldCount = 0;
            for (Node node : g.nodes()) {
              if (node.getType() == 'F') {
                fieldCount++;
              }
            }
            if (fieldCount == n) {
                dpn.add(g);
            }
        }
        return dpn;
    }

    public HashMap<Graph,Set<Graph>> getSplitDisconnectedVirialGraphs(Set<Graph> disconnectedGraphs) {
        HashMap<Graph,Set<Graph>> map = new HashMap<Graph,Set<Graph>>();
        SplitGraph splitGraph = new SplitGraph();
        MaxIsomorph maxIsomorph = new MaxIsomorph();
        Relabel relabel = new Relabel();
        for (Graph g : disconnectedGraphs) {
            Set<Graph> gSplit = makeGraphList();
            Set<Graph> gSplit1 = splitGraph.apply(g);
            for (Graph gs : gSplit1) {
                // the graph we get from splitting might not be in our preferred bonding arrangement
                Graph gsmax = maxIsomorph.apply(gs);
                HasSimpleArticulationPoint hap = new HasSimpleArticulationPoint();
                if (hap.check(gsmax)) {
                    if (!hap.getArticulationPoints().contains(0)) {
                        // the graph is articulated but not with the articulation point at 0.
                        // we calculate the diagram with the articulation point at 0, so permute
                        // now so that we can easily identify which graph this is
                        byte[] permutations = new byte[gsmax.nodeCount()];
                        for (int i=0; i<permutations.length; i++) {
                            permutations[i] = (byte)i;
                        }
                        permutations[0] = hap.getArticulationPoints().get(0);
                        permutations[hap.getArticulationPoints().get(0)] = 0;
                        RelabelParameters rp = new RelabelParameters(permutations);
                        gsmax = relabel.apply(gsmax, rp);
                    }
                }
                gSplit.add(gsmax);
            }
            map.put(g, gSplit);
        }
        return map;
    }

    public GraphList<Graph> makeGraphList() {
        ComparatorChain comp = new ComparatorChain();
        comp.addComparator(new ComparatorNumFieldNodes());
        comp.addComparator(new ComparatorBiConnected());
        comp.addComparator(new ComparatorNumEdges());
        comp.addComparator(new ComparatorNumNodes());
        GraphList<Graph> graphList = new GraphList<Graph>(comp);
        return graphList;
    }        
    
    public void makeRhoDiagrams() {
        char nodeColor = COLOR_CODE_0;
        char[] flexColors = new char[0];
        if (flex) {
           flexColors = new char[]{nodeColor};
        }

        final HashMap<Character,Integer> colorOrderMap = new HashMap<Character,Integer>();
        if (MetadataImpl.metaDataComparator == null) {
            MetadataImpl.metaDataComparator = new Comparator<Metadata>() {
    
                public int compare(Metadata m1, Metadata m2) {
                    if (m1.getType() != m2.getType()) {
                        return m1.getType() > m2.getType() ? 1 : -1;
                    }
                    Integer o1 = colorOrderMap.get(m1.getColor());
                    Integer o2 = colorOrderMap.get(m2.getColor());
                    if (o1 == o2) {
                        return m1.getColor() > m2.getColor() ? 1 : -1;
                    }
                    if (o1 == null) {
                        if (o2 == null) {
                            return m1.getColor() > m2.getColor() ? 1 : -1;
                        }
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }
                    return o1 > o2 ? 1 : (o1 < o2 ? -1 : 0);
                }
            };
        }

        GraphList<Graph> topSet = makeGraphList();

        char oneBond = 'o';
        mBond = 'm';  // multi-body
        lnfXi = new HashSet<Graph>();
        IsoFree isoFree = new IsoFree();

        MulFlexible mulFlex = new MulFlexible();
        MulFlexibleParameters mfp = new MulFlexibleParameters(flexColors, (byte)n);
        MulScalarParameters msp = null;
        MulScalar mulScalar = new MulScalar();

        
        if (doShortcut) {
            // just take lnfXi to be the set of connected diagrams
            fBond = 'A';
            eBond = 'e';

            colorOrderMap.put(oneBond, 0);
            colorOrderMap.put(mBond, 1);
            colorOrderMap.put(eBond, 2);
            colorOrderMap.put(fBond, 3);

            IsConnected isCon = new IsConnected();
            for (int i=1; i<n+1; i++) {
                GraphIterator iter = new PropertyFilter(new StoredIterator((byte)i), isCon);
                msp = new MulScalarParameters(1, (int)SpecialFunctions.factorial(i));
                while (iter.hasNext()) {
                    lnfXi.add(mulScalar.apply(iter.next(), msp));
                }
            }
        }
        else {
            
            eBond = COLOR_CODE_0;//color of edge
            fBond = 'f';

            Set<Graph> eXi = new HashSet<Graph>();//set of full star diagrams with e bonds
            colorOrderMap.put(oneBond, 0);
            colorOrderMap.put(mBond, 1);
            colorOrderMap.put(eBond, 2);
            colorOrderMap.put(fBond, 3);
            for (byte i=1; i<n+1; i++) {
                Graph g = GraphFactory.createGraph(i, BitmapFactory.createBitmap(i,true));
                g.coefficient().setDenominator((int)etomica.math.SpecialFunctions.factorial(i));
                eXi.add(g);
    
                if (multibody && i>2) {
                    g = GraphFactory.createGraph(i, BitmapFactory.createBitmap(i,true));
                    g.coefficient().setDenominator((int)etomica.math.SpecialFunctions.factorial(i));
                    for (Edge e : g.edges()) {
                        e.setColor(mBond);
                    }
                    eXi.add(g);
                }
            }
    
            if (isInteractive) {
                System.out.println("Xi");
                topSet.addAll(eXi);
                for (Graph g : topSet) {
                    System.out.println(g);
                }
            }
    
            
            Split split = new Split();
            SplitParameters bonds = new SplitParameters(eBond, fBond, oneBond);
            Set<Graph> setOfSubstituted = split.apply(eXi, bonds);
    
            DeleteEdgeParameters deleteEdgeParameters = new DeleteEdgeParameters(oneBond);
            DeleteEdge deleteEdge = new DeleteEdge();
            //set of full star diagrams with f bonds
            Set<Graph> fXi = deleteEdge.apply(setOfSubstituted, deleteEdgeParameters);

            if (isInteractive) {
                System.out.println("\nXi with f bonds");
                topSet.clear();
                topSet.addAll(fXi);
                for (Graph g : topSet) {
                    System.out.println(g);
                }
            }
            
            Set<Graph> fXipow = new HashSet<Graph>();
            fXipow.addAll(fXi);
            for (int i=1; i<n+1; i++) {

                lnfXi.addAll(fXipow);
                lnfXi = isoFree.apply(lnfXi, null);
                msp = new MulScalarParameters(new CoefficientImpl(-i,(i+1)));
                fXipow = isoFree.apply(mulScalar.apply(mulFlex.apply(fXipow, fXi, mfp), msp), null);
            }

        }

        if (isInteractive) {
            topSet.clear();
            topSet.addAll(lnfXi);
            System.out.println("\nlnfXi");
            for (Graph g : topSet) {
                System.out.println(g);
            }
//            ClusterViewer.createView("lnfXi", topSet);
        }
    

        DifByNode opzdlnXidz = new DifByNode();
        DifParameters difParams = new DifParameters(nodeColor);
        rho = isoFree.apply(opzdlnXidz.apply(lnfXi, difParams), null);

        if (isInteractive) {
            System.out.println("\nrho");
            topSet.clear();
            topSet.addAll(rho);
            for (Graph g : topSet) {
                System.out.println(g);
            }
            ClusterViewer.createView("rho", topSet);
        }
    }
    
    public void makeVirialDiagrams() {
        
        char nodeColor = COLOR_CODE_0;
        char[] flexColors = new char[0];
        if (flex) {
           flexColors = new char[]{nodeColor};
        }

        final HashMap<Character,Integer> colorOrderMap = new HashMap<Character,Integer>();
        MetadataImpl.metaDataComparator = new Comparator<Metadata>() {

            public int compare(Metadata m1, Metadata m2) {
                if (m1.getType() != m2.getType()) {
                    return m1.getType() > m2.getType() ? 1 : -1;
                }
                Integer o1 = colorOrderMap.get(m1.getColor());
                Integer o2 = colorOrderMap.get(m2.getColor());
                if (o1 == o2) {
                    return m1.getColor() > m2.getColor() ? 1 : -1;
                }
                if (o1 == null) {
                    if (o2 == null) {
                        return m1.getColor() > m2.getColor() ? 1 : -1;
                    }
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1 > o2 ? 1 : (o1 < o2 ? -1 : 0);
            }
        };

        GraphList<Graph> topSet = makeGraphList();

        char oneBond = 'o';
        mBond = 'm';  // multi-body
        lnfXi = new HashSet<Graph>();
        IsoFree isoFree = new IsoFree();

        MulFlexible mulFlex = new MulFlexible();
        MulFlexibleParameters mfp = new MulFlexibleParameters(flexColors, (byte)n);
        MulScalarParameters msp = null;
        MulScalar mulScalar = new MulScalar();

        if (doShortcut && !flex) {
            // just take lnfXi to be the set of connected diagrams
            fBond = COLOR_CODE_0;
            eBond = 'e';

            colorOrderMap.put(oneBond, 0);
            colorOrderMap.put(mBond, 1);
            colorOrderMap.put(eBond, 2);
            colorOrderMap.put(fBond, 3);

            // skip directly to p diagrams
            p = new HashSet<Graph>();
            IsBiconnected isBi = new IsBiconnected();
            for (int i=1; i<n+1; i++) {
                GraphIterator iter = new PropertyFilter(new StoredIterator((byte)i), isBi);
                msp = new MulScalarParameters(1-i, (int)SpecialFunctions.factorial(i));
                while (iter.hasNext()) {
                    p.add(mulScalar.apply(iter.next(), msp));
                }
            }
        }
        else {
            if (rho == null) {
                makeRhoDiagrams();
            }
            
            HashSet<Graph>[] allRho = new HashSet[n+1];
            for (int i=0; i<n+1; i++) {
                allRho[i] = new HashSet<Graph>();
            }
            for (Graph g : rho) {
                allRho[g.nodeCount()].add(g);
            }
            
            Set<Graph> z = new HashSet<Graph>();
            
            // r = z + b*z^2 + c*z^3 + d*z^4 + e*z^5
            // z = r - b*r^2 + (2 b^2 - c) r^3 + (-5 b^3 + 5*b*c - d) r^4
            //     + (14 b^4 - 21 b^2*c + 3 c^2 + 6 b*d - e) r^5
            //     + (-42 b^5 + 84 b^3*c - 28 (b*c^2 + b^2*d) + 7 (b*e + c*d) - f) r^6
            // or
            // z = r - b*z^2 - c*z^3 - d*z^4 - e*z^5
            // z0 = r
            // z1 = r - b*z0^2
            //    = r - b*r^2
            // z2 = r - b*z1^2 - c*z1^3
            //    = r - b*(r-b*r^2)^2 - c*(r-b*r^2)^3
            //    = r - b*r^2 + 2*b^2*r^3 - c*r^3 + O[r^4]
            // etc
    
            MulFlexibleParameters mfpnm1 = new MulFlexibleParameters(flexColors, (byte)(n-1));
            z.addAll(allRho[1]);
            for (int i=2; i<n+1; i++) {
                Set<Graph>[] zPow = new Set[n+1];
                zPow[1] = new HashSet<Graph>();
                zPow[1].addAll(z);
                for (int j=2; j<i+1; j++) {
                    zPow[j] = new HashSet<Graph>();
                    zPow[j] = isoFree.apply(mulFlex.apply(zPow[j-1], z, mfpnm1), null);
                }
                z = new HashSet<Graph>();
                z.addAll(allRho[1]);
                msp = new MulScalarParameters(new CoefficientImpl(-1,1));
                for (int j=2; j<i+1; j++) {
                    z.addAll(mulScalar.apply(mulFlex.apply(allRho[j], zPow[j], mfpnm1), msp));
                }
            }
    
            z = isoFree.apply(z, null);
            if (isInteractive) {
                System.out.println("\nz");
                topSet.clear();
                topSet.addAll(z);
                for (Graph g : topSet) {
                    System.out.println(g);
                }
            }
            
            Decorate decorate = new Decorate();
            DecorateParameters dp = new DecorateParameters(nodeColor, mfp);
            
            p = decorate.apply(lnfXi, z, dp);
            p = isoFree.apply(p, null);

            // clear these out -- we don't need them and (in extreme cases) we might need the memory
            lnfXi.clear();
            rho.clear();
            z.clear();
        }

        Set<Graph> newP = new HashSet<Graph>();

        // attempt to factor any graphs with an articulation point
        cancelMap = new HashMap<Graph,Set<Graph>>();
        cancelP = new GraphList<Graph>();
        disconnectedP = new HashSet<Graph>();
        if (!flex) {
            HasSimpleArticulationPoint hap = new HasSimpleArticulationPoint();
            Factor factor = new Factor();
            newP.clear();
            for (Graph g : p) {
                boolean ap = hap.check(g);
                boolean con = hap.isConnected();
                if ((con && ap) || (!con && hap.getArticulationPoints().size() > 0)) {
                    Graph gf = factor.apply(g, mfp);
                    newP.add(gf);
                }
                else {
                    newP.add(g.copy());
                }
            }
            p = isoFree.apply(newP, null);

            // perform Ree-Hoover substitution (brute-force)
            if (doReeHoover) {
                if (doShortcut) {
                    ReeHoover reeHoover = new ReeHoover();
                    p = reeHoover.apply(p, new ReeHooverParameters(eBond));
                }
                else {
                    char nfBond = 'F';
                    SplitOneParameters splitOneParameters = new SplitOneParameters(eBond, nfBond);
                    SplitOne splitOne = new SplitOne();
                    msp = new MulScalarParameters(-1, 1);
                    newP.clear();
                    for (Graph g : p) {
                        Set<Graph> gSet = splitOne.apply(g, splitOneParameters);
                        for (Graph g2 : gSet) {
                            boolean even = true;
                            for (Edge e : g2.edges()) {
                                if (e.getColor() == nfBond) {
                                    even = !even;
                                    e.setColor(fBond);
                                }
                            }
                            if (!even) {
                                g2 = mulScalar.apply(g2, msp);
                            }
                            newP.add(g2);
                        }
                    }
                    p = isoFree.apply(newP, null);
                    newP.clear();
                }
            }
            newP.clear();
            MaxIsomorph maxIsomorph = new MaxIsomorph();
            newP.addAll(maxIsomorph.apply(p, null));
            p = newP;
        }
        else {
            HasSimpleArticulationPoint hap = new HasSimpleArticulationPoint();
            Relabel relabel = new Relabel();
            FactorOnce factor = new FactorOnce();
            // we can take all permutations (all ways to factor the diagram) here
            // it can make the MSMC a bit more efficient, but (in practice) the difference
            // seems to be negligible
            boolean allPermutations = false;
            FactorOnceParameters fop = new FactorOnceParameters((byte)0, new char[0], allPermutations);
            newP.clear();
            msp = new MulScalarParameters(-1, 1);
            for (Graph g : p) {
                boolean ap = hap.check(g);
                boolean con = hap.isConnected();
                if (con && ap) {
                    if (!hap.getArticulationPoints().contains(0)) {
                        byte[] permutations = new byte[g.nodeCount()];
                        for (int i=0; i<permutations.length; i++) {
                            permutations[i] = (byte)i;
                        }
                        permutations[0] = hap.getArticulationPoints().get(0);
                        permutations[hap.getArticulationPoints().get(0)] = 0;
                        RelabelParameters rp = new RelabelParameters(permutations);
                        g = relabel.apply(g, rp);
                    }
                    // newP will contain connected diagrams
                    g = g.copy();
                    newP.add(g);
                    Set<Graph> gf = factor.apply(g, fop);
                    disconnectedP.addAll(gf);
                    gf = mulScalar.apply(gf, msp);
                    cancelP.addAll(gf);
                    cancelMap.put(g,gf);
                }
                else if (con) {
                    // this is a biconnected diagram;
                    newP.add(g.copy());
                }
                else {
                    // this is a disconnected diagram;
                    disconnectedP.add(g.copy());
                }
            }
            p = newP;
            // we don't need to re-isofree p, we know that's still good.
            // some of our new disconnected diagrams might condense with the old ones
            disconnectedP = isoFree.apply(disconnectedP, null);

            // we want to condense cancelP (in case multiple diagrams were factored into the
            // same one -- is that even possible?), but want to be careful not to permute bonds.
            PCopy pcopy = new PCopy();
            IteratorWrapper wrapper = new IteratorWrapper(pcopy.apply(cancelP, null).iterator());
            GraphIterator isomorphs = new IdenticalGraphFilter(wrapper);
            cancelP = new GraphList<Graph>();
            while (isomorphs.hasNext()) {
                cancelP.add(isomorphs.next());
            }
        }
        
        if (isInteractive) {
            topSet.clear();
            topSet.addAll(p);
            topSet.addAll(disconnectedP);
            System.out.println("\nP");
            for (Graph g : topSet) {
                System.out.println(g);
                Set<Graph> cancelGraphSet = cancelMap.get(g);
                if (cancelGraphSet != null) {
                    for (Graph cg : cancelGraphSet) {
                        System.out.println("    "+cg);
                    }
                }
            }
            ClusterViewer.createView("P", topSet);
        }

        GraphList<Graph> pFinal = makeGraphList();
        pFinal.addAll(p);
        p = pFinal;
        GraphList<Graph> disconnectedPFinal = makeGraphList();
        disconnectedPFinal.addAll(disconnectedP);
        disconnectedP = disconnectedPFinal;

    }

    /**
     * Returns the triplet ID for the given indices (id0, id1, id2) and given
     * number of molecules (n).  Triplets are ordered as
     * (0,1,2), (0,1,3), (0,1,4)... (0,1,n-1)
     * (0,2,3), (0,2,4), (0,2,5)... (0,2,n-1)
     * ...
     * (0,n-3,n-2),(0,n-3,n-1)
     * (0,n-2,n-1)
     * 
     * Indices must be given in order.  If idx < 0 or idx >= n, the id returned
     * will be nonsense (or there will be an exception).
     */
    public static int tripletId(int id0, int id1, int id2, int n) {
        while (tripletStart.length <= n) {
            tripletStart = (int[][])Arrays.addObject(tripletStart, new int[0]);
        }
        if (tripletStart[n].length == 0) {
            int[] nTripletStart = new int[n-2];
            int nTriplets = 0;
            for (int i=1; i<n-1; i++) {
                nTripletStart[i-1] = nTriplets;
                nTriplets += (n-i)*(n-i-1)/2;
            }
            tripletStart[n] = nTripletStart;
        }
        return tripletStart[n][id0] + (2*n-id0-id1-2)*(id1-id0-1)/2 + (id2-id1-1);
    }

    /**
     * Returns the quad ID for the given indices (id0, id1, id2, id3) and given
     * number of molecules (n).  Quads are ordered the same as triplets.
     */
    public static int quadId(int id0, int id1, int id2, int id3, int n) {
        while (quadStart.length <= n) {
            quadStart = (int[][])Arrays.addObject(quadStart, new int[0]);
        }
        if (quadStart[n].length == 0) {
            int[] nQuadStart = new int[n-3];
            int nQuads = 0;
            for (int i=1; i<n-2; i++) {
                nQuadStart[i-1] = nQuads;
                nQuads += tripletId(n-i-3, n-i-2, n-i-1, n-i)+1;
            }
            quadStart[n] = nQuadStart;
        }
        return quadStart[n][id0] + tripletId(id1-id0-1, id2-id0-1, id3-id0-1, n-id0-1);
    }

    /**
     * Returns the quint ID for the given indices (id0, id1, id2, id3, id4) and given
     * number of molecules (n).  Quints are ordered the same as triplets.
     */
    public static int quintId(int id0, int id1, int id2, int id3, int id4, int n) {
        while (quintStart.length <= n) {
            quintStart = (int[][])Arrays.addObject(quintStart, new int[0]);
        }
        if (quintStart[n].length == 0) {
            int[] nQuintStart = new int[n-4];
            int nQuints = 0;
            for (int i=1; i<n-3; i++) {
                nQuintStart[i-1] = nQuints;
                nQuints += quadId(n-i-4, n-i-3, n-i-2, n-i-1, n-i)+1;
            }
            quintStart[n] = nQuintStart;
        }
        return quintStart[n][id0] + quadId(id1-id0-1, id2-id0-1, id3-id0-1, id4-id0-1, n-id0-1);
    }

    /**
     * Returns the six-molecule ID for the given indices (id0, id1, id2, id3, id4, id5)
     * and given number of molecules (n).  Sixes are ordered the same as triplets.
     */
    public static int sixId(int id0, int id1, int id2, int id3, int id4, int id5, int n) {
        while (sixStart.length <= n) {
            sixStart = (int[][])Arrays.addObject(sixStart, new int[0]);
        }
        if (sixStart[n].length == 0) {
            int[] nSixStart = new int[n-5];
            int nSixes = 0;
            for (int i=1; i<n-4; i++) {
                nSixStart[i-1] = nSixes;
                nSixes += quintId(n-i-5, n-i-4, n-i-3, n-i-2, n-i-1, n-i)+1;
            }
            quintStart[n] = nSixStart;
        }
        return sixStart[n][id0] + quintId(id1-id0-1, id2-id0-1, id3-id0-1, id4-id0-1, id5-id0-1, n-id0-1);
    }
}
