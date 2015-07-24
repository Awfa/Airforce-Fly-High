package com.haanthony;

import com.haanthony.Game.GameColor;

public class Hanger {
	private int hanger;
	private int runway;

	private int capacity;
	private GameColor color;
	
	public Hanger(int capacity, GameColor color) {
		hanger = capacity;
		runway = 0;

		this.capacity = capacity;
		this.color = color;
	}
	
	public void moveAirplaneToRunway() {
		if (hanger == 0) {
			throw new IllegalStateException("No planes to move from hanger to runway.");
		}
		
		hanger--;
		runway++;
	}
	
	public AirplaneFormation launchPlaneFromRunway() {
		if (runway == 0) {
			throw new IllegalStateException("No planes to launch from runway.");
		}
		
		runway--;
		
		AirplaneFormation formationToLaunch = new AirplaneFormation(color);
		formationToLaunch.addPlane();
		return formationToLaunch;
	}
	
	public void landAirplanes(AirplaneFormation planes) {
		if (planes == null) {
			throw new NullPointerException();
		}
		
		if (planes.getColor() != color) {
			throw new IllegalArgumentException("Cannot land plane onto hanger of a different color.");
		}
		
		if (hanger + runway + planes.getSize() == capacity) {
			throw new IllegalStateException("No more room in this hanger for more airplanes.");
		}
		
		hanger += planes.getSize();
		planes.disband();
	}
	
	public int getPlanesInHanger() {
		return hanger;
	}
	
	public int getPlanesOnRunway() {
		return runway;
	}
}
