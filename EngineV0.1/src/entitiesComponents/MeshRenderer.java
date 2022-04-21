package entitiesComponents;

import org.joml.Vector3f;

import assets.Asset;
import assets.AssetType;
import assets.mesh.Material;
import assets.mesh.Mesh;
import engine.EngineManager;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import opengl.VertexArrayObject;

public class MeshRenderer extends Component{
	
	VertexArrayObject vao;
	Mesh mesh;
	Vector3f colour = new Vector3f(1, 1, 1);
	
	String textureName = "white";
	private ImBoolean preview = new ImBoolean();
	
	@Override
	public void prepare() {
		
	}

	@Override
	public void update(double dt) {
		
	}
	
	public Vector3f getColour() {
		return this.colour;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Mesh getMesh() {
		return this.mesh;
	}
	
	public String getTexture() {
		return this.textureName;
	}
	
	public int getTextureID() {
		return EngineManager.getTexture(textureName).getTextureID();
	}
	
	public void setTexture(String textureName) {
		this.textureName = textureName;
	}
		
	@Override
	public void UI() {
		
		Vector3f val = colour;
		float[] imFloat = {val.x, val.y, val.z};
		if(ImGui.colorEdit3("Colour", imFloat, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
			this.colour.set(imFloat);
		}
		
		ImGui.pushID("Mesh:");
		ImGui.text("Mesh: ");
				
		ImGui.sameLine();
		
		{	// Mesh
			String previewValue = mesh != null ? mesh.getAssetName(): "" ;
			if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
							
				ImGui.endCombo();
			}
		}
		
		if(mesh != null) {
			ImGui.separator();
			
					
			{	// Material
				Material material = mesh.getMaterial() != null ? mesh.getMaterial() : null;
				
				ImGui.pushID("Material:");
				ImGui.text("Material: ");
				
				ImGui.sameLine();
				String previewValue = material != null ? material.getAssetName(): "" ;
				if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
								
					ImGui.endCombo();
				}
				
				
				
				ImGui.popID();
			}
		}

		if (ImGui.beginDragDropTarget()) {
            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
            if (payload != null && payload instanceof Asset) {
            	Asset asset = (Asset) payload;
            	if(asset.getAssetType() == AssetType.Mesh) {
            		mesh = (Mesh) asset;
            		vao = mesh.getVertexArray();
            	}
            }
            ImGui.endDragDropTarget();
        }
		ImGui.popID();
		
		ImGui.pushID("Texture Preview");
		ImGui.text("Preview: ");
		ImGui.sameLine();
		if(ImGui.imageButton(getTextureID(), 30, 30, 0, 0, 1, 1)) {
        	ImGui.openPopup(getTexture()+"Preview");
        }
		if (ImGui.beginDragDropTarget()) {
            final Object payload = ImGui.acceptDragDropPayload("payload_type");
            if (payload != null) {
                setTexture((String) payload);
            }
            ImGui.endDragDropTarget();
        }
		
		ImGui.setNextWindowSize(300, 300, ImGuiCond.Once);
		if(ImGui.beginPopupModal(getTexture()+"Preview", preview)) {
			ImGui.image(getTextureID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY());			
			ImGui.endPopup();
		}
		ImGui.popID();
		
		
	}

}
