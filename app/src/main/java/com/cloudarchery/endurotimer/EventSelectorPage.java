package com.cloudarchery.endurotimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class EventSelectorPage extends Fragment implements AdapterView.OnItemClickListener{

    ListView mainListView;
    TextView warningTextView;
    JSONAdapterEventsList mJSONAdapter;

    MyApp myApp;

    public EventSelectorPage(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());

        View rootView = inflater.inflate(R.layout.stage_selector_page, container, false);

        warningTextView = (TextView) rootView.findViewById(R.id.stage_selector_page_NoStageHint);

        mainListView = (ListView) rootView.findViewById(R.id.stage_selector_page_listview);
        mainListView.setOnItemClickListener(this);
        mJSONAdapter = new JSONAdapterEventsList(getActivity(), getActivity().getLayoutInflater());
        mainListView.setAdapter(mJSONAdapter);

        if (myApp.CDS.stages.length() > 0) {
            mJSONAdapter.updateData(myApp.CDS.stages);
            rootView.findViewById(R.id.stage_selector_page_NoStageHint).setVisibility(View.INVISIBLE);
        }

        return rootView;
    } //onCreateView


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myApp.setStageID(position);
        getFragmentManager().popBackStackImmediate();
    }// OnItemClick


    @Override
    public void onDestroyView (){
        super.onDestroyView();
    } //onDestroyView


}
