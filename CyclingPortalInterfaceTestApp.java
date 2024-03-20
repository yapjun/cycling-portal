import cycling.*;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortalInterface interface -- note you
 * will want to increase these checks, and run it on your CyclingPortal class
 * (not the BadCyclingPortal class).
 *
 * 
 * @author Daphne Yap
 * @version 1.0
 */

public class CyclingPortalInterfaceTestApp implements Serializable {

	/**
	 * Test method.
	 * 
	 * @param args not used
	 */

	public static void main(String[] args) throws ClassNotFoundException, IOException, InvalidStageStateException, InvalidLocationException, DuplicatedResultException, InvalidCheckpointsException, InvalidLengthException, InvalidStageTypeException, InvalidNameException, InvalidNameException, IDNotRecognisedException, IllegalNameException{
		System.out.println("The system compiled and started the execution...");

		MiniCyclingPortalInterface testPortal = new CyclingPortal();


		testPortal.createRace("Race 1", "RACE 1 description");
		testPortal.createRace("Race 2", "RACE 2 description");
		assert(testPortal.getRaceIds().length == 2)
				: "Portal should have 2 races!";

		assert(testPortal.getTeams().length == 0)
				: "Initial Portal length should be 0!";
		testPortal.createTeam("Team 1", "TEAM 1 description");
		testPortal.createTeam("Team 2", "TEAM 2 description");
		testPortal.createTeam("Team 3", "TEAM 3 description");
		testPortal.createTeam("Team 4", "TEAM 4 description");
		testPortal.createTeam("Team 5", "TEAM 5 description");
		testPortal.createTeam("Team 6", "TEAM 6 description");
		assert(testPortal.getTeams().length == 6)
				: "Portal should have 6 teams!";

		testPortal.createRider(1,"John", 1990);
		testPortal.createRider(2,"Ken", 1991);
		testPortal.createRider(2,"Sam", 1992);
		testPortal.createRider(3,"Bob", 1993);
		testPortal.createRider(3,"Rob", 1994);
		testPortal.createRider(4,"Hob", 1995);
		assert (testPortal.getTeamRiders(2).length == 2)
				: "Team 2 should have 2 riders!";

		testPortal.createRace("Race 3", "RACE 3 description");
		testPortal.addStageToRace(1,
					"Stage 1 for Race 1",
					"Stage 1 description",
					13.55,
					LocalDateTime.now(),
					StageType.HIGH_MOUNTAIN);

		testPortal.addStageToRace(1,
				"Stage 2 for Race 1",
				"Stage 2 description",
				8,
				LocalDateTime.now(),
				StageType.HIGH_MOUNTAIN);
		testPortal.addStageToRace(2,
				"Stage 3 for Race 2",
				"Stage 3 description",
				6.69,
				LocalDateTime.now(),
				StageType.FLAT);
		assert(testPortal.getRaceStages(1).length == 2)
				: "Race 1 should have 2 stages!";

		testPortal.addCategorizedClimbToStage(1,7.5,SegmentType.C2, 2.3, 6.4);
		testPortal.concludeStagePreparation(2);

		testPortal.addIntermediateSprintToStage(3,12.2);
		testPortal.addIntermediateSprintToStage(3,7.2);
		testPortal.addCategorizedClimbToStage(3,6.2,SegmentType.C3, 2.9,8.42);
		testPortal.concludeStagePreparation(3);
		assert(testPortal.getStageSegments(3).length == 3)
				: "Stage 3 should have 3 segments!";

		LocalTime[] checkpoints1 = {LocalTime.parse("09:12"), LocalTime.parse("10:34"), LocalTime.parse("11:45"), LocalTime.parse("13:12"), LocalTime.parse("16:34")};
		testPortal.registerRiderResultsInStage(3,2, checkpoints1);
		LocalTime[] checkpoints2 = {LocalTime.parse("03:22"), LocalTime.parse("06:43"), LocalTime.parse("07:51"), LocalTime.parse("09:12"), LocalTime.parse("10:12")};
		testPortal.registerRiderResultsInStage(3,3, checkpoints2);
		LocalTime[] checkpoints3 = {LocalTime.parse("12:57"), LocalTime.parse("13:31"), LocalTime.parse("14:25"), LocalTime.parse("16:13"), LocalTime.parse("18:44")};

		testPortal.registerRiderResultsInStage(3,4, checkpoints3);
		assert(testPortal.getRidersRankInStage(3).length == 3)
				: "Stage 3 should have 3 results!";

		testPortal.deleteRiderResultsInStage(3,3);
		assert(testPortal.getRidersRankInStage(3).length == 2)
				: "Stage 3 should have 2 results!";

		testPortal.removeSegment(2);
		testPortal.saveCyclingPortal("testSave.ser");
		testPortal.loadCyclingPortal("testSave.ser");
		assert(testPortal.getTeams().length == 6)
				: "Portal should have 6 teams!";
		assert(testPortal.getRaceIds().length == 3)
				: "Portal should have 3 races!";

	}
}
