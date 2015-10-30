package spishu.space.engine.assets;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Vec2d;

public class SingleTexture implements Animation {
	
	private Texture texture;
	
	public SingleTexture(Texture texture) {
		this.texture = texture;
	}

	@Override
	public Vec2d getTexCoord(Vec2d texCoord) {
		return texCoord;
	}

	@Override
	public Vec2d getTextureDim() {
		return texture.getDim();
	}

	@Override
	public void bind() {
		texture.bind();
	}

	@Override
	public void update(double delta) {
		//Do nothing
	}

	@Override
	public Animation clone() {
		return new SingleTexture(texture);
	}

}
