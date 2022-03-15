package gui;

import java.io.File;

import engine.EngineManager;
import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class FileExplorer
{

	public static String title = "FileExplorer";
	
	private static File current_path;
	private static String current_path_dir = "assets";
	
	private static String root_dir = "assets";
	
	private static String prev_dir;
	
	private static float icon_width = 156;
	private static float icon_height = 128;

	private static String prev_icon = "ArrowLeft";
	
	private static String folder_icon = "FolderIcon";
	private static String general_file_icon = "FileIcon";
	private static String image_file_icon = "FileIconImage";
	private static String txt_file_icon = "FileIconText";
	private static String shader_vs_file_icon = "FileIconShaderVS";
	private static String shader_fs__file_icon = "FileIconShaderFS";
	
	static boolean[] selected;

	private static boolean flagged = false;

	private static ImBoolean isNavPaneActive = new ImBoolean(true);

	public static int onClose;
	
	public static void open() {
		onClose = 0;
		current_path_dir = "assets";
	}

	public static void render() {
		
		ImGui.setNextWindowSize(800, 420, ImGuiCond.Once);
		ImGui.begin(title, ImGuiWindowFlags.MenuBar);
		
		if(ImGui.beginMenuBar()) {
			
			if(prev_dir != null) {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
   
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
				if(ImGui.imageButton(EngineManager.getIconTexture(prev_icon).getTextureID(), 20, 20)) {
					openParent();
				}
				ImGui.popStyleColor(3);
			}
			
			ImGui.checkbox("showNavPane" , isNavPaneActive );
						
			ImGui.endMenuBar();
		}
		
		if(isNavPaneActive.get()) {
			showFileHierarchy();
			ImGui.sameLine();
		}
		
			
		
		Explorer();
		
		if(flagged ) {
			selected = null;
		}
		
		ImGui.separator();
		
		if(ImGui.button("Open")) {
			onClose = 1;
		}
		
		ImGui.end();
	}
	
	private static void Explorer() {
		current_path = new File(current_path_dir);
		
		File[] current_path_list = current_path.listFiles();
		ImGui.beginChild("Explorer");
		
		if(selected == null) {
			selected = new boolean[current_path_list.length];
		}
				
		for(int i=0; i<current_path_list.length; i++) {
			
			File file = current_path_list[i];
			
			if(i>selected.length) {
				selected = new boolean[current_path_list.length];
			}
			
			float WindowPosX = ImGui.getWindowPosX();
        	float WindowSizeX = ImGui.getWindowSizeX();
        	float ItemSpacingX = ImGui.getStyle().getItemSpacingX();
        	float windowX2 = WindowPosX + WindowSizeX;
        	
        	if(i<=selected.length && selected[i] == true) {
        		ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0.2f));
        	}else {
        		ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
        	}
   
			ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
			ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
			
			ImGui.pushID(i);
			if(file.isDirectory()) {
				ImGui.beginGroup();
				
				if(ImGui.imageButton(EngineManager.getIconTexture(folder_icon).getTextureID(), icon_width, icon_height)) {
					if(selected[i] == true) {
						openPath(file.getName());
					}else {
						selected = new boolean[current_path_list.length];
						selected[i] = true;
					}
					
				}
				
				ImVec2 text_size = new ImVec2();
				ImGui.calcTextSize(text_size , file.getName());
				
				ImGui.setCursorPosX((ImGui.getCursorPosX() + icon_width/2) - text_size.x/2.1f);
				ImGui.text(file.getName());
				
				ImGui.endGroup();
			}else {
				ImGui.beginGroup();
					
				String ext = getFileExtension(file);
				String fileicon_name;
				
				switch (ext) 
				{
					case "vs":
						fileicon_name = shader_vs_file_icon; 
						break;
					case "fs":
						fileicon_name = shader_fs__file_icon; 
						break;
					case "png":
						fileicon_name = image_file_icon; 
						break;
					case "txt":
						fileicon_name = txt_file_icon; 
						break;
					
					default:
						fileicon_name = general_file_icon;
						break;
				}
				
				if(ImGui.imageButton(EngineManager.getIconTexture(fileicon_name).getTextureID(), icon_height, icon_width)) {
					if(selected[i] == true) {
						openPath(file.getName());
					}else {
						selected = new boolean[current_path_list.length];
						selected[i] = true;
					}
					
				}
				
				ImVec2 text_size = new ImVec2();
				ImGui.calcTextSize(text_size , file.getName());
				
				ImGui.setCursorPosX((ImGui.getCursorPosX() + icon_width/2) - text_size.x/2.1f);
				ImGui.text(file.getName());
				
				ImGui.endGroup();
			}
			ImGui.popID();
			
			ImGui.popStyleColor(3);
        	
			float lastPosX = ImGui.getItemRectMaxX();
			float nextPosX = lastPosX + ItemSpacingX + 100;
			
    		if(nextPosX < windowX2) {
    			ImGui.sameLine();
    		}		
			
		}
		
		ImGui.endChild();
	}

	private static void showFileHierarchy() {
//		File Explorer Hierarchy
		ImGui.beginChild("File Hierachy", 150, 0, true);
        
		ImDrawList draw_list = ImGui.getWindowDrawList();
		
		File[] root_path_list = new File(root_dir).listFiles();
		for(int i=0; i<root_path_list.length; i++) {
			
			File file = root_path_list[i];
			
			ImGui.pushID(i);
			if(file.isDirectory()) {
				float xPoint = ImGui.getCursorScreenPosX() + 1;
		        float yPoint = ImGui.getCursorScreenPosY();
				draw_list.addImage(EngineManager.getIconTexture(folder_icon).getTextureID(), xPoint, yPoint, xPoint+24, yPoint+18);
				if(ImGui.treeNodeEx(file.getName(), ImGuiTreeNodeFlags.Leaf|ImGuiTreeNodeFlags.AllowItemOverlap)) {
					if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
						openDirectory(file);
					}		
					ImGui.treePop();
				}
			}
			ImGui.popID();
		}
	
		ImGui.endChild();
//		
//		================================================
	}

	private static void openDirectory(File file) {
		if(file.exists()) {
			prev_dir = current_path_dir;
			current_path_dir = file.getPath();
			flagged = true;
		}
	}

	private static String getFileExtension(File file) {
		String file_name = file.getName();
		String[] file_name_ = file_name.split("\\.");
		if(file_name_.length > 0) {
			String ext = file_name_[file_name_.length-1];
			return ext;
		}
		return "";
	}

	private static void openPath(String name) {
		
		String next_path = current_path_dir+"/"+name;
		
		File file = new File(next_path);
		if(file.exists()) {
//			System.out.println("Found : "+next_path);
			prev_dir = current_path_dir;
			current_path_dir = next_path;
			flagged = true;
		}
	}
	
	private static void openParent() {

		String next_path = current_path.getParent();
		
		if(next_path!= null) {
			File file = new File(next_path);
			if(file.exists()) {
//				System.out.println("Found : "+next_path);
				prev_dir = current_path_dir;
				current_path_dir = next_path;
				flagged = true;
			}
		}
		
		
	}
}
