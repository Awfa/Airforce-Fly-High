package com.haanthony;

import java.util.HashMap;
import java.util.Map;

import com.haanthony.Game.GameColor;

public class GameInfo {
	private Map<AirplaneFormation, Integer> airplanePositions;
	private Map<GameColor, Integer> airplanesInHanger;
	private Map<GameColor, Integer> airplanesOnRunway;
	private Choice lastChoice;
	private int lastDiceRoll;
	
	public GameInfo(Board board, Map<GameColor, Hanger> hangers, Choice lastChoice, int lastDiceRoll) {
		airplanePositions = board.getAirplaneFormationPositions();
		
		airplanesInHanger = new HashMap<GameColor, Integer>();
		airplanesOnRunway = new HashMap<GameColor, Integer>();
		for (GameColor color : hangers.keySet()) {
			airplanesInHanger.put(color, hangers.get(color).getPlanesInHanger());
			airplanesOnRunway.put(color, hangers.get(color).getPlanesOnRunway());
		}
		
		this.lastChoice = lastChoice;
		this.lastDiceRoll = lastDiceRoll;
	}

	public Map<AirplaneFormation, Integer> getAirplanePositions() {
		return airplanePositions;
	}

	public int getAirplanesInHanger(GameColor hangerColor) {
		return airplanesInHanger.get(hangerColor);
	}

	public int getAirplanesOnRunway(GameColor hangerColor) {
		return airplanesOnRunway.get(hangerColor);
	}

	public Choice getLastChoice() {
		return lastChoice;
	}

	public int getLastDiceRoll() {
		return lastDiceRoll;
	}
}
