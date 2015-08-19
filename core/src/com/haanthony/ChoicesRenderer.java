package com.haanthony;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.haanthony.Choice.ChoiceType;

public class ChoicesRenderer {
	private final static float CHOICE_ARROW_DEFAULT_OFFSET = 30;
	private final static float CHOICE_ARROW_BOUNCE_OFFSET = 10;
	private final static float CHOICE_ARROW_BOUNCE_DURATION = 0.5f;
	
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
			final ImmutablePoint2 position;
			
			if (choice.getType() == ChoiceType.MOVE_PLANE_TO_RUNWAY) {
				position = AssetLoader.getInstance().getSpawnPointCoordinates().get(choice.getColor()).get(4);
			} else {
				position = AssetLoader.getInstance().getFourPlayerBoardCoordinates().get(choice.getDestination());
			}
			
			// This section makes the choice arrow - the arrow that designates what plane the choice will move
			Choice current = choice;
			while (current.getParentChoice() != null) {
				current = current.getParentChoice();
			}
			
			final Image choiceArrow = new Image(AssetLoader.getInstance().getChoiceArrow());
			choiceArrow.setVisible(false);
			
			ImmutablePoint2 positionOfArrow;
			if (current.getType() == ChoiceType.LAUNCH_PLANE_FROM_RUNWAY) {
				positionOfArrow = AssetLoader.getInstance().getSpawnPointCoordinates().get(choice.getColor()).get(4);
			} else if (current.getType() == ChoiceType.FLY) {
				positionOfArrow = AssetLoader.getInstance().getFourPlayerBoardCoordinates().get(current.getOrigin());
			} else {
				positionOfArrow = AssetLoader.getInstance().getSpawnPointCoordinates().get(choice.getColor()).get(0);
			}
			
			choiceArrow.setPosition(positionOfArrow.x, positionOfArrow.y + CHOICE_ARROW_DEFAULT_OFFSET, Align.center);
			choiceArrow.addAction(Actions.forever(
					Actions.sequence
						(
							Actions.moveBy(0, CHOICE_ARROW_BOUNCE_OFFSET, CHOICE_ARROW_BOUNCE_DURATION, Interpolation.circleOut),
							Actions.moveBy(0, -CHOICE_ARROW_BOUNCE_OFFSET, CHOICE_ARROW_BOUNCE_DURATION, Interpolation.circleIn)
						)
					));

			Image choiceDot = new Image(AssetLoader.getInstance().getChoiceDot());
			
			choiceDot.setPosition(position.x, position.y, Align.center);
			choiceDot.setBounds(choiceDot.getX(), choiceDot.getY(), choiceDot.getWidth(), choiceDot.getHeight());
			
			choiceImages.add(choiceDot);
			choiceDot.addListener(new InputListener() {
				private boolean pressed = false;
				private boolean inChoiceDot = false;
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					Image image = ((Image)event.getListenerActor());
					Drawable drawable = AssetLoader.getInstance().getChoiceDotPressed();
					changeImage(image, drawable);
					pressed = true;
					return true;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					if (inChoiceDot) {
						manager.playChoice(choice);
						choiceArrow.remove();
						clearChoices();
					} else {
						pressed = false;
					}
				}
				
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					Image image = ((Image)event.getListenerActor());
					Drawable drawable;
					if (!pressed) {
						drawable = AssetLoader.getInstance().getChoiceDotHovered();
						choiceArrow.setVisible(true);
					} else {
						drawable = AssetLoader.getInstance().getChoiceDotPressed();
					}
					
					changeImage(image, drawable);
					
					inChoiceDot = true;
				}
				
				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					Image image = ((Image)event.getListenerActor());
					Drawable drawable = AssetLoader.getInstance().getChoiceDot();
					changeImage(image, drawable);
					
					inChoiceDot = false;
					
					choiceArrow.setVisible(false);
				}
				
				private void changeImage(Image image, Drawable drawable) {
					image.setDrawable(drawable);
				}
			});
			
			renderGroup.addActor(choiceDot);
			renderGroup.addActor(choiceArrow);
		}
	}
	
	private void clearChoices() {
		for (Image choiceImage : choiceImages) {
			renderGroup.removeActor(choiceImage);
		}
		choiceImages.clear();
	}
}
