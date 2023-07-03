package com.itpn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itpn.R;
import com.itpn.config.AppConfig;
import com.itpn.model.Feedback;

import java.util.ArrayList;

public class TestimonialAdapter extends RecyclerView.Adapter<TestimonialAdapter.TestimonialViewHolder> implements AppConfig {
	Context context;
	private ArrayList<Feedback> testimonial;
	View view;

	public TestimonialAdapter(Context context, ArrayList<Feedback> testimonial) {
		this.context = context;
		this.testimonial = testimonial;
	}

	@NonNull
	@Override
	public TestimonialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testimonial_cardview,parent,false);
		return new TestimonialViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull TestimonialViewHolder holder, int position) {
		Feedback feedback = testimonial.get(position);
		String name="Anonymous";
		if("no".equals(feedback.getAnonymous())){
			name = feedback.getName();
		}
		holder.name.setText(name);
		String photo= feedback.getPhoto();
		if(photo == null || photo.equals("null")){
			photo = name.toUpperCase().charAt(0)+".png";
		}
		Glide.with(view).load(PROFILE_IMAGE_URL+photo).into(holder.profileImage);
		holder.rating.setRating(feedback.getRating());
		holder.feedback.setText(feedback.getFeedback());
	}

	@Override
	public int getItemCount() {
		return testimonial.size();
	}

	public static class TestimonialViewHolder extends RecyclerView.ViewHolder{
		private ImageView profileImage;
		private TextView name, feedback;
		private RatingBar rating;

		public TestimonialViewHolder(@NonNull View itemView) {
			super(itemView);
			this.profileImage = itemView.findViewById(R.id.iv_profile_img);
			this.name = itemView.findViewById(R.id.tv_name);
			this.feedback = itemView.findViewById(R.id.tv_feedback);
			this.rating = itemView.findViewById(R.id.rb_stars);
		}
	}
}


