package com.example.homepageactivity.domain;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.homepageactivity.R;

import java.util.ArrayList;

public class AdapterPageIcon extends BaseAdapter {
    Context context;
    ArrayList<PageIconInfo> iconInfos;
    LayoutInflater inflter;
    Class currnetClass;

    public AdapterPageIcon(Context applicationContext, ArrayList<PageIconInfo> iconInfos, Class currnetClass) {
        this.context = applicationContext;
        this.iconInfos = iconInfos;
        this.currnetClass=currnetClass;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return iconInfos.size();
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
        view = inflter.inflate(R.layout.user_page_icon, null); // inflate the layout

        //Set Icon colors
        int iconColor;
        if(iconInfos.get(i).getPageClass() != null && currnetClass.getName().contains(iconInfos.get(i).getPageClass().getName())){
            int bgColor;
            CardView bg = (CardView) view.findViewById(R.id.bg);    // get the reference of Button
            if (currnetClass == Cook.class) {
                bgColor = R.color.cook_light;
            } else{
                bgColor = R.color.client_light;
            }
            bg.setCardBackgroundColor(ContextCompat.getColor(context, bgColor));

            iconColor = R.color.dark_grey;
        }
        else{
            iconColor = R.color.off_grey;
        }
        ImageView icon = (ImageView) view.findViewById(R.id.icon);

        icon.setColorFilter(ContextCompat.getColor(context, iconColor), PorterDuff.Mode.SRC_IN);

        //Set icon image
        icon.setImageResource(iconInfos.get(i).getDrawableImageRef());
        return view;
    }
}
