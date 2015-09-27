package spishu.space.engine.anim;

import spishu.space.engine.math.Vec2;

/**
 * Multi-framed  animation, has 2 implementations.
 * @author SpinyCucumber
 *
 */
public abstract class Animation { //Ported from CodingClub

	protected int length;
	protected float speed, frame;
	
	public abstract Vec2 getTexCoord(Vec2 texCoord, int frame);
	public abstract Vec2 getTextureDimensions();
	public abstract void bind(int frame);
	public abstract Animation clone();
	
	public void update(double delta) {
		frame = (frame + speed * (float) delta) % length;
	}
	
	public void bind() {
		bind((int) frame);
	}
	
	public Vec2 getTexCoord(Vec2 texCoord) {
		return getTexCoord(texCoord, (int) frame);
	}
	
	public Animation(float speed) {
		this.speed = speed;
	}
	
}
