package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class DriverRegisterActivityA extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    // "(?=.*[a-z])" +         //at least 1 lower case letter
                    // "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText busNumberInput;
    private EditText confirm_passwordInput;
    private CheckBox show_hide_password;
    private Button registerDriverBtn;

    private ProgressDialog loadingBar;
    AlertDialog.Builder alertDialoge;

    private LinearLayout linearLayout;

    private FirebaseAuth mAuth;
    boolean doubleTap = false;
    DriverData driverData;
    DatabaseReference driverDataRef;
    Animation shakeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registera);

        shakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake);

        linearLayout = findViewById(R.id.linear_layout_id4);

        alertDialoge = new AlertDialog.Builder(this);
        alertDialoge.setTitle("Register Driver");
        alertDialoge.setMessage("Driver Register Successfully");
        alertDialoge.setCancelable(false);
        alertDialoge.setIcon(R.drawable.emoji);
        alertDialoge.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialoge.create();

        nameInput = findViewById(R.id.name_input_id);
        emailInput = findViewById(R.id.email_input_id);
        busNumberInput = findViewById(R.id.busNumber_input_id);

        passwordInput = findViewById(R.id.password_input_id);
        confirm_passwordInput = findViewById(R.id.confirm_password_id);
        show_hide_password = findViewById(R.id.show_hide_pass_admin_id);
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    show_hide_password.setText("Hide Password");
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    confirm_passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirm_passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    show_hide_password.setText("Show Password");
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirm_passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirm_passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        registerDriverBtn = findViewById(R.id.register_driver_button_id);
        registerDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterDriverFunction();
            }
        });

        loadingBar = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        driverData = new DriverData();

        driverDataRef = FirebaseDatabase.getInstance().getReference().child("DriverData");

    }

    private void saveDataToFirebase(){
        String Name = nameInput.getText().toString().trim();
        int BusNumber = Integer.parseInt(busNumberInput.getText().toString().trim());
        String Email = emailInput.getText().toString();
        String Password = passwordInput.getText().toString();

        driverData.setDriverName(Name);
        driverData.setDriverBusNumber(BusNumber);
        driverData.setDriverEmail(Email);
        driverData.setDriverPassword(Password);
        driverData.setStatus("Driver");


        driverDataRef.push().setValue(driverData);

       // Toast.makeText(this, "data saved...", Toast.LENGTH_SHORT).show();
    }

    private boolean validateEmail(){
        String Email = emailInput.getText().toString().trim();
        if (Email.isEmpty())
        {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
        {
            Toasty.info(this, "Please Enter a valid Email Address", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }
    private boolean validatePassword(){
        String Password = passwordInput.getText().toString().trim();
        if (Password.isEmpty())
        {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(Password).matches())
        {
            Toasty.warning(this, "Password too weak!", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }
    private boolean validateName(){
        String Name = nameInput.getText().toString().trim();
        if (Name.isEmpty())
        {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }
    private boolean validateBusNumber(){
        String BusNumber = busNumberInput.getText().toString().trim();
        if (BusNumber.isEmpty())
        {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }
    private boolean validateConfirmPassword(){
        String ConfirmPassword = confirm_passwordInput.getText().toString().trim();
        if (ConfirmPassword.isEmpty())
        {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }
    private boolean validatePasswordMatch(){
        String Password = passwordInput.getText().toString().trim();
        String ConfirmPassword = confirm_passwordInput.getText().toString().trim();
        if (!Password.equals(ConfirmPassword))
        {
            Toasty.error(this, "Password do not match!", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }

    private void RegisterDriverFunction() {

        if (!validateEmail() | !validatePassword() | !validateName()
                | !validateBusNumber() | !validateConfirmPassword() | !validatePasswordMatch())
        {
            return;
        }


        loadingBar.setTitle("Register Driver");
        loadingBar.setMessage("Please wait! while we register your data");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        registerDriverBtn.setEnabled(false);

        String Email = emailInput.getText().toString().trim();
        String Password = passwordInput.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toasty.success(DriverRegisterActivityA.this, "Driver Registered Successfully", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            saveDataToFirebase();
                            registerDriverBtn.setEnabled(true);
                            nameInput.setText("");
                            busNumberInput.setText("");
                            emailInput.setText("");
                            passwordInput.setText("");
                            confirm_passwordInput.setText("");
                            alertDialoge.show();
                        } else
                        {
                            Toasty.info(DriverRegisterActivityA.this, "Failed to Register Driver", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            registerDriverBtn.setEnabled(true);
                            linearLayout.startAnimation(shakeAnimation);
                            new AlertDialog.Builder(DriverRegisterActivityA.this)
                                    .setTitle("Register Driver")
                                    .setIcon(R.drawable.emojisad)
                                    .setMessage("Error Registering your Driver")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .create().show();
                        }

                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aoptions, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.register_students:
                Intent intent = new Intent(DriverRegisterActivityA.this, StudentRegisterActivityA.class);
                startActivity(intent);
                return true;
            case R.id.register_drivers:
                Toast.makeText(this, "You are already in Register Driver Activity", Toast.LENGTH_LONG).show();
                return true;

            case R.id.track_bus:
                if (!gpsEnabled())
                {
                    return false;
                }
                Intent intent1 = new Intent(DriverRegisterActivityA.this, MapsActivityA.class);
                startActivity(intent1);
                CustomIntent.customType(this,"left-to-right");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean gpsEnabled(){
        //***********************GPS start*************
        this.setFinishOnTouchOutside(true);
        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            //Toast.makeText(SignInActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            return true;
        }
        // Todo Location Already on  ... end

        if(!hasGPSDevice(this)){
            Toasty.warning(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toasty.info(this, "Please Enable your GPS", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            // Toast.makeText(SignInActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            return true;
        }
        //*************GPS ENDS******************
    }

    //*********code for on the gps if its off***************************
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
}
