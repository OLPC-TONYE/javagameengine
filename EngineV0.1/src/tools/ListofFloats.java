package tools;

import java.util.ArrayList;

public class ListofFloats {
	
	ArrayList<Float> floats = new ArrayList<>();
	
	public void add(float f) {
		floats.add(Float.valueOf(f));
	}
	
	public void add(float[] floats) {
		for(float f: floats) {
			this.floats.add(Float.valueOf(f));
		}
	}
	
	public float get(int index) {
		float f = floats.get(index).floatValue();
		return f;
	}
	
	public float[] toArray() {
		float[] array = new float[floats.size()];
		for(int i =0; i < floats.size(); i++) {
			array[i] = floats.get(i).floatValue();
		}
		return array;
	}
}