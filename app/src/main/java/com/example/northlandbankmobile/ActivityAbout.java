package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ActivityAbout extends AppCompatActivity {
    //Helper classes
    private Navigator navigator;

    //DATA to pass to 'Enter Pin' to set this class as its return class (Class to redirect to when entering correct pin.)
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivityAbout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Helper Classes
        navigator = new Navigator(this);
        //To ensure Enter Pin Activity doesn't get called everytime onResume is called
        navigator.setGoingToAnotherActivity(true);
    }

    //Overwrite Lifecycle Methods
    @Override
    protected void onResume(){
        super.onResume();

        /*Redirect user to Enter Pin Activity whenever they leave the app
            This is done to provide additional security to users
         */
        if(!navigator.isGoingToAnotherActivity()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
        navigator.setGoingToAnotherActivity(false);
    }
}