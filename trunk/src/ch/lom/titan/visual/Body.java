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
 * Many thanks to Thomas Stiefmeier for adding legs and eyes to the body.
 * 
 */

package ch.lom.titan.visual;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import ch.lom.titan.bluetooth.BluetoothEulerParser;
import ch.lom.titan.visual.opengl.Cylinder;
import ch.lom.titan.visual.opengl.EulerAngle;
import ch.lom.titan.visual.opengl.MotionPath;
import ch.lom.titan.visual.opengl.Sphere;

public final class Body {
	
    private static final float SHOULDER_WIDTH   = 0.8f;
    private static final float BACKBONE_HEIGHT  = 1.0f;
    private static final float UPPER_ARM_LENGTH = 0.6f;
    private static final float LOWER_ARM_LENGTH = 0.4f;
    private static final float HIP_WIDTH        = 0.4f;
    private static final float UPPER_LEG_LENGTH = 0.55f;
    private static final float LOWER_LEG_LENGTH = 0.55f;
    
    public static final String [] ORIENTATION_SENSOR_NAMES = {
    		"Right Lower Arm (RLA)",
    		"Right Upper Arm (RUA)",
			"Right Lower Leg (RLL)",
			"Right Upper Leg (RUL)",
			"Left Lower Arm (LLA)",
			"Left Upper Arm (LUA)",
			"Left Lower Leg (LLL)",
			"Left Upper Leg (LUL)",
	  };
    public static EulerAngle[] m_orientations;
    public static BluetoothEulerParser[] m_parsers;
    
    public static MotionPath m_motion = new MotionPath();
    
    static {
    	m_parsers = new BluetoothEulerParser[ORIENTATION_SENSOR_NAMES.length];
    	
    	m_orientations = new EulerAngle[ORIENTATION_SENSOR_NAMES.length];
    	for(int i=0; i<ORIENTATION_SENSOR_NAMES.length; i++) {
    		m_orientations[i] = new EulerAngle(0,(i+1)*5,0);
    	}
    }
    
    public static void stop() {
    	for (int i=0; i<m_parsers.length; i++) {
    		if(m_parsers[i] != null) {
    			m_parsers[i].requestExitAndWait();
    			m_parsers[i] = null;
    		}
    	}
    }
    
    
	public final void draw(GL10 gl) {
		
		
		// test code
		m_orientations[0].set(0, m_orientations[0].getPitch()+5, 0);
		// end test code
		
		
		gl.glPushMatrix();

	    	/////////////////////////////////////////////////////////////////////
	    	// draw right body parts
			FloatBuffer rla = m_orientations[0].getRotMatrix();
			FloatBuffer rua = m_orientations[1].getRotMatrix();
		
			gl.glPushMatrix(); // right arms
				gl.glTranslatef( -SHOULDER_WIDTH/2.0F, 1.0F, 0.0F ); // to the end of the upper arm
				gl.glPushMatrix(); // upper right arm
					// rotation of right upper arm
					if (rua != null) gl.glMultMatrixf(rua);
					drawUpperArm(gl);
				gl.glPopMatrix(); // upper right arm
	      
				gl.glPushMatrix(); // lower right arm
				
					float a12,a13,a14;
				
					if (rla != null) {
						if (rua != null) {
							a12 = -UPPER_ARM_LENGTH*rua.get(4)-SHOULDER_WIDTH/2.0F;
							a13 = -UPPER_ARM_LENGTH*rua.get(5)+1f;
							a14 = -UPPER_ARM_LENGTH*rua.get(6);
							rla.put(12,a12);
							rla.put(13,a13);
							rla.put(14,a14);
						} else {
							a12 = 0;
							a13 = -UPPER_ARM_LENGTH;
							a14 = 0;
							rla.put(12,0);
							rla.put(13,-UPPER_ARM_LENGTH);
							rla.put(14,0);
						}
						gl.glMultMatrixf(rla);
					} else {
						a12 = 0;
						a13 = -UPPER_ARM_LENGTH;
						a14 = 0;
						gl.glTranslatef( 0.0F, -UPPER_ARM_LENGTH, 0.0F );
					}
					drawLowerArm(gl);
					gl.glTranslatef( 0.0F, -LOWER_ARM_LENGTH, 0.0F ); // to the end of the lower arm
					
					drawHand(gl);

					// to track the right hand, just store the coordinates:
					m_motion.addPosition(a12-LOWER_ARM_LENGTH*rla.get(4), a13-LOWER_ARM_LENGTH*rla.get(5), a14-LOWER_ARM_LENGTH*rla.get(6));

				gl.glPopMatrix(); // lower right arm
			gl.glPopMatrix(); // right arms


			FloatBuffer rll = m_orientations[2].getRotMatrix();
			FloatBuffer rul = m_orientations[3].getRotMatrix();
			
			gl.glPushMatrix(); // right legs
				gl.glTranslatef( -HIP_WIDTH/2.0F, 0.0F, 0.0F ); // to the end of the hip

				gl.glPushMatrix(); // upper right leg
					// rotation of right upper leg
					if (rul != null) gl.glMultMatrixf(rul);
					drawUpperLeg(gl);
				gl.glPopMatrix(); // upper right leg

				gl.glPushMatrix(); // lower right leg
					if (rll != null) {
						if (rul != null) {
							rll.put(12,-UPPER_LEG_LENGTH*rul.get(4));
							rll.put(13,-UPPER_LEG_LENGTH*rul.get(5));
							rll.put(14,-UPPER_LEG_LENGTH*rul.get(6));
						} else {
							rll.put(12,0);
							rll.put(13,-UPPER_LEG_LENGTH);
							rll.put(14,0);
						}
						gl.glMultMatrixf(rll);
					} else {
						gl.glTranslatef( 0.0F, -UPPER_LEG_LENGTH, 0.0F );
					}
					drawLowerLeg(gl);
				gl.glPopMatrix(); // lower right leg
			gl.glPopMatrix(); // right legs
				
			/////////////////////////////////////////////////////////////////////
			// draw left body parts
	    	/////////////////////////////////////////////////////////////////////
	    	// draw left body parts
			FloatBuffer lla = m_orientations[4].getRotMatrix();
			FloatBuffer lua = m_orientations[5].getRotMatrix();
		
			gl.glPushMatrix(); // left arms
				gl.glTranslatef( SHOULDER_WIDTH/2.0F, 1.0F, 0.0F ); // to the end of the upper arm
				gl.glPushMatrix(); // upper left arm
					// rotation of left upper arm
					if (lua != null) gl.glMultMatrixf(lua);
					drawUpperArm(gl);
				gl.glPopMatrix(); // upper left arm
	      
				gl.glPushMatrix(); // lower left arm
					if (lla != null) {
						if (lua != null) {
							lla.put(12,-UPPER_ARM_LENGTH*lua.get(4));
							lla.put(13,-UPPER_ARM_LENGTH*lua.get(5));
							lla.put(14,-UPPER_ARM_LENGTH*lua.get(6));
						} else {
							lla.put(12,0);
							lla.put(13,-UPPER_ARM_LENGTH);
							lla.put(14,0);
						}
						gl.glMultMatrixf(lla);
					} else {
						gl.glTranslatef( 0.0F, -UPPER_ARM_LENGTH, 0.0F );
					}
					drawLowerArm(gl);
					gl.glTranslatef( 0.0F, -LOWER_ARM_LENGTH, 0.0F ); // to the end of the lower arm
					
					// to track the left hand, just store the coordinates:
					// x = lla.get(12);
					// y = lla.get(13) - LOWER_ARM_LENGTH;
					// z = lla.get(14);
					drawHand(gl);
				gl.glPopMatrix(); // lower left arm
			gl.glPopMatrix(); // left arms


			FloatBuffer lll = m_orientations[6].getRotMatrix();
			FloatBuffer lul = m_orientations[7].getRotMatrix();
			
			gl.glPushMatrix(); // left legs
				gl.glTranslatef( HIP_WIDTH/2.0F, 0.0F, 0.0F ); // to the end of the hip

				gl.glPushMatrix(); // upper left leg
					// rotation of left upper leg
					if (lul != null) gl.glMultMatrixf(lul);
					drawUpperLeg(gl);
				gl.glPopMatrix(); // upper left leg

				gl.glPushMatrix(); // lower left leg
					if (lll != null) {
						if (lul != null) {
							lll.put(12,-UPPER_LEG_LENGTH*lul.get(4));
							lll.put(13,-UPPER_LEG_LENGTH*lul.get(5));
							lll.put(14,-UPPER_LEG_LENGTH*lul.get(6));
						} else {
							lll.put(12,0);
							lll.put(13,-UPPER_LEG_LENGTH);
							lll.put(14,0);
						}
						gl.glMultMatrixf(lll);
					} else {
						gl.glTranslatef( 0.0F, -UPPER_LEG_LENGTH, 0.0F );
					}
					drawLowerLeg(gl);
				gl.glPopMatrix(); // lower left leg
			gl.glPopMatrix(); // left legs

		/////////////////////////////////////////////////////////////////////
		// draw rest of the body
    	/////////////////////////////////////////////////////////////////////

		gl.glPushMatrix();
    		gl.glTranslatef( 0.0F, 1.4F, 0.0F ); // set the head on top of the torso
    		drawHead(gl);
		gl.glPopMatrix();
			 
	    // draw the torso
		drawTorso(gl);
		
		// draw the motion
		
		FloatBuffer motion = m_motion.getMotionPath();
		if ( motion != null ) {
			gl.glDisable(GL10.GL_LIGHTING);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glColor4f(1f, 1f, 0f, 1f);
			gl.glLineWidth(2);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, motion);
			gl.glDrawArrays(GL10.GL_LINE_STRIP,0,m_motion.getPathLength());
			gl.glEnable(GL10.GL_LIGHTING);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
	}

	private static Cylinder m_lowerarm = new Cylinder(0.050f,0.04f,LOWER_ARM_LENGTH,8);
	private final void drawLowerArm(GL10 gl) {
        gl.glPushMatrix();
        gl.glRotatef( 90.0F, 1.0F, 0.0F, 0.0F );
        gl.glTranslatef(0.0f, 0.0f,LOWER_ARM_LENGTH/2.0f);
        m_lowerarm.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        m_lowerarm.draw(gl);
        gl.glPopMatrix();
	}
	
	private static Sphere m_handcube = new Sphere(0.075f,4,4);
	private final void drawHand(GL10 gl) {
		m_handcube.draw(gl);
	}

	private static Cylinder m_upperarm = new Cylinder(0.05f,0.05f,UPPER_ARM_LENGTH,8);
	private final void drawUpperArm(GL10 gl) {
        gl.glPushMatrix();
        gl.glRotatef( 90.0F, 1.0F, 0.0F, 0.0F );
        gl.glTranslatef(0.0f, 0.0f,UPPER_ARM_LENGTH/2.0f);
        m_upperarm.draw(gl);
        gl.glPopMatrix();
	}

	private static Sphere m_skull = new Sphere(0.25f,8,8);
	private static Sphere m_eye   = new Sphere(0.05f,8,8);
	private static Sphere m_pupil = new Sphere(0.02f,8,8);
	private final void drawHead(GL10 gl) {

		m_skull.draw(gl);
        
		gl.glPushMatrix();
	        gl.glTranslatef(-0.1f, 0.075f, 0.19f);
	        m_eye.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	        m_eye.draw(gl);

	        gl.glTranslatef(0.005f, 0.00f, 0.05f);
	        m_pupil.setColor(0.0f, 0.0f, 0.0f, 1.0f);
	        m_pupil.draw(gl);
	        
	        gl.glTranslatef(0.190f, 0.00f, 0.00f);
	        m_pupil.draw(gl);

	        gl.glTranslatef(0.005f, 0.0f, -0.05f);
	        m_pupil.setColor(0.0f, 0.0f, 0.0f, 1.0f);
	        m_eye.draw(gl);

       gl.glPopMatrix();
	}

	private static Cylinder m_shoulders = new Cylinder(0.065f,0.065f, SHOULDER_WIDTH, 8);
	private static Cylinder m_backbone  = new Cylinder(0.1f,0.065f, BACKBONE_HEIGHT, 8);
	private static Cylinder m_hip       = new Cylinder(0.1f,0.1f,HIP_WIDTH,8);
	private final void drawTorso(GL10 gl) {

        // shoulders
        gl.glPushMatrix();
        gl.glTranslatef(0.0F, 1.0F, 0.0F);
        gl.glRotatef( 90.0F, 0.0F, 1.0F, 0.0F );
        m_shoulders.draw(gl);
        gl.glPopMatrix();

        // backbone
        gl.glPushMatrix();
        gl.glRotatef( 90.0F, 1.0F, 0.0F, 0.0F );
        gl.glTranslatef(0.0f, 0.0f,-BACKBONE_HEIGHT/2.0f);
        m_backbone.draw(gl);
        gl.glPopMatrix();
		
        // shoulders
        gl.glPushMatrix();
        //gl.glTranslatef(0.0F, 1.0F, 0.0F);
        gl.glRotatef( 90.0F, 0.0F, 1.0F, 0.0F );
        m_hip.draw(gl);
        gl.glPopMatrix();
	}

	private static Cylinder m_upperleg = new Cylinder(0.095f, 0.07f, UPPER_LEG_LENGTH, 8);
	private final void drawUpperLeg(GL10 gl) {
        gl.glPushMatrix();
        gl.glRotatef( 90.0F, 1.0F, 0.0F, 0.0F );
        gl.glTranslatef(0.0F, 0.0f, UPPER_LEG_LENGTH/2.0f);
        m_upperleg.draw(gl);
        gl.glPopMatrix();
	}

	private static Cylinder m_lowerleg = new Cylinder(0.070f, 0.06f, LOWER_LEG_LENGTH, 8);
	private final void drawLowerLeg(GL10 gl) {
        gl.glPushMatrix();
        gl.glRotatef( 90.0F, 1.0F, 0.0F, 0.0F );
        gl.glTranslatef(0.0F, 0.0f, LOWER_LEG_LENGTH/2.0f);
        m_lowerleg.draw(gl);
        gl.glPopMatrix();
	}


}
