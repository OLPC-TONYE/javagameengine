package entitiesComponents;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.EngineManager;

public class CameraComponent extends Component {
	
	/**
	 * 
	 * <b>NEAR_PLANE</b> NEAR PLANE of camera
	 * 
	 */
	protected float NEAR_PLANE;
	protected float FAR_PLANE;
	protected float FOV;
	
	protected float width;
	protected float height;
	
	protected int CameraProjection;
	
	@Override
	public void prepare() {
		if(entity.getComponent(Transform.class) != null) {
			System.out.println("Ready");
		}else {
			entity.addComponent(new Transform());
		}
	} 

	@Override
	public void update() {
		
	}
	
//	Getters
//	============================
	
	public float getNEAR_PLANE() {
		return NEAR_PLANE;
	}

	public float getFAR_PLANE() {
		return FAR_PLANE;
	}

	public float getFOV() {
		return FOV;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getCameraProjection() {
		return CameraProjection;
	}

//	Setters
//	============================
	
	public void setNEAR_PLANE(float nEAR_PLANE) {
		NEAR_PLANE = nEAR_PLANE;
	}

	public void setFAR_PLANE(float fAR_PLANE) {
		FAR_PLANE = fAR_PLANE;
	}

	public void setFOV(float fOV) {
		FOV = fOV;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setCameraProjection(int cameraProjection) {
		CameraProjection = cameraProjection;
	}

	/**
	 * 
	 * Get View Matrix of Camera
	 * 
	 */
	public Matrix4f getViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		
		Vector3f position = entity.getComponent(Transform.class).getPosition();
		Vector3f rotation = entity.getComponent(Transform.class).getRotation(); 

		viewMatrix.identity();

		// First do the rotation so camera rotates over its position
		viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
		// Then do the translation
		viewMatrix.translate(-position.x, -position.y, -position.z);
		
		return viewMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
		if(this.CameraProjection == EngineManager.ENGINE_CAMERA_ORTHOGRAPHIC) {
			return getOrthoProjectionMatrix();
		}else {
			return getPerspProjectionMatrix(width, height);
		}
	}
	
	
	public Matrix4f getOrthoProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
		return projectionMatrix.ortho(-width, width, -height, height, NEAR_PLANE, FAR_PLANE);
	}
	
	public Matrix4f getPerspProjectionMatrix(float width, float height) {
		float aspectRatio =  width / height;
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
		return projectionMatrix.perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
	}

}
