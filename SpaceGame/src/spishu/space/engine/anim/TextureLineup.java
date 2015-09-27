package spishu.space.engine.anim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spishu.space.engine.gl.Texture;
import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;

/**
 * Implementation of animation that uses an array of textures.
 * @author SpinyCucumber
 *
 */
public class TextureLineup extends Animation {
	
	private Texture[] textures;
	
	public TextureLineup(float speed, Texture...textures) {
		super(speed);
		this.textures = textures;
		this.length = textures.length;
	}

	public Vec2 getTexCoord(Vec2 texCoord, int frame) {
		return texCoord;
	}

	public Vec2 getTextureDimensions() {
		List<Vec2> vertices = new ArrayList<Vec2>();
		for(Texture texture : textures) vertices.add(texture.getDimensions());
		return new Shape(vertices).min();
	}

	public void bind(int frame) {
		textures[(int) Math.floor(frame)].bind();
	}

	public Animation clone() {
		return new TextureLineup(speed, textures.clone());
	}

	@Override
	public String toString() {
		return "TextureLineup [textures=" + Arrays.toString(textures) + "]";
	}
	
}
