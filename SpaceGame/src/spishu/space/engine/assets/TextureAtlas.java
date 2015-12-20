package spishu.space.engine.assets;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Vec2d;

public class TextureAtlas extends Animation {
	
	private Texture texture;
	private Vec2d dim, point;
	
	public TextureAtlas(float speed, Texture texture, Vec2d dim) {
		super((int) (dim.x * dim.y), speed);
		this.texture = texture;
		this.dim = dim.inverse();
	}

	@Override
	public void transform() {
		point.glTranslate();
		dim.glScale(); //Too simple to store in VRAM.
	}

	@Override
	public Vec2d getTextureDim() {
		return new Vec2d(texture.getWidth(), texture.getHeight()).divDim(dim);
	}
	
	@Override
	public void bind() {
		texture.bind();
	}
	
	@Override
	public TextureAtlas copy() {
		return new TextureAtlas(speed, texture, dim.clone());
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		int y = (int) Math.floor(frame / dim.x);
		point = new Vec2d((int) Math.floor(frame) - y * dim.x, y);
	}

}
