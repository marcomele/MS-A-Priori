package edu.uic.cs.dmtm.apriori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Java application running the <b>Multiple Minimum Support A Priori</b>
 * algoritghm on a transaction base. The basic A Priori algorithm
 * performs association rule mining as follows:
 * <ul>
 * <li>identify frequent individual items
 * <li>generate higher-dimension item sets
 * <ul>maintain only frequent items and item sets
 * </ul>
 * <p>
 * The Multiple Minimum Support is a generalization of the A Priori algorithm
 * that allows to specify the {@code minsup} threshold separately for each item.
 * <p>
 * This version also provides two more constraints:
 * <ul>
 * <li>on items that can not be together in the same itemset
 * <li>on items that must be present in an itemset to be considered
 * </ul>
 * 
 * @author Marco Mele
 * @author Massimo Piras
 *
 */

public class APriori {
	
	/**
	 * The main application running the MSAPriori algorithm.
	 * @param args no command line arguments are taken.
	 * @throws IOException when I/O system error occur or input files have bad formatted lines; this is considered fatal and aborts the execution.
	 * @throws NoFrequentItemsetsException only caught by the application when no higher-size itemsets can be produced; otherwise rises from a data error and aborts the execution.
	 * @throws DifferentItemsetSizeException arises when the algorithm mistakenly joins itemsets of different sizes; this should never occur and thus is considered fatal and aborts the execution.
	 * @throws NonJoinableItemsetsException arises when the algorithm mistakenly joins itemsets that can't be joined together; this should never occur and thus is considered fatal and aborts the execution.
	 */
	
	public static void main(String[] args) throws IOException,
													NoFrequentItemsetsException,
													DifferentItemsetSizeException, 
													NonJoinableItemsetsException {
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
	
	/**
	 * Generates all frequent 2-itemsets from all 1-itemsets in a set {@code L}
	 * that have support greater than, or equal to, a threshold defined as the {@code MIS}
	 * of the first frequent item; then applies the cannot-be-together rule for pruning.
	 * @param L A {@link TreeSet} of 1-itemsets.
	 * @param cannotBeTogetherItemsets An {@link ArrayList} of itemsets representing the cannot-be-together rules.
	 * @param SDC 
	 * @return A {@link TreeSet} of the leve-2 candidate {@link Itemset}s
	 * @throws NonJoinableItemsetsException
	 * @throws DifferentItemsetSizeException
	 */
	
	private static TreeSet<Itemset> generateLevel2Candidates(TreeSet<Itemset> L,
													ArrayList<Itemset> cannotBeTogetherItemsets,
													Double SDC) throws NonJoinableItemsetsException, DifferentItemsetSizeException {
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
	
	/**
	 * Generates all candidate k-itemsets joining frequent (k-1)-itemsets;
	 * then applies the cannot-be-together rule for pruning.
	 * @param F A {@link TreeSet} of frequent (k-1)-itemsets.
	 * @param cannotBeTogetherItemsets An {@link ArrayList} of itemsets representing the cannot-be-together rules.
	 * @param SDC 
	 * @return A {@link TreeSet} of the leve-2 candidate {@link Itemset}s
	 * @throws NonJoinableItemsetsException
	 * @throws DifferentItemsetSizeException
	 */
	
	private static TreeSet<Itemset> generateCandidates(TreeSet<Itemset> F,
											ArrayList<Itemset> cannotBeTogetherItemsets,
											Double SDC) throws DifferentItemsetSizeException, NonJoinableItemsetsException {
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
	
	/**
	 * Computes the support count and the tail count of all itemsets in
	 * a given {@link ArrayList} with respect to all the transactions.
	 * @param transactions An {@link ArrayList} of all the transaction in the dataset.
	 * @param candidates A {@link TreeSet} of the candidates on which to perform the count.
	 * @param N The total number of transactions.
	 */

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
	
	/**
	 * Generate the set of 1-itemsets with support greater than, or equal to, the
	 * {@code MIS} of the first frequent Itemset, from the list of 1-itemsets.
	 * @param candidates A {@link TreeSet} of 1-itemsets.
	 * @return A {@link TreeSet} the set of 1-items that respect the given frequency threshold.
	 * @throws NoFrequentItemsetsException
	 */
	
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
	
	/**
	 * Generates a collection of frequent k-itemsets from the possible candidates.
	 * @param candidates A {@link TreeSet} of candidates.
	 * @param itemsetSize The size {@code k} of the candidates.
	 * @return A {@link TreeSet} of the frequent k-itemsets.
	 * @throws NoFrequentItemsetsException
	 */
	
	private static TreeSet<Itemset> generateF(TreeSet<Itemset> candidates, int itemsetSize) throws NoFrequentItemsetsException {
		TreeSet<Itemset> F = new TreeSet<>(new ItemsetComparator());
		F.addAll(candidates.stream().filter(c -> c.getSupport() >= c.getMinMIS()).collect(Collectors.toSet()));
		if(F.isEmpty())
			throw new NoFrequentItemsetsException();
		return F;
	}
	
}
