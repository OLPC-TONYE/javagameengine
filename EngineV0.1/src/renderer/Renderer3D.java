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

import components.CameraComponent;
import components.LightType;
import components.LightingComponent;
import components.MeshRenderer;
import components.SpriteRenderer;
import components.Transform;
import entities.Entity;
import managers.EntityManager;
import opengl.Shader;
import opengl.VertexArrayObject;
import scenes.Scene;
import tools.Maths;

public class Renderer3D extends Renderer{

	private static final int MAX_POINTLIGHTS = 5;
	private static final int MAX_SPOTLIGHTS = 5;

	@Override
	protected void prepare() {		
		this.shader = new Shader("default");
		shader.bindAttribute(0, "position");
		shader.bindAttribute(1, "textureCords");
		shader.bindAttribute(2, "normals");
		shader.link();
	}
	
	@Override
	public void render(Scene scene) {
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_DEPTH_TEST);
		
		CameraComponent inGameCamera = scene.primaryCamera.getComponent(CameraComponent.class);	
		
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("inverseViewMatrix", Maths.getInvertedMatrix(inGameCamera.getViewMatrix()));
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		
		if(scene.useAmbient == true) {
			shader.loadFloat("ambientLightFactor", 1f);
		}else {
			shader.loadFloat("ambientLightFactor", 0.1f);
		}
		
		int pointLightsCounter = 0;
		int spotLightsCounter = 0;
		
		for(Entity light: scene.lights) {
			
			LightingComponent inGameLight = light.getComponent(LightingComponent.class);
			Transform lightTransform = light.getComponent(Transform.class);
			
			if(inGameLight.getFlag() == LightType.Point) {
				if(pointLightsCounter >= MAX_POINTLIGHTS) {
					continue;
				}	
				loadPointLight("pointLight["+pointLightsCounter+"]", inGameLight, lightTransform);
				pointLightsCounter++;
			}else if(inGameLight.getFlag() == LightType.Spot){
				if(spotLightsCounter >= MAX_SPOTLIGHTS) {
					continue;
				}
				loadSpotLight("spotLight["+spotLightsCounter+"]", inGameLight, lightTransform);
				spotLightsCounter++;
			}else {
				loadDirectionalLight("directionalLight", inGameLight, lightTransform);				
			}
			
		}
		
		// If less than MAX amount, fill remaining array index with empty light (i.e has no intensity)
		for(int i=pointLightsCounter; pointLightsCounter < MAX_POINTLIGHTS; i++) {
			loadEmptyLight("pointLight["+i+"]");
			pointLightsCounter++;
		}
		
		// If less than MAX amount, fill remaining array index with empty light (i.e has no intensity)
		for(int i=spotLightsCounter; spotLightsCounter < MAX_SPOTLIGHTS; i++) {
			loadEmptyLight("spotLight["+i+"]");
			spotLightsCounter++;
		}
		
		
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
			
			if(entity.getComponent(MeshRenderer.class)!= null) {
				MeshRenderer mesh = entity.getComponent(MeshRenderer.class);
				if(mesh.getMesh() != null) {								
					glBindTexture(GL_TEXTURE_2D, mesh.getTextureID());
					if(mesh.getMesh().getVertexArray() != null ) {
						vao = mesh.getMesh().getVertexArray();
					}
					loadMaterial("material", mesh.getMaterial());
					shader.loadVector3("ambientColour", mesh.getColour());
				}
			}else if(entity.getComponent(SpriteRenderer.class)!= null) {
				// Add Mesh To SpriteRenderer 
				SpriteRenderer sprite = entity.getComponent(SpriteRenderer.class);
				
				if(sprite.getSprite() != null && sprite.getMesh() != null) {
					glBindTexture(GL_TEXTURE_2D, sprite.getTextureID());
					if(sprite.getMesh().getVertexArray() != null ) {
						vao = sprite.getMesh().getVertexArray();
					}
					loadMaterial("material", sprite.getMaterial());
					shader.loadVector3("ambientColour", sprite.getSpriteColour());
				}
			}
				
			if(vao != null) {
				vao.bind();
				glEnableVertexAttribArray(0);
				glEnableVertexAttribArray(1);
				glEnableVertexAttribArray(2);
				glActiveTexture(GL_TEXTURE0);
				
				glDrawElements(GL_TRIANGLES, vao.getCount(), GL_UNSIGNED_INT, 0);
				
				glDisableVertexAttribArray(0);
				glDisableVertexAttribArray(1);
				glDisableVertexAttribArray(2);
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, 0);
				vao.unbind();
			}
			
		}
		shader.stop();
	}
	
	private void prepareInstance(Entity entity) {
//		Load Entity Id
		

	}
}
