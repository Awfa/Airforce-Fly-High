package com.haanthony;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.haanthony.Game.GameColor;
import com.haanthony.TurnIndicator.Direction;

public class AssetLoader {
	private static AssetLoader assetLoader;
	
	private Texture fourPlayerBoard;
	private List<ImmutablePoint2> fourPlayerBoardCoordinates;
	private Map<GameColor, List<ImmutablePoint2>> spawnPointCoordinates;
	private Map<GameColor, Texture> airplaneSprites;
	
	private Drawable choiceArrow;
	
	private Drawable choiceDot;
	private Drawable choiceDotHovered;
	private Drawable choiceDotPressed;
	
	private BitmapFont diceFont;
	
	private Texture turnIndicatorCircle;
	private Texture turnIndicatorArrow;

	private Map<Direction, ImmutablePoint2> turnIndicatorClipPoints;
	private int turnIndicatorClipWidth;

	private Map<GameColor, Direction> turnIndicatorColorDirections;
	private Map<GameColor, Color> colors;
	
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
		loadChoiceArrow("choiceArrow.png");
		loadChoiceDot("choiceDotFullShadow.png", "choiceDotHalfShadow.png", "choiceDotNoShadow.png");
		loadDiceFont("diceFont.txt");
		loadTurnIndicators("turnIndicatorCircle.png", "turnIndicatorArrow.png", "turnIndicatorClipPoints.txt", "turnIndicatorColorDirections.txt");
		loadColors("colors.txt");
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
	
	private void loadChoiceArrow(String choiceArrowLocation) {
		Texture choiceArrowTexture = new Texture(choiceArrowLocation);
		choiceArrow = new TextureRegionDrawable(new TextureRegion(choiceArrowTexture));
		choiceArrowTexture.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
	}
	
	private void loadChoiceDot(String choiceDotLocation, String hoveredChoiceDotLocation, String pressedChoiceDotLocation) {
		choiceDot = new TextureRegionDrawable(new TextureRegion(new Texture(choiceDotLocation)));
		((TextureRegionDrawable) choiceDot).getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		
		choiceDotHovered = new TextureRegionDrawable(new TextureRegion(new Texture(hoveredChoiceDotLocation)));
		((TextureRegionDrawable) choiceDotHovered).getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		
		choiceDotPressed = new TextureRegionDrawable(new TextureRegion(new Texture(pressedChoiceDotLocation)));
		((TextureRegionDrawable) choiceDotPressed).getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Nearest);
	}
	
	private void loadDiceFont(String diceFontLocation) {
		diceFont = new BitmapFont(Gdx.files.internal(diceFontLocation));
		diceFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Nearest);
	}
	
	private void loadTurnIndicators(String locationForCircle, String locationForArrow, String locationForClipPoints, String colorDirectionsLocation) {
		turnIndicatorCircle = new Texture(Gdx.files.internal(locationForCircle));
		turnIndicatorArrow = new Texture(Gdx.files.internal(locationForArrow));
		
		turnIndicatorCircle.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		turnIndicatorArrow.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		
		turnIndicatorClipPoints = new EnumMap<Direction, ImmutablePoint2>(Direction.class);
		Scanner scanner = new Scanner(Gdx.files.internal(locationForClipPoints).reader());
		turnIndicatorClipWidth = scanner.nextInt();
		while (scanner.hasNext()) {
			Direction direction = Direction.valueOf(scanner.next());
			turnIndicatorClipPoints.put(direction, new ImmutablePoint2(scanner.nextInt(), scanner.nextInt()));
		}
		scanner.close();
		
		turnIndicatorColorDirections = new EnumMap<GameColor, Direction>(GameColor.class);
		scanner = new Scanner(Gdx.files.internal(colorDirectionsLocation).reader());
		while (scanner.hasNext()) {
			turnIndicatorColorDirections.put(GameColor.valueOf(scanner.next()), Direction.valueOf(scanner.next()));
		}
		scanner.close();
	}
	
	private void loadColors(String colorsLocation) {
		colors = new EnumMap<>(GameColor.class);
		Scanner scanner = new Scanner(Gdx.files.internal(colorsLocation).reader());
		while (scanner.hasNext()) {
			colors.put(GameColor.valueOf(scanner.next()), new Color(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), 1.f));
		}
		scanner.close();
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
	
	public Drawable getChoiceArrow() {
		return choiceArrow;
	}
	
	public Drawable getChoiceDot() {
		return choiceDot;
	}
	
	public Drawable getChoiceDotHovered() {
		return choiceDotHovered;
	}
	
	public Drawable getChoiceDotPressed() {
		return choiceDotPressed;
	}

	public BitmapFont getDiceFont() {
		return diceFont;
	}
	
	public Texture getTurnIndicatorCircle() {
		return turnIndicatorCircle;
	}
	
	public Texture getTurnIndicatorArrow() {
		return turnIndicatorArrow;
	}
	
	public ImmutablePoint2 getClipPoint(Direction direction) {
		return turnIndicatorClipPoints.get(direction);
	}
	
	public int getClipWidth() {
		return turnIndicatorClipWidth;
	}
	
	public Map<GameColor, Direction> getColorDirections() {
		return turnIndicatorColorDirections;
	}
	
	public Map<GameColor, Color> getColors() {
		return colors;
	}
}
