package interfaces;

import assets.Asset;
import assets.Scene;
import assets.Sprite;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiPopupFlags;
import leveleditor.LevelEditorLayer;
import managers.AssetManager;

public class AssetBrowser{

	public void render(LevelEditorLayer layer) {
		ImGui.begin("Assets");
		
		contextMenu();
		
		ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
		
		int i = 0;
		for (Asset asset : AssetManager.getWorldAssets()) {
			float WindowPosX = ImGui.getWindowPosX();
			float WindowSizeX = ImGui.getWindowSizeX();
			float ItemSpacingX = ImGui.getStyle().getItemSpacingX();
			float windowX2 = WindowPosX + WindowSizeX;

			ImGui.pushID(i);
			ImGui.beginGroup();
			
			int assetIcon;
			
			boolean clicked;
			switch (asset.getAssetType())
			{
				case Mesh:
					assetIcon = AssetManager.getIconTexture("Mesh").getTextureID();
					clicked = ImGui.imageButton(assetIcon, 100, 80);
					break;
				case Sprite:
					Sprite sprite = (Sprite) asset;
					assetIcon = AssetManager.getTexture(sprite.getTextureName()).getTextureID();
					if(!sprite.isTilemap()) {
						clicked = ImGui.imageButton(assetIcon, 100, 80);
					}else {
						float[] tile_cords = sprite.getFromTileMap(sprite.getCurrentTileIndex()-1);		
						clicked = ImGui.imageButton(assetIcon, 100, 80, tile_cords[0], tile_cords[1], tile_cords[4], tile_cords[5]);
					}				
					break;
				case Scene:
					assetIcon = AssetManager.getIconTexture("Scene").getTextureID();
					clicked = ImGui.imageButton(assetIcon, 100, 80);
					break;
				default:
					assetIcon = AssetManager.getIconTexture("FileIcon").getTextureID();
					clicked = ImGui.imageButton(assetIcon, 100, 80);
					break;
			}
			
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("buggy_asset", asset);
				ImGui.text(asset.getAssetType()+" : " + asset.getAssetName());
				ImGui.endDragDropSource();
			}
			
			if(ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
				switch(asset.getAssetType()) {
					case Scene:
						layer.currentScene = (Scene) asset;
						break;
				}
			}

			if(clicked) {
				layer.selectedAsset = asset.getAssetName();
			}
			
			ImVec2 text_size = new ImVec2();
			ImGui.calcTextSize(text_size , asset.getAssetName());
			
			ImGui.setCursorPosX((ImGui.getCursorPosX() + 100/2) - text_size.x/2.1f);
			ImGui.text(asset.getAssetName());
			ImGui.endGroup();
			ImGui.popID();

			i++;
			float lastPosX = ImGui.getItemRectMaxX();
			float nextPosX = lastPosX + ItemSpacingX + 100;

			if (nextPosX < windowX2) {
				ImGui.sameLine();
			}
		}
		
		ImGui.popStyleColor(3);

		ImGui.end();
	}

	private void contextMenu() {
		if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight | ImGuiPopupFlags.NoOpenOverItems)) {
			
			if(ImGui.beginMenu("Create")) {
				
				if(ImGui.beginMenu("Mesh")) {
					
					if(ImGui.menuItem("Cube")) {
						AssetManager.createCubeMesh();
					}
					
					ImGui.separator();
					if(ImGui.menuItem("From OBJ")) {
						
					}
					ImGui.endMenu();
				}
				

				if(ImGui.menuItem("Sprite")) {
					
					AssetManager.createTileMapSprite();
					
				}
				
				if(ImGui.menuItem("Scene")) {
					AssetManager.createNewScene();
				}
				
				ImGui.endMenu();
			}
			ImGui.endPopup();
		}
	}
}
