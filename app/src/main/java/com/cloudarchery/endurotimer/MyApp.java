package com.cloudarchery.endurotimer;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by paulwilliams on 17/03/15.
 */
public class MyApp extends Application {

    private static Context mContext;
    private boolean timingAllowed;
    private boolean timingActive;
    private int stageID;
    private boolean stageIsSelected;
    private boolean timingStart = true;
    private static boolean activityVisible;
    public static int NFCMode = 0;   //0 = off, 1 = read, 2 = write

    public static CloudData CDS;
    public List <String> timerHistoryList;
    public String selectedEntrantID;


    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.mContext = getApplicationContext(); //this;
        timingAllowed = false;
        timingActive = false;
        stageIsSelected = false;
        timerHistoryList = new ArrayList<>();
        CDS = new CloudData();
        Log.d("EnduroTimer", "CDS Initilise Running");
        CDS.InitialiseCDS();
        Log.d("EnduroTimer", "CDS Initilise Run Finished");
    }


    public static Context getAppContext(){
        return MyApp.mContext;
    }

    public void startTiming () { timingActive = true;        NFCMode = 1;}
    public void stopTiming () { timingActive = false;         NFCMode = 0;}
    public boolean timingIsActive () {return timingActive;}

    public void setStageID (int newStageNo) {
        stageID = newStageNo;
        stageIsSelected = true;
    }
    public boolean hasStageID() {return stageIsSelected;}
    public void setStageSelectedOff(){stageIsSelected = false;}
    public int getStageID () {return stageID;}

    public void setTimingStart() {
        timingStart = true;
    }
    public void setTimingFinish() {
        timingStart = false;

    }
    public boolean timingStart() {return timingStart;}

    public void addHistoryListItem (String newHistoryItem) {
        timerHistoryList.add(newHistoryItem);
    }


    public boolean isActivityVisible() {
        Log.d("EnduroTimer", "activity status requested : is currently "+ activityVisible);
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
        Log.d("EnduroTimer", "activity Resumed");
    }

    public static void activityPaused() {
        activityVisible = false;
        Log.d("EnduroTimer", "activity Paused");

    }

    public static void setNFCMode(int newMode){
        NFCMode = newMode;
    }



}
