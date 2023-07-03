package com.itpn.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.itpn.R;
import com.itpn.adapter.TestimonialAdapter;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;
import com.itpn.model.Feedback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestimonialActivity extends MenuForActivity implements AppConfig {
	ArrayList<Feedback> testimonial;
	RecyclerView recyclerView;
	TestimonialAdapter adapter;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_testimonial);
		recyclerView = findViewById(R.id.rv_testimonial);
		context=this;
		testimonial =  new ArrayList<>();
		Testimonial testimonial1 = new Testimonial();
		testimonial1.execute();
	}

	private class Testimonial extends AsyncTask<Void,Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... voids) {
			JSONParser jsonParser = new JSONParser();
			JSONArray json = jsonParser.makeHttpRequest(APP_API_URL+TESTIMONIAL);
			return json;
		}

		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			try {
				TextView error = findViewById(R.id.tv_error_testimonial);
				if(jsonArray==null){
					error.setVisibility(View.VISIBLE);
				}
				else{
					error.setVisibility(View.GONE);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject feedbackJson = jsonArray.getJSONObject(i);
						Feedback feedback = new Feedback(feedbackJson);
						testimonial.add(feedback);
						adapter = new TestimonialAdapter(context,testimonial);
						RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
						recyclerView.setLayoutManager(layoutManager);
						recyclerView.setAdapter(adapter);
					}
				}
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}