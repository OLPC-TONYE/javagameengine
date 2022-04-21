package assets.light;

public class PointLight extends Light {
	
	Attenuation attenuation = new Attenuation(1, 0.0f, 0.0f);

	/**
	 * <b>PointLight</b>
	 * 
	 * <p>
	 * Light in a single point with attenuation
	 */
	public PointLight() {
		super();
		flag = LightFlags.Point;
	}

	/**
	 * PointLight
	 * 
	 * <p>
	 * Light in a single point with attenuation
	 * 
	 * @param tag name of Light
	 */
	public PointLight(String tag) {
		super(tag);
		flag = LightFlags.Point;
	}

	/**
	 * Returns the attenuation of the light
	 * @return attenuation
	 */
	public Attenuation getAttenuation() {
		return attenuation;
	}

	/**
	 * Sets the attenuation of the light
	 * @param attenuation
	 */
	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}
}
