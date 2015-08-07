package com.haanthony;

import java.util.Set;

public interface Player {
	public void updateGameInfo(GameInfo info);
	public void updateChoices(Set<Choice> choices);
	public void setGameManager(GameManager manager);
}
