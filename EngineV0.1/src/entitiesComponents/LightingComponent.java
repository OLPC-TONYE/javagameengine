package entitiesComponents;

import assets.light.DirectionalLight;
import assets.light.Light;


/**
 * 
 * LightingComponent
 * 
 * <p>
 * Component for light entities
 * 
 * @author Charles-Okhide Tonye
 *
 */
public class LightingComponent extends Component {
	
	private Light light = new DirectionalLight();

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return {@code Light} current light values
	 */	
	public Light getLight() {
		return light;
	}
	
	public void setLight(Light light) {
		this.light = light;
	}
}
