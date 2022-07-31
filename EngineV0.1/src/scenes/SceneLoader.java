package scenes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import assets.Asset;
import assets.Material;
import assets.Mesh;
import assets.Sprite;
import components.Component;
import entities.Entity;
import managers.AssetManager;
import managers.EntityManager;


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
		JsonObject properties = jsonObject.get("properties").getAsJsonObject();
		
		
		try {
			
			Class<?> componentClass = Class.forName(type);
			Component component = (Component) componentClass.newInstance();
			
			for (Field field : component.getClass().getDeclaredFields()) {

				boolean isTransient = Modifier.isTransient(field.getModifiers());
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				boolean isProtected = Modifier.isProtected(field.getModifiers());

				if (isPrivate | isProtected) {
					field.setAccessible(true);
				}

				if (isTransient) {
					continue;
				}

				Class<?> fieldType = field.getType();
				String name = field.getName();
				
				if(Asset.class.isAssignableFrom(fieldType)) {
					JsonObject assetProperties = properties.get(name).getAsJsonObject();
					int assetid = assetProperties.get("assetId").getAsInt();
					Asset val = AssetManager.getAsset(assetid);
					if(val != Asset.NullAsset) field.set(component, val);
					if (isPrivate | isProtected) field.setAccessible(false);
					continue;
				}
				
				JsonElement element = properties.get(name);
				Object value = context.deserialize(element, fieldType);
											
				field.set(component, value);
								
				if (isPrivate | isProtected) {
					field.setAccessible(false);
				}
			} 
			
			return component;
		}  catch (IllegalArgumentException e) {
			throw new JsonParseException("Component Deserializer- Problem parsing element type: (IllegalArgumentException)" + type, e);
		} catch (IllegalAccessException e) {
			throw new JsonParseException("Component Deserializer- Problem parsing element type: (IllegalAccessException)" + type, e);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Component Deserializer- Problem parsing element type: (ClassNotFoundException)" + type, e);
		} catch (InstantiationException e) {
			throw new JsonParseException("Component Deserializer- Unknown element type: (InstantiationException)" + type, e);
		}		
	}

	@Override
	public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
		
		JsonObject properties = new JsonObject();
		try {
			for (Field field : src.getClass().getDeclaredFields()) {

				boolean isTransient = Modifier.isTransient(field.getModifiers());
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				boolean isProtected = Modifier.isProtected(field.getModifiers());

				if (isPrivate | isProtected) {
					field.setAccessible(true);
				}

				if (isTransient) {
					continue;
				}

				Class<?> type = field.getType();
				String name = field.getName();
				Object value = field.get(src);
				
				if(Asset.class.isAssignableFrom(type)) {
					Asset val = (Asset) value;
					JsonObject assetProperties = new JsonObject();
					assetProperties.addProperty("assetId", val.getId());
					properties.add(name, assetProperties);
					if (isPrivate | isProtected) field.setAccessible(false);
					continue;
				}

				properties.add(name, context.serialize(value, type));
				
				if (isPrivate | isProtected) {
					field.setAccessible(false);
				}
			} 
		}  catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		result.add("properties", properties);
		return result;
	}

}

class AssetDeserializer implements JsonSerializer<Asset>, JsonDeserializer<Asset>{

	@Override
	public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		
		int uId = jsonObject.get("asset").getAsInt();
		String type = jsonObject.get("type").getAsString();
		String name = jsonObject.get("name").getAsString();		
		JsonObject data = jsonObject.get("data").getAsJsonObject();
		
		switch (type) {
			case "Material":
				type = Material.class.getCanonicalName();
				break;
			case "Mesh":
				type = Mesh.class.getCanonicalName();
				break;
			case "Sprite":
				type = Sprite.class.getCanonicalName();
				break;
		}
		try{
			Class<?> assetClass = Class.forName(type);
			Asset asset = (Asset) assetClass.newInstance();
					
			asset.setId(uId);
			asset.setAssetName(name);
			for(Field field : asset.getClass().getDeclaredFields()) {
				boolean isTransient = Modifier.isTransient(field.getModifiers());
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				boolean isProtected = Modifier.isProtected(field.getModifiers());

				boolean isEmpty = Modifier.toString(field.getModifiers()) == "";
				
				if (isPrivate | isProtected | isEmpty) {
					field.setAccessible(true);
				}

				if (isTransient) {
					continue;
				}
				
				String fieldName = field.getName();
				if(fieldName == "name"|fieldName == "type"|fieldName == "uId") {
					continue;
				}
				
				Class<?> fieldType = field.getType();
				Object value = context.deserialize(data.get(fieldName), fieldType);
				
				if(value != null) field.set(asset, value);
				
				if (isPrivate | isProtected | isEmpty) {
					field.setAccessible(false);
				}
			}
			return asset;
			
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Asset Deserializer- (ClassNotFoundException) Unknown element type: " + type, e);
		} catch (InstantiationException e) {
			throw new JsonParseException("Asset Deserializer- (InstantiationException) Unknown element type: " + type, e);
		} catch (IllegalAccessException e) {
			throw new JsonParseException("Asset Deserializer- (IllegalAccessException) Unknown element type: " + type, e);
		}
	}

	@Override
	public JsonElement serialize(Asset src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("asset", new JsonPrimitive(src.getId()));
		result.add("type", new JsonPrimitive(src.getAssetType().name()));
		result.add("name", new JsonPrimitive(src.getAssetName()));
		
		JsonObject properties = new JsonObject();
		try {
			for (Field field : src.getClass().getDeclaredFields()) {

				boolean isTransient = Modifier.isTransient(field.getModifiers());
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				boolean isProtected = Modifier.isProtected(field.getModifiers());

				boolean isEmpty = Modifier.toString(field.getModifiers()) == "";
				
				if (isPrivate | isProtected | isEmpty) {
					field.setAccessible(true);
				}

				if (isTransient) {
					continue;
				}

				Class<?> type = field.getType();
				String name = field.getName();
				if(name == "name"|name == "type"|name == "uId") {
					continue;
				}
				
				Object value = field.get(src);
				properties.add(name, context.serialize(value, type));
				
				if (isPrivate | isProtected | isEmpty) {
					field.setAccessible(false);
				}
			} 
		}  catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		result.add("data", properties);		
		return result;
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
					.registerTypeAdapter(Material.class, new AssetDeserializer())
					.registerTypeAdapter(Asset.class, new AssetDeserializer())
					.registerTypeAdapter(Component.class, new ComponentDeserializer())
					.registerTypeAdapter(Entity.class, new EntityDeserializer())					
					.create();
		}	
	}
	
	public static void save() {
		try {
			FileWriter typist = new FileWriter(new File("assets/scenes/savefile.bug"));
			
			JsonObject scene = new JsonObject();
			
			// First Stop All Entity & Their Components
			for(Entity entity: EntityManager.getWorldEntities()) {
				entity.stop();			
			}
			
			scene.add("save_version", new JsonPrimitive(1.2));
			
			// Add Entities 
			scene.add("entities", SceneLoader.serializer.toJsonTree(EntityManager.getWorldEntities()));
			
			// Add Assets
			scene.add("assets", SceneLoader.serializer.toJsonTree(AssetManager.getWorldAssets()));
			
			// Write Entities Into File
			if(scene.toString().length() > 0) {
				typist.write(scene.toString());
			}		
			typist.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String saveData() {
		JsonObject scene = new JsonObject();
		
		// First Stop All Entity & Their Components
		for(Entity entity: EntityManager.getWorldEntities()) {
			entity.stop();			
		}
		
		scene.add("save_version", new JsonPrimitive(1.2));
		
		// Add Entities 
		scene.add("entities", SceneLoader.serializer.toJsonTree(EntityManager.getWorldEntities()));
		
		// Add Assets
		scene.add("assets", SceneLoader.serializer.toJsonTree(AssetManager.getWorldAssets()));
		
		// Write Entities Into File
		if(scene.toString().length() < 0) return null;
		
		return scene.toString();
	}
	
	public static void load(String filepath) {
		String fileData = "";
		
		try {
			if(!filepath.contains(".bug")) return;
			fileData = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!fileData.equals("")) {
			
			// Parse String into a jsonObject
			JsonParser parser = new JsonParser();	
			JsonObject jsonObject = parser.parse(fileData).getAsJsonObject();
											
			//Check for version
			double version = jsonObject.get("save_version").getAsDouble();
			if(version < 1.2) return;
			
			//Get Entity & Asset Data
			JsonElement entitiesData = jsonObject.get("entities");
			JsonElement assetsData = jsonObject.get("assets");
							
			// Clear Managers (in case) 
			AssetManager.reset();
			EntityManager.reset();
					
			// Deserialize AssetData
			Asset[] assets = serializer.fromJson(assetsData, Asset[].class);
			
			// Add Assets (first)
			// Do this before deserializing entities
			for(int i=0; i < assets.length; i++) {
				boolean success = AssetManager.addAsset(assets[i]);
				if(!success) {
					System.out.println("Failed to Add " + assets[i].getAssetName());
				}
			}
				
			// Deserialize EntityData
			Entity[] entities = serializer.fromJson(entitiesData, Entity[].class);
			// Add Entities
			// Do this after deserializing assets
			for(int i=0; i < entities.length; i++) {
				boolean success = EntityManager.add(entities[i]);
				entities[i].start();
				if(!success) {
					System.out.println("Failed to Add " + entities[i].getName());
				}
			}
			
		}
	}
	
	public static ArrayList<Entity> loadToScene() {
		String fileData = SceneLoader.serializer.toJson(EntityManager.getWorldEntities());
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
