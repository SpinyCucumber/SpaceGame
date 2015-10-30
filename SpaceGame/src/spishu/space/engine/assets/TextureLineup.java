package spishu.space.engine.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2d;

/**
 * Implementation of animation that uses an array of textures.
 * @author SpinyCucumber
 *
 */
public class TextureLineup implements Animation {
	
	private Texture[] textures;
	private float speed, frame;
	private int length;
	
	public TextureLineup(float speed, Texture...textures) {
		this.speed = speed;
		this.textures = textures;
		this.length = textures.length;
	}

	public Vec2d getTexCoord(Vec2d texCoord) {
		return texCoord;
	}

	public Vec2d getTextureDim() {
		List<Vec2d> vertices = new ArrayList<Vec2d>();
		for(Texture texture : textures) vertices.add(texture.getDim());
		return new Shape(vertices).min();
	}

	public void bind() {
		textures[(int) Math.floor(frame)].bind();
	}

	public Animation clone() {
		return new TextureLineup(speed, textures.clone());
	}

	@Override
	public String toString() {
		return "TextureLineup [textures=" + Arrays.toString(textures) + "]";
	}

	@Override
	public void update(double delta) {
		frame = (frame+speed*(float)delta)%length;
	}
	
}
