package com.cloudarchery.endurotimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentWriteNFC extends Fragment{

    MyApp myApp;

    Button B_Write;
    TextView TV_EntrantID;


    public FragmentWriteNFC(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myApp = ((MyApp)getActivity().getApplicationContext());
        View rootView = inflater.inflate(R.layout.fragment_write_nfc, container, false);

        B_Write = (Button) rootView.findViewById(R.id.fragment_write_nfc_button_write);

        TV_EntrantID = (TextView) rootView.findViewById(R.id.fragment_write_nfc_textview_entrantID);

        if (!myApp.selectedEntrantID.equals("")) {
            TV_EntrantID.setText(myApp.selectedEntrantID);
        }

        //Write Button
        B_Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApp.setNFCMode(2); //write
                Log.d("EnduroTimer", "Set NFC Mode to :"+MyApp.NFCMode);
            }
        });

        return rootView;
    } //onCreateView

  /*  @Override  //onPause did not work - called when the timer event happened
    public void onPause (){
        MyApp.setNFCMode(1);  //set back to read
        Log.d("EnduroTimer", "Set NFC Mode to :" + MyApp.NFCMode);
        super.onPause();
    } //onDestroyView
    */

}
