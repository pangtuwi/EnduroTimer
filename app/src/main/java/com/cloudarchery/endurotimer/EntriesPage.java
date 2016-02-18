package com.cloudarchery.endurotimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;


public class EntriesPage extends Fragment implements AdapterView.OnItemClickListener{
    ImageView imageViewConnection;
    ListView mainListView;
    TextView warningTextView;
    AdapterEntriesList mAdapter;
    MyApp myAppState;
    //ClubFirebase CDS;


    public EntriesPage(){}

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

        if (myAppState.CDS.stages.length() > 0) {
            mAdapter.updateData(myAppState.CDS.entries);
            rootView.findViewById(R.id.entry_selector_page_no_entries_hint).setVisibility(View.INVISIBLE);
        }

    return rootView;
    } //onCreateView


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //JSONObject entryJSON = mAdapter.getItem(position);

        try {

        } catch (Throwable t) {
            Log.e("EnduroTimer", "Error getting id from JSON (EntriesPage.onItemClick)");
            t.printStackTrace();
        }
    }// OnItemClick


    public void loadRoundData() {


    } //loadRoundData

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        //   myAppState.CDS.StopRoundChangeListener();
    } //onDestroyView


}
