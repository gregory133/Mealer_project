package com.example.homepageactivity.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.homepageactivity.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        view = View.inflate(context, R.layout.meal_order_icon, null);
        MealOrder order = orders.get(i).toObject(MealOrder.class);
        //Set orderName
        ((TextView) view.findViewById(R.id.orderMealNameTextView)).setText(order.getMealName());

        String status;
        List<String> approvedOptions = Arrays.asList("Pending Approval", "Request Approved", "Request Declined");
        List<String> deliveredOptions = Arrays.asList("Delivery Status", "Order Delivered", "Order Canceled");
        List<String> receivedOptions = Arrays.asList("Received Status", "Order Received", "Order Lost");

        if(order.getReceived() != 0){
            status = receivedOptions.get(order.getReceived());
        }else if(order.getDelivered() != 0){
            status = deliveredOptions.get(order.getDelivered());
        }else{
            status = approvedOptions.get(order.approved);
        }
        ((TextView) view.findViewById(R.id.orderStatusTextView)).setText(status);

        return view;
    }
}
