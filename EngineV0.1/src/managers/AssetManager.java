package managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import assets.Asset;
import assets.mesh.Mesh;
import assets.sprite.Sprite;
import opengl.Texture;
import tools.MeshLoader;

public class AssetManager {

	public static Map<String, Texture> textureAssets = new HashMap<>();
	public static Map<String, Texture> iconTextureAssets = new HashMap<>();
	public static Map<String, Asset> assets =  new HashMap<>();
	static Mesh defaultCube = new Mesh("cube", EngineManager.positions, EngineManager.textureCoords, EngineManager.normals, EngineManager.indices);
	static Mesh dragon = MeshLoader.loadFromObj("dragon");
	static Mesh defaultSquare = new Mesh("square", EngineManager.ENGINE_SPRITE_SQUARE, EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS, EngineManager.ENGINE_SPRITE_SQUARE_NORMALS, EngineManager.ENGINE_SPRITE_SQUARE_INDICES);
	static Asset[] defaults = { defaultCube, defaultSquare};
	static Sprite texture = new Sprite("first");
	static Sprite whitesprite = new Sprite();
	public static String getAvailName(String name) {
		String newName = name;
		int count = 0;
		while(assets.containsKey(newName)) {
			count++;
			newName = name+" "+count;
		}
		return newName;
	}
	public static boolean hasAsset(String assetName) {
		if(assets.containsKey(assetName)) {
			return true;
		}
		return false;
	}
	public static Asset getAsset(String assetName) {
		if(assets.containsKey(assetName)) {
			return assets.get(assetName);
		}else {
			return Asset.NullAsset;
		}
	}
	public static void loadDefaultAssets() {
			
			texture.setTextureName("spritesheet");
			whitesprite.setTextureName("white");
			
			assets.put(defaultCube.getAssetName(), defaultCube);
	//		assets.put(dragon.getAssetName(), dragon);
			assets.put(defaultSquare.getAssetName(), defaultSquare);
			assets.put(texture.getAssetName(), texture);
			assets.put(whitesprite.getAssetName(), whitesprite);
		}
	public static Texture getTexture(String textureName) {
		String path = "assets/textures/"+textureName+".png";
		File file = new File(path);
		if (textureAssets.containsKey(file.getAbsolutePath())) {
			return textureAssets.get(file.getAbsolutePath());
		}else {
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
		}else {
			System.out.println("Texture Not Found, Creating "+file.getName().split("\\.")[0]);
			Texture texture = new Texture(texture_path, file.getName().split("\\.")[0]);
			textureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}
	public static Texture getIconTexture(String textureName) {
		String path = "res/icons/"+textureName+".png";
		File file = new File(path);
		if (iconTextureAssets.containsKey(file.getAbsolutePath())) {
			return iconTextureAssets.get(file.getAbsolutePath());
		}else {
			Texture texture = new Texture(path, textureName);
			iconTextureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}

}
