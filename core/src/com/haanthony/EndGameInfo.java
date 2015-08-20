package com.haanthony;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.haanthony.Game.GameColor;

public class EndGameInfo implements Iterable<GameColor> {
	private List<GameColor> placementOfColors;
	
	public EndGameInfo() {
		placementOfColors = new ArrayList<GameColor>();
	}
	
	public void insertPlayer(GameColor color) {
		if (placementOfColors.contains(color)) {
			throw new IllegalArgumentException("The given color (" + color + ") has already been placed in the end game info");
		}
		placementOfColors.add(color);
	}

	@Override
	public Iterator<GameColor> iterator() {
		return placementOfColors.iterator();
	}
	
	private static final char HEADER = '*';
	private static final char DELIMITER = ',';
	private static final char END_INDICATOR = '(';
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(HEADER);
		
		Iterator<GameColor> iter = iterator();
		if (iter.hasNext()) {
			builder.append(iter.next());
			while (iter.hasNext()) {
				builder.append(DELIMITER).append(iter.next());
			}
		}
		
		builder.append(END_INDICATOR);
		return builder.toString();
	}
	
	public static EndGameInfo fromString(String endGameInfoString) {
		// Process header
		EndGameInfo info = new EndGameInfo();
		
		if (endGameInfoString.charAt(0) != HEADER) {
			throw new IllegalArgumentException("Header not found");
		}
		
		// Process colors
		int index = 1;
		int nextSeperater = endGameInfoString.indexOf(DELIMITER, index);
		
		while (nextSeperater != -1) {
			GameColor color = GameColor.valueOf(endGameInfoString.substring(index, nextSeperater));
			try {
				info.insertPlayer(color);
			} catch (IllegalArgumentException e) {
				System.err.println("The end game info string contains multiples of the same color");
			}
			index = nextSeperater + 1;
			nextSeperater = endGameInfoString.indexOf(DELIMITER, index);
		}
		
		// Process end indicator
		nextSeperater = endGameInfoString.indexOf(END_INDICATOR, index);
		if (nextSeperater == -1) {
			throw new IllegalArgumentException("The end game info string has no end indicater");
		}
		
		GameColor color = GameColor.valueOf(endGameInfoString.substring(index, nextSeperater));
		
		try {
			info.insertPlayer(color);
		} catch (IllegalArgumentException e) {
			System.err.println("The end game info string contains multiples of the same color");
		}
		
		return info;
	}
}
