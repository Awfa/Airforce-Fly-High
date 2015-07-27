package com.haanthony;

public class ImmutablePoint2 {
	public final int x;
	public final int y;
	
	public ImmutablePoint2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
