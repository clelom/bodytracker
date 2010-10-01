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

package ch.lom.titan.visual.opengl;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GLView extends SurfaceView implements SurfaceHolder.Callback {

	private GLThread m_glthread;
	
	public GLView(Context context) {
		super(context);
		
		getHolder().addCallback(this);
		
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
		
		
		setFocusable(true);
		setClickable(true);
		
	}

	///////////////////////////////////////////////////////////////////////////
	// OpenGL handling
	public void surfaceCreated(SurfaceHolder arg0) {
		m_glthread = new GLThread(this);
		m_glthread.start();
		
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		m_glthread.requestExitAndWait();
		m_glthread = null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Interaction

	public boolean onTouchEvent(MotionEvent event) {
    	
    	int value = event.getAction();
    	
    	switch(value){
    		case MotionEvent.ACTION_DOWN:
    			GLThread.m_trackball.onDown(event.getX(), event.getY());
    			break;
    		case MotionEvent.ACTION_MOVE:
    			GLThread.m_trackball.onMove(event.getX(), event.getY());
    			break;
    		case MotionEvent.ACTION_UP:
    			GLThread.m_trackball.onUp(event.getX(), event.getY());
    			break;
    		default:
    			break;
    	}
    	
    	
    	
    	return super.onTouchEvent(event);
    }
    
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	GLThread.m_trackball.onResize(w, h);
    	super.onSizeChanged(w, h, oldw, oldh);
    }
    

}
