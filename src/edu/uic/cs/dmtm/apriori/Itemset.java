package edu.uic.cs.dmtm.apriori;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A representation of an Itemset, i.e. a collection of {@link Item}s.
 * It is internally represented as a {@link TreeSet} maintaining the
 * ordering given by {@link ItemComparator} based on the {@code Item}s MIS.
 * The collection is a set so no duplicates are allowed. The insertion policy 
 * for duplicates reflects the one followed by a {@link TreeSet}.
 * The Itemset carries information about it's SDC, support, support count,
 * and tail count. The class provides means to retreive the minumum and 
 * maximum MIS among the {@link Item}s it contains.
 * @author Marco Mele
 * @author Massimo Piras
 *
 */

public class Itemset {
	
	private TreeSet<Item> itemset = new TreeSet<>(new ItemComparator());
	private Double sdc;
	private Double support;
	private Double minMIS;
	private Double maxMIS;
	private int supportCount;
	private int tailCount;
	
	public int getSupportCount() {
		return supportCount;
	}
	/**
	 * Increases the support count of this {@link Itemset} by one.
	 */
	public void increaseSupportCount() {
		this.supportCount ++;
	}
	private void minMIS() {
		this.minMIS = itemset.stream().mapToDouble(i -> i.getMis()).min().getAsDouble();
	}
	/**
	 * 
	 * @return the minimum value of {@code MIS} of all the {@link Item}s contained.
	 * @see {@link Item}.
	 */
	public Double getMinMIS() {
		return minMIS;
	}
	/**
	 * 
	 * @return the maximum value of {@code MIS} of all the {@link Item}s contained.
	 * @see {@link Item}.
	 */
	public Double getMaxMIS() {
		return maxMIS;
	}
	private void maxMIS() {
		this.maxMIS = itemset.stream().mapToDouble(i -> i.getMis()).max().getAsDouble();
	}
	
	public void setSupport(Double support) {
		this.support = support;
	}
	public Double getSupport() {
		return support;
	}
	/**
	 * Constructs a new empty {@link Itemset} setting its {@code SDC}.
	 * The support, support count and tail count are set to 0.
	 * @param sdc the SDC required.
	 */
	public Itemset(Double SDC) {
		this.sdc = SDC;
		supportCount = 0;
		tailCount = 0;
		support = 0.0;
	}
	/**
	 * 
	 * @return a {@link TreeSet} representation of the {@link Itemset},
	 * sorted according to the {@link ItemComparator}.
	 */
	public TreeSet<Item> getItemset() {
		return itemset;
	}
	/**
	 * Overwrites the existing itemset with the one provided.
	 * @param itemset A {@link TreeSet} representation of the itemset.
	 */
	public void setItemset(TreeSet<Item> itemset) {
		this.itemset = itemset;
	}
	/**
	 * Adds a new {@link Item} to the itemset. Contextually computes
	 * the minimum and maximum {@code MIS}. Item insertion policies
	 * reflects the {@link TreeSet} ones.
	 * @param item A new {@link Item}.
	 * @see {@link Item}
	 * @see {@link TreeSet}
	 */
	public void addItem(Item item) {
		this.itemset.add(item);
		minMIS();
		maxMIS();
	}
	/**
	 * Computes the {@code support} of the itemset by its
	 * current {@code support count} over the totality of
	 * transactions in the transaction base.
	 * @param N
	 * @throws NumberFormatException when {@code N} is zero or negative.
	 */
	public void computeSupport(int N) throws NumberFormatException {
		if(N <= 0)
			throw new NumberFormatException("The total number of transactions in the transaction base must be a positive, non-zero integer.");
		support = ((double) (supportCount)) / N;
	}
	/**
	 * Tests whether this itemset contains all the {@code Item}s in
	 * the given {@link Itemset}. 
	 * @param other Another {@link Itemset}.
	 * @return {@code true} if all the {@link Item}s contained in {@code other}
	 * are contained in this itemset; {@code false} otherwise.
	 */
	public boolean contains(Itemset other) {
		for(Item i : other.getItemset())
			if(! this.getItemset().contains(i))
				return false;
		return true;
	}
	/**
	 * Retrieves an {@link Item} from the collection, based on
	 * its {@code id}, which are assumed to be unique.
	 * @param id the {@code id} to the item to look up.
	 * @return the corresponding {@link Item} if found, {@code null} otherwise.
	 */
	public Item getItem(String id) {
		return this.itemset.stream().filter(i -> i.getId().equals(id.trim())).findAny().orElse(null);
	}
	public Double getSdc() {
		return sdc;
	}
	public void setSdc(Double sdc) {
		this.sdc = sdc;
	}
	/**
	 * Joins two {@link Itemset}s in an higher-level one, if the two itemsets
	 * are eligible to be joined according to {@link isJoinable}.
	 * @param other Another {@link Itemset}
	 * @return A new {@link Itemset} joining the two, always respecting the 
	 * ordering given by the {@link ItemComparator}.
	 * @throws NonJoinableItemsetsException if the two itemsets are not joinable
	 * according to {@link isJoinable}.
	 * @throws DifferentItemsetSizeException if the two itemsets are not of the same size.
	 * @see #Itemset.isJoinable(Itemset, Double)
	 * @see Itemset 
	 */
	public Itemset join(Itemset other) throws NonJoinableItemsetsException, DifferentItemsetSizeException {
		if(!this.isJoinable(other, sdc))
			throw new NonJoinableItemsetsException();
		Itemset joined = new Itemset(this.sdc);
		joined.itemset.addAll(this.itemset);
		joined.itemset.addAll(other.itemset);
		joined.minMIS();
		joined.maxMIS();
		joined.sdc = this.sdc;
		return joined;
	}
	/**
	 * Defines whether two {@link Itemset}s can be joined together.
	 * Two Itemsets can be joined if they are of the same size, they
	 * contain exactly the same {@link Item}s up to the one before the
	 * last one, different last Item, and the difference between their
	 * support is less than, or equal to, the value of {@code SDC}.
	 * @param other Another {@link Itemset} of the same size.
	 * @param SDC
	 * @return {@code true}, if the two {@link Itemset}s are joinable, {@code false} otherwise.
	 * @throws DifferentItemsetSizeException when the Itemsets are of different sizes.
	 * @see {@link #join(Itemset)}
	 */
	public boolean isJoinable(Itemset other, Double SDC) throws DifferentItemsetSizeException {
		if(this.itemset.size() != other.itemset.size())
			throw new DifferentItemsetSizeException();
		Iterator<Item> f1 = this.itemset.iterator(), f2 = other.itemset.iterator();
		Item last1 = null, last2 = null; 
		while(f1.hasNext()) {
			last1 = f1.next();
			last2 = f2.next();
			if(last1 != last2 && f1.hasNext())
				return false;				
		}
		if(Math.abs(last1.getSupport() - last2.getSupport()) > SDC)
			return false;
		return true;
	}
	/**
	 * Applies the pruning rules to the current itemset and determines 
	 * whether it should be pruned or not from the frequent itemset. An 
	 * Itemset shall be pruned if its first two items share the same {@code MIS},
	 * and among its {@code (k-1)}-subsets containing the first element of this itemset
	 * there is at least one that is not frequent -- i.e. is not in the given set
	 * of frequent itemsets. 
	 * @param F A {@link TreeSet} of frequent {@code k}-itemsets.
	 * @return {@code true}, if this Itemset shall be pruned; {@code false} otherwise.
	 */
	public boolean prune(TreeSet<Itemset> F) {
		for(Item i : itemset) {
			TreeSet<Item> subset = itemset.stream().filter(item -> !item.equals(i)).collect(Collectors.toCollection(TreeSet::new));
			Iterator<Item> iterator = itemset.iterator();
			Item first = iterator.next();
			Item second = iterator.next();
			if(subset.stream().anyMatch(item -> item.equals(first)) || first.getMis() == second.getMis())
				if(!F.stream().anyMatch(iset -> iset.getItemset().containsAll(subset)))
					return true;
		}
		return false;
	}
	/**
	 * 
	 * @return The tail of this Itemset, i.e. the subset containing all
	 * the {@link Item}s but the first. Returns an empty Itemset if applied
	 * to a 1-itemset.
	 */
	public Itemset getTail() {
		Itemset tail = new Itemset(null);
		this.itemset.stream().skip(1L).forEachOrdered(item -> tail.addItem(item));
		return tail;
	}
	
	/**
	 * @return A String representation of the Itemset and its parameters.
	 */
	@Override
	public String toString() {
		return itemset.stream().collect(Collectors.toList()).toString() + "\tsupport: " + supportCount + "\ttailCount: " + tailCount + "\n";
	}
	public int getTailCount() {
		return tailCount;
	}
	/**
	 * Increases the tail count by one.
	 */
	public void increaseTailCount() {
		this.tailCount ++;
	}
	/**
	 * Tests whether this Itemset contains two {@link Item}s that can not be together
	 * according to the list of rules provided.
	 * @param cannotBeTogetherItemsets An {@link ArrayList} of Itemsets containing
	 * each a set of Items that can not be together in the same Itemset.
	 * @return {@code true} if the Itemset contains two or more Items that cannot be 
	 * together; {@code false} otherwise.
	 */
	public boolean cannotBeTogether(ArrayList<Itemset> cannotBeTogetherItemsets) {
		return cannotBeTogetherItemsets.stream().anyMatch(rule -> this.contains(rule));
	}
	/**
	 * Tests whether this Itemset contains at least one of the {@link Item}s specified
	 * by a set of rules.
	 * @param mustHaveItems An {@link ArrayList} of Items
	 * @return {@code true} when this Itemset contains at least one of the Items in the 
	 * {@code mustHaveItems} list, {@code false} otherwise.
	 */
	public boolean mustHave(ArrayList<Item> mustHaveItems) {
		return mustHaveItems.stream().anyMatch(item -> itemset.contains(item));
	}

}
