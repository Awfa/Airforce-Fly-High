package com.haanthony.screens;

import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.haanthony.AIPlayer;
import com.haanthony.GameManager;
import com.haanthony.HumanPlayer;
import com.haanthony.RemotePlayer;

public class LocalOnlineGameScreen implements Screen {
	private Stage stage;
	private HumanPlayer player;
	private GameManager manager;
	private Set<AIPlayer> aiPlayers;
	private Set<RemotePlayer> remotePlayers;
	
	public LocalOnlineGameScreen(HumanPlayer player, GameManager manager, Set<AIPlayer> aiPlayers, Set<RemotePlayer> remotePlayers) {
		stage = new Stage(new ExtendViewport(1086, 1086));
		Gdx.input.setInputProcessor(stage);
		
		this.player = player;
		this.manager = manager;
		
		stage.addActor(player.getRenderGroup());
		manager.newGame();
		
		this.aiPlayers = aiPlayers;
		this.remotePlayers = remotePlayers;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		player.update(delta);
		for (AIPlayer ai : aiPlayers) {
			ai.update(delta);
		}
		for (RemotePlayer remote : remotePlayers) {
			remote.update();
		}
		manager.update();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
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
		stage.dispose();
	}

}
