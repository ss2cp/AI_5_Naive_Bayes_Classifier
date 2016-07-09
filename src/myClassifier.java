import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class myClassifier extends Classifier {
	static double pMore = 0.0;
	static double pLess = 0.0;
	static double total = 0;
	static ArrayList<String> trainingSet = new ArrayList<String>();
	static ArrayList<String> testSet = new ArrayList<String>();
	static ArrayList<String> positiveSet = new ArrayList<String>();
	static ArrayList<String> negativeSet = new ArrayList<String>();
	static HashMap<String, Double> positive = new HashMap<String, Double>();
	static HashMap<String, Double> negative = new HashMap<String, Double>();
	static ArrayList<ArrayList<String>> categories = new ArrayList<ArrayList<String>>();

	/*
	 * Constructor
	 */
	public myClassifier(String namesFilepath) throws FileNotFoundException {
		super(namesFilepath);
		readNameFile(namesFilepath);
	}

	@Override
	public void train(String trainingDataFilpath) {

	}

	@Override
	public void makePredictions(String testDataFilepath) {
		// initialize scanner and variables
		Scanner in;
		double correct = 0;
		double total = 0;

		try {
			in = new Scanner(new File(testDataFilepath));

			while (in.hasNextLine()) {
				double pypositive = pMore;
				double pynegative = pLess;
				String tempLine = in.nextLine();
				String[] line = tempLine.split("\\s+");
				// TODO change this length-2 to -1 when handle the real data
				for (int i = 1; i < line.length - 2; i++) {
					// if it's numerical values, skip to the next aspect
					if (i == 9 || i == 10 || i == 11 || i == 3) {
						continue;
					}
					pypositive *= positive.get(line[i]);
					pynegative *= negative.get(line[i]);
				}
				String prediction = "";
				if (pypositive > pynegative) {
					prediction = ">50K";
				} else {
					prediction = "<=50K";
				}

				// TODO test code
				total++;
				if (prediction.equals(line[line.length - 1])) {
					correct++;
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("There were in total " + total + ", correct "
				+ correct + "\nCorrect rate is: " + correct / total);
	}

	// testSet

	/*-
	 * This method read the train file
	 * Change the number after random to pick poportion of training data
	 * It updates data in trainSet, positiveSet, negativeSet, positive, negative
	 */
	public static void readTrainFile(String filename)
			throws FileNotFoundException {
		PrintWriter printer = new PrintWriter("filename.txt");

		// initialize scanner and variables
		Scanner in = new Scanner(new File(filename));
		int count = 0;

		// we want to have 0.7 of the data to be training set
		while (in.hasNextLine()) {
			count++;
			// TODO change this number to 1 to read all data
			if (Math.random() <= 0.7) {
				// read in the line
				String tempLine = in.nextLine();
				trainingSet.add(tempLine);
				String[] line = tempLine.split("\\s+");
				if (line[line.length - 1].charAt(0) == '>') {
					// increment py_k
					pMore++;
					// add line to positive set
					positiveSet.add(tempLine);
					// update aspects
					for (int i = 1; i < line.length - 2; i++) {
						// if it's numerical values, skip to the next aspect
						if (i == 9 || i == 10 || i == 11 || i == 3) {
							continue;
						}
						if (positive.keySet().contains(line[i])) {
							positive.put(line[i], positive.get(line[i]) + 1);
						} else {
							positive.put(line[i], 1.0);
						}
					}

				} else {
					// increment py_k
					pLess++;
					// add line to negative set
					negativeSet.add(tempLine);
					// update negative aspects
					for (int i = 1; i < line.length - 2; i++) {
						// if it's numerical values, skip to the next aspect
						if (i == 9 || i == 10 || i == 11 || i == 3) {
							continue;
						}
						if (negative.keySet().contains(line[i])) {
							negative.put(line[i], negative.get(line[i]) + 1);
						} else {
							negative.put(line[i], 1.0);
						}
					}
				}
			} else {
				// Skip the line
				String thisLine = in.nextLine();
				// System.out.println(thisLine);
				printer.println(thisLine);
				testSet.add(thisLine);

			}
		}
		printer.close();

		// initialize smoothing factors
		double l = 1.0;
		double j = 0.0;

		// calculate proportion of each aspects
		// (D(x&y)+l) / (D(y)+lj)
		for (String key : positive.keySet()) {
			j = returnJ(key, categories);
			double temp = (positive.get(key) + l)
					/ (positiveSet.size() + l * j);
			positive.put(key, temp);
		}
		for (String key : negative.keySet()) {
			j = returnJ(key, categories);
			double temp = (negative.get(key) + l)
					/ (negativeSet.size() + l * j);
			negative.put(key, temp);
		}

		// adding those aspects that never showed up
		for (ArrayList<String> cate : categories) {
			if (cate.size() > 1) {
				for (String asp : cate) {
					if (!positive.keySet().contains(asp)) {
						j = returnJ(asp, categories);
						positive.put(asp, (0 + l)
								/ (positiveSet.size() + l * j));
					}
					if (!negative.keySet().contains(asp)) {
						j = returnJ(asp, categories);
						negative.put(asp, (0 + l)
								/ (negativeSet.size() + l * j));
					}
				}
			}
		}

		total = pMore + pLess;
		pMore = pMore / total;
		pLess = pLess / total;

		System.out.println("There are " + count
				+ " lines of data. We will learn from " + trainingSet.size()
				+ ", test from " + testSet.size());

		// System.out.println(positive.size() + " " + positive);
		// System.out.println(negative.size() + " " + negative);
	}

	/*
	 * This method will return value j for each category a.k.a. the size of that
	 * category
	 */
	public static double returnJ(String aspect,
			ArrayList<ArrayList<String>> categories) {
		for (ArrayList<String> arr : categories) {
			if (arr.contains(aspect)) {
				return arr.size();
			}
		}
		// never reach
		return 0;
	}

	/*
	 * This method reads in the file
	 */
	public static void readNameFile(String filename)
			throws FileNotFoundException {
		Scanner in = new Scanner(new File(filename));
		// read in the response variables
		String results = in.nextLine();
		// skip that empty line
		in.nextLine();

		// store each line in an array
		ArrayList<String> lines = new ArrayList<String>();
		while (in.hasNextLine()) {
			lines.add(in.nextLine());
		}

		// System.out.println("Response results are: " + results);

		// parse the txt file and add aspects in an arraylist to categories
		for (int i = 0; i < lines.size(); i++) {
			ArrayList<String> aspects = new ArrayList<String>();
			String[] line = lines.get(i).split("\\s+");
			// System.out.print(line[0] + "\n");
			for (int j = 1; j < line.length; j++) {
				aspects.add(line[j]);
				// System.out.print(line[j] + ", ");
			}
			// System.out.println("\n");
			categories.add(aspects);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		myClassifier temp = new myClassifier("census.names");
		// System.out.println(categories);

		temp.readTrainFile("census.train");
		temp.makePredictions("filename.txt");
	}

}
