package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.homepageactivity.domain.Cook;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CookDescriptionActivity extends AppCompatActivity {
    private Button nextButton;
    private ImageView background;
    private Button btnTakePhoto;
    private Bitmap voidCheck;
    private static final int RequestPermissionCode = 1;
    private FirebaseStorage fbStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_description);

        fbStorage = FirebaseStorage.getInstance();

        background=findViewById(R.id.background);
        nextButton=findViewById(R.id.completeCookRegistration);

        btnTakePhoto = findViewById(R.id.voidCheckButton);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enableRuntimePermission();
        }});
    }


    public void onClickFinishClientRegButton(View view){
        String description = (((EditText) findViewById(R.id.description)).getText()).toString();
        if(!validate(description)) return;
        if (voidCheck == null) {
            Toast.makeText(this, "Please upload a void check for payment purposes", Toast.LENGTH_LONG).show();
            return;
        }

        getIntent().putExtra("Description", description);

        createCookAccount();

        finish();
    }

    private void createCookAccount(){
        Bundle extras = getIntent().getExtras();
        firebaseAuth.createUserWithEmailAndPassword(extras.getString("Email"), extras.getString("Password"))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            //uploads void check to Firebase Storage
                            uploadVoidCheck();
                        }
                        else {
                            onAccountCreationFailure();
                        }
                    }
                });
    }

    private void uploadVoidCheck() {
        StorageReference voidCheckRef = fbStorage.getReference().child(firebaseAuth.getCurrentUser().getUid()+"VoidCheck.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        voidCheck.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = voidCheckRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                onFailedUpload();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                onSuccessfulUpload(voidCheckRef.getPath());
            }
        });
    }

    private void onFailedUpload() {
        Toast.makeText(
                        getApplicationContext(),
                        "Problem Uploading Void Check \n Please try again later",
                        Toast.LENGTH_LONG)
                .show();
    }

    private void onSuccessfulUpload(String voidCheckPath) {
        Bundle extras = getIntent().getExtras();

        Cook newCook = new Cook(
                extras.getString("FirstName"),
                extras.getString("LastName"),
                extras.getString("Address"),
                extras.getString("Email"),
                voidCheckPath,
                extras.getString("Description"));

        String uid = firebaseAuth.getCurrentUser().getUid();
        firestoreDB.collection("users").document(uid).set(newCook);
        Map<String, Object> temp = new HashMap<>(1);
        temp.put("role","Cook");
        firestoreDB.collection("users").document(uid).set(temp, SetOptions.merge());

        Toast.makeText(getApplicationContext(),
                        "Registration successful!",
                        Toast.LENGTH_SHORT)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            voidCheck = (Bitmap) data.getExtras().get("data");
        }
    }

    public void enableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(CookDescriptionActivity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(CookDescriptionActivity.this,"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CookDescriptionActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 7);
    }
}