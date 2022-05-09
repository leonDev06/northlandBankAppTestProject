package com.example.northlandbankmobile;

import android.app.Activity;
import android.content.Intent;

public class Navigator {
    //Variables
    private Intent intent;
    private Activity activity;
    private boolean isGoingToAnotherActivity;

    //Constructors
    public Navigator(Activity activity){
        this.activity=activity;
        activity.getApplicationContext();
        intent = new Intent();
        isGoingToAnotherActivity=true;
    }
    public Navigator(){

    }
    //Getters and Setters
    public Intent getIntent() {
        return intent;
    }
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public boolean isGoingToAnotherActivity() {
        return isGoingToAnotherActivity;
    }
    public void setGoingToAnotherActivity(boolean goingToAnotherActivity) {
        isGoingToAnotherActivity = goingToAnotherActivity;
    }

    public void redirectTo(Class destinationClass){
        this.intent.setClass(activity, destinationClass);
        isGoingToAnotherActivity=true;
        activity.startActivity(intent);

    }
    public void redirectTo(Class destinationClass, boolean finishAct){
        this.intent.setClass(activity, destinationClass);
        isGoingToAnotherActivity=true;
        activity.startActivity(intent);
        if(finishAct){
            activity.finish();
        }
    }

    public void putExtra(String name, String s){
        intent.putExtra(name, s);
    }
}
