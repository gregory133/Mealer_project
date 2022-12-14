package com.example.homepageactivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView;
    private Dialog notice;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseFirestore firestoreDB;
    public static User currentAccount;
    public static final List<String> cuisineOptions = Arrays.asList("Chinese", "Other");
    public static final List<String> mealTypeOptions = Arrays.asList("Appetizer", "Entree", "Dessert", "Other");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.emailTextEdit);
        passwordTextView = findViewById(R.id.editTextPassword);

        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentAccount = null;
    }

    public void onClickLoginButton(View view){
        //Check both boxes are filled
        if(!validateLoginInputs()){
            Toast.makeText(this, "Please Enter your Email and Password", Toast.LENGTH_LONG).show();
            return;
        }
        loginUserAttempt();
    }

    /**
     * Clears email and password boxes on login
     */
    private void ClearLoginInfoBoxes(){
        emailTextView.setText("");
        passwordTextView.setText("");
    }

    private boolean validateLoginInputs(){
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        return !email.equals("") && !password.equals("");
    }

    private void loginUserAttempt()
    {
        // Take the value of two edit texts in Strings
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        // signin existing user
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(
                    @NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful()){
                    loginAttemptSuccess();
                }else{
                    loginAttemptFailure("Invalid login information");
                }
            }
        });
    }

    private void loginAttemptSuccess(){
        //Don't want to leave there info sitting around after logging in
        ClearLoginInfoBoxes();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference docRef = firestoreDB.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        openHomePage(document);
                    } else {
                        loginAttemptFailure("Document doesn't exist");
                        return;
                    }
                } else {
                    loginAttemptFailure("on complete failed");
                    return;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    private void openHomePage(DocumentSnapshot document){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        String role=document.getString("role");
        if(role==null){
            loginAttemptFailure("Login attempt failed");
            return;
        }
        switch (role){
            case "Cook":
                intent = new Intent(getApplicationContext(), MenuActivity.class);
                //intent.putExtra("userRole","Cook");
                currentAccount = document.toObject(Cook.class);
                break;
            case "Admin":
                intent = new Intent(getApplicationContext(), InboxActivity.class);
                //intent.putExtra("userRole","Admin");
                currentAccount = document.toObject(Admin.class);
                break;
            case "Client":
                intent = new Intent(getApplicationContext(), InboxActivity.class);
                //intent.putExtra("userRole","Client");
                currentAccount = document.toObject(Client.class);
                break;
            default:
                loginAttemptFailure("Login attempt failed");
                return;
        }

        if (currentAccount.isBanned()) {
            showSuspensionNotice(" permanently");
        } else if (currentAccount.getBannedUntil().after(new Date())) {
            String until = currentAccount.getBannedUntil().toString();
            showSuspensionNotice(" until\n"+until);
        } else {
            startActivity(intent);
        }

        return;
    }

    private void showSuspensionNotice(String addon) {
        notice = new Dialog(this);
        notice.setContentView(R.layout.activity_user_suspension_notice);
        TextView noticeText = notice.findViewById(R.id.userSuspensionNoticeText);
        String noticeString = (String)noticeText.getText();
        noticeText.setText(noticeString+addon);
        notice.setCancelable(false);
        notice.setCanceledOnTouchOutside(false);
        notice.show();

        //this makes it so that the dialog window fits nicely within the screen
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        WindowManager.LayoutParams params = notice.getWindow().getAttributes();
        params.width = (6 * width)/7;
        notice.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void loginAttemptFailure(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }

    public void onClickRegister(View view){
        Intent intent=new Intent(this, ChooseRole.class);
        startActivity(intent);
    }

    public void suspendedUserLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        notice.dismiss();
    }
}