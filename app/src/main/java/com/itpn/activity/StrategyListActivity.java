package com.itpn.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itpn.R;
import com.itpn.adapter.StrategyShortAdapter;
import com.itpn.config.AppConfig;
import com.itpn.json.JSONParser;
import com.itpn.model.Strategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StrategyListActivity extends MenuForActivity implements AppConfig {
	ArrayList<Strategy> strategyList;
	RecyclerView recyclerView;
	StrategyShortAdapter adapter;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strategy_list);
		recyclerView = findViewById(R.id.rv_strategy_list);
		context = this;
		strategyList = new ArrayList<>();
		Intent intent = getIntent();
		String pageName = intent.getStringExtra("Page");
		if("".equals(pageName)){
			SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
			Gson gson = new Gson();
			String jsonStrategy = sharedPreferences.getString("strategies",null);
			Type type = new TypeToken<ArrayList<Strategy>>() {}.getType();
			strategyList = gson.fromJson(jsonStrategy,type);
			adapter = new StrategyShortAdapter(context,strategyList);
			RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setAdapter(adapter);
		}
		else{
			GetStrategyList getStrategyList = new GetStrategyList();
			getStrategyList.execute();
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(StrategyListActivity.this);
		builder.setMessage("Do you wish to exit the application?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
			StrategyListActivity.this.finishAffinity();
			System.exit(0);
		});

		builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
			dialog.cancel();
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private  class GetStrategyList extends AsyncTask<Void,Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... voids) {
			JSONParser jsonParser = new JSONParser();
			JSONArray json = jsonParser.makeHttpRequest(APP_API_URL+STRATEGY_LIST_API);
			return json;
		}

		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			try{
				for(int i=0;i<jsonArray.length();i++){
					JSONObject strategyJson = jsonArray.getJSONObject(i);
					Strategy strategy = new Strategy(strategyJson);
					strategyList.add(strategy);
				}
				SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				Gson gson = new Gson();
				String jsonStrategy = gson.toJson(strategyList);
				editor.putString("strategies", jsonStrategy);
				editor.commit();
				adapter = new StrategyShortAdapter(context,strategyList);
				RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
				recyclerView.setLayoutManager(layoutManager);
				recyclerView.setAdapter(adapter);
			}
			catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
}