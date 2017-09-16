package edu.uic.cs.dmtm.apriori;

import java.util.TreeSet;
import java.util.stream.Collectors;

import org.ietf.jgss.Oid;

public class Itemset {
	
	private TreeSet<Item> itemset = new TreeSet<>(new ItemComparator());
	private Double sdc;
	private Double support;
	private Double minMIS;
	private Double maxMIS;
	private int supportCount;
	
	public int getSupportCount() {
		return supportCount;
	}
	public void increaseSupportCount() {
		this.supportCount ++;
	}
	private void minMIS() {
		this.minMIS = itemset.stream().mapToDouble(i -> i.getMis()).min().getAsDouble();
	}
	public Double getMinMIS() {
		return minMIS;
	}
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
	public Itemset(Double sdc) {
		this.sdc = sdc;
		supportCount = 0;
	}
	
	public TreeSet<Item> getItemset() {
		return itemset;
	}
	public void setItemset(TreeSet<Item> itemset) {
		this.itemset = itemset;
	}
	public void addItem(Item item) {
		this.itemset.add(item);
		minMIS();
		maxMIS();
	}
	public void computeSupport(int N) {
		support = (double) (supportCount / N);
	}
	public boolean contains(Itemset other) {
		for(Item i : other.getItemset())
			if(! this.getItemset().contains(i))
				return false;
		return true;
	}
	public Item getItem(Integer id) {
		return this.itemset.stream().filter(i -> i.getId() == id).findAny().orElse(null);
	}
	public Double getSdc() {
		return sdc;
	}
	public void setSdc(Double sdc) {
		this.sdc = sdc;
	}
	
	@Override
	public String toString() {
		return itemset.stream().collect(Collectors.toList()).toString();
	}

}
