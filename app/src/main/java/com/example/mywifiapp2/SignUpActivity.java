package com.example.mywifiapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private Button SignUpButton;
    private TextView SignInTv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        firebaseAuth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.email);
        passwordEt1 = findViewById(R.id.password1);
        passwordEt2 = findViewById(R.id.password2);
        SignUpButton = findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        SignInTv = findViewById(R.id.signinTv);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        SignInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });









    }
    private void Register() {
        String email = emailEt.getText().toString();
        String password1 = passwordEt1.getText().toString();
        String password2 = passwordEt2.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailEt.setError("Enter your Email");
            return;
        }
        else if (TextUtils.isEmpty(password1)){
            emailEt.setError("Enter your Password");
            return;
        }
        else if (TextUtils.isEmpty(password2)){
            emailEt.setError("Confirm your Password");
            return;
        }
        else if (!password1.equals(password2)){
            emailEt.setError("Different Password");
            return;
        }
        else if (password1.length()<4){
            emailEt.setError("Length should be > 4");
            return;
        }
        else if (!isValidEmail(email)){
            emailEt.setError("Length should be > 4");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, StartingActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{Toast.makeText(SignUpActivity.this, "Sign up fail!", Toast.LENGTH_LONG).show();}
                progressDialog.dismiss();
            }
        });



    }
    private Boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
