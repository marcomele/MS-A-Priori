package edu.uic.cs.dmtm.apriori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class APriori {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("inputdata.txt");
		File parametersFile = new File("parameters.txt");
		
		ArrayList<Itemset> transactions = new ArrayList<>();
        ArrayList<Itemset> cannotBeTogetherItemsets = new ArrayList<>();
        ArrayList<Item> mustHaveItems = new ArrayList<>();
        TreeMap<Integer, Item> I = new TreeMap<>();
        
        InputReader inputReader = new InputReader();
        Double SDC = inputReader.read(inputFile, parametersFile, transactions, cannotBeTogetherItemsets, mustHaveItems, I);
        
        TreeSet<Itemset> M = new TreeSet<>(new ItemsetComparator());
        int N = transactions.size();
        
        for(Item i : I.values()) {
        	Itemset tmp = new Itemset(SDC);
        	tmp.addItem(i);
        	M.add(tmp);
        }
        
        
		
	}
	
	public void supportCounter(ArrayList<Itemset> transactions, TreeSet<Itemset> candidates, int N) {
		for(Itemset transaction : transactions)
			candidates.stream()
				.filter(c -> transaction.contains(c))
				.forEach(c -> c.increaseSupportCount());
		candidates.stream().forEach(c -> c.computeSupport(N));
	}
}
