package assets.light;

import org.joml.Vector3f;

public class PointLight extends Light {
	
	Attenuation attenuation;

	/**
	 * @param colour
	 * @param direction
	 * @param intensity
	 * @param attenuation
	 */
	public PointLight(Vector3f colour, Vector3f direction, float intensity, Attenuation attenuation) {
		super(colour, intensity);
		this.attenuation = attenuation;
	}

	/**
	 * @param tag
	 * @param colour
	 * @param direction
	 * @param intensity
	 * @param attenuation
	 */
	public PointLight(String tag, Vector3f colour, Vector3f direction, float intensity, Attenuation attenuation) {
		super(tag, colour, intensity);
		this.attenuation = attenuation;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}
}
