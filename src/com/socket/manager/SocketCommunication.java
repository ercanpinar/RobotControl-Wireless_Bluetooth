package com.socket.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;

import android.os.Handler;

public class SocketCommunication extends Thread {
	
	private int whatMsgSocket;
	private int whatMsgNotice;

	private Socket socket;
	private Handler handler;

	private DataInputStream dataInputStream = null;
	private DataOutputStream dataOutputStream = null;
	
	public SocketCommunication(Socket socket, Handler handler, int whatMsgSocket, int whatMsgNotice){
		this.socket = socket;
		this.handler = handler;
		this.whatMsgSocket = whatMsgSocket;
		this.whatMsgNotice = whatMsgNotice;
	}
	
	@Override
	public void run() {
		 super.run();
		
		 try {
			 dataInputStream = new DataInputStream(socket.getInputStream());
			 dataOutputStream = new DataOutputStream(socket.getOutputStream());
			
			 while (true) {
				 if(dataInputStream.available() > 0){
					 byte[] msg = new byte[dataInputStream.available()];
					 dataInputStream.read(msg, 0, dataInputStream.available());
					 
					 sendHandler(whatMsgSocket, "Sen" + ": " + new String(msg));
				 }
			 }
		 } catch (IOException e) {
			 e.printStackTrace(); 
			 
			 dataInputStream = null;
			 dataOutputStream = null;
			 
			 sendHandler(whatMsgNotice, "Ba€lant› koptu");
		 }
	}
	
	public void sendHandler(int what, Object object){
		handler.obtainMessage(what, object).sendToTarget();
	}
	
	public void sendMsg(String msg){
		try {
			if(dataOutputStream != null){
				dataOutputStream.write(msg.getBytes());
				dataOutputStream.flush();
			}else{
				sendHandler(whatMsgNotice, "Ba€lant› yok");
			}
			
		} catch (IOException e) {
			e.printStackTrace(); 
			 
			sendHandler(whatMsgNotice, "Mesaj gönderilemedi");
		}
	}
	
	 public void stopComunication(){ 
		try {
			socket.close();
			
			if(dataInputStream != null && dataOutputStream != null){
				dataInputStream.close();
				dataOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }

 }