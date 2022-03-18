package leveleditor;

import renderer.Renderer;
import scenes.Layer;
import scenes.Scene;

public class LevelTestLayer extends Layer
{

	Scene levelScene;
	Renderer renderer;

	@Override
	public void attach() {
		renderer = new Renderer();
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
		renderer.clear();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.0f);
		levelScene.render(renderer);
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		levelScene.update(dt);
	}

}
