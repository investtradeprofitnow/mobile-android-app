package com.itpn.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;
import com.itpn.model.Customer;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends MenuForActivity implements AppConfig {
	ImageView profileImg;
	EditText name, email;
	Button btnUpdate;
	ProgressDialog progressDialog;
	private final int GALLERY_REQUEST_CODE = 1000;
	boolean isImageUpdated = false;
	Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		profileImg = findViewById(R.id.iv_profile_img);
		name = findViewById(R.id.et_name);
		email = findViewById(R.id.et_email);
		btnUpdate = findViewById(R.id.btn_update_profile);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Updating Profile");
		progressDialog.setMessage("Please wait while we save your profile details");
		profileImg.setClickable(true);
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
		photo = photo.replace(" ","%20");
		Picasso.get().load(PROFILE_IMAGE_URL+photo).memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE).into(profileImg);

		//Glide.with(this).load(PROFILE_IMAGE_URL+photo).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true). into(profileImg);

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
					progressDialog.show();
					String base64Image = "No";
					if(isImageUpdated){
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
						byte[] bytes = byteArrayOutputStream.toByteArray();
						base64Image = Base64.encodeToString(bytes,Base64.DEFAULT);
					}
					UpdateProfile updateProfile = new UpdateProfile();
					updateProfile.execute(updateName, updateEmail, currentEmail, customerId+"",base64Image);
				}
			}
		});

		profileImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
				String photo = customer.getPhoto();
				if(photo == null || photo.equals("null")){
					String options[] = {"Upload Photo"};
					builder.setItems(options, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent,GALLERY_REQUEST_CODE);
						}
					});
				}
				else{
					String options[] = {"Change Photo", "Remove Photo"};
					builder.setItems(options, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which==0){
								Intent intent = new Intent(Intent.ACTION_PICK);
								intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent,GALLERY_REQUEST_CODE);
							}
							else if(which==1){
								AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
								builder1.setMessage("Do you want to remove your profile photo?");
								builder1.setCancelable(false);
								builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog1, which1) -> {
									DeletePhoto deletePhoto = new DeletePhoto();
									deletePhoto.execute(customer.getPhoto(), customer.getCustomerId()+"");
								});

								builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog1, which1) -> {
									dialog1.cancel();
								});

								AlertDialog dialog1 = builder1.create();
								dialog1.show();
							}
						}
					});
				}
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == GALLERY_REQUEST_CODE){
				try {
					Uri uri = data.getData();
					isImageUpdated = true;
					bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					profileImg.setImageURI(data.getData());
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private  class UpdateProfile extends AsyncTask<String,String, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONParser jsonParser = new JSONParser();
			String name = strings[0];
			String email = strings[1];
			String currEmail = strings[2];
			String customerId = strings[3];
			String photo = strings[4];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("name",name));
			params.add(new BasicNameValuePair("email",email));
			params.add(new BasicNameValuePair("currentEmail",currEmail));
			params.add(new BasicNameValuePair("customerId",customerId));
			params.add(new BasicNameValuePair("photo",photo));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+UPDATE_PROFILE,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				String message = jsonObject.getString("message");
				progressDialog.dismiss();
				if(status.equals("success")){
					String nameVal = jsonObject.getString("name");
					String emailVal = jsonObject.getString("email");
					String photoVal = jsonObject.getString("photo");
					SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
					String customerJson = sharedPreferences.getString("Customer","");
					Gson g = new Gson();
					Customer customer = g.fromJson(customerJson,Customer.class);
					customer.setName(nameVal);
					customer.setEmail(emailVal);
					customer.setPhoto(photoVal);
					String jsonCustomer = g.toJson(customer);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("Customer", jsonCustomer);
					editor.commit();
					name.setText(nameVal);
					email.setText(emailVal);
					startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
				}
				Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class DeletePhoto extends AsyncTask<String, String,JSONObject>{
		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONParser jsonParser = new JSONParser();
			String photo = strings[0];
			String customerId = strings[1];
			ArrayList<BasicNameValuePair> params  = new ArrayList<>();
			params.add(new BasicNameValuePair("photo",photo));
			params.add(new BasicNameValuePair("customerId",customerId));
			JSONObject json = jsonParser.makeHttpRequest(APP_API_URL+DELETE_PHOTO,params);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			try {
				String status = jsonObject.getString("status");
				String message = jsonObject.getString("message");
				progressDialog.dismiss();
				if(status.equals("success")) {
					SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
					String customerJson = sharedPreferences.getString("Customer","");
					Gson g = new Gson();
					Customer customer = g.fromJson(customerJson,Customer.class);
					customer.setPhoto(null);
					String jsonCustomer = g.toJson(customer);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("Customer", jsonCustomer);
					editor.commit();
					startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
				}
				Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}