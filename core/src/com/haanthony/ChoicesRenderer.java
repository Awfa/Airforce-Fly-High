package com.haanthony;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.haanthony.Choice.ChoiceType;

public class ChoicesRenderer {
	private Group renderGroup;
	private Set<Image> choiceImages;
	private GameManager manager;
	
	public ChoicesRenderer(Group renderGroup, GameManager manager) {
		this.renderGroup = renderGroup;
		choiceImages = new HashSet<>();
		this.manager = manager;
	}
	
	public void renderChoices(Set<Choice> choices) {
		
		for (final Choice choice : choices) {
			ImmutablePoint2 position;
			
			if (choice.getType() == ChoiceType.MOVE_PLANE_TO_RUNWAY) {
				position = AssetLoader.getInstance().getSpawnPointCoordinates().get(choice.getColor()).get(4);
			} else {
				position = AssetLoader.getInstance().getFourPlayerBoardCoordinates().get(choice.getDestination());
			}

			Image choiceDot = new Image(AssetLoader.getInstance().getChoiceDot());
			
			choiceDot.setPosition(position.x, position.y, Align.center);
			choiceDot.setBounds(choiceDot.getX(), choiceDot.getY(), 30, 30);
			choiceDot.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					manager.playChoice(choice);
					clearChoices();
				}
			});
			
			choiceImages.add(choiceDot);
			renderGroup.addActor(choiceDot);
		}
	}
	
	private void clearChoices() {
		for (Image choiceImage : choiceImages) {
			renderGroup.removeActor(choiceImage);
		}
		choiceImages.clear();
	}
}
