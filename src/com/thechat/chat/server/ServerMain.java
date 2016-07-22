package com.thechat.chat.server;

public class ServerMain {
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Use: java -jar Chat.jar [port]");
		}
		int port = Integer.parseInt(args[0]);
		new Server(port); 
	}
} 