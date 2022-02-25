package leveleditor;

import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.EngineManager;
import engine.EntityManager;
import entities.Entity;
import entities.Sprite;
import entitiesComponents.CameraComponent;
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
import maths.ListofFloats;
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
		
		float fExtent = 10.0f;
		float fStep = 1.0f;
		float y = -0.5f;
		int iLine;
		
		ListofFloats linesvertices = new ListofFloats();
		for(iLine = (int) -fExtent; iLine <= fExtent; iLine += fStep) {
			// Draw Z lines
			float[] verticesZ = new float[]{iLine, y, fExtent, iLine, y, -fExtent};
			linesvertices.add(verticesZ);
		
			float[] verticesX = new float[]{fExtent, y, iLine, -fExtent, y, iLine};
			linesvertices.add(verticesX);
		}
		lines = EngineManager.createLine(linesvertices.toArray());
	}

	@Override
	public void detach() {
		levelScene.close();
	}

	@Override
	public void render() {
		screen.bind();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.960f);
		renderer2.render(EntityManager.entities.get(levelScene.n_mainCamera), lines);
		levelScene.render(renderer);
		screen.unbind();	
		renderInterfaces();
	}

	@Override
	public void update(double dt) {
		levelScene.update(dt);
	}
	
	public void renderInterfaces() {
		
		beginDockspace();	
		beginScreen();
		beginAssetExplorer();
				
		beginEntityInspector();
		beginEntityExplorer();
		
		ImGui.showDemoWindow();
		// For Dock space
    	ImGui.end();
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
	}
	
	float cursorPosX;
	float cursorPosY;
	float sizeX;
	float sizeY;
	float PosX;
	float PosY;
	float ndsX;
	float ndsY;
	float gameX;
	float gameY;
	
	float menuSizeX;
	float menuSizeY;
	
	float windowSizeX;
	float windowSizeY;
	
	float tabSizeY;
	/**
     * Game Screen (using Framebuffer).
     */
	private void beginScreen() {
		ImGui.setNextWindowSize(screen.getFramBufferWidth(), screen.getFramBufferHeight(), ImGuiCond.Always);
	       
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.begin("Game", ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoTitleBar);
        
        cursorPosX = ImGui.getMousePosX();
        cursorPosY = ImGui.getMousePosY();
        
        sizeX = ImGui.getWindowSizeX();
        sizeY = ImGui.getWindowSizeY();
        
        windowSizeX = ImGui.getContentRegionAvailX();
    	windowSizeY = ImGui.getContentRegionAvailY();
        
        PosX = ImGui.getWindowPosX();
        PosY = ImGui.getWindowPosY();
        
        tabSizeY = sizeY - ImGui.getContentRegionAvailY();
        
        ndsX = ((cursorPosX-PosX)/windowSizeX) * 2 - 1;
        ndsY =  1 - 2 * ((cursorPosY-(tabSizeY + PosY))/windowSizeY);
        
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
        
        ImGui.popStyleVar();
        ImGui.popStyleVar();
        
        ImGui.beginMenuBar();
        	
        	menuSizeX = ImGui.getContentRegionAvailX();
        	menuSizeY = sizeY - ImGui.getContentRegionAvailY();
        	ImGui.setCursorPosX(menuSizeX/4);
        	ImGui.pushItemWidth(menuSizeX/4);
        	        	
        	String[] items = levelScene.cameras.toArray(new String[levelScene.cameras.size()]);
        	ImInt current_camera = new ImInt(levelScene.cameras.indexOf(levelScene.n_mainCamera));
        	if(ImGui.combo("Camera", current_camera, items)) {
        		
        	}
        	
        	ImGui.popItemWidth();
     
        ImGui.endMenuBar();
        
        ImGui.image(screen.getTexture(), aspectWidth, aspectHeight, 0, 1, 1, 0);
        
        if(ImGui.isItemHovered()) {
        	if(ImGui.isMouseClicked(ImGuiButtonFlags.MouseButtonLeft)) {
        		selectEntity();
        	}
        }
        ImGui.end();
	}
	

	private void selectEntity() {
		Transform camera_transform = EntityManager.entities.get(levelScene.n_mainCamera).getComponent(Transform.class);
		Vector3f origin = camera_transform.getPosition();
		
		CameraComponent camera = EntityManager.entities.get(levelScene.n_mainCamera).getComponent(CameraComponent.class);
		Vector3f ray_direction = Maths.calculateRayVector(ndsX, ndsY, camera);
		
		for(Entity entityRenderable: EntityManager.entities.values()) {
			
			if(!entityRenderable.isCamera()) {
				Transform entity_transform = entityRenderable.getComponent(Transform.class);
				Vector3f position = entity_transform.getPosition();
				Vector3f scale = entity_transform.getScale();
				if(Maths.intersectEntityuPlane(origin, ray_direction, position, scale, new Vector3f(0, 0, 1))) {
					this.selectedEntity = entityRenderable.getName();
					break;
				}
			}
		}
	}

	private void beginAssetExplorer() {
		ImGui.begin("Assets");
        
	        ImGui.text("screenX:"+ndsX+"  screenY:"+ndsY);
	        
	        CameraComponent camera = EntityManager.entities.get(levelScene.n_mainCamera).getComponent(CameraComponent.class);
	        Vector4f gameViewCords = Maths.calculateGameViewportCords(camera, ndsX, ndsY);
			
			ImGui.text("gameCordX:"+gameViewCords.x+"  gameCordY:"+gameViewCords.y);
			
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
