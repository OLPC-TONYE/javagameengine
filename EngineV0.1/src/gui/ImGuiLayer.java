package gui;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import main.Window;

public class ImGuiLayer {

	public static long glfwWindow = Window.get().glfwWindow;
	private static String glslVersion = "#version 120" ;
	
    public static ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public static ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public static void begin() {
    	
    	ImGui.createContext();
    	
    	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to find the GLFW window");
        }
    	
        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();
        
        io.setIniFilename("desk"); 
 
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
//      io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable); Does'nt work
        
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontAtlas.addFontFromFileTTF("assets/fonts/LatoItalic.ttf", 20, fontConfig);
        fontConfig.destroy();
        fontAtlas.build();
        
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);
    }
    
    public static void end() {
    	imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public static void beginFrame() {
    	imGuiGlfw.newFrame();
        ImGui.newFrame();
    }
    
    public static void endFrame() {
    	ImGui.render();
    	imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
}
