package com.cloudarchery.endurotimer;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentTimer extends Fragment{

    MyApp myApp;


    TextView TV_stageName;
    ListView LV_timingHistory;
    AdapterTimerHistoryList AD_timerHistory;
    Button BT_done;


    private FragmentActivity myContext;

    public FragmentTimer(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        TV_stageName = (TextView) rootView.findViewById(R.id.timer_page_textview_stagenamestart);

        if (myApp.hasStageID()) {
            int thisStageID = myApp.getStageID();
            TV_stageName.setText("Stage ("+thisStageID+1 +") START : " +myApp.CDS.getStageName(thisStageID));
            myApp.startTiming();
        }

        LV_timingHistory = (ListView) rootView.findViewById(R.id.fragment_timer_historylist);
        AD_timerHistory = new AdapterTimerHistoryList(getActivity(), getActivity().getLayoutInflater());
        LV_timingHistory.setAdapter(AD_timerHistory);
        AD_timerHistory.updateData(myApp.timerHistoryList);

        BT_done = (Button) rootView.findViewById(R.id.fragment_timer_donebutton);
        BT_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        //Listener for NFS Timer Eventsv
        myApp.CDS.myParticipantTimeListener = new CloudData.OnParticipantTimedListener() {
            @Override
            public void onParticipantTimeRegistered(boolean timerSuccess, String participantName, String timeStamp, String stageDescr, boolean stageStart) {
                if (timerSuccess) {
                    if (stageStart) {
                        myApp.timerHistoryList.add(stageDescr + " | " + participantName + " : " + timeStamp);
                    } else {
                        myApp.timerHistoryList.add(stageDescr + " | " + participantName + " : " + timeStamp);
                    }
                    AD_timerHistory.updateData(myApp.timerHistoryList);
                } else {
                    myApp.timerHistoryList.add("Timing tag not matched.  No time recorded");
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
