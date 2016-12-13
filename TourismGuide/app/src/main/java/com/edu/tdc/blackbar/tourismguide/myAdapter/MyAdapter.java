package com.edu.tdc.blackbar.tourismguide.myAdapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.tdc.blackbar.tourismguide.R;

/**
 * Created by Shiro on 03/12/2016.
 */

public class MyAdapter extends ArrayAdapter {
    private Activity context;
    private int IDLayout;
    private String[] keyTypesSearch = null;
    public MyAdapter(Activity context, int resource, String[] types) {
        super(context, resource, types);
        this.context = context;
        this.IDLayout = resource;
        this.keyTypesSearch = types;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = context.getLayoutInflater().inflate(this.IDLayout,null);

        if(keyTypesSearch.length > 0 && position >= 0){
            //add key
            TextView keyTypes = (TextView)convertView.findViewById(R.id.txt_types_item);
            keyTypes.setText(keyTypesSearch[position]);
            //add icon
            ImageView ic_item = (ImageView)convertView.findViewById(R.id.ic_types_item);
            switch (keyTypesSearch[position]){
                case "Cafe": {
                    ic_item.setImageResource(R.drawable.cafe);
                    break;
                }case "Food": {
                    ic_item.setImageResource(R.drawable.food);
                    break;
                }case "Gym": {
                    ic_item.setImageResource(R.drawable.gym);
                    break;
                }case "Park": {
                    ic_item.setImageResource(R.drawable.park);
                    break;
                }case "Bar": {
                    ic_item.setImageResource(R.drawable.bar);
                    break;
                }case "Post Office": {
                    ic_item.setImageResource(R.drawable.office);
                    break;
                }case "Airport": {
                    ic_item.setImageResource(R.drawable.airport);
                    break;
                }case "Car Repair": {
                    ic_item.setImageResource(R.drawable.car_rp);
                    break;
                }case "ATM": {
                    ic_item.setImageResource(R.drawable.atm);
                    break;
                }case "Bank": {
                    ic_item.setImageResource(R.drawable.bank);
                    break;
                }case "Police": {
                    ic_item.setImageResource(R.drawable.police);
                    break;
                }case "Hospital": {
                    ic_item.setImageResource(R.drawable.hospital);
                    break;
                }default:{

                }
            }
        }
        return convertView;
    }
}
