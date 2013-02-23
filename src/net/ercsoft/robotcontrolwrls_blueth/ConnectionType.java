package net.ercsoft.robotcontrolwrls_blueth;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

public class ConnectionType extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connecttype);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		Button blue=(Button)findViewById(R.id.bluetooth);
		blue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent("android.intent.action.com.bluetooth.activity.MAINACTIVITYBLUETOOTH"));

			}
		});
		Button wifi=(Button)findViewById(R.id.wifi);
		wifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.intent.action.com.socket.activity.MAINACTIVITYWIRELLES"));
			}
		});
		
	}


}
