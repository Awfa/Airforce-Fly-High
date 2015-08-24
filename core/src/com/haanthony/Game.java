package com.haanthony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.haanthony.Choice.ChoiceType;

// The Game class is the class that upholds the rules of Air Force Fly High
// It manages the game logic such as how pieces move.
public class Game {
	
	// The GameColor enum is holds the colors of the board
	public enum GameColor {
		BLUE	(3, 57, 0, 52, 20 ,32, 66, 0),
		GREEN	(16, 63, 13, 58, 33, 45, 72, 1),
		RED		(29, 69, 26, 64, 46, 6, 54, 2),
		YELLOW	(42, 75, 39, 70, 7, 19, 60, 3);
		
		private final int spawn, home, exit, exitDest, slideStart, slideEnd, slideCross;
		
		// Offset is the first point on the board for the given color
		private final int offset;
		
		// Privately, the game colors also contain additional information
		// Specifically it contains points pertaining to the color, such as where red spawns, etc
		private GameColor(int spawn, int home, int exit, int exitDest, int slideStart, int slideEnd, int slideCross, int offset) {
			this.spawn = spawn;
			this.home = home;
			this.exit = exit;
			this.exitDest = exitDest;
			this.slideStart = slideStart;
			this.slideEnd = slideEnd;
			this.slideCross = slideCross;
			
			this.offset = offset;
		}
		
		// Returns the spawn point for this color - where airplanes should enter flight
		private int getSpawn() { return spawn; }

		// Returns the home point for this color - where the airplanes final destination or goal is
		private int getHome() { return home; }

		// Returns the exit point for this color - where the airplane turns to go home
		private int getExit() { return exit; }

		// Returns the exit destination point for this color - where the airplane ends up after turning to go home
		private int getExitDest() { return exitDest; }

		// Returns the slide starting point for this color - the point where the airplane can choose to slide
		private int getSlideStart() { return slideStart; }

		// Returns the slide ending point for this color - the point where the airplane can end up if they slide
		private int getSlideEnd() { return slideEnd; }

		// Returns the slide crossing point for this color - the point they cross to get to the slide ending point
		private int getSlideCross() { return slideCross; }
		
		// Returns whether or not the given point is within the home stretch for this color
		private boolean inHomeStretch(int point) {
			return point >= exitDest && point <= home;
		}
		
		// Returns the color of a point on the board
		private static GameColor getColorOfPoint(int point) {
			verifyPointInbounds(point);
			
			if (point < BOARD_MAIN_ROUTE_END) {
				int offset = point % GameColor.values().length;
				for (GameColor color : GameColor.values()) {
					if (color.offset == offset) {
						return color;
					}
				}
			} else {
				for (GameColor color : GameColor.values()) {
					if (point >= color.exitDest && point <= color.home) {
						return color;
					}
				}
			}
			
			// Should never reach here
			throw new AssertionError("Assertion Error: Unable to determine color of point " + point);
		}
		
		// Returns the next color of the given color
		private static GameColor nextColor(GameColor color) {
			// This only works because we defined the colors in this order
			return GameColor.values()[(color.ordinal() + 1) % GameColor.values().length];
		}
		
	}
	
	private static final int GAME_BOARD_SIZE = 76;
	private static final int BOARD_MAIN_ROUTE_END = 52;
	public static final int NUMBER_OF_AIRPLANES_PER_PLAYER = 4;
	
	private Map<GameColor, Hanger> hangers;
	private Board board;
	
	private Map<Choice, GameAction> actionsMap;
	
	private Random randomGenerator;
	private int lastDiceRoll;
	
	private GameColor playerTurn;
	
	public Game() {
		long seed = System.nanoTime();
		System.out.println("Random seed: " + seed);
		randomGenerator = new Random(seed);
		newGame();
	}
	
	// Starts a new game
	public void newGame() {
		hangers = new EnumMap<>(GameColor.class);
		
		for (GameColor color : GameColor.values()) {
			hangers.put(color, new Hanger(NUMBER_OF_AIRPLANES_PER_PLAYER, color));
		}
		
		board = new Board(GAME_BOARD_SIZE);
		actionsMap = new HashMap<>();
		playerTurn = GameColor.values()[randomGenerator.nextInt(GameColor.values().length)];
	}
	
	// For the current player, rolls the dice, and generates choices
	public GameInfo advanceAndGetState() {
		if (!isGameOver()) {
			GameColor currentPlayer = playerTurn;
			
			if (actionsMap.isEmpty()) {
				lastDiceRoll = randomGenerator.nextInt(6) + 1;
				
				populateActionsMap(playerTurn, lastDiceRoll);
				
				// If no choices were generated, skip the player's turn
				if (actionsMap.isEmpty()) {
					moveToNextPlayersTurn();
				}
			}
			
			return new GameInfo(currentPlayer, Collections.unmodifiableSet(actionsMap.keySet()), lastDiceRoll);
		} else {
			return null;
		}
	}
	
	// Plays the choice onto the game
	// Will throw an IllegalArgumentException if the given choice wasn't one of the available choices
	// Returns a ChoiceAftermath representing what happened after the choice was played
	public ChoiceAftermath playChoice(Choice choice) {		
		if (!actionsMap.containsKey(choice)) {
			throw new IllegalArgumentException("The given choice cannot be played since it wasn't one of the choices");
		}
		
		actionsMap.get(choice).execute();
		
		// Record the results of the choice that was played
		ChoiceAftermath aftermath = new ChoiceAftermath(isGameOver(), isPlayerDone(playerTurn) ? playerTurn : null);
		
		// Move the turn to the next player or if the roll was a 6, keep the same player
		if (lastDiceRoll != 6 || isPlayerDone(playerTurn)) {
			moveToNextPlayersTurn();
		}
		
		actionsMap.clear();
		
		return aftermath;
	}
	
	private boolean isPlayerDone(GameColor playerColor) {
		AirplaneFormation formationAtHome = board.getFormations(playerColor.getHome(), playerColor);
		
		return formationAtHome != null && formationAtHome.getSize() == NUMBER_OF_AIRPLANES_PER_PLAYER;
	}
	
	private boolean isGameOver() {
		for (GameColor player : GameColor.values()) {
			if (!isPlayerDone(player)) {
				return false;
			}
		}
		
		return true;
	}
	
	private void moveToNextPlayersTurn() {
		if (!isGameOver()) {
			do {
				playerTurn = GameColor.nextColor(playerTurn);
			} while (isPlayerDone(playerTurn));
		}
	}
	
	private void populateActionsMap(GameColor color, int diceRoll) {
		actionsMap = generateGameActions(generateChoices(color, diceRoll), color);
	}
	
	// This method generates the available choices for a color given the dice roll
	private final Set<Choice> generateChoices(GameColor color, int diceRoll) {
		System.out.println("-------Generating choices for color: " + color + " for a dice roll of: " + diceRoll + "-------");
		
		Set<Choice> choices = new HashSet<Choice>();
		
		AirplaneFormation formationAtHome = board.getFormations(color.getHome(), color);
		if (formationAtHome == null || formationAtHome.getSize() < NUMBER_OF_AIRPLANES_PER_PLAYER) {
			if (diceRoll % 2 == 0 && hangers.get(color).getPlanesInHanger() > 0) {
				choices.add(new Choice(ChoiceType.MOVE_PLANE_TO_RUNWAY, color));
				System.out.println("-Can move plane from hanger to runway");
				System.out.println();
			}
			
			// Generate choices for taking off planes from the runway
			if (hangers.get(color).getPlanesOnRunway() > 0) {
				// We subtract 1 from the dice roll because moving from the runway to inflight takes one move.
				// and formation size is one since when a plane takes off it's a formation of 1				
				int destination = raycastDestination(1, color, color.getSpawn(), diceRoll - 1);
				List<Integer> route = generateRoute(ChoiceType.LAUNCH_PLANE_FROM_RUNWAY, color, -1, destination);
				Set<Integer> takedowns = generateTakedowns(ChoiceType.LAUNCH_PLANE_FROM_RUNWAY, color, -1, destination, false);
				
				Choice liftoffChoice = new Choice(ChoiceType.LAUNCH_PLANE_FROM_RUNWAY, color, destination, takedowns, route);
				choices.add(liftoffChoice);
				System.out.println("-Can liftoff a plane from the runway to position: " + destination);
				choices.addAll(generateBonusChoices(1, color, destination, liftoffChoice));
				System.out.println();
			}
			
			// Generate choices for how far each plane can fly
			for (AirplaneFormation formation : board.getFormations(color)) {
				int currentPosition = board.getFormationsPosition(formation);
				
				if (currentPosition != color.getHome()) {
					int destination = raycastDestination(formation.getSize(), color, currentPosition, diceRoll);
					List<Integer> route = generateRoute(ChoiceType.FLY, color, currentPosition, destination);
					Set<Integer> takedowns = generateTakedowns(ChoiceType.FLY, color, currentPosition, destination, false);
					if (!route.isEmpty()) {
						Choice flyChoice = new Choice(ChoiceType.FLY, color, destination, takedowns, route, currentPosition);
						choices.add(flyChoice);
						System.out.println("-Can fly plane formation from " + currentPosition + " to " + destination);
						choices.addAll(generateBonusChoices(formation.getSize(), color, destination, flyChoice));
						System.out.println();
					}
				}
			}
		} else {
			System.out.println("All planes have reached home! The game is over for this color.");
		}
		
		System.out.println();
		return choices;
	}
	
	// This method recursively generates bonus choices for when a formation of the given size and color lands on this position after flying
	private final Set<Choice> generateBonusChoices(int sizeOfFormation, GameColor color, int positionLanded, Choice choice) {
		return generateBonusChoices(sizeOfFormation, color, positionLanded, choice, false, false);
	}
	
	// The recursive pair for the other generateBonusChoices function
	private final Set<Choice> generateBonusChoices(int sizeOfFormation, GameColor color, int positionLanded, Choice choice, boolean jumped, boolean slided) {
		Set<Choice> choices = new HashSet<Choice>();
		
		if (positionLanded < BOARD_MAIN_ROUTE_END) {
			GameColor colorOfPoint = GameColor.getColorOfPoint(positionLanded);
			if (color == colorOfPoint) { // Matching color! Can be a slide or jump square
				
				// Slide condition
				if (!slided && positionLanded == colorOfPoint.getSlideStart()) {
					
					// Sliding is possible if the slide start square is not blocked by a bigger formation
					// Sliding will even take down larger formations that are on the slide cross point
					boolean obstacleFound = getPossibleObstacleSize(color.getSlideStart(), color) > sizeOfFormation;
					
					if (!obstacleFound) {
						List<Integer> route = generateRoute(ChoiceType.SLIDE, color, color.getSlideStart(), color.getSlideEnd());
						Set<Integer> takedowns = generateTakedowns(ChoiceType.SLIDE, color, color.getSlideStart(), color.getSlideEnd(), true);
						Choice slideChoice = new Choice(ChoiceType.SLIDE, color, color.getSlideEnd(), takedowns, route, positionLanded, choice);
						choices.add(slideChoice);
						System.out.println("--From " + positionLanded + ", can slide to position: " + colorOfPoint.getSlideEnd());
						choices.addAll(generateBonusChoices(sizeOfFormation, color, color.getSlideEnd(), slideChoice, jumped, true));
					}
				}
				
				// Jump condition
				if (!jumped && positionLanded != colorOfPoint.getExit()) {					
					int destination = raycastDestination(sizeOfFormation, color, positionLanded, 4);
					List<Integer> route = generateRoute(ChoiceType.JUMP, color, positionLanded, destination);
					Set<Integer> takedowns = generateTakedowns(ChoiceType.JUMP, color, positionLanded, destination, true);
					if (destination != positionLanded) {
						Choice jumpChoice = new Choice(ChoiceType.JUMP, color, destination, takedowns, route, positionLanded, choice);
						choices.add(jumpChoice);
						System.out.println("--From " + positionLanded + ", can jump to position: " + destination);
						choices.addAll(generateBonusChoices(sizeOfFormation, color, destination, jumpChoice, true, slided));
					}
				}
			}
		}
		
		return choices;
	}
	
	private Map<Choice, GameAction> generateGameActions(Set<Choice> choices, GameColor color) {
		Map<Choice, GameAction> resultMap = new HashMap<>();
		for (Choice choice : choices) {
			resultMap.put(choice, generateGameAction(choice, color, null));
		}
		return resultMap;
	}
	
	private GameAction generateGameAction(Choice choice, GameColor color, MoveAction nextAction) {
		MoveAction action = null;

		switch(choice.getType()) {
		case MOVE_PLANE_TO_RUNWAY:
			return new MoveToRunwayAction(color);
		case LAUNCH_PLANE_FROM_RUNWAY:
			return new LaunchPlaneFromRunwayAction(color, choice.getDestination(), choice.getTakedowns(), nextAction);
		case FLY:
		case JUMP:
		case SLIDE:
			action = new MoveAction(board.getFormations(choice.getOrigin(), color), choice.getDestination(), choice.getTakedowns(), nextAction);
			break;
		default:
			throw new AssertionError("Not all cases covered!?");	
		}
		
		if (choice.getParentChoice() != null) {
			return generateGameAction(choice.getParentChoice(), color, action);
		}
		
		return action;
	}
	
	// Returns the set of takedown positions for the given choice description
	// The takedowns will be places on the board of the interval (origin, destination)
	// If the choice is a child choice, the interval will be [origin, destination)
	private Set<Integer> generateTakedowns(ChoiceType type, GameColor color, int origin, int destination, boolean isChildChoice) {
		Set<Integer> takedowns = new TreeSet<Integer>();
		
		// Moving the plane to the runway will have no takedowns
		if (type == ChoiceType.MOVE_PLANE_TO_RUNWAY) {
			return takedowns;
		}
		
		// If we're not first in the chain, then add our origin
		if (isChildChoice) {
			if (getPossibleObstacleSize(origin, color) > 0) {
				takedowns.add(origin);
			}
		}
		
		if (type == ChoiceType.SLIDE) {
			if (getPossibleObstacleSize(color.getSlideCross(), color) > 0) {
				takedowns.add(color.getSlideCross());
			}
		} else {
			if (type == ChoiceType.LAUNCH_PLANE_FROM_RUNWAY) {
				// If the plane moves from the spawning position after launching
				if (destination != color.getSpawn()) {
					origin = color.getSpawn();
				} else {
					return takedowns; // if the destination is the same as the spawn, nothing is taken down
				}
			}
			
			// We want to add everything in between of our origin and our destination
			BoardRay ray = new BoardRay(origin, color);
			
			ray.advance(); // Skip the origin
			while (ray.getPosition() != destination) {
				if (getPossibleObstacleSize(ray.getPosition(), color) > 0) {
					takedowns.add(ray.getPosition());
					System.err.println("loop1");
				}
				
				ray.advance();
			}
		}
		
		return takedowns;
	}
	
	// Returns the route for the given choice description
	// This is the interval on which the plane travels (origin, destination]
	private List<Integer> generateRoute(ChoiceType type, GameColor color, int origin, int destination) {
		List<Integer> route = new ArrayList<Integer>();
		
		if (type == ChoiceType.MOVE_PLANE_TO_RUNWAY) {
			return route;
		} else if (type == ChoiceType.SLIDE) {
			route.add(color.getSlideCross());
			route.add(color.getSlideEnd());
		} else if (type == ChoiceType.JUMP) {
			route.add(destination);
		} else {
			BoardRay ray;
			if (type == ChoiceType.LAUNCH_PLANE_FROM_RUNWAY) {
				ray = new BoardRay(color.getSpawn(), color);
			} else {
				ray = new BoardRay(origin, color);
				ray.advance(); // Skip the beginning
			}
			
			while (ray.getPosition() != destination) {
				route.add(ray.getPosition());
				ray.advance();
			}
			route.add(destination);
		}
		
		return route;
	}
	
	// This method returns the farthest destination from the start position to the given displacement for the given formation size and color
	private int raycastDestination(int sizeOfFormation, GameColor color, int startPosition, int displacement) {
		// find the closest obstacle by advancing until we find an obstacle
		BoardRay ray = new BoardRay(startPosition, color);;
		
		while (displacement > 0) {
			if (getPossibleObstacleSize(ray.getPosition(), color) > sizeOfFormation) {
				return ray.getPosition();
			}
			
			ray.advance();
			displacement--;
		}
		
		return ray.getPosition();
	}
	
	// Returns the size of the biggest plane of not the given color at the point
	private int getPossibleObstacleSize(int position, GameColor color) {
		int biggestSize = 0;
		
		for (AirplaneFormation formations : board.getFormationsExcludingColor(position, color)) {
			if (formations.getSize() > biggestSize) {
				biggestSize = formations.getSize();
			}
		}
		
		return biggestSize;
	}
	
	// Verify that the given point is inbounds of the board
	// If it's not, then an IllegalArgumentException is thrown
	private static void verifyPointInbounds(int point) {
		if (point < 0 || point >= GAME_BOARD_SIZE) {
			throw new IllegalArgumentException();
		}
	}
	
	private interface GameAction {
		public void execute();
	}
	
	private class MoveToRunwayAction implements GameAction {
		private Hanger hanger;
		
		public MoveToRunwayAction(GameColor color) {
			this.hanger = hangers.get(color);
		}
		
		@Override
		public void execute() {
			hanger.moveAirplaneToRunway();
		}
	}
	
	private class LaunchPlaneFromRunwayAction implements GameAction {
		private Hanger hanger;
		private int destination;
		private MoveAction nextAction;
		private Set<Integer> takedowns;
		
		public LaunchPlaneFromRunwayAction(GameColor color, int destination, Set<Integer> takedowns, MoveAction nextAction) {
			this.hanger = hangers.get(color);
			this.destination = destination;
			this.nextAction = nextAction;
			this.takedowns = takedowns;
		}
		
		@Override
		public void execute() {
			AirplaneFormation formation = hanger.launchPlaneFromRunway();
			int spawnPoint = formation.getColor().getSpawn();
			board.addFormation(formation, spawnPoint);
			if (spawnPoint != destination) {
				new MoveAction(formation, destination, takedowns, nextAction).execute();
			}
		}
	}
	
	private class MoveAction implements GameAction {
		private AirplaneFormation formation;
		private int destination;
		private MoveAction nextAction;
		private Set<Integer> takedowns;
		
		public MoveAction(AirplaneFormation formation, int destination, Set<Integer> takedowns, MoveAction nextAction) {
			this.formation = formation;
			this.destination = destination;
			this.nextAction = nextAction;
			this.takedowns = takedowns;
		}
		
		@Override
		public void execute() {
			// Take down planes in the way
			for (Integer position : takedowns) {
				for (AirplaneFormation planesToFall : board.getFormationsExcludingColor(position, formation.getColor())) {
					board.removeFormation(planesToFall);
					hangers.get(planesToFall.getColor()).landAirplanes(planesToFall);
				}
			}
			
			if (nextAction != null) {
				board.moveFormation(formation, destination);
				nextAction.execute(formation);
			} else {
				AirplaneFormation formationAtThatSpot = board.getFormations(destination, formation.getColor());
				if (formationAtThatSpot != null && formationAtThatSpot != formation) {
					board.removeFormation(formation);
					formation.combineWithOtherFormation(formationAtThatSpot);
				} else {
					board.moveFormation(formation, destination);
				}
			}
		}
		
		private void execute(AirplaneFormation formation) {
			this.formation = formation;
			execute();
		}
	}
	
	private static class BoardRay {
		private int position;
		private GameColor color;
		
		private int displacement;
		private boolean isInHomeStretch;
		
		public BoardRay(int position, GameColor color) {
			this.position = position;
			this.color = color;
			
			displacement = 1;
			isInHomeStretch = color.inHomeStretch(position);
		}
		
		public void advance() {
			if (position == color.getExit()) {
				position = color.getExitDest();
				isInHomeStretch = true;
			} else if (position == color.getExitDest() && displacement == -1) {
				position = color.getExit();
				displacement = 1;
			} else {
				position += displacement;
			}
			
			if (isInHomeStretch) {
				if (position == color.getHome()) {
					displacement = -1;
				}
			} else {
				position = position % BOARD_MAIN_ROUTE_END;
			}
		}
		
		public int getPosition() {
			return position;
		}
	}
}
