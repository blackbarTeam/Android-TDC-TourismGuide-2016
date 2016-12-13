package com.edu.tdc.blackbar.tourismguide.myAdapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.tdc.blackbar.tourismguide.R;
import com.edu.tdc.blackbar.tourismguide.datamodel.Review;

import java.util.ArrayList;

/**
 * Created by Shiro on 03/12/2016.
 */

public class ReviewAdapter extends ArrayAdapter {
    private Activity context;
    private int IDLayout;
    private ArrayList<Review> reviews = null;
    public ReviewAdapter(Activity context, int resource, ArrayList<Review> reviews) {
        super(context, resource, reviews);
        this.context = context;
        this.IDLayout = resource;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = context.getLayoutInflater().inflate(this.IDLayout,null);

        if(reviews.size() > 0 && position >= 0){
            Review review = reviews.get(position);
            //set author
            TextView txtAuthor = (TextView) convertView.findViewById(R.id.txt_author_review_item);
            txtAuthor.setText(review.getName());
            //set rating
            TextView txtRating = (TextView) convertView.findViewById(R.id.txt_rating_review_item);
            txtRating.setText(String.valueOf(review.getRating()));
            //set content
            TextView txtContent = (TextView) convertView.findViewById(R.id.txt_content_review_item);
            txtContent.setText(review.getContent());
            //set time  ago
            TextView txtTimeAgo = (TextView) convertView.findViewById(R.id.txt_time_ago_review_item);
            txtTimeAgo.setText(review.getTimeAgo());
            //set rating imv
            ImageView imvRating = (ImageView) convertView.findViewById(R.id.imv_rating_review_item);
            //update rating
            double rating = review.getRating();
            if (rating == -1 || rating == 0) {
                txtRating.setText("");
                imvRating.setImageResource(R.drawable.rating0);
            } else {
                if (rating == 1) {
                    txtRating.setText(rating + "");
                    imvRating.setImageResource(R.drawable.rating1);
                } else {
                    if (rating > 1 && rating < 2) {
                        txtRating.setText(rating + "");
                        imvRating.setImageResource(R.drawable.rating15);
                    } else {
                        if (rating == 2) {
                            txtRating.setText(rating + "");
                            imvRating.setImageResource(R.drawable.rating2);
                        } else {
                            if (rating > 2 && rating < 3) {
                                txtRating.setText(rating + "");
                                imvRating.setImageResource(R.drawable.rating25);
                            } else {
                                if (rating == 3) {
                                    txtRating.setText(rating + "");
                                    imvRating.setImageResource(R.drawable.rating3);
                                } else {
                                    if (rating > 3 && rating < 4) {
                                        txtRating.setText(rating + "");
                                        imvRating.setImageResource(R.drawable.rating35);
                                    } else {
                                        if (rating == 4) {
                                            txtRating.setText(rating + "");
                                            imvRating.setImageResource(R.drawable.rating4);
                                        } else {
                                            if (rating > 4 && rating < 5) {
                                                txtRating.setText(rating + "");
                                                imvRating.setImageResource(R.drawable.rating45);
                                            } else {
                                                if (rating == 5) {
                                                    txtRating.setText(rating + "");
                                                    imvRating.setImageResource(R.drawable.rating5);
                                                } else {
                                                    if (rating > 0 && rating < 1) {
                                                        txtRating.setText(rating + "");
                                                        imvRating.setImageResource(R.drawable.rating05);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return convertView;
    }
}
