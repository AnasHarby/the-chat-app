package com.thechat.chat.server;

import java.net.InetAddress;

public class ServerClient {
	
	public String username;
	public InetAddress address;
	public int port;
	private final int ID;
	public int attempt = 0;
	
	public ServerClient(String clientName, InetAddress clientAddress, int clientPort, final int clientID) {
		username = clientName;
		address = clientAddress;
		port = clientPort;
		ID = clientID;
	}
	
	public int getID() {
		return ID;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getAttempts() {
		return attempt;
	}
	
	public void incrementAttempts() {
		attempt++;
	}
}
