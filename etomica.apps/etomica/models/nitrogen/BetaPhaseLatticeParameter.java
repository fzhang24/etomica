package etomica.models.nitrogen;

/**
 * The parameters that are determined through conjugate gradient method to give the lowest lattice energy
 *  for beta-N2 phase structure
 * 
 * @author taitan
 *
 */
public class BetaPhaseLatticeParameter {
	public BetaPhaseLatticeParameter(){
		
	}
	
	public double[][] getParameter(double density){
		double[][] parameters;
		
		// these parameters are for nA=1024
		if(density==0.0250){
			parameters = new double[][]{
				{ 3.943623675439537E-5, -7.774687777255597E-5, -1.253954091950439E-6, -5.821076381066166E-4,  -1.1426375628492568E-4},
			    {-3.413464215138422E-5,  1.576735518174415E-4, -6.290086612653883E-6,  4.3312591345254193E-4, -1.6695297891638808E-4},
			    { 2.308775438977747E-6, -7.003860375718278E-5,  3.6468107523589976E-6,-2.5064580018316097E-4, -3.260837904268126E-4},
			    { 2.0040370120364808E-5, 1.3522603075083E-4,    3.979941964634359E-6,  5.208391332508404E-4,  -1.197854022044697E-4}
			};
		}else if(density==0.0240){
			parameters = new double[][]{
				{ 0.004148944995165612, -9.918550032836796E-4,  0.0010620436399363738, -6.041581821607552E-4, -0.006087035922114443},
				{-0.004863785672826997, -0.0011050361164559944, 0.0010565267064424834,  4.576290727732785E-4, -0.006149073382905465},
				{-0.004817879517763631, -9.870050834225618E-4,  0.0010693763823469036, -2.8455498199588274E-4,-0.006279697205354881},
				{ 0.004126438799841219, -0.0011230969317391907, 0.0010708974143958164,  5.419906615315901E-4, -0.006103290968886958}
					
			};
		} else if (density==0.0238){
			parameters = new double[][]{
				{ 0.006280754154976942, 0.0018647135022125906, 2.2062493217815768E-4, -0.001400671718729978,  -0.00845654317120395},
				{-0.004716070064603045, 0.0012650320467982243, 2.1525422947135252E-4,  0.0012468717764013196, -0.00852300319734449},
				{-0.004683029924799134, 0.0018721416931490689, 2.277883680076147E-4,  -0.0010672455145830477, -0.00864585101097989},
				{ 0.006257018497738798, 0.0012430277522365958, 2.301714407410716E-4,   0.0013344577630835953, -0.008469468776208728}

			};
			
		} else if (density==0.0236){
			parameters = new double[][]{
				{ 0.00587858625614348,   3.498136370806539E-4,  2.530802124373233E-4,  -0.0024176577048584135, -0.011122890996224316},
				{-0.006838341102618047, -5.36908572446715E-4,   2.4714008544199687E-4,  0.0022626840421924323, -0.011181846342811854},
				{-0.0068040941664580775, 3.5690429130867413E-4, 2.6041157724316527E-4, -0.0020879869697364296, -0.011306573172402495},
				{ 0.005860486080591986, -5.58272442724777E-4,   2.62622984231827E-4,    0.0023518729234672494, -0.011125642111254046}

			};
			
		} else if (density==0.0234){
			parameters = new double[][]{
				{ 0.007507305976218076, 0.0033942214416570896, 4.8557740320274664E-4, -0.0036341142753092653, -0.013933160220737713},
				{-0.006514552729988442, 0.002205969069798159,  4.8081867142879444E-4,  0.0034753095853893865, -0.014001759464902418},
				{-0.006482879735755383, 0.0034019319982904342, 4.946809285843576E-4,  -0.003301113349027755,  -0.014122728142778693},
				{ 0.007462209141630439, 0.002184191240238766,  4.975315457049693E-4,   0.0035596263101883158, -0.013948007925388077}

			};
			
		} else if (density==0.0232){
			parameters = new double[][]{
				{ 0.007559425210441226,  0.0021451269090102154, 0.001036578655293802,  -0.004582311036273524, -0.01688755352885003},
				{-0.007410712548548332,  5.786648190746256E-4,  0.0010309920181210295,  0.004441167191164925, -0.016941528684612083},
				{-0.007387514417248088,  0.0021528198101499092, 0.0010418990753870865, -0.00426002930903527,  -0.017072759976749022},
				{ 0.0075289051162581114, 5.573233113520645E-4,  0.0010452875602986402,  0.004525048889679167, -0.016898676314120665}
				
			};		
			
		} else if (density==0.0230){
			parameters = new double[][]{
				{ 0.008643921859739718, 0.002627127103276624,  0.001956120433658189, -0.005378707642835141, -0.019636815966629115},
				{-0.007224190231223051, 7.976515173213365E-4,  0.0019489484145675763, 0.005213120619151363, -0.019690466742963146},
				{-0.007179743915596429, 0.0026352998078166424, 0.001962929723749009, -0.005025162897731024, -0.019830250709728648},
				{ 0.008621291864097444, 7.7379516101534E-4,    0.001965695798371826,  0.00529861048277718,  -0.019643975672095658}

			};	
			
		} else {
			throw new RuntimeException("<BetaPhaseLatticeParameter> Sorry I do not have the parameters you want!! " +
										"\n                         You have to do your own minimization procedure!!! HA! HA!");
		}
		
		
		return parameters;
	}
	
}