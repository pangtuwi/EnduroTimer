package com.cloudarchery.endurotimer;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MyApp myAppState;
    public static final String TAG = "NfcDemo";
    private TextView TV_NFCStatus;
    TextView TV_ConnectionStatus;
    FloatingActionButton FAB_timer;
    FloatingActionButton FAB_info;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myAppState = ((MyApp)getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FAB_timer = (FloatingActionButton) findViewById(R.id.fab_timer);
        FAB_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //            .setAction("Action", null).show();

                displayView(4);
            }
        });


        FAB_info = (FloatingActionButton) findViewById(R.id.fab_info);
        FAB_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String snackBarText = "";
                    if (myAppState.CDS.connected) {
                        snackBarText = "Connection OK";
                    } else {
                        snackBarText = "NOT connected";
                    }
                    if (!mNfcAdapter.isEnabled()) {
                        snackBarText += " | NFC disabled.";
                    } else {
                      snackBarText += " | NFC Enabled";
                    }
                    Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


       /* TV_ConnectionStatus = (TextView) findViewById(R.id.main_page_connectionstatus);

        if (myAppState.CDS.connected) {
            TV_ConnectionStatus.setText("connected");
        } else {
            TV_ConnectionStatus.setText("NOT connected");
        }


        TV_NFCStatus = (TextView) findViewById(R.id.main_page_NFCstatus);

        */
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

 /*       if (!mNfcAdapter.isEnabled()) {
            TV_NFCStatus.setText("NFC is disabled.");

        } else {
           TV_NFCStatus.setText("NFC is Enabled");
        }
        */

        Log.d ("EnduroTimer", "Setting listener for No Event ID");
        myAppState.CDS.myNoEventIDListener = new CloudData.OnNoEventIDListener() {
            @Override
            public void onNoEventIDFound() {
                Log.d("EnduroTimer","No Event ID Found : Listener Called, Starting Page");
                displayView(5); //Show EventSelector
            }
        };

        handleIntent(getIntent());

        if (!myAppState.CDS.hasEventID) {
            Log.d("EnduroTimer","No Event ID Found : Starting Page");
            displayView(5); //Show EventSelector
        } else {
            displayView(0);   //Display Main Page
        }

    } //onCreate


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eventDetails) {
            displayView(0);
        } else if (id == R.id.nav_eventEntries) {
            displayView(1);
        } else if (id == R.id.nav_eventStages) {
            displayView(2);
        } else if (id == R.id.nav_eventResults) {
            displayView(3);
        } else if (id == R.id.nav_timeRace) {
            displayView(4);
        } else if (id == R.id.nav_loadEvent) {
            displayView(5);
        } else if (id == R.id.nav_syncEvent) {
            displayView(6);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayView(int viewID) {
        //Displaying fragment view for selected nav drawer list item
        // update the main content by replacing fragments
        Fragment fragment = null;
        String fragmentName = "";

        switch (viewID) {
            case 0:  //nav_eventDetails
                fragment = new MainPage();
                fragmentName = "EnduroTimer";
                FAB_timer.show();
                break;
            case 1: //nav_eventEntries
                fragment = new EntriesPage();
                fragmentName = "Select an Entry";
                break;
            case 2: //nav_eventStages
                fragment = new StagesPage();
                fragmentName = "Select a Stage";
                break;
            case 3:  //nav_eventResults

                break;
            case 4:  //nav_timeRace
                fragment = new TimerPage();
                fragmentName = "EnduroTimer";
                FAB_timer.hide();
                break;
            case 5: //nav_loadEvent
                fragment = new EventSelectorPage();
                fragmentName = "Select Event";
                FAB_timer.hide();
                FAB_info.hide();
                break;
            case 6: //nav_syncEvent

                break;
            default:
                break;
        }
        //Bundle args = new Bundle();
        //args.putString("userID", userID);
        //fragment.setArguments(args);

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            getSupportActionBar().setTitle(fragmentName);
            ft.addToBackStack("");
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

            // update selected item and title, then close the drawer
           // mDrawerList.setItemChecked(position, true);
           // mDrawerList.setSelection(position);
           // setTitle(navPageTitles[position]);

           // mDrawerLayout.closeDrawer(mDrawerList);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d("NFC", "in handleIntent");
        String action = intent.getAction();
        Log.d("NFC", "String is " + action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d ("NFC", "ACTION_TECH_DISCOVERED");
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    public void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }



    /**
     * @param activity The corresponding  Activity} requesting the foreground dispatch.
     * @param adapter  The NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, null, null); //last two param was filters techList
    }


    // @param activity The corresponding BaseActivity requesting to stop the foreground dispatch.
    // @param adapter  The NfcAdapter used for the foreground dispatch.

    public static void stopForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }


    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                Log.d(TAG, "NDEF not supported by Tag");
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                long NFCTime = System.currentTimeMillis();
                String checkString = result.substring(0,7);
                int resultLength = result.length();
                if ((checkString.equals("Enduro-")) && (resultLength == 43)) {
                    String participantUUID = result.substring(7,43);
                    myAppState.CDS.setStageTime(participantUUID, myAppState.getStageID(), NFCTime, myAppState.timingStart());
                }
               // TV_NFCStatus.setText("Read content: " + result);
            }
        }
    }


}