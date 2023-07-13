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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.List;

public class StrategyListActivity extends MenuForActivity implements AppConfig {
	ArrayList<Strategy> strategyList;
	ArrayList<Strategy> intradayList;
	ArrayList<Strategy> btstList;
	ArrayList<Strategy> positionalList;
	ArrayList<Strategy> investmentList;
	RecyclerView recyclerView;
	Spinner filter;
	TextView emptyStrategyList;
	StrategyShortAdapter adapter;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strategy_list);
		recyclerView = findViewById(R.id.rv_strategy_list);
		filter = findViewById(R.id.sp_filter);
		emptyStrategyList = findViewById(R.id.tv_empty_list);
		List<String> categories = new ArrayList<String>();
		categories.add("All");
		categories.add("Intraday");
		categories.add("BTST");
		categories.add("Positional");
		categories.add("Investment");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,categories);
		filter.setAdapter(dataAdapter);
		context = this;
		strategyList = new ArrayList<>();
		intradayList = new ArrayList<>();
		btstList = new ArrayList<>();
		positionalList = new ArrayList<>();
		investmentList = new ArrayList<>();
		Intent intent = getIntent();
		String pageName = intent.getStringExtra("Page");
		if("".equals(pageName)){
			SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
			Gson gson = new Gson();
			String jsonStrategy = sharedPreferences.getString("strategies",null);
			String jsonIntradayStrategy =  sharedPreferences.getString("intradayStrategies",null);
			String jsonBTSTStrategy =  sharedPreferences.getString("btstStrategies",null);
			String jsonPositionalStrategy =  sharedPreferences.getString("positionalStrategies",null);
			String jsonInvestmentStrategy =  sharedPreferences.getString("investmentStrategies",null);
			Type type = new TypeToken<ArrayList<Strategy>>() {}.getType();
			strategyList = gson.fromJson(jsonStrategy,type);
			intradayList = gson.fromJson(jsonIntradayStrategy,type);
			btstList = gson.fromJson(jsonBTSTStrategy,type);
			positionalList = gson.fromJson(jsonPositionalStrategy,type);
			investmentList = gson.fromJson(jsonInvestmentStrategy,type);

			adapter = new StrategyShortAdapter(context,strategyList);
			RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setAdapter(adapter);
		}
		else {
			GetStrategyList getStrategyList = new GetStrategyList();
			getStrategyList.execute();
		}
		filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = parent.getItemAtPosition(position).toString();
				boolean isEmptyList = false;
				emptyStrategyList.setVisibility(View.GONE);
				switch (item){
					case "Intraday":
						if(intradayList.size()==0){
							isEmptyList=true;
						}
						adapter = new StrategyShortAdapter(context,intradayList);
						break;
					case "BTST":
						if(btstList.size()==0){
							isEmptyList=true;
						}
						adapter = new StrategyShortAdapter(context,btstList);
						break;
					case "Positional":
						if(positionalList.size()==0){
							isEmptyList=true;
						}
						adapter = new StrategyShortAdapter(context,positionalList);
						break;
					case "Investment":
						if(investmentList.size()==0){
							isEmptyList=true;
						}
						adapter = new StrategyShortAdapter(context,investmentList);
						break;
					case "All":
						adapter = new StrategyShortAdapter(context,strategyList);
						break;
				}

				if(isEmptyList){
					emptyStrategyList.setVisibility(View.VISIBLE);
				}
				RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
				recyclerView.setLayoutManager(layoutManager);
				recyclerView.setAdapter(adapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
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
					switch (strategy.getType()){
						case "Intraday":
							intradayList.add(strategy);
							break;
						case "BTST":
							btstList.add(strategy);
							break;
						case "Positional":
							positionalList.add(strategy);
							break;
						case "Investment":
							investmentList.add(strategy);
							break;
					}
				}
				SharedPreferences sharedPreferences = getSharedPreferences("ITPN",MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				Gson gson = new Gson();
				String jsonStrategy = gson.toJson(strategyList);
				String jsonIntradayStrategy = gson.toJson(intradayList);
				String jsonBTSTStrategy = gson.toJson(btstList);
				String jsonPositionalStrategy = gson.toJson(positionalList);
				String jsonInvestmentStrategy = gson.toJson(investmentList);
				editor.putString("strategies", jsonStrategy);
				editor.putString("intradayStrategies", jsonIntradayStrategy);
				editor.putString("btstStrategies", jsonBTSTStrategy);
				editor.putString("positionalStrategies", jsonPositionalStrategy);
				editor.putString("investmentStrategies", jsonInvestmentStrategy);
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