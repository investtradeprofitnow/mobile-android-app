package com.itpn.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Strategy {
	private int id;
	private String name;
	private String description;
	private String type;
	private String link;
	private String photo;

	public Strategy(int id, String name, String description, String photo, String link, String type) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.photo = photo;
		this.link = link;
		this.type = type;
	}

	public Strategy(JSONObject strategyJson){
		try{
			this.id = strategyJson.getInt("strategy_short_id");
			this.name = strategyJson.getString("name");
			String desc = strategyJson.getString("description");
			desc = desc.replace("<br/>","");
			this.description = desc;
			this.photo = strategyJson.getString("photo");
			this.link = strategyJson.getString("link");
			this.type = strategyJson.getString("type");
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		}
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}


}
