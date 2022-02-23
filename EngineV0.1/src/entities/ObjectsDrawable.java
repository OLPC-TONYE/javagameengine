package entities;

import org.joml.Vector3f;

import engine.EngineManager;
import opengl.VertexArrayObject;

public class ObjectsDrawable {
	
	class Line {
		
		VertexArrayObject vao;
		Vector3f colour;
		
		public void create(Vector3f from, Vector3f to) {
			float[] position = new float[] {from.x, from.y, from.z, to.x, to.y, to.z};
			vao = EngineManager.createLine(position);
			colour = new Vector3f(1,1,1);
		}
		
		
		public void create(Vector3f from, Vector3f to, Vector3f colour) {
			float[] position = new float[] {from.x, from.y, from.z, to.x, to.y, to.z};
			this.colour = colour;
			vao = EngineManager.createLine(position);
		}
	}

}
