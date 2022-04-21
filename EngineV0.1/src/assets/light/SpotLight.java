package assets.light;

import org.joml.Vector3f;

public class SpotLight extends Light {
	
	float cutOffAngle = (float) Math.toRadians(60.0f);
	Vector3f direction = new Vector3f(0, 0, -1);
	Attenuation attenuation = new Attenuation(1, 0.01f, 0.02f);

	/**
	 * Spot Light
	 * 
	 * <p>
	 * A cone shaped light pointed in a direction
	 */
	public SpotLight() {
		super();
		flag = LightFlags.Spot;
	}
	
	/**
	 * Spot  Light
	 * 
	 * <p>
	 * A cone shaped light pointed in a direction
	 * @param tag name of Entity
	 */
	public SpotLight(String tag) {
		super(tag);
		flag = LightFlags.Spot;
	}

	/**
	 * Returns the {@code cutOffAngle} (in radians)
	 * @returns cutOffAngle
	 */
	public float getCutOffAngle() {
		return cutOffAngle;
	}

	/**
	 * Sets the {@code cutOffAngle} (in radians)
	 * @param cutOffAngle cutoff angle of spot light
	 */
	public void setCutOffAngle(float cutOffAngle) {
		this.cutOffAngle = cutOffAngle;
	}

	/**
	 * Returns the {@code direction} of the light
	 * @returns cutOffAngle
	 */
	public Vector3f getDirection() {
		return direction;
	}

	/**
	 * Sets the {@code direction} of the light
	 * @param  direction 
	 */
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
