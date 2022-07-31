package renderer;


import static org.lwjgl.opengl.GL11.GL_BACK;
//import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
//import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL20.glDrawArrays;


import org.joml.Vector3f;

import components.CameraComponent;
import components.Transform;
import entities.Entity;
import entities.Drawable;
import managers.EntityManager;
import opengl.Shader;
import opengl.VertexArrayObject;
import scenes.Scene;
import tools.Maths;

public class RendererDebug extends Renderer{
	
	private Shader shader;
	
	public void drawLines(Entity camera, VertexArrayObject lines, Vector3f colour, Vector3f position, Vector3f rotation, Vector3f scale) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		shader.start();
		shader.loadMatrix("transformationMatrix", Maths.getTransformationMatrix(position, rotation, scale));
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", colour);
//		shader.loadFloat("entityId", 0);
		shader.loadFloat("selectable", 0);
			lines.bind();
			shader.enableAttributeArray(0);	
			glLineWidth(1.7f);
			glDrawArrays(GL_LINES, 0, lines.getCount());
			glLineWidth(1f);
			shader.disableAttributeArray(0);
			lines.unbind();
		shader.stop();
	}
	
	public void drawArrow(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = Drawable.createArrow(0.08f, 0.5f);
		
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
	
	public void drawArrowSquare(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = Drawable.createArrowSquare(0.08f, 0.5f);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", new Vector3f(1,0,0));
			arrow.bind();
			glLineWidth(2.5f);
			glDrawArrays(GL_LINES, 0, 2);
			glLineWidth(1f);
			glDrawArrays(GL_TRIANGLES, 2, arrow.getCount()-2);
			arrow.unbind();
		shader.stop();
	}
	
	public void draw3DArrowCone(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = Drawable.create3DArrowCone(0.05f, 0.5f);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", new Vector3f(1,0,0));
			arrow.bind();
			shader.enableAttributeArray(0);
			glLineWidth(2.5f);
			glDrawArrays(GL_LINES, 0, 2);
			glLineWidth(1f);
			glDrawArrays(GL_TRIANGLE_FAN, 2, arrow.getCount()-2);
			shader.disableAttributeArray(0);
			arrow.unbind();
		shader.stop();
	}
	
	public void draw3DArrowCube(Entity camera) {
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrow = Drawable.create3DArrowCube(0.05f, 0.5f);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		shader.loadVector3("colour", new Vector3f(1,0,0));
			arrow.bind();
			shader.enableAttributeArray(0);
			glLineWidth(2.5f);
			glDrawArrays(GL_LINES, 0, 2);
			glLineWidth(1f);
			glDrawArrays(GL_TRIANGLES, 2, arrow.getCount()-2);
			shader.disableAttributeArray(0);
			arrow.unbind();
		shader.stop();
	}
	
	private void draw3DCamera() {
		VertexArrayObject camera3d = Drawable.create3DCamera();
		camera3d.bind();
		shader.enableAttributeArray(0);
		
		glDrawArrays(GL_TRIANGLES, 0, 3);
		
//		Set To Polygon To Lines
		glPolygonMode(GL_FRONT, GL_LINE);
		glPolygonMode(GL_BACK, GL_LINE);
		
		glDrawElements(GL_TRIANGLES, camera3d.getCount(), GL_UNSIGNED_INT, 0);
		
//		Reset To Normal
		glPolygonMode(GL_FRONT, GL_FILL);
		glPolygonMode(GL_BACK, GL_FILL);
		
		shader.disableAttributeArray(0);
		camera3d.unbind();
	}

	@Override
	protected void prepare() {
		this.shader = new Shader("debug");
		shader.bindAttribute(0, "position");
		shader.link();
	}
	
	protected void beginScene() {
		// TODO Auto-generated method stub
		glEnable(GL_LINE_SMOOTH);
	}

	protected void endScene() {
		// TODO Auto-generated method stub
		glDisable(GL_LINE_SMOOTH);
	}

	@Override
	public void render(Scene scene) {
		beginScene();
		CameraComponent inGameCamera = scene.primaryCamera.getComponent(CameraComponent.class);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		for(Entity entity: scene.renderList) {
			
			if(!entity.isCamera()) {
				continue;
			}
			
			Transform transformation = entity.getComponent(Transform.class);					
			shader.loadMatrix("transformationMatrix", transformation.getTransformationMatrix());
			shader.loadVector3("colour", new Vector3f(1,1,1));	
			shader.loadFloat("entityId", EntityManager.getId(entity.getName()));
			shader.loadFloat("selectable", 1);
			draw3DCamera();
			
		}
		shader.stop();
		endScene();
	}

}
