package com.haanthony;

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
}
