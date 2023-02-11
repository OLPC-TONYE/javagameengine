package components;

import org.joml.Vector3f;

import annotations.BeginGroup;
import annotations.ColourField;
import annotations.ComboEnumField;
import annotations.EndGroup;
import annotations.MathsDegreeField;


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
	
	// General Light Properties
	private float intensity = 0.6f;
	@ColourField
	private Vector3f colour = new Vector3f(1);
	
	@ComboEnumField
	private LightType flag = LightType.Directional;
	
	/**
	 * <b> For Spot & Point Lights </b>
	 * 
	 * @param constant - x
	 * @param linear - y
	 * @param exponent - z
	 */
	private Vector3f attenuation = new Vector3f(1, 0.0f, 0.0f);
	
	@BeginGroup(name="SpotLight Properties")
	@MathsDegreeField
	/** For SpotLight cut-off angle	 */
	private float cutOffAngle = (float) Math.toRadians(60.0f);
	
	@EndGroup
	@MathsDegreeField
	/** For SpotLight inner angle	 */
	private float innerAngle = (float) Math.toRadians(60.0f);
	
	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the intensity
	 */
	public float getIntensity() {
		return intensity;
	}

	/**
	 * @param intensity the intensity to set
	 * @return 
	 */
	public LightingComponent setIntensity(float intensity) {
		this.intensity = intensity;
		return this;
	}

	/**
	 * @return the colour
	 */
	public Vector3f getColour() {
		return colour;
	}

	/**
	 * @param colour the colour to set
	 * @return 
	 */
	public LightingComponent setColour(Vector3f colour) {
		this.colour = colour;
		return this;
	}

	/**
	 * @return the flag
	 */
	public LightType getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 * @return this component
	 */
	public LightingComponent setFlag(LightType flag) {
		this.flag = flag;
		return this;
	}

	/**
	 * @return the attenuation
	 */
	public Vector3f getAttenuation() {
		return attenuation;
	}

	/**
	 * @param attenuation the attenuation to set
	 * @return this component
	 */
	public LightingComponent setAttenuation(Vector3f attenuation) {
		this.attenuation = attenuation;
		return this;
	}

	/**
	 * @return the cutOffAngle
	 */
	public float getCutOffAngle() {
		return cutOffAngle;
	}

	/**
	 * @param cutOffAngle the cutOffAngle to set
	 * @return this component
	 */
	public LightingComponent setCutOffAngle(float cutOffAngle) {
		this.cutOffAngle = cutOffAngle;
		return this;
	}

	/**
	 * @return the innerAngle
	 */
	public float getInnerAngle() {
		return innerAngle;
	}

	/**
	 * @param innerAngle the innerAngle to set
	 * @return this component
	 */
	public LightingComponent setInnerAngle(float innerAngle) {
		this.innerAngle = innerAngle;
		return this;
	}

}
