package com.cloudarchery.endurotimer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by paulwilliams on 01/01/15.
 */

public class JSONAdapterStagesList extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;
    //MyApp myAppState;

    public JSONAdapterStagesList(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
        //myAppState = (MyApp)mContext.getApplicationContext();
        //myAppState = ((MyApp)mContext);
        //myAppState = ((MyApp)getActivity().getApplicationContext());
        //myAppState = ((MyApp)mContext.get.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public JSONObject getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
        /* ### can use this to go back and get more info.  see http://www.raywenderlich.com/78578/android-tutorial-for-beginners-part-3*/
        /* ### can use this to go back and get more info.  see http://www.raywenderlich.com/78578/android-tutorial-for-beginners-part-3*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // check if the view already exists: if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_stage, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            //holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.stageNameTextView = (TextView) convertView.findViewById(R.id.row_stage_name);
            holder.stageStartDescriptionTextView = (TextView) convertView.findViewById(R.id.row_stage_startdescription);
            holder.stageFinishDescriptionTextView = (TextView) convertView.findViewById(R.id.row_stage_finishdescription);
            // holder.roundScoreTextView = (TextView) convertView.findViewById(R.id.rowround_roundscore);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {
            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }


        // Get the data in JSON form
        JSONObject stageJSON = (JSONObject) getItem(position);

        String stageName = "";
        String stageStartDescription = "";
        String stageFinishDescription = "";

        try {
            if (stageJSON.has("stageName")) {
                stageName = stageJSON.optString("stageName");
            }
            if (stageJSON.has("startDescription")) {
                stageStartDescription = "from: "+stageJSON.optString("startDescription");
            }
            if (stageJSON.has("finishDescription")) {
                stageFinishDescription = "to: "+stageJSON.optString("finishDescription");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

     /*   if (roundJSON.has("createdAt")) {
            //authorName = jsonObject.optJSONArray("author_name").optString(0);
            Long createdAtLong = roundJSON  .optLong("createdAt");
            Date createdAtDate = new Date(createdAtLong);
            //String DATE_FORMAT_NOW = "yyyy-MM-dd";
            String DATE_FORMAT_NOW = "EEEE dd MMMM yyyy";
            //Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
            roundDate = sdf.format(createdAtDate);
            //roundDate = createdAtDate.toString();
        } */

  /*      try {
            //   if (roundJSON.has("scores")) {
             //   JSONObject userJSON = roundJSON.getJSONObject("scores").getJSONObject("users")
             //           .getJSONObject(myAppState.CDS.getUserID()).getJSONObject("status");
             //   roundScore = ""+userJSON.getInt("totalScore");
           // }
        } catch (JSONException e) {
           // e.printStackTrace();
           // Log.d("CloudArchery", "No value for /scores/users/"+myAppState.CDS.getUserID()+"/status/");
        }
 */

// Send these Strings to the TextViews for display
        //holder.roundTypeTextView.setText("Hello World 7");
        holder.stageNameTextView.setText(stageName);
        holder.stageStartDescriptionTextView.setText(stageStartDescription);

        return convertView;
    }

    public void updateData(JSONArray jsonArray) {
        // update the adapter's dataset
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    // this is used so you only ever have to do inflation and finding by ID once ever per View
    private static class ViewHolder {
        //public ImageView thumbnailImageView;
        public TextView stageNameTextView;
        public TextView stageStartDescriptionTextView;
        public TextView stageFinishDescriptionTextView;
        //public TextView stageEnTextView;
    }
}