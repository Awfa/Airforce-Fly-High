package com.haanthony;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.haanthony.Choice.ChoiceType;
import com.haanthony.Game.GameColor;

public class BoardRenderManager {
	private List<ImmutablePoint2> boardPositions;
	private Map<GameColor, HangerRenderManager> hangerRenderManagers;
	private Group renderGroup;
	
	private Map<GameColor, List<Set<Image>>> positionLists;
	
	public BoardRenderManager() {
		boardPositions = AssetLoader.getInstance().getFourPlayerBoardCoordinates();
		
		renderGroup = new Group();
		Image board = new Image(AssetLoader.getInstance().getFourPlayerBoard());
		renderGroup.addActor(board);
		
		Map<GameColor, List<ImmutablePoint2>> spawnPoints = AssetLoader.getInstance().getSpawnPointCoordinates();
		
		hangerRenderManagers = new HashMap<GameColor, HangerRenderManager>();
		for (GameColor color : GameColor.values()) {
			hangerRenderManagers.put(color, new HangerRenderManager(spawnPoints.get(color)));
			
			for (int i = 0; i < Game.NUMBER_OF_AIRPLANES_PER_PLAYER; ++i) {
				Image plane =  new Image(AssetLoader.getInstance().getAirplaneSprite(color));
				hangerRenderManagers.get(color).addPlane(plane);
				renderGroup.addActor(plane);
			}
		}
		
		positionLists = new EnumMap<GameColor, List<Set<Image>>>(GameColor.class);
		for (GameColor color : GameColor.values()) {
			List<Set<Image>> positionList = new ArrayList<Set<Image>>(boardPositions.size());
			for (int i = 0; i < boardPositions.size(); ++i) {
				positionList.add(new HashSet<Image>());
			}
			
			positionLists.put(color, positionList);
		}
		
	}
	
	public void processChoice(Choice choice) {
		Deque<Choice> choiceStack = new ArrayDeque<Choice>();
		
		if (choice == null) {
			throw new NullPointerException();
		}
		Choice currentChoice = choice;
		choiceStack.push(currentChoice);
		while (currentChoice.getParentChoice() != null) {
			currentChoice = currentChoice.getParentChoice();
			choiceStack.push(currentChoice);
		}
		
		switch (currentChoice.getType()) {
		case MOVE_PLANE_TO_RUNWAY:
			hangerRenderManagers.get(choice.getColor()).movePlaneToRunway();
			break;
			
		case LAUNCH_PLANE_FROM_RUNWAY:
			Image planeToLaunch = hangerRenderManagers.get(choice.getColor()).removePlaneFromRunway();
			
			movePlane(planeToLaunch, choiceStack);
			break;
			
		case FLY:
			movePlanes(positionLists.get(choice.getColor()).get(currentChoice.getOrigin()), choiceStack);
			break;
			
		default:
			break;
		}
	}
	
	public boolean isDoneRendering() {
		for (List<Set<Image>> imageLists : positionLists.values()) {
			for (Set<Image> images : imageLists) {
				for (Image image : images) {
					if (image.getActions().size > 0) {
						return false;
					}
				}
			}
		}
		
		for (HangerRenderManager hanger : hangerRenderManagers.values()) {
			if (!hanger.isDoneRendering()) {
				return false;
			}
		}
		
		return true;
	}
	
	private void movePlanes(Set<Image> planes, Deque<Choice> choiceStack) {
		planes = new HashSet<Image>(planes);
		
		for (Image plane : planes) {
			movePlane(plane, choiceStack);
		}
	}
	
	private void movePlane(Image image, Deque<Choice> choiceStack) {
		GameColor color = choiceStack.peekLast().getColor();
		if (choiceStack.peek().getType() != ChoiceType.LAUNCH_PLANE_FROM_RUNWAY) {
			positionLists.get(color).get(choiceStack.peek().getOrigin()).remove(image);
		}
		positionLists.get(color).get(choiceStack.peekLast().getDestination()).add(image);
		
		SequenceAction planeMoveAction = new SequenceAction();
		for (Choice choice : choiceStack) {
			ImmutablePoint2 destination = boardPositions.get(choice.getDestination());
			MoveToAction subMove = new MoveToAction();
			subMove.setPosition(destination.x, destination.y, Align.center);
			subMove.setDuration(0.2f);
			planeMoveAction.addAction(subMove);
			
			for (Integer position : choice.getTakedowns()) {
				for (GameColor planeColor : GameColor.values()) {
					if (planeColor != choice.getColor()) {
						Set<Image> imagesToRemove = new HashSet<Image>(positionLists.get(planeColor).get(position));
						for (Image imageToRemove : imagesToRemove) {
							positionLists.get(planeColor).get(position).remove(imageToRemove);
							hangerRenderManagers.get(planeColor).addPlaneFromBoard(imageToRemove);
						}
					}
				}
			}
		}
		
		image.addAction(planeMoveAction);
	}
	
	public Group getRenderGroup() {
		return renderGroup;
	}
}
