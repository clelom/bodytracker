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

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL10;

import ch.lom.titan.visual.Body;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLU;

class GLThread extends Thread {
	private static GLView m_view;
	private boolean m_finish = false;
	private static Body   m_body = new Body();
	
	private static final int SLEEP_AFTER_DRAW_MS = 10;
	
	protected static Trackball m_trackball = new Trackball(); 

	public GLThread(GLView view) {
		m_view = view;
	}

	public void requestExitAndWait() {
		m_finish = true;
		
		try{
			join();
		} catch(InterruptedException e) {
			
		}
	}

	public void run() {
		
		setName("DrawThread");
		
		EGL10 egl = (EGL10)EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		
		int[] version = new int[2];
		egl.eglInitialize(display, version);
		
		int[] configSpec = { EGL10.EGL_RED_SIZE, 5, EGL10.EGL_GREEN_SIZE, 6, EGL10.EGL_BLUE_SIZE, 5, EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
		
		EGLConfig[] configs = new EGLConfig[1];
		int[] numConfig = new int[1];
		egl.eglChooseConfig(display, configSpec, configs, 1, numConfig);
		EGLConfig config = configs[0];
		
		EGLContext glc = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, null);
		EGLSurface surface = egl.eglCreateWindowSurface(display, config, m_view.getHolder(), null);
		egl.eglMakeCurrent(display, surface, surface, glc);
		
		GL10 gl = (GL10)(glc.getGL());
		init(gl);
		
		while(!m_finish) {
			drawFrame(gl);
			egl.eglSwapBuffers(display, surface);
			
			if (egl.eglGetError() == EGL11.EGL_CONTEXT_LOST) {
				Context c = m_view.getContext();
				if(c instanceof Activity) {
					((Activity)c).finish();
				}
			}
			
			//if (Body.m_orientations[0] != null) {
			//	BodyActivity.singleton.setText(Body.m_orientations[0].toString());
			//}
			
			try {
				Thread.sleep(SLEEP_AFTER_DRAW_MS);
			} catch (InterruptedException e) {
			}
		}
		
		egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		egl.eglDestroySurface(display, surface);
		egl.eglDestroyContext(display, glc);
		egl.eglTerminate(display);
	}

	private void init(GL10 gl) {

		// define frustrum
		gl.glViewport(0, 0, m_view.getWidth(), m_view.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float aspect = (float)m_view.getWidth()/m_view.getHeight();
		GLU.gluPerspective(gl, 45.0f, aspect, 2.0f, 7.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// create light
		float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1 };
		float lightDiffuse[] = new float[] { 1, 1, 1, 1 };
		float[] lightPos = new float[] { 1, 1, 1, 1 };
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);

		// other options
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// gl.glDisable(GL10.GL_DITHER); // turn off dither for better performance
		
	}
	
	private void drawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();
		
		// set up view area
		gl.glTranslatef(0,0,-4.5f);
		gl.glMultMatrixf(m_trackball.getRotMatrix());
	
		// draw model
		m_body.draw(gl);
		
	}

}
