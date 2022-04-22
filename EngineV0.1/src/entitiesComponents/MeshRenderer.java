package entitiesComponents;

import org.joml.Vector4f;

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

public class MeshRenderer extends Component{
	
	Mesh mesh;
	
	String textureName = "white";
	private ImBoolean preview = new ImBoolean();
	
	@Override
	public void prepare() {
		
	}

	@Override
	public void update(double dt) {
		
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
		
		ImGui.pushID("Mesh:");
		ImGui.text("Mesh: ");
				
		ImGui.sameLine();
		
		{	// Mesh
			String previewValue = mesh != null ? mesh.getAssetName(): "" ;
			if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
							
				ImGui.endCombo();
			}
			
		}
		
		if (ImGui.beginDragDropTarget()) {
            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
            if (payload != null && payload instanceof Asset) {
            	Asset asset = (Asset) payload;
            	if(asset.getAssetType() == AssetType.Mesh) {
            		mesh = (Mesh) asset;
            	}
            }
            ImGui.endDragDropTarget();
        }
		ImGui.popID();
		
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
				
				if(material != null) {
					
					
					{
						ImGui.pushID("Ambient");
						ImGui.text("Ambient: ");
						ImGui.sameLine();
						Vector4f val = material.getAmbient();
						float[] vals = {val.x, val.y, val.z, val.w};
						if(ImGui.colorEdit4("", vals, ImGuiColorEditFlags.NoInputs)) {
							val.set(vals);
						}
						ImGui.popID();
					}
					
					{
						ImGui.pushID("Diffuse");
						ImGui.text("Diffuse: ");
						ImGui.sameLine();
						Vector4f val = material.getDiffuse();
						float[] vals = {val.x, val.y, val.z, val.w};
						if(ImGui.colorEdit4("", vals, ImGuiColorEditFlags.NoInputs)) {
							val.set(vals);
						}
						ImGui.popID();
					}
					
					{
						ImGui.pushID("Specular");
						ImGui.text("Specular: ");
						ImGui.sameLine();
						Vector4f val = material.getSpecular();
						float[] vals = {val.x, val.y, val.z, val.w};
						if(ImGui.colorEdit4("", vals, ImGuiColorEditFlags.NoInputs)) {
							val.set(vals);
						}
						ImGui.popID();
					}
					
					{
						ImGui.pushID("Reflectivity");
						ImGui.text("Reflectivity: ");
						ImGui.sameLine();
						float val = material.getReflectivity();
						float[] vals = {val};
						if(ImGui.dragFloat("", vals, 0.01f, 0, 1)) {
							material.setReflectivity(vals[0]);
						}
						ImGui.popID();
					}
					
					{
						ImGui.pushID("Shininess");
						ImGui.text("Shininess: ");
						ImGui.sameLine();
						float val = material.getSpecularPower();
						float[] vals = {val};
						if(ImGui.dragFloat("", vals, 0.1f, 0, 20)) {
							material.setSpecularPower(vals[0]);
						}
						ImGui.popID();
					}
				}
				
				
				ImGui.popID();
			}
		}
	
		ImGui.pushID("Texture Preview");
		ImGui.text("Preview: ");
		ImGui.sameLine();
		if(ImGui.imageButton(getTextureID(), 30, 30, 0, 0, 1, 1)) {
        	ImGui.openPopup(getTexture()+"Preview");
        	preview.set(true);
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
