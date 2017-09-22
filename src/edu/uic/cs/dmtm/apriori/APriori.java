package edu.uic.cs.dmtm.apriori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class APriori {
	
	public static void main(String[] args) throws IOException, NoFrequentItemsetsException, DifferentItemsetSizeException {
		File inputFile = new File("inputdata.txt");
		File parametersFile = new File("parameters.txt");
		File outputFile = new File("output.txt");
		
		ArrayList<Itemset> transactions = new ArrayList<>();
        ArrayList<Itemset> cannotBeTogetherItemsets = new ArrayList<>();
        ArrayList<Item> mustHaveItems = new ArrayList<>();
        TreeMap<String, Item> I = new TreeMap<>();
        
        InputReader inputReader = new InputReader();
        Double SDC = inputReader.read(inputFile, parametersFile, transactions, cannotBeTogetherItemsets, mustHaveItems, I);
                
        TreeSet<Itemset> M = new TreeSet<>(new ItemsetComparator());
        int N = transactions.size();
        int kMax = transactions.stream().mapToInt(t -> t.getItemset().size()).max().getAsInt();
        
        /* generate set M */
        for(Item i : I.values()) {
        	Itemset tmp = new Itemset(SDC);
        	tmp.addItem(i);
        	M.add(tmp);
        }
        
        supportAndTailCounter(transactions, M, N);
        
        /* generate set L */
        TreeSet<Itemset> L = new TreeSet<>(new ItemsetComparator());
		L = generateL(M);
        
        /* generate F1 */
        ArrayList<TreeSet<Itemset>> frequentItemsets = new ArrayList<>();
        
        TreeSet<Itemset> F = new TreeSet<>();
		F = generateF(L, 1);
		frequentItemsets.add(F);

		try {
			for(int k = 2; k < kMax; k++) {
				TreeSet<Itemset> C;
				if(k == 2)
					C = generateLevel2Candidates(L, cannotBeTogetherItemsets, SDC);
				else
					C = generateCandidates(F, cannotBeTogetherItemsets, SDC);
				supportAndTailCounter(transactions, C, N);
				F = generateF(C, k);
				frequentItemsets.add(F);
			}
        } catch (NoFrequentItemsetsException e) {}
		
		/* apply mustHave rule */
		if(!mustHaveItems.isEmpty())
			frequentItemsets.stream()
				.forEach(fk -> fk.removeIf(itemset -> !itemset.mustHave(mustHaveItems)));
        
		System.out.println(frequentItemsets);
		OutputWriter outputWriter = new OutputWriter();
		outputWriter.write(outputFile, frequentItemsets);
	}
	
	private static TreeSet<Itemset> generateLevel2Candidates(TreeSet<Itemset> L, ArrayList<Itemset> cannotBeTogetherItemsets, Double SDC) {
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
						Itemset candidate = outer.join(inner);
						if(!candidate.cannotBeTogether(cannotBeTogetherItemsets))
							C.add(candidate);
					}
				}
			}
		}
		return C;
	}
	
	private static TreeSet<Itemset> generateCandidates(TreeSet<Itemset> F, ArrayList<Itemset> cannotBeTogetherItemsets, Double SDC) throws DifferentItemsetSizeException {
		TreeSet<Itemset> C = new TreeSet<>(new ItemsetComparator());
		int k = 0;
		for(Itemset outer : F) {
			k ++;
			int j = 0;
			for(Itemset inner : F) {
				j ++;
				if(j <= k)
					continue;
				if(outer.isJoinable(inner, SDC)) {
					Itemset candidate = outer.join(inner);
					if(!candidate.prune(F) && !candidate.cannotBeTogether(cannotBeTogetherItemsets))
						C.add(candidate);
				}
			}
		}
		return C;
	}

	private static void supportAndTailCounter(ArrayList<Itemset> transactions, TreeSet<Itemset> candidates, int N) {
		for(Itemset transaction : transactions)
			candidates.stream()
				.forEach(c -> {
					if(transaction.contains(c))
						c.increaseSupportCount();
					if(transaction.contains(c.getTail()))
						c.increaseTailCount();
				});
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
