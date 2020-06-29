package com.example.twerk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.twerk.registration.RegisterActivity;
import com.example.twerk.registration.UsernameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FrameLayout frameLayout;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();


        firebaseAuth=firebaseAuth.getInstance();
        firebaseFirestore=firebaseFirestore.getInstance();
        checkUsername();

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void init()
    {
        frameLayout=findViewById(R.id.framelayout);
        tabLayout=findViewById(R.id.tablayout);
    }



    private void checkUsername()
    {
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                if(!task.getResult().contains("username"))
                                {
                                    Intent usernameIntent=new Intent(MainActivity.this, UsernameActivity.class);
                                    startActivity(usernameIntent);
                                    finish();
                                }
                            }
                            else
                            {
                                Intent registerIntent=new Intent(MainActivity.this, RegisterActivity.class);
                                startActivity(registerIntent);

                            }

                        }
                        else
                        {
                            String error=task.getException().getMessage();
                            Toast.makeText(MainActivity.this,error,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
