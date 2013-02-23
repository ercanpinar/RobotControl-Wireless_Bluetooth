package com.socket.task;

import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.socket.manager.SocketClient;
import com.socket.notice.Notice;

@SuppressLint("UseValueOf")
public class SocketClientTask extends AsyncTask<String, Void, Socket>{

	private Handler handler;
	private Message message;
	
	private Activity activity;
	private ProgressDialog progressDialog;
	private Notice notice;
	
	private SocketClient client;
	
	public SocketClientTask(Activity activity, Handler handler, int what){
		this.activity = activity;
		this.handler = handler;
		
		message = new Message();
		message.what = what;
		
		client = new SocketClient();
		notice = new Notice(activity);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(activity, "Lütfen Bekleyiniz", "Ba€lan›l›yor...");
	}
	
	@Override
	protected Socket doInBackground(String... arg) {
		return client.startClient(arg[0], new Integer(arg[1]));
	}
	
	@Override
	protected void onPostExecute(Socket result) {
		super.onPostExecute(result);
		
		closeDialog();
		
		if(result == null){
			notice.showToast("Ba€lant› baﬂar›s›z");
		}else{
			notice.showToast("Baﬂar›yla ba€land›");
			message.obj = result;
			handler.dispatchMessage(message);
		}
	}
	
	private void closeDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}	