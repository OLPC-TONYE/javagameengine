package assets.light;

import org.joml.Vector3f;

import assets.Asset;
import assets.AssetType;

public abstract class Light extends Asset {

	float intensity;
	Vector3f colour = new Vector3f(1);
	
	/**
	 * @param colour
	 * @param intensity
	 * @param attenuation
	 */
	public Light(Vector3f colour, float intensity) {
		super("Light", AssetType.Light);
		this.colour = colour;
		this.intensity = intensity;
	}
	
	public Light(String tag, Vector3f colour, float intensity) {
		super(tag, AssetType.Light);
		this.colour = colour;
		this.intensity = intensity;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}

}
