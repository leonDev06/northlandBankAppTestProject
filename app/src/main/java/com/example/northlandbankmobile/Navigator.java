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

    /*
    Navigator functions
     */
    //Used to navigate from one activity to another.
    //Keeps the previous activity in the activity stack
    public void redirectTo(Class destinationClass){
        this.intent.setClass(activity, destinationClass);
        isGoingToAnotherActivity=true;
        activity.startActivity(intent);

    }
    //Redirect from one activity to another. Destroy activity that called this.
    public void redirectTo(Class destinationClass, boolean finishAct){
        this.intent.setClass(activity, destinationClass);
        isGoingToAnotherActivity=true;
        activity.startActivity(intent);
        if(finishAct){
            activity.finish();
        }
    }
    //Used to pass a String Extra from a previous activity to the target activity
    public void putExtra(String name, String s){
        intent.putExtra(name, s);
    }
}
