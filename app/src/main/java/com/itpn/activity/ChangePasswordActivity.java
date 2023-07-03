package com.itpn.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePasswordActivity extends MenuForActivity implements AppConfig {
	EditText password, confirmPassword;
	Button btnUpdatePassword;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		password = findViewById(R.id.et_change_pwd);
		confirmPassword = findViewById(R.id.et_confirm_pwd);
		btnUpdatePassword = findViewById(R.id.btn_update_password);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Updating Password");
		progressDialog.setMessage("Please wait while we update your password");
		btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String passwordVal = password.getText().toString();
				String confirmPasswordVal = confirmPassword.getText().toString();
				boolean error=false;
				if(passwordVal.equals("")){
					password.setError("Please enter your password");
					error = true;
				}
				else if(!passwordVal.matches("^(?=.*\\d)(?=.*[@#\\-_$%^&+=§!\\?])(?=.*[a-z])(?=.*[A-Z])[\\dA-Za-z@#\\-_$%^&+=§!\\?]{8,20}$")){
					password.setError("Password should be 8-20 Characters, at least one Capital and one Small Letter, one numeric and special characters");
					error = true;
				}
				if(confirmPasswordVal.equals("")){
					confirmPassword.setError("Please enter confirm password");
					error = true;
				}
				else if(!passwordVal.equals(confirmPasswordVal)){
					password.setError("Password and Confirm Password values should be same");
					error = true;
				}
				if(!error){
					Intent intent = getIntent();
					String pageName = intent.getStringExtra("Page");
					String email="";
					if(pageName.equals("Forgot Password")){
						email = intent.getStringExtra("email");
					}
					else if (pageName.equals("Change Password")) {
						SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
						String customerJson = sharedPreferences.getString("Customer","");
						Gson g = new Gson();
						Customer customer = g.fromJson(customerJson,Customer.class);
						email = customer.getEmail();
					}
					progressDialog.show();
					btnUpdatePassword.setClickable(false);
					UpdatePassword updatePassword = new UpdatePassword();
					updatePassword.execute(email,passwordVal);
				}
			}
		});
	}

	private class UpdatePassword extends AsyncTask<String,String, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONParser jsonParser = new JSONParser();
			String email = strings[0];
			String password = strings[1];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("password",password));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+UPDATE_PASSWORD,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				progressDialog.dismiss();
				btnUpdatePassword.setClickable(true);
				if(status.equals("success")){
					SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
					String customerJson = sharedPreferences.getString("Customer","");
					Gson g = new Gson();
					Customer customer = g.fromJson(customerJson,Customer.class);
					customer.setPassword(jsonObject.getString("hash"));
					String jsonCustomer = g.toJson(customer);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("Customer", jsonCustomer);
					editor.commit();
				}
				Toast.makeText(ChangePasswordActivity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}