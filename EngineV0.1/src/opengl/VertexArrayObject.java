package opengl;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.HashMap;
import java.util.Map;

public class VertexArrayObject {
	
	private int vertexArrayId;
	private int vertexCount;
	private Map<String, VertexBufferObject> vertexBuffers = new HashMap<>();

	public VertexArrayObject() {
		vertexArrayId = glGenVertexArrays();
		glBindVertexArray(vertexArrayId);
	}
	
	public int getId() {
		return vertexArrayId;
	}
	
	public int getCount() {
		return vertexCount;
	}
	
	public void bind() {
		glBindVertexArray(vertexArrayId);
	}
	
	public void unbind() {
		glBindVertexArray(0);
	}
	
	public void addVertexBufferObject(String name, int[] indices) {
		addVertexBufferObject(name, new VertexBufferObject(indices));
		vertexCount = indices.length;
	}

	public void addVertexBufferObject(String name, int index, int size, float[] vertices) {
		addVertexBufferObject(name, new VertexBufferObject(index, size, vertices));
	}
	
	private void addVertexBufferObject(String name, VertexBufferObject vertexBuffer) {
		if(!vertexBuffers.containsKey(name)) {
			vertexBuffers.put(name, vertexBuffer);
		}	
	}
	
	public void modifyVertexBufferObject(String name, float[] data) {
		getVertexBufferObject(name).reloadDynamicDataInAttrib(data);
	}
	
	private VertexBufferObject getVertexBufferObject(String name) {
		if(vertexBuffers.containsKey(name)) {
			return this.vertexBuffers.get(name);
		}else {
			System.out.println("No Buffer Object Called "+name);
			return null;
		}
	}

}