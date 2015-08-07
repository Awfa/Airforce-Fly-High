package com.haanthony;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haanthony.Game.GameColor;

// The Board class represents the game board. It holds the airplane formations that are flying
// and allows querying information from the board. It manages where pieces are.
public class Board {
	private List<Set<AirplaneFormation>> positionList;
	private Map<AirplaneFormation, Integer> positionMap;
	private Map<GameColor, Set<AirplaneFormation>> colorMap;
	
	// Constructs a board of the given size
	public Board(int size) {
		positionList = new ArrayList<Set<AirplaneFormation>>(size);
		for (int i = 0; i < size; ++i) {
			positionList.add(new HashSet<AirplaneFormation>());
		}
		
		positionMap = new HashMap<AirplaneFormation, Integer>();
		
		colorMap = new EnumMap<GameColor, Set<AirplaneFormation>>(GameColor.class);
		for (GameColor color : GameColor.values()) {
			colorMap.put(color, new HashSet<AirplaneFormation>());
		}
	}
	
	// Get the set of airplane formations at this position
	public Set<AirplaneFormation> getFormations(int position) {
		return new HashSet<AirplaneFormation>(positionList.get(position));
	}
	
	// Get the set of all airplane formations of this color
	public Set<AirplaneFormation> getFormations(GameColor color) {
		return new HashSet<AirplaneFormation>(colorMap.get(color));
	}
	
	// Get the set of airplane formations of this color at this position
	public AirplaneFormation getFormations(int position, GameColor color) {
		for (AirplaneFormation formation : positionList.get(position)) {
			if (formation.getColor() == color) {
				return formation;
			}
		}
		
		return null;
	}
	
	// Get the set of airplane formations not of this color at this position
	public Set<AirplaneFormation> getFormationsExcludingColor(int position, GameColor color) {
		Set<AirplaneFormation> resultSet = new HashSet<>();
		
		for (AirplaneFormation formation : positionList.get(position)) {
			if (formation.getColor() != color) {
				resultSet.add(formation);
			}
		}
		
		return resultSet;
	}
	
	// Get the position of the given formation on the board
	// If the formation is not on the board, an IllegalArgumentException is thrown
	public int getFormationsPosition(AirplaneFormation formation) {
		if (!positionMap.containsKey(formation)) {
			throw new IllegalArgumentException("Formation not present on board");
		}
		
		return positionMap.get(formation);
	}
	
	// Add a new formation to the board at the given position
	// If the position is not within the bounds of the board, an IllegalArgumentException is thrown
	// If the formation is already on the board, an IllegalArgumentException is thrown
	public void addFormation(AirplaneFormation formation, int position) {
		verifyPositionsInbound(position);
		if (positionMap.containsKey(formation)) {
			throw new IllegalArgumentException("Formation is already on board");
		}
		positionList.get(position).add(formation);
		positionMap.put(formation, position);
		colorMap.get(formation.getColor()).add(formation);
	}
	
	// Move the given formation to a new position
	// If the position is not within the bounds of the board, an IllegalArgumentException is thrown
	// If the formation is not on the board, an IllegalArgumentException is thrown
	public void moveFormation(AirplaneFormation formation, int newPosition) {
		verifyPositionsInbound(newPosition);
		
		String posMapBefore = positionMap.toString();
		
		removeFormationFromPositions(formation);
		positionList.get(newPosition).add(formation);
		positionMap.put(formation, newPosition);
		
		System.err.println("Before:\n" + posMapBefore + "\nAfter:\n" + positionMap + "\n");
	}
	
	// Remove the given formation from the board
	// If the formation is not on the board, an IllegalArgumentException is thrown
	public void removeFormation(AirplaneFormation formation) {
		removeFormationFromPositions(formation);
		positionMap.remove(formation);
		colorMap.get(formation.getColor()).remove(formation);
	}
	
	public Map<AirplaneFormation, Integer> getAirplaneFormationPositions() {
		Map<AirplaneFormation, Integer> resultMap = new HashMap<>();
		
		for (AirplaneFormation formation : positionMap.keySet()) {
			resultMap.put(new AirplaneFormation(formation), positionMap.get(formation));
		}
		
		return resultMap;
	}
	
	// Remove the given formation from the positions map
	// If the formation is not on the board, an IllegalArgumentException is thrown
	private void removeFormationFromPositions(AirplaneFormation formation) {
		int formationsPosition = getFormationsPosition(formation);
		positionList.get(formationsPosition).remove(formation);
	}
	
	// Verify if the given position is on the board
	// If it isn't an IllegalArgumentException is thrown
	private void verifyPositionsInbound(int position) {
		if (position < 0 || position >= positionList.size()) {
			throw new IllegalArgumentException("The given position is not in bounds of the board");
		}
	}
}
