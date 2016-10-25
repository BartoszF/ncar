package pl.bartoszf.nc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import pl.bartoszf.nc.car.*;

import static com.badlogic.gdx.graphics.GL20.GL_POINTS;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	World world;
	Box2DDebugRenderer debugRenderer;
	private float accumulator = 0;
	private OrthographicCamera cam;
	float drive = 0;

	Tire t;
	Car car;
	Body track;

	Raycast r;
	Raycast leftr;
	Raycast rightr;
	Raycast sleftr;
	Raycast srightr;

	Vector2 rcent;
	Vector2 sec;
	Vector2 left;
	Vector2 right;
	Vector2 sleft;
	Vector2 sright;

	ShapeRenderer sr;

	public static Body[] contacts = new Body[5];

	@Override
	public void create () {
		batch = new SpriteBatch();
		world = new World(new Vector2(0, 0), true);

		debugRenderer = new Box2DDebugRenderer(true,false,false,true,false,true);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam = new OrthographicCamera(250, 250 * (h / w));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();

		createGrounds();
		loadTrack();
		car = new Car(world, new Vector2(100,0));

		r = new Raycast(car, 2);
		leftr = new Raycast(car, 0);
		rightr = new Raycast(car, 4);
		sleftr = new Raycast(car, 1);
		srightr = new Raycast(car, 3);

		sr = new ShapeRenderer();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		for(int i=0;i<5;i++)
			contacts[i] = world.createBody(bodyDef);

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(0.5f, 0.5f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0;
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = 0;
		fixtureDef.filter.maskBits  = 0;
		for(int i=0;i<5;i++) {
			Fixture fixture = contacts[i].createFixture(fixtureDef);
		}
	}

	@Override
	public void render () {
		world.step((1.0f/60.0f),6,2);

		for(int i=0;i<5;i++)
		{
			car.inputs[i] = 1;
		}

		float d = 0;
		float a = 0;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			a = -1;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			a = 1;
		}
		else
		{
			a = 0;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			d = 1;
		}
		else
		{
			d = 0;
		}

		shootRays();

		/*if(drive > 1 || drive <= 0)
		{
			drive = 1;
		}
		drive = (drive * 2) - 1;
		System.out.println("Drive : " + drive);*/
		car.update(d,a);

		Vector2 po = car.body.getPosition();

		cam.position.x = (cam.position.x + po.x) / 2;
		cam.position.y = (cam.position.y + po.y) / 2;

		cam.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		debugRenderer.render(world,cam.combined);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	private void createGrounds(){

		BodyDef bodyDef = new BodyDef();
		Body ground = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Constants.GROUND;
		fixtureDef.filter.maskBits = Constants.TIRE;

		shape.setAsBox(9, 7, new Vector2(-10,15), 20*Constants.DEGTORAD);
		Fixture groundAreaFixture = ground.createFixture(fixtureDef);
		groundAreaFixture.setUserData(new GroundAreaType(2, false));

		shape.setAsBox(9,  5, new Vector2(5, 20), -40 * Constants.DEGTORAD);
		groundAreaFixture = ground.createFixture(fixtureDef);
		groundAreaFixture.setUserData(new GroundAreaType(0.02f, false));
	}

	private void loadTrack(){
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("test.json"));

		BodyDef bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyDef.BodyType.StaticBody;
		FixtureDef fd = new FixtureDef();
		fd.density = 1;
		fd.friction = 0.5f;
		fd.restitution = 0.3f;
		fd.filter.categoryBits = Constants.WORLD;

		track = world.createBody(bd);
		loader.attachFixture(track, "Track", fd, 75);
	}

	private void shootRays()
	{
		Vector2 forw = car.body.getWorldVector(new Vector2(0, 1));

		Vector2 cpos = car.body.getPosition();
		rcent = new Vector2(forw).scl(4).add(cpos);
		sec = new Vector2(forw).scl(40).add(rcent);
		left = new Vector2(sec);
		CarMath.rotate_point(left,rcent,30);
		right = new Vector2(sec);
		CarMath.rotate_point(right,rcent,-30);
		sleft = new Vector2(sec);
		CarMath.rotate_point(sleft,rcent,15);
		sright = new Vector2(sec);
		CarMath.rotate_point(sright,rcent,-15);

		world.rayCast(r, rcent , sec);
		world.rayCast(leftr, rcent , left);
		world.rayCast(rightr, rcent , right);
		world.rayCast(sleftr, rcent , sleft);
		world.rayCast(srightr, rcent , sright);
	}


}
