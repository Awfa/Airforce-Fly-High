package com.haanthony;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.haanthony.Game.GameColor;

public class GameInfo {
	private final GameColor color;
	private final Set<Choice> choices;
	private final int diceRoll;
	
	public GameInfo(GameColor color, Set<Choice> choices, int diceRoll) {
		this.color = color;
		this.choices = choices;
		this.diceRoll = diceRoll;
	}
	
	public GameColor getPlayerColor() {
		return color;
	}

	public Set<Choice> getChoices() {
		return choices;
	}

	public int getDiceRoll() {
		return diceRoll;
	}
	
	private static final char HEADER = '%';
	private static final char SEPERATER = '^';
	private static final char END_INDICATER = '&';
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(HEADER);
		sb.append(color.toString()).append(SEPERATER);
		
		for (Choice choice : choices) {
			sb.append(choice).append(SEPERATER);
		}
		
		sb.append(diceRoll).append(END_INDICATER);
		return sb.toString();
	}
	
	public static GameInfo fromString(String gameInfoString) {
		// Process header
		if (gameInfoString.charAt(0) != HEADER) {
			throw new IllegalArgumentException("Header not found");
		}
		
		GameInfoBuilder builder = new GameInfoBuilder();
		
		// Process color
		int index = 1;
		
		int nextSeperater = gameInfoString.indexOf(SEPERATER, index);
		builder.color = GameColor.valueOf(gameInfoString.substring(index, nextSeperater));
		index = nextSeperater + 1;
		
		// Process choices
		builder.choices = new HashSet<Choice>();
		
		nextSeperater = gameInfoString.indexOf(SEPERATER, index);
		while (nextSeperater != -1) {
			String choiceString = gameInfoString.substring(index, nextSeperater);
			Choice choice = Choice.fromString(choiceString);
			builder.choices.add(choice);
			index = nextSeperater + 1;
			nextSeperater = gameInfoString.indexOf(SEPERATER, index);
		}
		
		// Process dice roll and end indicater
		nextSeperater = gameInfoString.indexOf(END_INDICATER, index);
		if (nextSeperater == -1) {
			throw new IllegalArgumentException("No end indicater on this game info string");
		}
		builder.diceRoll = Integer.valueOf(gameInfoString.substring(index, nextSeperater));
		
		return builder.build();
	}
	
	private static class GameInfoBuilder {
		private GameColor color;
		private Set<Choice> choices;
		private int diceRoll;
		
		public GameInfo build() {
			return new GameInfo(color, choices, diceRoll);
		}
	}
}
