package assets.mesh;

import org.joml.Vector4f;

import assets.Asset;
import assets.AssetType;

public class Material extends Asset {
	
	Vector4f ambient = new Vector4f(1);
	Vector4f diffuse = new Vector4f(1);
	Vector4f specular = new Vector4f(1);
	
	float reflectivity = 1;
	float specularPower = 10;
	
	public Material() {
		super("Material", AssetType.Material);
	}
	
	public Material(String name) {
		super(name, AssetType.Material);
	}
	
	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getSpecularPower() {
		return specularPower;
	}

	public void setSpecularPower(float specularPower) {
		this.specularPower = specularPower;
	}

	@Override
	public void copy(Asset from) {
		if(from == null) return;
		if(!(from instanceof Material)) return;
		
		Material material = (Material) from;
		
		this.ambient = material.getAmbient();
		this.diffuse = material.getDiffuse();
		this.specular = material.getSpecular();
		this.reflectivity = material.getReflectivity();
		this.specularPower = material.getSpecularPower();
	}
	
}
