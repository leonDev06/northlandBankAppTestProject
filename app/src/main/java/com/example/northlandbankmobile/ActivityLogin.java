package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityLogin extends AppCompatActivity{
    //Widgets
    private Button buttonLogin, buttonGoToRegistration;

    //Helper Classes
    private LoginManager loginManager;
    private Navigator navigator;

    //Key for passing data to Enter Pin
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "ActivityLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Database
        Database.initDatabase();

        //Initialize Login Manager and other helper classes
        loginManager = new LoginManager(this);
        loginManager.initializeWidgets();
        navigator = new Navigator(this);

        //Redirect to Enter Pin if there's currently a logged-in user
        if(Database.getCurrentlyLoggedInUserFile().getAbsoluteFile().exists()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }

        //Clickable Buttons
        buttonLogin = findViewById(R.id.BUTTON_LOGIN);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginManager.verifyLogin()){
                    navigator.redirectTo(ActivityHome.class, true);
                }
            }
        });
        buttonGoToRegistration = findViewById(R.id.BUTTON_GO_TO_REGISTRATION);
        buttonGoToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityRegistration.class);
            }
        });
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