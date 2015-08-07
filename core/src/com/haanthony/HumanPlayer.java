package com.haanthony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Group;

public class HumanPlayer implements Player {
	private List<Choice> choices;
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
	public void updateChoices(Set<Choice> newChoices) {
		choicesRenderer.renderChoices(newChoices);
	}
	
	public void update() {
		if (manager != null && renderManager.isDoneRendering()) {
			manager.readyPlayer(this);
		}
	}
}
