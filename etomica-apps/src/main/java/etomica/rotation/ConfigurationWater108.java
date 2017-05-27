/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.rotation;

import etomica.api.IAtom;
import etomica.api.IAtomList;
import etomica.api.IVector;
import etomica.box.Box;
import etomica.config.Configuration;
import etomica.space3d.Vector3D;

/**
 * reads configuration coordinates from a file and assigns them to the leaf atoms in a box
 */
public class ConfigurationWater108 implements Configuration, java.io.Serializable {

    public ConfigurationWater108() {
    }
    
    public void initializeCoordinates(Box box) {
        IVector[] vec = new IVector[324];
        vec[ 0 ] = new Vector3D( -8.037857867124083 , -5.771979119571202 , -3.879474004757872 );
        vec[ 1 ] = new Vector3D( -7.5341521374047415 , -4.350795528349474 , -3.251675106735932 );
        vec[ 2 ] = new Vector3D( -7.288647724573756 , -5.307638959124011 , -3.407168618703901 );
        vec[ 3 ] = new Vector3D( -4.258771758509903 , -4.802618680738147 , -6.075803462421277 );
        vec[ 4 ] = new Vector3D( -4.949734377903492 , -4.910804463414639 , -4.599835910781008 );
        vec[ 5 ] = new Vector3D( -4.886492095640265 , -4.3625049183676685 , -5.433723206904067 );
        vec[ 6 ] = new Vector3D( -6.6519713842106025 , -4.660809465485988 , -6.683871366302014 );
        vec[ 7 ] = new Vector3D( -7.540697691137002 , -4.3339875393849345 , -8.014648093805205 );
        vec[ 8 ] = new Vector3D( -7.159965292391262 , -5.062870749431158 , -7.445638334083957 );
        vec[ 9 ] = new Vector3D( -3.691874902544234 , -7.8374041166707995 , -3.659501323974456 );
        vec[ 10 ] = new Vector3D( -3.6233988389968843 , -9.186488874145336 , -4.577594491544321 );
        vec[ 11 ] = new Vector3D( -3.119171910821156 , -8.60944443185306 , -3.9351188760365705 );
        vec[ 12 ] = new Vector3D( -5.682097861680123 , -6.1658554234845395 , -3.3512902519899566 );
        vec[ 13 ] = new Vector3D( -4.057623603832283 , -6.003605477658779 , -3.3025864622319707 );
        vec[ 14 ] = new Vector3D( -4.814912974419951 , -6.523568694851689 , -3.697746002115966 );
        vec[ 15 ] = new Vector3D( -3.999995061784187 , -3.588642155436691 , 2.273798096223515 );
        vec[ 16 ] = new Vector3D( -4.503265063734838 , -4.592437404140191 , 1.0877423967370117 );
        vec[ 17 ] = new Vector3D( -4.147419007592433 , -3.67980653465325 , 1.2889350417005074 );
        vec[ 18 ] = new Vector3D( -1.9387201901704054 , -4.374109724670252 , -3.02487990443145 );
        vec[ 19 ] = new Vector3D( -3.5543736333348206 , -4.142480500121954 , -2.9646671782544725 );
        vec[ 20 ] = new Vector3D( -2.8302716533297896 , -4.825801924910324 , -3.05819873965482 );
        vec[ 21 ] = new Vector3D( -3.9906631313006993 , -6.720251898438826 , 1.3333859999596245 );
        vec[ 22 ] = new Vector3D( -5.330155594546264 , -6.882606917383175 , 0.4130520892514243 );
        vec[ 23 ] = new Vector3D( -4.766431702922417 , -6.236748679499647 , 0.9279137314141032 );
        vec[ 24 ] = new Vector3D( -7.921276040063538 , -2.0912676247587108 , 4.78534737650419 );
        vec[ 25 ] = new Vector3D( -6.650430634303695 , -1.4727574256017124 , 3.9668054981220187 );
        vec[ 26 ] = new Vector3D( -6.963341234769632 , -2.233166568926552 , 4.535896783298682 );
        vec[ 27 ] = new Vector3D( -5.725938171138716 , -3.448913309304923 , 7.652295985194342 );
        vec[ 28 ] = new Vector3D( -5.773107818720446 , -2.4875600281554426 , 6.332754924204694 );
        vec[ 29 ] = new Vector3D( -5.946731790434992 , -2.533218727254502 , 7.316507941807511 );
        vec[ 30 ] = new Vector3D( -1.4448916773245843 , -3.400468878068403 , 3.9101181674902312 );
        vec[ 31 ] = new Vector3D( -2.074079027410869 , -4.027264055776607 , 2.5394012739776866 );
        vec[ 32 ] = new Vector3D( -2.2893189970045444 , -3.6753317048741585 , 3.4503433006059394 );
        vec[ 33 ] = new Vector3D( -1.7829405742508955 , -6.480991660292695 , 2.121590046394974 );
        vec[ 34 ] = new Vector3D( -1.9193365089250352 , -8.081373617484438 , 1.825300561993262 );
        vec[ 35 ] = new Vector3D( -2.3631925755289553 , -7.191149822908629 , 1.7228633240866162 );
        vec[ 36 ] = new Vector3D( -2.9929084598452436 , -1.0679923859565248 , -6.280215580292468 );
        vec[ 37 ] = new Vector3D( -2.6628684816457673 , -0.9259180531913408 , -7.873483359175528 );
        vec[ 38 ] = new Vector3D( -2.581163790943642 , -1.5186854985716132 , -7.072264850144055 );
        vec[ 39 ] = new Vector3D( -5.262172599699369 , 1.908422003758863 , -3.719787948055966 );
        vec[ 40 ] = new Vector3D( -4.1850837150397995 , 2.47527090908784 , -2.6306710711608887 );
        vec[ 41 ] = new Vector3D( -5.037592141917208 , 1.9656697261845162 , -2.7470154585912243 );
        vec[ 42 ] = new Vector3D( -4.4297238181834455 , 1.7339201140366087 , -6.108191669570632 );
        vec[ 43 ] = new Vector3D( -4.313345230913378 , 0.2316217598851933 , -5.477977897897472 );
        vec[ 44 ] = new Vector3D( -3.9574861703031363 , 0.8549305676538295 , -6.174289939398721 );
        vec[ 45 ] = new Vector3D( -4.6805749890624035 , -2.3386008056289476 , -5.089267744388185 );
        vec[ 46 ] = new Vector3D( -4.879213413196527 , -1.1279526203223644 , -4.0110806241076045 );
        vec[ 47 ] = new Vector3D( -4.288245879361997 , -1.4912787761788897 , -4.731324664988232 );
        vec[ 48 ] = new Vector3D( -6.277629774266168 , -1.2248048473019217 , -2.0379031390974323 );
        vec[ 49 ] = new Vector3D( -5.939330406255993 , 0.18374119581434037 , -2.792333586847905 );
        vec[ 50 ] = new Vector3D( -6.043429009782218 , -0.8033569330814169 , -2.9139919802056373 );
        vec[ 51 ] = new Vector3D( -6.104868447987238 , 1.4243157405046456 , 0.4438613596551428 );
        vec[ 52 ] = new Vector3D( -4.73506928556859 , 1.0917365132094454 , 1.2688795598643479 );
        vec[ 53 ] = new Vector3D( -5.593202290504828 , 0.7116296484962931 , 0.9237324647917718 );
        vec[ 54 ] = new Vector3D( -2.1616564636424886 , 2.080143343270114 , 1.5578725895915975 );
        vec[ 55 ] = new Vector3D( -2.814480878774317 , 1.0765590134899006 , 0.44690342057972493 );
        vec[ 56 ] = new Vector3D( -3.004648466940213 , 1.8217402952640283 , 1.0860756553526656 );
        vec[ 57 ] = new Vector3D( -5.575100301772885 , -1.1160276441479628 , 0.21997561412098596 );
        vec[ 58 ] = new Vector3D( -5.471932508173015 , -2.7456248171082436 , 0.25716672476770513 );
        vec[ 59 ] = new Vector3D( -5.6677253885283 , -1.952700112195503 , -0.31983896007536716 );
        vec[ 60 ] = new Vector3D( -4.752426518598107 , -0.9296735254717433 , 3.709588302376534 );
        vec[ 61 ] = new Vector3D( -5.5127629882754805 , -0.17540171023998782 , 2.4764718439294477 );
        vec[ 62 ] = new Vector3D( -5.506622311456944 , -0.3198920311337065 , 3.4659590036522214 );
        vec[ 63 ] = new Vector3D( -5.4474854178321275 , -0.7209981946149719 , 7.158356191181775 );
        vec[ 64 ] = new Vector3D( -4.328560320912255 , -0.531023149939445 , 5.983822314126313 );
        vec[ 65 ] = new Vector3D( -4.83389773123231 , -0.06913826945485221 , 6.7127231708350665 );
        vec[ 66 ] = new Vector3D( -1.1457928828398063 , -0.021553181571489017 , 6.550870165404329 );
        vec[ 67 ] = new Vector3D( -0.6226309003662284 , -0.42957999017657467 , 5.058412667844881 );
        vec[ 68 ] = new Vector3D( -1.3790743502417229 , -0.03300337989621882 , 5.578528327404373 );
        vec[ 69 ] = new Vector3D( -2.8106754313475846 , -1.009718449252729 , 5.100988957787565 );
        vec[ 70 ] = new Vector3D( -3.1097664665410156 , -2.518218886922966 , 4.550911962555838 );
        vec[ 71 ] = new Vector3D( -3.5072337773462428 , -1.6157335853439116 , 4.716863814835435 );
        vec[ 72 ] = new Vector3D( -6.310914789651064 , 3.677475265156601 , -5.357244371261531 );
        vec[ 73 ] = new Vector3D( -4.883106231970431 , 4.046541848327986 , -6.059217517662229 );
        vec[ 74 ] = new Vector3D( -5.451443813192208 , 3.3035276975932546 , -5.705773935772658 );
        vec[ 75 ] = new Vector3D( -3.480114256558675 , 6.158468442381774 , -6.048289331981365 );
        vec[ 76 ] = new Vector3D( -4.1580731280267615 , 6.093832073393946 , -7.532812025263395 );
        vec[ 77 ] = new Vector3D( -4.305431324320956 , 5.918440180533363 , -6.559403968021363 );
        vec[ 78 ] = new Vector3D( -1.8510438482615807 , 9.300745995677186 , -8.05634631839289 );
        vec[ 79 ] = new Vector3D( -2.0051854328491627 , 8.606639368122371 , -9.52674425091077 );
        vec[ 80 ] = new Vector3D( -2.2500712588432568 , 8.53443890692664 , -8.559884371905383 );
        vec[ 81 ] = new Vector3D( -2.14667331577539 , 3.4798891756522794 , -3.538572819041045 );
        vec[ 82 ] = new Vector3D( -2.4364256012945673 , 4.214606010047791 , -2.1089405219940742 );
        vec[ 83 ] = new Vector3D( -2.673682331395129 , 3.4359818227048056 , -2.6898481040641236 );
        vec[ 84 ] = new Vector3D( -3.807675815621007 , 5.111429231431897 , -0.2407478056875239 );
        vec[ 85 ] = new Vector3D( -3.6760910152512967 , 3.6493860182332356 , 0.4753030315572426 );
        vec[ 86 ] = new Vector3D( -4.252105860241442 , 4.460627370444556 , 0.37483230540613965 );
        vec[ 87 ] = new Vector3D( -6.086515428131447 , 3.796977991718979 , -0.21833775183245266 );
        vec[ 88 ] = new Vector3D( -6.012032555453254 , 2.614107016841129 , -1.3421181026736066 );
        vec[ 89 ] = new Vector3D( -6.48371627701925 , 2.929829159312567 , -0.51881135815921 );
        vec[ 90 ] = new Vector3D( 0.3111658607231813 , 7.860400358660528 , -2.12218881002502 );
        vec[ 91 ] = new Vector3D( -0.16352658807139528 , 8.24519103393687 , -3.636856078353392 );
        vec[ 92 ] = new Vector3D( -0.4359564410889727 , 7.799582291702963 , -2.7840871623922516 );
        vec[ 93 ] = new Vector3D( -1.365534038868783 , 3.2113650422479685 , 3.0420989012965136 );
        vec[ 94 ] = new Vector3D( -0.681812717758371 , 3.716296158482018 , 1.647400567826078 ); 
        vec[ 95 ] = new Vector3D( -0.7005472379588481 , 2.9858435131810137 , 2.3301069107917955 );
        vec[ 96 ] = new Vector3D( -5.283125553016815 , 2.8564160507413137 , 2.9064286200059604 );
        vec[ 97 ] = new Vector3D( -5.378915431715957 , 1.5527349848038399 , 3.8856393715413358 );
        vec[ 98 ] = new Vector3D( -5.088607502412753 , 2.5076305102895677 , 3.8232226943547096 );
        vec[ 99 ] = new Vector3D( -4.713124727068395 , 3.694221935373065 , 6.80909675826596 ); 
        vec[ 100 ] = new Vector3D( -4.608508725341491 , 3.4113860166559955 , 5.203894885063186 );
        vec[ 101 ] = new Vector3D( -4.310265282068384 , 4.007695579474526 , 5.949190634303563 );
        vec[ 102 ] = new Vector3D( -3.976617491492612 , 4.684664495250744 , 3.7137542327510964 );
        vec[ 103 ] = new Vector3D( -4.023113369564514 , 5.018194148406596 , 2.1155646875920726 );
        vec[ 104 ] = new Vector3D( -3.437005166827694 , 4.978635569985065 , 2.924831203369098 );
        vec[ 105 ] = new Vector3D( -5.303843559101106 , 1.9060539956720635 , 7.486037801439591 );
        vec[ 106 ] = new Vector3D( -6.530520083191104 , 2.6339491130477932 , 8.281680385653507 );
        vec[ 107 ] = new Vector3D( -5.581828117410502 , 2.7297063350318402 , 7.980326592743515 ); 
        vec[ 108 ] = new Vector3D( -0.9251812203755387 , -1.9332685205232514 , -6.896375285312804 );
        vec[ 109 ] = new Vector3D( 0.4852923314736774 , -1.1141689793473453 , -6.811204752575127 );
        vec[ 110 ] = new Vector3D( 0.07072741961006165 , -2.0160551857326245 , -6.93260164126458 );
        vec[ 111 ] = new Vector3D( -1.0998331046192902 , -8.119552670968455 , -3.7372915546448033 );
        vec[ 112 ] = new Vector3D( -0.14332911769805734 , -6.806497187023111 , -3.906404349998636 );
        vec[ 113 ] = new Vector3D( -0.30531675561912164 , -7.635326188898229 , -3.370864910917393 );
        vec[ 114 ] = new Vector3D( -1.6009285587475668 , -4.393758213911048 , -7.765676134723889 );
        vec[ 115 ] = new Vector3D( -0.05039926216252615 , -3.9330430238450917 , -7.53937073809903 );
        vec[ 116 ] = new Vector3D( -0.6605666009724058 , -4.716402216879578 , -7.657878937553875 );
        vec[ 117 ] = new Vector3D( -0.2087935533320294 , -5.088027896024703 , -5.588715014931991 );
        vec[ 118 ] = new Vector3D( 0.4018995139389786 , -4.202526782496787 , -4.3596684438064335 );
        vec[ 119 ] = new Vector3D( -0.1040651284458287 , -5.0321340597747835 , -4.595786096987649 );
        vec[ 120 ] = new Vector3D( -1.5029504744182904 , -6.889212283781924 , -0.39542516683617235 );
        vec[ 121 ] = new Vector3D( -0.7120175612428974 , -6.934074766766012 , -1.82372002538042 );
        vec[ 122 ] = new Vector3D( -0.758568480317475 , -6.488582428761003 , -0.9296453144573662 );
        vec[ 123 ] = new Vector3D( -0.24309295271369755 , -5.18453571327523 , 0.5349294780039041 );
        vec[ 124 ] = new Vector3D( 0.7251144478092141 , -5.66751759381797 , 1.7584149299434069 );
        vec[ 125 ] = new Vector3D( 0.02700392878883641 , -5.007323155757811 , 1.4813138523786828 );
        vec[ 126 ] = new Vector3D( 1.7374991272949702 , -5.524007418095587 , -2.263927388628231 );
        vec[ 127 ] = new Vector3D( 2.071156628546533 , -5.889793585283374 , -0.707493364899574 );
        vec[ 128 ] = new Vector3D( 2.41036711186223 , -5.432898210864517 , -1.529796754679396 );
        vec[ 129 ] = new Vector3D( 2.991960886777133 , -7.096719183763634 , 0.7987993504207193 );
        vec[ 130 ] = new Vector3D( 3.4731981428748955 , -6.025712168002001 , 1.9341267895326284 );
        vec[ 131 ] = new Vector3D( 2.742846809101403 , -6.2645163539628825 , 1.29415843873484 );
        vec[ 132 ] = new Vector3D( -1.3917136708852342 , -10.795450136103822 , 1.1567674682480704 );
        vec[ 133 ] = new Vector3D( -0.02378150543997163 , -9.920207997188042 , 1.3309125441873848 );
        vec[ 134 ] = new Vector3D( -1.0177954141937084 , -9.871150879416245 , 1.233292113948409 );
        vec[ 135 ] = new Vector3D( -0.19045962229504465 , -2.4505365030929105 , 5.802796759478183 );
        vec[ 136 ] = new Vector3D( -0.6211489177179637 , -1.9436059144588562 , 7.294487688452004 );
        vec[ 137 ] = new Vector3D( -0.1148311791223415 , -2.637350914820849 , 6.782276577623518 );
        vec[ 138 ] = new Vector3D( 3.2949320389609635 , -3.724664961816166 , 1.4820378141594919 );
        vec[ 139 ] = new Vector3D( 1.7588539766487734 , -4.177668577884295 , 1.80276453393813 );
        vec[ 140 ] = new Vector3D( 2.5087958073905408 , -3.5782182015127035 , 2.082490286724035 );
        vec[ 141 ] = new Vector3D( 2.7128856692721888 , -3.465488844047885 , 6.235554049124141 );
        vec[ 142 ] = new Vector3D( 1.4654397445664147 , -4.088682955310906 , 7.085919548829761 );
        vec[ 143 ] = new Vector3D( 2.449013770400059 , -4.149250100433574 , 6.915879092946192 );
        vec[ 144 ] = new Vector3D( -0.9981830589354844 , -1.8865385372472767 , -3.4456400558188003 );
        vec[ 145 ] = new Vector3D( -2.4970558856523164 , -2.301142132548696 , -3.9447370669905446 );
        vec[ 146 ] = new Vector3D( -1.832816485007281 , -2.3802393840218308 , -3.2014136895563894 );
        vec[ 147 ] = new Vector3D( -2.3523059407683764 , 1.3715365807511468 , -2.295456865531144 );
        vec[ 148 ] = new Vector3D( -1.8696010785183492 , -0.18816287295002754 , -2.251328093148695 );
        vec[ 149 ] = new Vector3D( -2.0091220680499164 , 0.6392189397929507 , -1.7072938760058058 );
        vec[ 150 ] = new Vector3D( 0.09269600605593432 , 1.056474373845923 , -6.465634156323015 );
        vec[ 151 ] = new Vector3D( 1.5126990226322707 , 1.8363291363644423 , -6.258193319555438 );
        vec[ 152 ] = new Vector3D( 1.0373831957283877 , 0.9630373225689955 , -6.151252809679431 );
        vec[ 153 ] = new Vector3D( 0.5775925594452006 , 0.28474231661673627 , -4.489265744805072 );
        vec[ 154 ] = new Vector3D( -0.05815758879952894 , 0.777061743443061 , -3.0676268843894796 );
        vec[ 155 ] = new Vector3D( 0.103773741405682 , -0.012040468556822679 , -3.6601603799109235 );
        vec[ 156 ] = new Vector3D( -1.373414655515113 , -3.358181743134301 , -1.2460654749603353 ); 
        vec[ 157 ] = new Vector3D( -2.5215292641325155 , -3.1902433783514543 , -0.09661200935987459 );
        vec[ 158 ] = new Vector3D( -1.5383520148595713 , -3.1691548340110485 , -0.27804455828560404 );
        vec[ 159 ] = new Vector3D( 0.815822620720432 , 0.002403383838469082 , 0.9908550727923175 );
        vec[ 160 ] = new Vector3D( 0.30682460323829425 , 1.4377577435982625 , 1.5810182693620076 );
        vec[ 161 ] = new Vector3D( 0.8050636539324231 , 0.99061953423171 , 0.8381688431066675 );
        vec[ 162 ] = new Vector3D( -0.9327438821677658 , 2.8622331141372155 , -0.7745633101327412 );
        vec[ 163 ] = new Vector3D( 0.3530182703513229 , 2.2366695278589206 , 0.014803274629785822 );
        vec[ 164 ] = new Vector3D( -0.011878861647508752 , 3.052318406187146 , -0.4341588511012511 );
        vec[ 165 ] = new Vector3D( 1.1137659487954035 , -2.105170019095558 , 1.0690573276310644 );
        vec[ 166 ] = new Vector3D( -0.4742082373354627 , -2.2469376466570328 , 0.7142987584249835 );
        vec[ 167 ] = new Vector3D( 0.22046250531112585 , -1.6598612754523587 , 1.12995955003755 );
        vec[ 168 ] = new Vector3D( 0.027494426557049353 , -1.9091704693590692 , 2.965561327861826 );
        vec[ 169 ] = new Vector3D( 0.8965036679006853 , -1.5975564190430491 , 4.312904395002688 );
        vec[ 170 ] = new Vector3D( 0.07716763682617331 , -2.0454238223706893 , 3.9549892413424663 );
        vec[ 171 ] = new Vector3D( -0.7007653410505728 , 0.512156358086709 , 9.006755130856883 );
        vec[ 172 ] = new Vector3D( 0.6230201473967651 , 0.18662641703396926 , 8.107179699599458 );
        vec[ 173 ] = new Vector3D( -0.37428890706606194 , 0.25908980838706674 , 8.096057343731054 );
        vec[ 174 ] = new Vector3D( 2.7829062294965516 , 1.7438593237580682 , 3.0825400726119856 );
        vec[ 175 ] = new Vector3D( 1.7681412539534385 , 2.683743850018072 , 3.951149789444313 );
        vec[ 176 ] = new Vector3D( 2.3627662631334556 , 1.880247447371793 , 3.9796915026401303 );
        vec[ 177 ] = new Vector3D( 2.2383196124985503 , -0.2711112586551723 , 6.054095149874458 );
        vec[ 178 ] = new Vector3D( 2.123513879078071 , 0.31656763031939433 , 4.534533737658766 );
        vec[ 179 ] = new Vector3D( 2.082202304722836 , -0.5101004390933046 , 5.095705063403208 );
        vec[ 180 ] = new Vector3D( -2.298753257110301 , 1.2420596644721233 , -6.6713830452851495 );
        vec[ 181 ] = new Vector3D( -1.4464751998391192 , 2.4991170051776517 , -7.272251311829449 );
        vec[ 182 ] = new Vector3D( -1.41298737509664 , 1.5277110850176046 , -7.037200032590162 );
        vec[ 183 ] = new Vector3D( -1.6498655160798426 , 6.7379334753872575 , -4.570171024256016 );
        vec[ 184 ] = new Vector3D( -1.6195255882078483 , 5.272018826125725 , -5.289745515413607 );
        vec[ 185 ] = new Vector3D( -2.023061748284802 , 6.18659625943465 , -5.316329081622985 );
        vec[ 186 ] = new Vector3D( -0.3896288565091953 , 3.8013405917336005 , -5.714954577631657 );
        vec[ 187 ] = new Vector3D( -0.23205542635647736 , 3.5276684175945476 , -4.112491512946717 );
        vec[ 188 ] = new Vector3D( -0.8512646138599095 , 3.848587441042761 , -4.829144122305243 );
        vec[ 189 ] = new Vector3D( 1.999652211772683 , 3.110930608414495 , -2.973856106052533 );
        vec[ 190 ] = new Vector3D( 0.6729484400132729 , 3.3044841034345183 , -2.0411139846532547 );
        vec[ 191 ] = new Vector3D( 0.9998660506102907 , 3.131276640140967 , -2.9701586824697532 );
        vec[ 192 ] = new Vector3D( -1.666235998912279 , 6.514126882517872 , -1.839751403005754 );
        vec[ 193 ] = new Vector3D( -1.5365972932089014 , 5.852571495033337 , -0.35208477578671227 );
        vec[ 194 ] = new Vector3D( -1.9915664800868276 , 5.783156795133417 , -1.2398824095014258 );
        vec[ 195 ] = new Vector3D( -0.3624149671220803 , 6.179687552363756 , 1.7386848511117017 );
        vec[ 196 ] = new Vector3D( -1.7194623349442686 , 5.474470932744149 , 2.3120134474390666 );
        vec[ 197 ] = new Vector3D( -1.138013574824392 , 5.592502395166024 , 1.5070377252998135 );
        vec[ 198 ] = new Vector3D( 2.0877032467850114 , 6.7816047632646805 , -0.752814368054864 );
        vec[ 199 ] = new Vector3D( 2.7627999074048244 , 8.259548072297134 , -0.9187715690848894 );
        vec[ 200 ] = new Vector3D( 2.2003380008583675 , 7.5638269453351095 , -1.3655474829195733 );
        vec[ 201 ] = new Vector3D( 1.488387577239198 , 5.976018049543372 , 3.071922300554523 );
        vec[ 202 ] = new Vector3D( 1.9898324773854077 , 7.112043287874922 , 2.010973014085729 );
        vec[ 203 ] = new Vector3D( 1.2161916047358075 , 6.78787167303783 , 2.5553924886638124 );
        vec[ 204 ] = new Vector3D( -2.4151880903648633 , 3.374167208184974 , 5.311094536931625 );
        vec[ 205 ] = new Vector3D( -1.8217381100928602 , 1.9505278744657044 , 4.773803287356444 );
        vec[ 206 ] = new Vector3D( -1.5808935187022075 , 2.85401988143492 , 5.128339711635541 );
        vec[ 207 ] = new Vector3D( -2.545469759861295 , 4.437194597640935 , 7.548331934418696 );
        vec[ 208 ] = new Vector3D( -1.8709044385272042 , 5.836394857603012 , 7.043540910089784 );
        vec[ 209 ] = new Vector3D( -1.7061308171785794 , 4.980612351889682 , 7.533934585235246 );
        vec[ 210 ] = new Vector3D( 0.1706091731947388 , 4.1800917088950325 , 4.160281635671306 );
        vec[ 211 ] = new Vector3D( 0.94408338475161 , 4.154726940957061 , 5.598582018439616 );
        vec[ 212 ] = new Vector3D( 1.0517202023097922 , 4.306358099763176 , 4.616022958034056 );
        vec[ 213 ] = new Vector3D( -0.18699246863056948 , 2.74630991882238 , 6.386947929766547 );
        vec[ 214 ] = new Vector3D( -0.12793158791645529 , 3.752210030356747 , 7.672362452965956 );
        vec[ 215 ] = new Vector3D( 0.4044134347598527 , 3.134112138821827 , 7.0939475730468375 );
        vec[ 216 ] = new Vector3D( 2.194802886080294 , -6.576338900462158 , -7.906820886390247 );
        vec[ 217 ] = new Vector3D( 0.8388742975641192 , -5.857740613328002 , -7.347635869566717 );
        vec[ 218 ] = new Vector3D( 1.8383417845716137 , -5.825291090429165 , -7.351066740966333 );
        vec[ 219 ] = new Vector3D( 1.109431008296896 , -1.750274345673934 , -3.723186022902789 );
        vec[ 220 ] = new Vector3D( 2.2895618664047035 , -2.843107562808826 , -4.007123402858077 );
        vec[ 221 ] = new Vector3D( 1.3478906770312329 , -2.720973940153407 , -3.69353330524494 );
        vec[ 222 ] = new Vector3D( 3.6491614970116744 , -4.322480931621343 , -5.130606251294134 );
        vec[ 223 ] = new Vector3D( 2.8076640516480897 , -4.775421349568456 , -6.455121538372394 );
        vec[ 224 ] = new Vector3D( 3.3678468732401097 , -4.052121169364226 , -6.051349256374564 );
        vec[ 225 ] = new Vector3D( 3.6810753947003287 , -4.947668718670951 , -2.75003199707612 );
        vec[ 226 ] = new Vector3D( 5.255250427490382 , -4.517191909220052 , -2.815426195082874 );
        vec[ 227 ] = new Vector3D( 4.452026825790816 , -4.760956872207426 , -3.3589429735658753 );
        vec[ 228 ] = new Vector3D( 2.9003403071224527 , -8.62644424702033 , -1.7433082929163577 );
        vec[ 229 ] = new Vector3D( 1.5137950805561624 , -7.882580989292638 , -1.3053887718208477 );
        vec[ 230 ] = new Vector3D( 2.3234002188797005 , -8.364149465838093 , -0.9697828684195257 );
        vec[ 231 ] = new Vector3D( 3.2487884975601644 , -4.293567698738215 , -0.8507646730974939 );
        vec[ 232 ] = new Vector3D( 3.233836248885512 , -2.6669249878623362 , -0.7043965075645197 );
        vec[ 233 ] = new Vector3D( 3.709236934750099 , -3.5062553297540004 , -0.44073239347330506 );
        vec[ 234 ] = new Vector3D( 6.7355020001425725 , -3.991466238840937 , 0.23916366016112875 );
        vec[ 235 ] = new Vector3D( 5.259277672922633 , -3.2945871884216795 , 0.1868660189300662 );
        vec[ 236 ] = new Vector3D( 6.202496864629161 , -3.1869212031367264 , 0.5011041324826813 );
        vec[ 237 ] = new Vector3D( 8.388792384206258 , -3.0430176706647916 , 2.6614343829634906 );
        vec[ 238 ] = new Vector3D( 6.974598292860617 , -2.6747775637904363 , 1.9320044588722367 );
        vec[ 239 ] = new Vector3D( 7.648876666451238 , -2.3726195988936305 , 2.6058362176647396 );
        vec[ 240 ] = new Vector3D( 5.317215403136159 , -5.607304581417405 , 3.5167143519212467 );
        vec[ 241 ] = new Vector3D( 5.255801249170045 , -7.2135722819411106 , 3.227325177254237 );
        vec[ 242 ] = new Vector3D( 4.858157627723183 , -6.326053057442382 , 2.9945394894042336 );
        vec[ 243 ] = new Vector3D( 3.1115954413957905 , -2.730629519618991 , 3.5520426227124524 );
        vec[ 244 ] = new Vector3D( 3.4828162554205107 , -1.4839769363712452 , 4.53980077245564 );
        vec[ 245 ] = new Vector3D( 3.811692358060551 , -2.345707088232288 , 4.153456813675586 );
        vec[ 246 ] = new Vector3D( 5.288819939664457 , -3.7271686452964783 , 4.302249663152598 );
        vec[ 247 ] = new Vector3D( 6.87260209948751 , -3.7028393276100298 , 3.9039333654761523 );
        vec[ 248 ] = new Vector3D( 6.106226016341178 , -4.287740710387099 , 4.169561059075214 );
        vec[ 249 ] = new Vector3D( 5.183796173815844 , -5.427945414817447 , 8.285940126387224 );
        vec[ 250 ] = new Vector3D( 5.367014588947083 , -5.292469941976894 , 9.903249969021891 );
        vec[ 251 ] = new Vector3D( 5.346565036600172 , -4.790191717972052 , 9.038785741017428 );
        vec[ 252 ] = new Vector3D( 4.344900942595356 , -2.998049314479512 , -6.841226953005335 );
        vec[ 253 ] = new Vector3D( 3.9836262850375688 , -1.765274547543198 , -7.849869959365445 );
        vec[ 254 ] = new Vector3D( 4.625456883398123 , -2.0912009121462316 , -7.15573345415571 );
        vec[ 255 ] = new Vector3D( 4.634978836885732 , -0.6206158944330704 , -6.376579243971306 );
        vec[ 256 ] = new Vector3D( 3.3423374903710874 , 0.11296039235830134 , -5.699410211007058 );
        vec[ 257 ] = new Vector3D( 4.328280731201069 , 0.17520528202780494 , -5.854463426296846 );
        vec[ 258 ] = new Vector3D( 5.284814316981037 , 1.0579365195444077 , -4.562060280365268 );
        vec[ 259 ] = new Vector3D( 6.812519120213336 , 1.5264368691404704 , -4.224067417900877 );
        vec[ 260 ] = new Vector3D( 5.9132694805759805 , 1.329855640019766 , -3.8332913874569887 );
        vec[ 261 ] = new Vector3D( 5.135036566961689 , -1.2218369483028562 , -3.2591996255667706 );
        vec[ 262 ] = new Vector3D( 4.2196708862918495 , -1.0287220486221105 , -1.9203844685564087 );
        vec[ 263 ] = new Vector3D( 5.052171164912362 , -1.4435527873551777 , -2.2876156550029654 );
        vec[ 264 ] = new Vector3D( 2.7844330079326003 , 0.06436906292810787 , -0.9997843498534826 );
        vec[ 265 ] = new Vector3D( 1.7194689554294074 , -1.0488337947869486 , -1.5422276030316808 );
        vec[ 266 ] = new Vector3D( 2.5944394644333 , -0.9143673811984145 , -1.077098167293516 );
        vec[ 267 ] = new Vector3D( 4.209895652184576 , 1.7132638551207802 , -1.279867610650406 );
        vec[ 268 ] = new Vector3D( 3.2796840552685422 , 1.9363486292244736 , 0.043972744903679244 );
        vec[ 269 ] = new Vector3D( 3.3117843579038357 , 1.5424594881826572 , -0.8746245091418916 );
        vec[ 270 ] = new Vector3D( 6.922132578136776 , 2.1163661683240598 , -2.380105706195911 );
        vec[ 271 ] = new Vector3D( 7.354110101992964 , 1.2893467986964013 , -1.0395654794174345 );
        vec[ 272 ] = new Vector3D( 7.465938087119263 , 2.1470192189854425 , -1.5414544324918462 );
        vec[ 273 ] = new Vector3D( 7.114825045253466 , -1.4808756021566523 , 0.0015442361280523804 );
        vec[ 274 ] = new Vector3D( 6.326964944450338 , -0.8439692576159009 , -1.279564849123381 );
        vec[ 275 ] = new Vector3D( 6.866319179986936 , -0.6318126063492964 , -0.4646497836520945 );
        vec[ 276 ] = new Vector3D( 6.660814166616597 , -0.6364709986181217 , 3.850907840198677 );
        vec[ 277 ] = new Vector3D( 5.213506783788422 , -1.1186868883100332 , 4.434331266028704 );
        vec[ 278 ] = new Vector3D( 5.800441280132862 , -0.32868017799655636 , 4.257138448283237 );
        vec[ 279 ] = new Vector3D( 2.905897854941823 , -0.8691001213236647 , 8.143321696327657 );
        vec[ 280 ] = new Vector3D( 3.3194710534122827 , 0.5624911819350786 , 7.474653416529377 );
        vec[ 281 ] = new Vector3D( 2.554860740010239 , -0.010555730402920623 , 7.769591214885432 );
        vec[ 282 ] = new Vector3D( 5.89900709277725 , 0.41534046469117586 , 0.9664489516511529 );
        vec[ 283 ] = new Vector3D( 5.402181596710199 , 0.5037647111614802 , 2.5198192544319893 );
        vec[ 284 ] = new Vector3D( 5.335230579501371 , 0.9258246954229146 , 1.6157269300646495 );
        vec[ 285 ] = new Vector3D( 3.1787368579288704 , -2.9527352492752725 , 8.248428915822302 );
        vec[ 286 ] = new Vector3D( 4.701180486777164 , -2.817880312967837 , 8.82427026689072 );
        vec[ 287 ] = new Vector3D( 3.862628033614912 , -2.3178614279060494 , 8.607912004101577 );
        vec[ 288 ] = new Vector3D( 1.1401879352669275 , 4.346527596497258 , -7.962511041653578 );
        vec[ 289 ] = new Vector3D( 1.3878099946799927 , 4.796567959913863 , -6.41210441581137 );
        vec[ 290 ] = new Vector3D( 0.9036858829086889 , 4.157235732046933 , -7.00949720687296 );
        vec[ 291 ] = new Vector3D( 3.4689003667251788 , 7.107214365455575 , -2.8025279433904817 );
        vec[ 292 ] = new Vector3D( 4.47090431297969 , 5.825586188739661 , -2.9475487239902503 );
        vec[ 293 ] = new Vector3D( 4.059774576763291 , 6.599393851576403 , -3.4294138928225384 );
        vec[ 294 ] = new Vector3D( 2.9004566045419073 , 6.165138383202259 , -4.7690888612047475 );
        vec[ 295 ] = new Vector3D( 1.7700460668172058 , 4.988420696922669 , -4.697526151185501 );
        vec[ 296 ] = new Vector3D( 2.3014728915610005 , 5.574189836654745 , -5.309457556557271 );
        vec[ 297 ] = new Vector3D( 4.632762032358227 , 2.818161691318718 , -3.8899652465074945 );
        vec[ 298 ] = new Vector3D( 4.134457974712747 , 3.7592261284013135 , -2.6515370047994775 );
        vec[ 299 ] = new Vector3D( 3.834905871611955 , 3.2089749496617377 , -3.43095485026082 );
        vec[ 300 ] = new Vector3D( 1.0879949311654458 , 4.685926244661145 , -0.14113535579189965 );
        vec[ 301 ] = new Vector3D( 2.542657607095457 , 4.6464270848468265 , 0.6004912578740655 );
        vec[ 302 ] = new Vector3D( 2.0027028954334964 , 5.089216383376167 , -0.11532055151576892 );
        vec[ 303 ] = new Vector3D( 4.148549749611037 , 7.3159588745042905 , 2.2552220691693807 );
        vec[ 304 ] = new Vector3D( 4.040714473880868 , 8.919002123477084 , 2.548884483683896 );
        vec[ 305 ] = new Vector3D( 3.796730303486087 , 8.187093035796225 , 1.9126606143053986 ); 
        vec[ 306 ] = new Vector3D( 5.326526784578227 , 4.841386370356235 , -0.46090455188123014 );
        vec[ 307 ] = new Vector3D( 3.7621814065402486 , 4.935069644657894 , -0.9209705262749878 );
        vec[ 308 ] = new Vector3D( 4.696021715625382 , 4.759829886378265 , -1.2327930787722041 );
        vec[ 309 ] = new Vector3D( 7.223738737172527 , 3.6078020949204874 , -0.2671928685040329 );
        vec[ 310 ] = new Vector3D( 6.204899921351288 , 2.8974516692235173 , 0.7934595055354782 );
        vec[ 311 ] = new Vector3D( 6.674403889815343 , 3.748007966081885 , 0.556562883628196 ); 
        vec[ 312 ] = new Vector3D( 3.5969566080493554 , 4.390369912691534 , 2.3387485516744015 );
        vec[ 313 ] = new Vector3D( 4.061835185002729 , 2.90984699651894 , 1.8292845962809396 );
        vec[ 314 ] = new Vector3D( 3.6431808477473044 , 3.774666377275198 , 1.5521276289039916 );
        vec[ 315 ] = new Vector3D( 4.5467344494483735 , 3.0203309331749355 , 5.7374003596810255 );
        vec[ 316 ] = new Vector3D( 5.9533747289353895 , 2.5195997874576626 , 5.075399481810841 );
        vec[ 317 ] = new Vector3D( 5.227774561577142 , 3.206158723866829 , 5.029126120506537 );
        vec[ 318 ] = new Vector3D( 5.757222040254815 , 5.3683082788968735 , 2.165966109239641 );
        vec[ 319 ] = new Vector3D( 5.435369667321902 , 5.583757395062144 , 3.7526628183963098 );
        vec[ 320 ] = new Vector3D( 5.158736142733473 , 5.826799345977981 , 2.8229292527072967 );
        vec[ 321 ] = new Vector3D( 2.185024260719632 , 2.5766689630425237 , 7.125190473934425 );
        vec[ 322 ] = new Vector3D( 2.7983056714909766 , 2.287960410680759 , 5.639206960747609 );
        vec[ 323 ] = new Vector3D( 3.0264948440448904 , 2.4636264440783586 , 6.596845277775242 );

        IAtomList leafList = box.getLeafList();
        int nLeaf = leafList.getAtomCount();
        for (int iLeaf=0; iLeaf<nLeaf; iLeaf++) {
            IAtom a = leafList.getAtom(iLeaf);
            a.getPosition().E(vec[iLeaf]);
        }
    }
        
    private static final long serialVersionUID = 2L;
}
