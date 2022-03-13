package gui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
//import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glLineWidth;
import org.joml.Vector3f;

import entities.DebugDrawableObject;
import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.Transform;
import maths.Maths;
import opengl.Shader;
import opengl.VertexArrayObject;

public class Guizmos
{
	
	Vector3f global_position;
	Vector3f world_position;
	
	Vector3f global_rotation;
	Vector3f world_rotation;
	
	Shader shader;
	
	public void init() {
		shader = new Shader("guizmo");
		shader.bindAttribute(0, "position");
		shader.link();
		
		world_position = new Vector3f(0, 0, 13);
		world_rotation = new Vector3f(0, 45, 0);
	}
	
	public void attachTo(Entity entity) {
		Transform entity_transform = entity.getComponent(Transform.class);	
		world_position = entity_transform.getPosition();
		world_rotation = entity_transform.getRotation();
	}

	public void render(Entity camera) {
		
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		VertexArrayObject arrows = DebugDrawableObject.create3DArrowCone(0.03f, 0.5f);	
		
		Vector3f scale = new Vector3f(1, 1, 1);
		
		shader.start();
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		
		arrows.bind();
		shader.enableAttributeArray(0);
		
		shader.loadVector3("colour", new Vector3f(0,0,1));
		shader.loadMatrix("transformationMatrix", Maths.getTransformationMatrix(world_position, world_rotation, scale).translate(0.26f, 0, 0.03f));
		drawArrow(arrows);
		
		shader.loadVector3("colour", new Vector3f(0,1,0));
		shader.loadMatrix("transformationMatrix", Maths.getTransformationMatrix(world_position, world_rotation, scale).translate(0, 0.26f, 0.03f).rotateZ((float) Math.toRadians(90)));	
		drawArrow(arrows);
		
		shader.loadVector3("colour", new Vector3f(1,0,0));
		shader.loadMatrix("transformationMatrix", Maths.getTransformationMatrix(world_position, world_rotation, scale).translate(0, 0, 0.27f).rotateY((float) Math.toRadians(-90)));
		drawArrow(arrows);
		
		shader.disableAttributeArray(0);
		arrows.unbind();
		
		shader.stop();
	}
	
	private void drawArrow(VertexArrayObject arrows) {
		glClear(GL_DEPTH_BUFFER_BIT);
		glLineWidth(2.5f);
		glDrawArrays(GL_LINES, 0, 2);
		glLineWidth(1f);
		glDrawArrays(GL_TRIANGLE_FAN, 2, arrows.getCount()-2);
	}
}
