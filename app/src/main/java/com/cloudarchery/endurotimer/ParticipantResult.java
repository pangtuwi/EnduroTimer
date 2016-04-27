package com.cloudarchery.endurotimer;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulwilliams on 27/02/16.
 */
public class ParticipantResult implements Comparable<ParticipantResult>{
    public String participantName;
    public String raceNo;
    public String racePosition;
    public boolean raceComplete = false;
    public Long totalTime = 0L;
    public List<Integer> stagePositions;
    public List<Long> stageTimes;

    public ParticipantResult(){
        stageTimes = new ArrayList<>();
    }

    public void setStageTime (int stageNo, long startTime, long finishTime ){
        if ((startTime ==  0 ) || (finishTime == 0)) {
            stageTimes.add(stageNo, 0L);

        } else {
            stageTimes.add(stageNo, (finishTime-startTime));
            Log.d("EnduroTimer", participantName+ " : "+ stageNo + " : " + (finishTime - startTime));
        }
    }

    public void calcRaceTime (int stageCount){
        //totalTime = 0L;
        boolean hasZeroTime = false;
        for (int i = 0; i < stageTimes.size() ; i++) {
            totalTime = totalTime + stageTimes.get(i);
            if (stageTimes.get(i) == 0) hasZeroTime = true;
        }
        raceComplete = ((stageCount == stageTimes.size()) && !hasZeroTime );
    }

    @Override
    public int compareTo(ParticipantResult another) {
        if (this.totalTime == 0L) {
            return 1;
        }
        if (another.totalTime == 0L) {
            return -1;
        }
        if (this.totalTime<another.totalTime){
            return -1;
        }else{
            return 1;
        }
    }

    public String raceTimeString(){
        String rTS = "00:00:00";
        if (totalTime > 0){
            Long millis = totalTime;

      /*      Date racetimeDate = new Date(totalTime);
            String DATE_FORMAT_RACETIME = "kk:mm:ss.S";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_RACETIME);
            rTS = sdf.format(racetimeDate);
            */
            rTS = String.format("%02d:%02d:%02d.%d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                    (millis - (1000*TimeUnit.MILLISECONDS.toSeconds(millis)))/100);
            Log.d("EnduroTimer", "RaceTime for "+participantName + " = "+ totalTime + " which is " + rTS);
        }
        return rTS;
    }

    public HashMap<String, String> asHashMap (){
       HashMap<String, String> resultHashMap = new HashMap<>();
        resultHashMap.put("raceNo", raceNo);
        resultHashMap.put("participantName", participantName);
        resultHashMap.put("raceTime", raceTimeString());
        return resultHashMap;
    }
}
