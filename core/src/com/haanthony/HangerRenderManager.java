package com.haanthony;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class HangerRenderManager {
	public static final float AIRPLANE_SHUFFLE_DURATION = 0.1f;
	
	private List<ImmutablePoint2> spawnPoints;
	
	private Queue<AirplaneSprite> hangerPlanes;
	private Queue<AirplaneSprite> runwayPlanes;
	
	public HangerRenderManager(List<ImmutablePoint2> spawnPoints) {
		this.spawnPoints = spawnPoints;
		
		hangerPlanes = new ArrayDeque<AirplaneSprite>();
		runwayPlanes = new ArrayDeque<AirplaneSprite>();
	}
	
	public void addPlane(AirplaneSprite plane) {
		if (hangerPlanes.size() == 4) {
			throw new IllegalStateException("Hanger Render Manager to full to add a plane to the runway");
		}
		
		ImmutablePoint2 position = spawnPoints.get(hangerPlanes.size());
		plane.moveToPoint(position);
		hangerPlanes.add(plane);
	}
	
	public void addPlaneFromBoard(AirplaneSprite plane) {
		if (hangerPlanes.size() == 4) {
			throw new IllegalStateException("Hanger Render Manager to full to add a plane to the runway");
		}
		
		plane.moveToPoint(spawnPoints.get(hangerPlanes.size()));
		hangerPlanes.add(plane);
	}
	
	public void movePlaneToRunway() {
		if (hangerPlanes.size() == 0) {
			throw new IllegalStateException("No planes in hanger to move");
		}
		
		AirplaneSprite planeToMove = hangerPlanes.remove();
		runwayPlanes.add(planeToMove);
		planeToMove.moveToPoint(spawnPoints.get(spawnPoints.size() - 1));
		
		for (int i = 0; i < hangerPlanes.size(); ++i) {
			AirplaneSprite planeToShuffle = hangerPlanes.remove();
			
			planeToShuffle.moveToPoint(spawnPoints.get(i));
			
			hangerPlanes.add(planeToShuffle);
		}
	}
	
	public AirplaneSprite removePlaneFromRunway() {
		if (runwayPlanes.size() == 0) {
			throw new IllegalStateException("No planes to remove from runway");
		}
		
		return runwayPlanes.remove();
	}
	
	public boolean isDoneRendering() {
		for (AirplaneSprite plane : hangerPlanes) {
			if (plane.hasActions()) {
				return false;
			}
		}
		
		for (AirplaneSprite plane : runwayPlanes) {
			if (plane.hasActions()) {
				return false;
			}
		}
		
		return true;
	}
}
