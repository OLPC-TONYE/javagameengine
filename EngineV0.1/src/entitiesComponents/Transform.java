package entitiesComponents;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform extends Component{

	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	private Vector3f translation;
	private Vector3f n_rotation;
	
	private Matrix4f transformationMatrix;
	
	private boolean modified;
	
	public Transform() {
		this.position = new Vector3f();
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1, 1, 1);
		
		this.translation = new Vector3f();
		this.n_rotation = new Vector3f();
		calcTransformationMatrix();
	}
	
	public Transform(Vector3f position) {
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1, 1, 1);
		
		this.translation = new Vector3f();
		this.n_rotation = new Vector3f();
		calcTransformationMatrix();
	}
	
	public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		this.translation = new Vector3f();
		this.n_rotation = new Vector3f();
		calcTransformationMatrix();
	}

	@Override
	public void prepare() {
	}
	
	@Override
	public void update(double dt) {
		
		if(this.modified) {
			calcTransformationMatrix();
			this.modified = false;
		}
		
		if(!this.translation.equals(0, 0, 0) | !this.n_rotation.equals(0, 0, 0)) {
			transform(translation, n_rotation);
			
			this.translation = new Vector3f();
			this.n_rotation = new Vector3f();
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
	}
	
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;		
	}
	
	public void rotate(float x, float y, float z) {
		this.n_rotation = new Vector3f(x, y, z);
		this.modified = true;
	}
	
	public void rotateZ(float z) {
		this.n_rotation = new Vector3f(0, 0, z);
		this.modified = true;
	}
	
	public void rotateZ(double z) {
		this.n_rotation = new Vector3f(0, 0, (float)z);
		this.modified = true;
	}
	
	public void rotateY(float y) {
		this.n_rotation = new Vector3f(0, y, 0);
		this.modified = true;
	}
	
	public void rotateY(double y) {
		this.n_rotation = new Vector3f(0, (float) y, 0);
		this.modified = true;
	}
	
	public void rotateX(float x) {
		this.n_rotation = new Vector3f(x, 0, 0);
		this.modified = true;
	}
	
	public void rotateX(double x) {
		this.n_rotation = new Vector3f((float) x, 0, 0);
		this.modified = true;
	}
	
	public void translate(float x, float y, float z) {
		this.translation = new Vector3f(x, y, z);
		this.modified = true;
	}
	
	public void translateZ(float z) {
		this.translation = new Vector3f(0, 0, z);
		this.modified = true;
	}
	
	public void translateZ(double z) {
		this.translation = new Vector3f(0, 0, (float)z);
		this.modified = true;
	}
	
	public void translateY(float y) {
		this.translation = new Vector3f(0, y, 0);
		this.modified = true;
	}
	
	public void translateY(double y) {
		this.translation = new Vector3f(0, (float) y, 0);
		this.modified = true;
	}
	
	public void translateX(float x) {
		this.translation = new Vector3f(x, 0, 0);
		this.modified = true;
	}
	
	public void translateX(double x) {
		this.translation = new Vector3f((float) x, 0, 0);
		this.modified = true;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		this.modified = true;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
		this.modified = true;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
		this.modified = true;
	}

}
