package com.itpn.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itpn.model.Subscription;

import java.util.ArrayList;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>{
	Context context;
	private ArrayList<Subscription> subscriptionList;
	View view;

	public SubscriptionAdapter(Context context, ArrayList<Subscription> subscriptionList) {
		this.context = context;
		this.subscriptionList = subscriptionList;
	}

	@NonNull
	@Override
	public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		view = LayoutInflater.from(parent.getContext()).inflate(com.itpn.R.layout.subscription_list_cardview,parent,false);
		return new SubscriptionViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SubscriptionAdapter.SubscriptionViewHolder holder, int position) {
		Subscription subscription = subscriptionList.get(position);
		holder.name.setText(subscription.getName());
		if(subscription.getPrice()==0) {
			holder.price.setText("Free");
		}
		else{
			holder.price.setText(subscription.getPrice()+"");
		}
		holder.details1.setText(Html.fromHtml(subscription.getDetails1()));
		holder.details2.setText(Html.fromHtml(subscription.getDetails2()));
		holder.buyNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Plan: "+subscription.getName() , Toast.LENGTH_LONG).show();
			}
		});
	}
	@Override
	public int getItemCount() {
		return subscriptionList.size();
	}

	public void onViewAttachedToWindow(@NonNull SubscriptionAdapter.SubscriptionViewHolder holder) {
		view.animate().alpha(1f).setDuration(150);
	}

	public class SubscriptionViewHolder extends RecyclerView.ViewHolder{
		private TextView name, price, details1, details2;
		Button buyNow;

		public SubscriptionViewHolder(@NonNull View itemView) {
			super(itemView);
			this.name =  itemView.findViewById(com.itpn.R.id.tv_name);
			this.price =  itemView.findViewById(com.itpn.R.id.tv_price);
			this.details1 =  itemView.findViewById(com.itpn.R.id.tv_details1);
			this.details2 =  itemView.findViewById(com.itpn.R.id.tv_details2);
			this.buyNow =  itemView.findViewById(com.itpn.R.id.btn_buy_now);

		}
	}
}
