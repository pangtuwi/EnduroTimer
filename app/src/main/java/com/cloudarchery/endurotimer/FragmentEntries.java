package com.cloudarchery.endurotimer;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;
import java.util.UUID;


public class FragmentEntries extends Fragment implements AdapterView.OnItemClickListener{
    ImageView imageViewConnection;
    ListView mainListView;
    TextView warningTextView;
    AdapterEntriesList mAdapter;
    MyApp myAppState;
    FloatingActionButton FAB_AddEntry;

    private FragmentActivity myContext;

    //ClubFirebase CDS;


    public FragmentEntries(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myAppState = ((MyApp)getActivity().getApplicationContext());


        View rootView = inflater.inflate(R.layout.entry_selector_page, container, false);
        warningTextView = (TextView) rootView.findViewById(R.id.entry_selector_page_no_entries_hint);

        mainListView = (ListView) rootView.findViewById(R.id.entry_selector_page_listview);
        mainListView.setOnItemClickListener(this);
        mAdapter = new AdapterEntriesList(getActivity(), getActivity().getLayoutInflater());
        mainListView.setAdapter(mAdapter);

        FAB_AddEntry = (FloatingActionButton) getActivity().findViewById(R.id.fab_add);
        FAB_AddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAB_AddEntry.hide();
                myAppState.selectedEntrantID = UUID.randomUUID().toString();
                Fragment fragment = new FragmentEntrant();
                if (fragment != null) {
                    FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                    // ((AppCompatActivity)getActivity().getSupportActionBar().setTitle("Select Stage to Time");
                    ft.addToBackStack("");
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });

        if (myAppState.CDS.stages.length() > 0) {
            mAdapter.updateData(myAppState.CDS.entriesList);
            rootView.findViewById(R.id.entry_selector_page_no_entries_hint).setVisibility(View.INVISIBLE);
        }

    return rootView;
    } //onCreateView


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Map<String, Object> entryItem = mAdapter.getItem(position);
        if (entryItem.containsKey("id")){
            myAppState.selectedEntrantID = (String) entryItem.get("id");
        }

        try {

            Fragment fragment = new FragmentEntrant();

            if (fragment != null) {
                FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                // ((AppCompatActivity)getActivity().getSupportActionBar().setTitle("Select Stage to Time");
                ft.addToBackStack("");
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }

        } catch (Throwable t) {
            Log.e("EnduroTimer", "Error opening FragmentEntrant (FragmentEntries)");
            t.printStackTrace();
        }
    }// OnItemClick


    public void loadRoundData() {


    } //loadRoundData


    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        //   myAppState.CDS.StopRoundChangeListener();
    } //onDestroyView


}
