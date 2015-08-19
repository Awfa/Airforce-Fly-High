package com.haanthony;

import com.haanthony.Game.GameColor;

public class ChoiceAftermath {
	private final boolean isGameOver;
	private final GameColor playerThatFinished;
	
	public ChoiceAftermath(boolean isGameOver, GameColor playerThatFinished) {
		this.isGameOver = isGameOver;
		this.playerThatFinished = playerThatFinished;
	}
	
	public boolean isGameOver() {
		return isGameOver;
	}
	
	public GameColor getPlayerThatFinished() {
		return playerThatFinished;
	}
}
