package spishu.space.ship;

import spishu.space.engine.gl.Texture;

/**
 * An individual tile of a ship.
 * @author SpinyCucumber
 *
 */
@SuppressWarnings("unused")
public class ShipTile {
	
	private Texture texture;
	private String name;
	
	public ShipTile(String name, String texturePath) {
		this.name = name;
	}

}
