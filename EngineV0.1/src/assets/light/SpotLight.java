package assets.light;

import org.joml.Vector3f;

public class SpotLight extends Light {
	
	float cutOffAngle;
	Vector3f direction;
	Attenuation attenuation;
	/**
	 * @param colour
	 * @param intensity
	 * @param direction
	 * @param cutOffAngle
	 * @param attenuation
	 */
	public SpotLight(Vector3f colour,  Vector3f direction, float intensity, float cutOffAngle, Attenuation attenuation) {
		super(colour, intensity);
		this.direction = direction;
		this.cutOffAngle = cutOffAngle;
		this.attenuation = attenuation;
	}
	
	/**
	 * @param tag
	 * @param colour
	 * @param intensity
	 * @param direction
	 * @param cutOffAngle
	 * @param attenuation
	 */
	public SpotLight(String tag, Vector3f colour, Vector3f direction, float intensity, float cutOffAngle, Attenuation attenuation) {
		super(tag, colour, intensity);
		this.direction = direction;
		this.cutOffAngle = cutOffAngle;
		this.attenuation = attenuation;
	}

	public float getCutOffAngle() {
		return cutOffAngle;
	}

	public void setCutOffAngle(float cutOffAngle) {
		this.cutOffAngle = cutOffAngle;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}
	
}
