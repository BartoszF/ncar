package pl.bartoszf.ncplus;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import pl.bartoszf.ncplus.car.Car;
import pl.bartoszf.ncplus.car.CarMath;
import pl.bartoszf.ncplus.car.Constants;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Raycast implements RayCastCallback
{
    Car car;
    int index = 0;

    public Raycast(Car car, int index)
    {
        this.car = car;
        this.index = index;
    }
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
    {
        car.inputs[index] = 1;
        if(fixture.getFilterData().categoryBits != Constants.WORLD)
        {
            car.inputs[index] = 1;
            return -1;
        }

        Game.contacts[index].setTransform(point,0);

        fraction = MathUtils.clamp(fraction,-1.0f,1.0f);
        car.inputs[index] = fraction;
        return fraction;
    }
}
