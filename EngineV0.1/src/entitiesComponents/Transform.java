package entitiesComponents;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform extends Component{

	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	public Transform() {
		this.position = new Vector3f();
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1, 1, 1);
	}
	
	public Transform(Vector3f position) {
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1, 1, 1);
	}
	
	public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
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
		return transformationMatrix;		
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
