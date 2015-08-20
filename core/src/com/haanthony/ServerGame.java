package com.haanthony;

import com.badlogic.gdx.Game;
import com.haanthony.screens.ServerScreen;

public class ServerGame extends Game {
	@Override
	public void create() {
		setScreen(new ServerScreen(this));
	}
	
	@Override
	public void dispose() {
		
	}
}