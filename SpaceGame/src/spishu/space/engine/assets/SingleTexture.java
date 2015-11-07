package spishu.space.engine.assets;

import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Vec2d;

public class SingleTexture implements Animation {
	
	private Texture texture;
	
	public SingleTexture(Texture texture) {
		this.texture = texture;
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
	public Animation copy() {
		return new SingleTexture(texture);
	}

	@Override
	public String toString() {
		return "SingleTexture [texture=" + texture + "]";
	}

}
