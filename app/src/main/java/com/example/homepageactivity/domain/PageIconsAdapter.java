package com.example.homepageactivity.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.homepageactivity.R;

import java.util.ArrayList;

public class PageIconsAdapter extends BaseAdapter {
    Context context;
    ArrayList<PageIconInfo> iconInfos;
    LayoutInflater inflter;

    public PageIconsAdapter(Context applicationContext, ArrayList<PageIconInfo> conInfos) {
        this.context = applicationContext;
        this.iconInfos = iconInfos;
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
        Button icon = (Button) view.findViewById(R.id.userPageButton); // get the reference of Button
        icon.setText(iconInfos.get(i).getIconName()); // set logo images
        return view;
    }
}
