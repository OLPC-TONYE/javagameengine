package entitiesComponents;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform extends Component{

	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	private Vector3f translation;
	
	public Transform() {
		this.position = new Vector3f();
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1, 1, 1);
		
		this.translation = new Vector3f();
	}
	
	public Transform(Vector3f position) {
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1, 1, 1);
		
		this.translation = new Vector3f();
	}
	
	public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		this.translation = new Vector3f();
	}

	@Override
	public void prepare() {
	}
	
	@Override
	public void update(double dt) {
		
	}
	
	public Matrix4f getTransformationMatrix() {
		Matrix4f transformationMatrix = new Matrix4f();
		
		transformationMatrix.identity();
		transformationMatrix.translate(position);
		transformationMatrix.rotateX((float) Math.toRadians(rotation.x));
		transformationMatrix.rotateY((float) Math.toRadians(rotation.y));
		transformationMatrix.rotateZ((float) Math.toRadians(rotation.z));
		transformationMatrix.scale(scale);
		
//		Apply Any Translation
		transformationMatrix.translate(translation);
		
		transformationMatrix.getTranslation(position);
		
		this.translation = new Vector3f();
		return transformationMatrix;		
	}
	
	public void translate(float x, float y, float z) {
		this.translation = new Vector3f(x, y, z);
	}
	
	public void translateZ(float z) {
		this.translation = new Vector3f(0, 0, z);
	}
	
	public void translateZ(double z) {
		this.translation = new Vector3f(0, 0, (float)z);
	}
	
	public void translateY(float y) {
		this.translation = new Vector3f(0, y, 0);
	}
	
	public void translateY(double y) {
		this.translation = new Vector3f(0, (float) y, 0);
	}
	
	public void translateX(float x) {
		this.translation = new Vector3f(x, 0, 0);
	}
	
	public void translateX(double x) {
		this.translation = new Vector3f((float) x, 0, 0);
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

}
