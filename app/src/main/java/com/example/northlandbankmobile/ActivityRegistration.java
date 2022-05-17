package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityRegistration extends AppCompatActivity{
    //Buttons
    private Button mBtnGoToLogin, mBtnRegister;

    //Helper CLasses
    private LoginManager loginManager;
    private Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Construct and Initialize Helper Objects
        loginManager = new LoginManager(this);
        loginManager.initializeWidgets();
        navigator = new Navigator(this);

        //Clickable Buttons
        mBtnGoToLogin = findViewById(R.id.button_goToLogin);
        mBtnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityLogin.class, true);
            }
        });
        mBtnRegister = findViewById(R.id.BUTTON_REGISTER);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginManager.verifyRegistration()) {
                    navigator.redirectTo(ActivityLogin.class);
                    Toast.makeText(getApplicationContext(), "Account Registration Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase){
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);

        super.attachBaseContext(newBase);
    }
}