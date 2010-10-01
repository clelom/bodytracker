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

public final class MotionPath {

	private static final int MAX_POSITIONS = 100;
	
	int m_curSize = 0;
	int m_curPos  = 0;

	FloatBuffer m_pathBuffer;
	
	public MotionPath() {
	    ByteBuffer vbb = ByteBuffer.allocateDirect( 3 * MAX_POSITIONS * Float.SIZE/8);
	    vbb.order(ByteOrder.nativeOrder());
	    m_pathBuffer = vbb.asFloatBuffer();
	}
	
	public final synchronized FloatBuffer getMotionPath() {
	    m_pathBuffer.position(0);
	    return m_pathBuffer;
	}
	
	public final synchronized void addPosition(float x, float y, float z) {
		
		m_curSize++;
		if (m_curSize >= MAX_POSITIONS) {
			m_curPos = m_curSize = 0;
		}
		
		int pos = m_pathBuffer.position();
		m_pathBuffer.position(m_curPos);
		m_pathBuffer.put(x);
		m_pathBuffer.put(y);
		m_pathBuffer.put(z);
		m_curPos += 3;
		m_pathBuffer.position(pos);
	}

	public final int getPathLength() {
		return m_curSize;
	}
	
	public final synchronized void restartMotion() {
		m_curSize = m_curPos = 0;
	}
	
	public final synchronized float [] getPathAsFloat() {
		float [] path = new float[m_curPos];
		
		int pos = m_pathBuffer.position();
		m_pathBuffer.position(0);
		m_pathBuffer.get(path,0,m_curPos);
		m_pathBuffer.position(pos);
		
		return path;
	}
	

	
}
