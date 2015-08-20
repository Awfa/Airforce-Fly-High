package com.haanthony;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.badlogic.gdx.net.Socket;
import com.haanthony.RemotePlayer.RemotePlayerMessage;

public class RemoteGameManager implements Manager {
	public enum RemoteGameManagerMessage {
		CHOICE,
		READY;
	}
	
	private Player player;
	private Socket socket;
	
	private BufferedReader in;
	private BufferedWriter out;
	
	public RemoteGameManager(Player player, Socket socket) {
		if (player == null || socket == null) {
			throw new NullPointerException();
		}
		
		if (!socket.isConnected()) {
			throw new IllegalArgumentException("Socket is not connected");
		}
		
		this.player = player;
		this.socket = socket;
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void playChoice(Choice choice) {
		try {
			out.write(RemoteGameManagerMessage.CHOICE + "\n" + choice.toString() + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to play the given choice");
		}
	}

	@Override
	public void readyPlayer(Player player) {
		try {
			out.write(RemoteGameManagerMessage.READY + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to ready the player");
		}
	}
	
	public void update() {
		try {
			while (in.ready()) {
				String line = in.readLine();
				RemotePlayerMessage message = RemotePlayerMessage.valueOf(line);
				
				switch (message) {
				case UPDATE_GAME_INFO:
					GameInfo gameInfo = GameInfo.fromString(in.readLine());
					boolean isCurrentPlayer = Boolean.parseBoolean(in.readLine());
					
					player.updateGameInfo(gameInfo, isCurrentPlayer);
					break;
					
				case REVEAL_DICE:
					player.revealDice();
					break;
					
				case PLAY_CHOICE:
					Choice choice = Choice.fromString(in.readLine());
					player.playChoice(choice);
					break;
					
				case END_GAME:
					EndGameInfo endGameInfo = EndGameInfo.fromString(in.readLine());
					player.endGame(endGameInfo);
					break;
					
				case RESET:
					player.reset();
					break;
				}
			}
		} catch (IOException e) {
			System.err.println("An IOException has occured while trying to read the input");
			System.err.println(e.getStackTrace());
		}
	}
	
	public void close() {
		socket.dispose();
		try {
			in.close();
		} catch (IOException e) {
			System.err.println("An IO Exception has occured when closing the socket reader");
		}
		
		try {
			out.close();
		} catch (IOException e) {
			System.err.println("An IO Exception has occured when closing the socket writer");
		}
	}
}
