package edu.uic.cs.dmtm.apriori;

public class Item implements Comparable<Item> {
	
	private Integer id;
	private Double mis;
	
	public Item(Integer id, Double mis) {
		this.id = id;
		this.mis = mis;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
		return this.id - o.id;
	}
	
	@Override
	public String toString() {
		return this.id.toString();
	}

}
