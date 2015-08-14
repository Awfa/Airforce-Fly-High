package com.haanthony;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class DiceRenderer {
	public static final float FLIP_TIME = 0.05f;
	public static final float HOLD_TIME = 0.5f;
	public static final float DEFAULT_TIME = 0.2f;
	
	private float x, y;
	private Label label;
	private Random random;
	
	private float time;
	private boolean active;
	private boolean stop;
	
	public DiceRenderer(Group renderGroup, float x, float y) {
		this.x = x;
		this.y = y;
		
		BitmapFont diceFont = AssetLoader.getInstance().getDiceFont();
		
		label = new Label("", new LabelStyle(diceFont, Color.WHITE));
		label.setAlignment(Align.bottom, Align.center);
		label.setVisible(false);
		renderGroup.addActor(label);
		
		random = new Random();
		
		time = 0;
		active = false;
		stop = false;
	}
	
	public void update(float deltaTime) {
		if (active) {
			time += deltaTime;
			if (time > FLIP_TIME) {
				time -= FLIP_TIME;
				flashDice();
			}
		} else if (stop) {
			time += deltaTime;
			if (time > HOLD_TIME) {
				time = 0;
				
				label.setVisible(false);
				stop = false;
			}
		}
	}
	
	public void startDiceRollToNumber(final int number) {
		startDiceRoll();
		label.addAction(new Action() {
			private float timerToStop = 0;
			private boolean executed = false;
			
			@Override
			public boolean act(float delta) {
				timerToStop += delta;
				if (!executed && timerToStop > DEFAULT_TIME) {
					endDiceRollWithNumber(number);
					executed = true;
					return true;
				}
				
				return false;
			}
		});
	}
	
	public void startDiceRoll() {
		label.setVisible(true);
		active = true;
	}
	
	public void endDiceRollWithNumber(int number) {
		time = 0;
		active = false;
		stop = true;
		
		flipDiceTo(number);
	}
	
	public boolean isDoneRendering() {
		return !label.isVisible();
	}
	
	private void flashDice() {
		int newRandomNumber = random.nextInt(6) + 1;
		flipDiceTo(newRandomNumber);
	}
	
	private void flipDiceTo(int number) {
		label.setText(Integer.toString(number));
		
		label.invalidate();
		label.setHeight(label.getGlyphLayout().height);
		label.setWidth(label.getGlyphLayout().width);
		label.invalidate();
		
		label.setPosition(x, y, Align.center);
	}
	
}
