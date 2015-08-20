package com.haanthony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.haanthony.Game.GameColor;

public class Choice {
	public enum ChoiceType {
		MOVE_PLANE_TO_RUNWAY,
		LAUNCH_PLANE_FROM_RUNWAY,
		FLY,
		SLIDE,
		JUMP;
	}
	
	private static final int DEFAULT_INVALID_POSITION = -1;
	
	private final ChoiceType type;
	private final Choice parentChoice;
	
	private final int destination; // Should not be available for MOVE_PLANE_TO_RUNWAY
	private final int origin; // Only available for FLY, SLIDE, and JUMP
	
	private final GameColor color; // Available to all
	
	private final Set<Integer> takedowns;
	private final List<Integer> route;
	
	public Choice(ChoiceType type, GameColor color) {
		this(type, color, DEFAULT_INVALID_POSITION, new TreeSet<Integer>(), new ArrayList<Integer>());
	}
	
	public Choice(ChoiceType type, GameColor color, int destination, Set<Integer> takedowns, List<Integer> route) {
		this(type, color, destination, takedowns, route, DEFAULT_INVALID_POSITION);
	}
	
	public Choice(ChoiceType type, GameColor color, int destination, Set<Integer> takedowns, List<Integer> route, int origin) {
		this(type, color, destination, takedowns, route, origin, null);
	}
	
	public Choice(ChoiceType type, GameColor color, int destination, Set<Integer> takedowns, List<Integer> route, int origin, Choice parentChoice) {
		if (destination != DEFAULT_INVALID_POSITION && type == ChoiceType.MOVE_PLANE_TO_RUNWAY) {
			throw new IllegalArgumentException("Type " + type + " can not have a destination");
		}
		
		if (origin != DEFAULT_INVALID_POSITION && (type == ChoiceType.MOVE_PLANE_TO_RUNWAY || type == ChoiceType.LAUNCH_PLANE_FROM_RUNWAY)) {
			throw new IllegalArgumentException("Type " + type + " can not have an origin" + origin);
		}
		
		this.type = type;
		this.destination = destination;
		
		if (takedowns != null) {
			this.takedowns = Collections.unmodifiableSet(takedowns);
		} else {
			this.takedowns = null;
		}
		
		if (route != null) {
			this.route = Collections.unmodifiableList(route);
		} else {
			this.route = null;
		}
		
		this.origin = origin;
		this.parentChoice = parentChoice;
		this.color = color;
	}
	
	public ChoiceType getType() {
		return type;
	}
	
	public int getDestination() {
		if (destination == DEFAULT_INVALID_POSITION) {
			throw new IllegalArgumentException("Destination not supported for type " + type);
		}
		return destination;
	}
	
	public Set<Integer> getTakedowns() {
		return takedowns;
	}
	
	public List<Integer> getRoute() {
		return route;
	}
	
	public int getOrigin() {
		if (origin == DEFAULT_INVALID_POSITION) {
			throw new IllegalArgumentException("Origin not supported for type " + type);
		}
		return origin;
	}
	
	public Choice getParentChoice() {
		return parentChoice;
	}
	
	public GameColor getColor() {
		return color;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + destination;
		result = prime * result + origin;
		result = prime * result + ((parentChoice == null) ? 0 : parentChoice.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((takedowns == null) ? 0 : takedowns.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Choice other = (Choice) obj;
		if (color != other.color)
			return false;
		if (destination != other.destination)
			return false;
		if (origin != other.origin)
			return false;
		if (parentChoice == null) {
			if (other.parentChoice != null)
				return false;
		} else if (!parentChoice.equals(other.parentChoice))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (takedowns == null) {
			if (other.takedowns != null)
				return false;
		} else if (!takedowns.equals(other.takedowns))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	// ----- The following code is used to convert the choice into a string and back
	private static final char HEADER = '!';
	private static final char SEPERATER = '@';
	private static final char NUMBER_DELIMITER = ',';
	private static final char PARENT_CHOICE_INDICATOR = '#';
	private static final char END_OF_CHOICE_CHAIN_INDICATOR = '$';
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(true, sb);
		return sb.toString();
	}
	
	private void toString(boolean includeHeader, StringBuilder sb) {
		if (includeHeader) {
			sb.append(HEADER);
			sb.append(color.toString());
			sb.append(SEPERATER);
		}
		
		sb.append(type.toString()).append(SEPERATER);
		sb.append(destination).append(SEPERATER);
		sb.append(origin).append(SEPERATER);
		
		if (takedowns != null && !takedowns.isEmpty()) {
			Iterator<Integer> iter = takedowns.iterator();
			sb.append(iter.next());
			while(iter.hasNext()) {
				sb.append(NUMBER_DELIMITER).append(iter.next());
			}
		}
		sb.append(SEPERATER);
		
		if (route != null && !route.isEmpty()) {
			Iterator<Integer> iter = route.iterator();
			sb.append(iter.next());
			while(iter.hasNext()) {
				sb.append(NUMBER_DELIMITER).append(iter.next());
			}
		}
		sb.append(SEPERATER);
		
		if (parentChoice != null) {
			sb.append(PARENT_CHOICE_INDICATOR).append(SEPERATER);
			parentChoice.toString(false, sb);
		} else {
			sb.append(END_OF_CHOICE_CHAIN_INDICATOR);
		}
	}
	
	public static Choice fromString(String choiceString) {
		// Process the header
		if (choiceString.charAt(0) != HEADER) {
			throw new IllegalArgumentException("Header not found");
		}
		
		int index = 1;
		int nextSeperater = choiceString.indexOf(SEPERATER, index);
		GameColor color = GameColor.valueOf(choiceString.substring(index, nextSeperater));
		
		index = nextSeperater + 1;
		
		return fromString(choiceString.substring(index), color);
	}
	
	private static Choice fromString(String choiceString, GameColor color) {
		if (choiceString == null || choiceString.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		int index = 0;
		
		ChoiceBuilder choiceBuilder = new ChoiceBuilder();
		choiceBuilder.color = color;
		
		// Process choice type
		int nextSeperater = choiceString.indexOf(SEPERATER, index);
		choiceBuilder.type = ChoiceType.valueOf(choiceString.substring(index, nextSeperater));
		index = nextSeperater + 1;
		
		// Process destination
		nextSeperater = choiceString.indexOf(SEPERATER, index);
		choiceBuilder.destination = Integer.valueOf(choiceString.substring(index, nextSeperater));
		index = nextSeperater + 1;
		
		// Process origin
		nextSeperater = choiceString.indexOf(SEPERATER, index);
		choiceBuilder.origin = Integer.valueOf(choiceString.substring(index, nextSeperater));
		index = nextSeperater + 1;
		
		// Process takedowns
		nextSeperater = choiceString.indexOf(SEPERATER, index);
		String takedownString = choiceString.substring(index, nextSeperater);
		Set<Integer> takedowns = new TreeSet<>();
		if (takedownString.length() > 0) {
			String[] numbers = takedownString.split(String.valueOf(NUMBER_DELIMITER));
			for (String number : numbers) {
				takedowns.add(Integer.valueOf(number));
			}
		}
		choiceBuilder.takedowns = takedowns;
		index = nextSeperater + 1;
		
		// Process route
		nextSeperater = choiceString.indexOf(SEPERATER, index);
		String routeString = choiceString.substring(index, nextSeperater);
		List<Integer> route = new ArrayList<>();
		if (routeString.length() > 0) {
			String[] numbers = routeString.split(String.valueOf(NUMBER_DELIMITER));
			for (String number : numbers) {
				route.add(Integer.valueOf(number));
			}
		}
		choiceBuilder.route = route;
		index = nextSeperater + 1;
		
		// Process parent choice if present
		if (choiceString.charAt(index) == PARENT_CHOICE_INDICATOR) {
			nextSeperater = choiceString.indexOf(SEPERATER, index);
			index = nextSeperater + 1;
			choiceBuilder.parentChoice = fromString(choiceString.substring(index), choiceBuilder.color);
			
			return choiceBuilder.build();
		} else if (choiceString.charAt(index) == END_OF_CHOICE_CHAIN_INDICATOR) {
			return choiceBuilder.build();
		} else {
			throw new IllegalArgumentException("Choice string does not terminate properly.");
		}
	}
	
	private static class ChoiceBuilder {
		private ChoiceType type;
		private Choice parentChoice;
		
		private int destination; // Should not be available for MOVE_PLANE_TO_RUNWAY
		private int origin; // Only available for FLY, SLIDE, and JUMP
		
		private GameColor color; // Available to all
		
		private Set<Integer> takedowns;
		private List<Integer> route;
		
		public Choice build() {
			return new Choice(type, color, destination, takedowns, route, origin, parentChoice);
		}
	}
}
