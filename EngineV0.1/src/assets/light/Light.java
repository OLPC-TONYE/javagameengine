package assets.light;

import org.joml.Vector3f;

import assets.Asset;
import assets.AssetType;

public abstract class Light extends Asset {

	float intensity = 0.6f;
	Vector3f colour = new Vector3f(1);
	LightFlags flag;
	
	/**
	 * @param colour
	 * @param intensity
	 * @param attenuation
	 */
	public Light() {
		super("Light", AssetType.Light);

	}
	
	public Light(String tag) {
		super(tag, AssetType.Light);
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

	public LightFlags getFlag() {
		return flag;
	}

	public void setFlag(LightFlags flag) {
		this.flag = flag;
	}
	
}
