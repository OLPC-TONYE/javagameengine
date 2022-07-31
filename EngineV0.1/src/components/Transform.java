package components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform extends Component{

	private Vector3f position = new Vector3f();
	private Vector3f rotation = new Vector3f();
	private Vector3f scale = new Vector3f(1);
	
	private transient Vector3f translate = new Vector3f();
	private transient Vector3f rotate = new Vector3f();
	
	/** Direction of Positive Z **/
	private transient Vector3f direction = new Vector3f(0, 0, 1);

	private transient Matrix4f transformationMatrix;
	
	private transient Transform lastTransform;

	@Override
	public void prepare() {
		calcTransformationMatrix();
		transformationMatrix.positiveZ(direction);
	}
	
	@Override
	public void update(double dt) {
		
		if(!this.equals(lastTransform)) {
			calcTransformationMatrix();
			copy(lastTransform);
		}
		
		if(!this.translate.equals(0, 0, 0) | !this.rotate.equals(0, 0, 0)) {
			transform(translate, rotate);
			
			this.translate = new Vector3f();
			this.rotate = new Vector3f();
		}
		
	}
	
	private void transform(Vector3f translation, Vector3f rotation) {
//		Apply Transform		
		transformationMatrix.translate(translation);
		
		transformationMatrix.rotateX((float) Math.toRadians(rotation.x));
		transformationMatrix.rotateY((float) Math.toRadians(rotation.y));
		transformationMatrix.rotateZ((float) Math.toRadians(rotation.z));
		
//		Update value to new values After Transformation 
		transformationMatrix.getTranslation(position);
		this.rotation.add(rotation);
	}
	
	private void calcTransformationMatrix() {
		transformationMatrix = new Matrix4f();
		transformationMatrix.identity();
		transformationMatrix.translate(position);
		transformationMatrix.rotateX((float) Math.toRadians(rotation.x));
		transformationMatrix.rotateY((float) Math.toRadians(rotation.y));
		transformationMatrix.rotateZ((float) Math.toRadians(rotation.z));
		transformationMatrix.scale(scale);
		
		// Update Direction
		transformationMatrix.positiveZ(direction);
	}
	
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;		
	}
	
	public void rotate(float x, float y, float z) {
		this.rotate = new Vector3f(x, y, z);
	}
	
	public void rotateZ(float z) {
		this.rotate = new Vector3f(0, 0, z);
	}
	
	public void rotateZ(double z) {
		this.rotate = new Vector3f(0, 0, (float)z);
	}
	
	public void rotateY(float y) {
		this.rotate = new Vector3f(0, y, 0);
	}
	
	public void rotateY(double y) {
		this.rotate = new Vector3f(0, (float) y, 0);
	}
	
	public void rotateX(float x) {
		this.rotate = new Vector3f(x, 0, 0);
	}
	
	public void rotateX(double x) {
		this.rotate = new Vector3f((float) x, 0, 0);
	}
	
	public void translate(float x, float y, float z) {
		this.translate = new Vector3f(x, y, z);
	}
	
	public void translateZ(float z) {
		this.translate = new Vector3f(0, 0, z);
	}
	
	public void translateZ(double z) {
		this.translate = new Vector3f(0, 0, (float)z);
	}
	
	public void translateY(float y) {
		this.translate = new Vector3f(0, y, 0);
	}
	
	public void translateY(double y) {
		this.translate = new Vector3f(0, (float) y, 0);
	}
	
	public void translateX(float x) {
		this.translate = new Vector3f(x, 0, 0);
	}
	
	public void translateX(double x) {
		this.translate = new Vector3f((float) x, 0, 0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	/**
	 * @return the direction
	 */
	public Vector3f getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public void copy(Transform to) {
		if(to == null) to = new Transform();
		to.setPosition(position);
		to.setRotation(rotation);
		to.setScale(scale);
	}

	@Override
	public boolean equals(Object o) {
		
		if(o == null) return false;
		if(!(o instanceof Transform)) return false;
		
		Transform transform = (Transform) o;
		
		boolean equals = transform.getPosition().equals(position) && transform.getRotation().equals(rotation)
				&& transform.getScale().equals(scale);

		return equals;
	}
	

}
