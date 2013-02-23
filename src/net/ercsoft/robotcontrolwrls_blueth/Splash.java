package net.ercsoft.robotcontrolwrls_blueth;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

public class Splash extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Thread ac = new Thread() {
			public void run() {
				try {

					sleep(2000);
					

				} catch (InterruptedException e) {

					e.printStackTrace();
				}finally{
					
					finish();
					startActivity(new Intent("android.intent.action.net.ercsoft.robotcontrolwrls_blueth.CONNECTIONTYPE"));
				}


			}
		};
		ac.start();
    }

}