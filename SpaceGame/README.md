it's a gme
abut space
n compoters
so enjoy
first edit from ipad

This project is becoming sort-of an engine that I can build demos and other games off of.
It contains alot of useful classes.

Default SpaceGame configuration file (config.yml):
  windowWidth: 1000
  windowHeight: 618
  cameraMoveSpeed: 3000.0
  cameraZoomSpeed: 0.99
  logLevel: INFO
  timeScale: 2.0
  vsync: true
  useShaders: false
  frameSkip: 0

Example of GameObject:

public class BadGame extends GameObject {
	
	World world;
	ShapeEntity platform;
	
	@Override
	protected void onExit() {
		world.delete();
	}

	@Override
	protected void onStart() throws Exception {
		
		Game.setSource(BadGame.class);
		Game.useDefaultLoaders();
		Game.loadResources();
		Controls.initialize(window);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		world = new World(new Vec2d(0,150), 20,100);
		Game.info("Initialized world %s", world);
		
		Animation anim = (Animation) Game.getResource("texture\\sonic.anim");
		Shape bounds = Rect.fromAABB(AABB.fromDim(anim.getTextureDim().div(2)));
		new Player(world, new Vec2d(0,0), new Vec2d(0,0), 1,0,0,0.5f,0.1f, bounds, anim).spawn();
		platform = new ShapeEntity(world, Vec2d.ZERO, null, 0,0,0,0.5f,0.15f, Rect.fromAABB(AABB.fromDim(new Vec2d(100,100))), anim);
		
		Vec2d d = window.getDim().div(2);
		new AABB(d.negate(), d).glOrtho();
		
		((ALSource) Game.getResource("sound\\sparta.ogg")).play();
		newPlatform(new Vec2d(0,200));
		
	}

	@Override
	protected void update(double delta) throws Exception {
		world.update(delta);
	}

	public static void main(String[] args) {
		new BadGame().start();
	}

	@Override
	protected void draw() throws Exception {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        world.draw();
	}
	
	private void newPlatform(Vec2d position) {
		ShapeEntity copy = platform.copy();
		copy.setPosition(position);
		copy.spawn();
	}

}