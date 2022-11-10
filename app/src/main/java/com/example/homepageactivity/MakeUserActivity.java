package com.example.homepageactivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.homepageactivity.domain.StyleApplyer;
import com.example.homepageactivity.domain.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class MakeUserActivity extends AppCompatActivity {

    private TextView titleText;
    private ImageView background;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_user);

        titleText=findViewById(R.id.title);
        background=findViewById(R.id.background);
        nextButton=findViewById(R.id.completeCookRegistration);

        Bundle extras=getIntent().getExtras();
        String mode=extras.getString("TYPE");

        String titleString=(String)titleText.getText();
        titleText.setText(titleString+" "+mode);
        setThemeColors(mode);
    }
    
    public void onClickMakeUserNextButton(View view){
        String firstName = (((EditText) findViewById(R.id.firstNameEdit)).getText()).toString();
        String lastName = (((EditText) findViewById(R.id.lastNameEdit)).getText()).toString();
        String email = (((EditText) findViewById(R.id.emailEdit)).getText()).toString();
        String address = (((EditText) findViewById(R.id.addressEdit)).getText()).toString();
        String password = (((EditText) findViewById(R.id.passwordEdit)).getText()).toString();
        String confirmPassword = (((EditText) findViewById(R.id.confirmPasswordEdit)).getText()).toString();

        if (!validate(firstName, lastName, email, address, password, confirmPassword)) return;

        Bundle extras=getIntent().getExtras();
        String userType=extras.getString("TYPE");
        Intent intent=new Intent(this, ClientPaymentActivity.class);

        if(userType.contains("Cook")){
            intent=new Intent(this, CookDescriptionActivity.class);
        }

        intent.putExtra("FirstName", firstName);
        intent.putExtra("LastName", lastName);
        intent.putExtra("Email", email);
        intent.putExtra("Address", address);
        intent.putExtra("Password", password);

        finish();
        startActivity(intent);
    }

    private void setThemeColors(String mode){

        ContextWrapper wrapper=null;
        if (mode.equals("Cook")){
            wrapper=new ContextThemeWrapper(this, R.style.cook_style);

        }
        else if (mode.equals("Client")){
            wrapper=new ContextThemeWrapper(this, R.style.client_style);
        }
        background.setImageDrawable(StyleApplyer.applyTheme(getApplicationContext(), wrapper,R.drawable.ic_wave));
        nextButton.setBackground(StyleApplyer.applyTheme(getApplicationContext(), wrapper,R.drawable.ic_button_1));

    }

    /**
     * rules for valid data entries given
     * @param firstName
     * @param lastName
     * @param email
     * @param address
     * @param password
     * @param confirmPassword
     * @return
     */
    private boolean validate(String firstName, String lastName, String email, String address, String password, String confirmPassword){
        Validator val = new Validator();
        if (!val.isAlphabetic(firstName)){
            Toast.makeText(this, "First Name Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!val.isAlphabetic(lastName)){
            Toast.makeText(this, "Last Name Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!val.checkValidEmail(email)){
            Toast.makeText(this, "Email Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!val.checkValidPassword(password)){
            Toast.makeText(this, "Password Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (confirmPassword.equals("")){
            Toast.makeText(this, "Confirm Password Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length()<6){
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_LONG).show();
            return false;
        }
        /*replaced by val.checkValidEmail(emial)*/
//        int dotCount= email.length() - email.replace(".", "").length();
//        int atCount= email.length() - email.replace("@", "").length();
//        if (dotCount!=1 || atCount!=1){
//            Toast.makeText(this, "Given email address is invalid", Toast.LENGTH_LONG).show();
//            return false;
//        }
        return true;
    }
}