package pl.bartoszf.nc.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import pl.bartoszf.nc.car.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Sim {
    public Generation gen;
    World world;
    public int genNum = 1;
    public int numRunning;

    public Sim(World world, Vector2 pos)
    {
        this.world = world;
        gen = new Generation(30,8,14,5,2, world, pos);
        numRunning = 30;
    }

    public void step()
    {
        numRunning = gen.genomes.size();
        for(int i=0;i<gen.genomes.size();i++)
        {
            Genome a = gen.genomes.get(i);
            a.step();
            if(!a.running)
            {
                numRunning--;
            }
        }

        if(numRunning <= 0)
        {
            Genome[] best = gen.getBest();
            if(best.length == 2) {
                System.out.println("Generation : " + genNum);
                System.out.println("Best score : " + best[0].getScore());
                System.out.println("Second best : " + best[1].getScore());
                Genome f = new Genome(best[0]);
                Genome s = new Genome(best[1]);
                int sle = 0;
                Array<Body> bodies = new Array<Body>();
                world.getBodies(bodies);
                for (Body b : bodies) {
                    if (!b.isAwake()) sle++;
                }
                System.out.println("Sleeping : " + sle);
                System.out.println("Cars : " + Car.cars.size());

                Generation gen2 = new Generation(f, s, gen.genomes.size() - 1, this.gen.genomes);

                System.out.println("After : " + Car.cars.size());
                for (Genome g : gen.genomes) {
                    g.dispose();
                }
                gen = null;
                gen = gen2;
                genNum++;
                numRunning = gen.genomes.size();
            }
            else
                System.out.println(best.length);
        }
    }

    public Genome getGenome(int index)
    {
        return gen.genomes.get(index);
    }
}
