package com.cloudarchery.endurotimer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
            return -1;
        }
        if (another.totalTime == 0L) {
            return 1;
        }
        if (this.totalTime<another.totalTime){
            return 1;
        }else{
            return -1;
        }
    }

    public String raceTimeString(){
        String rTS = "00:00:00";
        if (totalTime > 0){
            Date racetimeDate = new Date(totalTime);
            String DATE_FORMAT_RACETIME = "HH:MM:SS";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_RACETIME);
            rTS = sdf.format(racetimeDate);
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
