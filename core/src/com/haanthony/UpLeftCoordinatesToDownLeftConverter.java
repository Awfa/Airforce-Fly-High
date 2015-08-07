package com.haanthony;

import java.util.Scanner;

public class UpLeftCoordinatesToDownLeftConverter {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int numberOfCoords = in.nextInt();
		for (int i = 0; i < numberOfCoords; ++i) {
			System.out.println(in.nextInt() + " " + (1086 - in.nextInt()));
		}
		in.close();
	}
}
