package cycling;

import java.io.Serializable;
import java.util.*;
import java.time.LocalTime;

/**
 * Result class:
 * Used to hold all data related to Results
 * e.g. stageId, description, elapsed time
 *
 *
 * Includes get/set methods
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class Result implements Serializable {
    private int stageId;
    private int riderId;
    private LocalTime elapsedTime;
    private LocalTime[] checkpoints;
    private ArrayList<Integer> points;

    // constructor to insert all details in the object
    Result(int stageId, int riderId, LocalTime elapsedTime, LocalTime... checkpoints) {
        this.stageId = stageId;
        this.riderId = riderId;
        this.elapsedTime = elapsedTime;
        this.checkpoints = checkpoints;
    }

    public int getResultStageId() {
        return this.stageId;
    }

    public int getResultRiderId() {
        return this.riderId;
    }

    public LocalTime getElapsedTime() {
        return this.elapsedTime;
    }

    public LocalTime[] getCheckpoints() {
        return this.checkpoints;
    }

    public ArrayList<Integer> getResultPoints() {
        return this.points;
    }
}
