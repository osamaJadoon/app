package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class LoginActivityA extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private CheckBox showHidePassword;
    private TextView forgotPassword;
    private Button loginBtn;

    private ProgressDialog loadingBar;


    private FirebaseAuth mAuth;

    Animation mShakeAnimation;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logina);

        linearLayout = findViewById(R.id.linear_layout_id6);
        mShakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake);

        email = findViewById(R.id.email_login_id);
        password = findViewById(R.id.password_login_id);
        showHidePassword = findViewById(R.id.login_show_hide_pass_id);
        showHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    showHidePassword.setText("Hide Password");
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    showHidePassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    showHidePassword.setText("Show Password");
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivityA.this, ForgotPasswordActivityA.class);
                startActivity(intent);
                CustomIntent.customType(LoginActivityA.this,"left-to-right");
            }
        });
        loginBtn = findViewById(R.id.login_btn_id);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFunction();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean validateEmail(){
        String Email = email.getText().toString().trim();
        if (Email.isEmpty())
        {
            Toasty.info(this, "Field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean validatePassword(){
        String Password = password.getText().toString().trim();
        if (Password.isEmpty())
        {
            Toasty.info(this, "Field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        else
        {
            return true;
        }
    }

    private void LoginFunction() {
        if (!isOnline())
        {
            linearLayout.startAnimation(mShakeAnimation);
            return;
        }

        if (!validateEmail() | !validatePassword())
        {
            linearLayout.startAnimation(mShakeAnimation);
            return;
        }

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Login Admin");
        loadingBar.setMessage("Please wait!");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        loginBtn.setEnabled(false);

        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            Toasty.success(LoginActivityA.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivityA.this, AdminPanelActivityA.class);
                            startActivity(intent);
                            CustomIntent.customType(LoginActivityA.this,"left-to-right");
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingBar.dismiss();
                            loginBtn.setEnabled(true);
                            linearLayout.startAnimation(mShakeAnimation);
                            Toasty.error(LoginActivityA.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            //Toast.makeText(this, "Yor are online", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toasty.warning(this, "You are Offline", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    //back press function start here

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("EXIT?")
                .setIcon(R.drawable.exit)
                .setMessage("Are u sure you want to exit")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    //back press ends.....
}
