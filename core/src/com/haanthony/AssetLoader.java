package com.haanthony;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;

public class AssetLoader {
	private static AssetLoader assetLoader;
	
	private ImmutablePoint2[] fourPlayerBoardCoordinates;
	
	private AssetLoader() {
		loadFourPlayerBoardCoordinates("boardCoordinates.txt");
	}
	
	private void loadFourPlayerBoardCoordinates(String location) {
		Scanner boardCoordinatesScanner = new Scanner(Gdx.files.internal(location).reader());
		
		int numberOfCoordinates = boardCoordinatesScanner.nextInt();
		
		fourPlayerBoardCoordinates = new ImmutablePoint2[numberOfCoordinates];
		for (int i = 0; i < numberOfCoordinates; ++i) {
			fourPlayerBoardCoordinates[i] = new ImmutablePoint2(boardCoordinatesScanner.nextInt(), boardCoordinatesScanner.nextInt());
		}
		
		boardCoordinatesScanner.close();
	}
	
	public static AssetLoader getInstance() {
		if (assetLoader == null) {
			assetLoader = new AssetLoader();
		}
		
		return assetLoader;
	}
	
	public List<ImmutablePoint2> getFourPlayerBoardCoordinates() {
		return Collections.unmodifiableList(Arrays.asList(fourPlayerBoardCoordinates));
	}
}
