package com.haanthony;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class HangerRenderManager {
	public static final float AIRPLANE_SHUFFLE_DURATION = 0.1f;
	
	private List<ImmutablePoint2> spawnPoints;
	
	private Queue<Image> hangerPlanes;
	private Queue<Image> runwayPlanes;
	
	public HangerRenderManager(List<ImmutablePoint2> spawnPoints) {
		this.spawnPoints = spawnPoints;
		
		hangerPlanes = new ArrayDeque<Image>();
		runwayPlanes = new ArrayDeque<Image>();
	}
	
	public void addPlane(Image plane) {
		if (hangerPlanes.size() == 4) {
			throw new IllegalStateException("Hanger Render Manager to full to add a plane to the runway");
		}
		
		ImmutablePoint2 position = spawnPoints.get(hangerPlanes.size());
		plane.setPosition(position.x, position.y, Align.center);
		hangerPlanes.add(plane);
	}
	
	public void addPlaneFromBoard(Image plane) {
		if (hangerPlanes.size() == 4) {
			throw new IllegalStateException("Hanger Render Manager to full to add a plane to the runway");
		}
		
		SequenceAction moveActionToHanger = new SequenceAction();
		ImmutablePoint2 position = spawnPoints.get(hangerPlanes.size());

		float originX = plane.getX() + plane.getOriginX();
		float originY = plane.getY() + plane.getOriginY();
		
		float targetAngle = (((float) Math.toDegrees(Math.atan2(originY - position.y, originX - position.x))) + 180);
		float angleDifference = targetAngle - plane.getRotation();
		while (Math.abs(angleDifference) > 180) {
			float sign = angleDifference / Math.abs(angleDifference);
			angleDifference -= sign * 360;
		}
		float spinDuration = Math.abs(angleDifference) / BoardRenderManager.SPIN_SPEED;
		
		RotateByAction rotationAction = new RotateByAction();
		rotationAction.setAmount(angleDifference);
		rotationAction.setDuration(spinDuration);
		
		MoveToAction flyAction = new MoveToAction();
		flyAction.setPosition(position.x, position.y, Align.center);
		flyAction.setDuration(AIRPLANE_SHUFFLE_DURATION * 3);
		
		moveActionToHanger.addAction(rotationAction);
		moveActionToHanger.addAction(flyAction);
		plane.addAction(moveActionToHanger);
		hangerPlanes.add(plane);
	}
	
	public void movePlaneToRunway() {
		if (hangerPlanes.size() == 0) {
			throw new IllegalStateException("No planes in hanger to move");
		}
		
		ImmutablePoint2 position = spawnPoints.get(spawnPoints.size() - 1);
		
		Image planeToMove = hangerPlanes.remove();
		runwayPlanes.add(planeToMove);
		
		MoveToAction moveActionToRunway = new MoveToAction();
		moveActionToRunway.setPosition(position.x, position.y, Align.center);
		moveActionToRunway.setDuration(AIRPLANE_SHUFFLE_DURATION);
		planeToMove.addAction(moveActionToRunway);
		
		for (int i = 0; i < hangerPlanes.size(); ++i) {
			Image planeToShuffle = hangerPlanes.remove();
			
			MoveToAction moveActionToNewHangerSpot = new MoveToAction();
			moveActionToNewHangerSpot.setPosition(spawnPoints.get(i).x, spawnPoints.get(i).y, Align.center);
			moveActionToNewHangerSpot.setDuration(AIRPLANE_SHUFFLE_DURATION);
			
			planeToShuffle.addAction(moveActionToNewHangerSpot);
			
			hangerPlanes.add(planeToShuffle);
		}
	}
	
	public Image removePlaneFromRunway() {
		if (runwayPlanes.size() == 0) {
			throw new IllegalStateException("No planes to remove from runway");
		}
		
		return runwayPlanes.remove();
	}
	
	public boolean isDoneRendering() {
		for (Image image : hangerPlanes) {
			if (image.getActions().size > 0) {
				return false;
			}
		}
		
		for (Image image : runwayPlanes) {
			if (image.getActions().size > 0) {
				return false;
			}
		}
		
		return true;
	}
}
