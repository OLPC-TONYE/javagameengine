package renderer;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL20.glDrawArrays;

import java.util.ArrayList;
import org.joml.Vector3f;
import entities.Entity;
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
	
	public void render(Entity camera, ArrayList<VertexArrayObject> lines) {
		
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		shader.start();
		
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		
		shader.loadVector("colour", new Vector3f(1,1,1));
		
		
		for(VertexArrayObject line: lines) {
			line.bind();
			
			shader.enableAttributeArray(0);
			
			glDrawArrays(GL_LINES, 0, 2);
			
			shader.disableAttributeArray(0);
			
			line.unbind();
		}
	
		shader.stop();
	}
}
