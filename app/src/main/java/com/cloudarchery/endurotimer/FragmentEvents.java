package com.cloudarchery.endurotimer;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentEvents extends Fragment implements AdapterView.OnItemClickListener{

    ListView mainListView;
    TextView warningTextView;
    AdapterEventsList mJSONAdapter;

    MyApp myApp;

    public FragmentEvents(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());

        final View rootView = inflater.inflate(R.layout.event_selector_page, container, false);

        warningTextView = (TextView) rootView.findViewById(R.id.event_selector_page_NoEventHint);

        mainListView = (ListView) rootView.findViewById(R.id.event_selector_page_listview);
        mainListView.setOnItemClickListener(this);
        mJSONAdapter = new AdapterEventsList(getActivity(), getActivity().getLayoutInflater());
        mainListView.setAdapter(mJSONAdapter);

        if (myApp.CDS.events.size() > 0) {
            mJSONAdapter.updateData(myApp.CDS.events);
            rootView.findViewById(R.id.event_selector_page_NoEventHint).setVisibility(View.INVISIBLE);
        }

        myApp.CDS.myEventsListUpdatedListener = new CloudData.OnEventsListUpdatedListener() {
            @Override
            public void onEventsListUpdated() {
                Log.d("EnduroTimer", "events have been Loaded - listener responded");
                rootView.findViewById(R.id.event_selector_page_NoEventHint).setVisibility(View.INVISIBLE);
                mJSONAdapter.updateData(myApp.CDS.events);
            }
        };

        return rootView;
    } //onCreateView


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //myApp.setStageID(position);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.event_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialoglayout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                myApp.CDS.changeEvent(position);
              //  if (getFragmentManager().getBackStackEntryCount() > 0 ) {
               //     getFragmentManager().popBackStackImmediate();
              //  } else {
                    Fragment fragment = new MainPage();
                    //String fragmentName = "EnduroTimer";
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    //getSupportActionBar().setTitle(fragmentName);
                    ft.addToBackStack("");
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }

           // }
        });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        builder.show();


    }// OnItemClick


    @Override
    public void onDestroyView (){
        super.onDestroyView();
    } //onDestroyView


}
