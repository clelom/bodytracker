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

public class Sphere extends Mesh {

	
	public Sphere(float radius, int slices, int stacks) {

		float vertices[] = new float[3*(slices*stacks + 2)];

		for(int i=0; i<stacks; i++ ) {
			float stackoff = -radius + (i+1)*2*radius/(stacks+1);
			float r = (float) Math.sqrt(radius*radius - stackoff*stackoff);
			for(int j=0; j<slices; j++) {
				vertices[(i*slices + j)*3  ] = (float) (r * Math.cos(2.0*Math.PI/slices*(float)j));
				vertices[(i*slices + j)*3+1] = (float) (r * Math.sin(2.0*Math.PI/slices*(float)j));
				vertices[(i*slices + j)*3+2] = stackoff;
			}
		}
		
		// top and bottom
		vertices[3*slices*stacks  ] = 0;
		vertices[3*slices*stacks+1] = 0;
		vertices[3*slices*stacks+2] = -radius;

		vertices[3*slices*stacks+3] = 0;
		vertices[3*slices*stacks+4] = 0;
		vertices[3*slices*stacks+5] = radius;
		
		// create top and bottom caps
		short indices[] = new short[3*(2*slices + 2*stacks*slices)];
		for(short i=0; i<slices;i++) {
			indices[6*i  ] = (short)(slices*stacks); 
			indices[6*i+1] = (short)((i+1)%slices); 
			indices[6*i+2] = i;
			
			indices[6*i+3 ] = (short)(slices*stacks+1);
			indices[6*i+4]  = (short)(slices*(stacks-1) + i);
			indices[6*i+5]  = (short)(slices*(stacks-1) + (i+1)%slices);
		}
		int offset = 6*slices;

		// sides
		for (int i=0; i<stacks-1; i++) {
			for(int j=0; j<slices; j++) {
				indices[offset + 6*(i*slices+j)  ] = (short) (i*slices + j);
				indices[offset + 6*(i*slices+j)+1] = (short) (i*slices + (j+1)%slices);
				indices[offset + 6*(i*slices+j)+2] = (short) ((i+1)*slices + j);

				indices[offset + 6*(i*slices+j)+3] = (short) ((i+1)*slices + j);
				indices[offset + 6*(i*slices+j)+4] = (short) (i*slices + (j+1)%slices);
				indices[offset + 6*(i*slices+j)+5] = (short) ((i+1)*slices + (j+1)%slices);
			}
		}
		
		setVertices(vertices);
		setIndices(indices);
		
	}
	
	
}
