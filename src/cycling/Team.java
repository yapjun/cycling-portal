package cycling;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Team class:
 * Used to hold all data related to Team
 * e.g. team name, description, teamId
 *
 * Includes get/set methods
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class Team implements Serializable {
    private String teamName;
    private String teamDesc;
    private int teamId;
    private static int teamCount;
    private ArrayList<Rider> riders = new ArrayList<>();

    // constructor to insert all details in the object
    Team(String teamName, String teamDesc){
        this.teamName = teamName;
        this.teamDesc = teamDesc;
        this.teamId = ++teamCount;
    }

    public String getTeamName(){
        return teamName;
    }

    public String getTeamDesc(){
        return teamDesc;
    }

    public int getTeamId(){
        return teamId;
    }

    public ArrayList<Rider> getRiders() {
        return this.riders;
    }

}
