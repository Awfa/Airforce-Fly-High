package com.haanthony;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class DiceRenderer {
	public static final float FLIP_TIME = 0.05f;
	public static final float HOLD_TIME = 0.8f;
	
	private float x, y;
	private Label label;
	
	private float time;
	private boolean active;
	private boolean stop;
	
	private int diceFace;
	
	public DiceRenderer(Group renderGroup, float x, float y) {
		this.x = x;
		this.y = y;
		
		BitmapFont diceFont = AssetLoader.getInstance().getDiceFont();
		
		label = new Label("1", new LabelStyle(diceFont, Color.WHITE));
		label.setAlignment(Align.bottom, Align.center);
		label.setVisible(false);
		renderGroup.addActor(label);
		
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
	
	public void startDiceRoll(int number) {
		label.setVisible(true);
		active = true;
		diceFace = number;
	}
	
	public void endDiceRoll() {
		if (active) {
			time = 0;
			active = false;
			stop = true;
			
			flipDiceTo(diceFace);
		}
	}
	
	public boolean isDoneRendering() {
		return !label.isVisible();
	}
	
	private void flashDice() {
		int newNumber = Integer.valueOf(label.getText().toString()) + 1;
		if (newNumber == 7) {
			newNumber = 1;
		}
		flipDiceTo(newNumber);
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
