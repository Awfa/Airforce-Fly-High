package com.haanthony.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.net.Socket;
import com.haanthony.HumanPlayer;
import com.haanthony.RemoteGameManager;

public class ClientScreen implements Screen {
	public static final String IP_TO_CONNECT_TO = "";
	
	private Socket socket;
	private Game game;
	
	public ClientScreen(Game game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		socket = Gdx.net.newClientSocket(Protocol.TCP, IP_TO_CONNECT_TO, ServerScreen.PORT, null);
		RemoteGameManager remoteGameManager = new RemoteGameManager(socket);
		HumanPlayer player = new HumanPlayer();
		remoteGameManager.setPlayer(player);
		game.setScreen(new RemoteGameScreen(player, remoteGameManager));
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
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
