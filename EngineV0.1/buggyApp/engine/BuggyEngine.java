package engine;

import leveleditor.LevelEditorLayer;
import main.Application;

public class BuggyEngine extends Application{
		
	public BuggyEngine() {
		super("BuGGy");
		get().launch(() -> {
			pileOnTop(new LevelEditorLayer());
		});
	}

	public static void main(String[] args) {
		new BuggyEngine();
	}
}
