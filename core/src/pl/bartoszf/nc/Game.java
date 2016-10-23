package pl.bartoszf.nc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	World world;
	Box2DDebugRenderer debugRenderer;
	private float accumulator = 0;
	private OrthographicCamera cam;

	Tire t;
	Car car;

	@Override
	public void create () {
		batch = new SpriteBatch();
		world = new World(new Vector2(0, 0), true);

		debugRenderer = new Box2DDebugRenderer(true,true,false,true,true,false);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam = new OrthographicCamera(60, 60 * (h / w));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();
		car = new Car(world);
	}

	@Override
	public void render () {
		world.step((1.0f/60.0f),6,2);

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
}
