package spishu.space.engine.texture;

import spishu.space.engine.math.Shape;
import spishu.space.engine.math.Vec2;

public class TextureLineup extends Animation {
	
	private Texture[] textures;
	
	public TextureLineup(float speed, Texture...textures) {
		super(speed);
		this.textures = textures;
		this.length = textures.length;
	}

	public Vec2 getTexCoord(Vec2 texCoord) {
		return texCoord;
	}

	public Vec2 getTextureDimensions() {
		Vec2[] vertices = new Vec2[length];
		for(int i = 0; i < length; i++) {
			Texture texture = textures[i];
			vertices[i] = new Vec2(texture.getWidth(), texture.getHeight());
		}
		return new Shape(vertices).min();
	}

	public void bind() {
		textures[(int) Math.floor(frame)].bind();
	}

	public Animation clone() {
		return new TextureLineup(speed, textures.clone());
	}

}
