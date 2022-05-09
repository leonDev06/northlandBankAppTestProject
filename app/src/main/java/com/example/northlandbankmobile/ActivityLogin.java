package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{
    Button BUTTON_LOGIN, BUTTON_GO_TO_REGISTRATION;
    LoginManager loginManager;
    Navigator navigator;

    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "ActivityLogin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //Button init
        BUTTON_LOGIN = findViewById(R.id.BUTTON_LOGIN);
        BUTTON_LOGIN.setOnClickListener(this);
        BUTTON_GO_TO_REGISTRATION = findViewById(R.id.BUTTON_GO_TO_REGISTRATION);
        BUTTON_GO_TO_REGISTRATION.setOnClickListener(this);

        loginManager = new LoginManager(this);
        loginManager.initializeVariables();

        navigator = new Navigator(this);

        Database.initDatabase();

        //Redirect to Enter Pin if there's currently a logged-in user
        if(Database.getCurrentlyLoggedInUserFile().getAbsoluteFile().exists()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.BUTTON_LOGIN){
            if(loginManager.verifyLogin()){
                navigator.redirectTo(ActivityHome.class, true);
            }
        }
        if(view.getId()==R.id.BUTTON_GO_TO_REGISTRATION){
            navigator.redirectTo(ActivityRegistration.class);
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