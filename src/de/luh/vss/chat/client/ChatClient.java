package de.luh.vss.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

import de.luh.vss.chat.common.Message;
import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.MessageType;
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
		//System.out.println("Congratulation for successfully setting up your environment for Assignment 1!");
		//my ErgebnisPin is 9277
		UserId userId = new UserId(9277);
		User user = new User(userId, null); //unused
		
		InetAddress inetAddress = InetAddress.getByName("130.75.202.197");
		//creating socket to connect to uni server
		Socket socket = new Socket("130.75.202.197", 4444);
		//get stream via messages to the server are sent
		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		//get stream via messages from the server are received
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
		//request to the server to register as client
		RegisterRequest registerRequest = new RegisterRequest(userId, inetAddress, 4444);
		registerRequest.toStream(dOut);
		/*
		Message response;
		try {
			response = Message.parse(dIn);
			System.out.println(response);
		} catch (IOException | ReflectiveOperationException e) {
			e.printStackTrace();
		}*/
		
		//sending Message for Test 1 for user ID correctness
		//test1(userId, dOut);
		//sending Message for Test 2 for out of band message
		test2(userId, dOut, dIn);
		
	}
	
	public boolean test1(UserId userId, DataOutputStream dOut) {
		System.out.println("Starting Test 1.");
		//sending Message for Test 1 for user ID correctness
		ChatMessage chatMessageTest1 = new ChatMessage(userId, "TEST 1 USER ID CORRECTNESS");
		//handle exception
		try {
			chatMessageTest1.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Test 1 sucessfull.");
		return true;
	}
	
	public boolean test2(UserId userId, DataOutputStream dOut, DataInputStream dIn) {
		System.out.println("Starting Test 2.");
		//sending Message for Test 2 for out of band message
		ChatMessage chatMessageTest2 = new ChatMessage(userId, "TEST 2 OUT OF BAND PROTOCOL MESSAGE");
		try {
			chatMessageTest2.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		Message response;
		//dIn.read(null)
		/*
		try {
			response = Message.parse(dIn);
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}*/
		/*
		try {
			response = Message.parse(dIn);
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			System.out.println(e);
		}*/
		
		try {
			while (true) {
				try {
				System.out.println("Test");
				response = Message.parse(dIn);
				System.out.println(response);
				if (response.getMessageType().equals(MessageType.REGISTER_RESPONSE))
					System.out.println(response);
				if (response.getMessageType().equals(MessageType.CHAT_MESSAGE))
					response.toStream(dOut);
				} catch (IllegalStateException e) {
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			System.out.println(e);
		}
		
		return true;
	}

}
