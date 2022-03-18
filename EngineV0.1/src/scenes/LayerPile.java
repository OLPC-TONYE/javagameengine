package scenes;

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
	

	public void unstack(Layer layer) {
		// TODO Auto-generated method stub
		if(contains(layer)) {
			for(Layer clayer: layers) {
				if (clayer.getClass() == layer.getClass()) {
					layers.remove(clayer);
					break;
				}
			}
		}
	}
	
	public void pushOff() {
		if(layers.size()>0) {
			int top_layer = layers.size()-1;
			layers.get(top_layer).detach();
			this.layers.remove(top_layer);
		}		
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
