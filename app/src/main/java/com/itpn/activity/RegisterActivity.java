package com.itpn.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends MenuForActivity implements AppConfig {

	EditText name=null, email=null, password=null;
	Button sendOtp=null;
	TextView login=null;
	ProgressBar spinner;
	JSONParser jsonParser;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		name = findViewById(R.id.et_name);
		email = findViewById(R.id.et_email);
		password = findViewById(R.id.et_password);
		sendOtp = findViewById(R.id.btn_send_otp);
		login = findViewById(R.id.btn_login);
		spinner = findViewById(R.id.pb_loading);
		jsonParser = new JSONParser();
		spinner.setVisibility(View.GONE);
		sendOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String nameVal = name.getText().toString();
				String emailVal = email.getText().toString();
				String passwordVal = password.getText().toString();
				boolean error = false;
				if(nameVal.equals("")){
					name.setError("Please enter your name");
					error = true;
				}
				if(emailVal.equals("")){
					email.setError("Please enter your email id");
					error = true;
				}
				else if(!emailVal.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")){
					email.setError("Please enter a valid email id");
					error = true;
				}
				if(passwordVal.equals("")){
					password.setError("Please enter your password");
					error = true;
				}
				else if(!passwordVal.matches("^(?=.*\\d)(?=.*[@#\\-_$%^&+=§!\\?])(?=.*[a-z])(?=.*[A-Z])[\\dA-Za-z@#\\-_$%^&+=§!\\?]{8,20}$")){
					password.setError("Password should be 8-20 Characters, at least one Capital and one Small Letter, one numeric and special characters");
					error = true;
				}

				if(!error){
					intent = new Intent(RegisterActivity.this, DisplayOtpActivity.class);
					intent.putExtra("name",nameVal);
					intent.putExtra("email",emailVal);
					intent.putExtra("password",passwordVal);
					intent.putExtra("Page","Register");
					spinner.setVisibility(View.VISIBLE);
					sendOtp.setClickable(false);
					CheckCustomer checkCustomer = new CheckCustomer();
					checkCustomer.execute(emailVal);
				}
			}
		});

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
			}
		});
	}

	private class  CheckCustomer extends AsyncTask<String, String, JSONObject>{

		@Override
		protected JSONObject doInBackground(String... strings) {
			String email = strings[0];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+CHECK_CUSTOMER,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				if("not exists".equals(status)){
					String nameVal = name.getText().toString();
					String emailVal = email.getText().toString();
					SendOtpToUser sendOtpToUser = new SendOtpToUser();
					sendOtpToUser.execute(emailVal,nameVal);
				}
				else{
					spinner.setVisibility(View.GONE);
					sendOtp.setClickable(true);
					String message = jsonObject.getString("message");
					Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class SendOtpToUser extends AsyncTask<String,String, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected JSONObject doInBackground(String... strings) {
			String email = strings[0];
			String name = strings[1];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("name",name));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+SEND_OTP_API,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				spinner.setVisibility(View.GONE);
				sendOtp.setClickable(true);
				if(status.equals("success")){
					startActivity(intent);
				}
				else{
					String message = jsonObject.getString("message");
					Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}