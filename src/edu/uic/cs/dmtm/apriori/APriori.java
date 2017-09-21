package edu.uic.cs.dmtm.apriori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class APriori {
	
	public static void main(String[] args) throws IOException {
		File inputFile = new File("inputdata.txt");
		File parametersFile = new File("parameters.txt");
		
		ArrayList<Itemset> transactions = new ArrayList<>();
        ArrayList<Itemset> cannotBeTogetherItemsets = new ArrayList<>();
        ArrayList<Item> mustHaveItems = new ArrayList<>();
        TreeMap<String, Item> I = new TreeMap<>();
        
        InputReader inputReader = new InputReader();
        Double SDC = inputReader.read(inputFile, parametersFile, transactions, cannotBeTogetherItemsets, mustHaveItems, I);
        
        System.err.println(I);
        
        TreeSet<Itemset> M = new TreeSet<>(new ItemsetComparator());
        int N = transactions.size();
        int kMax = transactions.stream().mapToInt(t -> t.getItemset().size()).max().getAsInt();
        
        /* generate set M */
        for(Item i : I.values()) {
        	Itemset tmp = new Itemset(SDC);
        	tmp.addItem(i);
        	M.add(tmp);
        }
        
        supportCounter(transactions, M, N);
        for(Itemset is : M)
        	System.err.println(is.getItemset() + ": " + is.getSupport() + ", " + is.getMinMIS());
        
        /* generate set L */
        
        TreeSet<Itemset> L = new TreeSet<>(new ItemsetComparator());
		try {
			L = generateL(M);
		} catch (NoFrequentItemsetsException e) {
			System.err.println("Error: no frequent itemsets in the dataset; terminating.");
			e.printStackTrace();
		}       
        System.err.println("L: " + L);
        
        /* generate F1 */
        
        ArrayList<TreeSet<Itemset>> frequentItemsets = new ArrayList<>();
        
        TreeSet<Itemset> F = new TreeSet<>();
		try {
			F = generateF(L, 1);
		} catch (NoFrequentItemsetsException e) {
			System.err.println("Error: no frequent itemsets in the dataset; terminating.");
			e.printStackTrace();
		}
        System.err.println("F: " + F);
        
        for(int k = 2; k < kMax; k++) {
        	TreeSet<Itemset> C;
        	if(k == 2) {
        		C = generateLevel2Candidates(L, SDC);
        		supportCounter(transactions, C, N);
        		System.err.println("C: " + C);
        	}
        }
        
	}
	
	private static TreeSet<Itemset> generateLevel2Candidates(TreeSet<Itemset> L, Double SDC) {
		TreeSet<Itemset> C = new TreeSet<>(new ItemsetComparator());
		int k = 0;
		for(Itemset outer : L) {
			k ++;
			if(outer.getSupport() >= outer.getMinMIS()) {
				int j = 0;
				for(Itemset inner : L) {
					j ++;
					if(j <= k)
						continue;
					double inSup = inner.getSupport(), outSup = outer.getSupport();
					if(inSup >= outer.getMinMIS() && Math.abs(outSup - inSup) <= SDC) {
						C.add(outer.join(inner));
					}
				}
			}
		}
		return C;
	}
	
	private static TreeSet<Itemset> generateCandidates(TreeSet<Itemset> F, Double SDC) throws DifferentItemsetSizeException {
		TreeSet<Itemset> C = new TreeSet<>(new ItemsetComparator());
		int k = 0;
		for(Itemset outer : F) {
			int j = 0;
			for(Itemset inner : F) {
				if(j <= k)
					continue;
				if(outer.isJoinable(inner, SDC)) {
					Itemset candidate = outer.join(inner);
					if(!candidate.prune(F))
						C.add(candidate);
				}
			}
		}
		return C;
	}

	private static void supportCounter(ArrayList<Itemset> transactions, TreeSet<Itemset> candidates, int N) {
		for(Itemset transaction : transactions)
			candidates.stream()
				.filter(c -> transaction.contains(c))
				.forEach(c -> c.increaseSupportCount());
		candidates.stream().forEach(c -> c.computeSupport(N));
	}
	
	private static TreeSet<Itemset> generateL(TreeSet<Itemset> candidates) throws NoFrequentItemsetsException {
		TreeSet<Itemset> L = new TreeSet<>(new ItemsetComparator());
		double threshold = -1.0;
		for(Itemset is : candidates) 
			if(is.getSupport() >= is.getMinMIS()) {
				threshold = is.getMinMIS();
				break;
			}
		if(threshold < 0)
			throw new NoFrequentItemsetsException();
		final double th = threshold;
		L.addAll(candidates.stream().filter(c -> c.getSupport() >= th).collect(Collectors.toSet()));
		return L;
	}
	
	private static TreeSet<Itemset> generateF(TreeSet<Itemset> candidates, int itemsetSize) throws NoFrequentItemsetsException {
		TreeSet<Itemset> F = new TreeSet<>(new ItemsetComparator());
		F.addAll(candidates.stream().filter(c -> c.getSupport() >= c.getMinMIS()).collect(Collectors.toSet()));
		if(F.isEmpty())
			throw new NoFrequentItemsetsException();
		return F;
	}
	
}
