package entitiesComponents;

import org.joml.Vector3f;

import assets.light.Attenuation;
import assets.light.Light;
import assets.light.PointLight;
import assets.light.SpotLight;

public class LightingComponent extends Component {
	
	private Light light;

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

	public Vector3f getColour() {
		return light.getColour();
	}

	public void setColour(Vector3f colour) {
		this.light.setColour(colour);
	}

	public void defaultLight() {
		light = new PointLight(new Vector3f(1), new Vector3f(), 1, new Attenuation(1, 0.01f, 0.002f));
	}
	
	public void spotLight() {
		light = new SpotLight(new Vector3f(0, 1, 0),  new Vector3f(0, 0, -1), 1f, 30.0f, new Attenuation(1, 0.05f, 0.2f));
	}

	public Light getLight() {
		return light;
	}
}
