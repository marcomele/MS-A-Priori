package edu.uic.cs.dmtm.apriori;

/**
 * A representation of an Item. The item is represented
 * as a String and carries information about its minimum
 * support, actual support and support count.
 * Implements the {@code Comparable} interface to provide
 * an ordering capability to the items; items order is
 * lexicographical on the id.
 * 
 * @author Marco Mele
 * @author Massimo Piras
 */

public class Item implements Comparable<Item> {
	
	private String id;
	private Double mis;
	private Double support;
	private Integer supportCount;
	
	public Double getSupport() {
		return support;
	}

	public void setSupport(Double support) {
		this.support = support;
	}

	public Integer getSupportCount() {
		return supportCount;
	}
	
	/**
	 * Increases by one the support count of the item.
	 */
	public void increaseSupportCount() {
		this.supportCount ++;
	}
	
	/**
	 * Constructs a new {@code Item} object consisting of
	 * the id String and its minimum support. Initial values
	 * for support and support count are set to 0.
	 * @param id a {@code String} identifying the item.
	 * @param mis the minimum support of the item.
	 */
	public Item(String id, Double mis) {
		this.id = id;
		this.mis = mis;
		this.support = 0.0;
		this.supportCount = 0;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getMis() {
		return mis;
	}
	public void setMis(Double mis) {
		this.mis = mis;
	}

	/**
	 * Compares an {@link Item} to another measured by their
	 * lexicographic ordering based on their {@code id}s.
	 * @param o another {@link Item}
	 * @return the measured lexicographic difference between
	 * the two {@code Id}s.
	 */
	@Override
	public int compareTo(Item o) {
		return this.id.compareTo(o.id);
	}
	
	@Override
	public String toString() {
		return this.id.toString();
	}

}
