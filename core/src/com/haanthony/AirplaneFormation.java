package com.haanthony;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.haanthony.Game.GameColor;

public class AirplaneFormation {
	private GameColor color;
	private int airplanes;
	private boolean disbanded;
	
	public AirplaneFormation(GameColor color) {
		this.color = color;
		disbanded = false;
	}
	
	public void addPlane() {
		airplanes++;
	}
	
	public void combineWithOtherFormation(AirplaneFormation otherFormation) {
		if (otherFormation == null) {
			throw new NullPointerException();
		}
		
		if (otherFormation.equals(this)) {
			throw new IllegalArgumentException("Cannot combine the same formations");
		}
		
		otherFormation.airplanes += airplanes;
		disband();
	}
	
	public void disband() {
		disbanded = true;
		airplanes = 0;
	}
	
	public boolean isDisbanded() {
		return disbanded;
	}
	
	public int getSize() {
		return airplanes;
	}
	
	public GameColor getColor() { return color; }
}
