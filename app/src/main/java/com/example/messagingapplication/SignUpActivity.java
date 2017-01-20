package com.example.messagingapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    EditText mEmail, mPassword,mRPassword, mFName,mLName;
    String email, pass, fName,lName,rPass,url, gender;
    RadioGroup radioGroup;
    Button cancel, signUp;
    ImageView profileImage, edit;
    private Uri imageURI;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    FirebaseStorage storage =  FirebaseStorage.getInstance();
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Sign UP");

        cancel = (Button) findViewById(R.id.buttonCancel);
        signUp = (Button) findViewById(R.id.buttonSignUp);
        mFName = (EditText) findViewById(R.id.signUpFirstName);
        mLName = (EditText) findViewById(R.id.signUpLastName);
        mEmail = (EditText) findViewById(R.id.signUpEmail);
        mPassword = (EditText) findViewById(R.id.signUpPassword);
        mRPassword = (EditText) findViewById(R.id.signUpRepeatPassword);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        profileImage = (ImageView) findViewById(R.id.signUpImage);
        edit = (ImageView) findViewById(R.id.signUpImageEdit);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        storageRef = storage.getReferenceFromUrl("gs://homeworks-fcb2f.appspot.com");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                pass = mPassword.getText().toString();
                rPass = mRPassword.getText().toString();
                fName = mFName.getText().toString();
                lName = mLName.getText().toString();

                int id= radioGroup.getCheckedRadioButtonId();
                if (id == R.id.male){
                    gender = "Male";
                }else if (id == R.id.female){
                    gender = "Female";
                }

                if(fName.equals("")){
                    Toast.makeText(SignUpActivity.this,"First Name is null",Toast.LENGTH_LONG).show();
                } else if(lName.equals("")){
                    Toast.makeText(SignUpActivity.this,"Last Name is null",Toast.LENGTH_LONG).show();
                } else if(email.equals("")){
                    Toast.makeText(SignUpActivity.this,"Email is null",Toast.LENGTH_LONG).show();
                } else if(pass.equals("")){
                    Toast.makeText(SignUpActivity.this,"Choose a password",Toast.LENGTH_LONG).show();
                } else if(!rPass.equals(pass)){
                    Toast.makeText(SignUpActivity.this,"Password did not match",Toast.LENGTH_LONG).show();
                } else{
                    mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if(task.isSuccessful()){
                                int size = task.getResult().getProviders().size();
                                if(size >=1)
                                    Toast.makeText(SignUpActivity.this, "Email already exists, try with different email", Toast.LENGTH_LONG).show();
                                else {
                                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                onAuthSuccess(task.getResult().getUser());
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Error Signing up", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onImageGalaryClicked(View v){
        Intent pictureIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        pictureIntent.setDataAndType(data, "image/*");
        startActivityForResult(pictureIntent,20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 20){
                imageURI = data.getData();

                InputStream inputStream;
                try{
                    inputStream = getContentResolver().openInputStream(imageURI);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    profileImage.setImageBitmap(image);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void onAuthSuccess(FirebaseUser user) {

        final String id = user.getUid();
        String path = "images/profilepic/"+user.getUid()+".jpeg";
        StorageReference imageRef = storageRef.child(path);
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = profileImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                url = taskSnapshot.getDownloadUrl().toString();
                writeNewUser(email, fName, gender, lName, id, url);
            }
        });
    }

    private void writeNewUser(String email, String fName, String gender, String lName, String uId, String url) {
        User user = new User(email,fName,gender, lName,uId,url);
        mDatabase.child("users").child(uId).setValue(user);
        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        finish();
    }
}
