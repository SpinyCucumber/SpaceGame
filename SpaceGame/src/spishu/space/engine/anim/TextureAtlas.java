package spishu.space.engine.anim;

import spishu.space.engine.gl.Texture;
import spishu.space.engine.math.Vec2;

public class TextureAtlas extends Animation {
	
	private Texture texture;
	private Vec2 dimensions, point;
	
	public TextureAtlas(float speed, Texture texture, Vec2 dimensions) {
		super(speed);
		this.texture = texture;
		this.dimensions = dimensions;
		length = (int) (dimensions.x * dimensions.y);
	}

	public Vec2 getTexCoord(Vec2 texCoord) {
		return texCoord.divDim(dimensions).add(point);
	}

	public Vec2 getTextureDimensions() {
		return new Vec2(texture.getWidth(), texture.getHeight()).divDim(dimensions);
	}

	public void bind() {
		texture.bind();
	}
	
	public TextureAtlas clone() {
		return new TextureAtlas(speed, texture, dimensions.clone());
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		int y = (int) Math.floor(frame / dimensions.x);
		point = new Vec2((int) Math.floor(frame) - y * dimensions.x, y).divDim(dimensions);
	}

}
