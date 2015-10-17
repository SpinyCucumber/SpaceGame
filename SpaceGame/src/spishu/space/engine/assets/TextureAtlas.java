package spishu.space.engine.assets;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Vec2d;

public class TextureAtlas extends Animation {
	
	private Texture texture;
	private Vec2d dimensions, point;
	
	public TextureAtlas(float speed, Texture texture, Vec2d dimensions) {
		super(speed);
		this.texture = texture;
		this.dimensions = dimensions;
		length = (int) (dimensions.x * dimensions.y);
	}

	public Vec2d getTexCoord(Vec2d texCoord, int frame) {
		return texCoord.divDim(dimensions).add(point);
	}

	public Vec2d getTextureDim() {
		return new Vec2d(texture.getWidth(), texture.getHeight()).divDim(dimensions);
	}

	public void bind(int frame) {
		texture.bind();
	}
	
	public TextureAtlas clone() {
		return new TextureAtlas(speed, texture, dimensions.clone());
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		int y = (int) Math.floor(frame / dimensions.x);
		point = new Vec2d((int) Math.floor(frame) - y * dimensions.x, y).divDim(dimensions);
	}

}
