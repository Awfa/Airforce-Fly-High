package com.haanthony;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.haanthony.Game.GameColor;

public class HumanPlayer implements Player {
	private GameManager manager;
	
	private BoardRenderManager boardRenderManager;
	private ChoicesRenderer choicesRenderer;
	
	private boolean inPlay;
	private boolean isCurrentPlayer;
	
	public HumanPlayer() {
		boardRenderManager = new BoardRenderManager();
		
		inPlay = false;
		isCurrentPlayer = false;
	}
	
	public Group getRenderGroup() {
		return boardRenderManager.getRenderGroup();
	}
	
	@Override
	public void setGameManager(GameManager gameManager) {
		manager = gameManager;
		choicesRenderer = new ChoicesRenderer(boardRenderManager.getRenderGroup(), manager);
	}
	
	@Override
	public void updateGameInfo(final GameInfo info, boolean isCurrentPlayer) {
		inPlay = !info.getChoices().isEmpty();
		this.isCurrentPlayer = isCurrentPlayer;
		
		// Once information is received, start the dice roll
		boardRenderManager.spinTo(info.getPlayerColor());
		boardRenderManager.startDiceRoll(info.getDiceRoll(), isCurrentPlayer);
		
		if (isCurrentPlayer) {
			getRenderGroup().addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					boardRenderManager.endDiceRoll();
					manager.readyPlayer(HumanPlayer.this);
					
					if (!info.getChoices().isEmpty()) {
						choicesRenderer.renderChoices(info.getChoices());
					}
					
					getRenderGroup().removeListener(this);
				}
			});
		}
	}
	
	@Override
	public void revealDice() {
		if (!isCurrentPlayer) {
			boardRenderManager.endDiceRoll();
		}
	}

	@Override
	public void playChoice(Choice choice) {
		boardRenderManager.playChoice(choice);
		inPlay = false;
	}
	
	@Override
	public void endGame(EndGameInfo info) {
		System.out.println("Winners in order: ");
		for (GameColor color : info) {
			System.out.println(color);
		}
		
		inPlay = true;
		
		getRenderGroup().addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				manager.readyPlayer(HumanPlayer.this);
				getRenderGroup().removeListener(this);
			}
		});
	}

	@Override
	public void reset() {
		boardRenderManager.reset();
		inPlay = false;
		isCurrentPlayer = false;
	}
	
	public void update(float deltaTime) {
		boardRenderManager.update(deltaTime);
		if (manager != null && boardRenderManager.isDoneRendering() && !inPlay) {
			manager.readyPlayer(this);
		}
	}
}
