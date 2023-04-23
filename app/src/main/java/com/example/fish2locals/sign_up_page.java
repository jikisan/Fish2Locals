package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class sign_up_page extends AppCompatActivity {

    private TextView tv_signIn, tv_signUpBtn, tv_terms;
    private EditText et_firstName, et_lastName, et_contactNumber, et_username, et_password_signup,
            et_confirmPassword;
    private CheckBox checkBox_terms;

    private DatabaseReference userDatabase;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef(); // Initialize reference of UI ID's
        clicks(); // buttons users can click


    }

    private void clicks() {
        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sign_up_page.this, login_page.class);
                startActivity(intent);
            }
        }); //click to go to login page

        tv_signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });// click to submit form to register
        
        
    }

    private void validate() {

        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String contactNum = et_contactNumber.getText().toString();
        String username = et_username.getText().toString();
        String password = et_password_signup.getText().toString();
        String confirmPass = et_confirmPassword.getText().toString();


        if (TextUtils.isEmpty(firstName))
        {
            et_firstName.setError("This field is required");
        }
        else if (TextUtils.isEmpty(lastName))
        {
            et_lastName.setError("This field is required");
        }
        else if (TextUtils.isEmpty(contactNum))
        {
            et_contactNumber.setError("This field is required");
        }
        else if (contactNum.length() != 11)
        {
            et_contactNumber.setError("Contact number must be 11 digit");
        }
        else if (TextUtils.isEmpty(username) )
        {
            et_username.setError("This field is required");
        }
        else if ( !Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            et_username.setError("Incorrect Email Format");
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 8)
        {
            Toast.makeText(this, "Password must be 8 or more characters", Toast.LENGTH_LONG).show();

        }
        else if (!isValidPassword(password))
        {
            new SweetAlertDialog(sign_up_page.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!.")
                    .setContentText("Please choose a stronger password. Try a mix of letters, numbers, and symbols.")
                    .show();

        }
        else if (TextUtils.isEmpty(confirmPass))
        {
            Toast.makeText(this, "Please confirm the password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPass))
        {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        }
        else if(!checkBox_terms.isChecked())
        {
            Toast.makeText(this, "Must agree with the Terms and Conditions to proceed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            signUp(firstName, lastName, contactNum, username, password);
        }
    }

    private void signUp(String firstName, String lastName, String contactNum, String username, String password) {

        new AlertDialog.Builder(sign_up_page.this)
                .setTitle("SIGN UP")
                .setMessage("Please make sure all information entered are correct")
                .setCancelable(true)
                .setPositiveButton("SignUp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressDialog = new ProgressDialog(sign_up_page.this);
                        progressDialog.setTitle("Signing up");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        createAuthAccount(firstName, lastName, contactNum, username, password);

                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                    }
                })
                .show();
    }

    //create account method
    private void createAuthAccount(String firstName, String lastName, String contactNum, String username, String password) {

        fAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {

                            savingDataToDatabase(firstName, lastName, contactNum, username, password);

                        }

                    }
                });


            }
        });
    }

    //save database all data
    private void savingDataToDatabase(String firstName, String lastName, String contactNum,
                                      String username, String password) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid().toString();
        String imageUrl = "";
        String imageName = "";
        long rating = 0;
        boolean hasSellerAccount = false;
        boolean sellerMode = false;

        Users users = new Users(userID, firstName, lastName, contactNum, username, password,
                imageUrl, imageName, rating, hasSellerAccount, sellerMode);

        userDatabase.child(userID).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    fAuth.signOut();
//                    user.sendEmailVerification();

                    new SweetAlertDialog(sign_up_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Account Created.")
                            .setConfirmButton("Proceed to Login", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(getApplicationContext(), login_page.class));
                                }
                            })
                            .show();
                }
                else
                {
                    Toast.makeText(sign_up_page.this, "Creation Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

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
        tv_signIn = findViewById(R.id.tv_signIn);
        tv_signUpBtn = findViewById(R.id.tv_signUpBtn);
        tv_terms = findViewById(R.id.tv_terms);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_contactNumber = findViewById(R.id.et_contactNumber);
        et_username = findViewById(R.id.et_username);
        et_password_signup = findViewById(R.id.et_password_signup);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);

        checkBox_terms = findViewById(R.id.checkBox_terms);

        fAuth = FirebaseAuth.getInstance();

    }
}