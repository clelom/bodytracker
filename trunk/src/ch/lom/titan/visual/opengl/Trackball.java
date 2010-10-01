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
 *
 *
 * Most code copied from: MSSDK Sample pgonoffs (trackbal.c + .h)
 *
 * Implemented by Gavin Bell, lots of ideas from Thant Tessman and
 *   the August '88 issue of Siggraph's "Computer Graphics," pp. 121-129.
 *
 * Vector manip code:
 *
 * Original code from:
 * David M. Ciemiewicz, Mark Grossman, Henry Moreton, and Paul Haeberli
 *
 * Much mucking with by:
 * Gavin Bell
 *
 * Shell hacking courtesy of:
 * Reptilian Inhaleware
 * 
 * Ported to Java and Android by Clemens Lombriser
 * 
 */

package ch.lom.titan.visual.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Trackball {

	protected static final float MINMOTION = 0.5f;
	
	
	public void onDown(float x, float y) {
		glDownX = x;
		glDownY = y;
		gbLeftMouse = true;
	}
	
	public void onUp(float x, float y) {
		gbLeftMouse = false;
	}
	
	public void onMove(float x, float y) {
		if(gbLeftMouse) {
			glPosX = x;
			glPosY = y;
		}
	}
	
	public void onResize(int width, int height ) {
		giHeight = height;
		giWidth  = width;
	}
	
	public FloatBuffer getRotMatrix() {
		
		float px,py;
		
		if (gbLeftMouse) {
			px = glPosX;
			py = glPosY;
			
			// check for movement (touch input is not as exact)
			if (Math.abs(px - glDownX) >= MINMOTION || Math.abs(py - glDownY) >= MINMOTION){
				calcQuat(lastquat,
                        -(2.0f * ( giWidth - glDownX ) / giWidth - 1.0f),
                        -(2.0f * glDownY / giHeight - 1.0f),
                        -(2.0f * ( giWidth - px ) / giWidth - 1.0f),
                        -(2.0f * py / giHeight - 1.0f)
                       );
				gbSpinning = true;
            } else {
    			gbSpinning = false;
            }
			
            glDownX = px;
            glDownY = py;
        }
		
	    if (gbSpinning) addQuats(lastquat, curquat, curquat);
		
		return buildRotmatrix(curquat);
	}
	
	// //////////////////////////////////////////////////////////////////////
	// non-public methods

	int		giWidth;
	int		giHeight;
	float   glDownX;
	float   glDownY;
	float   glPosX;
	float   glPosY;
	boolean gbLeftMouse;
	boolean gbSpinning;
	float[] curquat  = new float[4];
	float[] lastquat = new float[4];
	
	FloatBuffer rotMatrix;
	
	private final static float TRACKBALLSIZE = 0.8f;
	
	public Trackball() {
		gbLeftMouse = false;
		gbSpinning  = false;
		
		giHeight = 480;
		giWidth  = 800;

		calcQuat( curquat, 0.0f, 0.0f, 0.0f, 0.0f );
		
	    ByteBuffer vbb = ByteBuffer.allocateDirect( 16 * 4 );
	    vbb.order(ByteOrder.nativeOrder());
	    rotMatrix = vbb.asFloatBuffer();

	}
	
	private void calcQuat(float [] q, float p1x, float p1y, float p2x, float p2y)
	{
	    float[] a = new float[3];
	    float phi;
	    float[] p1 = new float[3];
	    float[] p2 = new float[3];
	    float[] d = new float[3];
	    float t;

	    if (p1x == p2x && p1y == p2y) {
	        vzero(q); 
		q[3] = 1.0f; 
	        return;
	    }

	    vset(p1,p1x,p1y,projectToSphere(TRACKBALLSIZE,p1x,p1y));
	    vset(p2,p2x,p2y,projectToSphere(TRACKBALLSIZE,p2x,p2y));

	    vcross(p2,p1,a);

	    vsub(p1,p2,d);
	    t = vlength(d) / (2.0f*TRACKBALLSIZE);

	    if (t > 1.0f) t = 1.0f;
	    if (t < -1.0f) t = -1.0f;
	    phi = 2.0f * (float) Math.asin(t);

	    axisToQuat(a,phi,q);
	}

	private final void vzero(float [] v) {
	    v[0] = 0.0f;
	    v[1] = 0.0f;
	    v[2] = 0.0f;
	}

	private final void vset(float []v, float x, float y, float z) {
	    v[0] = x;
	    v[1] = y;
	    v[2] = z;
	}
	
	private final void vsub(float [] src1, float [] src2, float [] dst){
	    dst[0] = src1[0] - src2[0];
	    dst[1] = src1[1] - src2[1];
	    dst[2] = src1[2] - src2[2];
	}
	
	private final void vcopy(float [] v1, float [] v2){
	    int i;
	    for (i = 0 ; i < 3 ; i++) {
	        v2[i] = v1[i];
	    }
	}	
	
	private final void vcross(float [] v1, float [] v2, float [] cross) {
	    float [] temp = new float[3];

	    temp[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
	    temp[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
	    temp[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
	    vcopy(temp, cross);
	}
	
	private final float vlength(float [] v){
	    return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}
	
	private final void vscale(float [] v, float div) {
	    v[0] *= div;
	    v[1] *= div;
	    v[2] *= div;
	}
	
	private final void vnormal(float [] v) {
	    vscale(v,1.0f/vlength(v));
	}

	private final float vdot(float [] v1, float [] v2) {
	    return v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2];
	}

	private final void vadd(float [] src1, float [] src2, float [] dst) {
	    dst[0] = src1[0] + src2[0];
	    dst[1] = src1[1] + src2[1];
	    dst[2] = src1[2] + src2[2];
	}

	private final void axisToQuat(float [] a, float phi, float [] q) {
	    vnormal(a);
	    vcopy(a,q);
	    vscale(q,(float) Math.sin(phi/2.0f));
	    q[3] = (float) Math.cos(phi/2.0f);
	}

	private final float projectToSphere(float r, float x, float y) {
	    float d, t, z;

	    d = (float) Math.sqrt(x*x + y*y);
	    if (d < r * 0.70710678118654752440f) {   
		z = (float) Math.sqrt(r*r - d*d);
	    } else {        
	        t = r / 1.41421356237309504880f;
	        z = t*t / d;
	    }
	    return z;
	}

	private static final int RENORMCOUNT = 97;
	private int count;
	private final void addQuats(float [] q1, float [] q2, float [] dest) {
	    float [] t1 = new float[4];
	    float [] t2 = new float[4];
	    float [] t3 = new float[4];
	    float [] tf = new float[4];

	    vcopy(q1,t1); 
	    vscale(t1,q2[3]);

	    vcopy(q2,t2); 
	    vscale(t2,q1[3]);

	    vcross(q2,q1,t3);
	    vadd(t1,t2,tf);
	    vadd(t3,tf,tf);
	    tf[3] = q1[3] * q2[3] - vdot(q1,q2);

	    dest[0] = tf[0];
	    dest[1] = tf[1];
	    dest[2] = tf[2];
	    dest[3] = tf[3];

	    if (++count > RENORMCOUNT) {
	        count = 0;
	        normalizeQuat(dest);
	    }
	}

	private final void normalizeQuat(float [] q) {
	    int i;
	    float mag;

	    mag = (q[0]*q[0] + q[1]*q[1] + q[2]*q[2] + q[3]*q[3]);
	    for (i = 0; i < 4; i++) q[i] /= mag;
	}
	
	private final FloatBuffer buildRotmatrix(float [] q)
	{
		rotMatrix.position(0);
		
		rotMatrix.put(1.0f - 2.0f * (q[1] * q[1] + q[2] * q[2]));
		rotMatrix.put(2.0f * (q[0] * q[1] - q[2] * q[3]));
		rotMatrix.put(2.0f * (q[2] * q[0] + q[1] * q[3]));
		rotMatrix.put(0.0f);

		rotMatrix.put(2.0f * (q[0] * q[1] + q[2] * q[3]));
		rotMatrix.put(1.0f - 2.0f * (q[2] * q[2] + q[0] * q[0]));
		rotMatrix.put(2.0f * (q[1] * q[2] - q[0] * q[3]));
		rotMatrix.put(0.0f);

		rotMatrix.put(2.0f * (q[2] * q[0] - q[1] * q[3]));
		rotMatrix.put(2.0f * (q[1] * q[2] + q[0] * q[3]));
		rotMatrix.put(1.0f - 2.0f * (q[1] * q[1] + q[0] * q[0]));
		rotMatrix.put(0.0f);

		rotMatrix.put(0.0f);
		rotMatrix.put(0.0f);
		rotMatrix.put(0.0f);
		rotMatrix.put(1.0f);
		
		rotMatrix.position(0);
		
		return rotMatrix;
	}
	
}
