package entities;

import managers.EngineManager;
import opengl.VertexArrayObject;
import tools.ListofFloats;
	

public class Drawable {
	
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
		
		ListofFloats positions = new ListofFloats();
		for(int iLine = (int) (from-fExtent); iLine <= (from+fExtent); iLine += fStep) {
			// Draw Z lines
			float[] verticesZ = new float[]{iLine, y, fExtent, iLine, y, -fExtent};
			positions.add(verticesZ);
		
			float[] verticesX = new float[]{fExtent, y, iLine, -fExtent, y, iLine};
			positions.add(verticesX);
		}
		
		return EngineManager.loadToVAO(positions.toArray());
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
		
		return EngineManager.loadToVAO(vertices, indices);
	}
	
	public static VertexArrayObject createArrowSquare(float height, float length) {
		
		float w = length/2;			// Length of Arrow
		float h = w - height;	// Arrow Head
		
		float[] vertices = new float[] {
			-w, 0, 0,			// 	0 Arrow Body
			w, 0, 0,			//	1 Arrow Body
			(h), height, 0,		//	2 Arrow Head
			(h), -height, 0,		//	3 Arrow Head
			(h+0.05f), height, 0,		//	4 Arrow Head
			(h), -height, 0,		//	2 Arrow Head
			(h+0.05f), -height, 0,		//	4 Arrow Head
			(h+0.05f), height, 0,		//	3 Arrow Head		
		};
		
		return EngineManager.loadToVAO(vertices);
	}

	
	/**
	 * 
	 * @param height
	 *          the height of the arrow
	 * @param length
	 *          the length of the body
	 *          
	 * @return <code>VertexArrayObject</code>
	 */
	public static VertexArrayObject create3DArrowCone(float height, float length) {
		
		float w = length/2;			// Length of Arrow
		float h = w - height;	// Arrow Head
		
		float angle = 10f;
		
		ListofFloats vertices = new ListofFloats();
		
		vertices.add(new float[] {
				-w, 0, 0,			// 	0 Arrow Body
				w, 0, 0,			//	1 Arrow Body
				(w+0.02f), 0, 0,		//	3 Arrow Head
		});
		
		float GL_PI = 3.142f;
		for(angle = 0.0f; angle < (2.0f*GL_PI ); angle += (GL_PI/8.0f)) {
			vertices.add(new float[] {
					h, (float) (0.05f*Math.sin(angle)), (float) (0.05f*Math.cos(angle)), //
			});
		}
		
		return EngineManager.loadToVAO(vertices.toArray());
	}
	
	public static VertexArrayObject create3DArrowCube(float height, float length) {
		
		float w = length/2;			// Length of Arrow
		float h = w - height;	// Arrow Head
		float h2 = h/2;
		
		float[] vertices = new float[] {
			-w, 0, 0,			// 	0 Arrow Body
			w, 0, 0,			//	1 Arrow Body
			
			(h), height, height,		//	2 Arrow Head
			(h), -height, height,		//	3 Arrow Head
			(h+h2), height, height,		//	4 Arrow Head
			
			(h), -height, height,		//	2 Arrow Head
			(h+h2), -height, height,		//	4 Arrow Head
			(h+h2), height, height,		//	3 Arrow Head	
//			=======================
			(h), height, -height,		//	2 Arrow Head
			(h), -height, -height,		//	3 Arrow Head
			(h+h2), height, -height,		//	4 Arrow Head
			
			(h), -height, -height,		//	2 Arrow Head
			(h+h2), -height, -height,		//	4 Arrow Head
			(h+h2), height, -height,		//	3 Arrow Head
//			==========================
			(h+h2), height, height,		//	2 Arrow Head
			(h+h2), -height, height,		//	3 Arrow Head
			(h+h2), height, -height,		//	4 Arrow Head
			
			(h+h2), -height, height,		//	2 Arrow Head
			(h+h2), -height, -height,		//	4 Arrow Head
			(h+h2), height, -height,		//	3 Arrow Head
//			========================
			(h), height, height,		//	2 Arrow Head
			(h), -height, height,		//	3 Arrow Head
			(h), height, -height,		//	4 Arrow Head
			
			(h), -height, height,		//	2 Arrow Head
			(h), -height, -height,		//	4 Arrow Head
			(h), height, -height,		//	3 Arrow Head
//			========================
			(h), -height, height,		//	2 Arrow Head
			(h+h2), -height, height,		//	3 Arrow Head
			(h), -height, -height,		//	4 Arrow Head
			
			(h+h2), -height, height,		//	2 Arrow Head
			(h+h2), -height, -height,		//	4 Arrow Head
			(h), -height, -height,		//	3 Arrow Head
//			========================
			(h), height, height,		//	2 Arrow Head
			(h+h2), height, height,		//	3 Arrow Head
			(h), height, -height,		//	4 Arrow Head
			
			(h+h2), height, height,		//	2 Arrow Head
			(h+h2), height, -height,		//	4 Arrow Head
			(h), height, -height,		//	3 Arrow Head
		};
		
		return EngineManager.loadToVAO(vertices);
	}
	
	public static VertexArrayObject create3DCamera() {
		
		float[] positions = new float[] {
				/*0*/0.3f,0.3f,-0.25f, /*1*/-0.3f,0.3f,-0.25f, /*2*/0.0f,0.45f,-0.25f,
				/*3*/0.0f,0.0f,0.0f, 
				/*4*/0.3f,0.25f,-0.25f, 
				/*5*/0.3f,-0.25f,-0.25f,
				/*6*/-0.3f,-0.25f,-0.25f,
				/*7*/-0.3f,0.25f,-0.25f,	
		};
		
		int[] indices = new int[] {
				0,2,1,  3,5,4, 3,6,5, 3,7,6, 3,4,7
		};
		
		return EngineManager.loadToVAO(positions, indices);
	}
	
}
