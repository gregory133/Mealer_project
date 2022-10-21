package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CookDescriptionActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_description);

        //firebase integration
        mAuth = FirebaseAuth.getInstance();
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
        String firstName = extras.getString("FirstName");
        String lastName = extras.getString("LastName");
        String email = extras.getString("Email");
        String address = extras.getString("Address");
        String password = extras.getString("Password");
        String description = extras.getString("dDescription");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
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
                        "Problem Making Account"
                                + " Please try again later",
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