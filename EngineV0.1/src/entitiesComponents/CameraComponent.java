package entitiesComponents;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import managers.EngineManager;

public class CameraComponent extends Component {
	
	protected float near_plane;
	protected float far_plane;
	protected float field_of_view;
	
	protected float width;
	protected float height;
	
	protected float orthographic_size;
	protected float aspectRatio;
	
	protected float[] clear_colour = new float[4];
	
	protected int cameraProjection;
	
	@Override
	public void prepare() {
		
	} 

	@Override
	public void update(double dt) {
		
	}
	
	public float[] getClearColour() {
		return clear_colour;
	}
	
	public float[] getPerpsProperties() {
		return new float[] {near_plane, far_plane, field_of_view};
	}
	
	public float[] getCameraSize() {
		return new float[] {width, height};
	}
	
	/**
	 * 
	 * Get Orthographic Camera Properties
	 * 
	 * 
	 */
	public float getOrthoProperties() {
		return orthographic_size;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public int getCameraProjection() {
		return cameraProjection;
	}
	
	public void setClearColour(float red, float green, float blue, float alpha) {
		this.clear_colour[0] = red;
		this.clear_colour[1] = green;
		this.clear_colour[2] = blue;
		this.clear_colour[3] = alpha;
	}
	
	public void setPerpsProperties(float near_plane, float far_plane, float field_of_view) {
		this.far_plane = far_plane;
		this.near_plane = near_plane;
		this.field_of_view = field_of_view;
	}

	public void setCameraSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public void setOrthoProperties(float near_plane, float far_plane, float field_of_view, float size) {
		this.far_plane = far_plane;
		this.near_plane = near_plane;
		this.orthographic_size = size;
		this.field_of_view = field_of_view;
	}

	public void setCameraProjection(int projection) {
		cameraProjection = projection;
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
		aspectRatio =  width / height;
		if(this.cameraProjection == EngineManager.ENGINE_CAMERA_ORTHOGRAPHIC) {
			return getOrthoProjectionMatrix();
		}else {
			return getPerspProjectionMatrix();
		}
	}
	
	
	public Matrix4f getOrthoProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		float orthoLeft = -orthographic_size * aspectRatio * 0.5f;
		float orthoRight = orthographic_size * aspectRatio * 0.5f;
		float orthoBottom = -orthographic_size * 0.5f;
		float orthoTop = orthographic_size * 0.5f;
		
		projectionMatrix.identity();
		return projectionMatrix.ortho(orthoLeft, orthoRight, orthoBottom, orthoTop, near_plane, far_plane);
	}
	
	public Matrix4f getPerspProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
		return projectionMatrix.perspective(field_of_view, aspectRatio, near_plane, far_plane);
	}

	@Override
	public void UI() {
		
		
		ImVec2 dstImVec2 = new ImVec2();
			
		ImGui.pushID("Clear Colour:");
		ImGui.text("Clear Colour:");
		ImGui.sameLine();
		
		if(ImGui.colorEdit4("", clear_colour, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
		}
		ImGui.popID();
		
		ImGui.pushID("Width:");
		ImGui.text("Width:");
		ImGui.sameLine();
		ImFloat val = new ImFloat(width);
		ImGui.calcTextSize(dstImVec2, ""+val.get());
		ImGui.setNextItemWidth(dstImVec2.x+30);
		if(ImGui.inputFloat("", val)) {
			width = val.get();
			if(val.get() < 1) {
				width = 1;
			}			
		}
		ImGui.popID();
		ImGui.sameLine();
		ImGui.pushID("Height:");
		ImGui.text("Height:");
		ImGui.sameLine();
		ImFloat val_height = new ImFloat(height);
		ImGui.calcTextSize(dstImVec2, ""+val_height.get());
		ImGui.setNextItemWidth(dstImVec2.x+30);
		if(ImGui.inputFloat("", val_height)) {
			height = val_height.get();
			if(val_height.get() < 1) {
				height = 1;
			}		
		}
		ImGui.popID();
		
		ImGui.pushID("Near Plane:");
		ImGui.text("Near Plane:");
		ImGui.sameLine();
		ImFloat val_nplane = new ImFloat(near_plane);
		ImGui.calcTextSize(dstImVec2, ""+val_nplane.get());
		ImGui.setNextItemWidth(dstImVec2.x+30);
		if(ImGui.inputFloat("", val_nplane)) {
			near_plane = val_nplane.get();	
		}
		ImGui.popID();
		
		ImGui.pushID("Far Plane:");
		ImGui.text("Far Plane:");
		ImGui.sameLine();
		ImFloat val_fplane = new ImFloat(far_plane);
		ImGui.calcTextSize(dstImVec2, ""+val_fplane.get());
		ImGui.setNextItemWidth(dstImVec2.x+30);
		if(ImGui.inputFloat("", val_fplane)) {
			far_plane = val_fplane.get();		
		}
		ImGui.popID();
		
		ImGui.pushID("Field Of View:");
		ImGui.text("Field Of View:");
		ImGui.sameLine();
		ImFloat val_fov = new ImFloat(field_of_view);
		ImGui.calcTextSize(dstImVec2, ""+val_fov.get());
		ImGui.setNextItemWidth(dstImVec2.x+30);
		if(ImGui.inputFloat("", val_fov)) {
			field_of_view = val_fov.get();
			if(val_fov.get() < 1) {
				field_of_view = 1;
			}		
		}
		ImGui.popID();
		
		ImGui.pushID("Projection:");
		ImGui.text("Projection:");
		ImGui.sameLine();
		String[] items = new String[]{"Perspective", "Orthographic"};
		ImInt currentItem = new ImInt(cameraProjection);
		if(ImGui.combo("", currentItem, items)) {
			this.cameraProjection = currentItem.get();
		}
		ImGui.popID();
		
		if(cameraProjection == 1) 
		{
			ImGui.pushID("Size:");
			ImGui.text("Size:");
			ImGui.sameLine();
			ImFloat val_size = new ImFloat(orthographic_size);
			ImGui.calcTextSize(dstImVec2, ""+val_size.get());
			ImGui.setNextItemWidth(dstImVec2.x+30);
			if(ImGui.inputFloat("", val_size)) {
				orthographic_size = val_size.get();
				if(val_size.get() < 1) {
					orthographic_size = 1;
				}			
			}
			ImGui.popID();
		}
	
	}

}
