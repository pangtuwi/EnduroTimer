package com.cloudarchery.endurotimer;

/**
 * Created by paulwilliams on 02/01/16.
 */

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CloudData {
    Context myContext;
    Firebase myFirebaseRef;
    Firebase connectedRef;
    boolean connected = false;
    boolean eventLoaded = false;
    String eventName = "";
    long eventDateMs = 0;
    String eventDate = "";
    Map<String, Object> users;
    Map<String, Object> event;
    static JSONArray stages;
    String eventID = "ab80993d-74f6-4b39-bd10-cabd05a97442";

    //Local App Listeners
    OnRaceEventUpdatedListener myRaceEventListener;
    OnParticipantTimedListener myParticipantTimeListener;

    CloudData(){
        myContext = MyApp.getAppContext();
        Firebase.setAndroidContext(myContext);
        //myCentralFirebaseRef = new Firebase(myContext.getString(R.string.firebase_central_url));
        //fbQ = new SQLiteFirebaseQueue(myContext);
    }


// - - - - - - - - - - - INTERFACE LISTENER DEFINITIONS - - - - - - - - - - //


    public interface OnRaceEventUpdatedListener {
        public void onRaceEventUpdated();
    } // Listener for when RaceEventData is available or updated

    public interface OnParticipantTimedListener {
        public void onParticipantTimeRegistered (boolean timerSuccess, String participantName, String timeStamp, String stageDescr, boolean stageStart);
    } // Listener for when a participant stage time is Registered


    // - - - - - - - - - - - CLoud Firebase Interface - - - - - - - - - - //

    public void InitialiseCDS() {
        Log.d ("EnduroTimer", "Initialising CDS");
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        myFirebaseRef = new Firebase("https://endurotimer.firebaseio.com/");
        myFirebaseRef.keepSynced(true);

        connectedRef = new Firebase("https://endureotimer.firebaseio.com/.info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d("EnduroTimer","CDS connected");
                } else {
                    Log.d("EnduroTimer","CDS not connected");
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                Log.d("EnduroTimer","Connected Listener for CDS was cancelled");
            }
        });


        myFirebaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users = (HashMap<String, Object>) snapshot.getValue();
                    Log.d("EnduroTimer", users.toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        myFirebaseRef.child("events/"+eventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    eventLoaded = true;
                    Log.d ("EnduroTimer", "Event Loaded");
                    event = (Map<String, Object>) snapshot.getValue();
                    try {
                        eventName = (String)event.get("eventName");
                        myRaceEventListener.onRaceEventUpdated();
                        eventDateMs = (long)event.get("eventStarts");
                        if (eventDateMs > 0) {
                            Date eventDateDate = new Date(eventDateMs);
                            String DATE_FORMAT_NOW = "EEEE dd MMMM yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                            eventDate = sdf.format(eventDateDate);
                            Log.d("EnduroTimer", "Event date of "+ eventDateMs + " converted to "+eventDate);
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Data For Event (CloudData)");
                    }

                }
            }
            @Override public void onCancelled(FirebaseError error) { }
        });

        myFirebaseRef.child("events/"+eventID+"/stages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    List stagesList = (List) snapshot.getValue();
                    try {
                        stages = new JSONArray(stagesList);
               /*     if (usersMap != null) try {
                        Iterator x = usersJSON.keys();
                        while (x.hasNext()) {
                            String thisUSerID = (String) x.next();
                            //usersJSONArray.put(usersJSON.get(thisUSerID));

                            JSONObject thisUserJSON = usersJSON.getJSONObject(thisUSerID);
                            SQLiteRounds db = new SQLiteRounds(myContext);
                            JSONObject thisUserStatusJSON = thisUserJSON.getJSONObject("status");
                            JSONArray thisUserDataJSON = thisUserJSON.getJSONArray("data");
                            String name = thisUserJSON.getString("name");
                            Long updatedAt = thisUserJSON.getLong("updatedAt");
                            //Log.d("CloudArchery", "Downloading updated Round Data");
                            db.updateLocalRoundwithScores(thisUSerID, roundID, thisUserStatusJSON, thisUserDataJSON, name, updatedAt);
                            sortedUsersJSONArray = db.getRoundUsersJSONArray(roundID);
                            myScoreListener.onScoreUpdated(sortedUsersJSONArray);
                        }
                        */


                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Array (CloudData)");
                    }

                }
            }
            @Override public void onCancelled(FirebaseError error) { }
        });


    }

    // - - - - - - - - - - - - - -  DATA Functions - - - - - - - - - - - - - //

    //getStageName : returns the stage description name for a given stageID
    public String getStageName (int stageID) {
        String stageName = "";
        JSONObject thisStage;
        try {
            thisStage = (JSONObject) stages.get(stageID);
            stageName = thisStage.getString("stageName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stageName;
    } //getStageName


    //isRaceParticipant : checks if the UUID of the participant is in the current race
    public boolean isRaceParticipant (String participantUUID) {
        boolean isParticipant = false;
        try {
            Map <String, Object> participants = (Map <String, Object>) event.get("participants");
            isParticipant = participants.containsKey(participantUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isParticipant;
    } //isRaceParticipant


    //getParticipantName : returns name for given participantUUID
    public  String getParticipantName (String participantUUID) {
        String participantName = "";
        try {
            Map <String, Object> participants = (Map <String, Object>) event.get("participants");
            boolean isParticipant = participants.containsKey(participantUUID);
            if (isParticipant) {
                Map <String, Object> participant = (Map<String, Object>)participants.get(participantUUID);
                participantName = (String) participant.get("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return participantName;
    }//getParticipantName


    //LogTime : Registers a captured NFC tag as a start or stop time for a given stage
    public void setStageTime (String participantUUID, int stageID, long loggedTimeMs, boolean isStart) {
        String startString;
        if (isStart) {startString = "start";} else {startString = "finish";}
        String stageDescription = startString + " of "+ getStageName(stageID);

        Date loggedDateDate = new Date(loggedTimeMs);
        String DATE_FORMAT_NOW = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String loggedTimeString = sdf.format(loggedDateDate);

        if (isRaceParticipant(participantUUID)) {
            String participantName = getParticipantName (participantUUID);
            Map <String, Object> timeMap = new HashMap<String, Object>();
            timeMap.put(startString, loggedTimeMs);

            myFirebaseRef.child("events/"+eventID+"/participants/"+participantUUID+"/stageTimes/"+stageID).updateChildren(timeMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                    } else {
                    }
                }
            });

            myFirebaseRef.child("events/"+eventID+"/stages/"+stageID+"/stageTimes/"+participantUUID).updateChildren(timeMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                    } else {
                    }
                }
            });

            myParticipantTimeListener.onParticipantTimeRegistered(true, participantName , loggedTimeString, stageDescription, isStart);
        } else {
            myParticipantTimeListener.onParticipantTimeRegistered(false, "", "", "", false);
        }
    } //logTime
}


//ToDo : Add event history to database