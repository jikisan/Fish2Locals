package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class change_password_page extends AppCompatActivity {

    private EditText et_currentPassword, et_newPassword, et_confirmNewPassword;
    private TextView tv_forgotPassword, tv_back, tv_submit;
    private FirebaseUser user;
    private FirebaseAuth fAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fAuth = FirebaseAuth.getInstance();

        setRef();
        clicks();
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText resetMail = new TextInputEditText(view.getContext());
                resetMail.setPadding(24, 8, 8, 8);


                AlertDialog.Builder pwResetDialog = new AlertDialog.Builder(view.getContext());
                pwResetDialog.setTitle("Forgot Password?");
                pwResetDialog.setMessage("Please enter your email to reset password.");
                pwResetDialog.setView(resetMail);

                pwResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String email = resetMail.getText().toString();

                        if (TextUtils.isEmpty(email))
                        {
                            Toast.makeText(change_password_page.this, "Email is Required", Toast.LENGTH_SHORT).show();

                        }
                        else if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        {
                            Toast.makeText(change_password_page.this, "Invalid format", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(change_password_page.this, "Please check your email to reset your password.", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(change_password_page.this,  "Incorrect Email", Toast.LENGTH_LONG).show();
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

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = getIntent().getStringExtra("User Email");
                String currentPassword = et_currentPassword.getText().toString().trim();
                String newPassword = et_newPassword.getText().toString().trim();
                String confirmNewPassword = et_confirmNewPassword.getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword))
                {
                    Toast.makeText(change_password_page.this, "Password is Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(newPassword))
                {
                    Toast.makeText(change_password_page.this, "New Password is Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(confirmNewPassword))
                {
                    Toast.makeText(change_password_page.this, "Please confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (currentPassword.length() < 8)
                {
                    Toast.makeText(change_password_page.this, "Password must be 8 or more characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (newPassword.length() < 8)
                {
                    Toast.makeText(change_password_page.this, "New Password must be 8 or more characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (confirmNewPassword.length() < 8)
                {
                    Toast.makeText(change_password_page.this, "Confirmation Password must be 8 or more characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (isValidPassword(newPassword))
                {
                    Toast.makeText(change_password_page.this, "Please choose a stronger password. Try a mix of letters, numbers, and symbols.", Toast.LENGTH_LONG).show();
                }
                else if (!newPassword.equals(confirmNewPassword))
                {
                    Toast.makeText(change_password_page.this, "Password did not match", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    progressDialog = new ProgressDialog(change_password_page.this);
                    progressDialog.setTitle("Changing password...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, currentPassword);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        updatePassword(newPassword);
                                    }
                                    else
                                    {
                                        et_currentPassword.setError("Incorrect Password");
                                        Toast.makeText(change_password_page.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


    }

    private void updatePassword(String newPassword) {

        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    updatePasswordInDb(newPassword);

                }
                else
                {
                    Toast.makeText(change_password_page.this, "Updating password failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePasswordInDb(String newPassword) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("password", newPassword);

        userDB.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                progressDialog.dismiss();
                fAuth.signOut();

                new SweetAlertDialog(change_password_page.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Password Changed!")
                        .setConfirmButton("Proceed to Login", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                startActivity(new Intent(getApplicationContext(), login_page.class));
                            }
                        })
                        .show();

            }
        });


    }

    private static boolean isValidPassword(String password) {


        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=?!#$%&()*+,./])"
                + "(?=\\S+$).{8,15}$";


        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);

        return m.matches();
    }

    private void setRef() {

        et_currentPassword = findViewById(R.id.et_currentPassword);
        et_newPassword = findViewById(R.id.et_newPassword);
        et_confirmNewPassword = findViewById(R.id.et_confirmNewPassword);

        tv_back = findViewById(R.id.tv_back);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        tv_submit = findViewById(R.id.tv_submit);


    }
}