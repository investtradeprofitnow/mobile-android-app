package com.itpn.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;
import com.itpn.model.Customer;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends MenuForActivity implements AppConfig {
	ImageView profileImg;
	EditText name, email;
	Button btnUpdate;
	ProgressBar spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		profileImg = findViewById(R.id.iv_profile_img);
		name = findViewById(R.id.et_name);
		email = findViewById(R.id.et_email);
		btnUpdate = findViewById(R.id.btn_update_profile);
		spinner = findViewById(R.id.pb_loading);
		spinner.setVisibility(View.GONE);
		SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
		String customerJson = sharedPreferences.getString("Customer","");
		Gson g = new Gson();
		Customer customer = g.fromJson(customerJson,Customer.class);
		String nameVal = customer.getName();
		String photo = customer.getPhoto();
		name.setText(nameVal);
		email.setText(customer.getEmail());
		if(photo == null || photo.equals("null")){
			photo = nameVal.toUpperCase().charAt(0)+".png";
		}
		Glide.with(this).load(PROFILE_IMAGE_URL+photo).into(profileImg);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String updateName = name.getText().toString();
				String updateEmail = email.getText().toString();
				boolean error = false;
				if(updateName.equals("")){
					name.setError("Please enter your name");
					error = true;
				}
				if(updateEmail.equals("")){
					email.setError("Please enter your email id");
					error = true;
				}
				else if(!updateEmail.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")){
					email.setError("Please enter a valid email id");
					error = true;
				}
				if(!error){
					int customerId = customer.getCustomerId();
					String currentEmail = customer.getEmail();
					spinner.setVisibility(View.VISIBLE);
					btnUpdate.setClickable(false);
					UpdateProfile updateProfile = new UpdateProfile();
					updateProfile.execute(updateName, updateEmail, currentEmail, customerId+"");
				}
			}
		});
	}

	private  class UpdateProfile extends AsyncTask<String,String, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONParser jsonParser = new JSONParser();
			String name = strings[0];
			String email = strings[1];
			String currEmail = strings[2];
			String customerId = strings[3];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("name",name));
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("currentEmail",currEmail));
			params.add(new BasicNameValuePair("customerId",customerId));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+UPDATE_PROFILE,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				String message = jsonObject.getString("message");
				spinner.setVisibility(View.GONE);
				btnUpdate.setClickable(true);
				if(status.equals("success")){
					String nameVal = jsonObject.getString("name");
					String emailVal = jsonObject.getString("email");
					SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
					String customerJson = sharedPreferences.getString("Customer","");
					Gson g = new Gson();
					Customer customer = g.fromJson(customerJson,Customer.class);
					customer.setName(nameVal);
					customer.setEmail(emailVal);
					String jsonCustomer = g.toJson(customer);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("Customer", jsonCustomer);
					editor.commit();
					name.setText(nameVal);
					email.setText(emailVal);
				}
				Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}