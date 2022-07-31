package assets;

import managers.EngineManager;
import opengl.VertexArrayObject;

public class Mesh extends Asset{
	
	private float[] vertices;
	private float[] textureUVs;
	private float[] normals;
	private int[] indices;
	
	private transient VertexArrayObject vertexArray;
	
	public Mesh() {
		super("Mesh", AssetType.Mesh);
	}
	
	/**
	 * @param vertices
	 * @param textureUVs
	 * @param normals
	 * @param indices
	 */
	public Mesh(String name, float[] vertices, float[] textureUVs, float[] normals, int[] indices) {
		super(name, AssetType.Mesh);
		this.vertices = vertices;
		this.textureUVs = textureUVs;
		this.normals = normals;
		this.indices = indices;
	}
	
	/**
	 * @param vertices
	 * @param textureUVs
	 * @param normals
	 * @param indices
	 */
	public Mesh(float[] vertices, float[] textureUVs, float[] normals, int[] indices) {
		super("Mesh", AssetType.Mesh);
		this.vertices = vertices;
		this.textureUVs = textureUVs;
		this.normals = normals;
		this.indices = indices;
	}
		
	public void setData(float[] vertices, float[] textureUVs, float[] normals, int[] indices) {
		this.vertices = vertices;
		this.textureUVs = textureUVs;
		this.normals = normals;
		this.indices = indices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public void setTextureUVs(float[] textureUVs) {
		this.textureUVs = textureUVs;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureUVs() {
		return textureUVs;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public VertexArrayObject getVertexArray() {
		if(vertexArray == null) EngineManager.loadToVAO(this);
		
		return vertexArray;
	}

	public void setVertexArray(VertexArrayObject vertexArray) {
		this.vertexArray = vertexArray;
	}
	
	public void update() {
		EngineManager.loadToVAO(this);
	}

	@Override
	public Mesh copy(Asset from) {
		if(from == null) return null;
		if(!(from instanceof Mesh)) return null;
		
		Mesh mesh = (Mesh) from;
		
		this.indices = mesh.getIndices();
		this.vertices = mesh.getVertices();
		this.textureUVs = mesh.getTextureUVs();
		this.normals = mesh.getNormals();
		return this;
	}

}
