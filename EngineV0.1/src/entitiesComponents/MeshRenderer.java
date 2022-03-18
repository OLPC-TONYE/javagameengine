package entitiesComponents;

import org.joml.Vector3f;

import engine.EngineManager;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import opengl.VertexArrayObject;

public class MeshRenderer extends Component{

	float[] positions = {			
			/* */-0.5f,0.5f,-0.5f, /* */-0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,-0.5f, /* */0.5f,0.5f,-0.5f,	
			/* */-0.5f,0.5f,0.5f, /* */-0.5f,-0.5f,0.5f, /* */0.5f,-0.5f,0.5f, /* */0.5f,0.5f,0.5f,	
			/* */0.5f,0.5f,-0.5f, /* */0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,0.5f,	/* */0.5f,0.5f,0.5f,
			/* */-0.5f,0.5f,-0.5f,	/* */-0.5f,-0.5f,-0.5f,	/* */-0.5f,-0.5f,0.5f,	/* */-0.5f,0.5f,0.5f,
			/* */-0.5f,0.5f,0.5f, /* */-0.5f,0.5f,-0.5f, /* */0.5f,0.5f,-0.5f, /* */0.5f,0.5f,0.5f,
			/* */-0.5f,-0.5f,0.5f, /* */-0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,0.5f
	};
	
	int[] indices = {
			/* */0,1,3,	/* */3,1,2,	/* */4,5,7, /* */7,5,6, /* */8,9,11, /* */11,9,10, /* */12,13,15, /* */15,13,14, /* */16,17,19,
			/* */19,17,18, /* */20,21,23, /* */23,21,22

	};
	
	float[] textureCoords = {
			
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,			
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,			
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,
			/* */0,0, /* */0,1, /* */1,1, /* */1,0
			
	};

	VertexArrayObject mesh;
	Vector3f colour = new Vector3f(1, 1, 1);
	
	String textureName = "white";
	
	@Override
	public void prepare() {
		mesh = EngineManager.loadToVAO(positions, indices, textureCoords);
	}

	@Override
	public void update(double dt) {
		
	}
	
	public Vector3f getColour() {
		return this.colour;
	}

	public VertexArrayObject getMesh() {
		return this.mesh;
	}

	public String getTexture() {
		return this.getTexture();
	}
	
	public int getTextureID() {
		return EngineManager.getTexture(textureName).getTextureID();
	}
	
	public void setTexture(String textureName) {
		this.textureName = textureName;
	}
	
	@Override
	public void UI() {
		
		Vector3f val = colour;
		float[] imFloat = {val.x, val.y, val.z};
		if(ImGui.colorEdit3("Colour", imFloat, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
			this.colour.set(imFloat);
		}
		
		ImGui.pushID("Texture Preview");
		ImGui.text("Preview: ");
		ImGui.sameLine();
		if(ImGui.imageButton(getTextureID(), 
				30, 30, textureCoords[0], textureCoords[1], textureCoords[4], textureCoords[5])) {
//        	ImGui.openPopup("Tilemap");
        }
		if (ImGui.beginDragDropTarget()) {
            final Object payload = ImGui.acceptDragDropPayload("payload_type");
            if (payload != null) {
                setTexture((String) payload);
//                this.entity.modified();
            }
            ImGui.endDragDropTarget();
        }
		ImGui.popID();
	}

}
