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

public class Cylinder extends Mesh {

	/**
	 * Draws a cylinder along the z-axis centered around the origin
	 * @param r1 Radius at the negative z-axis
	 * @param r2 Radius at the positive z-axis
	 * @param length Length of the cylinder along the z-axis
	 * @param slices Number of slices generated around the z-axis
	 */
	public Cylinder(float r1, float r2, float length, int slices) {
		
		length /= 2.0f;
		
		float vertices[] = new float[2*3*slices];

		for(int i=0; i<slices; i++ ) {
			vertices[i*3  ] = (float) (r1 * Math.cos(2.0*Math.PI/slices*(float)i));
			vertices[i*3+1] = (float) (r1 * Math.sin(2.0*Math.PI/slices*(float)i));
			vertices[i*3+2] = -length;
			vertices[(slices+i)*3  ] = (float) (r2 * Math.cos(2.0*Math.PI/slices*(float)i));
			vertices[(slices+i)*3+1] = (float) (r2 * Math.sin(2.0*Math.PI/slices*(float)i));
			vertices[(slices+i)*3+2] = length;
		}
		
		// create only walls
		short indices[] = new short[2*3*(int)slices];
		for(short i=0; i<slices;i++) {
			indices[6*i  ] = i; 
			indices[6*i+1] = (short)((i+1)%slices); 
			indices[6*i+2] = (short)(i+slices);
			
			indices[6*i+3 ] = (short)(i+slices);
			indices[6*i+4]  = (short)((i+1)%slices);
			indices[6*i+5]  = (short)((i+1)%slices+slices);
		}
		
		setVertices(vertices);
		setIndices(indices);
	}
	
}
