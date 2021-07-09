import java.io.File;
import java.util.*;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class C3PO {

    // List of all the variables, which won't be modified afterwards, necessary to compute the odds to arrive on time
    // on Endor
    public long AUTONOMY;
    public JSONArray ROUTES;
    public long COUNTDOWN;
    public JSONArray BOUNTY_HUNTERS_POSITIONS;
    public final String DESTINATION = "Endor";

    // List of all the variables that can be modified through the program
    public long daysInCommonWithBountyHunters = 0;
    public String currentPlanet = "Tatooine";
    public long numberOfDaysInTravel = 0;
    public long currentLevelOfFuel;
    public ArrayList<Double> allPossibleOdds = new ArrayList<>();


    public C3PO(File milleniumFalconJsonFilePath) throws IOException, ParseException {
        // The goal of this function is to initialize the class C3PO
        // It also reads ans interprets the data given in the Json file regarding the Millenium Falcon's
        // choices of paths

        //Json parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        JSONObject object = (JSONObject) jsonParser.parse(new FileReader(milleniumFalconJsonFilePath));

        AUTONOMY = (long) object.get("autonomy");
        currentLevelOfFuel = AUTONOMY;
        ROUTES = (JSONArray) object.get("routes");
    }


    public double giveMeTheOdds(File empireJsonFile) throws IOException, ParseException {
        // Function that returns the odds of success of arriving on Endor on time before the end of the countdown
        // Another goal is to read and interpret the data given in the json file regarding the movment of Bounty
        // Hunters on planets

        //Json parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        JSONObject object = (JSONObject) jsonParser.parse(new FileReader(empireJsonFile));

        COUNTDOWN = (long) object.get("countdown");
        BOUNTY_HUNTERS_POSITIONS = (JSONArray) object.get("bounty_hunters");

        // We retrieve the list of all the possible odds of getting to Endor on time and store it in a variable
        ArrayList<Double> oddsList = variousPathsOptions(currentPlanet,
                currentLevelOfFuel,
                numberOfDaysInTravel,
                daysInCommonWithBountyHunters);

        // If the list retrieved is empty, it means that there is no possible path to reach Endor in time before
        // the end of the countdown
        if (oddsList.isEmpty()) {
            System.out.println("There is no possible path to reach Endor in time before the end of the countdown");
            System.out.println("The odds of success are in consequence : 0");
            return 0.0;
        }
        // Otherwise, there is a least one possible path to reach Endor in time, we just need the one with the
        // best odds of success
        else {
            Collections.sort(oddsList);
            System.out.println("There is at least one path to reach Endor before the end of the countdown");
            System.out.println("The best odds of success are : " + oddsList.get(oddsList.size() - 1));
            return oddsList.get(oddsList.size() - 1);
        }
    }



    public double oddsMathematicalFormula(long daysInCommonWithBountyHunters) {
        // Function that computes the odds of being caught by Bounty Hunters depending on the number of days
        // they spent in commonn on various planets during the flight

        // If the Millenium Falcon never landed on a planet on a day Bounty Hunters were present, then the probability
        // of being caught is null
        if (daysInCommonWithBountyHunters == 0) {
            return 0.0;
        }
        // Otherwise, thanks to a mathematical formula, we can compute the odds of being caught for a specific path
        // with a recursive function
        else {
            return ((Math.pow(9, daysInCommonWithBountyHunters -1) / Math.pow(10, daysInCommonWithBountyHunters)) +
                    oddsMathematicalFormula(daysInCommonWithBountyHunters -1));
        }
    }

    public boolean hasEnoughFuel(long currentLevelOfFuel, long travelTime) {
        // Function that estimates if the Millenium Falcon has enough fuel for a specific hyperspeed jump
        // Boolean function that returns TRUE if the tank has enough fuel, FALSE otherwise
        return (currentLevelOfFuel - travelTime >= 0);
    }

    public boolean planetHasBountyHunters(String currentPlanet, long numberOfDaysInTravel) {
        // Function that estimates if the Millenium is on a planet where Bounty Hunters are also present

        // With a loop FOR, we search into the list of Bounty Hunters postitions
        for (Object bounty_hunters_position : BOUNTY_HUNTERS_POSITIONS) {
            // If Bounty Hunters and the Millenium Falcon are on the same planet on the same day, we return TRUE
            JSONObject bountyHuntersPositions = (JSONObject) bounty_hunters_position;
            String bountyHuntersPlanet = (String) bountyHuntersPositions.get("planet");
            long bountyHuntersDay = (long) bountyHuntersPositions.get("day");
            if (bountyHuntersPlanet.equals(currentPlanet) && bountyHuntersDay == numberOfDaysInTravel) {
                return true;
            }

        }
        // Once, all the positions of the Bounty Hunters have been checked and the function did not return TRUE,
        // it means that there is no Bounty Hunters on the planet at the same time the Millenium Falcon is
        return false;
    }

    public boolean nextPlanetHasBountyHunters(String nextPlanet, long numberOfDaysInTravel,
                                              long travelTime) {
        // Function that estimates if the Millenium will be on a planet where Bounty Hunters will also be present

        // With a loop FOR, we search into the list of Bounty Hunters postitions
        for (Object bounty_hunters_position : BOUNTY_HUNTERS_POSITIONS) {
            // If Bounty Hunters and the Millenium Falcon will be on the same planet on the same day, we return TRUE
            JSONObject bountyHuntersPositions = (JSONObject) bounty_hunters_position;
            String bountyHuntersPlanet = (String) bountyHuntersPositions.get("planet");
            long bountyHuntersDay = (long) bountyHuntersPositions.get("day");
            if (bountyHuntersPlanet.equals(nextPlanet) && bountyHuntersDay == numberOfDaysInTravel + travelTime) {
                return true;
            }
        }
        // Once, all the positions of the Bounty Hunters have been checked and the function did not return TRUE, it
        // means that there is no Bounty Hunters on the planet at the same time the Millenium Falcon will be
        return false;
    }

    public String nextPlanet(String currentPlanet, JSONObject routes) {
        // Function that returns the next planet of the path after finding a route which has its origin or destination
        // that is the current planet the Millenium Falcon is on

        // If the current planet corresponds to the origin key of the route, then the next planet is the destination key
        String routeOrigin = (String) routes.get("origin");
        String routeDestination = (String) routes.get("destination");
        if (routeOrigin.equals(currentPlanet)) {
            return routeDestination;
        }
        // Otherwise, it means the current planet corresponds to the destination key of the route, so the next
        // planet is the origin key
        else {
            return routeOrigin;
        }
    }

    public boolean planetHasBountyHuntersOneDayAfterArrival(String nextPlanet, long numberOfDaysInTravel,
                                                            long travelTime) {
        // Function that estimates if there will be Bounty Hunters one day after the Millenium arrives on a planet with an almost or totally empty tank

        // With a loop FOR, we search into the list of Bounty Hunters
        for (Object bounty_hunters_position : BOUNTY_HUNTERS_POSITIONS) {
            // If the Bounty Hunters are on a planet one day after the Millenium Falcon has arrived, then we return TRUE
            JSONObject bountyHuntersPositions = (JSONObject) bounty_hunters_position;
            String bountyHuntersPlanet = (String) bountyHuntersPositions.get("planet");
            long bountyHuntersDay = (long) bountyHuntersPositions.get("day");
            if (bountyHuntersPlanet.equals(nextPlanet) && bountyHuntersDay == numberOfDaysInTravel + travelTime + 1) {
                return true;
            }
        }
        // Once, all the positions of the Bounty Hunters have been checked and the function did not return TRUE, it
        // means that there is no Bounty Hunters on the planet at the same time the Millenium Falcon will be
        return false;
    }

    public void enoughFuelAndTimeToTravel(String currentPlanet,
                                          String nextPlanet,
                                          long currentLevelOfFuel,
                                          long numberOfDaysInTravel,
                                          long daysInCommonWithBountyHunters,
                                          long travelTime) {
        // Function that allows the Millenium Falcon to travel because it has time to reach the next planet and enough
        // fuel to make the hyperspeepd jump

        // If the next planet has Bounty Hunters on it, there are 2 options :
        // - 1: the Millenium Falcon stays one more day on its current planet (also refueling) and verify the next
        // day if there are still Bounty Hunters on the next planet
        // - 2: the Millenium Falcon decides to go to the planet anyway, risking to get caught
        if (nextPlanetHasBountyHunters(nextPlanet, numberOfDaysInTravel, travelTime)
                || planetHasBountyHuntersOneDayAfterArrival(nextPlanet, numberOfDaysInTravel, travelTime)) {
            // Option 1:
            // Because the Millenium Falcon stays another day on its current planet, we need to recheck if Bounty
            // Hunters are on the same planet, or if it has time to reach
            // the planet before the end of the countdown
            // If the Millenium Falcon has enough time and fuel, because it stayed one day, the tank of fuel is now full
            if (travelTime + numberOfDaysInTravel + 1 <= COUNTDOWN) {
                currentLevelOfFuel = AUTONOMY;

                if (planetHasBountyHunters(currentPlanet, numberOfDaysInTravel + 1)) {
                    daysInCommonWithBountyHunters += 1;
                }
                // With a recursive way, we call again the function with the changed parameters in cas there are
                // again Bounty Hunters on the next planet
                enoughFuelAndTimeToTravel(currentPlanet, nextPlanet, currentLevelOfFuel, numberOfDaysInTravel + 1,
                        daysInCommonWithBountyHunters, travelTime);
            // If the Millenium Falcon now does not have time to reach the next planet in time to reach Endor,
            // we need to rechanged the parameters as they were before so the loop can go on
            }
            // The option 2 does not need any particular modifications to be come true
        }
        // Even if there are Bounty Hunters the same day or one day after the Millenium Falcon arrives on the next
        // planet, it can still travel to this next planet
        // This time, the Millenium Falcon goes to the next planet, so we have to restart the process from scratch to
        // verify if it has enough time and fuel
        numberOfDaysInTravel += travelTime;
        currentLevelOfFuel -= travelTime;
        currentPlanet = nextPlanet;
        allPossibleOdds = variousPathsOptions(currentPlanet, currentLevelOfFuel, numberOfDaysInTravel,
                daysInCommonWithBountyHunters);
    }

    public ArrayList<Double> variousPathsOptions(String currentPlanet,
                                            long currentLevelOfFuel,
                                            long numberOfDaysInTravel,
                                            long daysInCommonWithBountyHunters) {
        // Function that calculates the various paths options for the Millenium Falcon in order to arrive before the
        // end of the countdown on Endor
        // Of course, we begin thz travel on Tatooine with a tank full of fuel

        // If the planet the Millenium Falcon is on is currently occupied by Bounty Hunters, then we increment by 1
        // daysInCommonWithBountyHunters
        if (planetHasBountyHunters(currentPlanet, numberOfDaysInTravel)) {
            daysInCommonWithBountyHunters += 1;
        }

        // If the current planet the Millenium Falcon is on is the final destination of the trip Endor, then it means that the Millenium Falcon has arrived on time
        // So we calculate the odds of success of this path, and return it
        if (currentPlanet.equals(DESTINATION)) {
            allPossibleOdds.add(1.0 - oddsMathematicalFormula(daysInCommonWithBountyHunters));
        }
        // Otherwise, we continue to search paths that lead to Endor in time
        else {
            // We search into the routes list to look for a path that can lead to Endor in time
            for (Object route : ROUTES) {
                // If one of the routes has the current planet as its origin or destination and the Millenium Falcon
                // has enough time to travel to this next planet
                JSONObject routes = (JSONObject) route;
                String routeOrigin = (String) routes.get("origin");
                String routeDestination = (String) routes.get("destination");
                long routeTravelTime = (long) routes.get("travelTime");
                if ((routeOrigin.equals(currentPlanet) || routeDestination.equals(currentPlanet))
                        && (routeTravelTime + numberOfDaysInTravel <= COUNTDOWN)) {
                    // We use the nextPlanet function to determine which is the next planet of the path
                    String nextPlanet = nextPlanet(currentPlanet, routes);

                    // If the tank of the Millenium Falcon is full enough to make entirely the hyperspeed jump to
                    // the next planet
                    if (hasEnoughFuel(currentLevelOfFuel, routeTravelTime)) {
                        // We call the function that allows the Millenium Falcon to travel beacuse it has enough
                        // fuel and time
                        enoughFuelAndTimeToTravel(currentPlanet, nextPlanet, currentLevelOfFuel, numberOfDaysInTravel,
                                daysInCommonWithBountyHunters, routeTravelTime);
                    }
                    // If the tank is not empty enough to make the hyperspeed jump, the Millenium Falcon stays
                    // one day on its current planet to refuel
                    else {
                        numberOfDaysInTravel += 1;
                        currentLevelOfFuel = AUTONOMY;
                        allPossibleOdds = variousPathsOptions(currentPlanet, currentLevelOfFuel, numberOfDaysInTravel,
                                daysInCommonWithBountyHunters);
                    }
                }
            }
            //If the path can't lead to the destination Endor, we still need to return the list of possible odds
        }
        return allPossibleOdds;
    }


}

