package assets;

import org.joml.Vector3f;

public class Material extends Asset {
	
	Vector3f ambient = new Vector3f(1);
	Vector3f diffuse = new Vector3f(1);
	Vector3f specular = new Vector3f(1);
	
	float reflectivity = 1;
	float specularPower = 10;
	
	public Material() {
		super("Material", AssetType.Material);
	}
	
	public Material(String name) {
		super(name, AssetType.Material);
	}

	/**
	 * @return the ambient
	 */
	public Vector3f getAmbient() {
		return ambient;
	}

	/**
	 * @param ambient the ambient to set
	 */
	public void setAmbient(Vector3f ambient) {
		this.ambient = ambient;
	}

	/**
	 * @return the diffuse
	 */
	public Vector3f getDiffuse() {
		return diffuse;
	}

	/**
	 * @param diffuse the diffuse to set
	 */
	public void setDiffuse(Vector3f diffuse) {
		this.diffuse = diffuse;
	}

	/**
	 * @return the specular
	 */
	public Vector3f getSpecular() {
		return specular;
	}

	/**
	 * @param specular the specular to set
	 */
	public void setSpecular(Vector3f specular) {
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
	public Material copy(Asset from) {
		if(from == null) return null;
		if(!(from instanceof Material)) return null;
		
		Material material = (Material) from;
		
		this.ambient = material.getAmbient();
		this.diffuse = material.getDiffuse();
		this.specular = material.getSpecular();
		this.reflectivity = material.getReflectivity();
		this.specularPower = material.getSpecularPower();
		return this;
	}
	
}
