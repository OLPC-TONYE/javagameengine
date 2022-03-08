package renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import org.lwjgl.opengl.GL11;

import engine.EntityManager;
import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.MeshRenderer;
import entitiesComponents.SpriteRenderer;
import entitiesComponents.TextureComponent;
import entitiesComponents.Transform;
import opengl.Shader;
import opengl.VertexArrayObject;

public class Renderer {
	
	private Shader shader;
	
	public Renderer() {
		this.shader = new Shader("default");
		shader.bindAttribute(0, "position");
		shader.bindAttribute(1, "textureCords");
		shader.link();
	}
	
	public void clearColour() {
		GL11.glClear(GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(1, 1, 1, 1);
	}
	
	public void clearColour(float red, float green, float blue, float alpha) {
		GL11.glClear(GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(red, green, blue, alpha);
	}
	
	public void render(Entity camera, Entity entity) {
		
		CameraComponent inGameCamera = camera.getComponent(CameraComponent.class);
		
		MeshRenderer meshr;
		SpriteRenderer sprite;
		VertexArrayObject mesh = null;
		
		shader.start();
		
		shader.loadFloat("entityId", EntityManager.getId(entity.getName()));
	
		Transform transformation = entity.getComponent(Transform.class);
		shader.loadMatrix("transformationMatrix", transformation.getTransformationMatrix());
		
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		
		if(entity.getComponent(MeshRenderer.class)!= null) {
			meshr = entity.getComponent(MeshRenderer.class);
			mesh = meshr.getMesh();
			
			shader.loadVector3("colour", meshr.getColour());
		}else if(entity.getComponent(SpriteRenderer.class)!= null) {
			sprite = entity.getComponent(SpriteRenderer.class);
			mesh = sprite.getMesh();
			
			shader.loadVector3("colour", sprite.getSprite().getColour());
		}
		
		shader.loadBoolean("hasTexture", entity.getComponent(TextureComponent.class)!= null);
		
		mesh.bind();
		glEnableVertexAttribArray(0);
		
		if(entity.getComponent(TextureComponent.class)!= null) {
			
			glEnableVertexAttribArray(1);
			
			TextureComponent texture = entity.getComponent(TextureComponent.class);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		}
		
		glDrawElements(GL_TRIANGLES, mesh.getCount(), GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		
		if(entity.getComponent(TextureComponent.class)!= null) {
			glDisableVertexAttribArray(1);
			
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		
		mesh.unbind();
		
		shader.stop();
	}

}
