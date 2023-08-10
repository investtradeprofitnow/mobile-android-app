package com.itpn.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itpn.R;
import com.itpn.adapter.SubscriptionAdapter;
import com.itpn.model.Subscription;

import java.util.ArrayList;

public class SubscriptionActivity extends MenuForActivity {
	RecyclerView recyclerView;
	SubscriptionAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscription);
		recyclerView = findViewById(R.id.rv_subscription_list);


		Subscription plan1 = new Subscription(1,"Basic",0,"<b>No Discount</b> on any Strategy","Validity: <b>Lifetime</b>");
		Subscription plan2 = new Subscription(2,"Silver",999,"Flat <b>10%</b> discount on every strategy","Validity: <b>1 year</b>");
		Subscription plan3 = new Subscription(3,"Gold",1999,"Flat <b>25%</b> discount on every strategy","Validity: <b>3 years</b>");
		Subscription plan4 = new Subscription(4,"Platinum",3999,"Flat <b>50%</b> discount on every strategy","Validity: <b>5 years</b>");

		ArrayList<Subscription> planList = new ArrayList<>();
		planList.add(plan1);
		planList.add(plan2);
		planList.add(plan3);
		planList.add(plan4);

		adapter = new SubscriptionAdapter(this,planList);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);
	}
}