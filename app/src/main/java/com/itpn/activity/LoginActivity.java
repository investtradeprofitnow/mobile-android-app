package com.itpn.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
	ProgressDialog progressDialog;

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
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Login in Progress");
			progressDialog.setMessage("Please wait while we check the credentials...");
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
						progressDialog.show();
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
			SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
			String customerJson = sharedPreferences.getString("Customer","");
			Gson g = new Gson();
			Customer customer = g.fromJson(customerJson,Customer.class);
			GetCustomerDetails getCustomerDetails = new GetCustomerDetails();
			getCustomerDetails.execute(customer.getCustomerId()+"");
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
				progressDialog.dismiss();
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

	private class GetCustomerDetails extends AsyncTask<String,String,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONParser jsonParser = new JSONParser();
			String customerId = strings[0];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("customerId",customerId));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+GET_CUSTOMER_DETAILS, params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				saveLoginStatus(true);
				JSONObject customerJson = jsonObject.getJSONObject("customer");
				Customer customer = new Customer(customerJson);
				SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				Gson gson = new Gson();
				String jsonCustomer = gson.toJson(customer);
				editor.putString("Customer", jsonCustomer);
				editor.commit();
				Intent intent = new Intent(LoginActivity.this,StrategyListActivity.class);
				intent.putExtra("Page","Login");
				startActivity(intent);
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}