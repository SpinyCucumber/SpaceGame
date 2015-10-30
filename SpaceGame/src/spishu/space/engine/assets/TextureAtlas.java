package spishu.space.engine.assets;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Vec2d;

public class TextureAtlas implements Animation {
	
	private Texture texture;
	private Vec2d dimensions, point;
	private float speed, frame;
	private int length;
	
	public TextureAtlas(float speed, Texture texture, Vec2d dimensions) {
		this.speed = speed;
		this.texture = texture;
		this.dimensions = dimensions;
		length = (int) (dimensions.x * dimensions.y);
	}
	
	@Override
	public Vec2d getTexCoord(Vec2d texCoord) {
		return texCoord.divDim(dimensions).add(point);
	}

	@Override
	public Vec2d getTextureDim() {
		return new Vec2d(texture.getWidth(), texture.getHeight()).divDim(dimensions);
	}
	
	@Override
	public void bind() {
		texture.bind();
	}
	
	@Override
	public TextureAtlas clone() {
		return new TextureAtlas(speed, texture, dimensions.clone());
	}
	
	@Override
	public void update(double delta) {
		frame = (frame+speed*(float)delta)%length;
		int y = (int) Math.floor(frame / dimensions.x);
		point = new Vec2d((int) Math.floor(frame) - y * dimensions.x, y).divDim(dimensions);
	}

}
