package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    String userRole; //client, admin or cook
    TextView titleText;
    Spinner spinner;
    ListView listView;

    ArrayList<Message> items=new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        userRole = getIntent().getStringExtra("userRole");
        titleText=findViewById(R.id.title);
        spinner=findViewById(R.id.spinner);
        listView=findViewById(R.id.list);
        setTitle();
        hookDropDown();
        hookList();
    }

    private void hookDropDown(){
        final List<String> options = Arrays.asList("Settings", "Logout");
        ArrayAdapter adapter=new ArrayAdapter(this, R.layout.dropdown_layout, options);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (options.get(i).equals("Logout")){
                    logout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void setTitle(){
        HashMap<String, String> titleDict=new HashMap<String, String>(){{
            put("admin", "User Complaints");
            put("cook", "Messages");
            put("client", "Messages");
        }};
        titleText.setText(titleDict.get(userRole));

    }
    private void logout(){
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("Logout", 1);
//        setResult(RESULT_OK, returnIntent);
        finish();
    }
    private void hookList(){
        items.add(new ComplaintMessage(null, null, "Rat in my soup", "I send this complaint to inform you that there was a rat in my soup", null));
        ItemListAdapter adapter=new ItemListAdapter(this, R.layout.inbox_list_item, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected:");
                Intent intent=new Intent(getApplicationContext(), InboxDescriptionActivity.class);
                intent.putExtra("subject", items.get(i).getSubject());
                intent.putExtra("description", items.get(i).getBodyText());
                startActivity(intent);
            }
        });
    }
}