package com.example.homepageactivity.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.homepageactivity.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdapterMealOrder extends BaseAdapter {
    Context context;
    ArrayList<QueryDocumentSnapshot> orders;
    LayoutInflater inflter;

    public AdapterMealOrder(Context applicationContext, ArrayList<QueryDocumentSnapshot> orders) {
        this.context = applicationContext;
        this.orders = orders;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return orders.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.meal_order_icon, null);
        QueryDocumentSnapshot order = orders.get(i);
        //Set orderName
        ((TextView) view.findViewById(R.id.orderMealNameTextView)).setText(order.getString("mealName"));

        String status;
        String[][] statuses = {{"Request Declined", "Pending Approval", "Request Approved"},{"Order Canceled", "", "Order Delivered"},{"Order Lost", "", "Order Received"}};
        if(order.getDouble("received").intValue() != 0){
            status = statuses[2][order.getDouble("received").intValue()+1];     //-1 converts from -1, 0, 1 => 0, 1, 2 for: fail, default, pass
        }else if(order.getDouble("delivered") != 0){
            status = statuses[1][order.getDouble("delivered").intValue()+1];
        }else{
            status = statuses[0][order.getDouble("approved").intValue()+1];
        }
        ((TextView) view.findViewById(R.id.orderStatusTextView)).setText(status);

        return view;
    }
}
