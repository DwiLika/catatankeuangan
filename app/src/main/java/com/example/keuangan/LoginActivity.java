package com.example.keuangan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//import static com.example.keuangan.paketku.SharedClass.ROOT_UID;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPass;
    private TextView mTextView;
    private Button signInBtn;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.emailIn);
        mPass = findViewById(R.id.passwordIn);
        signInBtn = findViewById(R.id.btn_signin);
        mTextView = findViewById(R.id.textViewIn);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();

                if (email.isEmpty()) {
                    mEmail.setError("Empty Fields Are not Allowed");
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Email sent. Please check your inbox", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("gagalllll", e.getLocalizedMessage(), e);
                    }
                });
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

    }

    private void loginUser() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(LoginActivity.this, "Login Sukses", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                    }
                });

            } else {
                mPass.setError("Empty Fields Are not Allowed");
            }
        } else if (email.isEmpty()) {
            mEmail.setError("Empty Fields Are not Allowed");
        } else {
            mEmail.setError("Please Enter Correct Email");
        }
    }
    //    private String email, password, errMsg;
//
//    @Override
//    protected void onCreate( Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        final FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        if (auth.getCurrentUser()!=null){
//            ROOT_UID = auth.getUid();
//
//            Intent intent = new Intent(LoginActivity.this,NavApp.class);
//            startActivity(intent);
//
//            finish();
//        }
//
//        findViewById(R.id.sign_up).setOnClickListener(e -> {
//            Intent i =  new Intent(LoginActivity.this, SignUp.class);
//            startActivityForResult(i, 1);
//
//        });
//
//        findViewById(R.id.sign_in).setOnClickListener(e -> {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Authenticating ......");
//            if (checkFields()){
//
//                progressDialog.show();
//
//                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
//                            if (task.isSuccessful()){
//                                ROOT_UID = auth.getUid();
//                                Intent fragment = new Intent(this, NavApp.class);
//                                startActivity(fragment);
//                                progressDialog.dismiss();
//                                finish();
//                            }else{
//                                Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
//
//                            }
//
//                        });
//
//
//
//            }else{
//
//                Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
//            }
//
//        });
//    }
//
//    public boolean checkFields(){
//        email = ((EditText)findViewById(R.id.email)).getText().toString();
//        password = ((EditText)findViewById(R.id.password)).getText().toString();
//
//        if (email.trim().length() == 0 || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            errMsg = "Invalid Mail";
//            return false;
//        }
//
//        if (password.trim().length() == 0 ){
//            errMsg = "Fill password";
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == 1){
//            Intent fragment = new Intent(LoginActivity.this, NavApp.class);
//            startActivity(fragment);
//            finish();
//        }
//    }
}
