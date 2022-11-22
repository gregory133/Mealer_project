package com.example.homepageactivity.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.homepageactivity.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdapterInboxMessage extends ArrayAdapter<QueryDocumentSnapshot> {


    private Context context;
    private int res;

    public AdapterInboxMessage(Context context, int res, ArrayList<QueryDocumentSnapshot> list){
        super(context, res, list);
        this.context=context;
        this.res=res;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){

        LayoutInflater inflater=LayoutInflater.from(context);
        convertView=inflater.inflate(res, parent, false);

        TextView senderText=convertView.findViewById(R.id.senderText);
        TextView subjectText=convertView.findViewById(R.id.subject);
        TextView firstWordText=convertView.findViewById(R.id.firstWordText);

        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> taskDoc = firestoreDB.collection("users").document(getItem(pos).toObject(Message.class).getSenderUID()).get();
        while (true) {
            if (taskDoc.isComplete()) {
                break;
            }
        }
        String firstName = "ERROR";
        String lastName = "ERROR";
        if (taskDoc.isSuccessful()) {
            DocumentSnapshot document = taskDoc.getResult();
            firstName = document.getString("firstName");
            lastName = document.getString("lastName");
        }
        senderText.setText("From: "+firstName+" "+lastName);
        subjectText.setText(getItem(pos).toObject(Message.class).getSubject());
        firstWordText.setText(getItem(pos).toObject(Message.class).getBodyText());

        return convertView;
    }

}
