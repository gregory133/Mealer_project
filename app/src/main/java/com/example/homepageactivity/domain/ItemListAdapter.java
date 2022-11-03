package com.example.homepageactivity.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.homepageactivity.R;

import java.util.ArrayList;

public class ItemListAdapter extends ArrayAdapter<Message> {

    private Context context;
    private int res;

    public ItemListAdapter(Context context, int res, ArrayList<Message> list){
        super(context, res, list);
        this.context=context;
        this.res=res;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){

        LayoutInflater inflater=LayoutInflater.from(context);
        convertView=inflater.inflate(res, parent, false);
        TextView textView=convertView.findViewById(R.id.textView);
        textView.setText(getItem(pos).getSubject());

        return convertView;
    }

}
