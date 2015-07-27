package com.haanthony;

import java.io.Reader;
import java.util.List;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.GridPoint2;

public class HumanPlayer {
	private List<ImmutablePoint2> fourPlayerBoardCoordinates;
	
	public HumanPlayer() {
		fourPlayerBoardCoordinates = AssetLoader.getInstance().getFourPlayerBoardCoordinates();
	}
}
