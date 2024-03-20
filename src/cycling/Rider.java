package cycling;

import java.io.Serializable;

/**
 * Rider class:
 * Used to hold data related to Riders
 * e.g. name, id, year of birth.
 *
 * Includes get/set methods
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class Rider implements Serializable {
    private String riderName;
    private int riderYob;
    private int riderId;
    private static int riderCount;

    // constructor to insert all details in the object
    Rider(String riderName, int riderYob){
        this.riderName = riderName;
        this.riderYob = riderYob;
        this.riderId = ++riderCount;
    }

    public String getRiderName(){
        return this.riderName;
    }

    public int getRiderYob(){
        return this.riderYob;
    }

    public int getRiderId() {
        return this.riderId;
    }
}
