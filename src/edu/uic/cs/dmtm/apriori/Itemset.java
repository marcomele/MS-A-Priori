package edu.uic.cs.dmtm.apriori;

import java.util.TreeSet;
import java.util.stream.Collectors;

public class Itemset {
	
	private TreeSet<Item> itemset = new TreeSet<>((a, b) -> a.getMis().compareTo(b.getMis()));
	private Double sdc;
	
	public Itemset(Double sdc) {
		this.sdc = sdc;
	}
	
	public TreeSet<Item> getItemset() {
		return itemset;
	}
	public void setItemset(TreeSet<Item> itemset) {
		this.itemset = itemset;
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
