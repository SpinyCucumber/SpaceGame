package spishu.space.engine.anim;

import spishu.space.engine.math.Vec2;

public abstract class Animation { //Ported from CodingClub

	protected int length;
	protected float frame, speed;
	
	public abstract Vec2 getTexCoord(Vec2 texCoord);
	public abstract Vec2 getTextureDimensions();
	public abstract void bind();
	public abstract Animation clone();
	
	public void update(double delta) {
		frame = (frame + speed * (float) delta) % length;
	}
	
	public Animation(float speed) {
		this.speed = speed;
	}
	
}
