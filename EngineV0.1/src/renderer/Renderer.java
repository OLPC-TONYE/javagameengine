package renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import org.joml.Vector3f;

import assets.light.Attenuation;
import assets.light.DirectionalLight;
import assets.light.PointLight;
import assets.light.SpotLight;
import assets.mesh.Material;

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
	
	protected void loadEmptyLight(String uniformName) {
		shader.loadFloat(uniformName+".intensity", 0);
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
}
