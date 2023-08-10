package com.itpn.model;

public class Subscription {
	private  int id;
	private String name;
	private int price;
	private String details1;
	private String details2;

	public Subscription(int id, String name, int price, String details1, String details2) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.details1 = details1;
		this.details2 = details2;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDetails1() {
		return details1;
	}

	public void setDetails1(String details1) {
		this.details1 = details1;
	}

	public String getDetails2() {
		return details2;
	}

	public void setDetails2(String details2) {
		this.details2 = details2;
	}
}
