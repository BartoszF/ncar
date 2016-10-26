package pl.bartoszf.nc.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import pl.bartoszf.nc.car.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class Genome {
    public NN neuro;
    public float score;
    public boolean running = true;
    public Car c;
    public World world;
    public Vector2 pos;
    Vector2 prev, now;
    public static List<Genome> genomes = new ArrayList<Genome>();

    public int TTL = 3;

    public Genome(int in, int hid, int shid, int out, World world, Vector2 pos)
    {
        this.neuro = new NN(in, hid,shid, out);
        this.world = world;
        this.pos = pos;
        c = new Car(world,pos,this);
        genomes.add(this);
    }

    public Genome(Genome g)
    {
        this.neuro = new NN(g.neuro);
        this.score = 0;
        this.world = g.world;
        this.pos = g.pos;
        this.running = true;
        if(g.TTL < 0)
            this.TTL = 3;
        else
            this.TTL = g.TTL;
        //c = new Car(g.world,g.pos,this);
        genomes.add(this);
    }

    public void step()
    {
        if(c == null)
        {
            c = new Car(world,pos,this);
        }
        if(running == false) return;
        if(c != null) {
            if(TimeUtils.timeSinceMillis(c.start) >= 20000)
            {
                this.end();
                return;
            }
            if(now != null)
                prev = new Vector2(now);

            for (int i = 0; i < c.inputs.length; i++) {
                c.inputs[i] = 1;
            }
            setInputs(c.inputs);
            for(float i:c.inputs)
            {
                if(i < 0.1f)
                {
                    this.score -= 0.3f;
                }
            }
            activate();
            c.update(neuro.outputs.get(0).val, neuro.outputs.get(1).val);
            now = new Vector2(c.body.getPosition());

            if(prev != null)
            {
                float len = new Vector2(now).sub(prev).len();
                score+= len;
            }
        }
    }

    public void setInputs(float[] in)
    {
        for(int i=0;i<in.length;i++)
        {
            neuro.inputs.get(i).val = in[i];
        }
    }

    public void activate()
    {
        for(Node n: neuro.hidden)
        {
            n.activate();
        }

        for(Node n: neuro.outputs)
        {
            n.activate();
        }
    }

    public void preTrain(float[] inputs, float[] outputs)
    {
        float error = 0;

        do {
            setInputs(inputs);
            activate();
            error = (float)Math.abs((outputs[0] - getOutput()[0].val) + (outputs[1] - getOutput()[1].val));
            float in = 0;
            for(float i : inputs)
            {
                in += i;
            }

            for(int i=0;i<neuro.conns.size();i++)
            {
                neuro.conns.get(i).weight += error * in * 0.1f;
                activate();
                error = (outputs[0] - getOutput()[0].val) + (outputs[1] - getOutput()[1].val);
                if(error < 0.2f) return;
            }
        }while(error > 0.2f);


    }

    public Genome crossover(Genome b, int num)
    {
        Genome a = new Genome(this);
        /*for(int i=b.neuro.conns.length/2;i<b.neuro.conns.length;i++)
        {
            a.neuro.conns[i] = new Float(b.neuro.conns[i]);
        }*/

        for(int i=num; i>0;i--)
        {
            int r = (int)(Math.random() * b.neuro.conns.size());
            a.neuro.conns.get(r).weight = new Float(b.neuro.conns.get(r).weight);
        }

        return a;
    }

    public Genome mutate(double chance)
    {
        for(int i=0;i<neuro.conns.size();i++)
        {
            double r = Math.random();
            if (r<chance)
            {
                neuro.conns.get(i).weight = ((float)Math.random() * 6) - 3;
            }
            if(r<0.01f)
            {
                neuro.conns.get(i).weight = 0;
            }
        }

        return this;
    }

    public void addScore(float s) {this.score += s;}
    public float getScore() {return score;}

    public void end() {this.running = false;/*this.score -= 5;*/}

    public Node[] getOutput() {
        return (Node[])neuro.outputs.toArray();
    }
    public void dispose()
    {
        neuro.dispose();
        c.dispose();
        c = null;
    }
}
