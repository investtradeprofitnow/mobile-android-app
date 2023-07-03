package com.itpn.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Customer {
	private int customerId;
	private String name;
	private String email;
	private String password;
	private String photo;

	public Customer(JSONObject customerJson){
		try {
			this.customerId = customerJson.getInt("customer_id");
			this.name = customerJson.getString("name");
			this.email = customerJson.getString("email");
			this.photo = customerJson.getString("photo");
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public int getCustomerId() {
		return customerId;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}