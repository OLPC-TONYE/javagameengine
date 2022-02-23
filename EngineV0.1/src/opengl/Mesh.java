package opengl;

import java.util.HashMap;
import java.util.Map;

public class Mesh {
	
	private int vaoID;
	private Map<String, Integer> vboList = new HashMap<String, Integer>();
	private int vertexCount;	
	
	public Mesh(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}
	
	public Map<String, Integer> getVBOs(){
		return this.vboList;
	}
	
	public int getVBO(String vboname) {
		if(vboList.containsKey(vboname)) {
			return this.vboList.get(vboname);
		}else {
			System.out.println("No such vbo called "+vboname);
			return 0;
		}
	}
	
	public void addVBO(String vboname, int vbo) {
		this.vboList.put(vboname, vbo);
	}
	
	public void modifyVBO(String vboName, float[] data) {
		
	}

	public int getVertexCount() {
		return vertexCount;
	}

}
