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

public class AdapterMenuMeal extends BaseAdapter {
    Context context;
    ArrayList<QueryDocumentSnapshot> meals;
    LayoutInflater inflter;

    public AdapterMenuMeal(Context applicationContext, ArrayList<QueryDocumentSnapshot> meals) {
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
        view = inflter.inflate(R.layout.meal_menu_icon, null);
        //Set mealName
        ((TextView) view.findViewById(R.id.mealNameTextView)).setText(meals.get(i).getString("mealName"));
        //set mealIsOffered
        ((ImageView) view.findViewById(R.id.offeredIcon)).setVisibility((meals.get(i).getBoolean("offered")) ? View.VISIBLE : View.INVISIBLE);
        //set mealPicture
        //set mealPictureOverlay
        ((RelativeLayout) view.findViewById(R.id.overlay)).setAlpha(1);
        //set Rating
        setStarRating(view, 3.5f);
        return view;
    }

    private void setStarRating(View view, float rating){
        ImageView[] starViews = {view.findViewById(R.id.star1Icon),
                view.findViewById(R.id.star2Icon),
                view.findViewById(R.id.star3Icon),
                view.findViewById(R.id.star4Icon),
                view.findViewById(R.id.star5Icon)};
        for(int i=0;i< starViews.length;i++){
            starViews[i].setVisibility((rating-0.5f >= i) ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
