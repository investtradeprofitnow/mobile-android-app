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

public class ForgotPasswordActivity extends MenuForActivity implements AppConfig {
	TextView btnBack;
	EditText email;
	Button sendOtp;
	ProgressBar spinner;
	JSONParser jsonParser = null;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		email = findViewById(R.id.et_forgot_email);
		sendOtp = findViewById(R.id.btn_send_otp_forgot);
		spinner = findViewById(R.id.pb_loading);
		spinner.setVisibility(View.GONE);
		btnBack = findViewById(R.id.btn_back);
		jsonParser = new JSONParser();
		sendOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				spinner.setVisibility(View.VISIBLE);
				sendOtp.setClickable(false);
				CheckCustomer checkCustomer = new CheckCustomer();
				checkCustomer.execute(email.getText().toString());
			}
		});

		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
			}
		});
	}

	private class  CheckCustomer extends AsyncTask<String, String, JSONObject> {

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
				if("exists".equals(status)){
					String emailVal = email.getText().toString();
					SendOtpToUser sendOtp = new SendOtpToUser();
					sendOtp.execute(emailVal);
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
		protected JSONObject doInBackground(String... strings) {
			String email = strings[0];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+SEND_OTP_FORGOT_API,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				spinner.setVisibility(View.GONE);
				sendOtp.setClickable(true);
				if(status.equals("success")){
					String emailVal = email.getText().toString();
					intent = new Intent(ForgotPasswordActivity.this, DisplayOtpActivity.class);
					intent.putExtra("email",emailVal);
					intent.putExtra("Page", "Forgot Password");
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