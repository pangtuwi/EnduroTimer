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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CloudData {
    Context myContext;

    SharedPreferences sharedPreferences;
    Firebase CDSFirebaseRef;
    Firebase EventFirebaseRef;
    Firebase connectedRef;
    boolean connected = false;
    boolean eventLoaded = false;
    String eventName = "";
    long eventDateMs = 0;
    String eventDate = "";
    //Map<String, Object> users;
    List<Object> events;
    Map<String, Object> event;
    JSONArray results;
    List<Object> entriesList;
    Map <String, Object> entriesMap;
    JSONArray stages;
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
        CDSFirebaseRef = new Firebase("https://endurotimer.firebaseio.com/");
       // CDSFirebaseRef.keepSynced(true);

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

      /*  CDSFirebaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users = (HashMap<String, Object>) snapshot.getValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        }); */

        events = new ArrayList<Object>();

        CDSFirebaseRef.child("events").addValueEventListener(new ValueEventListener() {
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
        Log.d("EnduroTimer", "Saved EventID : " + eventID);

        EventFirebaseRef = new Firebase("https://endurotimer.firebaseio.com/eventData/"+eventID);
        EventFirebaseRef.keepSynced(true);
        EventFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    eventLoaded = true;
                    // Log.d ("EnduroTimer", "Event Loaded");
                    event = (Map<String, Object>) snapshot.getValue();
                    try {
                        eventName = (String) event.get("eventName");
                        eventDateMs = (long) event.get("eventStartsAt");
                        if (eventDateMs > 0) {
                            Date eventDateDate = new Date(eventDateMs);
                            String DATE_FORMAT_NOW = "EEEE dd MMMM yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                            eventDate = sdf.format(eventDateDate);
                            // Log.d("EnduroTimer", "Event date of "+ eventDateMs + " converted to "+eventDate);
                        }

                      //  stages = new JSONArray(event.get("stages"));
                        //did this separately as does not come through as JSONArray if done like this

                        entriesMap = (Map<String, Object>) event.get("participants");

                        entriesList = new ArrayList<Object>();
                        Iterator it = ((HashMap) entriesMap).entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            entriesList.add(pair.getValue());
                           // it.remove(); // avoids a ConcurrentModificationException
                        }

                        if (myRaceEventListener != null) myRaceEventListener.onRaceEventUpdated();

                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Data For Event (CloudData)");
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        EventFirebaseRef.child("stages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    List stagesList = (List) snapshot.getValue();
                    try {
                        stages = new JSONArray(stagesList);

                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Array on stages read (CloudData)");
                    }

                }
            }
            @Override public void onCancelled(FirebaseError error) { }
        });



        CDSFirebaseRef.child("events/" + eventID + "/results/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List resultsList = (List) snapshot.getValue();
                    try {
                        results = new JSONArray(resultsList);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Log.e("EnduroTimer", "Error extracting JSON Array on Results read (CloudData)");
                    }

                   // if (myEventsListUpdatedListener != null) myEventsListUpdatedListener.onEventsListUpdated();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
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
            isParticipant = entriesMap.containsKey(participantUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isParticipant;
    } //isRaceParticipant


    //getParticipantName : returns name for given participantUUID
    public  String getParticipantName (String participantUUID) {
        String participantName = "";
        try {
            boolean isParticipant = entriesMap.containsKey(participantUUID);
            if (isParticipant) {
                Map <String, Object> participant = (Map<String, Object>)entriesMap.get(participantUUID);
                participantName = (String) participant.get("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return participantName;
    }//getParticipantName

    //getParticipantRaceNo : returns raceNo for given participantUUID
    public  String getParticipantRaceNo (String participantUUID) {
        String participantRaceNo = "";
        try {
            boolean isParticipant = entriesMap.containsKey(participantUUID);
            if (isParticipant) {
                Map <String, Object> participant = (Map<String, Object>)entriesMap.get(participantUUID);
                participantRaceNo = (String) participant.get("raceNo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return participantRaceNo;
    }//getParticipantRaceNo

    public boolean newParticipantRaceNoOk (String newRaceNo) {
       boolean match = false;
        Iterator it = entriesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map <String, Object> participant = (Map<String, Object>) entriesMap.get(it.next());
            String participantRaceNo = (String) participant.get("raceNo");
            if (newRaceNo.equals(participantRaceNo)) match = true;
        }
        return !match; //no match means no is OK
    } //newParticipantRaceNo

    public void addNewParticipant (String newUUID, String newName, String newRaceNo) {
        Map <String, Object> newParticipant = new HashMap<>();
        newParticipant.put("id", newUUID);
        newParticipant.put("name", newName);
        newParticipant.put("raceNo", newRaceNo);
        entriesMap.put(newUUID, newParticipant);

        CDSFirebaseRef.child("events/"+eventID+"/participants").updateChildren(newParticipant, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                } else {
                }
            }
        });

        EventFirebaseRef.child("participants").updateChildren(newParticipant, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                } else {
                }
            }
        });
    } //addNewParticipant


    //LogTime : Registers a captured NFC tag as a start or stop time for a given stage
    public void setStageTime (String participantUUID, int stageID, long loggedTimeMs, boolean isStart) {
        Log.d("Endurotimer", "Attempting setStageTimer for :"+ participantUUID+".");
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
            reverseTimeMap.put(""+loggedTimeMs, participantUUID);

            EventFirebaseRef.child("participants/" + participantUUID + "/stageTimes/" + stageID).updateChildren(timeMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                    } else {
                    }
                }
            });

            EventFirebaseRef.child("times/" + stageID + "/"+startString+"/").updateChildren(reverseTimeMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                    } else {
                    }
                }
            });

            myParticipantTimeListener.onParticipantTimeRegistered(true, participantName , loggedTimeString, stageDescription, isStart);
        } else {
            Log.d ("EnduroTimer", "Participant Tag not found.");
            myParticipantTimeListener.onParticipantTimeRegistered(false, "", "", "", false);
        }
    } //logTime



    public void updateResults(){
        //parses current data and updates the list of current result standings.
        //Results Structure :
        //
        //TODO: Needs an upDatedAt to avoid continuous unnecessary overwrite
        //final List<Object> participants = new ArrayList<Object>();

        EventFirebaseRef.child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<ParticipantResult> participantResults = new ArrayList<ParticipantResult>();
                    Map<String, Object> stagesMap = (HashMap<String, Object>) snapshot.getValue();
                    Iterator it = stagesMap.entrySet().iterator();
                    while (it.hasNext()) {

                        Map.Entry participant = (Map.Entry) it.next();
                        ParticipantResult participantResult = new ParticipantResult();
                        //  participantResult.participantName = (String) participant.getValue();
                        HashMap thisParticipant = (HashMap) participant.getValue();
                        if (thisParticipant.containsKey("name")) {
                            participantResult.participantName = (String) thisParticipant.get("name");
                        }
                        if (thisParticipant.containsKey("raceNo")) {
                            participantResult.raceNo = (String) thisParticipant.get("raceNo");
                        }
                        if (thisParticipant.containsKey("stageTimes")) {
                            ArrayList thisTimeSet = (ArrayList) thisParticipant.get("stageTimes");
                            long startTime = 0;
                            long finishTime = 0;
                            for (int i = 0; i < stages.length(); i++) {
                                HashMap times = (HashMap) thisTimeSet.get(i);
                                if (times.containsKey("start")) {
                                    startTime = (Long) times.get("start");
                                }
                                if (times.containsKey("finish")) {
                                    finishTime = (Long) times.get("finish");
                                }
                                participantResult.setStageTime(i, startTime, finishTime);
                            }
                            participantResult.calcRaceTime(stages.length());
                        }
                        participantResults.add(participantResult);
                        // results.add(pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException
                    }

                    Collections.sort(participantResults);
                    Map <String, Object> resultsMap = new HashMap<>();
                    Iterator i = participantResults.iterator();
                    int count = 0;
                    while (i.hasNext()) {
                        //Map.Entry pair = (Map.Entry) i.next();
                        ParticipantResult thisPR = (ParticipantResult)i.next();
                        //entriesList.add(pair.getValue());
                        resultsMap.put(""+count, thisPR.asHashMap());
                        i.remove(); // avoids a ConcurrentModificationException
                        count ++;
                    }



                    CDSFirebaseRef.child("events/" + eventID + "/results/").updateChildren(resultsMap, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                            } else {
                            }
                        }
                    });

                    EventFirebaseRef.child("results").updateChildren(resultsMap, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                            } else {
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });





    } //getResults

}
//ToDo : Add event history to database