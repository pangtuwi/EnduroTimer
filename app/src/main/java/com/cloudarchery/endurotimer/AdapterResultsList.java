package com.cloudarchery.endurotimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by paulwilliams on 01/01/15.
 */

public class AdapterResultsList extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    JSONArray resultsList;

    public AdapterResultsList(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        if (resultsList != null) {
            return resultsList.length();
        } else {
            return 0;
        }

    }


    @Override
    public JSONObject getItem(int position) {
        return resultsList.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
        /* ### can use this to go back and get more info.  see http://www.raywenderlich.com/78578/android-tutorial-for-beginners-part-3*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // check if the view already exists: if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_result, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            //holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.resultNameTextView = (TextView) convertView.findViewById(R.id.row_result_name);
            holder.resultTimeTextView = (TextView) convertView.findViewById(R.id.row_result_time);
            holder.resultStagesTextView = (TextView) convertView.findViewById(R.id.row_result_stages);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {
            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }


        // Get the data
       // Map <String, Object> resultItem = getItem(position);

        // Get the data in JSON form
        JSONObject resultItem = (JSONObject) getItem(position);

        String resultStages = "";
        String resultName = "";
        String resultTime = "00:00:00";
        try {

            resultName = ""+(position+1) + " [" + resultItem.get("raceNo") + "] " + resultItem.get("participantName");
            resultTime =  ""+ resultItem.get("raceTime");
        } catch (Throwable e) {
            e.printStackTrace();
        }


// Send these Strings to the TextViews for display
        holder.resultNameTextView.setText(resultName);
        holder.resultTimeTextView.setText(resultTime);
        holder.resultStagesTextView.setText(resultStages);

        return convertView;
    }

    public void updateData(JSONArray newList) {
        // update the adapter's dataset
        resultsList = newList;
        notifyDataSetChanged();
    }

    // this is used so you only ever have to do inflation and finding by ID once ever per View
    private static class ViewHolder {
        //public ImageView thumbnailImageView;
        public TextView resultNameTextView;
        public TextView resultTimeTextView;
        public TextView resultStagesTextView;
    }
}