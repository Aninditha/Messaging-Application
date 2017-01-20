package com.example.messagingapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    User user= new User();
    EditText mFname,mLname;
    TextView mEmail;
    String fName,lName,email,gender,url;
    RadioGroup rg;
    ImageView profilePic,editImage;

    DatabaseReference mUsers;
    FirebaseStorage storage =  FirebaseStorage.getInstance();
    StorageReference storageRef;



    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnFragmentInteractionListener) {
            mListener = (ProfileFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExpenceFragmentInteractionListener");
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = mListener.getPresentUser();
        mEmail= (TextView) getView().findViewById(R.id.myProfileEmail);
        mFname = (EditText) getView().findViewById(R.id.myProfileFirstName);
        mLname = (EditText) getView().findViewById(R.id.myProfileLastName);
        rg = (RadioGroup) getView().findViewById(R.id.radioGroup);
        profilePic= (ImageView) getView().findViewById(R.id.myProfileImage);
        editImage= (ImageView) getView().findViewById(R.id.myProfileImageEdit);

        storageRef = storage.getReferenceFromUrl("gs://homeworks-fcb2f.appspot.com");

        mEmail.setText(user.getEmail());
        mFname.setText(user.getfName());
        mLname.setText(user.getlName());

        if(user.getGender().equals("Male")){
            rg.check(R.id.male);
        }else{
            rg.check(R.id.female);
        }

        getView().findViewById(R.id.buttonUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName= mFname.getText().toString();
                lName= mLname.getText().toString();
                email= mEmail.getText().toString();
                int id= rg.getCheckedRadioButtonId();
                if (id==R.id.male){
                    gender="Male";
                }else if (id==R.id.female){
                    gender="Female";
                }

                if(mFname.equals("")){
                    Toast.makeText(getView().getContext(),"First Name is null",Toast.LENGTH_LONG).show();
                } else if(mLname.equals("")){
                    Toast.makeText(getView().getContext(),"Last Name is null",Toast.LENGTH_LONG).show();
                } else{

                    String path = "images/profilepic/"+user.getuId()+".jpeg";
                    StorageReference imageRef = storageRef.child(path);
                    profilePic.setDrawingCacheEnabled(true);
                    profilePic.buildDrawingCache();
                    Bitmap bitmap = profilePic.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            url = taskSnapshot.getDownloadUrl().toString();
                            writeNewUser(email,fName,gender, lName,user.getuId(),url);

                        }
                    });
                }
            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                pictureIntent.setDataAndType(data, "image*//*");
                startActivityForResult(pictureIntent,20);

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 20){
                Uri imageURI = data.getData();
                InputStream inputStream;
                try{

                    inputStream = getView().getContext().getContentResolver().openInputStream(imageURI);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    profilePic.setImageBitmap(image);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeNewUser(String email, String fName, String gender, String lName, String uId, String url) {
        User user = new User(email,fName,gender, lName,uId,url);
        mUsers.child(uId).setValue(user);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        User getPresentUser();
    }
}
