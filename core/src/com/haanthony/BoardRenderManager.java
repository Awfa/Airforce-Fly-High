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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.haanthony.Choice.ChoiceType;
import com.haanthony.Game.GameColor;

public class BoardRenderManager {
	private List<ImmutablePoint2> boardPositions;
	private Map<GameColor, HangerRenderManager> hangerRenderManagers;
	private Group renderGroup;
	
	private Map<GameColor, List<Set<AirplaneSprite>>> positionLists;
	
	private TurnIndicatorRenderer turnIndicatorRenderer;
	private DiceRenderer diceRenderer;
	
	public BoardRenderManager() {
		Image board = new Image(AssetLoader.getInstance().getFourPlayerBoard());
		boardPositions = AssetLoader.getInstance().getFourPlayerBoardCoordinates();
		
		float middleOfBoardX = board.getX(Align.center);
		float middleOfBoardY = board.getY(Align.center);
		
		renderGroup = new Group();
		
		turnIndicatorRenderer = new TurnIndicatorRenderer(middleOfBoardX, middleOfBoardY);
		renderGroup.addActor(turnIndicatorRenderer.getRenderGroup());
		
		renderGroup.addActor(board);
		
		Map<GameColor, List<ImmutablePoint2>> spawnPoints = AssetLoader.getInstance().getSpawnPointCoordinates();
		
		hangerRenderManagers = new HashMap<GameColor, HangerRenderManager>();
		for (GameColor color : GameColor.values()) {
			hangerRenderManagers.put(color, new HangerRenderManager(spawnPoints.get(color)));
			
			for (int i = 0; i < Game.NUMBER_OF_AIRPLANES_PER_PLAYER; ++i) {
				Image plane =  new Image(AssetLoader.getInstance().getAirplaneSprite(color));
				plane.setOrigin(Align.center);
				plane.setPosition(middleOfBoardX, middleOfBoardY, Align.center);
				
				AirplaneSprite sprite = new AirplaneSprite(plane);
				hangerRenderManagers.get(color).addPlane(sprite);
				renderGroup.addActor(sprite);
			}
		}
		
		positionLists = new EnumMap<GameColor, List<Set<AirplaneSprite>>>(GameColor.class);
		for (GameColor color : GameColor.values()) {
			List<Set<AirplaneSprite>> positionList = new ArrayList<Set<AirplaneSprite>>(boardPositions.size());
			for (int i = 0; i < boardPositions.size(); ++i) {
				positionList.add(new HashSet<AirplaneSprite>());
			}
			
			positionLists.put(color, positionList);
		}
		
		diceRenderer = new DiceRenderer(renderGroup, middleOfBoardX, middleOfBoardY);
	}
	
	public void spinTo(GameColor color) {
		turnIndicatorRenderer.spinTo(color);
	}
	
	public void startDiceRoll(int number, boolean clickToReveal) {
		diceRenderer.startDiceRoll(number);
	}
	
	public void endDiceRoll() {
		diceRenderer.endDiceRoll();
	}
	
	public void update(float deltaTime) {
		// Show dice roll first before animating planes to move
		diceRenderer.update(deltaTime);
	}
	
	public void playChoice(Choice choice) {
		if (choice == null) {
			throw new NullPointerException("Choice to render is null");
		}
		
		Choice currentChoice = choice;
		Deque<Choice> choiceStack = new ArrayDeque<Choice>();
		
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
			AirplaneSprite planeToLaunch = hangerRenderManagers.get(choice.getColor()).removePlaneFromRunway();
			
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
		for (List<Set<AirplaneSprite>> planeLists : positionLists.values()) {
			for (Set<AirplaneSprite> planes : planeLists) {
				for (AirplaneSprite plane : planes) {
					if (plane.hasActions()) {
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
		
		return diceRenderer.isDoneRendering();
	}
	
	public void reset() {
		for (GameColor color : GameColor.values()) {
			for (Set<AirplaneSprite> setOfPlanes : positionLists.get(color)) {
				for (AirplaneSprite airplane : setOfPlanes) {
					hangerRenderManagers.get(color).addPlaneFromBoard(airplane);
				}
				
				setOfPlanes.clear();
			}
		}
	}
	
	private void movePlanes(Iterable<AirplaneSprite> planes, Deque<Choice> choiceStack) {
		// Update the position lists
		GameColor color = choiceStack.peek().getColor();
		
		Set<AirplaneSprite> destinationSet = positionLists.get(color).get(choiceStack.peekLast().getDestination());
		for (AirplaneSprite plane : planes) {
			destinationSet.add(plane);
		}
		
		Set<AirplaneSprite> originSet = null;
		if (choiceStack.peek().getType() != ChoiceType.LAUNCH_PLANE_FROM_RUNWAY) {
			originSet = positionLists.get(color).get(choiceStack.peek().getOrigin());
		}
		
		List<ImmutablePoint2> destinations = new ArrayList<>();
		
		while(!choiceStack.isEmpty()) {
			Choice choice = choiceStack.pop();
			
			// Make the the destination list for the planes to travel to
			
			for (int routePath : choice.getRoute()) {
				destinations.add(boardPositions.get(routePath));
			}
			
			// Handle the planes to be taken down
			for (Integer position : choice.getTakedowns()) {
				for (GameColor planeColor : GameColor.values()) {
					if (planeColor != choice.getColor()) {
						Set<AirplaneSprite> planesToRemove = new HashSet<AirplaneSprite>(positionLists.get(planeColor).get(position));
						for (AirplaneSprite planeToRemove : planesToRemove) {
							positionLists.get(planeColor).get(position).remove(planeToRemove);
							hangerRenderManagers.get(planeColor).addPlaneFromBoard(planeToRemove);
						}
					}
				}
			}
		}
		
		// Make every plane move according to their destination list
		for (AirplaneSprite plane : planes) {
			plane.moveToPoints(destinations);
		}
		
		// Clear the origin set now that we worked with all the planes
		// However, if the origin set is the same as the destination set - like when planes are in home row
		// and they move back to their same spot via backtravel, then we shouldn't clear it
		if (originSet != null && originSet != destinationSet) {
			originSet.clear();
		}
	}
	
	private void movePlane(AirplaneSprite plane, Deque<Choice> choiceStack) {
		Set<AirplaneSprite> planes = new HashSet<>();
		planes.add(plane);
		
		movePlanes(planes, choiceStack);
	}
	
	public Group getRenderGroup() {
		return renderGroup;
	}

}
