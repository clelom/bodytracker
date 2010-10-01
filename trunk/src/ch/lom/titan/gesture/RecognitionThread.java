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


package ch.lom.titan.gesture;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.Prediction;
import ch.lom.titan.visual.BodyActivity;
import ch.lom.titan.visual.R;
import ch.lom.titan.visual.opengl.MotionPath;

public class RecognitionThread extends Thread {

	private MotionPath m_mpath;
	private boolean    m_running = true;
	
	public RecognitionThread(MotionPath mp) {
		m_mpath = mp;
	}
	
	private static final float MIN_VARIANCE = 0.001f;
	
	public void run() {
		
		GestureLibrary library = GestureLibraries.fromRawResource(BodyActivity.singleton, R.raw.spells);
		
		if (!library.load()) {
			return;
		}

		while(m_running) {

			int length = m_mpath.getPathLength(); 
			
			if ( length > 10 ) {
				
				// compute variance
				float mag = 0;
				float mean = 0;
				float sqmean = 0;
				
				float [] path = m_mpath.getPathAsFloat();
				
				float [] x = new float[length];
				float [] y = new float[length];
				float [] z = new float[length];

				
				int pos = 0;
				for (int i=0; i<length; i++) {
					x[i] = path[pos++];
					y[i] = path[pos++];
					z[i] = path[pos++];
					mag = x[i]*x[i] + y[i]*y[i] + z[i]*z[i];
					
					// compute variance
					mean   += Math.sqrt(mag);
					sqmean += mag;
				}
				
				// check whether we have enough motion
				if ( (sqmean - mean*mean/length)/(length-1) < MIN_VARIANCE) {
					//m_mpath.restartMotion();
				} else {
					Gesture3D gest3D = new Gesture3D(x,y,z);
					Gesture gest2D = gest3D.get2DGesture();
					
					ArrayList<Prediction> predictions = library.recognize(gest2D);
					
					String strPrediction = "";
					for (Prediction p : predictions) {
						strPrediction += p.name + "=" + p.score +"\n";
					}
					BodyActivity.singleton.setText(strPrediction);
					
				}
				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	public void requestExit() {
		m_running = false;
	}
	
}
