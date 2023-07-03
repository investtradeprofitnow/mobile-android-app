package com.itpn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class LoginActivity extends MenuForActivity implements AppConfig {
	EditText email, password;
	Button login;
	TextView register, forgotPassword;
	ProgressBar spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if(!displayLoginStatus()){
			setContentView(R.layout.activity_login);
			email = findViewById(R.id.et_email);
			password = findViewById(R.id.et_password);
			login = findViewById(R.id.btn_login);
			register = findViewById(R.id.btn_register);
			forgotPassword = findViewById(R.id.btn_forgot_pwd);
			spinner = findViewById(R.id.pb_loading);
			spinner.setVisibility(View.GONE);
			login.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String emailVal = email.getText().toString();
					String passwordVal = password.getText().toString();
					boolean error = false;
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
						password.setError("Password should be 8-20 Characters, atl east one Capital and one Small Letter, one numeric and special characters");
						error = true;
					}
					if(!error){
						spinner.setVisibility(View.VISIBLE);
						login.setClickable(false);
						AttemptLogin attemptLogin = new AttemptLogin();
						attemptLogin.execute(emailVal,passwordVal);
					}
				}
			});

			register.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
				}
			});

			forgotPassword.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
					intent.putExtra("Page","Forgot Password");
					startActivity(intent);
				}
			});
		}
		else{
			saveLoginStatus(true);
			Intent intent = new Intent(this,StrategyListActivity.class);
			intent.putExtra("Page","Login");
			startActivity(intent);
		}
	}

	private  class AttemptLogin extends AsyncTask<String,String, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser jsonParser = new JSONParser();
			String email = args[0];
			String password = args[1];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("password",password));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+LOGIN_API, params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try{
				String status = jsonObject.getString("status");
				spinner.setVisibility(View.GONE);
				login.setClickable(true);
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
					Intent intent = new Intent(LoginActivity.this,StrategyListActivity.class);
					intent.putExtra("Page","Login");
					startActivity(intent);
				}
				else{
					String message = jsonObject.getString("message");
					Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}