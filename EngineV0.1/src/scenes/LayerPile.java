package scenes;

import java.util.ArrayList;
import java.util.List;

public class LayerPile {
	
	public List<Layer> layers;
	
	public LayerPile() {
		this.layers = new ArrayList<Layer>();
	}
	
	public void stack(Layer layer) {
		if(!contains(layer)) {
			this.layers.add(layer);
			layer.attach();
		}	
	}
	
	public void unstack() {
		layers.get(layers.size()-1).detach();
		this.layers.remove(layers.size()-1);
	}
	
	public void render() {
		if(!layers.isEmpty()) {
			this.layers.get(layers.size()-1).render();
		}
	}
	
	public void update(double dt) {
		if(!layers.isEmpty()) {
			this.layers.get(layers.size()-1).update(dt);
		} 
	}

	public void unstackAll() {
		for(Layer layer:layers) {
			layer.detach();
		}
		layers.clear();
	}
	
	public boolean contains(Layer layer) {
		for(Layer clayer: layers) {
			if (clayer.getClass() == layer.getClass()) {
				return true;
			}
		}
		return false;
	}
}
