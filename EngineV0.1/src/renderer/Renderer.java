package renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import org.joml.Vector3f;
import org.joml.Vector4f;

import assets.Material;
import components.LightingComponent;
import components.Transform;

import static org.lwjgl.opengl.GL11.*;

import opengl.Shader;
import scenes.Scene;

public abstract class Renderer {
	
	protected Shader shader;
	
	public Renderer() {
		prepare();
	}
	
	protected abstract void prepare();
	
	public abstract void render(Scene scene);
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearColour() {
		glClearColor(1, 1, 1, 1);
	}
	
	public void clearColour(float red, float green, float blue, float alpha) {
		glClearColor(red, green, blue, alpha);
	}
	
	public void clearColour(Vector4f colour) {
		glClearColor(colour.x, colour.y, colour.z, colour.w);
	}
	
	protected void loadDirectionalLight(String uniformName, LightingComponent light, Transform transform) {
		shader.loadVector3(uniformName+".colour", light.getColour());
		shader.loadVector3(uniformName+".position", transform.getPosition());
		shader.loadVector3(uniformName+".direction", transform.getDirection());
		shader.loadFloat(uniformName+".intensity", light.getIntensity());
	}
	
	protected void loadPointLight(String uniformName, LightingComponent light, Transform transform) {
		shader.loadVector3(uniformName+".colour", light.getColour());
		shader.loadVector3(uniformName+".position", transform.getPosition());
		shader.loadFloat(uniformName+".intensity", light.getIntensity());
		loadAttenuation(uniformName+".att", light.getAttenuation());
	}
	
	protected void loadEmptyLight(String uniformName) {
		shader.loadFloat(uniformName+".intensity", 0);
	}
	
	protected void loadSpotLight(String uniformName, LightingComponent light, Transform transform) {
		shader.loadVector3(uniformName+".colour", light.getColour());
		shader.loadVector3(uniformName+".position", transform.getPosition());
		shader.loadVector3(uniformName+".direction", transform.getDirection());
		shader.loadFloat(uniformName+".intensity", light.getIntensity());
		shader.loadFloat(uniformName+".cutOffAngle", light.getCutOffAngle());
		loadAttenuation(uniformName+".att", light.getAttenuation());
	}
	
	protected void loadAttenuation(String uniformName, Vector3f att) {
		shader.loadFloat(uniformName+".constant", att.x);
		shader.loadFloat(uniformName+".linear", att.y);
		shader.loadFloat(uniformName+".exponent", att.z);
	}
	
	protected void loadMaterial(String uniformName, Material material) {
		shader.loadVector3(uniformName+".ambient", material.getAmbient());
		shader.loadVector3(uniformName+".diffuse", material.getDiffuse());
		shader.loadVector3(uniformName+".specular", material.getSpecular());
		
		shader.loadFloat(uniformName+".reflectivity", material.getReflectivity());
		shader.loadFloat(uniformName+".specularPower", material.getSpecularPower());
	}

}
