package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivityLogin extends AppCompatActivity{
    //Widgets
    private Button btnLogin, btnGoToRegistration, btnShowPassword;
    private EditText mPassword;

    //Helper Classes
    private LoginManager loginManager;
    private Navigator navigator;

    //For showing/hiding password
    private boolean isPasswordVisible;

    //Key for passing data to Enter Pin
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "ActivityLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Database. Connecting the Application Context
        Database.onDatabaseFirstCreate(getApplicationContext());

        //Initialize Login Manager and other helper classes
        loginManager = new LoginManager(this);
        loginManager.initializeWidgets();
        navigator = new Navigator(this);

        //Initialize password EditText (Pulled out of LoginManager to manage show/hide password)
        mPassword = loginManager.getPasswordLogin();

        //Redirect to Enter Pin if there's currently a logged-in user
        if(Database.accessCurrentlyLoggedInUserFile().getAbsoluteFile().exists()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }

        //Clickable Buttons
        btnShowPassword = findViewById(R.id.showPassword);
        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordVisible){
                    isPasswordVisible=false;
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnShowPassword.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24);
                }else{
                    isPasswordVisible=true;
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnShowPassword.setBackgroundResource(R.drawable.ic_baseline_visibility_24);
                }
            }
        });
        btnLogin = findViewById(R.id.BUTTON_LOGIN);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.attemptLogin();
                if(loginManager.isLoginSuccess()){
                    navigator.redirectTo(ActivityHome.class, true);
                }
            }
        });
        btnGoToRegistration = findViewById(R.id.BUTTON_GO_TO_REGISTRATION);
        btnGoToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityRegistration.class);
            }
        });
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    //Makes sure that fonts are non-scalable
    @Override
    protected void attachBaseContext(Context newBase){
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }
}