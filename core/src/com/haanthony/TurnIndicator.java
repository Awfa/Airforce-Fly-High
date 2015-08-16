package com.haanthony;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TurnIndicator extends Group {
	public enum Direction {
		UP_RIGHT(0),
		UP_LEFT(90),
		DOWN_LEFT(180),
		DOWN_RIGHT(270);
		
		private float angle;
		
		private Direction(float angle) {
			this.angle = angle;
		}
		
		public float getAngle() {
			return angle;
		}
		
		public static Direction nextDirection(Direction direction) {
			return Direction.values()[(direction.ordinal() + 1) % Direction.values().length];
		}
	}
	
	public static final int RADIUS = 47;
	
	private Map<Direction, Actor> imageMap;
	
	// Constructs a new TurnIndicator where the given direction is the one that will have the arrow be pointing when visible
	public TurnIndicator(Direction direction, Color color) {
		imageMap = new EnumMap<Direction, Actor>(Direction.class);
		for (Direction d : Direction.values()) {
			Actor actorToAdd;
			if (d == direction) {
				Group indicatorGroup = new Group();
				indicatorGroup.addActor(new ClippingImage(AssetLoader.getInstance().getTurnIndicatorCircle(), d));
				indicatorGroup.addActor(new ClippingImage(AssetLoader.getInstance().getTurnIndicatorArrow(), d));
				for (Actor actor : indicatorGroup.getChildren()) {
					actor.setColor(color);
				}
				actorToAdd = indicatorGroup;
			} else {
				actorToAdd = new ClippingImage(AssetLoader.getInstance().getTurnIndicatorCircle(), d);
				actorToAdd.setColor(color);
			}
			
			actorToAdd.setOrigin(-RADIUS, -RADIUS);
			actorToAdd.setRotation(d.getAngle() - direction.getAngle());
			
			imageMap.put(d, actorToAdd);
			this.addActor(actorToAdd);
		}
	}
	
	private static class ClippingImage extends Image {
		private final ImmutablePoint2 clipOrigin;
		private final int clipWidth;
		
		public ClippingImage(Texture texture, Direction direction) {
			this(texture, AssetLoader.getInstance().getClipPoint(direction), AssetLoader.getInstance().getClipWidth());
		}
		
		public ClippingImage(Texture texture, ImmutablePoint2 clipOrigin, int clipWidth) {
			super(texture);
			
			this.clipOrigin = clipOrigin;
			this.clipWidth = clipWidth;
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			Vector2 clipCoords = getStage().stageToScreenCoordinates(new Vector2(clipOrigin.x, clipOrigin.y));
			
			Vector2 measurePoint1 = getStage().stageToScreenCoordinates(new Vector2(0, 0));
			Vector2 measurePoint2 = getStage().stageToScreenCoordinates(new Vector2(clipWidth, 0));
			int clipWidthOnScreen = (int) (measurePoint2.x - measurePoint1.x);
			
			batch.flush();
			
			Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
			Gdx.gl.glScissor((int)clipCoords.x, (int)clipCoords.y, clipWidthOnScreen, clipWidthOnScreen);
			
			super.draw(batch, parentAlpha);
			
			batch.flush();
			Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		}
	}
}
