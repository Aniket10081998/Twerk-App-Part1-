package com.example.twerk.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twerk.MainActivity;
import com.example.twerk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.example.twerk.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }
    private EditText emailOrphone,password;
    private Button loginBtn;
    private TextView createAccountTV,forgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailOrphone.setError(null);
                password.setError(null);
                if(emailOrphone.getText().toString().isEmpty())
                {
                    emailOrphone.setError("Required!");
                    return;
                }
                if(password.getText().toString().isEmpty())
                {
                    password.setError("Required!");
                    return;
                }
                if(VALID_EMAIL_ADDRESS_REGEX.matcher(emailOrphone.getText().toString()).find())
                {
                    login(emailOrphone.getText().toString());
                }
                else if(emailOrphone.getText().toString().matches("\\d{10}"))
                {
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone",emailOrphone.getText().toString())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                List<DocumentSnapshot> document=task.getResult().getDocuments();
                                if(document.isEmpty())
                                {
                                    emailOrphone.setError("Phone number does not exist");
                                    return;
                                }
                                else
                                {
                                    String email=document.get(0).get("email").toString();
                                    login(email);
                                }

                            }
                            else
                            {
                                String error=task.getException().getMessage();
                                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    emailOrphone.setError("Enter valid email or Phone Number");

                }

            }
        });

        createAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new CreateAccountFragment());
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new ForgotPasswordFragment());
            }
        });
    }
    private void init(View view)
    {
        emailOrphone=view.findViewById(R.id.email_or_phone);
        password=view.findViewById(R.id.password);
        loginBtn=view.findViewById(R.id.login_btn);
        createAccountTV=view.findViewById(R.id.create_account_text);
        forgotPassword=view.findViewById(R.id.forgot_password);

    }
    private void login(String email)
    {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent mainIntent=new Intent(getContext(), UsernameActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }
                else
                {
                    String error=task.getException().getMessage();
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
