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

public class AdapterEventsList extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<Object> eventsList;

    public AdapterEventsList(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        //mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
        if (eventsList != null) {
            return eventsList.size();
        } else {
            return 0;
        }

    }

    @Override
    public Map<String, Object> getItem(int position) {
        return (Map<String, Object>) eventsList.get(position);
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
            convertView = mInflater.inflate(R.layout.row_event, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            //holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.eventNameTextView = (TextView) convertView.findViewById(R.id.row_event_name);
            holder.eventDateTextView = (TextView) convertView.findViewById(R.id.row_event_date);
            // holder.roundScoreTextView = (TextView) convertView.findViewById(R.id.rowround_roundscore);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {
            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }


        // Get the data in JSON form
        //JSONObject eventJSON = (JSONObject) getItem(position);
        Map <String, Object> eventItem = getItem(position);

        String eventName = "";
        String eventDateString = "";

        try {
            if (eventItem.containsKey("eventName")) {
                eventName = (String) eventItem.get("eventName");
            }
            if (eventItem.containsKey("eventStartsAt")) {
                Long eventDateLong = (Long)eventItem.get("eventStartsAt");
                Date eventDate = new Date(eventDateLong);
                //String DATE_FORMAT_NOW = "yyyy-MM-dd";
                String DATE_FORMAT_NOW = "EEEE dd MMMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                eventDateString = sdf.format(eventDate);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

// Send these Strings to the TextViews for display
        //holder.roundTypeTextView.setText("Hello World 7");
        holder.eventNameTextView.setText(eventName);
        holder.eventDateTextView.setText(eventDateString);

        return convertView;
    }

    public void updateData(List<Object> newList) {
        // update the adapter's dataset
        eventsList = newList;
        notifyDataSetChanged();
    }

    // this is used so you only ever have to do inflation and finding by ID once ever per View
    private static class ViewHolder {
        //public ImageView thumbnailImageView;
        public TextView eventNameTextView;
        public TextView eventDateTextView;
        //public TextView stageEnTextView;
    }
}