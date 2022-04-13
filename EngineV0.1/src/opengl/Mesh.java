package opengl;

public class Mesh {
	
	private VertexArrayObject vao;
	
	public Mesh(VertexArrayObject vao) {
		this.vao = vao;
	}

	public int getVaoID() {
		return vao.getId();
	}

	public VertexArrayObject getVAO() {
		return this.vao;
	}

	public int getVertexCount() {
		return vao.getCount();
	}

}
