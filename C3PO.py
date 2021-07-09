class C3PO:

	def __init__(self, MillenniumFalconJsonFilePath):
		# The goal of this function is to initialize the class C3PO
		# It also reads ans interprets the data given in the Json file regarding the Millennium Falcon's choices
		# of paths

		# List of all the variables, which won't be modified afterwards, necessary to compute the odds to arrive on
		# time on Endor
		AUTONOMY  # The autonomy of the Millennium Falcon
		ROUTES  # The list of all the routes possible
		COUNTDOWN  # The number of days before Endor exploses
		BOUNTY_HUNTERS_POSITIONS  # The list of all the positions of the Bounty Hunters
		DESTINATION = "Endor"  # The planet that has to reach the Millennium Falcon, which is always the planet Endor, before the end of the countdown

	def giveMeTheOdds(self, empireJsonFile):
		# Function that returns the odds of success of arriving on Endor on time before the end of the countdown
		# Another goal is to read and interpret the data given in the json file regarding the movment of Bounty
		# Hunters on planets

		# List of all the variables that can be modified through the program
		daysInCommonWithBountyHunters = 0  # The number of days the Millennium Falcon stays on a planet where Bounty Hunters are present
		currentPlanet = "Tatooine"  # The planet the Millennium Falcon is on; at first, it is always Tatooine
		numberOfDaysInTravel = 0  # The number of days the Millennium Falcon has already travelled, starting at 0 on the first day
		currentLevelOfFuel = AUTONOMY  # The level of fuel the Millennium Falcon is currently at; at first, the tank is full
		allPossibleOdds = []  # The list that will store all the different odds possible to reach Endor in time

		# We retrieve the list of all the possible odds of getting to Endor on time and store it in the variable
		# oddsList
		oddsList = self.variousPathsOptions(currentPlanet, currentLevelOfFuel, numberOfDaysInTravel,
											daysInCommonWithBountyHunters, allPossibleOdds)

		# If the list retrieved is empty, it means that there is no possible path to reach Endor in time before
		# the end of the countdown
		if not oddsList:
			println("There is no possible path to reach Endor in time before the end of the countdown")
			println("The odds of success are in consequence : 0")
			return 0.0
		# Otherwise, there is a least one possible path to reach Endor in time, we just need the one with the best
		# odds of success
		else:
			println("There is at least one path to reach Endor before the end of the countdown")
			println("The best odds of success are : ", max(oddsList))
			return max(oddsList)


	def oddsMathematicalFormula(self, daysInCommonWithBountyHunters):
		# Function that computes the odds of being caught by Bounty Hunters depending on the number of days they
		# spent in commonn on various planets during the flight
		k = daysInCommonWithBountyHunters				# To simpify the reading of the function, we will change the name daysInCommonWithBountyHunters to k

		# If the Millennium Falcon never landed on a planet on a day Bounty Hunters were present, then the probability
		# of being caught is null
		if k == 0:
			return 0.0
		# Otherwise, thanks to a mathematical formula, we can compute the odds of being caught for a specific path
		# with a recursive function
		else:
			return (9 ** (k - 1) / 10 ** k) + self.oddsMathematicalFormula(k - 1)


	def hasEnoughFuel(self, currentLevelOfFuel, travelTime):
		# Function that estimates if the Millennium Falcon has enough fuel for a specific hyperspeed jump
		# Boolean function that returns TRUE if the tank has enough fuel, FALSE otherwise

		return currentLevelOfFuel - travelTime >= 0



	def planetHasBountyHunters(self, currentPlanet, numberOfDaysInTravel):
		# Function that estimates if the Millennium is on a planet where Bounty Hunters are also present

		# With a loop FOR, we search into the list of Bounty Hunters postitions
		for dictionaryIndex in BOUNTY_HUNTERS_POSITIONS:

			# If Bounty Hunters and the Millennium Falcon are on the same planet on the same day, we return TRUE
			if BOUNTY_HUNTERS_POSITIONS[dictionaryIndex]["planet"] == currentPlanet \
					and BOUNTY_HUNTERS_POSITIONS[dictionaryIndex]["day"] == numberOfDaysInTravel:
				return True
		# Once, all the positions of the Bounty Hunters have been checked and the function did not return TRUE,
		# it means that there is no Bounty Hunters on the planet at the same time the Millennium Falcon is
		return False


	def nextPlanetHasBountyHunters(self, nextPlanet, numberOfDaysInTravel, dictionary):
		# Function that estimates if the Millennium will be on a planet where Bounty Hunters will also be present

		# With a loop FOR, we search into the list of Bounty Hunters postitions
		for dictionaryIndex in BOUNTY_HUNTERS_POSITIONS:

			# If Bounty Hunters and the Millennium Falcon will be on the same planet on the same day, we return TRUE
			if BOUNTY_HUNTERS_POSITIONS[dictionaryIndex]["planet"] == nextPlanet \
									and BOUNTY_HUNTERS_POSITIONS[dictionaryIndex]["day"] == (numberOfDaysInTravel + dictionary["travelTime"]):
				return True
		# Once, all the positions of the Bounty Hunters have been checked and the function did not return TRUE,
		# it means that there is no Bounty Hunters on the planet at the same time the Millennium Falcon will be
		return False


	def nextPlanet(self, currentPlanet, dictionary):
		# Function that returns the next planet of the path after finding a route which has its origin or
		# destination that is the current planet the Millennium Falcon is on

		# If the current planet corresponds to the origin key of the route, then the next planet is the destination key
		if dictionary["origin"] == currentPlanet:
			return dictionary["destination"]
		# Otherwise, it means the current planet corresponds to the destination key of the route, so the next planet
		# is the origin key
		else:
			return dictionary["origin"]

	def planetHasBountyHuntersOneDayAfterArrival(self, nextPlanet, numberOfDaysInTravel, dictionary):
		# Function that estimates if there will be Bounty Hunters one day after the Millennium arrives on a planet
		# with an almost or totally empty tank

		# With a loop FOR, we search into the list of Bounty Hunters
		for dictionaryIndex in BOUNTY_HUNTERS_POSITIONS:

			# If the Bounty Hunters are on a planet one day after the Millennium Falcon has arrived, then we return TRUE
			if BOUNTY_HUNTERS_POSITIONS[dictionaryIndex]["planet"] == nextPlanet \
					and BOUNTY_HUNTERS_POSITIONS[dictionaryIndex]["day"] == (numberOfDaysInTravel + dictionary["travelTime"] + 1):
				return True
		# Once, all the positions of the Bounty Hunters have been checked and the function did not return TRUE,
		# it means that there is no Bounty Hunters on the planet one day after the Millennium Falcon will arrive
		return False


	def enoughFuelAndTimeToTravel(self, currentPlanet, nextPlanet, currentLevelOfFuel, numberOfDaysInTravel,
								  daysInCommonWithBountyHunters, dictionary, allPossibleOdds):
		# Function that allows the Millennium Falcon to travel because it has time to reach the next planet and enough
		# fuel to make the hyperspeepd jump

		# If the next planet has Bounty Hunters on it, there are 2 options :
		# - 1: the Millennium Falcon stays one more day on its current planet (also refueling) and verify the next
		# 		day if there are still Bounty Hunters on the next planet
		# - 2: the Millennium Falcon decides to go to the planet anyway, risking to get caught
		if self.nextPlanetHasBountyHunters(nextPlanet, numberOfDaysInTravel, dictionary) \
				or self.planetHasBountyHuntersOneDayAfterArrival(nextPlanet, numberOfDaysInTravel, dictionary):
			# Option 1:
			numberOfDaysInTravel += 1

			if dictionary["travelTime"] + numberOfDaysInTravel <= COUNTDOWN
				currentLevelOfFuel = AUTONOMY

				if self.planetHasBountyHunters(currentPlanet, numberOfDaysInTravel):
					daysInCommonWithBountyHunters += 1

				# With a recursive way, we call again the function with the changed parameters in cas there are
				# again Bounty Hunters on the next planet
				self.enoughFuelAndTimeToTravel(currentPlanet, nextPlanet, currentLevelOfFuel, numberOfDaysInTravel,
											   daysInCommonWithBountyHunters, dictionary)
			# If the Millennium Falcon now does not have time to reach the next planet in time to reach Endor,
			# we need to rechanged the parameters as they were before so the loop can go on
			else:
				numberOfDaysInTravel -= 1

			# The option 2 does not need any particular modifications to be come true

		numberOfDaysInTravel += dictionary["travelTime"]
		currentLevelOfFuel -= dictionary["travelTime"]
		currentPlanet = nextPlanet
		allPossibleOdds = self.variousPathsOptions(currentPlanet, currentLevelOfFuel, numberOfDaysInTravel,
												   daysInCommonWithBountyHunters, allPossibleOdds)



	def variousPathsOptions(self, currentPlanet, currentLevelOfFuel, numberOfDaysInTravel,
							daysInCommonWithBountyHunters, allPossibleOdds):
		# Function that calculates the various paths options for the Millennium Falcon in order to arrive before
		# the end of the countdown on Endor
		# Of course, we begin thz travel on Tatooine with a tank full of fuel

		# If the planet the Millennium Falcon is on is currently occupied by Bounty Hunters, then we increment
		# by 1 daysInCommonWithBountyHunters
		if self.planetHasBountyHunters(currentPlanet, numberOfDaysInTravel):
			daysInCommonWithBountyHunters += 1

		# If the current planet the Millennium Falcon is on is the final destination of the trip Endor, then it
		# means that the Millennium Falcon has arrived on time
		# So we calculate the odds of success of this path, and return it
		if currentPlanet == DESTINATION:
			allPossibleOdds.append(1 - self.oddsMathematicalFormula(daysInCommonWithBountyHunters))
			return allPossibleOdds
		# Otherwise, we continue to search paths that lead to Endor in time
		else:
			# We search into the routes list to look for a path that can lead to Endor in time
			for dictionaryIndex in ROUTES:

				# If one of the routes has the current planet as its origin or destination and the Millennium
				# Falcon has enough time to travel to this next planet
				if (ROUTES[dictionaryIndex]["origin"] == currentPlanet or ROUTES[dictionaryIndex]["destination"] == currentPlanet) and ROUTES[dictionaryIndex]["travelTime"] + numberOfDaysInTravel <= COUNTDOWN :

					# We use the nextPlanet function to determine which is the next planet of the path
					nextPlanet = nextPlanet(currentPlanet, ROUTES[dictionaryIndex])

					# If the tank of the Millennium Falcon is full enough to make entirely the hyperspeed jump to
					# the next planet
					if self.hasEnoughFuel(currentLevelOfFuel, ROUTES[dictionaryIndex]["travelTime"]):

						# We call the function that allows the Millennium Falcon to travel beacuse it has enough
						# fuel and time
						self.enoughFuelAndTimeToTravel(currentPlanet, nextPlanet, numberOfDaysInTravel, daysInCommonWithBountyHunters, ROUTES[dictionaryIndex])

					# If the tank is not empty enough to make the hyperspeed jump, the Millennium Falcon stays one
					# day on its current planet to refuel
					else:
						numberOfDaysInTravel += 1
						currentLevelOfFuel = AUTONOMY
						allPossibleOdds = self.variousPathsOptions(currentPlanet, currentLevelOfFuel, numberOfDaysInTravel, daysInCommonWithBountyHunters, allPossibleOdds)

			# If the path can't lead to the destination Endor, we still need to return the list of possible odds
			return allPossibleOdds