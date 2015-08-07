package com.haanthony;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
	
	private Choice latestChoice;
	
	public Game() {
		randomGenerator = new Random();
		newGame();
	}
	
	public void newGame() {
		hangers = new EnumMap<>(GameColor.class);
		
		for (GameColor color : GameColor.values()) {
			hangers.put(color, new Hanger(NUMBER_OF_AIRPLANES_PER_PLAYER, color));
		}
		
		board = new Board(GAME_BOARD_SIZE);
		actionsMap = new HashMap<>();
		playerTurn = GameColor.values()[randomGenerator.nextInt(GameColor.values().length)];
	}
	
	// Generates the choices for the current player
	public Set<Choice> getTurnChoices() {
		if (actionsMap.isEmpty()) {
			lastDiceRoll = randomGenerator.nextInt(6) + 1;
			if (playerTurn == GameColor.BLUE) {
				lastDiceRoll = 1;
			}
			populateActionsMap(playerTurn, lastDiceRoll);
			
			// If no choices were generated, skip the player's turn
			if (actionsMap.isEmpty()) {
				moveToNextPlayersTurn();
			}
		}
		
		return Collections.unmodifiableSet(actionsMap.keySet());
	}
	
	// TODO: Check for win conditions
	public void playChoice(Choice choice) {
		if (actionsMap.isEmpty()) {
			throw new IllegalStateException("Cannot play a choice yet if no choices are generated");
		}
		
		if (!actionsMap.containsKey(choice)) {
			throw new IllegalArgumentException("The given choice cannot be played since it wasn't one of the choices");
		}
		
		actionsMap.get(choice).execute();
		
		// Move the turn to the next player or if the roll was a 6, keep the same player
		if (lastDiceRoll != 6) {
			moveToNextPlayersTurn();
		}
		
		actionsMap.clear();
		latestChoice = choice;
	}
	
	public GameColor getTurn() {
		return playerTurn;
	}
	
	public int getDiceRoll() {
		return lastDiceRoll;
	}
	
	public GameInfo getGameInfo() {
		return new GameInfo(board, hangers, latestChoice, lastDiceRoll);
	}
	
	private void moveToNextPlayersTurn() {
		playerTurn = GameColor.nextColor(playerTurn);
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
			if (diceRoll == 6 && hangers.get(color).getPlanesInHanger() > 0) {
				choices.add(new Choice(ChoiceType.MOVE_PLANE_TO_RUNWAY, color));
				System.out.println("-Can move plane from hanger to runway");
				System.out.println();
			}
			
			// Generate choices for taking off planes from the runway
			if (hangers.get(color).getPlanesOnRunway() > 0) {
				// We subtract 1 from the dice roll because moving from the runway to inflight takes one move.
				// and formation size is one since when a plane takes off it's a formation of 1
				int destination = raycastDestination(1, color, color.getSpawn(), diceRoll - 1);
				
				Choice liftoffChoice = new Choice(ChoiceType.LAUNCH_PLANE_FROM_RUNWAY, color, destination);
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
					
					if (destination != currentPosition) {
						Choice flyChoice = new Choice(ChoiceType.FLY, color, destination, currentPosition);
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
				if (!slided && positionLanded == colorOfPoint.getSlideStart()) {
					int obstacleSize = 0;
					if (!board.getFormations(color.getSlideCross()).isEmpty()) {
						// On the home stretch, between exit dest and home, will only have one airplane formation
						// since only plane formations of their color can be on it
						obstacleSize = board.getFormations(color.getSlideCross()).iterator().next().getSize();
					}
					
					if (obstacleSize <= sizeOfFormation) {
						Choice slideChoice = new Choice(ChoiceType.SLIDE, color, color.getSlideEnd(), positionLanded, choice);
						choices.add(slideChoice);
						System.out.println("--From " + positionLanded + ", can slide to position: " + colorOfPoint.getSlideEnd());
						choices.addAll(generateBonusChoices(sizeOfFormation, color, color.getSlideEnd(), slideChoice, jumped, true));
					}
				}
				
				if (!jumped && positionLanded != colorOfPoint.getExit()) {
					int destination = raycastDestination(sizeOfFormation, color,
							positionLanded, 4);
					if (destination != positionLanded) {
						Choice jumpChoice = new Choice(ChoiceType.JUMP, color, destination, positionLanded, choice);
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
			return new LaunchPlaneFromRunwayAction(color, choice.getDestination(), nextAction);
		case FLY:
		case JUMP:
		case SLIDE:
			action = new MoveAction(board.getFormations(choice.getOrigin(), color), choice.getDestination(), nextAction);
			break;
		default:
			throw new AssertionError("Not all cases covered!?");	
		}
		
		if (choice.getParentChoice() != null) {
			return generateGameAction(choice.getParentChoice(), color, action);
		}
		
		return action;
	}
	
	// This method returns the farthest destination from the start position to the given displacement for the given formation size and color
	private int raycastDestination(int sizeOfFormation, GameColor color, int startPosition, int displacement) {
		// find the closest obstacle by advancing until we find an obstacle
		int ray = startPosition;
		int rayDisplacement = 1;
		
		boolean obstacleFound = false;
		boolean inHomeStretch = color.inHomeStretch(startPosition);
		
		while (displacement > 0 && !obstacleFound) {
			for (AirplaneFormation possibleObstacle : board.getFormationsExcludingColor(ray, color)) {
				// We check the size against one because freshly lifted off formations start at size 1
				if (possibleObstacle.getSize() > sizeOfFormation) {
					obstacleFound = true;
				}
			}
			
			if (!obstacleFound) {
				ray += rayDisplacement;
				if (!inHomeStretch) {
					ray = ray % BOARD_MAIN_ROUTE_END;
					// modulo here not needed but just in case the exit point is ever at the boundary
					if (ray == (color.getExit() + 1) % BOARD_MAIN_ROUTE_END) {
						ray = color.getExitDest();
						inHomeStretch = true;
					}
				} else {
					if (ray > color.getHome()) {
						ray = color.getHome() - 1;
						rayDisplacement = -1;
					}
					
					if (ray < color.getExitDest()) {
						throw new AssertionError("Traced ray went backwards out of the exit!?");
					}
				}
			}
			
			displacement--;
		}
		
		return ray;
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
		
		public LaunchPlaneFromRunwayAction(GameColor color, int destination, MoveAction nextAction) {
			this.hanger = hangers.get(color);
			this.destination = destination;
			this.nextAction = nextAction;
		}
		
		@Override
		public void execute() {
			AirplaneFormation formation = hanger.launchPlaneFromRunway();
			int spawnPoint = formation.getColor().getSpawn();
			board.addFormation(formation, spawnPoint);
			if (spawnPoint != destination) {
				new MoveAction(formation, destination, nextAction).execute();
			}
		}
	}
	
	private class MoveAction implements GameAction {
		private AirplaneFormation formation;
		private int destination;
		private MoveAction nextAction;
		
		
		public MoveAction(AirplaneFormation formation, int destination, MoveAction nextAction) {
			this.formation = formation;
			this.destination = destination;
			this.nextAction = nextAction;
		}
		
		@Override
		public void execute() {
			if (nextAction != null) {
				board.moveFormation(formation, destination);
				nextAction.execute(formation);
			} else {
				AirplaneFormation formationAtThatSpot = board.getFormations(destination, formation.getColor());
				if (formationAtThatSpot != null) {
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
	
	public static void main(String[] args) {
		Game game = new Game();
		
		Map<Choice, GameAction> map = game.doBlueRoll(6);
		
		printHangerInfo(GameColor.BLUE, game);
		
		System.out.println("Moving plane from blue hanger to blue runway...");
		map.values().iterator().next().execute();
		printHangerInfo(GameColor.BLUE, game);		
		
		map = game.doBlueRoll(6);
		for (Choice choice : map.keySet()) {
			if (choice.getType() == ChoiceType.JUMP) {
				System.out.println(choice);
				map.get(choice).execute();
			}
		}
		
		map = game.doBlueRoll(4);
	}
	
	private static void printHangerInfo(GameColor color, Game game) {
		System.out.println("Planes in " + color + " hanger: " + game.hangers.get(color).getPlanesInHanger());
		System.out.println("Planes in " + color + " runway: " + game.hangers.get(color).getPlanesOnRunway());
		System.out.println();
	}
	
	private Map<Choice, GameAction> doBlueRoll(int diceRoll) {
		return generateGameActions(generateChoices(GameColor.BLUE, diceRoll), GameColor.BLUE);
	}
}
