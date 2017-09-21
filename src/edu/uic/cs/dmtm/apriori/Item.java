package edu.uic.cs.dmtm.apriori;

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

	public void increaseSupportCount() {
		this.supportCount ++;
	}

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

	@Override
	public int compareTo(Item o) {
		return this.id.compareTo(o.id);
	}
	
	@Override
	public String toString() {
		return this.id.toString();
	}

}
