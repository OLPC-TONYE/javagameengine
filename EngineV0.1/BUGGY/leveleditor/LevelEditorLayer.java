package leveleditor;

import org.joml.Vector2f;
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
	Framebuffer picking;
	
	private Scene levelScene = new LevelEditorScene();
	private String selectedEntity;
	private RendererDebug renderer2;
	
	VertexArrayObject lines;
	float[] pixelData;
	@Override
	public void attach() {
		
		screen = new Framebuffer(1024, 600);
//		picking = new Framebuffer(1, 1024, 600);
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
//		picking.bind();
//		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.960f);
//		levelScene.render(renderer);
//		picking.unbind();
		
		screen.bind();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.960f);
		renderer2.render(EntityManager.entities.get(levelScene.n_mainCamera), lines);
		levelScene.render(renderer);
		pixelData = screen.readPixelData( 1, (int)viewportMouseHoverX, (int)viewportMouseHoverY);
		screen.unbind();	
		renderInterfaces();
	}

	@Override
	public void update(double dt) {
		levelScene.update(dt);
	}
	
	public void renderInterfaces() {
		Transform camera_transform = EntityManager.entities.get(levelScene.n_mainCamera).getComponent(Transform.class);
		origin = camera_transform.getPosition();
		
		CameraComponent camera = EntityManager.entities.get(levelScene.n_mainCamera).getComponent(CameraComponent.class);
		ray_direction = Maths.calculateRayVector(ndsX, ndsY, camera);
		
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
		
		viewportMouseHoverX =  cursorPosX - viewportCursor.x; 
		viewportMouseHoverY = aspectHeight - (cursorPosY - viewportCursor.y);
				
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
	
	Vector3f origin;
	Vector3f ray_direction;
	Vector3f position = new Vector3f();
	Vector3f scale = new Vector3f();
	
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
	        
	        ImGui.text("r: "+pixelData[0] + " g: "+ pixelData[1] + " b: "+ pixelData[2]);
	        ImGui.text("r: "+(int)pixelData[0] + " g: "+ pixelData[1] + " b: "+ pixelData[2]);
	        
	        ImGui.text("cameraX:"+origin.x+"  cameraY:"+origin.y+"  cameraZ:"+origin.z);
	        ImGui.text("directionX:"+ray_direction.x+"  directionY:"+ray_direction.y+"  directionZ:"+ray_direction.z);
	        
	        float t = Maths.calculateIntersectPlane(origin, ray_direction, position, new Vector3f(0,0,1));
	        ImGui.text("t:"+tabSizeY);
	        
	        Vector3f plane = new Vector3f(ray_direction).mul(t).add(origin);
	        ImGui.text("planeX:"+plane.x+"  planeY:"+plane.y+"  planeZ:"+plane.z);
	        
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
