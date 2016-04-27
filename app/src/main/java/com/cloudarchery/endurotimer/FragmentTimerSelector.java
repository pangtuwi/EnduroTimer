package com.cloudarchery.endurotimer;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;


public class FragmentTimerSelector extends Fragment implements AdapterView.OnItemClickListener{

    MyApp myApp;
    Switch SW_startOrFinish;
    CheckBox CB_ManualTiming;
    ListView LV_stages;
    AdapterStagesList Adapter_stages;
    Button B_startTiming;


    private FragmentActivity myContext;

    public FragmentTimerSelector(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());
        View rootView = inflater.inflate(R.layout.fragment_timer_selector, container, false);

        B_startTiming = (Button) rootView.findViewById(R.id.fragment_timer_selector_button1);
        SW_startOrFinish = (Switch) rootView.findViewById(R.id.fragment_timer_selector_switch1);
        CB_ManualTiming = (CheckBox) rootView.findViewById(R.id.fragment_timer_selector_checkbox1);

        LV_stages = (ListView) rootView.findViewById(R.id.fragment_timer_selector_stagelist);
        LV_stages.setOnItemClickListener(this);
        Adapter_stages = new AdapterStagesList(getActivity(), getActivity().getLayoutInflater());
        LV_stages.setAdapter(Adapter_stages);

        if (myApp.CDS.stages.length() > 0) {
            Adapter_stages.updateData(myApp.CDS.stages);
        } else {
            rootView.findViewById(R.id.fragment_timer_selector_stagesheader).
        }

        SW_startOrFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SW_startOrFinish.isSelected()) {
                    myApp.setTimingStart();
                } else {
                    myApp.setTimingFinish();
                }
            }
        });

        B_startTiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentTimer();
                if (fragment != null) {
                    FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack("");
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        return rootView;
    } //onCreateView


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myApp.setStageID(position);
        LV_stages.setSelection(position);
    }// OnItemClick

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


    //ToTo : Turn off Timing on exit myApp.stopTiming();





}
