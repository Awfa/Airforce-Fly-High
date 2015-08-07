package com.haanthony;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.haanthony.Game.GameColor;

public class GameManager {
	private Map<GameColor, Player> players;
	private Set<Player> readyPlayers;
	private Player currentPlayer;
	private Game game;
	
	private boolean choicePlayed;
	
	public GameManager() {
		players = new EnumMap<GameColor, Player>(GameColor.class);
		game = new Game();
		
		readyPlayers = new HashSet<Player>();
		choicePlayed = false;
	}
	
	public void insertPlayer(GameColor color, Player player) {		
		players.put(color, player);
		player.setGameManager(this);
		
		readyPlayers.add(player);
	}
	
	public void newGame() {
		game.newGame();
		choicePlayed = true;
	}
	
	public void playChoice(Choice choice) {
		game.playChoice(choice);
		choicePlayed = true;
	}
	
	public Player getPlayer(GameColor color) {
		return players.get(color);
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void readyPlayer(Player player) {
		if (!players.values().contains(player)) {
			throw new IllegalArgumentException();
		}
		readyPlayers.add(player);
	}
	
	public void update() {
		if (choicePlayed && readyPlayers.size() == players.values().size()) {
			readyPlayers.clear();
			currentPlayer = players.get(game.getTurn());
			
			for (Player player : players.values()) {
				player.updateGameInfo(game.getGameInfo());
			}
			
			Set<Choice> choices = game.getTurnChoices();
			currentPlayer.updateChoices(choices);
			if (choices.isEmpty()) {
				choicePlayed = true;
			} else {
				choicePlayed = false;
			}
		}
		
	}
	
}
