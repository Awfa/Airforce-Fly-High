package com.haanthony;

import java.util.Collections;
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
	
	public Choice(ChoiceType type, GameColor color) {
		this(type, color, DEFAULT_INVALID_POSITION, null);
	}
	
	public Choice(ChoiceType type, GameColor color, int destination, Set<Integer> takedowns) {
		this(type, color, destination, takedowns, DEFAULT_INVALID_POSITION);
	}
	
	public Choice(ChoiceType type, GameColor color, int destination, Set<Integer> takedowns, int origin) {
		this(type, color, destination, takedowns, origin, null);
	}
	
	public Choice(ChoiceType type, GameColor color, int destination, Set<Integer> takedowns, int origin, Choice parentChoice) {
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
}
