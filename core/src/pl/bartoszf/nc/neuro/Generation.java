package pl.bartoszf.ncplus.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import pl.bartoszf.ncplus.car.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Generation {
    public List<Genome> genomes = new ArrayList<Genome>();
    public Vector2 pos;

    public Generation(int num, int in, int hid, int out, World world, Vector2 pos)
    {
        for(int i=0;i<num;i++)
        {
            genomes.add(new Genome(in,hid,out,world, pos));
        }
    }

    public Generation(Genome a, Genome b, int num)
    {
        genomes.add(a);
        genomes.add(b);

        for(int i=0;i<num;i++)
        {
            Genome m = new Genome(a);
            m.crossover(b,i);
            m.mutate(0.02f);

            genomes.add(m);
        }
    }

    public Genome[] getBest()
    {
        Genome[] best = new Genome[2];

        float maxFScore = -1;
        float maxSScore = -1;

        for(int i=0;i<genomes.size();i++)
        {
            Genome g = genomes.get(i);
            if (g.getScore() > maxFScore) {
                maxFScore = g.getScore();
                best[0] = g;
            } else if (g.getScore() > maxSScore) {
                maxSScore = g.getScore();
                best[1] = g;
            }
        }

        return best;
    }
}
