package com.socket.manager;

public interface SocketCallback {

	void onSocketReceiverMsg(byte[] msg);
	
}