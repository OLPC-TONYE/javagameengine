package entities;

import engine.EngineManager;
import maths.ListofFloats;
import opengl.VertexArrayObject;
	

public class DebugDrawableObject {
	
	/**
	 * 
	 * @param from
	 *          the start point of grid
	 * @param fExtent
	 *          the length of the body
	 * @param fStep
	 *          the distance between gridlines
	 * @param y
	 *          the y-axis of the grid
	 *                     
	 * @return <code>VertexArrayObject</code>
	 */
	public static VertexArrayObject makeGridlines(float from, float fExtent, float fStep, float y) {
		
		ListofFloats vertices = new ListofFloats();
		for(int iLine = (int) (from-fExtent); iLine <= (from+fExtent); iLine += fStep) {
			// Draw Z lines
			float[] verticesZ = new float[]{iLine, y, fExtent, iLine, y, -fExtent};
			vertices.add(verticesZ);
		
			float[] verticesX = new float[]{fExtent, y, iLine, -fExtent, y, iLine};
			vertices.add(verticesX);
		}
		
		return EngineManager.createLines(vertices.toArray());
		
	}
	/**
	 * 
	 * @param height
	 *          the height of the body
	 * @param length
	 *          the length of the body
	 *          
	 * @return <code>VertexArrayObject</code>
	 */
	public static VertexArrayObject createArrow(float height, float length) {
		
		float l = height/2;			// Height of Arrow	
		float w = length/2;			// Length of Arrow
		float h = length * 0.3f;	// Arrow Head
		
		float[] vertices = new float[] {
			-w, l, 0,			// 	0 Arrow Body
			-w, -l, 0,			//	1 Arrow Body
			h, -l, 0,			//	2 Arrow Body			
			h, l, 0,			//	3 Arrow Body
			h, -height, 0,		//	4 Arrow Head
			w, 0, 0,			//	5 Arrow Head
			h, height, 0,		//	6 Arrow Head
		};
		
		int[] indices = new int[] {
				0, 1, 3,
				3, 1, 2,
				4, 5, 6,
		};
		
		return EngineManager.createArrow(vertices, indices);
	}

}
