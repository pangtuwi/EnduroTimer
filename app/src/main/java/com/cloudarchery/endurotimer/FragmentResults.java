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


public class FragmentResults extends Fragment implements AdapterView.OnItemClickListener{

    ListView mainListView;
    TextView warningTextView;
    AdapterResultsList myResultsAdapter;

    MyApp myApp;

    public FragmentResults(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());

        final View rootView = inflater.inflate(R.layout.results_page, container, false);

        warningTextView = (TextView) rootView.findViewById(R.id.results_page_NoResultsHint);

        mainListView = (ListView) rootView.findViewById(R.id.results_page_listview);
        mainListView.setOnItemClickListener(this);
        myResultsAdapter = new AdapterResultsList(getActivity(), getActivity().getLayoutInflater());
        mainListView.setAdapter(myResultsAdapter);

        if (myApp.CDS.events.size() > 0) {
            myResultsAdapter.updateData(myApp.CDS.results);
            rootView.findViewById(R.id.results_page_NoResultsHint).setVisibility(View.INVISIBLE);
        }

        myApp.CDS.updateResults();

        myApp.CDS.myEventsListUpdatedListener = new CloudData.OnEventsListUpdatedListener() {
            @Override
            public void onEventsListUpdated() {
                rootView.findViewById(R.id.results_page_NoResultsHint).setVisibility(View.INVISIBLE);
                myResultsAdapter.updateData(myApp.CDS.results);
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
