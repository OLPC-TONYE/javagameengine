package main;

import java.util.ArrayList;
import java.util.List;

public class LayerPile {
	
	public List<Layer> layers;
	
	public LayerPile() {
		this.layers = new ArrayList<Layer>();
	}
	
	public void pileOntop(Layer layer) {
		if(!contains(layer)) {
			this.layers.add(layer);
			layer.attach();
		}	
	}
	
	public void pushOff(Layer layer) {
		if(contains(layer)) {
			for(Layer clayer: layers) {
				if (clayer.getClass() == layer.getClass()) {
					layers.remove(clayer);
					break;
				}
			}
		}
	}

	public void render() {
		if(!layers.isEmpty()) {
			int top_layer = layers.size()-1;
			this.layers.get(top_layer).render();
		}	
	}
	
	public void update(double dt) {
		if(!layers.isEmpty()) {
			int top_layer = layers.size()-1;
			this.layers.get(top_layer).update(dt);
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
