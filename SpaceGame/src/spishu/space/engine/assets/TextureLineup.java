package spishu.space.engine.assets;

import java.util.ArrayList;
import java.util.List;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2d;

/**
 * Implementation of animation that uses an array of textures.
 * @author SpinyCucumber
 *
 */
public class TextureLineup extends Animation {
	
	private List<Texture> textures;
	
	public TextureLineup(float speed, List<Texture> textures) {
		super(textures.size(), speed);
		this.textures = textures;
	}

	public Vec2d getTextureDim() {
		List<Vec2d> vertices = new ArrayList<Vec2d>();
		for(Texture texture : textures) vertices.add(texture.getDim());
		return new Shape(vertices).min();
	}

	public void bind() {
		textures.get((int) Math.floor(frame)).bind();
	}

	public Animation copy() {
		List<Texture> newList = new ArrayList<Texture>();
		newList.addAll(textures);
		return new TextureLineup(speed, newList);
	}

	@Override
	public String toString() {
		return "TextureLineup [textures=" + textures + "]";
	}
	
}
