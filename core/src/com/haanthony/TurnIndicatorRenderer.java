package com.haanthony;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.haanthony.Game.GameColor;
import com.haanthony.TurnIndicator.Direction;

public class TurnIndicatorRenderer {
	private Map<GameColor, TurnIndicator> indicators;
	private Group renderGroup;
	
	private static final float SPIN_DURATION = 0.4f;
	private static final Interpolation SPIN_INTERPOLATION = Interpolation.elasticOut;
	
	private Direction currentDirection = Direction.UP_RIGHT;
	
	public TurnIndicatorRenderer(float positionX, float positionY) {
		indicators = new EnumMap<>(GameColor.class);
		renderGroup = new Group();
		
		for (GameColor color : GameColor.values()) {
			TurnIndicator turnIndicator = new TurnIndicator(AssetLoader.getInstance().getColorDirections().get(color),
					AssetLoader.getInstance().getColors().get(color));

			indicators.put(color, turnIndicator);
			renderGroup.addActor(turnIndicator);
		}
		
		renderGroup.setPosition(positionX + TurnIndicator.RADIUS , positionY + TurnIndicator.RADIUS);
		renderGroup.setOrigin(-TurnIndicator.RADIUS, -TurnIndicator.RADIUS);
	}
	
	public void spin() {
		renderGroup.addAction(Actions.rotateBy(-90, SPIN_DURATION, SPIN_INTERPOLATION));
		currentDirection = Direction.nextDirection(currentDirection);
	}
	
	public void spinTo(GameColor color) {
		currentDirection = AssetLoader.getInstance().getColorDirections().get(color);
		float direction = currentDirection.getAngle();
		
		// This conditional makes sure that the turn indicator always spins clockwise
		if (renderGroup.getRotation() < direction) {
			renderGroup.addAction(Actions.sequence(Actions.rotateBy(direction - 360, SPIN_DURATION, SPIN_INTERPOLATION), Actions.rotateTo(direction)));
		} else {
			renderGroup.addAction(Actions.rotateTo(direction, SPIN_DURATION, SPIN_INTERPOLATION));
		}
	}
	
	public Group getRenderGroup() {
		return renderGroup;
	}
}
