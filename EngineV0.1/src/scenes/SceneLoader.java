package scenes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import assets.Asset;
import assets.AssetType;
import assets.light.DirectionalLight;
import assets.light.Light;
import assets.light.LightFlags;
import assets.light.PointLight;
import assets.light.SpotLight;
import assets.mesh.Material;
import assets.mesh.Mesh;
import assets.sprite.Sprite;
import engine.EngineManager;
import engine.EntityManager;
import entities.Entity;
import entitiesComponents.Component;
import tools.ListofFloats;

class EntityDeserializer implements JsonDeserializer<Entity> {

	@Override
	public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");
		
		Entity entity = new Entity();
		entity.setName(name);
		for(JsonElement jsonElement: components) {
			Component c = context.deserialize(jsonElement, Component.class);
			entity.addComponent(c);
		}
		return entity;
	}
	
}

class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component>{

	@Override
	public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");
		
		try{
			return context.deserialize(element, Class.forName(type));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Unknown element type: " + type, e);
		}
	}

	@Override
	public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
		result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}
	
}

class AssetDeserializer extends TypeAdapter<Asset> implements JsonSerializer<Asset>, JsonDeserializer<Asset>{

	@Override
	public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");
		
		try{
			return context.deserialize(element, Class.forName(type));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Unknown element type: " + type, e);
		}
	}

	@Override
	public JsonElement serialize(Asset src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
		result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}

	@Override
	public void write(JsonWriter out, Asset value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		out.beginObject();
		out.name("type").value(value.getAssetType().name());
		out.name("name").value(value.getAssetName());		
		out.name("data");
			out.beginObject();
			if(value.getAssetType() == AssetType.Mesh) {
				Mesh ifmesh = (Mesh) value;			
				writeMesh(out, ifmesh);
				out.name("material");
				out.beginObject();
				writeMaterial(out, ifmesh.getMaterial());
				out.endObject();
			}else if(value.getAssetType() == AssetType.Material) {
				Material ifmaterial = (Material) value;
				writeMaterial(out, ifmaterial);
			}else if(value.getAssetType() == AssetType.Sprite) {
				Sprite ifsprite = (Sprite) value;
				writeSprite(out, ifsprite);
			}else if(value.getAssetType() == AssetType.Light) {
				Light iflight = (Light) value;
				writeLight(out, iflight);
			}
			out.endObject();
		out.endObject();
	}

	private void writeLight(JsonWriter out, Light iflight) throws IOException {
		LightFlags type = iflight.getFlag();
		out.name("type").value(type.toString());
		out.name("light properties").beginObject();
			out.name("intensity").value(iflight.getIntensity());
			out.name("colour"); writeVector3f(out, iflight.getColour());
			if(type == LightFlags.Directional) {
				DirectionalLight ifdirectionallight = (DirectionalLight) iflight;
				out.name("direction"); writeVector3f(out, ifdirectionallight.getDirection());
			}else if(type == LightFlags.Point) {
				PointLight ifpointlight = (PointLight) iflight;
				out.name("attenuation"); writeVector3f(out, ifpointlight.getAttenuationVector());
			}else if(type == LightFlags.Spot) {
				SpotLight ifspotlight = (SpotLight) iflight;
				out.name("cutoff angle").value(ifspotlight.getCutOffAngle());
				out.name("direction"); writeVector3f(out, ifspotlight.getDirection());
				out.name("attenuation"); writeVector3f(out, ifspotlight.getAttenuationVector());
			}
		out.endObject();
	}

	private void writeSprite(JsonWriter out, Sprite ifsprite) throws IOException {
		out.name("texture").value(ifsprite.getTextureName());
		out.name("isTilemap").value(ifsprite.isTilemap());
		out.name("tilemap properties").beginObject();
			out.name("currentTile").value(ifsprite.getCurrentTile());
			out.name("width").value(ifsprite.getTileWidth());
			out.name("height").value(ifsprite.getTileHeight());
			out.name("spacing").value(ifsprite.getTileSpacing());
			out.name("tiles"); writeListFloatArray(out, ifsprite.getTilemap());		
		out.endObject();
	}

	private void writeListFloatArray(JsonWriter out, List<float[]> tilemap) throws IOException {
		out.beginArray();
			for(float[] floatsarray: tilemap) {
				writeFloatArray(out, floatsarray);
			}
		out.endArray();
	}

	private void writeFloatArray(JsonWriter out, float[] floatsarray) throws IOException {
		out.beginArray();
			for(float floats: floatsarray) {
				out.value(floats);
			}
		out.endArray();
	}

	@Override
	public Asset read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}
		
		AssetType assetType = null;
		String assetName = "";
		
		in.beginObject();
		while(in.hasNext()) {
			String property = in.nextName();
			if(property.equals("type")) {
				assetType = AssetType.valueOf(in.nextString());
			}else if(property.equals("name")) {
				assetName = in.nextString();
			}else if(property.equals("data")) {
				if(assetType == AssetType.Mesh) {
					Mesh ifmesh = new Mesh();			
					ifmesh.setAssetType(assetType);
					ifmesh.setAssetName(assetName);
					readMeshData(in, ifmesh);
					
					//Add comments TODO
					in.endObject();
					return ifmesh;
				}else if(assetType == AssetType.Material) {
					Material ifmaterial = new Material();
					ifmaterial.setAssetName(assetName);
					ifmaterial.setAssetType(assetType);
					readMaterialData(in, ifmaterial);
					
					in.endObject();
					return ifmaterial;
				}else if(assetType == AssetType.Sprite) {
					Sprite ifsprite = new Sprite();
					ifsprite.setAssetName(assetName);
					ifsprite.setAssetType(assetType);
					readSpriteData(in, ifsprite);
					
					in.endObject();
					return ifsprite;
				}else if(assetType == AssetType.Light) {
					Light iflight = readLightData(in);
					iflight.setAssetName(assetName);
					iflight.setAssetType(assetType);
					
					in.endObject();
					return iflight;
				}else {
					in.skipValue();
				}
			}else {
				in.skipValue();
			}			
		}
		in.endObject();
		return null;
	}
	
	private Light readLightData(JsonReader in) throws IOException {
		
		LightFlags type = null;
		in.beginObject();
		while(in.hasNext()) {
			String property = in.nextName();
			if(property.equals("type")) {
				type = LightFlags.valueOf(in.nextString());
			}else if(property.equals("light properties")) {
				
				if(type == LightFlags.Directional) {
					DirectionalLight iflight = new DirectionalLight();
					in.beginObject();
					while(in.hasNext()) {
						String lightproperty = in.nextName();
						if(lightproperty.equals("intensity")) {
							iflight.setIntensity((float) in.nextDouble());
						}else if(lightproperty.equals("colour")) {
							iflight.setColour(readVector3f(in));
						}else if(lightproperty.equals("direction")) {
							iflight.setDirection(readVector3f(in));
						}else {
							in.skipValue();
						}					
					}
					in.endObject();
					return iflight;
				}else if(type == LightFlags.Point) {
					PointLight iflight = new PointLight();
					in.beginObject();
					while(in.hasNext()) {
						String lightproperty = in.nextName();
						if(lightproperty.equals("intensity")) {
							iflight.setIntensity((float) in.nextDouble());
						}else if(lightproperty.equals("colour")) {
							iflight.setColour(readVector3f(in));
						}else if(lightproperty.equals("attenuation")) {
							iflight.setAttenuation(readVector3f(in));
						}else {
							in.skipValue();
						}					
					}
					in.endObject();
					return iflight;
				}else if(type == LightFlags.Spot) {
					SpotLight iflight = new SpotLight();
					in.beginObject();
					while(in.hasNext()) {
						String lightproperty = in.nextName();
						if(lightproperty.equals("intensity")) {
							iflight.setIntensity((float) in.nextDouble());
						}else if(lightproperty.equals("colour")) {
							iflight.setColour(readVector3f(in));
						}else if(lightproperty.equals("cutoff angle")) {
							iflight.setCutOffAngle((float) in.nextDouble());
						}else if(lightproperty.equals("direction")) {
							iflight.setDirection(readVector3f(in));
						}else if(lightproperty.equals("attenuation")) {
							iflight.setAttenuation(readVector3f(in));
						}else {
							in.skipValue();
						}					
					}
					in.endObject();
					return iflight;
				}else {
					in.skipValue();
				}							
			}else {
				in.skipValue();
			}
		}
		in.endObject();
		return null;
	}

	private void readSpriteData(JsonReader in, Sprite ifsprite) throws IOException {
		in.beginObject();
		while(in.hasNext()) {
			boolean hasTileMap = false;
			String property = in.nextName();
			if(property.equals("texture")) {
				ifsprite.setTextureName(in.nextString());
			}else if(property.equals("isTilemap")) {
				hasTileMap = in.nextBoolean();
				ifsprite.toggleTileMap(hasTileMap);
			}else if(property.equals("tilemap properties")) {
				// first check if it's a tilemap, don't bother 
				if(!hasTileMap) {
					in.skipValue();
					continue;
				}	
				in.beginObject();
				while(in.hasNext()) {
					String tileproperty = in.nextName();
					if(tileproperty.equals("currentTile")) {
						ifsprite.setCurrentTile(in.nextInt());
					}else if(tileproperty.equals("width")) {
						ifsprite.setTileWidth(in.nextInt());
					}else if(tileproperty.equals("height")) {
						ifsprite.setTileHeight(in.nextInt());
					}else if(tileproperty.equals("spacing")) {
						ifsprite.setTileSpacing(in.nextInt());
					}else if(tileproperty.equals("tiles")) {
						List<float[]> tiles = readListFloatArray(in);
						ifsprite.setTilemap(tiles);
					}else {
						in.skipValue();
					}
				}
				in.endObject();
			}else {
				in.skipValue();
			}
		}
		in.endObject();
	}

	private List<float[]> readListFloatArray(JsonReader in) throws IOException {
		List<float[]> floatarrays = new ArrayList<>();
		in.beginArray();
		while(in.hasNext()) {
			floatarrays.add(readFloatArray(in));
		}
		in.endArray();
		return floatarrays;
	}

	private void readMaterialData(JsonReader in, Material ifmaterial) throws IOException {
		in.beginObject();
		while(in.hasNext()) {
			String property = in.nextName();
			if(property.equals("ambient")) {
				Vector4f value = readVector4f(in);
				ifmaterial.setAmbient(value);
			}
			if(property.equals("diffuse")) {
				Vector4f value = readVector4f(in);
				ifmaterial.setDiffuse(value);
			}
			if(property.equals("specular")) {
				Vector4f value = readVector4f(in);
				ifmaterial.setSpecular(value);
			}
			if(property.equals("specularity")) {
				float value = (float) in.nextDouble();
				ifmaterial.setSpecularPower(value);
			}
			if(property.equals("reflectivity")) {
				float value = (float) in.nextDouble();
				ifmaterial.setReflectivity(value);
			}
		}	
		in.endObject();
	}

	private void readMeshData(JsonReader in, Mesh ifmesh) throws IOException {
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextName();
			if(name.equals("vertices")) {
				float[] vertices = readFloatArray(in);
				ifmesh.setVertices(vertices);
			}
			if(name.equals("textureUVs")) {
				float[] textureUVs = readFloatArray(in);
				ifmesh.setTextureUVs(textureUVs);
			}
			if(name.equals("normals")) {
				float[] normals = readFloatArray(in);
				ifmesh.setNormals(normals);
			}
			if(name.equals("indices")) {
				int[] indices = readIntArray(in);
				ifmesh.setIndices(indices);;
			}
			if(name.equals("material")) {
				Material material = new Material();
				in.beginObject();
				while(in.hasNext()) {
					String property = in.nextName();
					if(property.equals("ambient")) {
						Vector4f value = readVector4f(in);
						material.setAmbient(value);
					}
					if(property.equals("diffuse")) {
						Vector4f value = readVector4f(in);
						material.setDiffuse(value);
					}
					if(property.equals("specular")) {
						Vector4f value = readVector4f(in);
						material.setSpecular(value);
					}
					if(property.equals("specularity")) {
						float value = (float) in.nextDouble();
						material.setSpecularPower(value);
					}
					if(property.equals("reflectivity")) {
						float value = (float) in.nextDouble();
						material.setReflectivity(value);
					}
				}	
				in.endObject();
				ifmesh.setMaterial(material);
			}
		}
		in.endObject();
	}

	private Vector4f readVector4f(JsonReader in) throws IOException {
		in.beginObject();
		Vector4f vector = new Vector4f();
		in.nextName();
		vector.x = (float) in.nextDouble();
		in.nextName();
		vector.y = (float) in.nextDouble();
		in.nextName();
		vector.z = (float) in.nextDouble();
		in.nextName();
		vector.w = (float) in.nextDouble();
		in.endObject();
		return vector;
	}
	
	public Vector3f readVector3f(JsonReader in) throws IOException {
		in.beginObject();
		Vector3f vector = new Vector3f();
		in.nextName();
		vector.x = (float) in.nextDouble();
		in.nextName();
		vector.y = (float) in.nextDouble();
		in.nextName();
		vector.z = (float) in.nextDouble();
		in.endObject();
		return vector;
	}

	private int[] readIntArray(JsonReader in) throws IOException {
		ArrayList<Integer> ints = new ArrayList<>();
		in.beginArray();
		while(in.hasNext()) {
			ints.add(in.nextInt());
		}
		in.endArray();
		
		int[] integers = new int[ints.size()];
		for(int i=0; i < ints.size(); i++) {
			integers[i] = ints.get(i).intValue();
		}
		return integers;
	}

	private float[] readFloatArray(JsonReader in) throws IOException {
		ListofFloats floats = new ListofFloats();
		in.beginArray();
		while(in.hasNext()) {
			floats.add((float) in.nextDouble());
		}
		in.endArray();
		return floats.toArray();
	}

	public void writeVector3f(JsonWriter out, Vector3f value) throws IOException {
		out.beginObject();
		out.name("x").value(value.x);
		out.name("y").value(value.y);
		out.name("z").value(value.z);
		out.endObject();
	}
	
	public void writeVector4f(JsonWriter out, Vector4f value) throws IOException {
		out.beginObject();		
		out.name("x").value(value.x);
		out.name("y").value(value.y);
		out.name("z").value(value.z);
		out.name("w").value(value.w);
		out.endObject();
	}
	
	public void writeMesh(JsonWriter out, Mesh mesh) throws IOException {
		out.name("vertices").beginArray();
		for(float values:mesh.getVertices()) {
			out.value(values);
		}			
		out.endArray();
		
		// encode the uvs
		out.name("textureUVs").beginArray();
		for(float values:mesh.getTextureUVs()) {
			out.value(values);
		}			
		out.endArray();
		
		// encode the normals
		out.name("normals").beginArray();
		for(float values:mesh.getNormals()) {
			out.value(values);
		}			
		out.endArray();
		
		// encode the normals
		out.name("indices").beginArray();
		for(int values:mesh.getIndices()) {
			out.value(values);
		}			
		out.endArray();
	}
	
	private void writeMaterial(JsonWriter out, Material material) throws IOException {
		out.name("ambient");
		writeVector4f(out, material.getAmbient());
		out.name("diffuse");
		writeVector4f(out, material.getDiffuse());
		out.name("specular");
		writeVector4f(out, material.getSpecular());
		out.name("reflectivity").value(material.getReflectivity());
		out.name("specularity").value(material.getSpecularPower());
	}
}

public class SceneLoader {
	
	public static Gson serializer = null;
	
	public static void ready() {
		if(SceneLoader.serializer == null) {
			SceneLoader.serializer = new GsonBuilder()
					.setPrettyPrinting()
					.registerTypeAdapter(Sprite.class, new AssetDeserializer())
					.registerTypeAdapter(Mesh.class, new AssetDeserializer())
					.registerTypeAdapter(Light.class, new AssetDeserializer())
					.registerTypeAdapter(Asset.class, new AssetDeserializer())
					.registerTypeAdapter(Component.class, new ComponentDeserializer())
					.registerTypeAdapter(Entity.class, new EntityDeserializer())					
					.create();
		}	
	}
	
	public static void save() {
		try {
			FileWriter typist = new FileWriter(new File("savefile.bug"));
			
			JsonObject scene = new JsonObject();
			
			// First Stop All Entity & Their Components
			for(Entity entity: EntityManager.world_entities.values()) {
				entity.stop();
				
			}
			
			// Add Entities 
			scene.add("entities", SceneLoader.serializer.toJsonTree(EntityManager.world_entities.values()));
			
			// Add Assets
			scene.add("assets", SceneLoader.serializer.toJsonTree(EngineManager.assets.values()));
			
			// Write Entities Into File
			if(scene.toString().length() > 0) {
				typist.write(scene.toString());
			}		
			typist.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		String fileData = "";
		
		try {
			fileData = new String(Files.readAllBytes(Paths.get("savefile.bug")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!fileData.equals("")) {
			
			// Parse String into a jsonObject
			JsonParser parser = new JsonParser();	
			JsonObject jsonObject = parser.parse(fileData).getAsJsonObject();
			JsonElement entitiesData = jsonObject.get("entities");
			JsonElement assetsData = jsonObject.get("assets");
			
			Entity[] entities = serializer.fromJson(entitiesData, Entity[].class);
			Asset[] assets = serializer.fromJson(assetsData, Asset[].class);
			
			for(int i=0; i < entities.length; i++) {
				entities[i].start();
				boolean success = EntityManager.add(entities[i]);
				if(!success) {
					System.out.println("Failed to Add " + entities[i].getName());
				}
			}
			
			for(int i=0; i < assets.length; i++) {
				
			}
		}
	}
	
	public static ArrayList<Entity> loadToScene() {
		String fileData = SceneLoader.serializer.toJson(EntityManager.world_entities.values());
		System.out.println(fileData);
		if(!fileData.equals("")) {
			Entity[] entities = serializer.fromJson(fileData, Entity[].class);
			ArrayList<Entity> gameObjects = new ArrayList<Entity>();
			for(int i=0; i < entities.length; i++) {
				entities[i].start();
				gameObjects.add(entities[i]);
			}
			
			return gameObjects;
		}
		return null;
	}
	
	
}
