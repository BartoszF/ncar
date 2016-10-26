package pl.bartoszf.nc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import pl.bartoszf.nc.car.*;
import pl.bartoszf.nc.neuro.Sim;

import java.util.ArrayList;
import java.util.List;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	World world;
	Box2DDebugRenderer debugRenderer;
	private OrthographicCamera cam;

	public Vector2 startPos = new Vector2(115,0);

	Body track;
	Body[] checkpoints = new Body[8];

	Sim sim;

	Vector2 po;

	BitmapFont font;

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

		createChecks();

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
					/*if(c.contacts >= 50)
					{
						c.genome.end();
					}
					else
					{
						c.contacts++;
						//c.genome.score -= 0.5f;
					}*/
				} else if (a.getBody() == track &&
						(b.getBody().getUserData() != null && b.getBody().getUserData().getClass() == Car.class)) {
					Body car = contact.getFixtureB().getBody();
					Car c = (Car) car.getUserData();

                    c.genome.end();
					/*if(c.contacts >= 50)
					{
						c.genome.end();
					}
					else
					{
						c.contacts++;
					}*/
				}
                if (((a.getBody().getUserData()!= null && a.getBody().getUserData().getClass() == Car.class) &&
                        (b.getBody().getUserData()!= null && b.getBody().getUserData() == "CH"))
                        )
                {
                    Body car = contact.getFixtureA().getBody();
                    Car c = (Car) car.getUserData();

                    if(c.genome.running)
                        c.genome.score+=200;
                } else if ((a.getBody().getUserData()!= null && a.getBody().getUserData() == "CH") &&
                        (b.getBody().getUserData() != null && b.getBody().getUserData().getClass() == Car.class)) {
                    Body car = contact.getFixtureB().getBody();
                    Car c = (Car) car.getUserData();

                    if(c.genome.running)
                        c.genome.score+=200;
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
			if(sim.gen.getBest()[0] != null && sim.gen.getBest()[0].c != null && sim.gen.getBest()[0].c.body != null)
				po = sim.gen.getBest()[0].c.body.getPosition();
		}

		world.step((1.0f/30.0f),6,2);

		sim.step();

		cam.position.x = (cam.position.x + po.x) / 2;
		cam.position.y = (cam.position.y + po.y) / 2;

		//cam.position.x = (cam.position.x + checkpoints[0].getPosition().x) / 2;
		//cam.position.y = (cam.position.y + checkpoints[0].getPosition().y) / 2;

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

	private void createChecks()
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		Body check = world.createBody(bodyDef);

		check.setTransform(new Vector2(110,210),0);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(60, 5);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Constants.CHECKPOINT;
		fixtureDef.filter.maskBits = Constants.CAR;
		check.createFixture(fixtureDef);
        check.setUserData("CH");

		checkpoints[0] = check;
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
