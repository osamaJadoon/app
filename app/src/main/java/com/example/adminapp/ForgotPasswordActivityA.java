package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ForgotPasswordActivityA extends AppCompatActivity {

    private EditText forgotEmail;
    private Button submit;
    private TextView backToLogin;

    private FirebaseAuth mAuth;

    ProgressDialog loadingBar;

    AlertDialog.Builder alertDialoge;
    boolean doubleTap = false;
    Animation shakeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passworda);

        shakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake);

        alertDialoge = new AlertDialog.Builder(this);
        alertDialoge.setTitle("Reset Password");
        alertDialoge.setMessage("Please check your email, we just send a link to reset your password");
        alertDialoge.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialoge.create();

        forgotEmail = findViewById(R.id.forgot_email_idd);
        submit = findViewById(R.id.submit_btn_idd);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetFunction();
            }
        });
        backToLogin = findViewById(R.id.backtologin_id);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivityA.this, LoginActivityA.class);
                startActivity(intent);
                CustomIntent.customType(ForgotPasswordActivityA.this,"right-to-left");
            }
        });
        mAuth = FirebaseAuth.getInstance();
    }


    private void ResetFunction() {
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Reset Password");
        loadingBar.setMessage("Please wait!");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        submit.setEnabled(false);

        String Email = forgotEmail.getText().toString().trim();
        if (Email.isEmpty())
        {
            Toasty.info(this, "Enter your Email ID", Toast.LENGTH_LONG).show();
            loadingBar.dismiss();
            forgotEmail.startAnimation(shakeAnimation);
            submit.setEnabled(true);
        }else {


            mAuth.sendPasswordResetEmail(Email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.success(ForgotPasswordActivityA.this, "Email Send", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                alertDialoge.show();
                            } else {
                                Toasty.error(ForgotPasswordActivityA.this, "Failed to send Email", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                submit.setEnabled(true);
                            }
                        }
                    });
        }
    }
}
