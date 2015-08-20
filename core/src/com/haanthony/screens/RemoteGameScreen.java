package com.haanthony.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.haanthony.HumanPlayer;
import com.haanthony.RemoteGameManager;

public class RemoteGameScreen implements Screen {
	private Stage stage;
	private HumanPlayer player;
	private RemoteGameManager manager;
	
	public RemoteGameScreen(HumanPlayer player, RemoteGameManager manager) {
		stage = new Stage(new ExtendViewport(1086, 1086));
		Gdx.input.setInputProcessor(stage);
		
		this.player = player;
		this.manager = manager;
		
		stage.addActor(player.getRenderGroup());
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
		manager.close();
	}

}
