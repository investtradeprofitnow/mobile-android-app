package com.itpn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;
import com.itpn.model.Customer;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayOtpActivity extends MenuForActivity implements AppConfig {

	PinView pinview;
	private TextView resendOtp, btnBack;
	private final int resendTime = 80;
	private String name, email,password;
	JSONParser jsonParser;
	ProgressBar spinner;
	String pageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_otp);
		pinview = findViewById(R.id.pv_otp);
		resendOtp = findViewById(R.id.btn_resend_otp);
		spinner = findViewById(R.id.pb_loading);
		btnBack = findViewById(R.id.btn_back);
		jsonParser = new JSONParser();
		pinview.requestFocus();
		InputMethodManager inputMethodManager = 	(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS);
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		name = intent.getStringExtra("name");
		password  = intent.getStringExtra("password");
		pageName = intent.getStringExtra("Page");
		String backPage = "Register".equals(pageName) ? "Back to Register" : "Back to Forgot Password";
		btnBack.setText(backPage);

		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if("Register".equals(pageName)){
					startActivity(new Intent(DisplayOtpActivity.this, RegisterActivity.class));
				}
				else if ("Forgot Password".equals(pageName)) {
					startActivity(new Intent(DisplayOtpActivity.this, ForgotPasswordActivity.class));
				}
			}
		});

		pinview.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().length()==6){
					String otp = s.toString();
					spinner.setVisibility(View.VISIBLE);
					VerifyOtp verifyOtp = new VerifyOtp();
					verifyOtp.execute(email,otp);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		startCountdownTimer();

		resendOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				spinner.setVisibility(View.VISIBLE);
				ResendOtp resend = new ResendOtp();
				resend.execute(email);
			}
		});
	}

	public void startCountdownTimer(){
		resendOtp.setTextColor(Color.parseColor("#99000000"));
		new CountDownTimer(resendTime * 1000, 1000){
			@Override
			public void onTick(long millisUntilFinished) {
				String resendText = "Resend Code in "+(millisUntilFinished/1000)+" seconds";
				resendOtp.setText(resendText);
				resendOtp.setTextSize(20);
			}

			@Override
			public void onFinish() {
				resendOtp.setText("Resend Code");
				resendOtp.setTextColor(getApplicationContext().getResources().getColor(R.color.blue));
			}
		}.start();
	}

	private class VerifyOtp extends AsyncTask<String, String, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... strings) {
			String email = strings[0];
			String otp = strings[1];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("otp",otp));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+VERIFY_OTP_API,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				spinner.setVisibility(View.GONE);
				if(status.equals("success")){
					if("Register".equals(pageName)){
						spinner.setVisibility(View.VISIBLE);
						RegisterUser registerUser = new RegisterUser();
						registerUser.execute(name,email,password);
					}
					else if ("Forgot Password".equals(pageName)) {
						Intent intent = new Intent(DisplayOtpActivity.this,ChangePasswordActivity.class);
						intent.putExtra("email",email);
						intent.putExtra("Page","Forgot Password");
						startActivity(intent);
					}
				}
				else{
					String message = jsonObject.getString("message");
					resendOtp.setText(message);
					resendOtp.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
					resendOtp.setTextSize(15);
				}
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class RegisterUser extends AsyncTask<String,String, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... strings) {
			String name = strings[0];
			String email = strings[1];
			String password = strings[2];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("name",name));
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("password",password));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+REGISTER_API,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				if(jsonObject!=null){
					String status = jsonObject.getString("status");
					spinner.setVisibility(View.GONE);
					if(status.equals("success")){
						JSONObject customerJson = jsonObject.getJSONObject("customer");
						Customer customer = new Customer(customerJson);
						SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						Gson gson = new Gson();
						String jsonCustomer = gson.toJson(customer);
						editor.putString("Customer", jsonCustomer);
						editor.commit();
						saveLoginStatus(true);
						Intent intent = new Intent(DisplayOtpActivity.this,StrategyListActivity.class);
						intent.putExtra("Page","Register");
						startActivity(intent);
					}
					else{
						String message = jsonObject.getString("message");
						Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
						setContentView(R.layout.activity_register);
					}
				}
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class ResendOtp extends AsyncTask<String,String,JSONObject>{
		@Override
		protected JSONObject doInBackground(String... strings) {
			String email = strings[0];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+SEND_OTP_API,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				spinner.setVisibility(View.GONE);
				if(status.equals("success")){
					resendOtp.setText("OTP has been resent to the email id");
					resendOtp.setTextSize(15);
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