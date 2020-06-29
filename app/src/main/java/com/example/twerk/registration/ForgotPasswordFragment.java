package com.example.twerk.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twerk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.twerk.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }
    private TextView email;
    private Button resetbtn;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);
                if(VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find())
                {
                    resetbtn.setEnabled(false);
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(),"Reset email is sent",Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                    }
                                    else
                                    {
                                        String error=task.getException().getMessage();
                                        email.setError(error);
                                    }
                                    resetbtn.setEnabled(true);
                                }
                            });
                }
                else
                {
                    email.setError("Enter a valid Email id");
                }
            }
        });

    }
    private void init(View view)
    {
        email=view.findViewById(R.id.email);
        resetbtn=view.findViewById(R.id.reset_btn);
    }
}
