package opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import managers.EngineManager;

enum VertexBufferType {
	INDICES_BUFFER,
	VERTEX_BUFFER,
}

public class VertexBufferObject {
	
	private int vertexBufferId;
	private int size;
	private VertexBufferType type;
	
	public VertexBufferObject(int[] indices) {
		vertexBufferId = glGenBuffers();
		storeIndicesBuffer(indices);
	}
	
	public VertexBufferObject(int index, int size, float[] data) {
		vertexBufferId = glGenBuffers();
		storeStaticDataInAttrrib(index, size, data);
	}
	
	public int getId() {
		return vertexBufferId;
	}
	
	public int getSize() {
		return size;
	}
	
	public VertexBufferType getType() {
		return type;
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
	}
	
	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void reloadDynamicDataInAttrib(float[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		FloatBuffer buffer = storeInFloatBuffer(data);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void storeIndicesBuffer(int[] indices) {
		type = VertexBufferType.INDICES_BUFFER;
		EngineManager.addVBOS(vertexBufferId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBufferId);
		IntBuffer buffer = storeInIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}
	
	private void storeStaticDataInAttrrib(int index, int size, float[] data) {
		this.type = VertexBufferType.VERTEX_BUFFER;
		this.size = size;
		EngineManager.addVBOS(vertexBufferId);
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		FloatBuffer buffer = storeInFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	private static FloatBuffer storeInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data).flip();
		return buffer;
	}
	
	private static IntBuffer storeInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data).flip();
		return buffer;
	}

}
