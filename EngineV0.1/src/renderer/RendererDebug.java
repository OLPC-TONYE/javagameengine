package renderer;


import static org.lwjgl.opengl.GL11.GL_LINES;
//import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDrawArrays;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import entities.Entity;
import entities.DebugDrawableObject;
import entitiesComponents.CameraComponent;

import opengl.Shader;
import opengl.VertexArrayObject;

public class RendererDebug {
	
	private Shader shader;
	
	public RendererDebug() {
		this.shader = new Shader("debug");
		shader.bindAttribute(0, "position");
		shader.link();
	}

	public void drawLines(Entity camera, VertexArrayObject lines, Vector3f colour) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", colour);
			lines.bind();
			shader.enableAttributeArray(0);
			glDrawArrays(GL_LINES, 0, lines.getCount());
			shader.disableAttributeArray(0);
			lines.unbind();
		shader.stop();
	}
	
	public void drawArrow(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = DebugDrawableObject.createArrow(0.08f, 0.5f);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", new Vector3f(1,0,0));
			arrow.bind();
			shader.enableAttributeArray(0);
			glDrawElements(GL_TRIANGLES, arrow.getCount(), GL_UNSIGNED_INT, 0);
			shader.disableAttributeArray(0);
			arrow.unbind();
		shader.stop();
	}
	
	public void draw3DArrow(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = DebugDrawableObject.create3DArrow(0.05f, 0.5f);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", new Vector3f(1,0,0));
			arrow.bind();
			shader.enableAttributeArray(0);
			GL11.glLineWidth(2.5f);
			glDrawArrays(GL_LINES, 0, 2);
			GL11.glLineWidth(1f);
			glDrawArrays(GL_TRIANGLES, 2, 3);
			shader.disableAttributeArray(0);
			arrow.unbind();
		shader.stop();
	}
	
	public void draw3DArrowCone(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = DebugDrawableObject.create3DArrowCone(0.05f, 0.5f);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", new Vector3f(1,0,0));
			arrow.bind();
			shader.enableAttributeArray(0);
			GL11.glLineWidth(2.5f);
			glDrawArrays(GL_LINES, 0, 2);
			GL11.glLineWidth(1f);
			glDrawArrays(GL_TRIANGLE_FAN, 2, arrow.getCount()-2);
			shader.disableAttributeArray(0);
			arrow.unbind();
		shader.stop();
	}

}
