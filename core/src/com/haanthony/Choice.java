package com.haanthony;

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
	
	public Choice(ChoiceType type) {
		this(type, DEFAULT_INVALID_POSITION);
	}
	
	public Choice(ChoiceType type, int destination) {
		this(type, destination, DEFAULT_INVALID_POSITION);
	}
	
	public Choice(ChoiceType type, int destination, int origin) {
		this(type, destination, origin, null);
	}
	
	public Choice(ChoiceType type, int destination, int origin, Choice parentChoice) {
		if (destination != DEFAULT_INVALID_POSITION && type == ChoiceType.MOVE_PLANE_TO_RUNWAY) {
			throw new IllegalArgumentException("Type " + type + " can not have a destination");
		}
		
		if (origin != DEFAULT_INVALID_POSITION && (type == ChoiceType.MOVE_PLANE_TO_RUNWAY || type == ChoiceType.LAUNCH_PLANE_FROM_RUNWAY)) {
			throw new IllegalArgumentException("Type " + type + " can not have an origin" + origin);
		}
		
		this.type = type;
		this.destination = destination;
		this.origin = origin;
		this.parentChoice = parentChoice;
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
	
	public int getOrigin() {
		if (origin == DEFAULT_INVALID_POSITION) {
			throw new IllegalArgumentException("Origin not supported for type " + type);
		}
		return origin;
	}
	
	public Choice getParentChoice() {
		return parentChoice;
	}
}
