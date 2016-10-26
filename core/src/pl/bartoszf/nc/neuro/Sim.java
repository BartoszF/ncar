package pl.bartoszf.nc.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import pl.bartoszf.nc.car.Car;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Sim {
    public Generation gen;
    public int genNum = 1;
    public int numRunning;

    public Sim(World world, Vector2 pos)
    {
        gen = new Generation(49,5,12,5,2, world, pos);
        numRunning = 49;
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

        //System.out.println(numRunning);

        if(numRunning <= 0)
        {
            for(Car c: Car.cars)
            {
                c.dispose();
            }
            Car.cars.clear();
            Genome[] best = gen.getBest();
            System.out.println("Generation : " + genNum);
            System.out.println("Best score : " + best[0].getScore());
            System.out.println("Second best : " + best[1].getScore());
            best[0].TTL--;
            best[1].TTL--;
            Genome f = new Genome(best[0]);
            Genome s = new Genome(best[1]);
            Generation gen2 = new Generation(f,s,gen.genomes.size()-2,gen.genomes);
            for(Genome g: gen.genomes)
            {
                g.dispose();
            }
            gen = null;
            gen = gen2;
            genNum++;
            numRunning = gen.genomes.size();
        }
    }

    public Genome getGenome(int index)
    {
        return gen.genomes.get(index);
    }
}
