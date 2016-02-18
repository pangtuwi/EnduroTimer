package com.cloudarchery.endurotimer;

/**
 * Created by paulwilliams on 02/01/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CloudData {
    Context myContext;

    SharedPreferences sharedPreferences;
    Firebase myFirebaseRef;
    Firebase connectedRef;
    boolean connected = false;
    boolean eventLoaded = false;
    String eventName = "";
    long eventDateMs = 0;
    String eventDate = "";
    Map<String, Object> users;
    List<Object> events;
    Map<String, Object> event;
    List<Object> entries;
    static JSONArray stages;
    //String eventID = "ab80993d-74f6-4b39-bd10-cabd05a97442";
    String eventID = "";
    boolean hasEventID = false;

    //Local App Listeners
    OnRaceEventUpdatedListener myRaceEventListener;
    OnParticipantTimedListener myParticipantTimeListener;
    OnNoEventIDListener myNoEventIDListener;
    OnEventsListUpdatedListener myEventsListUpdatedListener;

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

    public interface OnNoEventIDListener {
        public void onNoEventIDFound ();
    } // Listener for when a user needs to get an EventID

    public interface OnEventsListUpdatedListener {
        public void onEventsListUpdated ();
    } // Listener for when a user needs to get an EventID

    // - - - - - - - - - - - CLoud Firebase Interface - - - - - - - - - - //

    public void InitialiseCDS() {
        Log.d ("EnduroTimer", "Initialising CDS");

        sharedPreferences = myContext.getSharedPreferences(myContext.getString(R.string.shared_prefs_KEY), myContext.MODE_PRIVATE);
        eventID = sharedPreferences.getString(myContext.getString(R.string.shared_prefs_string_EVENTID), "");

        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        myFirebaseRef = new Firebase("https://endurotimer.firebaseio.com/");
        myFirebaseRef.keepSynced(true);

        if ((eventID.equals("")) || (eventID == null)) {
            //set callback to get eventID
            if (myNoEventIDListener !=  null) {
                myNoEventIDListener.onNoEventIDFound();
            } else {
                hasEventID = false;
            }
        } else {
            hasEventID = true;
            loadEvent(eventID);
            Log.d("EnduroTimer", "Loaded Event with ID: " + eventID);
        }



        connectedRef = new Firebase("https://endurotimer.firebaseio.com/.info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d("EnduroTimer", "CDS connected");
                    if (!eventID.equals("")) loadEvent(eventID);
                } else {
                    Log.d("EnduroTimer", "CDS not connected");
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                Log.d("EnduroTimer", "Connected Listener for CDS was cancelled");
            }
        });

        myFirebaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users = (HashMap<String, Object>) snapshot.getValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        events = new ArrayList<Object>();
        myFirebaseRef.child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> eventsMap = (HashMap<String, Object>) snapshot.getValue();

                    Iterator it = eventsMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        events.add(pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException
                    }

                    if (myEventsListUpdatedListener != null) myEventsListUpdatedListener.onEventsListUpdated();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

    } //initialiseCDS

    private void loadEvent(String newEventID){
        eventID =  newEventID;

        //Save locally
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(myContext.getString(R.string.shared_prefs_string_EVENTID), eventID);
        e.commit();
        Log.d("EnduroTimer", "Saved EventID : "+eventID);

        myFirebaseRef.child("eventData/"+eventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    eventLoaded = true;
                   // Log.d ("EnduroTimer", "Event Loaded");
                    event = (Map<String, Object>) snapshot.getValue();
                    try {
                        eventName = (String)event.get("eventName");
                        eventDateMs = (long)event.get("eventStartsAt");
                        if (eventDateMs > 0) {
                            Date eventDateDate = new Date(eventDateMs);
                            String DATE_FORMAT_NOW = "EEEE dd MMMM yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                            eventDate = sdf.format(eventDateDate);
                           // Log.d("EnduroTimer", "Event date of "+ eventDateMs + " converted to "+eventDate);
                        }
                        Object entriesObj = event.get("participants");

                        entries = new ArrayList<Object>();
                        Iterator it = ((HashMap) entriesObj).entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            entries.add(pair.getValue());
                            it.remove(); // avoids a ConcurrentModificationException
                        }

                        if (myRaceEventListener != null) myRaceEventListener.onRaceEventUpdated();

                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Data For Event (CloudData)");
                    }

                }
            }
            @Override public void onCancelled(FirebaseError error) { }
        });

        myFirebaseRef.child("eventData/"+eventID+"/stages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    List stagesList = (List) snapshot.getValue();
                    try {
                        stages = new JSONArray(stagesList);

                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Array (CloudData)");
                    }

                }
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }//loadEvent

    public void changeEvent(int eventNo){
        //changes to new event (if new event selected)
        String thisEventID = "";
        Map <String, Object> thisEvent = (HashMap) events.get(eventNo);
        if (thisEvent.containsKey("id")){
            thisEventID = (String) thisEvent.get("id");
        }
        if (!thisEventID.equals(eventID)){
            eventID = thisEventID;
            loadEvent(eventID);
        }

    } //changeEvent

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

            Map <String, Object> reverseTimeMap = new HashMap<String, Object>();
            reverseTimeMap.put(""+loggedTimeMs, startString);

            myFirebaseRef.child("events/"+eventID+"/participants/"+participantUUID+"/stageTimes/"+stageID).updateChildren(timeMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                    } else {
                    }
                }
            });

            myFirebaseRef.child("events/"+eventID+"/stages/"+stageID+"/stageTimes/"+participantUUID).updateChildren(reverseTimeMap, new Firebase.CompletionListener() {
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