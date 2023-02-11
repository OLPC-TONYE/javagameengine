package interfaces;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gui.ImGuiLayer;
import gui.ImGuiWindowLayer;
import imgui.ImColor;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImString;
import managers.AssetManager;

public class FileExplorer {
	
	private int height = 420;
	private int width = 800;
	private String title = "Explorer";
	private File current_path;
	private String current_path_dir = "assets";

	boolean[] selected;
	boolean refresh;
	
	private String extensionFilter;
	
//	Navigation
	private String root_dir = "assets";
	private String my_computer_dir = "C:\\";

	private ArrayList<String> back_dirs = new ArrayList<>();
	private ArrayList<String> front_dirs = new ArrayList<>();
	
//	Return File
	private String current_selected_file = null;
	
//	Icons
	private float icon_width, icon_height;
	
	private final String prev_icon = "ArrowLeft";
	private final String next_icon = "ArrowRight";
	
	private final String folder_icon = "FolderIcon";
	private final String general_file_icon = "FileIcon";
	private final String image_file_icon = "FileIconImage";
	private final String txt_file_icon = "FileIconText";
	private final String shader_vs_file_icon = "FileIconShaderVS";
	private final String shader_fs__file_icon = "FileIconShaderFS";
	
//	FileManager Variables
	enum State {
		Open,
		Save
	}
	
	private State currentState;
	
	public FileExplorer(String title) {
		this.title = title;
	}
	
	public void render() {
		
		int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking;
		ImGui.setNextWindowSize(width, height, ImGuiCond.Always);
		ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.FirstUseEver);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
		windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize
				| ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

		ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.16f, 0.29f, 0.48f, 0.9f);
			
		ImGui.begin(title, windowFlags);
		
		ImGui.popStyleColor();
		ImGui.popStyleVar(3);
		
		int dockspace_id = ImGui.getID("FileExplorerDock");
		ImGui.dockSpace(dockspace_id);	
		
		menuBar();
				
		showFileHierarchy();
		explorer();
		buttons();
		
		ImGui.end();			
	}

	private ImString text = new ImString();
	private void buttons() {
		ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.16f, 0.16f, 0.16f, 1f);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.16f, 0.16f, 0.16f, 0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.16f, 0.16f, 0.16f, 0f);
		ImGui.begin("ButtonsFileBrowser", ImGuiWindowFlags.NoTitleBar);
		ImGui.popStyleColor(3);
		
		ImGui.pushID("textBar");
		ImGui.text("File Name: ");
		ImGui.sameLine();
		if(ImGui.inputText("", text, ImGuiInputTextFlags.NoUndoRedo)) {
			
		}
		ImGui.popID();
		
		if(ImGui.button(currentState.name())) {
			if(currentState == State.Open) {
				if(current_selected_file != null) {
					openFilePath(new File(current_selected_file));
				}			
			}else if(currentState == State.Save) {
				if(text.isNotEmpty()) {
					saveInPath(saveData);
				}				
			}
			
		}
		ImGui.sameLine();
		ImGui.button("Cancel");
		
		ImGui.end();
	}

	private void menuBar() {
		ImGui.pushStyleColor(ImGuiCol.MenuBarBg, 0.16f, 0.16f, 0.16f, 1f);
		if(ImGui.beginMenuBar()) {
		ImGui.popStyleColor();
		
			if(back_dirs.size() > 0) {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
				if(ImGui.imageButton(AssetManager.getIconTexture(prev_icon).getTextureID(), 20, 20)) {
					previousDirectory();
				}
				ImGui.popStyleColor(3);
			}else {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0));
				if(ImGui.imageButton(AssetManager.getIconTexture(prev_icon).getTextureID(), 20, 20)) {
				}
				ImGui.popStyleColor(3);
			}
			
			if(front_dirs.size() > 0) {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0.2f));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0.2f));
				if(ImGui.imageButton(AssetManager.getIconTexture(next_icon).getTextureID(), 20, 20)) {
					formerDirectory();
				}
				ImGui.popStyleColor(3);
			}else {
				ImGui.pushStyleColor(ImGuiCol.Button, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(1, 1, 1, 0));
				ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.floatToColor(1, 1, 1, 0));
				if(ImGui.imageButton(AssetManager.getIconTexture(next_icon).getTextureID(), 20, 20)) {
				}
				ImGui.popStyleColor(3);
			}
			
			ImGui.endMenuBar();
		}
	}
	
	private void showFileHierarchy() {
		
		ImGui.setNextWindowSize(150f, 50f, ImGuiCond.Once);
		ImGui.pushStyleColor(ImGuiCol.Button, 0.16f, 0.16f, 0.16f, 0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.16f, 0.16f, 0.16f, 0f);
//		File Explorer Hierarchy
		ImGui.begin("File Hierachy", ImGuiWindowFlags.NoTitleBar);
        ImGui.popStyleColor(2);		
		
		File root = new File(root_dir);
		File my_computer = new File(my_computer_dir);
		
//		Add Child On TreeOpen
			
		if(root.isDirectory()) {
			float width = ImGui.getCursorPosX();
			boolean open = ImGui.treeNodeEx("        "+root.getName(), ImGuiTreeNodeFlags.AllowItemOverlap|ImGuiTreeNodeFlags.OpenOnArrow);
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
				openDirectory(root);
			}
			ImGui.sameLine(width+ImGui.getFont().getFontSize());
			ImGui.image(AssetManager.getIconTexture(folder_icon).getTextureID(), 24, 18);
			if(open) {
				showChildren(root);
				ImGui.treePop();
			}	
		}
		
		if(my_computer.isDirectory()) {
			float width = ImGui.getCursorPosX();
			boolean open = ImGui.treeNodeEx("        "+my_computer.getName(), ImGuiTreeNodeFlags.AllowItemOverlap|ImGuiTreeNodeFlags.OpenOnArrow);
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
				openDirectory(my_computer);
			}
			ImGui.sameLine(width+ImGui.getFont().getFontSize());
			ImGui.image(AssetManager.getIconTexture(folder_icon).getTextureID(), 24, 18);
			if(open) {
				showChildren(my_computer);
				ImGui.treePop();
			}
		}
	
		ImGui.end();
	}
	
	private void showChildren(File file) {
		File[] children_list = file.listFiles();
		for(int i=0; i<children_list.length; i++) {
			
			if(children_list[i].isHidden()) {
				continue;
			}
			
			if(children_list[i].isDirectory()) {
				ImGui.pushID(i);
				float width = ImGui.getCursorPosX();
				boolean open = ImGui.treeNodeEx("       "+children_list[i].getName(), ImGuiTreeNodeFlags.AllowItemOverlap|ImGuiTreeNodeFlags.OpenOnArrow);
				if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)){
					openDirectory(children_list[i]);
				}
				ImGui.sameLine(width+ImGui.getFont().getFontSize());
				ImGui.image(AssetManager.getIconTexture(folder_icon).getTextureID(), 24, 18);
				if(open) {
					showChildren(children_list[i]);
					ImGui.treePop();
				}								
				ImGui.popID();
			}
			
		}
	}

	private void explorer() {
		
		ImGui.pushStyleColor(ImGuiCol.Button, 0.16f, 0.16f, 0.16f, 0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.16f, 0.16f, 0.16f, 0f);
		ImGui.begin("FileBrowser", ImGuiWindowFlags.NoTitleBar);
		ImGui.popStyleColor(2);
		
		current_path = new File(current_path_dir );
		File[] current_path_list = current_path.listFiles();
		
		if(selected == null) {
			selected = new boolean[current_path_list.length];
		}
		
		if(refresh) {
			selected = new boolean[current_path_list.length];
			current_selected_file = null;
			refresh = false;
		}
			
		ImGui.pushStyleVar(ImGuiStyleVar.SelectableTextAlign, 0.5f, 1f);
		
		for(int i=0; i < current_path_list.length; i++) {
			
			String file_name = current_path_list[i].getName();
			String fileicon_name  = "";
			if(current_path_list[i].isDirectory()) {
				fileicon_name = folder_icon;
				icon_width = 80;
				icon_height = 50;
			}else {
				String ext = getFileExtension(current_path_list[i]);
				if(extensionFilter != null) {
					if(!ext.contains(extensionFilter)) continue;
				}				
				fileicon_name = determineFileIcon(ext);
			}
			
			 
					
			ImGui.beginGroup();
			
			ImGui.pushID(i);
			float aspectIcon = icon_width > icon_height ? icon_width: icon_height;
			if(ImGui.selectable(file_name, selected[i], ImGuiSelectableFlags.AllowItemOverlap|ImGuiSelectableFlags.AllowDoubleClick, (aspectIcon+10), (aspectIcon+20))) {
				if(!ImGui.getIO().getKeyCtrl()) {
					selected = new boolean[current_path_list.length];
				}
				selected[i] = !selected[i];
				current_selected_file = current_path_list[i].getPath();
				text.set(file_name);
				if(ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
					if(current_path_list[i].isDirectory()) {
						openDirectory(current_path_list[i]);
					}else {
						openFilePath(current_path_list[i]);
					}
				}
			}
			ImGui.sameLine();
			ImGui.setCursorPosX(ImGui.getCursorPosX()-(aspectIcon+10));
			ImGui.image(AssetManager.getIconTexture(fileicon_name).getTextureID(), icon_width, icon_height);
			ImGui.popID();
			
			ImGui.endGroup();
			
			ImGui.sameLine();
		}
		
		ImGui.popStyleVar();
		ImGui.end();
	}
	
	private void previousDirectory() {
		int last_dir = back_dirs.size()-1;
		String file_path = back_dirs.get(last_dir);
		File file = new File(file_path);
		if(file.exists()) {
			front_dirs.add(current_path_dir);
			current_path_dir = file.getPath();
			current_selected_file = null;
			back_dirs.remove(last_dir);
			text.clear();
			refresh = true;
		}
	}
	
	private void formerDirectory() {
		int last_dir = front_dirs.size()-1;
		String file_path = front_dirs.get(last_dir);
		File file = new File(file_path);
		if(file.exists()) {
			back_dirs.add(current_path_dir);
			current_path_dir = file_path;
			current_selected_file = null;
			front_dirs.remove(last_dir);
			text.clear();
			refresh = true;
		}
	}
	
	private void openDirectory(File file) {
		if(file.exists()) {
			back_dirs.add(current_path_dir);
			front_dirs.clear();
			current_path_dir = file.getPath();
			current_selected_file = null;
			text.clear();
			refresh = true;
		}
	}
	
	private void openFilePath(File file) {
		if(file.exists()) {
			current_selected_file = file.getPath();
			searching = false;
		}
	}
	
	private void saveInPath(String data) {
		File saveFile = new File(current_path_dir+File.separatorChar+text);
		if(saveFile.exists()) {
			System.out.print("Already Existed: ");
		}else {
			if(extensionFilter != null) {
				String saveFileExtension = getFileExtension(saveFile);
				if(!saveFileExtension.contains(extensionFilter)) {
					saveFile = new File(current_path_dir+File.separatorChar+text+"."+extensionFilter);
				}
			}
			
			try {
				FileWriter typist = new FileWriter(saveFile);
				typist.write(data);
				typist.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print("Created: ");
		}
		System.out.println(saveFile.getPath());
		searching = false;
	}

	private String getFileExtension(File file) {
		String file_name = file.getName();
		String[] file_name_ = file_name.split("\\.");
		if(file_name_.length > 1) {
			String ext = file_name_[file_name_.length-1];
			return ext;
		}
		return "";
	}
	
	private String determineFileIcon(String ext) {
		String fileicon_name;
		
		switch (ext) 
		{
			case "vs":
				fileicon_name = shader_vs_file_icon;
				icon_width = 60;
				icon_height = 80;
				
				break;
			case "fs":
				fileicon_name = shader_fs__file_icon; 
				icon_width = 60;
				icon_height = 80;
				
				break;
			case "png":
				fileicon_name = image_file_icon;
				icon_width = 60;
				icon_height = 70;
				
				break;
			case "txt":
				fileicon_name = txt_file_icon; 
				icon_width = 60;
				icon_height = 80;
				
				break;
				
			default:
				fileicon_name = general_file_icon;
				icon_width = 47;
				icon_height = 70;
				
				break;
		}
		
		return fileicon_name;
	}

	boolean searching;
	private boolean returnNullFlag;
	
	public String fetchFile() {
			
		ImGuiLayer.dispose();
		
		currentState = State.Open;
		searching = true;
		refresh = true;
		
		ImGuiWindowLayer window = new ImGuiWindowLayer(title, width, height);
		
		while(searching) {
			window.updateWindowSpecs();	
			width = window.getWidth();
			height = window.getHeight();
			
			window.beginFrame();
	        
			render();
		
			window.endFrame();	    	
	    	window.updateWindow();
			
			if(window.isClosing()) {
				searching = false;
				returnNullFlag = true;
			}
		}
		
		window.destroyWindow();
		
 		ImGuiLayer.resumeFrame();
 		
		if(returnNullFlag) return "";
		
		return current_selected_file;
	}

	private String saveData;
	
	public void saveFile(String data) {
		saveFile(data, null);
	}
	
	public void saveFile(String data, String extension) {
		
		ImGuiLayer.dispose();
		
		currentState = State.Save;
		extensionFilter = extension;
		saveData = data;
		
		searching = true;
		refresh = true;
		
		ImGuiWindowLayer window = new ImGuiWindowLayer(title, width, height);
		
		while(searching) {
			window.updateWindowSpecs();	
			width = window.getWidth();
			height = window.getHeight();
			
			window.beginFrame();
	        
			render();
		
			window.endFrame();	    	
	    	window.updateWindow();
			
			if(window.isClosing()) {
				searching = false;
				returnNullFlag = true;
			}
		}
		
		saveData = null;
		extensionFilter = null;
		
		window.destroyWindow();
		
 		ImGuiLayer.resumeFrame();

	}
}
