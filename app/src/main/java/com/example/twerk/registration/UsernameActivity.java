package com.example.twerk.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.twerk.MainActivity;
import com.example.twerk.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsernameActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private Button removeBtn,createAccountButton;
    private EditText username;
    private Uri photoUri;
    public final static String USERNAME="^[a-z0-9_-]{3,15}$";
    private StorageReference storage;
    private FirebaseAuth firebaseAuth;
    private String url="";
    private FirebaseFirestore fireStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        init();

        storage=FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        fireStore=FirebaseFirestore.getInstance();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(UsernameActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted())
                        {
                            selectImage();
                        }
                        else
                        {
                            String perm="Please allow the permissions";
                            Toast.makeText(UsernameActivity.this,perm,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
            }
        });


        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUri=null;
                profileImageView.setImageResource(R.drawable.profileplaceholder);
            }
        });



        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setError(null);
                if(username.getText().toString().isEmpty()|| username.getText().toString().length()<4)
                {
                    username.setError("Username length must be greater then 4");
                    return;
                }
                if(!username.getText().toString().matches(USERNAME))
                {
                    username.setError("Username length should be less then 115,alphabets,numbers,'-','_' allowed");
                    return;
                }
                fireStore.collection("users").whereEqualTo("username",username.getText().toString())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<DocumentSnapshot> document=task.getResult().getDocuments();
                            if(document.isEmpty())
                            {
                                uploadData();

                                return;
                            }
                            else
                            {
                                username.setError("Username already taken");
                                return;
                            }

                        }
                        else
                        {
                            String error=task.getException().getMessage();
                            Toast.makeText(UsernameActivity.this,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }



    private void init()
    {
        profileImageView=findViewById(R.id.profile_image);
        removeBtn=findViewById(R.id.remove_btn);
        createAccountButton=findViewById(R.id.create_account_btn);
        username=findViewById(R.id.username);
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 photoUri = result.getUri();
                Glide
                        .with(this)
                        .load(photoUri)
                        .centerCrop()
                        .placeholder(R.drawable.profileplaceholder)
                        .into(profileImageView);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectImage()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Display Profile")
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }


    private void uploadUsername()
    {
        Map<String,Object> map=new HashMap<>();
        map.put("username",username.getText().toString());
        map.put("profile_Url",url);

        fireStore.collection( "users").document(FirebaseAuth.getInstance().getUid()).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent mainIntent=new Intent(UsernameActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                        else{
                            String error=task.getException().getMessage();
                            Toast.makeText(UsernameActivity.this,error,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    private void uploadData()
    {
        if(photoUri!=null)
        {
            final StorageReference ref = storage.child("images/"+firebaseAuth.getCurrentUser().getUid());
            UploadTask uploadTask = ref.putFile(photoUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        String error=task.getException().getMessage();
                        Toast.makeText(UsernameActivity.this,error,Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url=uri.toString();
                        }
                    });
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {


                        uploadUsername();
                    } else {
                        // Handle failures
                        // ...
                        String error=task.getException().getMessage();
                        Toast.makeText(UsernameActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            uploadUsername();
        }

    }




}
