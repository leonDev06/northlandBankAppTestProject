package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityRegistration extends AppCompatActivity implements View.OnClickListener{
    Button BUTTON_GO_TO_LOGIN, BUTTON_REGISTER;
    LoginManager loginManager;
    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        BUTTON_GO_TO_LOGIN = (Button) findViewById(R.id.button_goToLogin);
        BUTTON_GO_TO_LOGIN.setOnClickListener(this);
        BUTTON_REGISTER = (Button) findViewById(R.id.BUTTON_REGISTER);
        BUTTON_REGISTER.setOnClickListener(this);

        loginManager = new LoginManager(this);
        loginManager.initializeWidgets();

        navigator = new Navigator(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_goToLogin){
            navigator.redirectTo(ActivityLogin.class, true);
        }
        if(view.getId()==R.id.BUTTON_REGISTER){
            loginManager.verifyRegistration();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase){
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);

        super.attachBaseContext(newBase);
    }
}