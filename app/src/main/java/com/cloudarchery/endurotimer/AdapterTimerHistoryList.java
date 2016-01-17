package com.cloudarchery.endurotimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by paulwilliams on 01/01/15.
 */

public class AdapterTimerHistoryList extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<String> historyList;

    public AdapterTimerHistoryList(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        //mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
       if (historyList != null) {
           return historyList.size();
       } else {
           return 0;
       }
    }

    @Override
    public String getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // check if the view already exists: if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_timer_history, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.historyItemTextView = (TextView) convertView.findViewById(R.id.row_timer_history_textview);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {
            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the data
        //JSONObject historyItemJSON = (JSONObject) getItem(position);

        String historyItem = getItem(position);



    /*    try {
      //      if (roundJSON.has("roundType")) {
       //         roundType = roundJSON.getJSONObject("roundType").optString("name");
        //    }
        } catch (JSONException e) {
            e.printStackTrace();

        } */

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
        holder.historyItemTextView.setText(historyItem);
        return convertView;
    }

    public void updateData(List<String> newHistoryList) {
        // update the adapter's dataset
        historyList = newHistoryList;
        notifyDataSetChanged();
    }

    // this is used so you only ever have to do inflation and finding by ID once ever per View
    private static class ViewHolder {
        public TextView historyItemTextView;

    }
}