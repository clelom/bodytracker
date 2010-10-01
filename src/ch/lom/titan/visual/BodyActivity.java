/*
 * Copyright (C) 2010 Clemens Lombriser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package ch.lom.titan.visual;

import ch.lom.titan.bluetooth.AssignBTSensors;
import ch.lom.titan.gesture.RecognitionThread;
import ch.lom.titan.visual.opengl.GLView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BodyActivity extends Activity {
	
	private GLView m_view;
	private TextView m_text;
    public static BodyActivity singleton;
    public static RecognitionThread m_recognition;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linlay = new LinearLayout(this);
        linlay.setOrientation(LinearLayout.VERTICAL);
        
        m_text = new TextView(this);
        m_text.setText("   ");
        linlay.addView(m_text,LayoutParams.MATCH_PARENT);
        
        
        m_view = new GLView(this);
        linlay.addView(m_view);
        
        setContentView(linlay);
    
        singleton = this;
        
        m_recognition = new RecognitionThread(Body.m_motion);
        m_recognition.start();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	
    	menu.getItem(0).setIcon(android.R.drawable.ic_menu_preferences);
    	menu.getItem(1).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {

    	boolean retVal = true;
    	
    	switch(item.getItemId()) {
    		case R.id.ic_menu_exit:
    			Body.stop();
    			m_recognition.requestExit();
    			finish();
    			break;
    		case R.id.ic_menu_sensor_assignment:
    			
    			Intent setint = new Intent(getApplicationContext(), AssignBTSensors.class);
    			startActivity(setint);
    			
    			break;
			default:
				retVal = false;
				break;
    	}
    	
    	return retVal;
    }


    private String m_strText;
    private final Handler  m_handler = new Handler();
    private final Runnable m_updateString = new Runnable() {
    	public void run() {
    		updateText(m_strText);
    	}
    };
    public void setText(String strText) {
    	m_strText = strText;
    	m_handler.post(m_updateString);
    }
    private void updateText(String str) {
    	
    	// show as toast
		//Toast toast = Toast.makeText(getApplicationContext(), m_strText, Toast.LENGTH_SHORT);
		//toast.show();
    	
    	// show in text field
    	m_text.setText(str);
    }

}
