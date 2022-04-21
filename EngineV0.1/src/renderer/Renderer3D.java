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

import org.joml.Vector3f;

import assets.light.Attenuation;
import assets.light.DirectionalLight;
import assets.light.LightFlags;
import assets.light.PointLight;
import assets.light.SpotLight;
import assets.mesh.Material;
import engine.EntityManager;
import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.LightingComponent;
import entitiesComponents.MeshRenderer;
import entitiesComponents.SpriteRenderer;
import entitiesComponents.Transform;
import opengl.Shader;
import opengl.VertexArrayObject;
import scenes.Scene;
import tools.Maths;

public class Renderer3D extends Renderer{

	@Override
	protected void prepare() {
		this.shader = new Shader("default");
		shader.bindAttribute(0, "position");
		shader.bindAttribute(1, "textureCords");
		shader.bindAttribute(2, "normals");
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
	
	protected void loadDirectionalLight(String uniformName, DirectionalLight light, Vector3f position) {
		shader.loadVector3(uniformName+".colour", light.getColour());
		shader.loadVector3(uniformName+".position", position);
		shader.loadVector3(uniformName+".direction", light.getDirection());
		shader.loadFloat(uniformName+".intensity", light.getIntensity());
	}
	
	protected void loadPointLight(String uniformName, PointLight light, Vector3f position) {
		shader.loadVector3(uniformName+".colour", light.getColour());
		shader.loadVector3(uniformName+".position", position);
		shader.loadFloat(uniformName+".intensity", light.getIntensity());
		loadAttenuation(uniformName+".att", light.getAttenuation());
	}
	
	protected void loadSpotLight(String uniformName, SpotLight light, Vector3f position) {
		shader.loadVector3(uniformName+".colour", light.getColour());
		shader.loadVector3(uniformName+".position", position);
		shader.loadVector3(uniformName+".direction", light.getDirection());
		shader.loadFloat(uniformName+".intensity", light.getIntensity());
		shader.loadFloat(uniformName+".cutOffAngle", light.getCutOffAngle());
		loadAttenuation(uniformName+".att", light.getAttenuation());
	}
	
	protected void loadAttenuation(String uniformName, Attenuation att) {
		shader.loadFloat(uniformName+".constant", att.getConstant());
		shader.loadFloat(uniformName+".linear", att.getExponent());
		shader.loadFloat(uniformName+".exponent", att.getLinear());
	}
	
	protected void loadMaterial(String uniformName, Material material) {
		shader.loadVector4(uniformName+".ambient", material.getAmbient());
		shader.loadVector4(uniformName+".diffuse", material.getDiffuse());
		shader.loadVector4(uniformName+".specular", material.getSpecular());
		
		shader.loadFloat(uniformName+".reflectivity", material.getReflectivity());
		shader.loadFloat(uniformName+".specularPower", material.getSpecularPower());
	}

	@Override
	public void render(Scene scene) {
		beginScene();
		
		CameraComponent inGameCamera = scene.main_camera.getComponent(CameraComponent.class);
		LightingComponent inGameLight = scene.main_light.getComponent(LightingComponent.class);
		
		shader.loadMatrix("projectionMatrix", inGameCamera.getProjectionMatrix());
		shader.loadMatrix("inverseViewMatrix", Maths.getInvertedMatrix(inGameCamera.getViewMatrix()));
		shader.loadMatrix("viewMatrix", inGameCamera.getViewMatrix());
		
		if(scene.useAmbient == true) {
			shader.loadFloat("ambientLightFactor", 0.7f);
		}
		
		if(inGameLight.getLight().getFlag() == LightFlags.Point) {
			loadPointLight("pointLight", (PointLight) inGameLight.getLight(), scene.main_light.getComponent(Transform.class).getPosition());	
		}else if(inGameLight.getLight().getFlag() == LightFlags.Directional){
			loadDirectionalLight("directionalLight", (DirectionalLight) inGameLight.getLight(), scene.main_light.getComponent(Transform.class).getPosition());
		}else {
			loadSpotLight("spotLight", (SpotLight) inGameLight.getLight(), scene.main_light.getComponent(Transform.class).getPosition());
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
				shader.loadVector3("colour", mesh.getColour());
				
				glBindTexture(GL_TEXTURE_2D, mesh.getTextureID());
				if(mesh.getMesh() != null) {
					if(mesh.getMesh().getVertexArray() != null ) {
						vao = mesh.getMesh().getVertexArray();
					}
					loadMaterial("material", mesh.getMesh().getMaterial());
				}
			}else if(entity.getComponent(SpriteRenderer.class)!= null) {
				SpriteRenderer sprite = entity.getComponent(SpriteRenderer.class);
				shader.loadVector3("colour", sprite.getColour());
							
				glBindTexture(GL_TEXTURE_2D, sprite.getTextureID());
				vao = sprite.getMesh();
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
		endScene();
	}
	
	private void prepareInstance(Entity entity) {
//		Load Entity Id
		

	}
}
