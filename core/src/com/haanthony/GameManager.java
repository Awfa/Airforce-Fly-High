package com.haanthony;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.haanthony.Game.GameColor;

public class GameManager {
	private enum GameManagerState {
		WAIT_FOR_PLAYER_READY {
			@Override
			public void update(GameManager manager) {
				if (manager.allPlayersReady()) {
					GameInfo gameInformation = manager.game.advanceAndGetState();
					
					// We can only wait for a choice to be played if there is a choice
					manager.isChoicePlayed = gameInformation.getChoices().isEmpty();
					
					// Unready the current player for the next state
					Player currentPlayer = manager.players.get(gameInformation.getPlayerColor());
					manager.readyPlayers.remove(currentPlayer);
					
					// Send the game information to everyone
					for (Player player : manager.players.values()) {
						player.updateGameInfo(gameInformation, currentPlayer == player);
					}
					
					manager.state = GameManagerState.WAIT_TO_REVEAL_DICE;
				}
			}
		},
		WAIT_TO_REVEAL_DICE {
			@Override
			public void update(GameManager manager) {
				if (manager.allPlayersReady()) {
					for (Player player : manager.players.values()) {
						player.revealDice();
					}
					
					manager.state = GameManagerState.WAIT_FOR_CHOICE_TO_PLAY;
				}
			}
		},
		WAIT_FOR_CHOICE_TO_PLAY {
			@Override
			public void update(GameManager manager) {
				if (manager.isChoicePlayed) {
					if (manager.choicePlayed != null) {
						for (Player player : manager.players.values()) {
							player.playChoice(manager.choicePlayed);
						}
					}
					
					manager.isChoicePlayed = false;
					manager.choicePlayed = null;
					manager.unreadyAll();
					manager.state = GameManagerState.WAIT_FOR_PLAYER_READY;
				}
			}
		};
		
		public abstract void update(GameManager manager);
	}
	
	private Map<GameColor, Player> players;
	private Set<Player> readyPlayers;

	private Game game;
	
	private GameManagerState state = GameManagerState.WAIT_FOR_PLAYER_READY;
	
	private boolean isChoicePlayed;
	private Choice choicePlayed;
	
	public GameManager() {
		players = new EnumMap<GameColor, Player>(GameColor.class);
		game = new Game();
		
		readyPlayers = new HashSet<Player>();
	}
	
	public void insertPlayer(GameColor color, Player player) {		
		players.put(color, player);
		player.setGameManager(this);
	}
	
	public void newGame() {
		game.newGame();
	}
	
	public void playChoice(Choice choice) {
		game.playChoice(choice);
		choicePlayed = choice;
		isChoicePlayed = true;
	}
	
	public void readyPlayer(Player player) {
		if (!players.values().contains(player)) {
			throw new IllegalArgumentException();
		}
		readyPlayers.add(player);
	}
	
	public void update() {
		state.update(this);
	}
	
	private boolean allPlayersReady() {
		return readyPlayers.size() == players.values().size();
	}
	
	private void unreadyAll() {
		readyPlayers.clear();
	}
}
