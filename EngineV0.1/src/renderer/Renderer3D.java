package renderer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import engine.EntityManager;
import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.MeshComponent;
import entitiesComponents.SpriteRenderer;
import entitiesComponents.Transform;
import opengl.Shader;
import opengl.VertexArrayObject;
import scenes.Scene;

public class Renderer3D extends Renderer{

	@Override
	protected void prepare() {
		this.shader = new Shader("default");
		shader.bindAttribute(0, "position");
		shader.bindAttribute(1, "textureCords");
		shader.link();
	}
	
	@Override
	protected void beginScene() {
		// TODO Auto-generated method stub
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	protected void endScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Scene scene) {
		beginScene();
		CameraComponent inGameCamera = scene.main_camera.getComponent(CameraComponent.class);
		
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		
		shader.start();
		for(Entity entity: scene.renderList) {
			prepareInstance(entity);
			
			if(entity.isCamera()) {
				continue;
			}
			shader.loadFloat("entityId", EntityManager.getId(entity.getName()));
			
			Transform transformation = entity.getComponent(Transform.class);
			shader.loadMatrix("transformationMatrix", transformation.getTransformationMatrix());
			
			VertexArrayObject vao = null;
			
			if(entity.getComponent(MeshComponent.class)!= null) {
				MeshComponent mesh = entity.getComponent(MeshComponent.class);
				shader.loadVector3("colour", mesh.getColour());
				
				glBindTexture(GL_TEXTURE_2D, mesh.getTextureID());
				vao = mesh.getMesh();
			}else if(entity.getComponent(SpriteRenderer.class)!= null) {
				SpriteRenderer sprite = entity.getComponent(SpriteRenderer.class);
				shader.loadVector3("colour", sprite.getColour());
							
				glBindTexture(GL_TEXTURE_2D, sprite.getTextureID());
				vao = sprite.getMesh();
			}
					
			vao.bind();
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glActiveTexture(GL_TEXTURE0);
			
			glDrawElements(GL_TRIANGLES, vao.getCount(), GL_UNSIGNED_INT, 0);
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);
			vao.unbind();
		}
		shader.stop();
		endScene();
	}
	
	private void prepareInstance(Entity entity) {
//		Load Entity Id
		

	}
}
