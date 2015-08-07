package com.haanthony;

import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Group;

public class HumanPlayer implements Player {
	private GameInfo latestGameInfo;
	private GameManager manager;
	
	private BoardRenderManager renderManager;
	private ChoicesRenderer choicesRenderer;
	
	public HumanPlayer() {
		renderManager = new BoardRenderManager();
	}
	
	@Override
	public void setGameManager(GameManager gameManager) {
		manager = gameManager;
		choicesRenderer = new ChoicesRenderer(renderManager.getRenderGroup(), manager);
	}
	
	public Group getRenderGroup() {
		return renderManager.getRenderGroup();
	}
	
	@Override
	public void updateGameInfo(GameInfo info) {
		
		Choice lastChoice = info.getLastChoice();
		if (latestGameInfo != null && latestGameInfo.getLastChoice() != lastChoice && lastChoice != null) {
			renderManager.processChoice(lastChoice);
		}
		
		latestGameInfo = info;
	}
	
	@Override
	public void updateChoices(Set<Choice> choices) {
		//choicesRenderer.renderChoices(choices);
		if (!choices.isEmpty()) {
			manager.playChoice(choices.iterator().next());
		}
	}
	
	public void update() {
		if (manager != null && renderManager.isDoneRendering()) {
			manager.readyPlayer(this);
		}
	}
}
