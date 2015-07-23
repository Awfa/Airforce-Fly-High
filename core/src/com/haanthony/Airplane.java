package com.haanthony;

import com.haanthony.GameBoard.GameColor;

// The Airplane class represents the airplanes the players control on the game board
// It has state and knows its position and color on the board
public class Airplane {
	public enum State {
		IN_HANGER ("ON_RUNWAY"),
		ON_RUNWAY ("IN_FLIGHT"),
		IN_FLIGHT ("IN_HANGER");
		
		// This variable is used to keep track of the state that comes after
		// the current one.
		// ON_RUNWAY is the state that IN_HANGER can transition to after a player rolls a 6.
		// IN_FLIGHT is the state that ON_RUNWAY can transition to when the player decides to move it.
		// IN_HANGER is the state that IN_FLIGHT will transition to when the airplane gets knocked out.
		private State nextState;
		
		private State(String nextState) {
			// Assigning next state by using the string representation
			// feels a little hacky. However, it's more concise than a
			// switch statement or a map which is why I use it here.
			// Forgive me!
			this.nextState = State.valueOf(nextState);
		}
	}
	
	private GameColor color;
	private State state;
	
	private int position;
	private int positionCap;
	
	// Constructs a new airplane with the given color and with the position cap
	// In limiting the position, the position cap is exclusive.
	public Airplane(GameColor color, int positionCap) {
		this.color = color;
		this.state = State.IN_HANGER;
		
		this.position = 0;
		this.positionCap = positionCap;
	}
	
	// Transitions the airplane to its next state
	public State gotoNewState() {
		state = state.nextState;
		return state;
	}
	
	// Returns the color of this airplane
	public GameColor getColor() { return color; }
	
	// Returns the state of the airplane
	public State getState() { return state; }
	
	// Returns where the airplane is on the baord
	// If the airplane is not in-flight (not on the board), then this throws an IllegalStateException
	public int getPosition() {
		// If the airplane is not in flight, it has no position in the air
		if (state != State.IN_FLIGHT) {
			throw new IllegalStateException("Airplane has no position while not in flight");
		}
		
		return position;
	}
	
	// This sets the new position of the airplane
	// If this is called with a newPosition < 0 or >= positionCap,
	// then this throws an IllegalArgumentException
	public void setPosition(int newPosition) {
		if (position < 0 || position >= positionCap) {
			throw new IllegalArgumentException();
		}
		
		position = newPosition;
	}
}
