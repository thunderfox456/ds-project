package de.luh.vss.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat.Encoding;

import de.luh.vss.chat.common.Message;
import de.luh.vss.chat.common.Message.ChatMessage;
import de.luh.vss.chat.common.Message.ErrorResponse;
import de.luh.vss.chat.common.Message.RegisterRequest;
import de.luh.vss.chat.common.MessageType;
import de.luh.vss.chat.common.User;
import de.luh.vss.chat.common.User.UserId;

public class ChatClient {
	
	private static int max_lenght = 4000;

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
		String hostname = "130.75.202.197";
		int port = 4444;
		InetAddress inetAddress = InetAddress.getByName(hostname);
		//creating socket to connect to uni server
		Socket socket = new Socket(hostname, port);
		
		//get stream via messages to the server are sent
		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		//get stream via messages from the server are received
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
		//request to the server to register as client
		RegisterRequest registerRequest = new RegisterRequest(userId, inetAddress, port);
		registerRequest.toStream(dOut);
		
		//sending Message for Test 1 for user ID correctness
		//test1(userId, dOut);
		//sending Message for Test 2 for out of band message
		//test2(userId, dOut, dIn);
		//sending Message for Test 3 for exceeding max message length
		//test3(userId, dOut, dIn);
		//sending Message for Test 4 for handling error message
		//test4(userId, dOut, dIn);
		
		//test2_1(userId, dOut, dIn);
		
		//test2(userId, dOut, dIn, inetAddress, port);
		
		//test2_3(userId, dOut, dIn, inetAddress, port);
		
		test2_4(userId, dOut, dIn);
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
	
	public boolean test2(UserId userId, DataOutputStream dOut, DataInputStream dIn, InetAddress serverAddress, int serverPort) {
		System.out.println("Starting Test 2.");
		//sending Message for Test 2 for out of band message
		String messageString = "TEST 2 ECHO MESSAGE FROM USER";
		ChatMessage chatMessageTest2 = new ChatMessage(userId, messageString);
		try {
			chatMessageTest2.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Message response;
		try {
			String userHostname = "130.75.202.197";
			InetAddress userAddress = InetAddress.getByName(userHostname);
			int userPort = 5252;
			DatagramSocket utpSocket = new DatagramSocket();
			//catch all incoming messages
			while (true) {
				try {
					//send request
					byte[] sendBuffer = messageString.getBytes();
					DatagramPacket request = new DatagramPacket(sendBuffer, sendBuffer.length, userAddress, userPort);
	                utpSocket.send(request);
	                //receive response
	                byte[] receiveBuffer = new byte[512];
	                DatagramPacket utpResponse = new DatagramPacket(receiveBuffer, receiveBuffer.length);
	                utpSocket.receive(utpResponse);
	                //output 
	                String quote = new String(receiveBuffer, 0, utpResponse.getLength());
	                System.out.println(quote);
	                /*
					//parse messages incoming from the DataStream
					response = Message.parse(dIn);
					//print the message for debug reasons
					System.out.println(response);
					//ignore register responses, print for debug reasons
					if (response.getMessageType().equals(MessageType.REGISTER_RESPONSE))
						System.out.println(response);
					//chat messages should be echoed
					if (response.getMessageType().equals(MessageType.CHAT_MESSAGE))
						response.toStream(dOut);*/
				//unknown message type	
				} catch (IllegalStateException e) { 
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			System.out.println(e);
		}
		
		return true;
	}
	
	public boolean test3(UserId userId, DataOutputStream dOut, DataInputStream dIn) {
		System.out.println("Starting Test 3.");
		//sending Message for Test 3 for exceeding max message length
		ChatMessage chatMessageTest3 = new ChatMessage(userId, "TEST 3 EXCEEDING MAX MESSAGE LENGTH");
		
		try {
			chatMessageTest3.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Message response;
		try {
			//catch all incoming messages
			while (true) {
				try {
					//parse messages incoming from the DataStream
					response = Message.parse(dIn);
					
					//print the message for debug reasons
					System.out.println(response);
					//ignore register responses, print for debug reasons
					if (response.getMessageType().equals(MessageType.REGISTER_RESPONSE))
						System.out.println(response);
					//chat messages
					if (response.getMessageType().equals(MessageType.CHAT_MESSAGE)) {
						int messageLength = ((ChatMessage)(response)).getMessage().length();
						System.out.println(messageLength);
						//the size of the message in bytes, each char is 2 Bytes
						int byteSize = messageLength * 2;
						System.out.println(byteSize);
						//message exceeds max length
						if (byteSize > max_lenght) {
							//calculate how much bytes the message is too big
							int difference = byteSize - max_lenght;
							System.out.println(difference);
							//send difference back to server
							ChatMessage chatMessage = new ChatMessage(userId, String.valueOf(difference));
							chatMessage.toStream(dOut);
						}
						
					}
				//unknown message type	
				} catch (IllegalStateException e) { 
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			System.out.println(e);
		}
		
		return true;
	}
	
	public boolean test4(UserId userId, DataOutputStream dOut, DataInputStream dIn) {
		System.out.println("Starting Test 4.");
		//sending Message for Test 4 for handling error message
		ChatMessage chatMessageTest4 = new ChatMessage(userId, "TEST 4 HANDLING ERROR MESSAGE");
		
		try {
			chatMessageTest4.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Message response;
		try {
			//catch all incoming messages
			while (true) {
				try {
					//parse messages incoming from the DataStream
					response = Message.parse(dIn);
					
					//print the message for debug reasons
					System.out.println(response);
					//ignore register responses, print for debug reasons
					if (response.getMessageType().equals(MessageType.REGISTER_RESPONSE))
						System.out.println(response);
					//chat messages
					if (response.getMessageType().equals(MessageType.CHAT_MESSAGE)) {
						int messageLength = ((ChatMessage)(response)).getMessage().length();
						//the size of the message in bytes, each char is 2 Bytes
						int byteSize = messageLength * 2;
						//message exceeds max length
						if (byteSize > max_lenght) {
							//calculate how much bytes the message is too big
							int difference = byteSize - max_lenght;
							System.out.println(difference);
							//send difference back to server
							ChatMessage chatMessage = new ChatMessage(userId, String.valueOf(difference));
							chatMessage.toStream(dOut);
						}
						response.toStream(dOut);
					}
					if (response.getMessageType().equals(MessageType.ERROR_RESPONSE)) {
						String errorMessage = ((ErrorResponse)(response)).toString();
						System.out.println(errorMessage);
					}
				//unknown message type	
				} catch (IllegalStateException e) { 
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			System.out.println(e);
		}
		
		return true;
	}

	public boolean test2_1(UserId userId, DataOutputStream dOut, DataInputStream dIn) {
		System.out.println("Starting Test 1.");
		//sending Message for Test 1 for sending message while having an active lease
		ChatMessage chatMessageTest2_1 = new ChatMessage(userId, "TEST 1 SEND MESSAGE WHILE HAVING AN ACTIVE LEASE");
		//handle exception
		try {
			chatMessageTest2_1.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Test 1 sucessfull.");
		return true;
	}
	
	public boolean test2_2(UserId userId, DataOutputStream dOut, DataInputStream dIn) {
		System.out.println("Starting Test 2.");
		//sending Message for Test 1 for sending message while having an active lease
		ChatMessage chatMessageTest2_1 = new ChatMessage(userId, "TEST 2 ECHO MESSAGE FROM USER");
		//handle exception
		try {
			chatMessageTest2_1.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Message response;
		try {
			//catch all incoming messages
			while (true) {
				try {
					//parse messages incoming from the DataStream
					response = Message.parse(dIn);
					//print the message for debug reasons
					System.out.println(response);
					//ignore register responses, print for debug reasons
					if (response.getMessageType().equals(MessageType.REGISTER_RESPONSE))
						System.out.println(response);
					//chat messages should be echoed
					if (response.getMessageType().equals(MessageType.CHAT_MESSAGE))
						response.toStream(dOut);
				//unknown message type	
				} catch (IllegalStateException e) { 
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			System.out.println(e);
			return false;
		}
		
		//return true;
	}
	
	public boolean test2_3(UserId userId, DataOutputStream dOut, DataInputStream dIn, InetAddress serverAddress, int serverPort) {
		System.out.println("Starting Test 3.");
		//sending Message for Test 2 for out of band message
		String messageString = "TEST 3 RENEW LEASE";
		ChatMessage chatMessageTest2 = new ChatMessage(userId, messageString);
		try {
			chatMessageTest2.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		int waitTime = 120; // time in seconds
		//Message response;
		try {
			//String userHostname = "130.75.202.197";
			//InetAddress userAddress = InetAddress.getByName(userHostname);
			//int userPort = 5252;
			//DatagramSocket utpSocket = new DatagramSocket();
			//catch all incoming messages
			while (true) {
				try {
					try {
						for (int i = 0; i < waitTime + 1; i++) {
							TimeUnit.SECONDS.sleep(1);
							System.out.println("Waiting since: " + i + " Seconds");
						}
						System.out.println("Renewing lease.");
						//request to the server to register as client
						RegisterRequest registerRequest = new RegisterRequest(userId, serverAddress, serverPort);
						registerRequest.toStream(dOut);
					}
					catch (Exception e) {
						System.out.println(e);
						return false;
					}
					
				//unknown message type	
				} catch (IllegalStateException e) { 
					System.out.println(e);
				}
			}
		} catch (IllegalStateException e) {
			System.out.println(e);
		}
		
		return true;
	}
	
	public boolean test2_4(UserId userId, DataOutputStream dOut, DataInputStream dIn) {
		System.out.println("Starting Test 2.");
		//sending Message for Test 1 for sending message while having an active lease
		ChatMessage chatMessageTest2_4 = new ChatMessage(userId, "TEST 4 LISTEN TO INCOMING MESSAGES AND ECHO SPECIAL MESSAGE");
		//handle exception
		try {
			chatMessageTest2_4.toStream(dOut);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Message response;
		try {
			//catch all incoming messages
			while (true) {
				try {
					//parse messages incoming from the DataStream
					response = Message.parse(dIn);
					//print the message for debug reasons
					System.out.println(response);
					//ignore register responses, print for debug reasons
					//if (response.getMessageType().equals(MessageType.REGISTER_RESPONSE))
					//	System.out.println(response);
					//chat messages should be echoed
					if (response.getMessageType().equals(MessageType.CHAT_MESSAGE)) {
						String responseMessage = ((ChatMessage)response).getMessage();
						System.out.println(responseMessage);
						if (responseMessage.contains("SPECIAL MESSAGE TEST 4")) {
							System.out.println("Special message received");
							chatMessageTest2_4 = new ChatMessage(userId, responseMessage);
							chatMessageTest2_4.toStream(dOut);
						}
						
					}
				//unknown message type	
				} catch (IllegalStateException e) { 
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			System.out.println(e);
			return false;
		}
		
		//return true;
	}
}
