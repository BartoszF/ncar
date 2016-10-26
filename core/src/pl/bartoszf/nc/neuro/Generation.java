package pl.bartoszf.nc.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import pl.bartoszf.nc.car.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Generation {
    public List<Genome> genomes = new ArrayList<Genome>();
    public Vector2 pos;

    public Generation(int num, int in, int hid, int shid, int out, World world, Vector2 pos)
    {
        for(int i=0;i<num;i++)
        {
            genomes.add(new Genome(in,hid,shid,out,world, pos));
        }
    }

    public Generation(Genome a, Genome b, int num, List<Genome> genomes)
    {
        if(a.TTL > 0)
        {
            this.genomes.add(a);
        }
        else
        {
            genomes.remove(a);
            a = new Genome(getBest(genomes)[0]);
            this.genomes.add(a);
        }

        genomes.remove(b);
        num++;
        //b = new Genome(getBest(genomes)[0]);
        //this.genomes.add(b);

        /*if(b.TTL > 0)
        {
            this.genomes.add(b);
        }
        else {
            genomes.remove(b);
            b = new Genome(getBest(genomes)[0]);
            this.genomes.add(b);
        }*/

        for(int i=0;i<num;i++)
        {
            Genome m = new Genome(a);
            m.crossover(b,i);
            m.mutate(0.4f);

            this.genomes.add(m);
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

    public Genome[] getBest(List<Genome> genomes)
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
