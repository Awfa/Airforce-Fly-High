package com.haanthony;

public class AIPlayer implements Player {
	public static final float DICE_DELAY = 0.5f;
	
	private Manager manager;
	
	private float timer;
	private boolean rollTime;

	private Choice choiceToPlay;
	private boolean unready;
	
	public AIPlayer() {
		timer = 0;
		rollTime = false;
	}

	@Override
	public void setGameManager(Manager manager) {
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
	
	@Override
	public void endGame(EndGameInfo info) {
		manager.readyPlayer(this);
	}

	@Override
	public void reset() {
		manager.readyPlayer(this);
	}

	public void update(float deltaTime) {
		if (unready) {
			if (rollTime) {
				timer += deltaTime;
				
				if (timer > DICE_DELAY) {
					rollTime = false;
					timer = 0;
					manager.readyPlayer(this);
					unready = false;
					if (choiceToPlay != null) {
						manager.playChoice(choiceToPlay);
						choiceToPlay = null;
					}
				}
			} else {
				manager.readyPlayer(this);
				unready = false;
			}
		}
	}

	@Override
	public void unready() {
		unready = true;
	}
}
