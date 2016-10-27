package pl.bartoszf.nc;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import pl.bartoszf.nc.car.Car;
import pl.bartoszf.nc.car.Constants;

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
        car.genome.neuro.inputs.get(index).val = 1;
        if(fixture.getFilterData().categoryBits != Constants.WORLD)
        {
            car.inputs[index] = 1;
            car.genome.neuro.inputs.get(index).val = 1;
            return -1;
        }

        /*if(index == 0 || index == 1) {
            if(fraction < 0.4f)
            {
                car.genome.score -= 0.15;
            }
            else if((fraction < 0.9f))
                car.genome.score += 0.10;
        }
        else if(fraction < 0.9f)
        {
            car.genome.score -= 0.10;
        }*/
        fraction = MathUtils.clamp(fraction,-1.0f,1.0f);
        car.inputs[index] = fraction;
        car.genome.neuro.inputs.get(index).val = fraction;
        return fraction;
    }
}
