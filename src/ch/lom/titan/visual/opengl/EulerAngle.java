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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.Matrix;

/**
 * Stores orientation as a set of nautical (Tait–Bryan) angles (roll, pitch,yaw).
 *
 * Positive pitch : nose up
 * Positive roll : right wing down
 * Positive yaw : clockwise
 *
 * more information at: http://en.wikipedia.org/wiki/Euler_angles
 *
 **/
 
 public class EulerAngle {
	private float m_roll;
	private float m_pitch;
	private float m_yaw;
	private FloatBuffer m_matrix;
	private boolean m_changed = true;
	private float [] m_buffer = new float[16];
	
	public EulerAngle(float roll, float pitch, float yaw) {
		m_roll  = roll;
		m_pitch = pitch;
		m_yaw   = yaw;
	}
	
	public synchronized EulerAngle set(float roll, float pitch, float yaw) {
		m_roll  = roll;
		m_pitch = pitch;
		m_yaw   = yaw;

		m_changed = true;

		return this;
	}
	public String toString() {
		return "roll=" + m_roll + " pitch=" + m_pitch + " yaw=" + m_yaw;
	}
	
	public float getYaw() {
		return m_yaw;
	}

	public float getPitch() {
		return m_pitch;
	}

	public float getRoll() {
		return m_roll;
	}
	
	/**
	 * This code parses an Euler angle string from the 
	 * Sparkfun 9DOF Razor IMU AHRS 1.0 firmware.
	 *
	 * Expected format (only text within quotes): "!ANG:0.15,0.42,17.98"
	 *
	 * @return EulerAngle containing the angles, NULL if parsing unsuccessful
	 */
	public static EulerAngle parseLine(String line) {
		return parseLine(line, null);
	}

	public static EulerAngle parseLine(String line, EulerAngle eu) {
		if (line.length() < 6 || line.substring(0,5).compareTo("!ANG:") != 0) {
			return null;
		}
		
		String [] strAngles = line.substring(5).split(",");
		
		if (strAngles.length != 3) return null;

		float roll  = Float.parseFloat(strAngles[0]);
		float pitch = Float.parseFloat(strAngles[1]);
		float yaw   = Float.parseFloat(strAngles[2]);
		
		return (eu == null)? new EulerAngle(roll,pitch,yaw) : eu.set(roll, pitch, yaw);
	}
	
	public synchronized FloatBuffer getRotMatrix() {
		if(m_matrix == null) {
			ByteBuffer storage = ByteBuffer.allocateDirect(16*Float.SIZE/8);
			storage.order(ByteOrder.nativeOrder());
			m_matrix = storage.asFloatBuffer();
		}
		
		if(!m_changed) {
			m_matrix.position(0);
			return m_matrix;
		}
		
		Matrix.setRotateEulerM(m_buffer,0,m_pitch,m_yaw,m_roll);
		m_matrix.position(0);
		m_matrix.put(m_buffer);
		
		
		/* 
		// compute rotation
		float A = (float) Math.cos(Math.toRadians(m_pitch));
		float B = (float) Math.sin(Math.toRadians(m_pitch));
		float C = (float) Math.cos(Math.toRadians(m_yaw));
		float D = (float) Math.sin(Math.toRadians(m_yaw));
		float E = (float) Math.cos(Math.toRadians(m_roll));
		float F = (float) Math.sin(Math.toRadians(m_roll));
		float AD = A*D;
		float BD = B*D;
		
		m_matrix.position(0);

		m_matrix.put(C*E);
		m_matrix.put(-C*F);
		m_matrix.put(D);
		m_matrix.put(0);
		
		m_matrix.put( BD*E+A*F);
		m_matrix.put(-BD*F+A*E);
		m_matrix.put(-B*C);
		m_matrix.put(0);
		
		m_matrix.put(-AD*E+B*F);
		m_matrix.put( AD*F+B*E);
		m_matrix.put(A*C);
		m_matrix.put(0);
		
		m_matrix.put(0);
		m_matrix.put(0);
		m_matrix.put(0);
		m_matrix.put(1);
		*/
		
		
		// scroll back to the beginning
		m_matrix.position(0);

		m_changed = false;
		
		return m_matrix;
	}
	
 }
 
