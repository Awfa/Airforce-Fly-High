package com.haanthony;

import com.haanthony.Game.GameColor;

public class Hanger {
	private Airplane[] hanger;
	private Airplane[] runway;
	
	private int planesInHanger; // pointer to cap on hanger array
	private int planesOnRunway; // pointer to cap on runway array
	
	private int capacity;
	private GameColor color;
	
	public Hanger(int capacity, GameColor color) {
		hanger = new Airplane[capacity];
		runway = new Airplane[capacity];
		
		planesInHanger = 0;
		planesOnRunway = 0;
		
		this.capacity = capacity;
		this.color = color;
		
		for (int i = 0; i < capacity; ++i) {
			hanger[planesInHanger++] = new Airplane();
		}
	}
	
	public void moveAirplaneToRunway() {
		if (planesInHanger == 0) {
			throw new IllegalStateException("No planes to move from hanger to runway.");
		}
		
		runway[planesOnRunway] = hanger[planesInHanger - 1];
		planesOnRunway++;
		planesInHanger--;
	}
	
	public AirplaneFormation launchPlaneFromRunway() {
		if (planesOnRunway == 0) {
			throw new IllegalStateException("No planes to launch from runway.");
		}
		
		Airplane planeToLaunch = runway[planesOnRunway - 1];
		runway[planesOnRunway - 1] = null;
		planesOnRunway--;
		
		AirplaneFormation formationToLaunch = new AirplaneFormation(color);
		formationToLaunch.addPlane(planeToLaunch);
		return formationToLaunch;
	}
	
	public void landAirplanes(AirplaneFormation planes) {
		if (planes == null) {
			throw new NullPointerException();
		}
		
		if (planes.getColor() != color) {
			throw new IllegalArgumentException("Cannot land plane onto hanger of a different color.");
		}
		
		if (planesInHanger + planesOnRunway + planes.getSize() == capacity) {
			throw new IllegalStateException("No more room in this hanger for more airplanes.");
		}
		
		for (Airplane plane : planes) {
			hanger[planesInHanger++] = plane;
		}
	}
	
	public int getPlanesInHanger() {
		return planesInHanger;
	}
	
	public int getPlanesOnRunway() {
		return planesOnRunway;
	}
}
