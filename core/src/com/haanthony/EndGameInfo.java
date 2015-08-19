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
}
