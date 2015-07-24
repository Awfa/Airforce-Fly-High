package com.haanthony;

import java.util.EnumMap;
import java.util.Map;

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
		
		private boolean inHomeStretch(int point) {
			return point >= exitDest && point <= home;
		}
		
/*		private static int getJumpPoint(int point) {
			verifyPointInbounds(point);
			if (point < BOARD_MAIN_ROUTE_END) {
				point = (point + 4) % BOARD_MAIN_ROUTE_END;
			}
			
			return point;
		}*/
	}
	
	private static final int GAME_BOARD_SIZE = 76;
	private static final int BOARD_MAIN_ROUTE_END = 52;
	private static final int NUMBER_OF_AIRPLANES_PER_PLAYER = 4;
	
	private Map<GameColor, Hanger> hangers;
	private Board board;
	
	public Game() {
		hangers = new EnumMap<>(GameColor.class);
		
		for (GameColor color : GameColor.values()) {
			hangers.put(color, new Hanger(NUMBER_OF_AIRPLANES_PER_PLAYER, color));
		}
		
		board = new Board(GAME_BOARD_SIZE);
	}
	
	// This method generates the available choices for a color given the dice roll
	private final void generateChoices(GameColor color, int diceRoll) {
		System.out.println("-------Generating choices for color: " + color + " for a dice roll of: " + diceRoll + "-------");
		
		AirplaneFormation formationAtHome = board.getFormations(color.getHome(), color);
		if (formationAtHome == null || formationAtHome.getSize() < NUMBER_OF_AIRPLANES_PER_PLAYER) {
			if (diceRoll == 6 && hangers.get(color).getPlanesInHanger() > 0) {
				System.out.println("-Can move plane from hanger to runway");
				System.out.println();
			}
			
			// Generate choices for taking off planes from the runway
			if (hangers.get(color).getPlanesOnRunway() > 0) {
				// We subtract 1 from the dice roll because moving from the runway to inflight takes one move.
				// and formation size is one since when a plane takes off it's a formation of 1
				int destination = raycastDestination(1, color, color.getSpawn(), diceRoll - 1);
				
				System.out.println("-Can liftoff a plane from the runway to position: " + destination);
				generateBonusChoices(1, color, destination);
				System.out.println();
			}
			
			// Generate choices for how far each plane can fly
			for (AirplaneFormation formation : board.getFormations(color)) {
				int currentPosition = board.getFormationsPosition(formation);
				
				if (currentPosition != color.getHome()) {
					int destination = raycastDestination(formation.getSize(), color, currentPosition, diceRoll);
					
					if (destination != currentPosition) {
						System.out.println("-Can fly plane formation from " + currentPosition + " to " + destination);
						generateBonusChoices(formation.getSize(), color, destination);
						System.out.println();
					}
				}
			}
		} else {
			System.out.println("All planes have reached home! The game is over for this color.");
		}
		
		System.out.println();
	}
	
	private final void generateBonusChoices(int sizeOfFormation, GameColor color, int positionLanded) {
		generateBonusChoices(sizeOfFormation, color, positionLanded, false, false);
	}
	
	private final void generateBonusChoices(int sizeOfFormation, GameColor color, int positionLanded, boolean jumped, boolean slided) {
		if (positionLanded < BOARD_MAIN_ROUTE_END) {
			GameColor colorOfPoint = GameColor.getColorOfPoint(positionLanded);
			if (color == colorOfPoint) { // Matching color! Can be a slide or jump square
				if (!slided && positionLanded == colorOfPoint.getSlideStart()) {
					int obstacleSize = 0;
					if (!board.getFormations(colorOfPoint.getSlideCross()).isEmpty()) {
						// On the home stretch, between exit dest and home, will only have one airplane formation
						// since only plane formations of their color can be on it
						obstacleSize = board.getFormations(colorOfPoint.getSlideCross()).iterator().next().getSize();
					}
					
					if (obstacleSize <= sizeOfFormation) {
						System.out.println("--From " + positionLanded + ", can slide to position: " + colorOfPoint.getSlideEnd());
						generateBonusChoices(sizeOfFormation, color, colorOfPoint.getSlideEnd(), jumped, true);
					}
				}
				
				if (!jumped && positionLanded != colorOfPoint.getExit()) {
					int destination = raycastDestination(sizeOfFormation, color,
							positionLanded, 4);
					if (destination != positionLanded) {
						System.out.println("--From " + positionLanded + ", can jump to position: " + destination);
						generateBonusChoices(sizeOfFormation, color, destination, true, slided);
					}
				}
			}
		}
	}
	
	private int raycastDestination(int sizeOfFormation, GameColor color, int startPosition, int displacement) {
		// find the closest obstacle by advancing until we find an obstacle
		int ray = startPosition;
		int rayDisplacement = 1;
		
		boolean obstacleFound = false;
		boolean inHomeStretch = color.inHomeStretch(startPosition);
		
		while (displacement > 0 && !obstacleFound) {
			for (AirplaneFormation possibleObstacle : board.getFormationsExcludingColor(ray, color)) {
				// We check the size against one because freshly lifted off formations start at size 1
				if (possibleObstacle.getSize() > 1) {
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
	
	public static void main(String[] args) {
		Game game = new Game();
		
		game.generateChoices(GameColor.BLUE, 6);
		
		printHangerInfo(GameColor.BLUE, game);
		
		System.out.println("Moving plane from blue hanger to blue runway...");
		game.hangers.get(GameColor.BLUE).moveAirplaneToRunway();
		
		printHangerInfo(GameColor.BLUE, game);
		
		game.generateChoices(GameColor.BLUE, 18);
		game.generateChoices(GameColor.BLUE, 14);
		
		System.out.println("Putting yellow 2 stacked formation in the way at position 3...");
		AirplaneFormation yellowFormation = new AirplaneFormation(GameColor.YELLOW);
		yellowFormation.addPlane(new Airplane());
		yellowFormation.addPlane(new Airplane());
		game.board.addFormation(yellowFormation, 3);
		
		game.generateChoices(GameColor.BLUE, 6);
		
		System.out.println("Putting the yellow 2 stacked formation to position 4...");
		game.board.moveFormation(yellowFormation, 4);
		
		game.generateChoices(GameColor.BLUE, 2);
		
		game.generateChoices(GameColor.YELLOW, 3);
		
		System.out.println("Putting green 3 stacked formation in the way of the yellow slide at position 60...");
		AirplaneFormation greenFormation = new AirplaneFormation(GameColor.GREEN);
		greenFormation.addPlane(new Airplane());
		greenFormation.addPlane(new Airplane());
		greenFormation.addPlane(new Airplane());
		game.board.addFormation(greenFormation, 60);
		
		game.generateChoices(GameColor.GREEN, 3);
		game.generateChoices(GameColor.YELLOW, 3);
		
		System.out.println("Removing green 3 stacked formation in the way of the yellow slide at position 60...");
		if (greenFormation == game.board.getFormations(60, GameColor.GREEN)) {
			System.out.println("Board get formation works.");
		}
		game.board.removeFormation(game.board.getFormations(60, GameColor.GREEN));
		
		game.generateChoices(GameColor.YELLOW, 3);
		
		System.out.println("Removing the yellow 2 stacked formation from position 4...");
		game.board.removeFormation(yellowFormation);
		
		game.generateChoices(GameColor.YELLOW, 6);
		game.generateChoices(GameColor.BLUE, 6);
		
		System.out.println("Putting a blue plane formation of size 1 at position 47 (exit is in 5 spaces)");
		AirplaneFormation blueFormation = new AirplaneFormation(GameColor.BLUE);
		blueFormation.addPlane(new Airplane());
		game.board.addFormation(blueFormation, 47);
		
		game.generateChoices(GameColor.BLUE, 6);
		
		System.out.println("Putting red 2 stacked formation at red home...");
		AirplaneFormation redFormation = new AirplaneFormation(GameColor.RED);
		redFormation.addPlane(new Airplane());
		redFormation.addPlane(new Airplane());
		game.board.addFormation(redFormation, GameColor.RED.getHome());
		
		game.generateChoices(GameColor.RED, 6);
		
		System.out.println("Adding 2 more planes to red home...");
		redFormation.addPlane(new Airplane());
		redFormation.addPlane(new Airplane());
		
		game.generateChoices(GameColor.RED, 6);		
	}
	
	private static void printHangerInfo(GameColor color, Game game) {
		System.out.println("Planes in " + color + " hanger: " + game.hangers.get(color).getPlanesInHanger());
		System.out.println("Planes in " + color + " runway: " + game.hangers.get(color).getPlanesOnRunway());
		System.out.println();
	}
}
