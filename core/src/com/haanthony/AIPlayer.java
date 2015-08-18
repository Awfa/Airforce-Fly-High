package com.haanthony;

public class AIPlayer implements Player {
	public static final float DICE_DELAY = 0.5f;
	
	private GameManager manager;
	
	private float timer;
	private boolean rollTime;

	private Choice choiceToPlay;
	
	public AIPlayer() {
		timer = 0;
		rollTime = false;
	}

	@Override
	public void setGameManager(GameManager manager) {
		this.manager = manager;
		manager.readyPlayer(this);
	}

	@Override
	public void updateGameInfo(GameInfo info, boolean isCurrentPlayer) {
		rollTime = isCurrentPlayer;
		
		if (isCurrentPlayer && !info.getChoices().isEmpty()) {
			choiceToPlay = info.getChoices().iterator().next();
		}
	}

	@Override
	public void revealDice() {
		
	}

	@Override
	public void playChoice(Choice choice) {
		manager.readyPlayer(this);
	}

	public void update(float deltaTime) {
		if (rollTime) {
			timer += deltaTime;
			
			if (timer > DICE_DELAY) {
				rollTime = false;
				timer = 0;
				manager.readyPlayer(this);
				
				if (choiceToPlay != null) {
					manager.playChoice(choiceToPlay);
					choiceToPlay = null;
				}
			}
		} else {
			manager.readyPlayer(this);
		}
		
	}
}
