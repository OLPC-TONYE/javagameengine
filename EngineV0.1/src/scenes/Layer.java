package scenes;

public abstract class Layer {
	
	public abstract void attach();
	public abstract void detach();
	public abstract void render();
	public abstract void update(double dt);

}
