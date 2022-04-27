package assets.light;

import org.joml.Vector3f;

/**
 * @param constant - x
 * @param linear - y
 * @param exponent - z
 */
public interface Attenuation {
	
	Vector3f attenuation = new Vector3f(1, 0.0f, 0.0f);
	
	public default Attenuation getAttenuation() {
		return this;
	}
	
	public default Vector3f getAttenuationVector() {
		return attenuation;
	}
	
	public default void setAttenuation(Vector3f vector) {
		attenuation.set(vector);
	}
	
	public default float getConstant() {
		return attenuation.x;
	}
	
	public default void setConstant(float constant) {
		attenuation.set(constant, getLinear(), getExponent());
	}
	
	public default float getLinear() {
		return attenuation.y;
	}
	
	public default void setLinear(float linear) {
		attenuation.set(getConstant(), linear, getExponent());
	}
	
	public default float getExponent() {
		return attenuation.z;
	}
	
	public default void setExponent(float exponent) {
		attenuation.set(getConstant(), getLinear(), exponent);
	}

}
