package spishu.space.engine.assets;

import spishu.space.engine.math.Vec2d;

/**
 * Multi-framed  animation, has 2 implementations.
 * @author SpinyCucumber
 *
 */
public abstract class Animation {
	
	protected float frame;
	private int length;
	private float speed;
	
	public abstract Vec2d getTextureDim();
	public abstract void bind();
	public abstract Animation copy();
	
	public void update(double delta) {
		frame = (frame+speed*(float)delta)%length;
	}
	
	public void transformTexCoords() { }
	
	protected Animation(int length, float speed) {
		this.length = length;
		this.speed = speed;
	}
	
}
