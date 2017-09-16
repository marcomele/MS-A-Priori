package edu.uic.cs.dmtm.apriori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class APriori {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("inputdata.txt");
		File parametersFile = new File("parameters.txt");
		
		ArrayList<Itemset> itemsets = new ArrayList<>();
        ArrayList<Itemset> cannotBeTogetherItemsets = new ArrayList<>();
        ArrayList<Item> mustHaveItems = new ArrayList<>();
        TreeMap<Integer, Item> items = new TreeMap<>();
        
        InputReader inputReader = new InputReader();
        Double SDC = inputReader.read(inputFile, parametersFile, itemsets, cannotBeTogetherItemsets, mustHaveItems, items);
        
        System.err.println(SDC);
		
	}
}
