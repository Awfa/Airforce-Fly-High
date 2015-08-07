package com.haanthony;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.haanthony.Game.GameColor;

public class AssetLoader {
	private static AssetLoader assetLoader;
	
	private Texture fourPlayerBoard;
	private List<ImmutablePoint2> fourPlayerBoardCoordinates;
	private Map<GameColor, List<ImmutablePoint2>> spawnPointCoordinates;
	private Map<GameColor, Texture> airplaneSprites;
	
	private Texture choiceDot;
	
	public static AssetLoader getInstance() {
		if (assetLoader == null) {
			assetLoader = new AssetLoader();
		}
		
		return assetLoader;
	}
	
	private AssetLoader() {
		loadFourPlayerBoard("airforceflyhigh.png");
		loadFourPlayerBoardCoordinates("boardCoordinates.txt");
		loadSpawnPointCoordinates("spawnPoints.txt");
		loadAirplaneSprites("blueplane.png", "greenplane.png", "redplane.png", "yellowplane.png");
		loadChoiceDot("choiceDot.png");
	}

	private void loadFourPlayerBoard(String location) {
		fourPlayerBoard = new Texture(location);
		fourPlayerBoard.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
	}

	private void loadFourPlayerBoardCoordinates(String location) {
		Scanner boardCoordinatesScanner = new Scanner(Gdx.files.internal(location).reader());
		
		int numberOfCoordinates = boardCoordinatesScanner.nextInt();
		
		ImmutablePoint2[] fourPlayerBoardCoordinatesArray = new ImmutablePoint2[numberOfCoordinates];
		for (int i = 0; i < numberOfCoordinates; ++i) {
			fourPlayerBoardCoordinatesArray[i] = new ImmutablePoint2(boardCoordinatesScanner.nextInt(), boardCoordinatesScanner.nextInt());
		}
		
		boardCoordinatesScanner.close();
		
		fourPlayerBoardCoordinates = Collections.unmodifiableList(Arrays.asList(fourPlayerBoardCoordinatesArray));
	}
	
	private void loadSpawnPointCoordinates(String location) {
		spawnPointCoordinates = new EnumMap<GameColor, List<ImmutablePoint2>>(GameColor.class);
		
		Scanner boardCoordinatesScanner = new Scanner(Gdx.files.internal(location).reader());
		
		int numberOfCoordinates = boardCoordinatesScanner.nextInt();
		int coordinatesPerColor = boardCoordinatesScanner.nextInt();
		
		ImmutablePoint2[] spawnPointCoordinatesArray = new ImmutablePoint2[numberOfCoordinates];
		for (int i = 0; i < numberOfCoordinates; ++i) {
			spawnPointCoordinatesArray[i] = new ImmutablePoint2(boardCoordinatesScanner.nextInt(), boardCoordinatesScanner.nextInt());
		}
		
		boardCoordinatesScanner.close();
		
		for (int i = 0; i < GameColor.values().length; ++i) {
			ImmutablePoint2[] temp = Arrays.copyOfRange(spawnPointCoordinatesArray, i * coordinatesPerColor, (i + 1) * coordinatesPerColor);
			List<ImmutablePoint2> immutableList = Collections.unmodifiableList(Arrays.asList(temp));
			spawnPointCoordinates.put(GameColor.values()[i], immutableList);
		}
	}
	
	private void loadAirplaneSprites(String bluePlaneLoc, String greenPlaneLoc, String redPlaneLoc, String yellowPlaneLoc) {
		airplaneSprites = new EnumMap<GameColor, Texture>(GameColor.class);
		
		airplaneSprites.put(GameColor.BLUE, new Texture(Gdx.files.internal(bluePlaneLoc)));
		airplaneSprites.put(GameColor.GREEN, new Texture(Gdx.files.internal(greenPlaneLoc)));
		airplaneSprites.put(GameColor.RED, new Texture(Gdx.files.internal(redPlaneLoc)));
		airplaneSprites.put(GameColor.YELLOW, new Texture(Gdx.files.internal(yellowPlaneLoc)));
		
		for (Texture texture : airplaneSprites.values()) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		}
	}
	
	private void loadChoiceDot(String choiceDotLocation) {
		choiceDot = new Texture(choiceDotLocation);
		choiceDot.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
	}
	
	public Texture getFourPlayerBoard() {
		return fourPlayerBoard;
	}
	
	public List<ImmutablePoint2> getFourPlayerBoardCoordinates() {
		return fourPlayerBoardCoordinates;
	}
	
	public Texture getAirplaneSprite(GameColor color) {
		return airplaneSprites.get(color);
	}

	public Map<GameColor, List<ImmutablePoint2>> getSpawnPointCoordinates() {
		return spawnPointCoordinates;
	}
	
	public Texture getChoiceDot() {
		return choiceDot;
	}
}
