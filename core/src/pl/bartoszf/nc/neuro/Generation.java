package pl.bartoszf.nc.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

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
        this.genomes = new ArrayList<Genome>();
        if(a.TTL > 0)
        {
            this.genomes.add(new Genome(a));
        }
        else
        {
            System.out.println("Champion died");
            num++;
        }

        System.out.println(num);

        for(int i=0;i<num;i++)
        {
            Genome m = new Genome(a);
            m.crossover(b,i);
            m.mutate(0.3f);

            this.genomes.add(m);
        }

        List<Genome> toDel = new ArrayList<Genome>();
        for(Genome g: genomes)
        {
            if(!this.genomes.contains(g))
            {
                toDel.add(g);
            }
        }

        for(Genome g: toDel)
        {
            g.dispose();
            g = null;
        }

        genomes.clear();
        toDel.clear();
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
