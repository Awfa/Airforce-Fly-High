package com.haanthony;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class AirplaneSprite extends Actor {
	public static final float FLY_SPEED = 700.f;
	public static final float SPIN_SPEED = 500.f;
	
	public static final Interpolation FLY_INTERPOLATION = Interpolation.exp10;
	public static final Interpolation SPIN_INTERPOLATION = Interpolation.linear;
	
	private Image airplane;
	
	public AirplaneSprite(Image airplane) {
		this.airplane = airplane;
		airplane.setRotation((float) (Math.random()*360+1));
	}
	
	@Override
	public void act(float delta) {
		airplane.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		airplane.draw(batch, parentAlpha);
	}
	
	@Override
	public boolean hasActions() {
		return airplane.hasActions();
	}
	
	public void moveToPoint(final ImmutablePoint2 destination) {
		moveToPoints(new Iterable<ImmutablePoint2>() {
			@Override
			public Iterator<ImmutablePoint2> iterator() {
				return new Iterator<ImmutablePoint2>() {
					private boolean notNexted = true;
					
					@Override
					public boolean hasNext() {
						return notNexted;
					}

					@Override
					public ImmutablePoint2 next() {
						notNexted = false;
						return destination;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
			
		});
	}
	
	public void moveToPoints(Iterable<ImmutablePoint2> destinations) {
		SequenceAction planeMoveAction = Actions.sequence();
		
		float currentX = airplane.getX() + airplane.getOriginX();
		float currentY = airplane.getY() + airplane.getOriginY();
		float currentRotation = airplane.getRotation();
		
		for (ImmutablePoint2 destination : destinations) {
			// Spinning
			float targetAngle = (((float) Math.toDegrees(Math.atan2(currentY - destination.y, currentX - destination.x))) + 180);
			float angleDifference = targetAngle - currentRotation;
			while (Math.abs(angleDifference) > 180) {
				float sign = angleDifference / Math.abs(angleDifference);
				angleDifference -= sign * 360;
			}
			float spinDuration = Math.abs(angleDifference) / SPIN_SPEED;
			
			RotateByAction rotationAction = Actions.rotateBy(angleDifference, spinDuration);
			rotationAction.setInterpolation(SPIN_INTERPOLATION);
			currentRotation += angleDifference;
			
			// Flying
			float deltaX = destination.x - currentX;
			float deltaY  = destination.y - currentY;
			float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			float flyDuration = distance / FLY_SPEED;
			
			MoveToAction flyAction = Actions.moveTo(destination.x, destination.y, flyDuration);
			flyAction.setAlignment(Align.center);
			flyAction.setInterpolation(FLY_INTERPOLATION);
			currentX = destination.x;
			currentY = destination.y;
			
			planeMoveAction.addAction(rotationAction);
			planeMoveAction.addAction(flyAction);
		}
		
		airplane.clearActions();
		airplane.addAction(planeMoveAction);
	}
}
