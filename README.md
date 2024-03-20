# A back-end Java cycling portal package

ECM1410 Continuous Assessment.

The system handles:
1. Rider Management
2. Race Design
3. Race Results

At the end of the game, the program generates output files for each player, containing a complete log of the player's actions. The deck output file contains the deck contents of each player at the end of the game.

### Running the test file
Ensure java is installed. Run `java -version` on the device's terminal to display the currently installed java version. If Java is not installed, it can be done from [this link here](https://www.java.com/en/download/help/download_options.html)

## Running Tests
Navigate to the `src` folder containing `CyclingPortalInterfaceTestApp.java`. 

Then to run the test suite, run the following commands.

``` cmd
javac -cp .:cycling.jar CyclingPortalInterfaceTestApp.java
java -cp .:cycling.jar CyclingPortalInterfaceTestApp
```

Authors
Daphne Yap
