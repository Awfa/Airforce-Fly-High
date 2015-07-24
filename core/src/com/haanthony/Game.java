package com.haanthony;

// The GameBoard class is a the game board for Airforce Fly High
// It manages the game logic such as how pieces move, and where the pieces are
// Using this class, the client will be able to run the game
public class GameBoard {
	
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
			if (point < 0 || point >= GAME_BOARD_SIZE) {
				throw new IllegalArgumentException();
			}
			
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
	}
	
	public static final int GAME_BOARD_SIZE = 76;
	private static final int BOARD_MAIN_ROUTE_END = 52; 
	
	public GameBoard() {
		
	}
}
