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


public class FragmentStages extends Fragment implements AdapterView.OnItemClickListener{
    ImageView imageViewConnection;
    ListView mainListView;
    TextView textViewConnectionStatus;
    TextView warningTextView;
    AdapterStagesList mJSONAdapter;

    String userID = "";
    String roundID = "";

    MyApp myAppState;
    //ClubFirebase CDS;


    public FragmentStages(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Bundle args = getArguments();
        //userID = args.get("userID").toString();
        myAppState = ((MyApp)getActivity().getApplicationContext());


        View rootView = inflater.inflate(R.layout.stages_page, container, false);
//        getActivity().getActionBar().setTitle("CloudArchery");

        warningTextView = (TextView) rootView.findViewById(R.id.stages_page_NoStageHint);

        mainListView = (ListView) rootView.findViewById(R.id.stages_page_listview);
        mainListView.setOnItemClickListener(this);
        mJSONAdapter = new AdapterStagesList(getActivity(), getActivity().getLayoutInflater());
        mainListView.setAdapter(mJSONAdapter);

        if (myAppState.CDS.stages.length() > 0) {
            mJSONAdapter.updateData(myAppState.CDS.stages);
            rootView.findViewById(R.id.stages_page_NoStageHint).setVisibility(View.INVISIBLE);
        }

      /*  myAppState.CDS.myRoundsListListener = new ClubFirebase.OnRoundsListUpdatedListener() {
            @Override
            public void onRoundsListUpdated() {
                loadRoundData();
                Log.d ("CloudArchery", "loading round data to list");
            }
        }; */

      /*  myAppState.CDS.myConnectionListener = new ClubFirebase.OnConnectionListener() {
            @Override
            public void onConnectionUpdated(Boolean SyncOn, Boolean Network, Boolean CDSConnected, Boolean Authenticated, Boolean Linked, String ErrorMessage) {
                displayConnectionStatus(SyncOn, Network, CDSConnected, Authenticated, Linked, ErrorMessage);
            }
        }; //myConnectionListener
*/



        //Join Round Button
       /* if (myAppState.CDS.syncOn) {
            rootView.findViewById(R.id.roundslist_joinround).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new JoinRound();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        //fragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_container, fragment)
                                .addToBackStack("")
                                .commit();
                    }
                }
            });
        } else {
            rootView.findViewById(R.id.roundslist_joinround).setVisibility(View.INVISIBLE);
        }

        */
        //displayConnectionStatus(myAppState.CDS.syncOn, myAppState.CDS.network, myAppState.CDS.connected, myAppState.CDS.authenticated, myAppState.CDS.linked, myAppState.CDS.firebaseError);
        return rootView;
    } //onCreateView

    /*  public void displayConnectionStatus (Boolean SyncOn, Boolean Network, Boolean CDSConnected, Boolean Authenticated, Boolean Linked, String ErrorMessage) {

          if (Network == null) Network = false;
          if (CDSConnected == null) CDSConnected = false;
          if (Authenticated == null) Authenticated = false;
          if (Linked == null) Linked = false;

          if (SyncOn) {
              if (Network) {
                  if (CDSConnected) {
                      if (Authenticated) {
                          if (Linked) {
                              TV_ConnectionStatus.setText("connected, login OK");
                              imageViewConnection.setImageResource(R.drawable.ic_cloud_connected);
                          } else {
                              TV_ConnectionStatus.setText("logged in but cannot synchronise");
                              imageViewConnection.setImageResource(R.drawable.ic_cloud_disconnected);
                          }
                      } else {
                          TV_ConnectionStatus.setText("connected, checking login credentials");
                          imageViewConnection.setImageResource(R.drawable.ic_change_user);
                      }
                  } else {
                      TV_ConnectionStatus.setText("initialising database connection");
                      imageViewConnection.setImageResource(R.drawable.ic_change_user);
                  }
              } else {
                  TV_ConnectionStatus.setText("no network connection");
                  imageViewConnection.setImageResource(R.drawable.ic_action_nok);
              }
          } else {
              TV_ConnectionStatus.setText("stand alone mode, no cloud sync");
              imageViewConnection.setImageResource(R.drawable.ic_action_nok);
          }
      }//displayConnectionStatus

  */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        JSONObject stageJSON = mJSONAdapter.getItem(position);

        try {
            // String selectedRoundID = scoreRoundJSON.get("id").toString();
            //JSONObject  scoreDetailRoundJSON = roundJSON.getJSONObject("detail");
            //roundID = roundJSON.getString("id");
            // myAppState.setCurrentEnd(0);

           // Bundle args = new Bundle();
           // args.putString("roundID", roundID);
            //args.putString("userID", userID);
      /*      Fragment fragment = new RoundScores();
            if (fragment != null) {
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .addToBackStack("")
                        .commit();
            }*/


        } catch (Throwable t) {
            Log.e("EnduroTimer", "Error getting id from JSON (FragmentStages.onItemClick)");
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
