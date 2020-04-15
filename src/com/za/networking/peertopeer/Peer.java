package com.za.networking.peertopeer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

import javax.json.Json;

public class Peer {

	public static void main(String[] args) throws Exception  {
		// TODO Auto-generated method stub
 BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
 System.out.println("Enter your username & port # for this peer");
 String[] setupValues = bufferedreader.readLine().split(" "); 
 ServerThread serverThread = new ServerThread(setupValues[1]);
 serverThread.start();
 new Peer().updateListenToPeers(bufferedreader, setupValues[0], serverThread);
	}
	public void updateListenToPeers (BufferedReader bufferedReader, String username,ServerThread serverThread ) throws Exception {
		System.out.println("> enter (space separated) hostname:port#");
		System.out.println(" peer to receive messages from (s to skip)");
		String input = bufferedReader.readLine();
		String[] inputValues = input.split(" ");
		if (!input.equals("s")) 
			for (int i = 0; i < inputValues.length; i++) {
				String[] address = inputValues[i].split(":");
				Socket socket = null;
				try {
				socket = new Socket(address[0],Integer.valueOf(address[1]));
				new PeerThread(socket).start();
				}catch(Exception e){
					if(socket != null ) {
						socket.close();
					}
					else System.out.println("invalid value, skipping to next step");
					
				}
 				
			}
		communiate(bufferedReader, username, serverThread);
	}
	public void communiate(BufferedReader bufferedReader, String username,ServerThread serverThread) {
		try {
			System.out.println("> You can now communicate(e to exit, c to change)");
			boolean flag = true;
			while (flag ) {
				String message = bufferedReader.readLine();
				if(message.equals("e")){
				flag = false;
				break;
				}else if (message.equals("c")) {
					updateListenToPeers(bufferedReader, username, serverThread);
				}else {
					StringWriter stringWritter = new StringWriter();
					Json.createWriter(stringWritter).writeObject(Json.createObjectBuilder().add("username", username).add("message", message).build()); 
					serverThread.sendMessage(stringWritter.toString());
				}
					
				}
			System.exit(0);
		}catch(Exception e) {}
	}
}
