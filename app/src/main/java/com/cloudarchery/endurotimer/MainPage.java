package com.cloudarchery.endurotimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class MainPage extends Fragment {

    MyApp myAppState;
    TextView TV_EventName;
    TextView TV_EventDate;

    public MainPage(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myAppState = ((MyApp)getActivity().getApplicationContext());
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.main_page, container, false);
        TV_EventName = (TextView) rootView.findViewById(R.id.main_page_eventName);
        TV_EventDate = (TextView) rootView.findViewById(R.id.main_page_eventDate);

        if (myAppState.CDS.eventLoaded) {
            TV_EventName.setText("Event : "+myAppState.CDS.eventName);
            TV_EventDate.setText("Date : " +myAppState.CDS.eventDate);
        }

        myAppState.CDS.myRaceEventListener = new CloudData.OnRaceEventUpdatedListener() {
            @Override
            public void onRaceEventUpdated() {
                Log.d("EnduroTimer","eventLoaded = "+myAppState.CDS.eventLoaded);
                if (myAppState.CDS.eventLoaded) {
                    TV_EventName.setText("Event : "+myAppState.CDS.eventName);
                    TV_EventDate.setText("Date : " +myAppState.CDS.eventDate);
                }
            }
        };

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
    public void onDestroyView (){
        super.onDestroyView();
     //   myAppState.CDS.StopRoundChangeListener();
    } //onDestroyView


}
