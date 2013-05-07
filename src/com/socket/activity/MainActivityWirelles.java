package com.socket.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import net.ercsoft.robotcontrolwrls_blueth.R;
import com.socket.business.BusinessLogic;

public class MainActivityWirelles extends Activity{
    private static final int REQUEST_CODE = 1234;
	private BusinessLogic businessLogic;
    String cisim="one";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wirelles_main);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        businessLogic = new BusinessLogic(this);
        businessLogic.configView();
        
        Button speakBtn=(Button)findViewById(R.id.micrphone);
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
        	speakBtn.setEnabled(false);
        	speakBtn.setText("--");
        
        }
    	speakBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        startVoiceRecognitionActivity();
				
			}
		});
        
        
    }
    
    
    
	//microphone code
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "In the picture? tell a voice");
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            
            for(int i=0;i<matches.size();i++){
            	
            	if(matches.get(i).toString().equals(cisim)){
            		//soylenen kelime biliniyorsa
            		businessLogic.sendSubstanceName(cisim);
            		break;
            		}
            	if(i==matches.size()-1){
            		//soylenen kelime bilinmiyorsa

            	}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	businessLogic.closeCommunication();
    }
    
}