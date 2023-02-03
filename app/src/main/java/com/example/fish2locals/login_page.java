package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Models.Users;
import Models.Wallets;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class login_page extends AppCompatActivity {

    private TextView tv_signUp, tv_loginBtn, tv_forgotPassword;
    private EditText et_username, et_password;

    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference userDatabase, walletDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");

        setRef();
        clicks();


    }

    private void clicks() {
        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_page.this, sign_up_page.class);
                startActivity(intent);
            }
        });

        tv_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText resetMail = new TextInputEditText(view.getContext());
                resetMail.setPadding(24, 8, 8, 8);


                AlertDialog.Builder pwResetDialog = new AlertDialog.Builder(view.getContext());
                pwResetDialog.setTitle("Reset Password?");
                pwResetDialog.setMessage("Please enter your email to reset password.");
                pwResetDialog.setView(resetMail);

                pwResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String email = resetMail.getText().toString();

                        if (TextUtils.isEmpty(email))
                        {
                            Toast.makeText(login_page.this, "Email is Required", Toast.LENGTH_SHORT).show();

                        }
                        else if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        {
                            Toast.makeText(login_page.this, "Invalid format", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(login_page.this, "Please check your email to reset your password.", Toast.LENGTH_LONG).show();

                                    new SweetAlertDialog(login_page.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Request has been Sent")
                                            .setContentText("Please check your email to reset your password.")
                                            .show();

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(login_page.this,  "The email is not registered", Toast.LENGTH_LONG).show();
                                }
                            });

                        }


                    }
                });

                pwResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                pwResetDialog.create().show();
            }
        });

    }

    private void validate() {

        String email = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(email))
        {
            et_username.setError("Email is Required");

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            et_username.setError("Incorrect Email Format");
        }
        else if (TextUtils.isEmpty(password))
        {

            et_password.setError("Password is Required");
            return;
        }
        else
        {
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        String myUserId = user.getUid();

                        generateMyUserData(myUserId);
//                                if(user.isEmailVerified())
//                                {
//                                    Toast.makeText(login_page.this, "Login Successfully", Toast.LENGTH_SHORT).show();
//
//                                    if(!(userType == null))
//                                    {
//                                        if(userType.equals("guest"))
//                                        {
//                                            Toast.makeText(login_page.this, "Guest Login", Toast.LENGTH_SHORT).show();
//                                        }
//                                        else
//                                        {
//                                            startActivity(new Intent(getApplicationContext(), homepage.class));
//                                        }
//                                    }
//                                    else
//                                    {
//                                        startActivity(new Intent(getApplicationContext(), homepage.class));
//                                    }
//
//                                }
//                                else
//                                {
//                                    user.sendEmailVerification();
//
//                                    Toast.makeText(login_page.this, "Please check your email to verify your account.", Toast.LENGTH_SHORT).show();
//                                    new SweetAlertDialog(login_page.this, SweetAlertDialog.ERROR_TYPE)
//                                            .setTitleText("Account not verified.")
//                                            .setContentText("Please check your email " +
//                                                    "\nto verify your account.")
//                                            .show();
//                                }

                    } else {
                        Toast.makeText(login_page.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void generateMyUserData(String myUserId) {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                if(users != null)
                {

                    checkIfWalletExist(myUserId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkIfWalletExist(String myUserId) {

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Toast.makeText(login_page.this, "Welcome, You are now logged in!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login_page.this, homepage.class);
                    startActivity(intent);
                    finish();


                }
                else
                {
                    createWallet(myUserId);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createWallet(String myUserId) {

        double i = 0;

        Wallets wallets = new Wallets(myUserId, i);

        walletDatabase.push().setValue(wallets).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(login_page.this, "Welcome, You are now logged in!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login_page.this, homepage.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void setRef() {
        tv_signUp = findViewById(R.id.tv_signUp);
        tv_loginBtn = findViewById(R.id.tv_loginBtn);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        fAuth = FirebaseAuth.getInstance();
    }
}