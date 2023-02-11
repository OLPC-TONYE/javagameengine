package leveleditor;

import org.lwjgl.glfw.GLFW;

import listeners.KeyListener;
import main.Application;
import main.Layer;
import renderer.Renderer;
import renderer.Renderer3D;
import scenes.Scene;

public class LevelTestLayer extends Layer
{

	Scene levelScene;
	Renderer renderer;

	@Override
	public void attach() {
		renderer = new Renderer3D();
		LevelTestScene play = new LevelTestScene();
		play.init();
		play.findCameras();
		levelScene = play;
	}

	@Override
	public void detach() {
		levelScene.close();
	}

	@Override
	public void render() {
		levelScene.render(renderer);
	}

	@Override
	public void update(double dt) {
		levelScene.update(dt);
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			Application.get().pushOffPile(this);
		}
	}

}
