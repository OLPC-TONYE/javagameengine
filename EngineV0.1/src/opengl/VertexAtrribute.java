package opengl;

public class VertexAtrribute {
	
	private int index;
	private String name;
	
	public VertexAtrribute(int attributeNo, String variableName) {
		this.index = attributeNo;
		this.name = variableName;
	}

	public int getAttributeNumber() {
		return index;
	}

	public String getVariableName() {
		return name;
	}

	public void setAttributeNumber(int attributeNo) {
		this.index = attributeNo;
	}

	public void setVariableName(String variableName) {
		this.name = variableName;
	}

}
