package cycling;

import java.io.Serializable;

/**
 * Segment class:
 * Used to hold all data related to Segments
 * e.g. stageId, location, SegmentType
 *
 * Includes get/set methods
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class Segment implements Serializable {
    private final int stageId;
    private final double location;
    private final SegmentType type;
    private double averageGradient;
    private double length;
    private int segmentId;
    private static int segmentCount;

    // 1st constructor: for sprints
    Segment(int stageId, double location, SegmentType type){
        this.stageId = stageId;
        this.location = location;
        this.type = type;
        this.segmentId = ++segmentCount;
    }

    // 2nd constructor: for climbs, takes 2 more arguments
    // averageGradient and length
    Segment(int stageId, double location, SegmentType type, double averageGradient, double length){
        this.stageId = stageId;
        this.location = location;
        this.type = type;
        this.averageGradient = averageGradient;
        this.length = length;
        this.segmentId = ++segmentCount;
    }

    public int getSegmentStageId(){
        return this.stageId;
    }

    public double getSegmentLocation(){
        return this.location;
    }

    public SegmentType getSegmentType(){
        return this.type;
    }

    public double getAverageGradient(){
        return this.averageGradient;
    }

    public double getSegmentLength(){
        return this.length;
    }

    public int getSegmentId(){
        return this.segmentId;
    }

}
