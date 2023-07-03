package com.itpn.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Feedback {
	private int rating;
	private String name;
	private String photo;
	private String feedback;
	private String anonymous;

	public Feedback(JSONObject feedbackJson){
		try {
			this.name = feedbackJson.getString("name");
			this.feedback = feedbackJson.getString("feedback");
			this.photo = feedbackJson.getString("photo");
			this.rating = feedbackJson.getInt("rating");
			this.anonymous = feedbackJson.getString("anonymous");
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public int getRating() {
		return rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto() {
		return photo;
	}

	public String getFeedback() {
		return feedback;
	}

	public String getAnonymous() {
		return anonymous;
	}
}
