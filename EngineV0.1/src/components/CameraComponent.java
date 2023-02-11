package components;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import annotations.BeginGroup;
import annotations.ColourField;
import annotations.ComboEnumField;
import annotations.EndGroup;
import annotations.ShowIfValueEnum;
import annotations.SkipField;

public class CameraComponent extends Component {
	
	@ColourField
	protected Vector4f clearColour = new Vector4f(0.06f);
	
	@ComboEnumField
	protected CameraType cameraProjection = CameraType.Perspective;
	
	@ShowIfValueEnum(fieldname="cameraProjection", value=0)
	protected float orthographicSize = 15;
	
	@ShowIfValueEnum(fieldname="cameraProjection", value=1)
	protected float fieldOfView = 70;
	
	
	@BeginGroup(name="Culling Planes")
	protected float nearPlane = 0.03f;
	@EndGroup
	protected float farPlane = 1000.0f;
		
	@BeginGroup(name="Camera Size")
	protected float width = 510;	
	@EndGroup
	protected float height = 320;
	
	@SkipField
	protected float aspectRatio;
	
	@Override
	public void prepare() {
		aspectRatio =  width / height;
	} 

	@Override
	public void update(double dt) {
		aspectRatio =  width / height;
	}
	
	/**
	 * @return the clearColour
	 */
	public Vector4f getClearColour() {
		return clearColour;
	}

	/**
	 * @return the cameraProjection
	 */
	public CameraType getCameraProjection() {
		return cameraProjection;
	}

	/**
	 * @return the orthographicSize
	 */
	public float getOrthographicSize() {
		return orthographicSize;
	}

	/**
	 * @return the fieldOfView
	 */
	public float getFieldOfView() {
		return fieldOfView;
	}

	/**
	 * @return the nearPlane
	 */
	public float getNearPlane() {
		return nearPlane;
	}

	/**
	 * @return the farPlane
	 */
	public float getFarPlane() {
		return farPlane;
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @return the aspectRatio
	 */
	public float getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * @param clearColour the clearColour to set
	 */
	public CameraComponent setClearColour(Vector4f clearColour) {
		this.clearColour = clearColour;
		return this;
	}

	/**
	 * @param cameraProjection the cameraProjection to set
	 */
	public CameraComponent setCameraProjection(CameraType cameraProjection) {
		this.cameraProjection = cameraProjection;
		return this;
	}

	/**
	 * @param orthographicSize the orthographicSize to set
	 */
	public CameraComponent setOrthographicSize(float orthographicSize) {
		this.orthographicSize = orthographicSize;
		return this;
	}

	/**
	 * @param fieldOfView the fieldOfView to set
	 */
	public CameraComponent setFieldOfView(float fieldOfView) {
		this.fieldOfView = fieldOfView;
		return this;
	}

	/**
	 * @param nearPlane the nearPlane to set
	 */
	public CameraComponent setNearPlane(float nearPlane) {
		this.nearPlane = nearPlane;
		return this;
	}

	/**
	 * @param farPlane the farPlane to set
	 */
	public CameraComponent setFarPlane(float farPlane) {
		this.farPlane = farPlane;
		return this;
	}

	/**
	 * @param width the width to set
	 */
	public CameraComponent setWidth(float width) {
		this.width = width;
		return this;
	}

	/**
	 * @param height the height to set
	 */
	public CameraComponent setHeight(float height) {
		this.height = height;
		return this;
	}

	/**
	 * Get View Matrix of Camera
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
		if(this.cameraProjection == CameraType.Orthographic) {
			return getOrthoProjectionMatrix();
		}else {
			return getPerspProjectionMatrix();
		}
	}
	
	
	public Matrix4f getOrthoProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		float orthoLeft = -orthographicSize * aspectRatio * 0.5f;
		float orthoRight = orthographicSize * aspectRatio * 0.5f;
		float orthoBottom = -orthographicSize * 0.5f;
		float orthoTop = orthographicSize * 0.5f;
		
		projectionMatrix.identity();
		return projectionMatrix.ortho(orthoLeft, orthoRight, orthoBottom, orthoTop, nearPlane, farPlane);
	}
	
	public Matrix4f getPerspProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
		return projectionMatrix.perspective(fieldOfView, aspectRatio, nearPlane, farPlane);
	}

}
