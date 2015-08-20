package com.haanthony;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.badlogic.gdx.net.Socket;
import com.haanthony.RemoteGameManager.RemoteGameManagerMessage;

public class RemotePlayer implements Player {
	public enum RemotePlayerMessage {
		UPDATE_GAME_INFO,
		REVEAL_DICE,
		PLAY_CHOICE,
		END_GAME,
		RESET;
	}

	private Socket socket;
	private Manager manager;
	
	private BufferedReader in;
	private BufferedWriter out;
	
	public RemotePlayer(Socket socket) {
		if (socket == null) {
			throw new NullPointerException();
		}
		
		if (!socket.isConnected()) {
			throw new IllegalArgumentException("Socket is not connected");
		}
		
		this.socket = socket;
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	@Override
	public void setGameManager(Manager manager) {
		this.manager = manager;
	}

	@Override
	public void updateGameInfo(GameInfo info, boolean isCurrentPlayer) {
		try {
			out.write(RemotePlayerMessage.UPDATE_GAME_INFO + "\n" + info.toString() + "\n" + isCurrentPlayer + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to update game info for remote player");
		}
	}

	@Override
	public void revealDice() {
		try {
			out.write(RemotePlayerMessage.REVEAL_DICE + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to reveal dice for remote player");
		}
	}

	@Override
	public void playChoice(Choice choice) {
		try {
			out.write(RemotePlayerMessage.PLAY_CHOICE + "\n" + choice.toString() + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to play choice for remote player");
		}
	}

	@Override
	public void endGame(EndGameInfo info) {
		try {
			out.write(RemotePlayerMessage.END_GAME + "\n" + info.toString() + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to end game for remote player");
		}
	}

	@Override
	public void reset() {
		try {
			out.write(RemotePlayerMessage.RESET + "\n");
		} catch (IOException e) {
			System.err.println("An IOException has occured when trying to reset game for remote player");
		}
	}
	
	public void update() {
		try {
			while (in.ready()) {
				String line = in.readLine();
				RemoteGameManagerMessage message = RemoteGameManagerMessage.valueOf(line);
				
				switch (message) {
				case CHOICE:
					Choice choice = Choice.fromString(in.readLine());
					manager.playChoice(choice);
					break;
				case READY:
					manager.readyPlayer(this);
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
