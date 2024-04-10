import java.io.IOException;
import java.text.NumberFormat;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;



public class Task3 {
	
	public static StringToWordVector CreateFilter() {
		// This function creates the word filter and configures it according to the findings from weka in part 1 of Task 3.
		
		// Instantiates the String to word vector filter.
		StringToWordVector swFilter = new StringToWordVector();
		
		// Configure the filter according to the settings yielded from the weka experiments.
		swFilter.setAttributeIndices("first-last"); // 
		swFilter.setIDFTransform(false);
		swFilter.setTFTransform(true);
		swFilter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL,StringToWordVector.TAGS_FILTER));
		swFilter.setDoNotCheckCapabilities(false); 
		swFilter.setDoNotOperateOnPerClassBasis(false);
		swFilter.setInvertSelection(false);
		swFilter.setLowerCaseTokens(true);
		swFilter.setOutputWordCounts(true);
		swFilter.setMinTermFreq(2);
		SnowballStemmer stemmer = new SnowballStemmer();
		stemmer.setStemmer("porter");
		swFilter.setStopwordsHandler(new Rainbow());
		swFilter.setTokenizer(new WordTokenizer()); 
		swFilter.setWordsToKeep(100);
		return swFilter;
	}
	
	public static FilteredClassifier CreateFilteredClassifier(StringToWordVector filter, AbstractClassifier classifier,Instances data) {
		// This function creates a filtered classifier using the input classifier algorithm and string to word filter.
		
		// Instantiate the filtered classifier
		FilteredClassifier filter_classifier = new FilteredClassifier();
		// Set the string to word filter as the input filter
		filter_classifier.setFilter(filter);
		
		// Set the classifier for the filtered classifier as the input classifier
		filter_classifier.setClassifier(classifier);
		try {
			// Attempt to build the classifier
			filter_classifier.buildClassifier(data);
		
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return filter_classifier;
		
	}
	
	public static void doEvaluation(FilteredClassifier classifier,Instances data) {
		// This function performs the required crossvalidation of the filtered classifier.
		
		// Declare the evaluation object
		
		try {
			// Instantiate the evaluation object in the loop.
			Evaluation eval = new Evaluation(data);
			
			// Perform the cross validation
			eval.crossValidateModel(classifier, data, 10, new Random(1));
			
			// Fancy formatter for percentages
			NumberFormat a = NumberFormat.getPercentInstance();
			a.setMinimumFractionDigits(2);
			
			// Output results
			System.out.println("Done");
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println("Proportion of correctly classified instances: "+ a.format(eval.correct()/eval.numInstances())+"\n");
			System.out.println("################################################## Evaluating on filtered training dataset done ##################################################");	
		} catch (Exception e) {
			System.out.println("Error in ...");
			e.printStackTrace();
		}
	}
	public void doClassification(Instances data) {
		// This function performs a filtered classification for a number of different  classification algorithms.
		
		// Instantiate the filter object with the CreateFilter() method.
		StringToWordVector swFilter = CreateFilter();
		
		// Create an array of all the classifiers we'd like to perform filtered classification with.
		AbstractClassifier[] classifiers = new AbstractClassifier[] {new IBk(), new SMO(), new J48(), new HoeffdingTree()};
		
		// Create a string array of the names of all the classifiers, admittedly I could just use <AbstractClassifier>.getClass().getCanonicalName() instead as I loop.
		String[] names = new String[] {"IBk","SMO","J48","HoeffdingTree"};
	
		// Loop over each classifier, perform that classification, time it and then output the results.
		for(int i = 0;i<classifiers.length;i++) {
			System.out.println("Building model - "+names[i]+"...");
			
			long startData = System.nanoTime();
			// Create Filtered classifier for given classifier and string to word filter
			FilteredClassifier filter_classifier = CreateFilteredClassifier(swFilter,classifiers[i],data);
			// Perform the evaluation with filtered classifier
			doEvaluation(filter_classifier,data);
			long endData = System.nanoTime();
			double elapsedData = (endData - startData);
			elapsedData = elapsedData/ 1000000000;
			
			// Output results.
			System.out.println("\nThe "+names[i]+" model took " + elapsedData + "seconds\n");
			System.out.println("########################################################################################################################\n\n\n");	
			
		}		
	}

	public static void main(String[] args)throws Exception {
		// ############################################################# VERY IMPORTANT #####################################################################################################
		// The following string sets the directory that the program will use to find files.
		String preamble = new String("C:\\Code\\Java\\Task3\\");		
		//
		// Change preamble to your directory with everything stored in it.
		// ##################################################################################################################################################################################
		
		DataSource source = new DataSource(preamble+"News.arff");
		Instances data = source.getDataSet();
		data.setClassIndex(1);
		Task3 task3 = new Task3();
		task3.doClassification(data);

	}
}
