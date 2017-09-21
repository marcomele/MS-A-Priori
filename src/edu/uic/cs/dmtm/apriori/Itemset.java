package edu.uic.cs.dmtm.apriori;

import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Collector;
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
		support = ((double) (supportCount)) / N;
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
	public Itemset join(Itemset other) {
		Itemset joined = new Itemset(this.sdc);
		joined.itemset.addAll(this.itemset);
		joined.itemset.addAll(other.itemset);
		joined.minMIS();
		joined.maxMIS();
		joined.sdc = this.sdc;
		return joined;
	}
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
	public boolean prune(TreeSet<Itemset> F) {
		for(Item i : itemset) {
			TreeSet<Item> subset = itemset.stream().filter(item -> item.equals(i)).collect(Collectors.toCollection(TreeSet::new));
			Iterator<Item> iterator = itemset.iterator();
			Item first = iterator.next();
			Item second = iterator.next();
			if(itemset.stream().anyMatch(item -> item.equals(first)) || first.getMis() == second.getMis())
				if(!F.contains(subset))
					return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return itemset.stream().collect(Collectors.toList()).toString() + ":" + supportCount;
	}

}
