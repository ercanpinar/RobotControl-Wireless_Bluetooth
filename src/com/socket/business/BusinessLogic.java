package com.socket.business;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import net.ercsoft.robotcontrolwrls_blueth.R;

import com.socket.manager.SocketCommunication;
import com.socket.notice.Notice;
import com.socket.task.SocketClientTask;
import com.socket.task.SocketServerTask;

@SuppressLint("HandlerLeak")
public class BusinessLogic implements OnClickListener{
	
	private EditText edMsg;
	private Button btnSend;
	private Button btnService;
	private Button btnClient;
	private ListView lstHistoric;
	
	private Activity context;
	private Notice notice;
	private ArrayAdapter<String> historic;
	
	private SocketCommunication communication;
	
	public BusinessLogic(Activity activity){
		this.context = activity;
		
		notice = new Notice(activity);
		edMsg = (EditText) activity.findViewById(R.id.edtMsg);
        btnSend = (Button) activity.findViewById(R.id.btnSend);
        btnClient = (Button) activity.findViewById(R.id.btnClient);
        btnService = (Button) activity.findViewById(R.id.btnService);
        lstHistoric = (ListView) activity.findViewById(R.id.lstHistoric);
	}
	
	public void configView(){
		historic = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
		lstHistoric.setAdapter(historic);
		
		edMsg.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		btnClient.setOnClickListener(this);
		btnService.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
	        case R.id.btnSend:
	        		if(isNetworkConnected()){
	        			sendMsg();
	        		}
	                break;
	        case R.id.btnService:
		        	if(isNetworkConnected()){
		        		popupServer();
		        	}
	                break;
	        case R.id.btnClient:
		        	if(isNetworkConnected()){
		        		popupClient();
		        	}
	                break;
	                /*
	        case R.id.micrphone:
	        	if(isNetworkConnected()){

	        		sendSubstanceName("test");
	        	}
                break;
                */
		}
	}

  
	private void sendMsg(){
		if(communication != null){
			String msg = edMsg.getText().toString(); 
			
			if(msg.trim().length() > 0){
				edMsg.setText(""); 
				
				communication.sendMsg(msg);
				
				historic.add("Ben: " + msg); 
				historic.notifyDataSetChanged();							
			}else{
				notice.showToast("Bir mesaj yaz›n›z");
			}
		}else{
			notice.showToast("Baﬂka cihaz ile ba€lant› yok");
		}
	}
	public void sendSubstanceName(String subName){
		if(communication != null){
			
			if(subName.trim().length() > 0){				
				communication.sendMsg(subName);
				notice.showToast(subName+"   istendi");

				historic.add("Ben.Getir: " + subName); 
				historic.notifyDataSetChanged();							
			}else{
				notice.showToast("Cisim anlaﬂ›lmad›");
			}
		}else{
			notice.showToast("Baﬂka cihaz ile ba€lant› yok");
		}
	}
	private void popupServer(){
		AlertDialog.Builder alertConfig = new AlertDialog.Builder(context);
        alertConfig.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
        alertConfig.setTitle("Server");
        
        final EditText editText = new EditText(context);
        editText.setHint("Port");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertConfig.setView(editText);	

        alertConfig.setPositiveButton("Kabulet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	closeCommunication();
            	if(editText.getText().toString().length() > 0){
            		SocketServerTask serverTask = new SocketServerTask(context, handler, 1);
            		serverTask.execute(new String[]{editText.getText().toString()});
            	}else{
            		notice.showToast("Hiçbir de€er girilmedi");
            	}
            }
        });
            
        alertConfig.setNeutralButton("Vazgeç", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        
        alertConfig.create().show();
	}
	
	@SuppressLint("HandlerLeak")
	private void popupClient(){
		AlertDialog.Builder alertConfig = new AlertDialog.Builder(context);
        alertConfig.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
        alertConfig.setTitle("Client");
        
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.wireless_popup_client, null);
        
        final EditText editText = (EditText)convertView.findViewById(R.id.editText1);
        final EditText editText2 = (EditText)convertView.findViewById(R.id.editText2);
        
        alertConfig.setView(convertView);	
        
        alertConfig.setPositiveButton("Kabulet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	closeCommunication();
            	if(editText.getText().toString().length() > 0 && editText2.getText().toString().length() > 0){
            		SocketClientTask clientTask = new SocketClientTask(context, handler, 1);
            		clientTask.execute(new String[]{editText.getText().toString(), editText2.getText().toString()});
            	}else{
            		notice.showToast("Tüm alanlar› doldurun");
            	}
            }
        });
            
        alertConfig.setNeutralButton("Vazgeç", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        
        alertConfig.create().show();
	}
	
	public void closeCommunication(){
		if(communication != null){
			communication.stopComunication();
		}
	}
	
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null) {
			notice.showToast("Ba€lant› yok");
			return false;
		} else{
			return true;
		}
	}
	
	private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            synchronized (msg) {
                switch (msg.what) {
                	case 1:
                		Socket socket = (Socket)(msg.obj);
                		communication = new SocketCommunication(socket, handler, 3, 2);
            			communication.start();
                		break;
            			
                	case 2:
                		String message = (String)(msg.obj);
                		
                		notice.showToast(message);
                		break;
                	
                	case 3:
                		String messageBT = (String)(msg.obj);
                		
                		historic.add(messageBT);
       				 	historic.notifyDataSetChanged();
       				 	break;
                }
            }
        };
    };
	
}