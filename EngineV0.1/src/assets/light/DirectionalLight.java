package assets.light;

import org.joml.Vector3f;

public class DirectionalLight extends Light {
	
	Vector3f direction = new Vector3f(0, 1, 0);

	/**
	 * 
	 */
	public DirectionalLight() {
		super();
		flag = LightFlags.Directional;
	}

	/**
	 * @param tag
	 */
	public DirectionalLight(String tag) {
		super(tag);
		flag = LightFlags.Directional;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}
	
}
