package com.haanthony;

import java.util.Set;

public class AIPlayer implements Player {
	private GameManager manager;
	
	public AIPlayer() {
		
	}
	
	@Override
	public void updateGameInfo(GameInfo info) {
		manager.readyPlayer(this);
	}

	@Override
	public void updateChoices(Set<Choice> choices) {
		if (!choices.isEmpty()) {
			manager.playChoice(choices.iterator().next());
			
		}
	}

	@Override
	public void setGameManager(GameManager manager) {
		this.manager = manager;
	}

}
