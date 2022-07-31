package gui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
//import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glLineWidth;
import org.joml.Vector3f;

import components.CameraComponent;
import components.Transform;
import entities.Drawable;
import entities.Entity;
import listeners.MouseListener;
import opengl.Shader;
import opengl.VertexArrayObject;
import tools.Maths;

public class Guizmos
{
	
	Vector3f global_position;
	Vector3f world_position;
	
	Vector3f global_rotation;
	Vector3f world_rotation;
	
	Vector3f x_colour;
	Vector3f y_colour;
	Vector3f z_colour;
	
	Shader shader;
	
	int operation = 0;
	
	boolean attached;
	boolean isUsing;
	boolean[] active = new boolean[3];
	
	Entity attachedEntity;
	
	public void init() {
		shader = new Shader("guizmo");
		shader.bindAttribute(0, "position");
		shader.link();
		
		world_position = new Vector3f(0, 0, 13);
		world_rotation = new Vector3f(0, 45, 0);
	}
	
	public void attachTo(Entity entity) {
		if(attachedEntity != null) {
			if(attachedEntity == entity) {
				changeOperation();
			}
		}
		attachedEntity = entity;
		Transform entity_transform = entity.getComponent(Transform.class);	
		world_position = entity_transform.getPosition();
		world_rotation = entity_transform.getRotation();
		attached = true;
	}
	
	private void changeOperation() {
		switch(operation) {
			case 0:
				operation = 1;
				break;
			case 1:
				operation = 2;
				break;
			case 2:
				operation = 0;
				break;
		}
	}

	public void setHover(int axis) {
		this.active = new boolean[3];
		this.active[axis] = true;
	}
	
	public boolean[] isMouseOver() {
		return this.active;
	}
	
	public void dettach() {
		attached = false;
		attachedEntity = null;
	}
	
	public void update(double dt, float[] guizmo_pixelData) {
		
		if(MouseListener.isDragging() & active[0] & !active[1] & !active[2]) {
			this.isUsing = true;
			Transform entity_transform = attachedEntity.getComponent(Transform.class);
			if(operation == 0) {
				entity_transform.translateZ(dt * MouseListener.getDx());
			}else if(operation == 2) {
				entity_transform.getScale().z += dt * 0.35f * MouseListener.getDx();
			}		
		}
		if(MouseListener.isDragging() & !active[0] & active[1] & !active[2]) {
			this.isUsing = true;
			Transform entity_transform = attachedEntity.getComponent(Transform.class);
			if(operation == 0) {
				entity_transform.translateY(dt * MouseListener.getDy());
			}else if(operation == 2) {
				entity_transform.getScale().y += dt * 0.35f * MouseListener.getDy();
			}
						
		}
		if(MouseListener.isDragging() & !active[0] & !active[1] & active[2]) {
			this.isUsing = true;
			Transform entity_transform = attachedEntity.getComponent(Transform.class);
			if(operation == 0) {
				entity_transform.translateX(dt * -MouseListener.getDx());
			}else if(operation == 2) {
				entity_transform.getScale().x += dt * 0.35f * MouseListener.getDx();
			}
					
		}
		
		if(MouseListener.mouseButtonDown(0) & guizmo_pixelData[0] >= 1) {
			if(!isUsing) setHover(0);
		}else if(MouseListener.mouseButtonDown(0) & guizmo_pixelData[1] >= 1) {
			if(!isUsing) setHover(1);
		}else if(MouseListener.mouseButtonDown(0) & guizmo_pixelData[2] >= 1) {
			if(!isUsing) setHover(2);
		}	
		
		if(MouseListener.mouseButtonDown(0) != true) {
			isUsing = false;
			active = new boolean[3];
		}
	}

	public void render(Entity camera) {
		
		if (attached) {
			CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
			
			VertexArrayObject arrows = Drawable.create3DArrowCone(0.03f, 0.5f);
			if(operation == 2) {
				arrows = Drawable.create3DArrowCube(0.03f, 0.5f);
			}
			
			Vector3f scale = new Vector3f(1, 1, 1);
			shader.start();
			shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
			shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
			arrows.bind();
			shader.enableAttributeArray(0);
			shader.loadVector3("colour", new Vector3f(0, 0, 1));
			shader.loadMatrix("transformationMatrix",
					Maths.getTransformationMatrix(world_position, world_rotation, scale).translate(0.26f, 0, 0.03f));
			drawArrow(arrows);
			shader.loadVector3("colour", new Vector3f(0, 1, 0));
			shader.loadMatrix("transformationMatrix",
					Maths.getTransformationMatrix(world_position, world_rotation, scale).translate(0, 0.26f, 0.03f)
							.rotateZ((float) Math.toRadians(90)));
			drawArrow(arrows);
			shader.loadVector3("colour", new Vector3f(1, 0, 0));
			shader.loadMatrix("transformationMatrix",
					Maths.getTransformationMatrix(world_position, world_rotation, scale).translate(0, 0, 0.27f)
							.rotateY((float) Math.toRadians(-90)));
			drawArrow(arrows);
			shader.disableAttributeArray(0);
			arrows.unbind();
			shader.stop();
		}
	}
	
	private void drawArrow(VertexArrayObject arrows) {
		glClear(GL_DEPTH_BUFFER_BIT);
		glLineWidth(4.5f);
		glDrawArrays(GL_LINES, 0, 2);
		glLineWidth(1f);
		glDrawArrays(GL_TRIANGLE_FAN, 2, arrows.getCount()-2);
	}
}
