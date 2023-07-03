package com.itpn.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;
import com.itpn.model.Customer;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedbackActivity extends MenuForActivity implements AppConfig {
	RatingBar ratingBar;
	EditText feedback;
	TextView characterLength;
	Button saveFeedback;
	CheckBox anonymousCheck;
	ProgressBar spinner;
	JSONParser jsonParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		ratingBar = findViewById(R.id.rb_stars);
		feedback = findViewById(R.id.et_feedback);
		saveFeedback = findViewById(R.id.btn_save_feedback);
		anonymousCheck = findViewById(R.id.cb_anonymous);
		characterLength = findViewById(R.id.tv_character);
		spinner = findViewById(R.id.pb_loading);
		spinner.setVisibility(View.GONE);
		jsonParser = new JSONParser();

		feedback.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int length = s.length();
				String text = length+"/5000";
				characterLength.setText(text);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		saveFeedback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String rating =String.valueOf((int)ratingBar.getRating());
				String feedbackVal = feedback.getText().toString();
				String anonymous=anonymousCheck.isChecked() ? "yes" : "no";
				boolean error=false;
				if("".equals(feedbackVal)){
					feedback.setError("Please enter the feedback");
					error=true;
				}
				if(feedbackVal.length()>5000){
					feedback.setError("Please enter feedback less than 5000 characters");
					error = true;
				}
				if(!error){
					SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
					String customerJson = sharedPreferences.getString("Customer","");
					Gson g = new Gson();
					Customer customer = g.fromJson(customerJson,Customer.class);
					int customerId = customer.getCustomerId();
					spinner.setVisibility(View.VISIBLE);
					saveFeedback.setClickable(false);
					SaveFeedback saveFeedback1 = new SaveFeedback();
					saveFeedback1.execute(customerId+"", rating, feedbackVal,anonymous);
				}
			}
		});
	}

	private class SaveFeedback  extends AsyncTask<String, String, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... strings) {
			String customerId= strings[0];
			String rating = strings[1];
			String feedback = strings[2];
			String anonymous = strings[3];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("customer_id",customerId));
			params.add(new BasicNameValuePair("rating",rating));
			params.add(new BasicNameValuePair("feedback",feedback));
			params.add(new BasicNameValuePair("anonymous",anonymous));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+SAVE_FEEDBACK ,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String message = jsonObject.getString("message");
				Toast.makeText(FeedbackActivity.this, message, Toast.LENGTH_LONG).show();
				spinner.setVisibility(View.GONE);
				saveFeedback.setClickable(true);
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}