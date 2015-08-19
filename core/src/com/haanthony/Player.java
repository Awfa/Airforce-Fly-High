package com.haanthony;

public interface Player {
	// Sets the manager for this player. This manager is where the player reports
	// their actions and receives game information from
	public void setGameManager(GameManager manager);
	
	// This will update the player with the current game information
	// The player will behave in one of two ways:
	// 1. If this player is the current player,
	// the player should call manager.readyPlayer(this) once they want
	// to reveal the dice. Then they should show the choices and pick one.
	// Once they pick a choice, they should call manager.playChoice(Choice)
	// Finally manager.readyPlayer(this) once again once they're ready
	// 2. If this player is not the current player,
	// once revealDice() has been called, they should reveal the dice,
	// If there is a choice to be played, wait for play choice to be called,
	// if not ready. Once the choice has been played, show it, then ready up.
	public void updateGameInfo(GameInfo info, boolean isCurrentPlayer);
	
	// This method will show the dice once the current player is ready
	// It will show the dice from the latest game info.
	public void revealDice();
	
	// This method will inform the player of the choice that was played
	public void playChoice(Choice choice);
	
	// This method will inform the player of the end game results
	public void endGame(EndGameInfo info);
	
	// This method will tell the player to reset their game
	public void reset();
}
