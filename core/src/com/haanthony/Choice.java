package com.haanthony;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.haanthony.Game.GameColor;

public class Choice {
	public enum ChoiceType {
		MOVE_PLANE_TO_RUNWAY		("Move a plane from the hanger to the runway"),
		LAUNCH_PLANE_FROM_RUNWAY	("Launch the plane from the runway to "),
		FLY							("Fly the plane to "),
		SLIDE						(" and slide the plane to "),
		JUMP						(" and jump the plane to ");
		
		private final String description;
		
		ChoiceType(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	private static final int DEFAULT_INVALID_POSITION = -1;
	
	private final ChoiceType type;
	private final Choice parentChoice;
	
	private final int destination; // Should not be available for MOVE_PLANE_TO_RUNWAY
	private final int origin; // Only available for FLY, SLIDE, and JUMP
	
	private final GameColor color;
	
	private final Set<Integer> takedowns;
	private final List<Integer> route;
	
	public Choice(ChoiceType type, GameColor color) {
		this(type, color, DEFAULT_INVALID_POSITION, null, null);
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (parentChoice != null) {
			sb.append(parentChoice.toString());
		}
		
		sb.append(type.getDescription());
		
		if (destination != DEFAULT_INVALID_POSITION) {
			sb.append(destination);
		}
		
		if (origin != DEFAULT_INVALID_POSITION) {
			sb.append(" from ").append(origin);
		}
		
		return sb.toString();
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
	
	
}
