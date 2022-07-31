package interfaces;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.joml.Vector3f;
import org.joml.Vector4f;

import annotations.BeginGroup;
import annotations.ColourField;
import annotations.ComboEnumField;
import annotations.EndGroup;
import annotations.HideIfNull;
import annotations.MathsDegreeField;
import annotations.ShowIfValueBool;
import annotations.ShowIfValueEnum;
import annotations.SkipField;
import annotations.TextureNameField;
import assets.Asset;
import assets.AssetType;
import assets.Material;
import assets.Mesh;
import assets.Sprite;
import components.Component;
import components.SpriteRenderer;
import entities.Entity;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import leveleditor.LevelEditorLayer;
import managers.AssetManager;
import managers.EntityManager;
import tools.StringFormatter;

public class InspectorPanel {
	

	private ImBoolean texture_preview = new ImBoolean();
	private float labelDistance = 30;
	private float itemOffset = 120;

	public void render(LevelEditorLayer layer) {
		
		ImGui.begin("Inspector");
		int imGuiID = ImGui.getID("Inspector Dockspace");
		ImVec4 wBg = ImGui.getStyle().getColor(ImGuiCol.WindowBg);
		ImGui.pushStyleColor(ImGuiCol.DockingEmptyBg, wBg.x, wBg.y, wBg.z, wBg.w);
		ImGui.dockSpace(imGuiID );
		ImGui.popStyleColor();
		if (layer.selectedEntity != null) {
			ImGui.begin("Inspector:Entity", ImGuiWindowFlags.None);
			ImGui.text(layer.selectedEntity);
			Entity entity = EntityManager.world.getFromSecondMap(layer.selectedEntity);

			if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight)) {

				if (ImGui.beginMenu("Add Component")) {

					if (ImGui.menuItem("Sprite Renderer")) {
						entity.addComponent(new SpriteRenderer());
					}

					ImGui.endMenu();
				}

				ImGui.endPopup();
			}

			for (Component component : entity.getComponents()) {

				if (ImGui.collapsingHeader(component.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen)) {
					componentUI(component);
				}

			}
			ImGui.end();
		}
		if(layer.selectedAsset != null) {
			Asset asset = AssetManager.assets.getFromSecondMap(layer.selectedAsset);
			ImGui.begin("Inspector:Asset");
			if(ImGui.collapsingHeader("Asset: "+asset.getAssetName(), ImGuiTreeNodeFlags.DefaultOpen)){

				assetUI(asset);
			}
			ImGui.end();
		}

		ImGui.end();
	}

	private void assetUI(Asset asset) {
		try {
			for(Field field: asset.getClass().getDeclaredFields()) {
				boolean isTransient  = Modifier.isTransient(field.getModifiers());
		    	boolean isPrivate = Modifier.isPrivate(field.getModifiers());
		    	boolean isProtected = Modifier.isProtected(field.getModifiers());
		    	
		    	if(isPrivate | isProtected) {
		    		field.setAccessible(true);
		    	}
		    	
		    	if(isTransient) {
		    		continue;
		    	}
		    	
		    	if(hideField(asset, field)) {
		    		continue;
		    	}
		    	
		    	field(asset, field);
		    	
		    	if(isPrivate| isProtected) {
		    		field.setAccessible(false);
		    	}			
				
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
//    	===== Asset (Sprite) ==================================
    	if(asset.getClass() == Sprite.class) {
    		Sprite sprite = (Sprite) asset;
		
			if(sprite != null) {
				
				if (sprite.isTilemap()) {

					if(sprite.getNumberOfTiles() > 0) {
						ImGui.pushID("Tile Preview");
						ImGui.text("Preview: ");
						
						int texture_id = AssetManager.getTexture(sprite.getTextureName()).getTextureID();
						
						ImGui.popID();
						
						if(ImGui.beginChildFrame(1, ImGui.getContentRegionAvailX() - 10, 85, ImGuiWindowFlags.NoMove|ImGuiWindowFlags.HorizontalScrollbar)){
							for(int i=0; i < sprite.getNumberOfTiles(); i++) {
			            		
			            		float[] tile_cords = sprite.getFromTileMap(i);
			            		
			            		// Change ID For each icon
			            		ImGui.pushID(i);
			            		if(ImGui.imageButton(texture_id, 60, 60, tile_cords[0], tile_cords[1], tile_cords[4], tile_cords[5])) {
			            			sprite.setCurrentTile(i+1);
			            		}
			            		ImGui.popID();
			            		
			            		ImGui.sameLine();
							}
							ImGui.endChildFrame();
						}
					}
					
				}
			}
    	}
	}
	
	private void componentUI(Component component) {
		try {
			for(Field field: component.getClass().getDeclaredFields()) {
				
				boolean isTransient  = Modifier.isTransient(field.getModifiers());
	        	boolean isPrivate = Modifier.isPrivate(field.getModifiers());
	        	boolean isProtected = Modifier.isProtected(field.getModifiers());
	        	
	        	if(isPrivate | isProtected) {
	        		field.setAccessible(true);
	        	}
	        	
	        	if(isTransient) {
	        		continue;
	        	}
	        	
	        	if(hideField(component, field)) {
	        		continue;
	        	}
				
				field(component, field);
				
				if(isPrivate| isProtected) {
	        		field.setAccessible(false);
	        	}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	

	private boolean hideField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		
		if(field.getDeclaredAnnotation(SkipField.class) != null) {
    		return true;
    	}
		
		HideIfNull hideif = field.getDeclaredAnnotation(HideIfNull.class);
		if(hideif != null) {
    		Field check = object.getClass().getDeclaredField(hideif.fieldName());
    		
			check.setAccessible(true);
    		Object actualValue = check.get(object);
    		check.setAccessible(false);
			if(actualValue == null) {
    			return true;
    		}
    		
		}	
    		
		ShowIfValueBool showifBool = field.getDeclaredAnnotation(ShowIfValueBool.class);
		if(showifBool != null) {
			Field check = object.getClass().getDeclaredField(showifBool.fieldname());
    		boolean expected = showifBool.value();
    		check.setAccessible(true);
    		boolean actual = check.getBoolean(object);
    		check.setAccessible(false);
    		
    		if(actual != expected) {
    			return true;
    		}
		}
    	
    	ShowIfValueEnum showIf = field.getDeclaredAnnotation(ShowIfValueEnum.class);
		if(showIf != null) {
    		Field check = object.getClass().getDeclaredField(showIf.fieldname());
    		Class<?> checkType = check.getType();
    		int expected = showIf.value();
    		
    		if(checkType.isEnum()) {
    			Enum<?>[] allValues = (Enum<?>[]) checkType.getEnumConstants();
    			Enum<?> expectedValue = allValues[expected];
    			check.setAccessible(true);
        		Enum<?> actualValue = (Enum<?>) check.get(object);
        		check.setAccessible(false);
    			if(actualValue != expectedValue) {
        			return true;
        		}
    		}       		
    		
    	}
		return false;
	}
	
	private void field(Object object, Field field) throws IllegalArgumentException, IllegalAccessException {				
    	
    	BeginGroup beginGroup = field.getDeclaredAnnotation(BeginGroup.class);
		if(beginGroup != null) {
			ImGui.text(beginGroup.name()+": ");
			ImGui.indent();
		}
		
		Class<?> type = field.getType();
    	String name = StringFormatter.firstLetterUpperCase(field.getName());
		Object value = field.get(object);
										
		ImGui.pushTextWrapPos(itemOffset);
		
//		===== Texture UI ============================
		if(field.getDeclaredAnnotation(TextureNameField.class) != null) {
			ImGui.pushID("Texture Preview");
			ImGui.text("Preview: ");
			ImGui.sameLine(itemOffset , labelDistance);
			
			String val = (String) value;
			int textureID = AssetManager.getTexture(val).getTextureID();
			
			if(ImGui.imageButton(textureID, 30, 30, 0, 0, 1, 1)) {
	        	ImGui.openPopup(val+"Preview");
	        	texture_preview.set(true);
	        }
			if (ImGui.beginDragDropTarget()) {
	            final Object payload = ImGui.acceptDragDropPayload("payload_type");
	            if (payload != null) {
	            	field.set(object, (String) payload);
	            }
	            ImGui.endDragDropTarget();
	        }
			
			ImGui.pushStyleColor(ImGuiCol.ModalWindowDimBg, 0.3f, 0, 0.5f, 0.9f);
			ImGui.setNextWindowSize(300, 300, ImGuiCond.Once);
			if(ImGui.beginPopupModal(val+"Preview", texture_preview)) {
				ImGui.image(textureID, ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY());			
				ImGui.endPopup();
			}
			ImGui.popID();
			ImGui.popStyleColor();
		}
		
				
//    	===== ColourPicker UI ============================
		else if(field.getDeclaredAnnotation(ColourField.class) != null) {          	
    		if(type == Vector3f.class) {
    			Vector3f val = (Vector3f)value;
        		float[] imFloat = {val.x, val.y, val.z};
        		
        		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
        		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
        		if(ImGui.colorEdit3("", imFloat, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
        			val.set(imFloat);
        		}
        		ImGui.popID();
    		}else if(type == Vector4f.class) {
    			Vector4f val = (Vector4f)value;
        		float[] imFloat = {val.x, val.y, val.z, val.w};
        		
        		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
        		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
        		if(ImGui.colorEdit4("", imFloat, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
        			val.set(imFloat);
        		}
        		ImGui.popID();
    		}else  {
    			ImGui.pushTextWrapPos();
    			ImGui.text("Unsupported Colour Variable Type: "+type.getSimpleName());
    			ImGui.popTextWrapPos();
    		}
    		
    	}
		
//    	===== Enum Combo ============================
		else if(field.getDeclaredAnnotation(ComboEnumField.class) != null) { 
			if(type.isEnum()) {
				Enum<?> val = (Enum<?>) value;
				Enum<?>[] values = (Enum<?>[]) type.getEnumConstants();
				ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
        		String previewValue = val.name();
        		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
				if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.None)) {
        			for(int i = 0; i < values.length; i++) {
        				ImGui.pushID(i);
        				
        				if(ImGui.selectable(values[i].name())) {
        					field.set(object, values[i]);
        				}
        				
        				ImGui.popID();
        			}
        			ImGui.endCombo();
        		}
        		ImGui.popID();
    		}else {
    			ImGui.pushTextWrapPos();
    			ImGui.textColored(1.0f, 0, 0, 0.9f, "Unsupported Variable Type: "+type.getSimpleName());
    			ImGui.popTextWrapPos();
    		}
    		
    	}
		
//    	===== Degree (if the value is radians) ============================
		else if(field.getDeclaredAnnotation(MathsDegreeField.class) != null) {
			if(type == float.class) {
				float val = (float)value;
        		float[] imFloat = {(float) Math.toDegrees(val)};
        		
        		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
        		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
        		if(ImGui.dragFloat("", imFloat, 0.1f, 0.1f, 89.9f)) {
        			field.set(object, (float) Math.toRadians(imFloat[0]));
        		}
        		ImGui.popID();
			}
		}

//		===== Booleans =================================
    	else if(type == boolean.class) {
    		boolean val = (boolean)value;
    		
    		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
    		if(ImGui.checkbox("", val)) {
    			field.set(object, !val);
    		}
    		ImGui.popID();
    	}
		
//		===== Floats =================================
    	else if(type == float.class) {
    		float val = (float)value;
    		float[] imFloat = {val};
    		
    		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
    		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
    		if(ImGui.dragFloat("", imFloat)) {
    			field.set(object, imFloat[0]);
    		}
    		ImGui.popID();
    	}
    	
//		===== Integers =================================
    	else if(type == int.class) {
    		int val = (int)value;
    		ImInt imInt = new ImInt(val);
    		
    		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
    		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
    		if(ImGui.inputInt("", imInt, 1, 1, ImGuiInputTextFlags.None)) {
    			field.set(object, imInt.get());
    		}
    		ImGui.popID();
    	}
    	
//    	===== Vector3f ==================================
    	else if(type == Vector3f.class) {
    		Vector3f val = (Vector3f)value;
    		float[] imFloat = {val.x, val.y, val.z};
    		
    		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
    		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
    		if(ImGui.inputFloat3("", imFloat)) {
    			val.set(imFloat);
    		}
    		ImGui.popID();
    	}
		
//    	===== Vector4f ==================================
    	else if(type == Vector4f.class) {
    		Vector4f val = (Vector4f)value;
    		float[] imFloat = {val.x, val.y, val.z, val.w};
    		
    		ImGui.pushID(name+" :"); ImGui.text(name); ImGui.sameLine(itemOffset, labelDistance);
    		ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
    		if(ImGui.inputFloat4("", imFloat)) {
    			val.set(imFloat);
    		}
    		ImGui.popID();
    	}
		
//    	===== Asset (Sprite) ==================================
    	else if(type == Sprite.class) {
    		Sprite sprite = (Sprite) value;
    		String previewValue = sprite != null ? sprite.getAssetName(): "" ;
    		ImGui.pushID("Sprite:");
			ImGui.text("Sprite: "); ImGui.sameLine(itemOffset, labelDistance);
			ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
			if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
							
				ImGui.endCombo();
			}
			if (ImGui.beginDragDropTarget()) {
	            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
	            if (payload != null && payload instanceof Asset) {
	            	Asset asset = (Asset) payload;
	            	if(asset.getAssetType() == AssetType.Sprite) {
	            		Sprite spriteLoad = (Sprite) asset;
						field.set(object, spriteLoad);
	            	}
	            }
	            ImGui.endDragDropTarget();
	        }
			ImGui.popID();
    	}
    	
//    	===== Asset (Mesh) ==================================
    	else if(type == Mesh.class) {
    		Mesh mesh = (Mesh) value;
    		String previewValue = mesh != null ? mesh.getAssetName(): "" ;
    		ImGui.pushID("Mesh:");
			ImGui.text("Mesh: "); ImGui.sameLine(itemOffset, labelDistance);
			ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
			if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
							
				ImGui.endCombo();
			}
			if (ImGui.beginDragDropTarget()) {
	            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
	            if (payload != null && payload instanceof Asset) {
	            	Asset asset_ = (Asset) payload;
	            	if(asset_.getAssetType() == AssetType.Mesh) {
	            		field.set(object, (Mesh) asset_);
	            	}
	            }
	            ImGui.endDragDropTarget();
	        }
			ImGui.popID();
    	}
		   	
//    	===== Asset (Material) ==================================
    	else if(type == Material.class) {
    		Material material = (Material) value;
    		
    		ImGui.pushID("Material:");
			ImGui.text("Material: "); ImGui.sameLine(itemOffset, labelDistance);
			String previewValue = material != null ? material.getAssetName(): "" ;
			ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 10);
			if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) ImGui.endCombo();
			if (ImGui.beginDragDropTarget()) {
	            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
	            if (payload != null && payload instanceof Asset) {
	            	Asset asset_ = (Asset) payload;
	            	if(asset_.getAssetType() == AssetType.Material) {
	            		field.set(object, (Material) asset_);
	            	}
	            }
	            ImGui.endDragDropTarget();
	        }
			
			ImGui.popID();
    	}            	
		
		ImGui.popTextWrapPos();
		
		EndGroup ifEndGroup = field.getDeclaredAnnotation(EndGroup.class);
		if(ifEndGroup != null) {
			ImGui.unindent();
		}
			
	}

}
