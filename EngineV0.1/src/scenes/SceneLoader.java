package scenes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import assets.Asset;
import assets.light.Light;
import engine.EntityManager;
import entities.Entity;
import entitiesComponents.Component;

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

class AssetDeserializer implements JsonSerializer<Asset>, JsonDeserializer<Asset>{

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
	
}

public class SceneLoader {
	
	public static Gson serializer = null;
	
	public static void ready() {
		if(SceneLoader.serializer == null) {
			SceneLoader.serializer = new GsonBuilder()
					.setPrettyPrinting()
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
			
			// First Stop All Entity & Their Components
			for(Entity entity: EntityManager.world_entities.values()) {
				entity.stop();
			}
			
			// Write Entities Into File
			typist.write(SceneLoader.serializer.toJson(EntityManager.world_entities.values()));
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
			Entity[] entities = serializer.fromJson(fileData, Entity[].class);
			
			for(int i=0; i < entities.length; i++) {
				entities[i].start();
				boolean success = EntityManager.add(entities[i]);
				if(!success) {
					System.out.println("Failed to Add " + entities[i].getName());
				}
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
