package com.cloudarchery.endurotimer;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentEntrant extends Fragment {

    ListView LVResults;

    ImageButton IBBAck;
    ImageButton IBSave;
    ImageButton IBWriteNFC;

    EditText ETEntrantName;
    EditText ETEntrantRaceNo;
    EditText ETEntrantID;
    AdapterResultsList myResultsAdapter;

    private FragmentActivity myContext;

    MyApp myApp;

    public FragmentEntrant(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());
        final View rootView = inflater.inflate(R.layout.entrant_page, container, false);

        IBWriteNFC = (ImageButton) rootView.findViewById(R.id.entrant_page_imageButton_writeNFC);

        ETEntrantName = (EditText) rootView.findViewById(R.id.entrantpage_editText_name);
        ETEntrantRaceNo = (EditText) rootView.findViewById(R.id.entrantpage_editText_raceNo);
        ETEntrantID = (EditText) rootView.findViewById(R.id.entrantpage_editText_UUID);

        if (!myApp.selectedEntrantID.equals("")) {
            ETEntrantID.setText(myApp.selectedEntrantID);
        }

        LVResults = (ListView) rootView.findViewById(R.id.entrant_page_resultsListView);
        //LVResults.setOnItemClickListener(this);
        myResultsAdapter = new AdapterResultsList(getActivity(), getActivity().getLayoutInflater());
        LVResults.setAdapter(myResultsAdapter);


        IBWriteNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentWriteNFC();

                if (fragment != null) {
                    FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                    // ((AppCompatActivity)getActivity().getSupportActionBar().setTitle("Select Stage to Time");
                    ft.addToBackStack("");
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });

      /*  if (myApp.CDS.events.size() > 0) {
            myResultsAdapter.updateData(myApp.CDS.results);
            rootView.findViewById(R.id.results_page_NoResultsHint).setVisibility(View.INVISIBLE);
        }

        myApp.CDS.updateResults();



        myApp.CDS.myEventsListUpdatedListener = new CloudData.OnEventsListUpdatedListener() {
            @Override
            public void onEventsListUpdated() {
                rootView.findViewById(R.id.event_selector_page_NoEventHint).setVisibility(View.INVISIBLE);
                myResultsAdapter.updateData(myApp.CDS.results);
            }
        };

*/
        return rootView;
    } //onCreateView



    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
    } //onDestroyView


}
