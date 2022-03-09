package leveleditor;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.EntityManager;
import entities.DebugDrawableObject;
import entities.Entity;
import entities.Sprite;
import entitiesComponents.Component;
import entitiesComponents.SpriteRenderer;
import entitiesComponents.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import loaders.LevelLoader;
import main.Window;
import maths.Maths;
import opengl.Framebuffer;
import opengl.VertexArrayObject;
import renderer.Renderer;
import renderer.RendererDebug;
import scenes.Layer;
import scenes.Scene;

public class LevelEditorLayer extends Layer {
	
	Renderer renderer;
	Framebuffer screen;
	
	private Scene levelScene = new LevelEditorScene();
	private String selectedEntity;
	private RendererDebug renderer2;
	
	VertexArrayObject lines;
	float[] pixelData;
	
	@Override
	public void attach() {
		
		screen = new Framebuffer(1024, 600);
		renderer = new Renderer();
		renderer2 = new RendererDebug();
		
		LevelLoader.ready();		
		LevelLoader.load();
		System.out.println("Level Loaded");
		levelScene.init();	
		levelScene.findCameras();
		
		lines = DebugDrawableObject.makeGridlines(0, 10.0f, 1.0f, -0.5f);
	}

	@Override
	public void detach() {
		levelScene.close();
		screen.cleanup();
	}

	@Override
	public void render() {
		screen.bind();
		renderer.clear();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.960f);
		renderer2.drawLines(EntityManager.entities.get(levelScene.n_mainCamera), lines);
		renderer2.drawArrow(EntityManager.entities.get(levelScene.n_mainCamera));
		levelScene.render(renderer);
		pixelData = screen.readPixelData(1, (int)viewportMouseHoverX , (int)viewportMouseHoverY);
		screen.unbind();	
		renderInterfaces();
	}

	@Override
	public void update(double dt) {
		levelScene.update(dt);
	}
	
	public void renderInterfaces() {
		beginDockspace();
	}
	
	private void beginDockspace() {
		//  DOCKSPACE
		int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
		
		ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(Window.get().width(), Window.get().height());
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
		windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
				ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus |
				ImGuiWindowFlags.NoNavFocus;
		
		ImGui.begin("DockspaceBackground", new ImBoolean(true), windowFlags);
		ImGui.popStyleVar(2);
		
		int dockspace_id = ImGui.getID("ActualDockspace");
		ImGui.dockSpace(dockspace_id);
			
		beginScreen();
		beginAssetExplorer();
				
		beginEntityInspector();
		beginEntityExplorer();
		
		ImGui.showDemoWindow();
		// For Dock space
    	ImGui.end();
	}
	
	float cursorPosX;
	float cursorPosY;
	float sizeX;
	float sizeY;
	float posX;
	float posY;
	float ndsX;
	float ndsY;
	float gameX;
	float gameY;
	
	float menuSizeX;
	float menuSizeY;
	
	float windowSizeX;
	float windowSizeY;
	
	float tabSizeY;
	
	Vector2f viewportSize;
	Vector2f viewportCursor;
	Vector2f viewportPos;
	float viewportMouseHoverX;
	float viewportMouseHoverY;
	/**
     * Game Screen (using Framebuffer).
     */
	private void beginScreen() {
		ImGui.setNextWindowSize(screen.getFramBufferWidth(), screen.getFramBufferHeight(), ImGuiCond.Always);
	       
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.begin("Game", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoTitleBar);
        
        cursorPosX = ImGui.getMousePosX();
        cursorPosY = ImGui.getMousePosY();
        
        sizeX = ImGui.getWindowSizeX();
        sizeY = ImGui.getWindowSizeY();
        
        windowSizeX = ImGui.getContentRegionAvailX();
    	windowSizeY = ImGui.getContentRegionAvailY();
        
        posX = ImGui.getWindowPosX();
        posY = ImGui.getWindowPosY();
        
        tabSizeY = sizeY - ImGui.getContentRegionAvailY();
        
        ndsX = ((cursorPosX-posX)/windowSizeX) * 2 - 1;
        ndsY =  1 - 2 * ((cursorPosY-(tabSizeY + posY))/windowSizeY);
        
        ndsX = Maths.clamp(-1, 1, ndsX);
        ndsY = Maths.clamp(-1, 1, ndsY);
        // Calculate Size of Window
        ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Window.get().TargetAspectRatio();
		if(aspectHeight > windowSize.x) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.get().TargetAspectRatio();
		}        
		ImVec2 aspectSize = new ImVec2(aspectWidth, aspectHeight);
        
        float viewportX = ((windowSize.x/2.0f) - (aspectSize.x /2.0f)) + ImGui.getCursorPosX();
		float viewportY = ((windowSize.y/2.0f) - (aspectSize.y /2.0f)) + ImGui.getCursorPosY();
		
		ImGui.setCursorPos(viewportX, viewportY);
		
		viewportPos = new Vector2f(ImGui.getWindowPosX(), ImGui.getWindowPosY());
		viewportCursor = new Vector2f(ImGui.getWindowPosX() + ImGui.getCursorPosX(), ImGui.getWindowPosY() + ImGui.getCursorPosY());
		viewportSize = new Vector2f(ImGui.getWindowSizeX(), ImGui.getWindowSizeY());
		
		viewportMouseHoverX =  ((cursorPosX - viewportCursor.x)/aspectWidth) * screen.getFramBufferWidth(); 
		viewportMouseHoverY = ((aspectHeight - (cursorPosY - viewportCursor.y))/aspectHeight) * screen.getFramBufferHeight();
				
        ImGui.popStyleVar();
        ImGui.popStyleVar();
        
        ImGui.image(screen.getTexture(0), aspectWidth, aspectHeight, 0, 1, 1, 0);
        
        if(ImGui.isItemHovered()) {
        	if(ImGui.isMouseClicked(ImGuiButtonFlags.MouseButtonLeft)) {
        		selectEntity();
        	}
        }
        ImGui.end();
	}
	
	private void selectEntity() {
		
		for(Entity entity: EntityManager.entities.values()) {
			
			if(!entity.isCamera()) {
				int id = EntityManager.getId(entity.getName());
				if((int)pixelData[0] == id) {
					this.selectedEntity = entity.getName();
					break;
				}
			}
		}
	}

	private void beginAssetExplorer() {
		ImGui.begin("Assets");
        
	        ImGui.text("screenX:"+ndsX+"  screenY:"+ndsY);
	        
	        ImGui.text("ViewPortX:"+viewportMouseHoverX+"  Y:"+viewportMouseHoverY);
	        
	        ImGui.text("r: "+(int)pixelData[0] + " g: "+ pixelData[1] + " b: "+ pixelData[2]);
			
		ImGui.end();
	}
	
	ImString new_entityName = new ImString();
	ImInt new_entityType = new ImInt();
	
	private void beginEntityExplorer() {
		ImGui.begin("EntityExplorer");
			
			if(ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight)) {
				
				if(ImGui.beginMenu("Add Entity")) {
					
					if(ImGui.menuItem("Empty Entity")) {
						
					}
					
					if(ImGui.menuItem("Sprite")) {
						Entity new_Entity = new Entity();
						String new_entityName = EntityManager.createEntity("Sprite");
						new_Entity.setName(new_entityName);
						new_Entity.addComponent(new Transform(new Vector3f(0,0,0)));
						new_Entity.addComponent(new SpriteRenderer(new Sprite(new Vector3f(0,1,1))));
						new_Entity.start();
						EntityManager.entities.put(new_entityName, new_Entity);
					}
					
					ImGui.endMenu();
				}
				
				ImGui.endPopup();
			}
					
			for(Entity entity: EntityManager.entities.values()) {
				if(ImGui.collapsingHeader(entity.getName(), ImGuiTreeNodeFlags.DefaultOpen)) {
					ImGui.text("Components: "+entity.getComponents().size());
				}
			}
		
			
		
		ImGui.end();
	}
	

	/**
	 * 
	 * EntityInspector shows the details of the current entity selected
	 * 
	 */
	private void beginEntityInspector() {
		ImGui.begin("EntityInspector");
		
			if(this.selectedEntity != null) {
				ImGui.text(selectedEntity);
				Entity entity = EntityManager.entities.get(selectedEntity);
				
				if(ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight)) {
					
					if(ImGui.beginMenu("Add Component")) {
						
						if(ImGui.menuItem("Sprite Renderer")) {
							
						}
						
						ImGui.endMenu();
					}
					
					ImGui.endPopup();
				}
				
				for(Component component: entity.getComponents()) {
					
					if(ImGui.collapsingHeader(component.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen)) {
						component.UI();
					}
					
				}
				
				
			}
			
		ImGui.end();
	}

}
