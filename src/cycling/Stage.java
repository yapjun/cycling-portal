package cycling;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Stage class:
 * Used to hold data of Stages
 * e.g. name, description, length
 *
 * Includes get/set methods
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class Stage implements Serializable {
    private int raceId;
    private String stageName;
    private String stageDesc;
    private double length;
    private LocalDateTime startTime;
    private StageType type;
    private int stageId;
    private static int stageCount;
    private String stageState;
    private ArrayList<Segment> segments = new ArrayList<>();

    // constructor to insert all details in the object
    Stage (int raceId, String stageName, String stageDesc, double length, LocalDateTime startTime, StageType type){
        this.raceId = raceId;
        this.stageName = stageName;
        this.stageDesc = stageDesc;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
        this.stageId = ++stageCount;
        this.stageState = "in preparation";
    }

    public int getRaceId(){
        return this.raceId;
    }

    public String getStageName(){
        return this.stageName;
    }

    public String getStageDesc(){
        return this.stageDesc;
    }

    public double getStageLength(){
        return this.length;
    }

    public LocalDateTime getStageStartTime(){
        return this.startTime;
    }

    public StageType getStageType(){
        return this.type;
    }

    public int getStageId(){
        return this.stageId;
    }

    public String getStageState(){
        return this.stageState;
    }

    public void addSegment (Segment segment){
        this.segments.add(segment);
    }

    public ArrayList<Segment> getSegments() {
        return this.segments;
    }

    public void setStageState(String state){
        this.stageState = state;
    }
}
