package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.StyleApplyer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CookDescriptionActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button nextButton;
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_description);

        background=findViewById(R.id.background);
        nextButton=findViewById(R.id.completeCookRegistration);
        setThemeColors();

        //firebase integration
        mAuth = FirebaseAuth.getInstance();
    }

    private void setThemeColors(){

        ContextWrapper wrapper=new ContextThemeWrapper(this, R.style.cook_style);

        background.setImageDrawable(StyleApplyer.applyTheme(getApplicationContext(), wrapper,R.drawable.ic_wave));
        nextButton.setBackground(StyleApplyer.applyTheme(getApplicationContext(), wrapper,R.drawable.ic_button_1));

    }


    public void onClickFinishClientRegButton(View view){
        String description = (((EditText) findViewById(R.id.description)).getText()).toString();
        if(!validate(description)) return;

        getIntent().putExtra("Description", description);

        createCookAccount();

        finish();
    }

    private void createCookAccount(){
        Bundle extras = getIntent().getExtras();

        Cook newCook = new Cook(
                extras.getString("FirstName"),
                extras.getString("LastName"),
                extras.getString("Address"),
                null,
                extras.getString("Description"));

        mAuth.createUserWithEmailAndPassword(extras.getString("Email"), extras.getString("Password"))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            //adds the user's details to the Cloud Firestore
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(uid).set(newCook);
                            Map<String, Object> temp = new HashMap<>(1);
                            temp.put("role","Cook");
                            db.collection("users").document(uid).set(temp, SetOptions.merge());
                            /* For some reason it doesn't like addOnSuccessListener(new OnSuccessListener<DocumentReference>(), which worked in the MainActivity test case
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            onAccountCreationSuccess();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            onDetailAdditionFailure();                                        }
                                    });*/
                            onAccountCreationSuccess();
                        }
                        else {
                            onAccountCreationFailure();
                        }
                    }
                });
    }

    private void onAccountCreationSuccess(){
        Toast.makeText(getApplicationContext(),
                        "Registration successful!",
                        Toast.LENGTH_LONG)
                .show();
        finish();
    }

    private void onAccountCreationFailure(){
        Toast.makeText(
                        getApplicationContext(),
                        "Problem Registering User \n Please try again later",
                        Toast.LENGTH_LONG)
                .show();
    }

    private boolean validate(String description){
        if (description.equals("")){
            Toast.makeText(this, "Description Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}