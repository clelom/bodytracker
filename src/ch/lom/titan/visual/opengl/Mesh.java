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

/*
 * Based on code from Per-Erik Bergman's tutorial to be found at:
 * 
 * http://blog.jayway.com/2010/02/15/opengl-es-tutorial-for-android-%E2%80%93-part-v/
 * 
 */


package ch.lom.titan.visual.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Mesh {
  // Our vertex buffer.
  private FloatBuffer verticesBuffer = null;

  // Our index buffer.
  private ShortBuffer indicesBuffer = null;

  // The number of indices.
  private int numOfIndices = -1;

  // Flat Color
  private float[] rgba = new float[]{0.5f, 0.5f, 0.5f, 0.5f};

  // Smooth Colors
  private FloatBuffer colorBuffer = null;

  public void draw(GL10 gl) {
    // Counter-clockwise winding.
    gl.glFrontFace(GL10.GL_CCW);
    // Enable face culling.
    gl.glEnable(GL10.GL_CULL_FACE);
    // What faces to remove with the face culling.
    gl.glCullFace(GL10.GL_BACK);
    // Enabled the vertices buffer for writing and to be used during rendering.
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    // Specifies the location and data format of an array of vertex coordinates to use when rendering.
    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
    
    // Set flat color
    gl.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
	float matAmbient[] = new float[] { rgba[0], rgba[1], rgba[2], rgba[3] };
	float matDiffuse[] = new float[] { rgba[0], rgba[1], rgba[2], rgba[3] };
	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,matAmbient, 0);
	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,matDiffuse, 0);

    // Smooth color
    if ( colorBuffer != null ) {
      // Enable the color array buffer to be used during rendering.
      gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
      // Point out the where the color buffer is.
      gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
    }
    
    gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
    // Disable the vertices buffer.
    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    // Disable face culling.
    gl.glDisable(GL10.GL_CULL_FACE);
  }
  
  protected void setVertices(float[] vertices) {
    // a float is 4 bytes, therefore we multiply the number if vertices with 4.
    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
    vbb.order(ByteOrder.nativeOrder());
    verticesBuffer = vbb.asFloatBuffer();
    verticesBuffer.put(vertices);
    verticesBuffer.position(0);
  }
  
  protected void setIndices(short[] indices) {
    // short is 2 bytes, therefore we multiply the number if vertices with 2.
    ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
    ibb.order(ByteOrder.nativeOrder());
    indicesBuffer = ibb.asShortBuffer();
    indicesBuffer.put(indices);
    indicesBuffer.position(0);
    numOfIndices = indices.length;
  }
  
  public void setColor(float red, float green, float blue, float alpha) {
    // Setting the flat color.
    rgba[0] = red;
    rgba[1] = green;
    rgba[2] = blue;
    rgba[3] = alpha;
  }
  
  protected void setColors(float[] colors) {
    // float has 4 bytes.
    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
    cbb.order(ByteOrder.nativeOrder());
    colorBuffer = cbb.asFloatBuffer();
    colorBuffer.put(colors);
    colorBuffer.position(0);
  }
  
}
