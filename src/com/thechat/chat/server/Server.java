package com.thechat.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {

	private Map<Integer, ServerClient> clients = new ConcurrentHashMap<Integer, ServerClient>();
	private ArrayList<Integer> checks = new ArrayList<Integer>();
	private DatagramSocket socket;
	private int port;
	private boolean on = false, raw = false;
	private String userlist = "/u/";

	private Thread runThread, manageThread, sendThread, receiveThread;

	private final int MAX_ATTEMPTS = 5;
	private Scanner scanner;

	public Server(int serverPort) {
		port = serverPort;
		try {
			socket = new DatagramSocket(port); 
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		runThread = new Thread(this, "Server");
		runThread.start();
	}

	public void run() {
		on = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
		scanner = new Scanner(System.in);
		while (on) {
			String text = scanner.nextLine();
			if (!text.startsWith("/")) {
				if (!text.equals("raw"))
					sendToClients("/m/Server: " + text);
			}
			if (text.equals("raw")) {
				raw = !raw;
			}
		}
	}

	private void manageClients() {
		manageThread = new Thread("Manage") {
			public void run() {
				while (on) {
					sendToClients("/i/check");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (clients.size() != checks.size()) {
						java.util.Iterator<Entry<Integer, ServerClient>> it = clients.entrySet().iterator();
						while (it.hasNext()) {
							Entry<Integer, ServerClient> entry = it.next();
							ServerClient client = entry.getValue();
							if (!checks.contains(client.getID())) {
								if (client.getAttempts() >= MAX_ATTEMPTS) {
									disconnect(client.getID(), true);
								} else {
									client.incrementAttempts();
								}
							} else {
								client.attempt = 0;
							}
							checks.remove((Integer) client.getID());
						}
					}
				}
			}
		};
		manageThread.start();
	}

	private void receive() {
		receiveThread = new Thread("Receive") {
			public void run() {
				while (on) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}

			}
		};
		receiveThread.start();
	}

	private void process(DatagramPacket packet) {
		String packetString = new String(packet.getData());
		if (packetString.startsWith("/c/")) { 
			String name = packetString.substring(3, packetString.length()).trim();
			int id = new Random().nextInt(Integer.MAX_VALUE);
			clients.put(id, new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			String idString = "/c/" + id;
			send(idString.getBytes(), packet.getAddress(), packet.getPort());
			userlist += name + "\n";
			sendToClients(userlist);

		} else if (packetString.startsWith("/m/")) {
			sendToClients(packetString);
		} else if (packetString.startsWith("/d/")) {
			int idDisconnect = Integer.parseInt(packetString.substring(3, packetString.length()).trim());
			disconnect(idDisconnect, false);
		} else if (packetString.startsWith("/i/")) {
			checks.add(Integer.parseInt(packetString.substring(3, packetString.length()).trim()));
		}
	}

	private void sendToClients(String packetString) {
		if (raw)
			System.out.println(packetString);
		Iterator<Entry<Integer, ServerClient>> it = clients.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, ServerClient> entry = it.next();
			ServerClient client = entry.getValue();
			send(packetString.getBytes(), client.getAddress(), client.getPort());
		}
	}

	private void send(byte[] data, InetAddress addressSendTo, int portSendTo) {
		sendThread = new Thread() {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, addressSendTo, portSendTo);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}

	private void disconnect(int idDisconnect, boolean timeout) {
		String name = clients.get(idDisconnect).getUsername();
		clients.remove(idDisconnect);
		String message = "";
		if (timeout)
			message = name + " has timed out!";
		else
			message = name + " has disconnected!";
		sendToClients("/m/" + message);
		userlist = "/u/";
		Iterator<Entry<Integer, ServerClient>> it = clients.entrySet().iterator();
		while (it.hasNext()) {
			ServerClient client = it.next().getValue();
			userlist += client.getUsername() + "\n";
		}
		sendToClients(userlist);
	}
}
