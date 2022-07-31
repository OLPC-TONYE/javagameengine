package components;

import org.joml.Vector3f;

import annotations.ColourField;
import annotations.HideIfNull;
import annotations.TextureNameField;
import assets.Material;
import assets.Mesh;
import managers.AssetManager;

public class MeshRenderer extends Component{
	
	private Mesh mesh;
	
	@ColourField
	@HideIfNull(fieldName = "mesh")
	private Vector3f colour = new Vector3f(1);
	
	private Material material = AssetManager.defaultMeshMaterial;
	
	@TextureNameField
	private String textureName = "white";
	
	@Override
	public void prepare() {
		
	}

	@Override
	public void update(double dt) {
		
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Mesh getMesh() {
		return this.mesh;
	}
	
	/**
	 * @return the colour
	 */
	public Vector3f getColour() {
		return colour;
	}

	/**
	 * @param colour the colour to set
	 */
	public void setColour(Vector3f colour) {
		this.colour = colour;
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getTexture() {
		return this.textureName;
	}
	
	public int getTextureID() {
		return AssetManager.getTexture(textureName).getTextureID();
	}
	
	public void setTexture(String textureName) {
		this.textureName = textureName;
	}
	
}
