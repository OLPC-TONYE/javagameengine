package managers;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import assets.Asset;
import assets.AssetType;
import assets.Material;
import assets.Mesh;
import assets.Sprite;
import opengl.Texture;
import tools.Dictionary;
import tools.Maths;
import tools.MeshLoader;

public class AssetManager {

	public static Map<String, Texture> textureAssets = new HashMap<>();
	public static Map<String, Texture> iconTextureAssets = new HashMap<>();
		
	
	static Mesh dragon = MeshLoader.loadFromObj("dragon");
	
//	Meshes
	public static Mesh defaultCube = new Mesh("defaultCubeMesh", EngineManager.positions, EngineManager.textureCoords, EngineManager.normals, EngineManager.indices);
	
	public static Mesh defaultSquareMesh = new Mesh("defaultSquareMesh", EngineManager.ENGINE_SPRITE_SQUARE, EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS, EngineManager.ENGINE_SPRITE_SQUARE_NORMALS,EngineManager.ENGINE_SPRITE_SQUARE_INDICES);
//	Sprite
	public static Sprite defaultSprite = new Sprite();
	
//	Materials
	public static Material defaultMeshMaterial = new Material();
	public static Material defaultSpriteMaterial = new Material();
		
	public static Dictionary<String, Integer, Asset, AssetType> assets = new Dictionary<>();
	
//	For Game Engine Use
	
	public static void createCubeMesh() {
		Mesh newCubeMesh = new Mesh();
		newCubeMesh.setAssetName("Cube");
		newCubeMesh.copy(defaultCube);
		addAsset(newCubeMesh);
	}
	
	public static void createTileMapSprite() {
		Sprite newSprite = new Sprite();
		newSprite.setTextureName("spritesheet");
		addAsset(newSprite);
	}
	
	public static boolean hasAsset(String assetName) {
		if (assets.containsKey(assetName)) {
			return true;
		}
		return false;
	}

	public static Asset getAsset(String assetName) {
		if (assets.containsKey(assetName)) {
			return assets.getFromSecondMap(assetName);
		} else {
			return Asset.NullAsset;
		}
	}
	
	public static Asset getAsset(int assetId) {
		for(Entry<String, Integer> entry: assets.getFirstMap().entrySet()) {
			
			int id = entry.getValue();
			if(id == assetId) {
				return assets.getFromSecondMap(entry.getKey());
			}
		}
		return Asset.NullAsset;
	}

	public static Collection<Asset> getWorldAssets() {
		
		return assets.getSecondMap().values();
	}
	
	public static boolean addAsset(Asset asset) {		
		String assetName = getAvailName(asset.getAssetName());
		int assetId = asset.getId() != 0 ? asset.getId(): getAvailIdentifier();
		if(!assets.containsKey(assetName)) {
			boolean success = add(assetName, assetId, asset);
			return success;
		}
		return false;
	}
	
	public static boolean removeAsset(String assetName) {
		if(assets.containsKey(assetName)) {
			assets.remove(assetName);
			return true;
		}
		System.out.println("Failed to delete"+assetName+": Does not exist");
		return false;
	}
	
	public static void reset() {
		assets.clear();
	}
//	==================================================
	
	private static String getAvailName(String name) {
		String newName = name;
		int count = 0;
		while (assets.containsKey(newName)) {
			count++;
			newName = name + " " + count;
		}
		return newName;
	}
	
	private static int getAvailIdentifier() {
		int entityId = Maths.randomNInt();
		while(assets.getFirstMap().containsValue(entityId)) {
			entityId++;
		}
		return entityId;
	}

	private static boolean add(String name, int id, Asset asset) {
		asset.setAssetName(name);
		asset.setId(id);
		assets.put(name, id, asset, asset.getAssetType());
		return true;
	}

	public static Texture getTexture(String textureName) {
		String path = "assets/textures/" + textureName + ".png";
		File file = new File(path);
		if (textureAssets.containsKey(file.getAbsolutePath())) {
			return textureAssets.get(file.getAbsolutePath());
		} else {
			System.out.println("Texture Not Found, Creating");
			Texture texture = new Texture(path, textureName);
			textureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}

	public static Texture getTextureWithPath(String texture_path) {
		File file = new File(texture_path);
		if (textureAssets.containsKey(file.getAbsolutePath())) {
			return textureAssets.get(file.getAbsolutePath());
		} else {
			System.out.println("Texture Not Found, Creating " + file.getName().split("\\.")[0]);
			Texture texture = new Texture(texture_path, file.getName().split("\\.")[0]);
			textureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}

	public static Texture getIconTexture(String textureName) {
		String path = "res/icons/" + textureName + ".png";
		File file = new File(path);
		if (iconTextureAssets.containsKey(file.getAbsolutePath())) {
			return iconTextureAssets.get(file.getAbsolutePath());
		} else {
			Texture texture = new Texture(path, textureName);
			iconTextureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}

}
