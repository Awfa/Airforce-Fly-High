package com.haanthony.screens;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.Socket;
import com.haanthony.Game.GameColor;
import com.haanthony.AIPlayer;
import com.haanthony.GameManager;
import com.haanthony.HumanPlayer;
import com.haanthony.RemotePlayer;

public class ServerScreen implements Screen {
	public static final int PORT = 16477;
	public static final int numberOfRemotes = 2;
	
	private ServerSocket server;
	private Socket socket;
	
	private Set<AIPlayer> aiPlayers;
	private Game game;
	
	public ServerScreen(Game game) {
		server = Gdx.net.newServerSocket(Protocol.TCP, PORT, null);
		aiPlayers = new HashSet<AIPlayer>();
		
		this.game = game;
	}
	
	@Override
	public void show() {
		GameManager manager = new GameManager();
		Set<RemotePlayer> remotes = new HashSet<RemotePlayer>();
		Set<GameColor> colorsLeft = EnumSet.allOf(GameColor.class);
		
		Iterator<GameColor> gameColorIter = colorsLeft.iterator();
		
		while (remotes.size() < numberOfRemotes) {
			socket = server.accept(null);
			RemotePlayer remotePlayer = new RemotePlayer(socket);
			manager.insertPlayer(gameColorIter.next(), remotePlayer);
			remotes.add(remotePlayer);
		}
		
		HumanPlayer player = new HumanPlayer();
		manager.insertPlayer(gameColorIter.next(), player);
		
		while (gameColorIter.hasNext()) {
			AIPlayer ai = new AIPlayer();
			aiPlayers.add(ai);
			manager.insertPlayer(gameColorIter.next(), ai);
		}
		
		game.setScreen(new LocalOnlineGameScreen(player, manager, aiPlayers, remotes));
	}

	@Override
	public void render(float delta) {
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
