package com.bluetooth.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;

public class BluetoothComunication extends Thread {
	 
	private int whatMsgBT;
	private int whatMsgNotice;
	private int whatMsgCsm; //int 4 geliyor
	private int whatMsgFar;  //int 5 geliyors


	private Handler handler;
	
	private BluetoothSocket socket;
	private DataInputStream dataInputStream = null;
	private DataOutputStream dataOutputStream = null;
	
	public BluetoothComunication(Handler handler, int whatMsgBT, int whatMsgNotice , int whatMsgCsm,int whatMsgFar){
		this.handler = handler;
		this.whatMsgBT = whatMsgBT;
		this.whatMsgNotice = whatMsgNotice;
		this.whatMsgCsm=whatMsgCsm;
		this.whatMsgFar=whatMsgFar;
	}
	
	public void openComunication(BluetoothSocket socket){
		this.socket = socket;
		start();
	}
	 
	@Override
	public void run() {
		 super.run();
		
		 String nameBluetooth;
		
		 try {
			 nameBluetooth = socket.getRemoteDevice().getName();
			 dataInputStream = new DataInputStream(socket.getInputStream());
			 dataOutputStream = new DataOutputStream(socket.getOutputStream());
			
			 sendHandler(whatMsgNotice, "Baﬂar›yla ba€land›");
			 
			 while (true) {
				 if(dataInputStream.available() > 0){
					 byte[] msg = new byte[dataInputStream.available()];
					 dataInputStream.read(msg, 0, dataInputStream.available());
					 
					 sendHandler(whatMsgBT, nameBluetooth + ": " + new String(msg));
					 
					 
					
					 
					 if(new String(msg)=="ac"){
						 sendHandler(whatMsgFar, "ac");
					 }else if(new String(msg)=="kapat"){
						 sendHandler(whatMsgFar, "kapat");

						 
					 }else if(new String(msg)=="silgi"){
						istenilenNesneOku(new String(msg));
						
						 
					 }
					 
					 
				 }
			 }
		 } catch (IOException e) {
			 e.printStackTrace(); 
			 
			 dataInputStream = null;
			 dataOutputStream = null;
			 
			 sendHandler(whatMsgNotice, "Ba€lant› bulunamad›");
		 }
	}
	
	private void istenilenNesneOku(String str) {

		 sendHandler(whatMsgCsm, str);

		
		
	}



	public void sendMessageByBluetooth(String msg){
		try {
			if(dataOutputStream != null){
				dataOutputStream.write(msg.getBytes());
			}else{
				sendHandler(whatMsgNotice, "Ba€lant› yok");
			}
			
		} catch (IOException e) {
			e.printStackTrace(); 
			 
			sendHandler(whatMsgNotice, "Mesaj gönderilemedi");
		}
	}
	
	public void sendHandler(int what, Object object){
		handler.obtainMessage(what, object).sendToTarget();
	}
           
	 public void stopComunication(){ 
		try {
			if(dataInputStream != null && dataOutputStream != null){
				dataInputStream.close();
				dataOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
 }