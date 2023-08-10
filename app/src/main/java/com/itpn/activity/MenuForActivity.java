package com.itpn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.itpn.R;
import com.itpn.model.Customer;

public class MenuForActivity extends AppCompatActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_links, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.mi_logged_menu);
		boolean loggedIn = displayLoginStatus();
		MenuItem login = menu.findItem(R.id.mi_login);
		MenuItem logout = menu.findItem(R.id.mi_logout);
		MenuItem strategyList = menu.findItem(R.id.mi_strategy_list);
		if(loggedIn){
			menuItem.setVisible(true);
			SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
			Gson gson = new Gson();
			String json = sharedPreferences.getString("Customer", "");
			Customer customer = gson.fromJson(json, Customer.class);
			menuItem.setTitle(customer.getName());
			logout.setVisible(true);
			strategyList.setVisible(true);
			login.setVisible(false);
		}
		else{
			menuItem.setVisible(false);
			logout.setVisible(false);
			strategyList.setVisible(false);
			login.setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item) {
		Intent intent;
		switch(item.getItemId()){
			case R.id.mi_about_us:
				intent = new Intent(this,AboutUsActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_testimonial:
				intent = new Intent(this,TestimonialActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_subscriptions:
				intent = new Intent(this,SubscriptionActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_login:
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_logout:
				saveLoginStatus(false);
				intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_strategy_list:
				intent = new Intent(this,StrategyListActivity.class);
				intent.putExtra("Page","");
				startActivity(intent);
				return true;
			case R.id.mi_my_profile:
				intent = new Intent(this,ProfileActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_feedback:
				intent = new Intent(this,FeedbackActivity.class);
				startActivity(intent);
				return true;
			case R.id.mi_change_password:
				intent = new Intent(this,ChangePasswordActivity.class);
				intent.putExtra("Page","Change Password");
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void saveLoginStatus(boolean status){
		SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("loggedInStatus",status);
		editor.commit();
	}

	protected  boolean displayLoginStatus(){
		SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
		boolean status = sharedPreferences.getBoolean("loggedInStatus",false);
		return status;
	}
}
