package cycling;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Cycling Portal.
 *
 * Includes methods and functions to create,
 * manage, view and calculate race results etc.
 *
 * @author Daphne Yap
 * @version 1.0
 *
 */

public class CyclingPortal implements MiniCyclingPortalInterface, Serializable {
    // initialize arrays for races, teams and results.
    public ArrayList<Race> races = new ArrayList<>();
    public ArrayList<Team> teams = new ArrayList<>();
    public ArrayList<Result> results = new ArrayList<>();

    @Override
    public int[] getRaceIds() {
        // check if array is empty
        if (races.size() == 0){
            return new int[0];
        }

        // not empty: convert ArrayList to simple array
        int[] raceIdArray = new int[races.size()];
        for (int i =0; i < races.size(); i++){
            raceIdArray[i] = races.get(i).getRaceId();
        }
        return raceIdArray;
    }

    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
        checkIllegalRaceName(name);
        checkInvalidNameException(name);

        // passed all checks, add race to ArrayList
        Race temp = new Race(name,description);
        races.add(temp);
        return temp.getRaceId();
    }

    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
        checkRaceIdNotRecognised(raceId);
        int id = findRaceId(raceId);

        // retrieve race data
        Race temp = races.get(id);

        // format data into a more readable string
        return "Race ID: " + temp.getRaceId()
                + "\nName: " + temp.getRaceName()
                + "\nDescription:" + temp.getRaceDesc()
                + "\nNumber of Stages: " + temp.getStages().size()
                + "\nTotal length: " + temp.getRaceLength();
    }

    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        checkRaceIdNotRecognised(raceId);
        int racePos = findRaceId(raceId);
        int[] resPos = findResultsIndexArrayUsingRaceId(raceId);

        // remove all results found for that race
        if (resPos.length != 0){
            for (int i = 0; i < resPos.length; i++){
                results.remove(results.get(resPos[i]));
            }
        }

        // remove race
        races.remove(races.get(racePos));
    }

    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
        checkRaceIdNotRecognised(raceId);
        int racePos = findRaceId(raceId);

        // return stage size
        return races.get(racePos).getStages().size();
    }

    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime, StageType type) throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
        checkRaceIdNotRecognised(raceId);
        checkIllegalStageName(stageName);
        checkInvalidNameException(stageName);
        checkInvalidLength(length);
        int racePos = findRaceId(raceId);

        // set and store new stage data in Stage ArrayList
        Stage temp = new Stage(raceId, stageName, description, length, startTime, type);

        // add stage to race
        races.get(racePos).addStage(temp);

        // calculate and update total stage length
        races.get(racePos).setRaceLength(calculateTotalRaceLength(raceId));

        return temp.getStageId();
    }

    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
        checkRaceIdNotRecognised(raceId);
        int racePos = findRaceId(raceId);

        // check if empty return empty array
        if (races.get(racePos).getStages().size() <= 0){
            return new int[0];
        }

        // get all stages of the list
        ArrayList<Stage> currStages = races.get(racePos).getStages();

        // sort according to stage start time
        Collections.sort(currStages, Comparator.comparing(Stage::getStageStartTime));

        // store and return in a simple int array
        int[] stageIds = new int[currStages.size()];
        for (int i = 0; i < currStages.size(); i++){
            stageIds[i] = currStages.get(i).getStageId();
        }

        return stageIds;
    }

    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);
        int[] ids = findAllIdsUsingStageId(stageId);

        // return stage length
        return races.get(ids[0]).getStages().get(ids[1]).getStageLength();
    }

    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);
        int[] ids = findAllIdsUsingStageId(stageId);
        int resPos[] = findResultsIndexArrayUsingStageId(stageId);

        // remove all results
        for (int i = 0; i < resPos.length; i++){
            results.remove(results.get(resPos[i]));
        }

        // removes stageId from raceId
        races.get(ids[0]).getStages().remove(races.get(ids[0]).getStages().get(ids[1]));
    }

    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, Double averageGradient, Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        checkStageIdNotRecognised(stageId);
        checkInvalidLocation(location);
        String state = checkStageState(stageId);
        if (state.equals("waiting for results")){
            throw new InvalidStageStateException("Stage must be in preparation to add a categorized climb!");
        }
        checkInvalidStageType(stageId);
        int[] ids = findAllIdsUsingStageId(stageId);

        // create new segment object and add new segment
        Segment temp = new Segment(stageId, location, type, averageGradient, length);
        races.get(ids[0]).getStages().get(ids[1]).addSegment(temp);
        return temp.getSegmentId();
    }

    @Override
    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        checkStageIdNotRecognised(stageId);
        checkInvalidLocation(location);
        if (checkStageState(stageId).equals("waiting for results")){
            throw new InvalidStageStateException("Stage must be in preparation to add an intermediate sprint!");
        }
        checkInvalidStageType(stageId);

        int[] ids = findAllIdsUsingStageId(stageId);

        // create new segment object and add new segment
        Segment temp = new Segment(stageId, location, SegmentType.SPRINT);
        races.get(ids[0]).getStages().get(ids[1]).addSegment(temp);

        return temp.getSegmentId();
    }

    @Override
    public void removeSegment(int segmentId) throws IDNotRecognisedException, InvalidStageStateException {
        // ids[0] = racePos, ids[1] = stagePos, ids[2] = segmentPos;
        int[] ids = checkSegmentIdNotRecognised(segmentId);
        if (checkStageState(ids[1]).equals("waiting for results")){
            throw new InvalidStageStateException("Stage must NOT be 'waiting for results' in order to remove a segment!");
        }

        // get and remove segment
        races.get(ids[0]).getStages().get(ids[1]).getSegments().remove(races.get(ids[0]).getStages().get(ids[1]).getSegments().get(ids[2]));
    }

    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
        checkStageIdNotRecognised(stageId);
        if (checkStageState(stageId).equals("waiting for results")){
            throw new InvalidStageStateException("This stage is already waiting for results!");
        }

        // get stageId and change stage state
        int[] ids = findAllIdsUsingStageId(stageId);
        races.get(ids[0]).getStages().get(ids[1]).setStageState("waiting for results");
    }

    @Override
    public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);
        int[] ids = findAllIdsUsingStageId(stageId);

        ArrayList<Segment> currentSegments = races.get(ids[0]).getStages().get(ids[1]).getSegments();

        Collections.sort(currentSegments, Comparator.comparing(Segment::getSegmentLocation));

        // loop and get all segments in the stage
        int[] segments = new int[currentSegments.size()];
        for (int i = 0; i < currentSegments.size(); i++){
            segments[i] = currentSegments.get(i).getSegmentId();
        }
        return segments;
    }

    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
        checkIllegalTeamName(name);
        checkInvalidNameException(name);

        // add team
        Team temp = new Team(name, description);
        teams.add(temp);
        return temp.getTeamId();
    }

    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {
        checkTeamIdNotRecognised(teamId);
        int counter = 0;
        for (Team i : teams){
            if (i.getTeamId() == teamId){
                break;
            }
            counter++;
        }

        // remove team
        teams.remove(teams.get(counter));
    }

    @Override
    public int[] getTeams() {
        // check for empty list
        if (teams.size() == 0){
            return new int[0];
        }

        // get all existing team ids and store in simple array
        int[] teamArray = new int[teams.size()];

        for (int i = 0; i < teams.size(); i++){
            teamArray[i] = teams.get(i).getTeamId();
        }

        return teamArray;
    }

    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
        checkTeamIdNotRecognised(teamId);

        ArrayList<Rider> curr = teams.get(teamId).getRiders();
        if (curr.size() == 0){
            return new int [0];
        }

        // get all riders
        int[] riderArray = new int[curr.size()];
        for (int i = 0; i < curr.size(); i++){
            riderArray[i] = curr.get(i).getRiderId();
        }

        return riderArray;
    }

    @Override
    public int createRider(int teamID, String name, int yearOfBirth) throws IDNotRecognisedException, IllegalArgumentException {
        checkTeamIdNotRecognised(teamID);
        checkIllegalArgument(name, yearOfBirth);

        // create new rider
        Rider temp = new Rider(name, yearOfBirth);
        teams.get(teamID).getRiders().add(temp);

        return temp.getRiderId();
    }

    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        int teamPos = checkRiderIdNotRecognised(riderId);
        int riderPos = findRiderId(riderId);

        // find all stage ids that the rider was involved in
        for (Race i : races){
            for (Stage j : i.getStages()){
                // remove rider results in this stage
                if (checkResultsUsingStageId(j.getStageId())){
                    // get all results pos
                    int[] resPos = findResultsIndexArrayUsingStageId(j.getStageId());
                    for (int k = 0; k < resPos.length; k++){
                        if (results.get(resPos[k]).getResultRiderId() == riderId){
                            results.remove(results.get(resPos[k]));
                        }
                    }
                }
            }
        }

        // remove rider
        teams.get(teamPos).getRiders().remove(teams.get(teamPos).getRiders().get(riderPos));
    }

    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints) throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException, InvalidStageStateException {
        checkRiderIdNotRecognised(riderId);
        checkStageIdNotRecognised(stageId);
        checkInvalidCheckpoints(stageId, checkpoints);
        if (!checkStageState(stageId).equals("waiting for results")){
            throw new InvalidStageStateException("Results can only be added while stage is waiting for results!");
        }
        checkDuplicatedResults(stageId, riderId);

        // calculate difference between 1st checkpoint and last checkpoint
        LocalTime startingTime = checkpoints[0];
        LocalTime finishTime = checkpoints[(checkpoints.length - 1)];
        int minutes = (int) MINUTES.between(startingTime, finishTime);

        // get time differences in minutes for processing
        int hours = minutes / 60;
        int min = minutes % 60;

        // formatting time into string
        String strTotalElapsed;
        if (hours < 10 ){
            strTotalElapsed = "0" + hours + ":" + min;
        }else {
            strTotalElapsed = hours + ":" + min;
        }

        // parsed to type LocalTime
        LocalTime parsedTotalElapsed = LocalTime.parse(strTotalElapsed);

        // get segment checkpoint times only
        ArrayList<LocalTime> segmentTimes = new ArrayList<>();

        // loop checkpoints to get non-starting and finishing times
        for (int i = 1; i < checkpoints.length-1; i++){
            segmentTimes.add(checkpoints[i]);
        }

        // initialize simple array
        LocalTime[] arrayTimes = new LocalTime[segmentTimes.size()];

        // convert arrayList to simple array
        for (int i = 0; i < segmentTimes.size(); i++){
            arrayTimes[i] = segmentTimes.get(i);
        }
        Result temp = new Result(stageId, riderId, parsedTotalElapsed, arrayTimes);
        results.add(temp);
    }

    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);
        checkRiderIdNotRecognised(riderId);
        int resultPos = findResultsIndexUsingStageIdAndRiderId(stageId, riderId);

        // retrieve checkpoints from results arraylist
        LocalTime[] checkpoints = results.get(resultPos).getCheckpoints();

        // store checkpoints in new array of size+1 to accommodate elapsed time
        LocalTime[] riderResults = new LocalTime[checkpoints.length+1];
        for (int i = 0; i < checkpoints.length; i++){
            riderResults[i] = checkpoints[i];
        }

        // store total elapsed time at the last slot of the array
        riderResults[riderResults.length-1] = results.get(resultPos).getElapsedTime();

        return riderResults;
    }

    /**
     * For the general classification, the aggregated time is based on the adjusted
     * elapsed time, not the real elapsed time. Adjustments are made to take into
     * account groups of riders finishing very close together, e.g., the peloton. If
     * a rider has a finishing time less than one second slower than the
     * previous rider, then their adjusted elapsed time is the smallest of both. For
     * instance, a stage with 200 riders finishing "together" (i.e., less than 1
     * second between consecutive riders), the adjusted elapsed time of all riders
     * should be the same as the first of all these riders, even if the real gap
     * between the 200th and the 1st rider is much bigger than 1 second. There is no
     * adjustments on elapsed time on time-trials.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @return The adjusted elapsed time for the rider in the stage. Return null if
     * there is no result registered for the rider in the stage.
     * @throws IDNotRecognisedException If the ID does not match to any rider or
     *                                  stage in the system.
     */
    @Override
    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
        checkRiderIdNotRecognised(riderId);
        checkStageIdNotRecognised(stageId);
        checkResultsUsingStageIdAndRiderId(stageId,riderId);


        return null;
    }

    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        checkRiderIdNotRecognised(riderId);
        checkStageIdNotRecognised(stageId);

        // find the results index in the arrayList and delete
        int resultsPos = findResultsIndexUsingStageIdAndRiderId(stageId, riderId);

        results.remove(results.get(resultsPos));
    }

    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);
        if (!checkResultsUsingStageId(stageId)){
            return new int[0];
        }

        // find results indexes of this stage
        int[] resultsPos = findResultsIndexArrayUsingStageId(stageId);

        // put in arrayList for sorting
        ArrayList<Result> stageResults = new ArrayList<>();
        for (int i = 0; i < resultsPos.length; i++){
            stageResults.add(results.get(resultsPos[i]));
        }

        // sort according to stage start time
        Collections.sort(stageResults, Comparator.comparing(Result::getElapsedTime));

        // to simple array
        int[] ids = new int[stageResults.size()];
        for (int i = 0; i < stageResults.size(); i++){
            ids[i] = stageResults.get(i).getResultRiderId();
        }

        return ids;
    }

    /**
     * Get the adjusted elapsed times of riders in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of adjusted elapsed times sorted by their finish
     * time. An empty list if there is no result for the stage. These times
     * should match the riders returned by
     * {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);
        checkResultsUsingStageId(stageId);

        return new LocalTime[0];
    }

    /**
     * Get the number of points obtained by each rider in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of points each rider received in the stage, sorted
     * by their elapsed time. An empty list if there is no result for the
     * stage. These points should match the riders returned by
     * {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);


        return new int[0];
    }

    /**
     * Get the number of mountain points obtained by each rider in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of mountain points each rider received in the stage,
     * sorted by their finish time. An empty list if there is no result for
     * the stage. These points should match the riders returned by
     * {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        checkStageIdNotRecognised(stageId);

        getRidersRankInStage(stageId);

        ArrayList<Integer> points = new ArrayList<>();
        int[] resultsId = findResultsIndexArrayUsingStageId(stageId);
        ArrayList<Result> resArray = new ArrayList<>();

        // get location for every segment

        // get the time of when the rider reaches the segment

        // add mountain points
        for (int i = 0; i < resultsId.length; i++){
            resArray.add(results.get(resultsId[i]));
        }

        return new int[0];
    }

    @Override
    public void eraseCyclingPortal() {
        // clear all arrayLists
        races.clear();
        teams.clear();
        results.clear();
    }

    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)){

            // serialize all arraylists
            objectOut.writeObject(races);
            objectOut.writeObject(teams);
            objectOut.writeObject(results);

            System.out.println("All ArrayLists have been serialized successfully.\n");
        } catch (IOException ex){
            ex.printStackTrace();
            throw new IOException("Error while serializing portal!");
        }
    }

    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException, InvalidClassException{
        try( ObjectInputStream objectIn = new ObjectInputStream(new
                FileInputStream(filename))) {

            ArrayList<Race> raceTemp = new ArrayList<>();
            ArrayList<Team> teamTemp = new ArrayList<>();
            ArrayList<Result> resultsTemp = new ArrayList<>();

            // count to 3 : race, team, results arrayLists
            int counter = 0;
            while (counter < 3) {
                // read first object
                Object obj = objectIn.readObject();

                // check if type is of ArrayList, cast to variable arrayL
                if (obj instanceof ArrayList<?> arrayL) {

                    // loop through cast object
                    for (int i = 0; i < arrayL.size(); i++) {
                        // get each line from object
                        Object o = arrayL.get(i);

                        // check instanceof
                        if (o instanceof Race) {
                            // add to temp ArrayList
                            raceTemp.add((Race) o);
                        } else if (o instanceof Team) {
                            // add to temp ArrayList
                            teamTemp.add((Team) o);
                        } else if (o instanceof Result) {
                            // add to temp ArrayList
                            resultsTemp.add((Result) o);
                        }
                    }
                }
                counter++;
            }

            // assign temp to actual arrayLists
            races = raceTemp;
            teams = teamTemp;
            results = resultsTemp;

            System.out.println("Portal successfully loaded and deserialized!\n");

        }catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            throw new IOException("Error while trying to load cycling portal!");
        }
    }

//================ Self-written 1  =====================================================

    /**
     * Checks a string, throw exception if it's null, empty or greater than 30 characters.
     *
     * @param name String of the name to be checked.
     * @throws InvalidNameException If name is an empty string or if
     *                              name length is greater than 30 chars.
     */
    private void checkInvalidNameException(String name) throws InvalidNameException{
        // throws exception if name is invalid
        if (name.trim().isEmpty() || name.length() > 30) {
            throw new InvalidNameException(name + " is invalid!");
        }
    }

    /**
     * Checks if the team name is unique and doesn't already exist.
     * Throws an exception if otherwise.
     *
     * @param name String of the team name to be checked.
     * @throws IllegalNameException If the team name already exists in the system.
     *
     */
    private void checkIllegalTeamName(String name) throws IllegalNameException {
        // check for existing team name
        for (Team i : teams) {
            if (i.getTeamName().equals(name)) {
                throw new IllegalNameException("Team name: '" + name + "' already exists!");
            }
        }
    }

    /**
     * Checks if the race name is unique and doesn't already exist.
     * Throws an exception if otherwise.
     *
     * @param raceName String of the race name to be checked.
     * @throws IllegalNameException If the race name already exists in the system.
     *
     */
    private void checkIllegalRaceName (String raceName) throws IllegalNameException{
        // check for existing race name
        for (Race i : races) {
            if (i.getRaceName().equals(raceName)) {
                throw new IllegalNameException("Race name: '" + raceName + "' already exists!");
            }
        }
    }

    /**
     * Checks if length is greater than 5.0
     *
     * @param length Length of type double to be checked.
     * @throws InvalidLengthException If length is less than 5.0.
     *
     */
    private void checkInvalidLength (double length) throws InvalidLengthException {
        if (length < 5.0){
            throw new InvalidLengthException("Length must be less than 5km!");
        }
    }

    /**
     * Check if location is greater than 5.0
     *
     * @param location Location type Double to be checked.
     * @throws InvalidLocationException If location is less than 5.0.
     *
     */
    private void checkInvalidLocation (double location) throws InvalidLocationException {
        if (location < 5.0){
            throw new InvalidLocationException("Location must be less than 5km!");
        }
    }


    /**
     * Confirms if race ID exists in the system to prevent IndexOutOfBounds exception.
     *
     * @param id The Race ID to be checked
     * @throws IDNotRecognisedException If Race ID doesn't exist: is not found in the system.
     *
     */
    private void checkRaceIdNotRecognised (int id) throws IDNotRecognisedException {
        // loop through all races to check for a matching id
        for (Race i : races){
            if (i.getRaceId() == id){
                return;
            }
        }
        throw new IDNotRecognisedException("Race ID: '"+ id + "' doesn't exist!");
    }

    /**
     * Checks if the stage name is unique/ doesn't already exist in the system.
     *
     * @param stageName String of the stage name to be checked
     * @throws IllegalNameException If the stage name already exists.
     *
     */
    private void checkIllegalStageName (String stageName) throws IllegalNameException{
        // loop through races
        for (Race i : races){
            // loop through all stages in the current race
            for (Stage j : i.getStages()){
                // check for a match
                if (j.getStageName().equals(stageName)){
                    throw new IllegalNameException("Stage Name: '" + stageName + "' already exists!");
                }
            }
        }
    }


    /**
     * Confirms if the stage ID exists in the system to prevent IndexOutOfBounds exception.
     *
     * @param stageId The stageId to be checked.
     * @throws IDNotRecognisedException If the stageId doesn't exist.
     *
     */
    private void checkStageIdNotRecognised(int stageId) throws IDNotRecognisedException{
        // check for ID match in all races
        for (Race i : races){
            for (Stage j : i.getStages()){
                if (j.getStageId() == stageId){
                    return;
                }
            }
        }
        throw new IDNotRecognisedException("Stage ID: '" + stageId + "' doesn't exist!");
    }


    /**
     * Returns the stage state of a stageId.
     *
     * @param stageId The stageId to be checked.
     * @return String stageState of the stageId.
     *
     */
    public String checkStageState(int stageId){
        // returns stage state
        int[] ids = findAllIdsUsingStageId(stageId);

        return races.get(ids[0]).getStages().get(ids[1]).getStageState();
    }

    /**
     * Confirms that the queried stageType is NOT a time-trial.
     *
     * @param stageId The stageId to be checked.
     * @throws InvalidStageTypeException If the stageType is a time-trial.
     *
     */
    private void checkInvalidStageType(int stageId) throws InvalidStageTypeException {
        int[] ids = findAllIdsUsingStageId(stageId);

        if (races.get(ids[0]).getStages().get(ids[1]).getStageType() == StageType.TT ){
            throw new InvalidStageTypeException("Time-trial stages cannot contain any segments!");
        }
    }


    /**
     * Confirms if the teamId exists in the system to prevent IndexOutOfBounds exception.
     *
     * @param teamId The teamId to be checked.
     * @throws IDNotRecognisedException If the teamId is not found in the system.
     *
     */
    public void checkTeamIdNotRecognised(int teamId) throws IDNotRecognisedException {
        // throws exception if team id doesn't exist
        for (Team i: teams){
            if (i.getTeamId() == teamId){
                return;
            }
        }
        throw new IDNotRecognisedException("Team ID: '" + teamId + "' doesn't exist!");
    }

    /**
     * Checks if the riderName and yearOfBirth is valid before adding new rider.
     *
     * @param name The name of the rider to be checked.
     * @param yearOfBirth The year of birth to be checked.
     * @throws IllegalArgumentException If rider name is empty OR if the
     *                                  year of birth is less than 1900
     */
    public void checkIllegalArgument(String name, int yearOfBirth) throws IllegalArgumentException {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Rider name cannot be empty!");
        }
        else if (yearOfBirth < 1900){
            throw new IllegalArgumentException("Rider year of birth must be greater than 1900!");
        }
    }

    /**
     * Check if the riderId exists in the system.
     *
     * @param riderId The ID of the rider to be checked.
     * @return Index of the team which the rider belongs to.
     * @throws IDNotRecognisedException If rider cannot be found in the system.
     */
    public int checkRiderIdNotRecognised(int riderId) throws IDNotRecognisedException {
        // throws exception if riderId doesn't exist
        int teamPos = 0;
        for (Team i : teams){
            for (Rider j : i.getRiders()){
                if (j.getRiderId() == riderId){
                    return teamPos;
                }
            }
            teamPos++;
        }
        throw new IDNotRecognisedException("Rider ID: '" + riderId + "' doesn't exist!");
    }

    /**
     * Check if the length of checkpoints are valid before trying to
     * register rider results.
     *
     *
     * @param stageId The stageId of which the checkpoints are t
     * @param checkpoints The array of checkpoints to be checked.
     * @throws InvalidCheckpointsException If the length of the checkpoints are NOT
     *                                     +2 of the segments in that stage. The 2 other
     *                                     times are to represent startTime and finishTime.
     *
     */
    public void checkInvalidCheckpoints (int stageId, LocalTime... checkpoints) throws InvalidCheckpointsException{
        // throws exception if checkpoints are not the correct length
        for (Race i : races){
            for (Stage j : i.getStages()){
                if (j.getStageId() == stageId){
                    int criteria = j.getSegments().size() + 2;
                    if (checkpoints.length != criteria){
                        throw new InvalidCheckpointsException("Checkpoint length for this race must be > " + criteria);
                    }
                }
            }
        }
    }

    /**
     * Checks if there's already a result registered to the queried rider and stage.
     *
     * @param stageId The stageId to be checked
     * @param riderId The riderId to be checked.
     * @throws DuplicatedResultException If there are existing results for the queried
     *                                   rider and stage.
     *
     */
    public void checkDuplicatedResults (int stageId, int riderId) throws DuplicatedResultException{
        // throws exception if there's existing results
        for (Result i : results){
            if (i.getResultRiderId() == riderId && i.getResultStageId() == stageId){
                throw new DuplicatedResultException("Results have already been registered for this rider and stage!");
            }
        }
    }


    /**
     * Checks if the queried segmentId exists in the system.
     *
     * @param segmentId The segmentId to be checked.
     * @return An array storing the indexes needed to access the segmentId.
     *         int[0] = race index,
     *         int[1] = stage index,
     *         int[2] = and segment index.
     * @throws IDNotRecognisedException If the segmentId doesn't exist in the system.
     *
     */
    public int[] checkSegmentIdNotRecognised(int segmentId) throws IDNotRecognisedException{
        // throws exception if existing segmentId cannot be found
        boolean found = false;

        // loop through races, stages and segments
        for (int i = 0; i < races.size(); i++){
            for (int j = 0; j < races.get(i).getStages().size(); j++){
                for (int k = 0; k < races.get(i).getStages().get(j).getSegments().size(); k++){
                    // segment found
                    if (races.get(i).getStages().get(j).getSegments().get(k).getSegmentId() == segmentId){
                        // return positions
                        return new int[]{i,j,k};
                    }
                }
            }
        }
        if (!found){
            throw new IDNotRecognisedException("Segment ID: '" + segmentId + "' doesn't exist!");
        }

        return new int[0];
    }


    /**
     * Check if there are results registered for the queried stage.
     *
     * @param stageId The stageId to be checked.
     * @return true : results exists for this stage,
     *         false : results not found for this stage.
     *
     */
    public boolean checkResultsUsingStageId(int stageId){
        // return true if there's existing results for this stage

        // loop through all results
        for (Result i : results){
            if (i.getResultStageId() == stageId){
                return true;
            }
        }
        return false;
    }


    /**
     * Check if there are results registered for the queried
     *  stage AND rider.
     *
     * @param stageId The stageId to be checked.
     * @param riderId The riderId to be checked.
     * @return true : results exist for this stage and rider,
     *         false : results doesn't exist for this stage and rider.
     *
     */
    public boolean checkResultsUsingStageIdAndRiderId(int stageId, int riderId){
        // return true if results found for this stage and rider
        for (Result i : results){
            if (i.getResultStageId() == stageId || i.getResultRiderId() == riderId){
                return true;
            }
        }
        return false;
    }

    /**
     * Get the indexes of all results registered to the queried stage.
     *
     * @param stageId The stageId to be queried.
     * @return Array containing the indexes of results registered to
     *         the queried stage.
     * @throws IDNotRecognisedException If there are no results registered
     *                                  for this stage.
     *
     */
    public int[] findResultsIndexArrayUsingStageId(int stageId) throws IDNotRecognisedException{
        // initialize empty arrayList
        ArrayList<Integer> resultsPos = new ArrayList<>();
        boolean found = false;
        int count = 0;

        // find matching ids in results
        for (Result i : results){
            if (i.getResultStageId() == stageId){
                // add index to arrayList
                resultsPos.add(count);
                found = true;
            }
            count++;
        }

        if (!found){
            throw new IDNotRecognisedException("Results not found for stage ID: " + stageId);
        }

        // indexes arrayList to simple int
        int[] results = new int[resultsPos.size()];
        for (int i = 0; i < resultsPos.size(); i++){
            results[i] = resultsPos.get(i);
        }

        return results;
    }

    /**
     * Get the index of the result registered to the queried
     * stage AND rider.
     *
     * @param stageId The stageId to be queried.
     * @param riderId The riderId to be queried.
     * @return The index of the results registered to the stage
     *         and rider.
     * @throws IDNotRecognisedException If no results are found for the
     *                                  queried stage and rider.
     *
     */
    public int findResultsIndexUsingStageIdAndRiderId (int stageId, int riderId) throws IDNotRecognisedException{
        // return results index for this stage and rider

        int resultPos = 0;
        boolean found = false;

        // search for a match in results
        for (Result i : results){
            if (i.getResultStageId() == stageId && i.getResultRiderId() == riderId){
                return resultPos;
            }
            resultPos++;
        }

        if (!found){
            throw new IDNotRecognisedException("Results not found for these IDs!");
        }

        return 0;
    }

    //========= Non-exception related functions ============================================================

    /**
     * Get the index of this raceId.
     *
     * @param raceId The raceId to be queried.
     * @return The index of the raceId in the races ArrayList.
     * 
     */
    public int findRaceId (int raceId){
        // returns index for this raceId

        int racePos = 0;
        for (Race i : races){
            if(i.getRaceId() == raceId){
                return racePos;
            }
            racePos++;
        }
        return 0;
    }


    /**
     * Get the index of the queried riderId.
     *
     * @param riderId The riderId to be queried.
     * @return The index of the riderId in the riders ArrayList.
     *
     */
    public int findRiderId(int riderId){
        // returns rider index in riders ArrayList
        for (Team i : teams){
            int riderPos = 0;
            for (Rider j : i.getRiders()){
                if (j.getRiderId() == riderId){
                    return riderPos;
                }
                riderPos++;
            }
        }
        return 0;
    }


    /**
     * Get the indexes necessary to access the queried stageId.
     *
     * @param stageId The stageId to be queried.
     * @return An array of the indexes to access the queried stageId.
     *         int[0] = race index,
     *         int[1] = stage index.
     *         
     */
    private int[] findAllIdsUsingStageId(int stageId){
        // returns the race and stage index of this stage

        int racePos = 0;
        for (Race i : races){
            int stagePos = 0;
            for (Stage j : i.getStages()){
                if (j.getStageId() == stageId){
                    return new int[]{racePos,stagePos};
                }
                stagePos++;
            }
            racePos++;
        }
        return new int[]{0,0};
    }

    /**
     * Calculates the total length of the race : 
     * The sum of all stage lengths in that race.
     * 
     * Called whenever a new stage is added to the race.
     *
     * @param raceId The raceId of the calculation.
     * @return The total length of the race.
     * 
     */
    public double calculateTotalRaceLength(int raceId){
        int racePos = findRaceId(raceId);
        double total = 0;

        // sum of all stage lengths
        for (Stage j : races.get(racePos).getStages()){
            total += j.getStageLength();
        }

        return total;
    }

    /**
     * Get the indexes of results registered to the queried race.
     * 
     * @param raceId The raceId to be queried.
     * @return An array of indexes of results registered to this race.
     * 
     */
    public int[] findResultsIndexArrayUsingRaceId (int raceId){
        // returns array of results indexes for this race
        int racePos = findRaceId(raceId);

        // use raceId to get all stageIds
        ArrayList<Stage> stages = races.get(racePos).getStages();
        int[] stageIds = new int[stages.size()];
        //ArrayList<Integer> resultPos = new ArrayList<>();

        int counter = 0;
        for (Stage i : stages){
            stageIds[counter] = i.getStageId();
            counter++;
        }

        // use stageId array to find all matching results
        counter = 0;
        ArrayList<Integer> tempArray = new ArrayList<>();

        for (Result i : results){
            // check for matches in the array of stageIds
            for (int j = 0; j < stageIds.length; j++){
                if (i.getResultStageId() == stageIds[j]){
                    tempArray.add(counter);
                }
            }
            counter++;
        }

        // to simple array
        int[] idArray = new int[tempArray.size()];
        for (int i = 0; i < tempArray.size(); i++){
            idArray[i] = tempArray.get(i);
        }

        return idArray;
    }

}
