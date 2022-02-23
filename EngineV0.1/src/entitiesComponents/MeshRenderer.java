package entitiesComponents;

import org.joml.Vector3f;

import engine.EngineManager;
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
	Vector3f colour = new Vector3f(1, 1, 1);;
	
	@Override
	public void prepare() {
		if(entity.getComponent(TextureComponent.class)!= null) {
			TextureComponent texture = entity.getComponent(TextureComponent.class);
			texture.start();
			mesh = EngineManager.loadToVAO(positions, indices, textureCoords);
		}
	}

	@Override
	public void update() {
		
	}
	
	public Vector3f getColour() {
		return this.colour;
	}

	public VertexArrayObject getMesh() {
		return this.mesh;
	}


}
