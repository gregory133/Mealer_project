package com.example.homepageactivity.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.example.homepageactivity.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MealsGridAdapter extends BaseAdapter {
    Context context;
    ArrayList<QueryDocumentSnapshot> meals;
    LayoutInflater inflter;

    public MealsGridAdapter(Context applicationContext, ArrayList<QueryDocumentSnapshot> meals) {
        this.context = applicationContext;
        this.meals = meals;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return meals.size();
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
        view = inflter.inflate(R.layout.meal_menu_icon, null); // inflate the layout
        Button icon = (Button) view.findViewById(R.id.mealDisplay); // get the reference of Button
        icon.setText(meals.get(i).getString("mealName")); // set logo images
        return view;
    }
}
