package com.thechat.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

	private String username, address;
	private int port;
	private InetAddress ip;

	private DatagramSocket socket;
	private Thread sendThread, closeThread;
	
	private int ID = -1;

	public Client(String clientName, String clientAddress, int clientPort) {
		username = clientName;
		address = clientAddress;
		port = clientPort;
	}

	public boolean connect() {
		try {
			socket = new DatagramSocket(); 
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		if (message.startsWith("/c/")) {
			this.ID = Integer.parseInt(message.substring(3, message.length()).trim());
		}
		return message;
	}

	public void send(byte[] data) {
		sendThread = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}
	
	public void close() {
		closeThread = new Thread() {
			public void run() {
				synchronized (socket) { 
					socket.close();
				}
			}
		};
		closeThread.start();
	}

	public String getUsername() {
		return username;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int newID) {
		ID = newID;
	}
	
	public void setUsername(String name) {
		username = name;
	}
	

}
