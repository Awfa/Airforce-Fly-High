package com.haanthony;

import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Group;

public class HumanPlayer implements Player {
	private GameInfo latestGameInfo;
	private GameManager manager;
	
	private BoardRenderManager boardRenderManager;
	private ChoicesRenderer choicesRenderer;
	private TurnIndicatorRenderer turnIndicatorRenderer;
	
	public HumanPlayer() {
		boardRenderManager = new BoardRenderManager();
		turnIndicatorRenderer = new TurnIndicatorRenderer(1086/2,1086/2);
	}
	
	@Override
	public void setGameManager(GameManager gameManager) {
		manager = gameManager;
		choicesRenderer = new ChoicesRenderer(boardRenderManager.getRenderGroup(), manager);
		turnIndicatorRenderer.spinTo(manager.getCurrentPlayerColor());
		boardRenderManager.getRenderGroup().addActor(turnIndicatorRenderer.getRenderGroup());
		turnIndicatorRenderer.getRenderGroup().toBack();
	}
	
	public Group getRenderGroup() {
		return boardRenderManager.getRenderGroup();
	}
	
	@Override
	public void updateGameInfo(GameInfo info) {
		Choice lastChoice = info.getLastChoice();
		
		if (latestGameInfo != null && latestGameInfo.getLastChoice() != lastChoice && lastChoice != null) {
			boardRenderManager.processChoice(info);
		} else {
			boardRenderManager.showDiceRoll(info.getLastDiceRoll());
		}
		
		latestGameInfo = info;
		turnIndicatorRenderer.spinTo(manager.getCurrentPlayerColor());
	}
	
	@Override
	public void updateChoices(Set<Choice> choices) {
		choicesRenderer.renderChoices(choices);
		for (Choice choice : choices) {
			//manager.playChoice(choice);
			break;
		}
	}
	
	public void update(float deltaTime) {
		boardRenderManager.update(deltaTime);
		if (manager != null && boardRenderManager.isDoneRendering()) {
			manager.readyPlayer(this);
		}
	}
}
