package com.cloudarchery.endurotimer;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentTimer extends Fragment{

    MyApp myApp;
    Button B_stage;
    Button B_startOrFinish;
    Button B_Go;
    Button B_Stop;
    FrameLayout FR_start;
    FrameLayout FR_finish;

    TextView TV_stageNameStart;
    TextView TV_stageNameFinish;
    TextView TV_participantStart;
    TextView TV_participantFinish;

    ListView LV_timingHistory;
    AdapterTimerHistoryList historyListAdapter;


    private FragmentActivity myContext;

    public FragmentTimer(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());
        View rootView = inflater.inflate(R.layout.timer_page, container, false);

        B_stage = (Button) rootView.findViewById(R.id.timer_page_button_stage);
        B_startOrFinish = (Button) rootView.findViewById(R.id.timer_page_button_startorfinish);
        B_Go = (Button) rootView.findViewById(R.id.timer_page_button_go);
        B_Stop = (Button) rootView.findViewById(R.id.timer_page_button_stop);
        B_Go.setEnabled(false);
        B_Stop.setEnabled(false);

        FR_start = (FrameLayout) rootView.findViewById(R.id.timer_page_frame_stagestart);
        FR_finish = (FrameLayout) rootView.findViewById(R.id.timer_page_frame_stagefinish);
        TV_stageNameStart = (TextView) rootView.findViewById(R.id.timer_page_textview_stagenamestart);
        TV_stageNameFinish = (TextView) rootView.findViewById(R.id.timer_page_textview_stagenamefin);
        TV_participantStart= (TextView) rootView.findViewById(R.id.timer_page_textview_participantstart);
        TV_participantFinish= (TextView) rootView.findViewById(R.id.timer_page_textview_participantfinish);



        FR_finish.setVisibility(View.INVISIBLE);

        if (myApp.hasStageID()) {
            int thisStageID = myApp.getStageID();
            B_stage.setText("Stage Selected : "+(thisStageID+1));
            TV_stageNameStart.setText("Stage ("+thisStageID+1 +") START : " +myApp.CDS.getStageName(thisStageID));
            TV_stageNameFinish.setText("Stage (" + thisStageID+1 + ") FINISH : " + myApp.CDS.getStageName(thisStageID));
            B_Go.setEnabled(true);
            B_Stop.setEnabled(true);
            myApp.startTiming();
        }

        LV_timingHistory = (ListView) rootView.findViewById(R.id.timer_page_listView);
        //LV_timingHistory.setOnItemClickListener(this);
        historyListAdapter = new AdapterTimerHistoryList(getActivity(), getActivity().getLayoutInflater());
        LV_timingHistory.setAdapter(historyListAdapter);

        //if (myApp.timerHistoryList.length() > 0) {
            historyListAdapter.updateData(myApp.timerHistoryList);
         //   rootView.findViewById(R.id.stage_selector_page_NoStageHint).setVisibility(View.INVISIBLE);
        //}

        //Stage Select Button
        B_stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new StageSelectorPage();

                if (fragment != null) {
                    FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                    // ((AppCompatActivity)getActivity().getSupportActionBar().setTitle("Select Stage to Time");
                    ft.addToBackStack("");
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }

            }
        });

        //Stage Start/finish Button
        B_startOrFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApp.timingStart()) {
                    B_startOrFinish.setText("Position : Finish");
                    FR_start.setVisibility(View.INVISIBLE);
                    FR_finish.setVisibility(View.VISIBLE);
                    myApp.setTimingFinish();
                } else { //timing finish
                    B_startOrFinish.setText("Position : Start");
                    FR_start.setVisibility(View.VISIBLE);
                    FR_finish.setVisibility(View.INVISIBLE);
                    myApp.setTimingStart();
                }

            }
        });

        //Stage Start GO Button
        B_Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long NFCTime = System.currentTimeMillis();
                myApp.CDS.setStageTime("f87bc61c-2fc4-4fc5-a858-ea3d8f6ef732", myApp.getStageID(), NFCTime, myApp.timingStart());
            }
        });

        //Stage Finish STOP Button
        B_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long NFCTime = System.currentTimeMillis();
                myApp.CDS.setStageTime("f87bc61c-2fc4-4fc5-a858-ea3d8f6ef732", myApp.getStageID(), NFCTime, myApp.timingStart());
            }
        });

        //Listener for NFS Timer Eventsv
        myApp.CDS.myParticipantTimeListener = new CloudData.OnParticipantTimedListener() {
            @Override
            public void onParticipantTimeRegistered(boolean timerSuccess, String participantName, String timeStamp, String stageDescr, boolean stageStart) {
                if (timerSuccess) {
                    if (stageStart) {
                        TV_participantStart.setText("Start recorded : " + participantName + " : " + timeStamp);
                        myApp.timerHistoryList.add(stageDescr + " | " + participantName + " : " + timeStamp);
                    } else {
                        TV_participantFinish.setText("Finish recorded : "+ participantName + " : " + timeStamp);
                        myApp.timerHistoryList.add(stageDescr + " | " + participantName + " : " + timeStamp);
                    }
                    historyListAdapter.updateData(myApp.timerHistoryList);
                } else {
                    TV_participantStart.setText("Timing tag not matched.  No time recorded");
                    TV_participantFinish.setText("Timing tag not matched.  No time recorded");
                }
            }
        };

        return rootView;
    } //onCreateView


    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


    //ToTo : Turn off Timing on exit myApp.stopTiming();





}
