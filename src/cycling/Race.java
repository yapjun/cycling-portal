package cycling;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Race class:
 * Used to hold data of Races
 * e.g. name, description, length
 *
 * Includes get/set methods
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class Race implements Serializable {
    private final String raceName;
    private final String raceDesc;
    private double length;
    private final ArrayList<Stage> stages = new ArrayList<>();
    private final int raceId;
    private static int raceCount;

    // constructor to insert all details in the object
    Race(String raceName, String raceDesc) {
        this.raceName = raceName;
        this.raceDesc = raceDesc;
        this.raceId = ++raceCount;
    }

    public String getRaceName() {
        return raceName;
    }

    public String getRaceDesc(){
        return raceDesc;
    }

    public double getRaceLength() {
        return length;
    }

    public ArrayList<Stage> getStages() {
        return this.stages;
    }

    public int getRaceId(){
        return this.raceId;
    }

    public void addStage(Stage stage){
        stages.add(stage);
    }

    public void setRaceLength(double length){
        this.length = length;
    }

}
