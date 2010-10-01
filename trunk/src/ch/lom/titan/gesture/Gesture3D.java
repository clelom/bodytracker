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
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;

public class Gesture3D {

	private float[] m_x;
	private float[] m_y;
	private float[] m_z;
	
	public static final long SAMP_FREQUENCY = 100; //ms
	
	public Gesture3D(float [] x, float [] y, float [] z) {
		m_x = x;
		m_y = y;
		m_z = z;
	}
	
	public Gesture get2DGesture() {
		
		if ( m_x.length != m_y.length || m_x.length != m_z.length) {
			return null;
		}
		
		// project the 3D coordinates into a 2D plane.
		// best would probably be to use a principal component analysis, but let's just use the maximum extends
		
		
		// search for maximum distance between two points
		int length = m_x.length;
		float maxdist = 0;
		int p1=0,p2=0;
		for (int i=0; i<length; i++) {
			for (int j=i+1; j<length; j++) {
				float dx = m_x[j] - m_x[i];
				float dy = m_y[j] - m_y[i];
				float dz = m_z[j] - m_z[i];
				float dist = dx*dx + dy*dy + dz*dz;
				if (dist > maxdist) {
					maxdist = dist;
					p1 = i;
					p2 = j;
				}
			}
		}
		
		// plane normal vector
		float mag = (float) Math.sqrt(maxdist);
		float n1_x = (m_x[p2] - m_x[p1])/mag;
		float n1_y = (m_y[p2] - m_z[p1])/mag;
		float n1_z = (m_y[p2] - m_z[p1])/mag;
		
		// find max distance points on the plane normal to the first two points connection
		maxdist = 0;
		float n2_x=0,n2_y=0,n2_z=0;
		for (int i=0; i<length; i++) {
			for (int j=i+1; j<length; j++) {
				
				// get the connection
				float dx = m_x[j] - m_x[i];
				float dy = m_y[j] - m_y[i];
				float dz = m_z[j] - m_z[i];
				
				// project onto the plane by removing component along n_*
				float sp = dx*n1_x + dy*n1_y * dz*n1_z;
				float dx1 = dx - sp*dx;
				float dy1 = dy - sp*dy;
				float dz1 = dz - sp*dz;
				
				
				float dist = dx1*dx1 + dy1*dy1 + dz1*dz1;
				if (dist > maxdist) {
					maxdist = dist;
					n2_x = dx1;
					n2_y = dy1;
					n2_z = dz1;
				}
			}
		}
		mag = (float)Math.sqrt(maxdist);
		n2_x = n2_x/mag;
		n2_y = n2_y/mag;
		n2_z = n2_z/mag;
		
		// now we got the two surface coordinate vectors, project all points into 2D and generate timestamps
		long timestamp = 0;
		ArrayList<GesturePoint> path2D = new ArrayList<GesturePoint>(length);
		float [] x = new float[length];
		float [] y = new float[length];
		for (int i=0; i<length;i++) {
			
			float x2D = m_x[i]*n1_x + m_y[i]*n1_y + m_z[i]*n1_z; 
			float y2D = m_x[i]*n2_x + m_y[i]*n2_y + m_z[i]*n2_z; 
			
			x[i] = x2D;
			y[i] = y2D;
			
			path2D.add( new GesturePoint(x2D,y2D,timestamp));

			timestamp += SAMP_FREQUENCY;
		}
		
		GestureStroke stroke = new GestureStroke(path2D);
		
		Gesture gesture = new Gesture();
		gesture.addStroke(stroke);
		
		return gesture;
		
	}
	
}
