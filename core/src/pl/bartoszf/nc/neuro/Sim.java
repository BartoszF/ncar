package pl.bartoszf.ncplus.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import pl.bartoszf.ncplus.car.Car;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Sim {
    public Generation gen;
    public int genNum = 1;
    public int numRunning;

    public Sim(World world, Vector2 pos)
    {
        gen = new Generation(49,5,8,2, world, pos);
        numRunning = 49;
    }

    public void step()
    {
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
            gen = new Generation(best[0],best[1],gen.genomes.size());
            genNum++;
            numRunning = gen.genomes.size();
        }
    }

    public Genome getGenome(int index)
    {
        return gen.genomes.get(index);
    }
}
