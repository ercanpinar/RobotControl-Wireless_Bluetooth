package com.bluetooth.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ercsoft.robotcontrolwrls_blueth.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bluetooth.manager.BluetoothComunication;
import com.bluetooth.notice.Notice;
import com.bluetooth.task.BluetoothClientTask;
import com.bluetooth.task.BluetoothServiceTask;
import com.google.inject.Inject;

@SuppressLint("HandlerLeak")
@ContentView(R.layout.bluetooth_main)
public class MainActivityBluetooth extends RoboActivity{

	private final int BT_ACTIVATE = 0;
	private final int BT_VISIBLE = 1;

	public static int BT_TIMER_VISIBLE = 30; 

	@InjectView(R.id.edtMsg)
	private EditText edMsg;

	@InjectView(R.id.btnSend)
	private Button btnSend;

	@InjectView(R.id.btnService)
	private Button btnService;

	@InjectView(R.id.btnClient)
	private Button btnClient;

	@InjectView(R.id.lstHistoric)
	private ListView lstHistoric;

	@InjectView(R.id.sagblt)
	private Button btnsagBlt;

	@InjectView(R.id.solblt)
	private Button btnsolBlt;

	@InjectView(R.id.ileriblt)
	private Button btnileriBlt;
	
	@InjectView(R.id.geriblt)
	private Button btngeriBlt;
	
	@InjectView(R.id.durblt)
	private Button btndurBlt;
	
	@Inject
	private Notice notice;

	private BluetoothSocket socket;
	private BluetoothAdapter adaptador;
	private BluetoothComunication bluetoothComunication;
	private BluetoothClientTask bluetoothClientTask;
	private BluetoothServiceTask bluetoothServiceTask;

	private ArrayAdapter<String> historic;
	private List<BluetoothDevice> devicesFound; 
	Button speakBtn;
	private ProgressDialog progressDialog;	
	private EventsBluetoothReceiver eventsBTReceiver; 
	private static final int REQUEST_CODE = 1234;
	String cisim="can";
	ArrayList<String>cisimler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		devicesFound = new ArrayList<BluetoothDevice>(); 

		configView();
		inicializaBluetooth();
		registerFilters();

		

	}

	//microphone code
	private void startVoiceRecognitionActivityBlt()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hangi cismi istiyorsunuz ?");
		startActivityForResult(intent, REQUEST_CODE);
	}

	public void configView(){
		historic = new ArrayAdapter<String>(this,	android.R.layout.simple_list_item_1);
		lstHistoric.setAdapter(historic);

		btnSend.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(bluetoothComunication != null){
					String msg = edMsg.getText().toString(); 

					if(msg.trim().length() > 0){
						edMsg.setText(""); 

						bluetoothComunication.sendMessageByBluetooth(msg);

						historic.add("Ben: " + msg); 
						historic.notifyDataSetChanged();							
					}else{
						notice.showToast("Bir mesaj yaz›n›z");
					}
				}else{
					notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
				}
			}
		});

		btnService.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_TIMER_VISIBLE); 
				startActivityForResult(discoverableIntent, BT_VISIBLE);
			}
		});

		btnClient.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				progressDialog = ProgressDialog.show(MainActivityBluetooth.this, "Lütfen Bekleyin", "Cihazlar Aran›yor...");

				closeCommunication();

				devicesFound.clear(); 
				adaptador.startDiscovery(); 
			}
		});
		
		speakBtn=(Button)findViewById(R.id.micrphoneblt);
		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0)
		{
			speakBtn.setEnabled(false);
			speakBtn.setText("--");

		}
		speakBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivityBlt();				
			}
		});
		
		//kontrol tuslar›
		btnileriBlt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(bluetoothComunication != null){
					String msg = "ileri"; 


						bluetoothComunication.sendMessageByBluetooth(msg);

						historic.add("Yon: " + msg); 
						historic.notifyDataSetChanged();							
					
				}else{
					notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
				}
			}
		});
		btngeriBlt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(bluetoothComunication != null){
					String msg = "geri"; 


						bluetoothComunication.sendMessageByBluetooth(msg);

						historic.add("Yon: " + msg); 
						historic.notifyDataSetChanged();							
					
				}else{
					notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
				}
			}
		});
		btndurBlt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(bluetoothComunication != null){
					String msg = "dur"; 


						bluetoothComunication.sendMessageByBluetooth(msg);

						historic.add("Yon: " + msg); 
						historic.notifyDataSetChanged();							
					
				}else{
					notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
				}
			}
		});
		btnsagBlt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(bluetoothComunication != null){
					String msg = "sag"; 


						bluetoothComunication.sendMessageByBluetooth(msg);

						historic.add("Yon: " + msg); 
						historic.notifyDataSetChanged();							
					
				}else{
					notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
				}
			}
		});
		btnsolBlt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(bluetoothComunication != null){
					String msg = "sol"; 


						bluetoothComunication.sendMessageByBluetooth(msg);

						historic.add("Yon: " + msg); 
						historic.notifyDataSetChanged();							
					
				}else{
					notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
				}
			}
		});
		//
		cisimler=new ArrayList<String>();
		cisimler.add("kalem");
		cisimler.add("top");
		cisimler.add("silgi");
	}

	public void inicializaBluetooth() {
		adaptador = BluetoothAdapter.getDefaultAdapter(); 

		if (adaptador != null) {
			if (!adaptador.isEnabled()) { 
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				startActivityForResult(enableBtIntent, BT_ACTIVATE);
			}
		} else {
			notice.showToast("Ayg›t Bluetooth desteklemiyor");
			finish();
		}
	}

	public void registerFilters(){
		eventsBTReceiver = new EventsBluetoothReceiver(); 

		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 

		registerReceiver(eventsBTReceiver, filter1);
		registerReceiver(eventsBTReceiver, filter2);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			synchronized (msg) {
				switch (msg.what) {
				case 1:
					socket = (BluetoothSocket)(msg.obj);

					if(socket != null){
						bluetoothComunication = new BluetoothComunication(handler, 3, 2);
						bluetoothComunication.openComunication(socket);
					}else{
						notice.showToast("Ba€lant› Baﬂar›s›z");
					}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(eventsBTReceiver);

		closeCommunication();
	}

	public void closeCommunication(){
		try {
			if(socket != null){
				socket.close();
				socket = null;
			}

			if(bluetoothServiceTask != null){
				bluetoothServiceTask.closeServerSocket();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case BT_ACTIVATE:
			if (RESULT_OK != resultCode) {
				notice.showToast("Devam etmek için Bluetooth etkinleﬂtirmeniz gerekir");
				finish(); 
			}
			break;

		case BT_VISIBLE:
			if (resultCode == BT_TIMER_VISIBLE) {
				closeCommunication();

				bluetoothServiceTask = new BluetoothServiceTask(MainActivityBluetooth.this, adaptador, handler, 1); 
				bluetoothServiceTask.execute();
			} else {
				notice.showToast("Sunucuyu baﬂlatmak için, cihaz›n›z görünür olmal›");
			}
			break;
		}

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		{
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);

			for(int i=0;i<matches.size();i++){
				notice.showToast(matches.get(i).toString()+"  "+i);

				if(cisimler.contains(matches.get(i).toString())){
					//soylenen kelime biliniyorsa

					if(bluetoothComunication != null){
						String msg =matches.get(i).toString(); 


							bluetoothComunication.sendMessageByBluetooth(msg);

							historic.add("Cisim : " + msg); 
							historic.notifyDataSetChanged();							
						
					}else{
						notice.showToast("Baﬂka cihaz ile ba€lant›n›z olamaz.");
					}
					break;
				}
				if(i==matches.size()-1){
					//soylenen kelime bilinmiyorsa

				}
			}
		}
	}


	//
	private class EventsBluetoothReceiver extends BroadcastReceiver { 

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) { 
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				devicesFound.add(device); 
			} else{
				if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
					progressDialog.dismiss();
					exibirDispositivosEncontrados();
				}
			}
		}
	}

	private void exibirDispositivosEncontrados() { 

		String[] aparelhos = new String[devicesFound.size()]; 

		for (int i = 0; i < devicesFound.size(); i++){
			aparelhos[i] = devicesFound.get(i).getName();
		}

		AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Bulunan Cihazlar").setItems(aparelhos, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				bluetoothClientTask = new BluetoothClientTask(MainActivityBluetooth.this, handler, 1);
				bluetoothClientTask.execute(devicesFound.get(which));

				dialog.dismiss(); 
			}
		});
		dialog.show();
	}

}