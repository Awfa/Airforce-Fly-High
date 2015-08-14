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
	
	private DiceRenderer diceRenderer;
	private Choice choiceToProcess;
	private boolean timeToProcess;
	private boolean diceRolled;
	private int diceRoll;
	
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
				plane.setOrigin(Align.center);
				plane.setPosition(1030.f/2, 1030.f/2);
				
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
		
		diceRenderer = new DiceRenderer(renderGroup, 1086.f/2, 1086.f/2);
		diceRolled = false;
		timeToProcess = false;
	}
	
	public void update(float deltaTime) {
		// Show dice roll first before animating planes to move
		diceRenderer.update(deltaTime);
		if (timeToProcess) {
			if (!diceRolled) {
				diceRenderer.startDiceRollToNumber(diceRoll);
				diceRolled = true;
			} else if (diceRenderer.isDoneRendering()) {
				processChoice();
				timeToProcess = false;
				diceRolled = false;
			}
		}
	}
	
	public void processChoice(GameInfo info) {
		choiceToProcess = info.getLastChoice();
		diceRoll = info.getLastDiceRoll();
		timeToProcess = true;
	}
	
	private void processChoice() {
		Deque<Choice> choiceStack = new ArrayDeque<Choice>();
		
		if (choiceToProcess == null) {
			throw new NullPointerException();
		}
		Choice currentChoice = choiceToProcess;
		choiceStack.push(currentChoice);
		while (currentChoice.getParentChoice() != null) {
			currentChoice = currentChoice.getParentChoice();
			choiceStack.push(currentChoice);
		}
		
		switch (currentChoice.getType()) {
		case MOVE_PLANE_TO_RUNWAY:
			hangerRenderManagers.get(choiceToProcess.getColor()).movePlaneToRunway();
			break;
			
		case LAUNCH_PLANE_FROM_RUNWAY:
			AirplaneSprite planeToLaunch = hangerRenderManagers.get(choiceToProcess.getColor()).removePlaneFromRunway();
			
			movePlane(planeToLaunch, choiceStack);
			break;
			
		case FLY:
			movePlanes(positionLists.get(choiceToProcess.getColor()).get(currentChoice.getOrigin()), choiceStack);
			break;
			
		default:
			break;
		}
		
		timeToProcess = false;
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
	
	// TODO: Fix bug that if airplane on homerow lands on the spot it current is on, it will no longer animate
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
			if (choice.getRoute().isEmpty()) {
				destinations.add(boardPositions.get(choice.getDestination()));
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
		if (originSet != null) {
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

	public void showDiceRoll(int diceRoll) {
		diceRenderer.startDiceRollToNumber(diceRoll);
	}
}
