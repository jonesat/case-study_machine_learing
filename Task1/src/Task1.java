import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Random;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTNR;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTopKClassRules;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTopKRules;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.frequentpatterns.apriori.AlgoApriori;
import ca.pfv.spmf.algorithms.frequentpatterns.apriori_close.AlgoAprioriClose;
import ca.pfv.spmf.algorithms.frequentpatterns.charm.AlgoCharm_Bitset;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPClose;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPMax;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.dataset_converter.TransactionDatabaseConverter;
import ca.pfv.spmf.tools.resultConverter.ResultConverter;


public class Task1 {

	public static void main(String[] args) {
		
		// ############################################################# VERY IMPORTANT #####################################################################################################
		// The following string sets the directory that the program will use to find files.
		String preamble = new String("D:\\Coding\\Java\\Task1\\");
		// String preamble = new String("C:\\Users\\n6912125\\OneDrive - Queensland University of Technology\\IT Masters (Computer Science)\\Semester 2 - 2022\\IFN645\\Task 1\\");
		//
		// Change preamble to your directory with everything stored in it.
		// ##################################################################################################################################################################################
		
		

		
		String[] convert_files = new String[] {"bank.arff","bank_no.arff","bank_yes.arff"};
		String[] input_filesAll = new String[] {"bank.txt","bank_no.txt","bank_yes.txt"};
		String[] input_filesQ3 = new String[] {"bank_no.txt","bank_yes.txt"};
		String[] input_filesQ1 = new String[] {"bank.txt"};
		TransactionDatabaseConverter converter = new TransactionDatabaseConverter();
		try {
			for(int i = 0; i<input_filesAll.length;i++) {
				converter.convertARFFandReturnMap(preamble+convert_files[i], preamble+input_filesAll[i], Integer.MAX_VALUE);
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		
		// Comment and uncomment the functions here as required, or run all at once to prepare all outputs simultaneously.
		
//		Question1(input_filesQ1,preamble);
		Question2(input_filesQ3,preamble);
//		Question3(input_filesQ3,preamble);
//		Question4(input_filesQ1,preamble);
//		Question5(input_filesAll,preamble);
	}
	public static void Question1(String[] input_files, String preamble) {
		// Question 1 
		// Frequent Pattern from bank.arff using two frequent pattern mining algorithms.
		// 1. Apriori
		// 2. FPGrowth
		// Loop over 3 support 0.2, 0.3, 0.4
		// Make a table
		
		
		// Create output directory if it doesn't already exist.
		File f = new File(preamble+"Question1\\");
		if(f.mkdir()==true) {
			System.out.println("Directory has been created successfully at: "+preamble+"Question1\\");
		}
		else {
			System.out.println("Directory "+preamble+"Question1\\ already exists");
		}
		// This string sets the label that will determine where exported files end up.
		String label = "Question1\\Question1_";
				
		// Initialise Parameters
		double[] min_support = new double[] {0.2,0.3,0.4};
		
		// Instantiate algorithm objects for Apriori and FP Growth.
		
		

		
		// Instantiate accessory objects for converting output files.
		ResultConverter output_converter = new ResultConverter();
		
		for(String file: input_files) 
		{
			for(int i = 0; i < min_support.length;i++)
			{
				try {
					// Instantiate and run Apriori Algorithm		
					long startData = System.nanoTime();
					AlgoApriori Apriori = new AlgoApriori();
					Apriori.runAlgorithm(min_support[i], preamble+file, preamble+label+"Apriori_"+min_support[i]+".txt");
					long endData = System.nanoTime();
					double elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+file,preamble+label+"Apriori_"+min_support[i]+".txt",preamble+label+"Final_Apriori_"+min_support[i]+".txt",null);
					System.out.println("########## Apriori ##########");
					System.out.println("\nThe "+Apriori.getClass().getCanonicalName()+" model with min support: "+min_support[i] +" took " + elapsedData + " seconds\n");
					Apriori.printStats();
					
					// Instantiate and run FP Growth Algorithm		
					startData = System.nanoTime();
					AlgoFPGrowth FPGrowth = new AlgoFPGrowth();
					FPGrowth.runAlgorithm(preamble+file, preamble+label+"FPGrowth_"+min_support[i]+".txt",min_support[i]);
					endData = System.nanoTime();
					elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+file,preamble+label+"FPGrowth_"+min_support[i]+".txt",preamble+label+"Final_FPGrowth_"+min_support[i]+".txt",null);
					System.out.println("########## FPGrowth ##########");
					System.out.println("\nThe "+FPGrowth.getClass().getCanonicalName()+" model with min support: "+min_support[i] +"  took " + elapsedData + " seconds\n");
					FPGrowth.printStats();
				
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
		}
	}	
	public static void Question2(String[] input_files, String preamble) {
		
		// Question 2 
		// Use 1 algorithm - the better one
		// 1. FPGrowth
		// Generate top 5 most frequent size - 3 patterns from bank_yes.arff and bank_no.arff
		// Talk a bit
		
		// Create output directory if it doesn't already exist.
		File f = new File(preamble+"Question2\\");
		if(f.mkdir()==true) {
			System.out.println("Directory has been created successfully at: "+preamble+"Question2\\");
		}
		else {
			System.out.println("Directory "+preamble+"Question2\\ already exists");
		}
		
		
		// This string sets the label that will determine where exported files end up.
		String label = "Question2\\Question2_";
	
		// Initialise Parameters
		double[] min_support = new double[] {0.2,0.3,0.4};
  		//double min_confidence = 0.5;
  		//int top_k = 5;
		

		// Instantiate some loop variables for timing that we will reuse
		long startData;
		long endData;
		long elapsedData;
		
		// Loop over input files and over support
		try {
			for(String file:input_files) {
				for(int i = 0; i<min_support.length;i++) {	
					// Instantiate FPGrowth Algorithm Object
					AlgoFPGrowth FPGrowth = new AlgoFPGrowth();
					//	AlgoTopKRules rules = new AlgoTopKRules();
					
					// Configure FPGrowth algorithm to restrict it to only patterns of size 3.
					FPGrowth.setMaximumPatternLength(3);
					FPGrowth.setMinimumPatternLength(3);
					
					// Instantiate accessory objects for converting output files.
					ResultConverter output_converter = new ResultConverter();
					
					// Run FP Growth Algorithm		
					startData = System.nanoTime();
					FPGrowth.runAlgorithm(preamble+file, preamble+label+file.replace(".txt", "_")+"FPGrowth_"+min_support[i]+".txt",min_support[i]);
					endData = System.nanoTime();
					elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+file,preamble+label+file.replace(".txt", "_")+"FPGrowth_"+min_support[i]+".txt",preamble+label+file.replace(".txt", "_")+"Final_FPGrowth_"+min_support[i]+".txt",null);
					System.out.println("########## FPGrowth ##########");
					System.out.println("\nThe "+FPGrowth.getClass().getCanonicalName()+" model with min support: "+min_support[i] +"  took " + elapsedData + " seconds\n");
					FPGrowth.printStats();
				}
			}
			
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static void Question3(String[] input_files, String preamble) {
		
		// Question 3 
		// Generate the top 5 most frequent maximum patterns from bank_yes.arff and bank_no.arff
		// Talk about similarity or difference - use support 0.2, 0.3, 0.4
		
		// Create output directory if it doesn't already exist.
		File f = new File(preamble+"Question3\\");
		if(f.mkdir()==true) {
			System.out.println("Directory has been created successfully at: "+preamble+"Question3\\");
		}
		else {
			System.out.println("Directory "+preamble+"Question3\\ already exists");
		}
		
		// This string sets the label that will determine where exported files end up.
		String label = "Question3\\Question3_";
		
		// Initialise parameters
		double[] min_support = new double[] {0.01,0.2,0.3,0.4};
		
		
		
		// Change the order of the loops, loopify both segments of printing and generating model and model elements 
		for(String file: input_files) 
		{
			System.out.println("###################################  "+file+"  ###################################");	
			for(int i = 0; i < min_support.length;i++)
			{
				try {
					// Instantiate Frequent maximal patterns algorithm
					AlgoFPMax FPMax = new AlgoFPMax();
					
					// Instantiate accessory objects for conversion
					ResultConverter output_converter = new ResultConverter();
					
					// Run Frequent Maximal Pattterns Algorithm		
					long startData = System.nanoTime();
					FPMax.runAlgorithm(preamble+file, preamble+label+"FP_Max_"+file.replace(".txt", "_")+min_support[i]+".txt",min_support[i]);
					long endData = System.nanoTime();
					double elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+file,preamble+label+"FP_Max_"+file.replace(".txt", "_")+min_support[i]+".txt",preamble+label+"Final_FP_Max_"+file.replace(".txt", "_")+min_support[i]+".txt",null);
					System.out.println("########## Frequent Maximal Patterns ##########\n");
					System.out.println("\nThe "+FPMax.getClass().getCanonicalName()+" model took " + elapsedData + "seconds\n");
					FPMax.printStats();
					System.out.println("###################################\n\n");					
				} catch (IOException e) {
					
					e.printStackTrace();
				}				
			}
		}
	}
	public static void Question4(String[] input_files, String preamble) {
		// Question 4
		// Use three algorithms to generate frequent closed patterns from bank.arff
		// 1. Apriori Close
		// 2. FP Close
		// 3. Charm Bitset
		// Use support 0.2, 0.3, 0.4
		// Compare time efficiency with a table
		
		// Create output directory if it doesn't already exist.
				File f = new File(preamble+"Question4\\");
				if(f.mkdir()==true) {
					System.out.println("Directory has been created successfully at: "+preamble+"Question4\\");
				}
				else {
					System.out.println("Directory "+preamble+"Question4\\ already exists");
				}

		// This string sets the label that will determine where exported files end up.
		String label = "Question4\\Question4_";
		
		// Parameters
		double[] min_support = new double[] {0.2,0.3,0.4};
		
		// Instantiate required accessory objects, database and converter.
		ResultConverter output_converter = new ResultConverter();
		
		// I wanted to create a single function to loop over all the closed pattern algorithms but I couldn't figure out the common class between the closed pattern algorithms : (
		for(int i = 0; i < min_support.length;i++) 
		{			
				try {					
					// Instantiate algorithm object and run FCP_Apriori Algorithm		
					long startData = System.nanoTime();
					AlgoAprioriClose AprioriClose = new AlgoAprioriClose();

					AprioriClose.runAlgorithm(min_support[i], preamble+input_files[0], preamble+label+"FCP_Apriori_"+min_support[i]+".txt");
					long endData = System.nanoTime();
					double elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+input_files[0],preamble+label+"FCP_Apriori_"+min_support[i]+".txt",preamble+label+"Final_FCP_Apriori_"+min_support[i]+".txt",null);
					System.out.println("########## FCP Apriori with support: "+min_support[i]+"##########\n");
					System.out.println("The "+AprioriClose.getClass().getCanonicalName()+" model took " + elapsedData + "seconds\n");
					AprioriClose.printStats();
					System.out.println("###################################\n\n");
					
					// Instantiate algorithm object and run FCP_Growth Algorithm		
					startData = System.nanoTime();

					AlgoFPClose FCP_Growth = new AlgoFPClose();
					FCP_Growth.runAlgorithm(preamble+input_files[0], preamble+label+"FCP_Growth_"+min_support[i]+".txt",min_support[i]);
					endData = System.nanoTime();
					elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+input_files[0],preamble+label+"FCP_Growth_"+min_support[i]+".txt",preamble+label+"Final_FCP_Growth_"+min_support[i]+".txt",null);
					System.out.println("########## FCP Growth with support: "+min_support[i]+"##########\n");
					System.out.println("The "+FCP_Growth.getClass().getCanonicalName()+" model took " + elapsedData + "seconds\n");
					AprioriClose.printStats();
					System.out.println("###################################\n\n");
					
					TransactionDatabase tdb = new TransactionDatabase();
					tdb.loadFile(preamble+input_files[0]);
					
					// Instantiate algorithm object and run FCP_Charm Algorithm
					startData = System.nanoTime();
					AlgoCharm_Bitset FCP_Charm = new AlgoCharm_Bitset();
					FCP_Charm.runAlgorithm(preamble+label+"FCP_Charm_"+min_support[i]+".txt",tdb,min_support[i],false,10000);
					endData = System.nanoTime();
					elapsedData = (endData - startData);
					elapsedData = elapsedData/ 1000000000;
					
					// Output Results
					output_converter.convert(preamble+input_files[0],preamble+label+"FCP_Charm_"+min_support[i]+".txt",preamble+label+"Final_FCP_Charm_"+min_support[i]+".txt",null);
					System.out.println("########## FCP Charm with support: "+min_support[i]+"##########\n");
					System.out.println("The "+FCP_Growth.getClass().getCanonicalName()+" model took " + elapsedData + "seconds\n");
					
					FCP_Charm.printStats();
					System.out.println("###################################\n\n");

				} catch (IOException e) {
					
					e.printStackTrace();
				}
		}
	}
	public static void Question5(String[] input_files, String preamble) {	
		// Question 5
		// Use bank.arff generate top 10 most frequent association rules with:
		// 1. subscribed = yes as the consequent
		// 2. subscribed = no as the consequent
		// Specify the minimum confidence used
		// List the rules generated (20) of them
		// Talk about redundant rules
		
		// Inputs: Base file path
		// Inputs: Array of .arff file names needed
		// Side Effects: Writes several files to directory
		// 1. output non-redundant rules
		// 2. output converted non-redundant rules
		// 3. output topK rules
		// 4. output converted topK rules
		
		// Create output directory if it doesn't already exist.
		File f = new File(preamble+"Question5\\");
		if(f.mkdir()==true) {
			System.out.println("Directory has been created successfully at: "+preamble+"Question5\\");
		}
		else {
			System.out.println("Directory "+preamble+"Question5\\ already exists");
		}
		
		
		// This string sets the label that will determine where exported files end up.
		String label = "Question5\\Question5_";

		// Initialise Parameters
		double min_confidence = 0.3;
		int top_k = 10;
		// 11 is subscribed = no, 42 is subscribed = yes.
		int consequents[][] = new int[][] {
				new int[] {11},
				new int[] {42},
		};
		// Need a converter object 
		ResultConverter output_converter = new ResultConverter();
		


		try {
			// ################################################  Top-K Class Rules ##########################################################################
			// Initialise some parameters that will be reused.
			String out_label;
			long startData;
			long endData;
			double elapsedData;
			
			for(int[] consequent : consequents) {
				// Create objects - Top K Rules with specified consequent
				AlgoTopKClassRules topkconsequent = new AlgoTopKClassRules();				
				
				// Initialise database.
				Database db = new Database();
				// Load data into database
				db.loadFile(preamble+input_files[0]);	
								
				// Run Algorithm
				startData = System.nanoTime();
				topkconsequent.runAlgorithm(top_k,min_confidence,db,consequent);
				endData = System.nanoTime();
				elapsedData = (endData - startData);
				elapsedData = elapsedData/ 1000000000;
				
				// Hardcode the labels for the output files.
				if(consequent[0] == 42) {
					out_label = "Yes";
				}
				else {
					out_label = "No";	
				}
				// Write to file and convert to readable format
				topkconsequent.writeResultTofile(preamble+label+out_label+"_class_rules.txt");
				output_converter.convert(preamble+input_files[0],preamble+label+out_label+"_class_rules.txt",preamble+label+out_label+"_final_class_rules.txt",null);

				// Output Results
				System.out.println("########## Top K - Class Rules: "+out_label+" class ##########\n");
				System.out.println("\nThe "+topkconsequent.getClass().getCanonicalName()+" model took " + elapsedData + "seconds\n");
				topkconsequent.printStats();
				System.out.println("###################################\n\n");	
			}
						
			// ################################################  Top-K non redundant  ##########################################################################
			// Generate dataset size from Apriori algorithm for top-K non redundant rules.
			
			for(int i = 1; i<input_files.length;i++)
			{
				// Create objects - Top K Rules with non-redundant rules for comparison with class rules.
				// Need to generate an apriori object to yield database size
				AlgoApriori algo_Apri = new AlgoApriori();
				AlgoTNR TNR = new AlgoTNR();
				
				// Instantiate database and load data 
				Database rdb = new Database();
				rdb.loadFile(preamble+input_files[i]);
				// Run apriori algorithm - do include it's runtime in TNR runtime.
				startData = System.nanoTime();
				algo_Apri.runAlgorithm(min_confidence, preamble+input_files[i],preamble+label+"apriori_output.txt");
				int dataset_size = algo_Apri.getDatabaseSize();
				
				// Run Algorithm
				TNR.runAlgorithm(top_k,min_confidence,rdb,dataset_size);
				endData = System.nanoTime();
				elapsedData = (endData - startData);
				elapsedData = elapsedData/ 1000000000;
							
				// Write to file and convert to readable format
				TNR.writeResultTofile(preamble+label+input_files[i].replace(".txt", "_")+"non_redundant_rules.txt");						
				output_converter.convert(preamble+input_files[i],preamble+label+input_files[i].replace(".txt", "_")+"non_redundant_rules.txt",preamble+label+input_files[i].replace(".txt", "_")+"final_non_redundant_rules.txt",null);
				
				// Output Results
				System.out.println("########## Top K - Non Redundant: "+input_files[i].replace(".txt", "_")+" class ##########\n");
				System.out.println("\nThe "+TNR.getClass().getCanonicalName()+" model took " + elapsedData + "seconds\n");
				TNR.printStats();
				System.out.println("###################################\n\n");
			}
			
		}catch(IOException e1) {
			e1.printStackTrace();
		}
	}


		
		
		
	
		
		
		
		
		
		

}

