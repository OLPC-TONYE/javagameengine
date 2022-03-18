package engine;

import main.Application;

public class BuggyEngine{
	
	private final static Application app = new Application();
	
	public static void main(String[] args) throws InterruptedException {
		app.launch();
	}
	
	public static Application get() {
		return app;
	}

}
