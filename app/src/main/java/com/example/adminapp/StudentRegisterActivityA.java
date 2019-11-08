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

public class StudentRegisterActivityA extends AppCompatActivity {

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
    private EditText rollNoInput;
    private EditText departmentInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirm_passwordInput;
    private CheckBox show_hide_password;
    private Button registerStudentBtn;

    private ProgressDialog loadingBar;
    AlertDialog.Builder alertDialoge;

    private FirebaseAuth mAuth;

    StudentData studentData;
    DatabaseReference studentDataRef;
    Animation shakeAnimation;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registera);

        shakeAnimation= AnimationUtils.loadAnimation(this,R.anim.shake);
        linearLayout = findViewById(R.id.linear_layout_id5);

        alertDialoge = new AlertDialog.Builder(this);
        alertDialoge.setTitle("Register Student");
        alertDialoge.setMessage("Student Register Successfully");
        alertDialoge.setCancelable(false);
        alertDialoge.setIcon(R.drawable.emoji);
        alertDialoge.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialoge.create();

        nameInput = findViewById(R.id.name_input_std_id);
        rollNoInput = findViewById(R.id.roll_no_input_id);
        departmentInput = findViewById(R.id.department_input_id);
        emailInput = findViewById(R.id.email_input_std_id);
        passwordInput = findViewById(R.id.password_input_std_id);
        confirm_passwordInput = findViewById(R.id.confirm_password_std_id);
        show_hide_password = findViewById(R.id.show_hide_pass_std_id);
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
        registerStudentBtn = findViewById(R.id.student_register_button_id);
        registerStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterStudentFunction();
            }
        });

        loadingBar = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        studentData = new StudentData();
        studentDataRef = FirebaseDatabase.getInstance().getReference().child("StudentData");

    }

    private void saveDataToFirebase(){
        String Name = nameInput.getText().toString().trim();
        int RollNo = Integer.parseInt(rollNoInput.getText().toString().trim());
        String Email = emailInput.getText().toString();
        String Password = passwordInput.getText().toString();
        String Department = departmentInput.getText().toString().trim();

        studentData.setStudentName(Name);
        studentData.setStudentRollNo(RollNo);
        studentData.setStudentEmail(Email);
        studentData.setStudentPassword(Password);
        studentData.setStudentDepartment(Department);
        studentData.setStatus("Student");

        studentDataRef.push().setValue(studentData);

    }

    private boolean validateEmail() {
        String Email = emailInput.getText().toString().trim();
        if (Email.isEmpty()) {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toasty.error(this, "Please Enter a valid Email Address", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        } else {
            return true;
        }
    }
    private boolean validatePassword() {
        String Password = passwordInput.getText().toString().trim();
        if (Password.isEmpty()) {
            Toasty.info(this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(Password).matches()) {
            Toasty.warning(this, "Password too weak!", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        } else {
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
    private boolean validateRollNo(){
        String RollNo = rollNoInput.getText().toString().trim();
        if (RollNo.isEmpty())
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
    private boolean validateDepartment(){
        String Department = departmentInput.getText().toString().trim();
        if (Department.isEmpty())
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
            Toasty.error(this, "Password does not match", Toast.LENGTH_SHORT).show();
            linearLayout.startAnimation(shakeAnimation);
            return false;
        }
        else
        {
            return true;
        }
    }

    private void RegisterStudentFunction() {
        if (!validateEmail() | !validatePassword()
                | !validateName() | !validateRollNo()
                | !validateDepartment() | !validateConfirmPassword()
                | !validatePasswordMatch())
        {
            return;
        }

        loadingBar.setTitle("Register Student");
        loadingBar.setMessage("Please wait! while we register your data");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        registerStudentBtn.setEnabled(false);

        String Email = emailInput.getText().toString().trim();
        String Password = passwordInput.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(StudentRegisterActivityA.this, "Student Registered Successfully", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            registerStudentBtn.setEnabled(true);
                            saveDataToFirebase();
                            nameInput.setText("");
                            rollNoInput.setText("");
                            departmentInput.setText("");
                            emailInput.setText("");
                            passwordInput.setText("");
                            confirm_passwordInput.setText("");
                            alertDialoge.show();

                        } else {
                            Toasty.error(StudentRegisterActivityA.this, "Failed to Register Student", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            registerStudentBtn.setEnabled(true);
                            linearLayout.startAnimation(shakeAnimation);
                            new AlertDialog.Builder(StudentRegisterActivityA.this)
                                    .setTitle("Register Student")
                                    .setMessage("Error Registering your Student")
                                    .setIcon(R.drawable.emojisad)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                Toast.makeText(this, "You are already in Student Register Activity", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.register_drivers:
                Intent intent = new Intent(StudentRegisterActivityA.this, DriverRegisterActivityA.class);
                startActivity(intent);
                return true;

            case R.id.track_bus:
                if (!gpsEnabled())
                {
                    return false;
                }
                Intent intent1 = new Intent(StudentRegisterActivityA.this, MapsActivityA.class);
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
            Toasty.error(this, "Gps not Supported", Toast.LENGTH_LONG).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toasty.info(this, "Please Enable your GPS", Toast.LENGTH_LONG).show();
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

//    public void checkEmailAvailability(){
//        mAuth.fetchProvidersForEmail(emailInput.getText().toString())
//                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
//                        boolean check = !task.getResult().getProviders().isEmpty();
//                        if (!check)
//                        {
//                            Toast.makeText(StudentRegisterActivityA.this, "email not found", Toast.LENGTH_SHORT).show();
//                        }else
//                        {
//                            Toast.makeText(StudentRegisterActivityA.this, "Email already present", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
}
