package com.haanthony;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.haanthony.Game.GameColor;

public class AirforceGame extends ApplicationAdapter {
	private Stage stage;
	private HumanPlayer human;
	private GameManager manager;
	
	@Override
	public void create() {
		stage = new Stage(new ExtendViewport(1086, 1086));
		Gdx.input.setInputProcessor(stage);

		human = new HumanPlayer();
		stage.addActor(human.getRenderGroup());
		
		manager = new GameManager();
		manager.insertPlayer(GameColor.BLUE, human);
		manager.insertPlayer(GameColor.GREEN, new AIPlayer());
		manager.insertPlayer(GameColor.RED, new AIPlayer());
		manager.insertPlayer(GameColor.YELLOW, new AIPlayer());
		
		manager.newGame();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		human.update(Gdx.graphics.getDeltaTime());
		manager.update();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
}
