import java.text.NumberFormat;
import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

public class Task2 {
	public void doAllClassification(String path,String classLabel) {
		try {
			DataSource source = new DataSource(path);
			Instances data = source.getDataSet();
			data.setClass(data.attribute(classLabel));			
			Question1(data);
			Question2(data);
		} catch (Exception e) {
			System.out.println("Error or somethn idk");
			e.printStackTrace();
		}
	}
	public void Question1(Instances data){
		// Java code for Question 1 of Task 2 - Java
		// Instantiate the algorithms from 1.2 with the number of attributes they need.
		String[] names = new String[] {"NaiveBayes","Logistic","PART","J48"};
		int[] numAttList = new int[] {3,6,10,6};

		try {
			// Instantiate the classifier and evaluation objects.
			// We use four classifiers for comparison NaiveBayes, Logistic, PART(), J48
			// We use two single attribute filters for comparison, InfoGain and GainRatio
			AbstractClassifier[] classifiers = new AbstractClassifier[] {new NaiveBayes(),new Logistic(),new PART(),new J48()};
			ASEvaluation[] evaluators = new ASEvaluation[] {new InfoGainAttributeEval(),new GainRatioAttributeEval()};
			
			// Loop over all evaluator objects and over all classifiers
			for(ASEvaluation evaluator: evaluators) {
				for(int i = 0; i<classifiers.length;i++) {
					
					// Begin classification according to each of the classifiers with each of the evaluators.
					long startSource = System.nanoTime(); // Start timer
					doClassification(classifiers[i],data,names[i],numAttList[i],evaluator);
					long endSource = System.nanoTime(); // End Timer
					double elapsedSource = (endSource - startSource);
					elapsedSource = elapsedSource/ 1000000000;
					System.out.println("Runtime: " + elapsedSource + " seconds\n");
				}
				
			}				
		}catch (Exception e) {
			System.out.println("Error or somethn idk");
			e.printStackTrace();
		}	
	}
	public void Question2(Instances data) {
		// Java code for Question 1 of Task 2 - Java
		// Instantiate the algorithms from 1.3 with the number of attributes they need.

		String[] names = new String[] {"PART","J48"};
		int[] numAttList = new int[] {10,6};
		Boolean[] minimizeList = new Boolean[] {true,false}; // Parameters for cost sensitive learning.
		try {
			// Instantiate the classifiers
			// We use four classifiers for comparison NaiveBayes, Logistic, PART(), J48
			AbstractClassifier[] classifiers = new AbstractClassifier[] {new PART(), new J48()};
		
			System.out.println("########## Cost Sensitive Analysis ##########");
			// Loop over all classifiers and parameters.
			for(int i = 0; i<classifiers.length;i++) {
				for(Boolean minimize: minimizeList) {
					doCostSensitiveClassification(classifiers[i],data,names[i],minimize);	
				}
			}			
		} catch (Exception e) {
			System.out.println("Error or somethn idk");
			e.printStackTrace();
		}
	}
	public void doClassification(AbstractClassifier classifier, Instances data, String Name,int numAtt, ASEvaluation evaluator) throws Exception{
		// Set the ranker and use the number of attributes for that classifier to maximise the accuracy, as determined in 1.2.
		Ranker ranking = new Ranker();
		ranking.setNumToSelect(numAtt);
		
		// Instantiate the attribute selected classifier and configure it to our needs.
		AttributeSelectedClassifier asc = new AttributeSelectedClassifier();
		asc.setClassifier(classifier);
		asc.setEvaluator(evaluator);
		asc.setSearch(ranking);
		
		// Build the classifier
		System.out.println("Building dataset...");
		asc.buildClassifier(data);
		
		// Formatter for percentages.
		NumberFormat a = NumberFormat.getPercentInstance();
		a.setMinimumFractionDigits(2);
		
		// Instantiate evaluator and setup cross validation with 10 folds and seed 1.
		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(asc, data, 10, new Random(1));
		
		// Output results
		System.out.println(evaluator.toString());
		System.out.println(Name + " : "+ eval.correct()+" correctly classified instances out of "+eval.numInstances());
		System.out.println(Name + " : "+ a.format(eval.correct()/eval.numInstances()));		
	}
	public void doCostSensitiveClassification(AbstractClassifier classifier, Instances data, String Name, Boolean MinimizeCost) throws Exception{
		// Instantiate the cost matrix
		String costMatrix = "[0.0 5.0;1.0 0.0 ]";
		CostMatrix matrix = CostMatrix.parseMatlab(costMatrix);
		
		// Build the classifier for baseline cost results
		classifier.buildClassifier(data);
		classifier.setBatchSize("100");
		
		// Build the cost sensitive classifier and assign it the input classifier
		CostSensitiveClassifier CostSensitiveClassifier = new CostSensitiveClassifier();
		CostSensitiveClassifier.setClassifier(classifier);		
		
		// Configure the classifier with the cost matrix, whether or not we wish to minimize costs set the seed as 1.
		CostSensitiveClassifier.setCostMatrix(matrix);
		CostSensitiveClassifier.setMinimizeExpectedCost(MinimizeCost);
		CostSensitiveClassifier.setSeed(1);
		
		// Fancy formatter for percent
		NumberFormat a = NumberFormat.getPercentInstance();
		a.setMinimumFractionDigits(2);
		
		// Create an array of classifiers so we can get base costs with the cost matrix vs those from full cost sensitive classification.
		AbstractClassifier[] listClassifiers = new AbstractClassifier[] {classifier,CostSensitiveClassifier};
		
		// Loop over classifiers
		for(AbstractClassifier classify: listClassifiers) {
			System.out.println(classify.getClass().getCanonicalName()); // Output the name
			
			// Track the average results over all folds of cross validation
			double avgAccuracy = 0;
			double avgCost = 0;
			double avgInstances = 0;
			
		
			long startSource = System.nanoTime(); 	// Begin timing the model evluation
			
			// Loop over all the folds
			for(int i = 0;i<10;i++) {
				// Instantiate a new evaluator with the cost matrix.
				Evaluation eval = new Evaluation(data,matrix);				
				Random r = new Random(i);
				
				// Begin cross validation
				eval.crossValidateModel(classify, data, 10, r);
				
				// Update average metrics.
				avgAccuracy+=(eval.correct()/eval.numInstances());
				avgCost+=eval.totalCost();
				avgInstances+=eval.correct();
				}
			long endSource = System.nanoTime(); // End Timing
			double elapsedSource = (endSource - startSource);
			elapsedSource = elapsedSource/ 1000000000;
			
			// Output Results
			System.out.println("Runtime: " + elapsedSource + " seconds");
			System.out.println("Classifier "+Name+" with MinmizeExpectedCost = "+MinimizeCost+" has, an average accuracy of: "+a.format(avgAccuracy/10));
			System.out.println("Classifier "+Name+" has an average of: "+(avgInstances/10)+" correctly classified instances");
			System.out.println("Classifier "+Name+" has an average cost of: "+(avgCost/10)+"\n");

		}
	}
	public static void main(String[] args) {
		// ############################################################# VERY IMPORTANT #####################################################################################################
		// The following string sets the directory that the program will use to find files.
		String preamble = new String("C:\\Code\\Java\\Task2\\");		
		//
		// ##################################################################################################################################################################################
		
		// Declare the data files and class labels.
		String[] files = new String[] {"COVID19_risk.arff"};
		String[] labels = new String[] {"infection_risk"};
		
		// Instantiate the class that contains the methods to do all classification
		Task2 weka1 = new Task2();
		// Loop over all files to be reviewed - in this instance we only have one file
		for(int i =0; i<files.length;i++) {
					System.out.println(String.format("For dataset: %s",files[i]));
					weka1.doAllClassification(preamble+files[i], labels[i]);
								
		}
	}
}
