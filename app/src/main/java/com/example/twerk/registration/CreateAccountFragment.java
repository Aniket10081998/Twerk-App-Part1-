package com.example.twerk.registration;

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

import com.example.twerk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {

    public CreateAccountFragment() {
        // Required empty public constructor
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    private EditText email,phone,password,confirmPassword;
    private Button createAccountBtn;
    private TextView loginTV;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        firebaseAuth= FirebaseAuth.getInstance();
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new LoginFragment());
            }
        });
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);
                if(email.getText().toString().isEmpty())
                {
                    email.setError("Required!!");
                    return;
                }
                phone.setError(null);
                if(phone.getText().toString().isEmpty())
                {
                    phone.setError("Required!!");
                    return;
                }
                password.setError(null);
                if(password.getText().toString().isEmpty())
                {
                    password.setError("Required!!");
                    return;
                }
                confirmPassword.setError(null);
                if(confirmPassword.getText().toString().isEmpty())
                {
                    confirmPassword.setError("Required!!");
                    return;
                }
                if(!VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find())
                {
                    email.setError("Enter valid email");
                    return;
                }
                if(phone.getText().toString().length()!=10)
                {
                    phone.setError("Enter valid phone number");
                    return;
                }
                if(!password.getText().toString().equals(confirmPassword.getText().toString()))
                {
                    confirmPassword.setError("Password and Confirm Password Mismatched");
                    return;
                }
                createAccount();
            }
        });
    }
    private void init(View view)
    {
        email=view.findViewById(R.id.email);
        phone=view.findViewById(R.id.phone);
        password=view.findViewById(R.id.password);
        confirmPassword=view.findViewById(R.id.confirm_password);
        createAccountBtn=view.findViewById(R.id.create_account_btn);
        loginTV=view.findViewById(R.id.login_text);

    }
    private void createAccount()
    {
        firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().getSignInMethods().isEmpty())
                    {
                        ((RegisterActivity)getActivity()).setFragment(new OTPFragment(email.getText().toString(),phone.getText().toString(),password.getText().toString()));
                    }
                    else
                    {
                        email.setError("This email already exist.Try different Email id");
                    }
                }
                else
                {
                    String error=task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
