package entities;

import org.joml.Vector3f;

public class Sprite {
	
	private Vector3f colour;
	
	public Sprite(Vector3f colour) {
		this.colour = colour;
	}
	
	public Vector3f getColour() {
		return this.colour;
	}

}
