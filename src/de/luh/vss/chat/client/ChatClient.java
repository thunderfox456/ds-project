package de.luh.vss.chat.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.User;
import de.luh.vss.chat.common.User.UserId;

public class ChatClient {

	public static void main(String... args) {
		try {
			new ChatClient().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException {
		System.out.println("Congratulation for successfully setting up your environment for Assignment 1!");
		
		// implement your chat client logic here
		UserId userId = new UserId(9277);
		//SocketAddress socketAddress = new SocketAddress();
		User user = new User(userId, null);
		ChatMessage chatMessage = new ChatMessage(userId, "test");
		InetAddress inetAddress = InetAddress.getByName("130.75.202.197");
		RegisterRequest registerRequest = new RegisterRequest(userId, inetAddress, 4444);
		Socket socket = new Socket("localhost", 8081);
		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		registerRequest.toStream(dOut);
	}

}
