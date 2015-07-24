package com.haanthony;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.haanthony.Game.GameColor;

public class AirplaneFormation implements Iterable<Airplane> {
	private GameColor color;
	private Set<Airplane> airplanes;
	private boolean disbanded;
	
	public AirplaneFormation(GameColor color) {
		airplanes = new HashSet<Airplane>();
		this.color = color;
		disbanded = false;
	}
	
	public void addPlane(Airplane plane) {
		if (airplanes == null) {
			throw new NullPointerException();
		}
		
		if (airplanes.contains(plane)) {
			throw new IllegalArgumentException("This plane is already in the formation.");
		}
		
		airplanes.add(plane);
	}
	
	public void combineWithOtherFormation(AirplaneFormation otherFormation) {
		if (otherFormation == null) {
			throw new NullPointerException();
		}
		
		if (otherFormation.equals(this)) {
			throw new IllegalArgumentException("Cannot combine the same formations");
		}
		
		otherFormation.airplanes.addAll(airplanes);
		disband();
	}
	
	public void disband() {
		disbanded = true;
		airplanes = null;
	}
	
	public boolean isDisbanded() {
		return disbanded;
	}
	
	public int getSize() {
		if (disbanded) {
			return 0;
		}
		return airplanes.size();
	}
	
	public GameColor getColor() { return color; }

	@Override
	public Iterator<Airplane> iterator() {
		if (disbanded) {
			throw new IllegalStateException("Cannot iterate over a disbanded formation");
		}
		return airplanes.iterator();
	}
}
