package gui;

import java.io.File;
import java.util.ArrayList;

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
	private static String my_computer_dir = "C:\\";
	
	private static ArrayList<String> back_dirs;
	private static ArrayList<String> front_dirs;
	
	private static float icon_width = 126;
	private static float icon_height = 98;
	
//	Icons
	private static String prev_icon = "ArrowLeft";
	private static String next_icon = "ArrowRight";
	
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
	
	private static File selectedFile;
	private static File filePicked;
	
	public static void open() {
		onClose = 0;
		current_path_dir = "assets";
		back_dirs = new ArrayList<>();
		front_dirs = new ArrayList<>();
	}
	
	public static File getFetchedFile() {
		return filePicked;
	}

	public static void render() {
		
		ImGui.setNextWindowSize(800, 420, ImGuiCond.Once);
		
		ImGui.pushStyleColor(ImGuiCol.ChildBg, ImColor.floatToColor(0.16f, 0.29f, 0.48f, 0.8f));
		
		ImGui.begin(title, ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking);
		
		if(ImGui.beginMenuBar()) {
			
			if(back_dirs.size() > 0) {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
				if(ImGui.imageButton(EngineManager.getIconTexture(prev_icon).getTextureID(), 20, 20)) {
					goBack();
				}
				ImGui.popStyleColor(3);
			}else {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0));
				if(ImGui.imageButton(EngineManager.getIconTexture(prev_icon).getTextureID(), 20, 20)) {
				}
				ImGui.popStyleColor(3);
			}
			
			if(front_dirs.size() > 0) {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
				if(ImGui.imageButton(EngineManager.getIconTexture(next_icon).getTextureID(), 20, 20)) {
					goFront();
				}
				ImGui.popStyleColor(3);
			}else {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0));
				if(ImGui.imageButton(EngineManager.getIconTexture(next_icon).getTextureID(), 20, 20)) {
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
		
		if(flagged) {
			selected = null;
			flagged = false;
		}
		
		Explorer();	
				
		ImGui.setCursorPosX(ImGui.getWindowWidth()-110);
		
		if(ImGui.button("Open")) {
			if(selectedFile != null) {
				filePicked = selectedFile;
				onClose = 1;
			}
			
		}
		ImGui.sameLine();
		if(ImGui.button("Close")) {
			onClose = 2;
		}
		ImGui.popStyleColor();
		ImGui.end();
	}
	
	private static void Explorer() {
		current_path = new File(current_path_dir);
		
		File[] current_path_list = current_path.listFiles();
		ImGui.beginChild("Explorer", 0, ImGui.getContentRegionAvailY()-35);
		
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
					selectedFile = file;
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
		ImGui.beginChild("File Hierachy", 150, ImGui.getContentRegionAvailY()-35);
        
		ImDrawList draw_list = ImGui.getWindowDrawList();
		
		File root = new File(root_dir);
		File my_computer = new File(my_computer_dir);
		
//		Add Child On TreeOpen
			
		if(root.isDirectory()) {
			float xPoint = ImGui.getCursorScreenPosX() + 1;
	        float yPoint = ImGui.getCursorScreenPosY();
			draw_list.addImage(EngineManager.getIconTexture(folder_icon).getTextureID(), xPoint, yPoint, xPoint+24, yPoint+18);
			boolean open = ImGui.treeNodeEx(root.getName(), ImGuiTreeNodeFlags.AllowItemOverlap|ImGuiTreeNodeFlags.OpenOnArrow);
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
				openDirectory(root);
			}
			if(open) {
				showChildren(root);
				ImGui.treePop();
			}	
		}
		
		if(my_computer.isDirectory()) {
			float xPoint = ImGui.getCursorScreenPosX() + 1;
	        float yPoint = ImGui.getCursorScreenPosY();
			draw_list.addImage(EngineManager.getIconTexture(folder_icon).getTextureID(), xPoint, yPoint, xPoint+24, yPoint+18);
			boolean open = ImGui.treeNodeEx(my_computer.getName(), ImGuiTreeNodeFlags.AllowItemOverlap|ImGuiTreeNodeFlags.OpenOnArrow);
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
				openDirectory(my_computer);
			}
			if(open) {
				showChildren(my_computer);
				ImGui.treePop();
			}
		}
	
		ImGui.endChild();
//		
//		================================================
	}
	
	private static void showChildren(File file) {
		File[] children_list = file.listFiles();
		for(int i=0; i<children_list.length; i++) {
			
			if(children_list[i].isHidden()) {
				continue;
			}
			
			if(children_list[i].isDirectory()) {
				ImGui.pushID(i);
				float xPoint = ImGui.getCursorScreenPosX() + 1;
		        float yPoint = ImGui.getCursorScreenPosY();
		        ImGui.getWindowDrawList().addImage(EngineManager.getIconTexture(folder_icon).getTextureID(), xPoint, yPoint, xPoint+24, yPoint+18);
				boolean open = ImGui.treeNodeEx(children_list[i].getName(), ImGuiTreeNodeFlags.AllowItemOverlap|ImGuiTreeNodeFlags.OpenOnArrow);
				if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
					openDirectory(children_list[i]);
				}
				if(open) {
					showChildren(children_list[i]);
					ImGui.treePop();
				}					
				
				ImGui.popID();
			}
			
		}
	}

	private static void openDirectory(File file) {
		if(file.exists()) {
			back_dirs.add(current_path_dir);
			current_path_dir = file.getPath();
			selectedFile = null;
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
			back_dirs.add(current_path_dir);
			if(front_dirs.size()>0) {
				front_dirs.clear();
			}
			current_path_dir = next_path;
			selectedFile = null;
			flagged = true;
		}
	}
	
	private static void openParent(File child) {

		String next_path = child.getParent();
		
		if(next_path!= null) {
			File file = new File(next_path);
			if(file.exists()) {
				back_dirs.add(current_path_dir);
				current_path_dir = next_path;
				selectedFile = null;
				flagged = true;
			}
		}		
		
	}
	
	private static void goBack() {

		int last_item = back_dirs.size()-1;
		String next_path = back_dirs.get(last_item);
		
		if(next_path!= null) {
			File file = new File(next_path);
			if(file.exists()) {
				back_dirs.remove(last_item);
				front_dirs.add(current_path_dir);
				current_path_dir = next_path;
				selectedFile = null;
				flagged = true;
			}
		}		
		
	}
	
	private static void goFront() {

		int last_item = front_dirs.size()-1;
		String next_path = front_dirs.get(last_item);
		
		if(next_path!= null) {
			File file = new File(next_path);
			if(file.exists()) {
				front_dirs.remove(last_item);
				back_dirs.add(current_path_dir);
				current_path_dir = next_path;
				selectedFile = null;
				flagged = true;
			}
		}		
		
	}
}
