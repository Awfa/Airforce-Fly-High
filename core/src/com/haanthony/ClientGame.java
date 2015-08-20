package com.haanthony;

import com.badlogic.gdx.Game;
import com.haanthony.screens.ClientScreen;

public class ClientGame extends Game {
	@Override
	public void create() {
		setScreen(new ClientScreen(this));
	}
	
	@Override
	public void dispose() {
		
	}
}