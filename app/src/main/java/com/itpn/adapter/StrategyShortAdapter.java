package com.itpn.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.model.Strategy;

import java.util.ArrayList;

public class StrategyShortAdapter extends RecyclerView.Adapter<StrategyShortAdapter.StrategyViewHolder> implements AppConfig {
	Context context;
	private ArrayList<Strategy> strategyList;
	View view;

	public StrategyShortAdapter(Context context, ArrayList<Strategy> strategyList) {
		this.context = context;
		this.strategyList = strategyList;
	}

	@NonNull
	@Override
	public StrategyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		view = LayoutInflater.from(parent.getContext()).inflate(R.layout.strategy_list_cardview,parent,false);
		return new StrategyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull StrategyViewHolder holder, int position) {
		Strategy strategy = strategyList.get(position);
		holder.name.setText(strategy.getName());
		holder.description.setText(strategy.getDescription());
		holder.description.setMovementMethod(new ScrollingMovementMethod());
		String text = "Type: "+strategy.getType();
		holder.type.setText(text);
		holder.buyNowLink.setMovementMethod(new ScrollingMovementMethod());
		String photoFileName = strategy.getPhoto().replace(" ","%20");
		Glide.with(view).load(STRATEGY_IMAGE_URL+photoFileName).into(holder.image);
		holder.buyNowLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("This will take you to a third-party site to view the content. Do you wish to continue?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
					String url = strategy.getLink();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					context.startActivity(intent);
				});

				builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
					dialog.cancel();
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	@Override
	public int getItemCount() {
		return strategyList.size();
	}

	@Override
	public void onViewAttachedToWindow(@NonNull StrategyViewHolder holder) {
		view.animate().alpha(1f).setDuration(150);
	}

	public class StrategyViewHolder extends RecyclerView.ViewHolder{
		private ImageView image;
		private TextView name, description, type, buyNowLink;

		public StrategyViewHolder(@NonNull View itemView) {
			super(itemView);
			this.image = itemView.findViewById(R.id.iv_strategy_short_img);
			this.name = itemView.findViewById(R.id.tv_strategy_short_name);
			this.description = itemView.findViewById(R.id.tv_strategy_short_desc);
			this.type = itemView.findViewById(R.id.tv_strategy_short_type);
			this.buyNowLink = itemView.findViewById(R.id.btn_buy_now);
		}
	}
}
