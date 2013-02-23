package com.socket.manager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient { 
	
	private Socket socket;
	
	public Socket startClient(String host, int port){
		
		try {
			socket = new Socket(host, port);
			
			return socket;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	 
}  