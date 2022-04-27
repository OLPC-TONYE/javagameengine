package assets.light;

import assets.Asset;

public class PointLight extends Light implements Attenuation {

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

	@Override
	public void copy(Asset from) {
		// TODO Auto-generated method stub
		
	}
}
