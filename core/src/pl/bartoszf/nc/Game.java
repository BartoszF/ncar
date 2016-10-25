package pl.bartoszf.ncplus;

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
import pl.bartoszf.ncplus.car.*;
import pl.bartoszf.ncplus.neuro.Genome;
import pl.bartoszf.ncplus.neuro.Sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_POINTS;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	World world;
	Box2DDebugRenderer debugRenderer;
	private float accumulator = 0;
	private OrthographicCamera cam;
	float drive = 0;

	public Vector2 startPos = new Vector2(100,0);

	Tire t;
	List<Car> cars = new ArrayList<Car>();
	Body track;

	Sim sim;

	List<Car> toDestroy = new ArrayList<Car>();
	Vector2 po;

	public static Body[] contacts = new Body[5];

	@Override
	public void create () {
		batch = new SpriteBatch();
		world = new World(new Vector2(0, 0), true);

		debugRenderer = new Box2DDebugRenderer(true, false, false, true, false, true);

		Gdx.graphics.setWindowedMode(900, 900);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam = new OrthographicCamera(250, 250 * (h / w));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();

		createGrounds();
		loadTrack();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		for (int i = 0; i < 5; i++)
			contacts[i] = world.createBody(bodyDef);

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(0.5f, 0.5f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0;
		fixtureDef.shape = polygonShape;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = 0;
		fixtureDef.filter.maskBits = 0;
		for (int i = 0; i < 5; i++) {
			Fixture fixture = contacts[i].createFixture(fixtureDef);
		}

		sim = new Sim(world,startPos);

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Fixture a = contact.getFixtureA();
				Fixture b = contact.getFixtureB();
				if (((a.getBody().getUserData()!= null && a.getBody().getUserData().getClass() == Car.class) &&
						b.getBody() == track)
						) {
					Body car = contact.getFixtureA().getBody();
					Car c = (Car) car.getUserData();

					c.genome.end();
					//c.dispose();

					//c = null;
					toDestroy.add(c);
				} else if (a.getBody() == track &&
						(b.getBody().getUserData() != null && b.getBody().getUserData().getClass() == Car.class)) {
					Body car = contact.getFixtureB().getBody();
					Car c = (Car) car.getUserData();

					c.genome.end();
					//c.dispose();

					//c = null;

					toDestroy.add(c);
				}
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}


	@Override
	public void render () {
		if(sim.gen.getBest() != null && sim.gen.getBest().length > 0) {
			if(sim.gen.getBest()[0].c != null && sim.gen.getBest()[0].c.body != null)
				po = sim.gen.getBest()[0].c.body.getPosition();
		}

		world.step((1.0f/60.0f),6,2);

		for(Car c : toDestroy)
		{
			c.dispose();
		}
		toDestroy.clear();

		sim.step();

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


}
