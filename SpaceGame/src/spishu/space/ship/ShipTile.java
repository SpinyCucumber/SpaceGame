package spishu.space.ship;

import spishu.space.engine.gl.Texture;

public class ShipTile {
	
	private Texture texture;
	private String name;
	
	public ShipTile(String name, String texturePath) {
		this.name = name;
	}

	public static void main(String[] args) {
		ShipTile tile1 = new ShipTile("test", "res/texture/test.png");
	}

}
