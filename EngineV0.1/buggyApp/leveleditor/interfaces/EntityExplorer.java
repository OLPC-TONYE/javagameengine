package leveleditor.interfaces;

import org.joml.Vector3f;

import entities.Entity;
import entitiesComponents.LightingComponent;
import entitiesComponents.MeshRenderer;
import entitiesComponents.SpriteRenderer;
import entitiesComponents.Transform;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import leveleditor.LevelEditorLayer;
import managers.EngineManager;
import managers.EntityManager;

public class EntityExplorer {

	String explorer_deleteFlag = null;

	private void showChildren(String parent) {
		if (EntityManager.hasChildren(parent)) {
			for (String child : EntityManager.getChildrenOf(parent)) {
				boolean open = ImGui.treeNodeEx(child, ImGuiTreeNodeFlags.OpenOnArrow);
				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("child", child);
					ImGui.text(child);
					ImGui.endDragDropSource();
				}
				if (ImGui.beginDragDropTarget()) {
					Object payload_check = ImGui.acceptDragDropPayload("child", ImGuiDragDropFlags.AcceptPeekOnly);
					boolean check = true;
					if (payload_check != null) {
						check = EntityManager.isAbove( (String)payload_check, child);				
						payload_check = null;
					}
					if(check) {
						ImGui.setMouseCursor(ImGuiMouseCursor.NotAllowed);
					}
					if(!check) {
						Object payload = ImGui.acceptDragDropPayload("child");
						if (payload != null) {
							String childaccept = (String) ImGui.acceptDragDropPayload("child");
							EntityManager.makeChildOf(childaccept, child);
							payload = null;
						}
					}				
					ImGui.endDragDropTarget();
				}
				if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
					if (ImGui.menuItem("Delete")) {
						explorer_deleteFlag = child;
					}
					ImGui.endPopup();
				}
				if (open) {
					showChildren(child);
					ImGui.treePop();
				}

			}
		}
	}
	
	public void render(LevelEditorLayer layer) {
		ImGui.begin("EntityExplorer");

		for (String root : EntityManager.world.keySet()) {
			if (!EntityManager.hasParent(root)) {
				boolean open = ImGui.treeNodeEx(root, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.OpenOnArrow);
				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("child", root);
					ImGui.text(root);
					ImGui.endDragDropSource();
				}
				if (ImGui.beginDragDropTarget()) {
					Object payload_check = ImGui.acceptDragDropPayload("child", ImGuiDragDropFlags.AcceptPeekOnly);
					boolean check = true;
					if (payload_check != null) {
						check = EntityManager.isAbove( (String)payload_check, root);
										
						payload_check = null;
					}
					if(check) {
						ImGui.setMouseCursor(ImGuiMouseCursor.NotAllowed);
					}
					if(!check) {
						Object payload = ImGui.acceptDragDropPayload("child");
						if (payload != null) {
							String childaccept = (String) ImGui.acceptDragDropPayload("child");
							EntityManager.makeChildOf(childaccept, root);
							payload = null;
						}
					}				
					ImGui.endDragDropTarget();
				}
				if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
					if (ImGui.menuItem("Delete")) {
						explorer_deleteFlag = root;
					}
					ImGui.endPopup();
				}
				if (open) {
					showChildren(root);
					ImGui.treePop();
				}
			}
		}

		if (explorer_deleteFlag != null) {
			boolean success = EntityManager.remove(explorer_deleteFlag);
			if (!success) {
				System.out.println("Failed to Remove " + explorer_deleteFlag);
			} else {
				layer.selectedEntity = null;
			}
			explorer_deleteFlag = null;
		}

		if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight | ImGuiPopupFlags.NoOpenOverItems)) {

			if (ImGui.beginMenu("Add Entity")) {

				if (ImGui.menuItem("Empty Entity")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform());
					boolean success = EntityManager.add(new_Entity, "Empty Entity");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					layer.selectedEntity = new_Entity.getName();
				}
				
				if (ImGui.menuItem("Camera 2D")) {
					Entity new_Entity = EngineManager.create2DCamera();
					boolean success = EntityManager.add(new_Entity, "2D Camera");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					layer.selectedEntity = new_Entity.getName();
				}
				
				if (ImGui.menuItem("Camera 3D")) {
					Entity new_Entity = EngineManager.create3DCamera();
					boolean success = EntityManager.add(new_Entity, "3D Camera");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					layer.selectedEntity = new_Entity.getName();
				}
				
				if(ImGui.menuItem("Light")) {
					Entity new_Entity = new Entity();
					Transform transform1 = new Transform();
					transform1.setPosition(new Vector3f(0, 0, 2));	
					new_Entity.addComponent(transform1);

					new_Entity.addComponent(new LightingComponent());		
					boolean success = EntityManager.add(new_Entity, "Light");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					layer.selectedEntity = new_Entity.getName();
				}

				if (ImGui.menuItem("Sprite")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform());
					new_Entity.addComponent(new SpriteRenderer());
					boolean success = EntityManager.add(new_Entity, "Sprite");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					layer.selectedEntity = new_Entity.getName();
				}

				if (ImGui.menuItem("Mesh")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform());
					MeshRenderer m = new MeshRenderer();
					m.setTexture("first");
					new_Entity.addComponent(m);
					boolean success = EntityManager.add(new_Entity, "Mesh");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					layer.selectedEntity = new_Entity.getName();
				}

				ImGui.endMenu();
			}

			ImGui.endPopup();
		}

		ImGui.end();
	}

}
