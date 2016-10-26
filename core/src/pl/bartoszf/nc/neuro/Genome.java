package pl.bartoszf.nc.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import pl.bartoszf.nc.car.Car;

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

    public int TTL = 25;

    public Genome(int in, int hid, int shid, int out, World world, Vector2 pos)
    {
        this.neuro = new NN(in, hid,shid, out);
        this.world = world;
        this.pos = pos;
        c = new Car(world,pos,this);
    }

    public Genome(Genome g)
    {
        this.neuro = new NN(g.neuro);
        this.score = 0;
        this.world = g.world;
        this.pos = g.pos;
        this.running = true;
        this.TTL = g.TTL;
        c = new Car(g.world,g.pos,this);
    }

    public void step()
    {
        if(running == false) return;
        if(c != null) {
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
                else if(i < 0.4f)
                {
                    this.score -= 0.1f;
                }
            }
            activate();
            c.update(neuro.outputs[0], neuro.outputs[1]);
            now = new Vector2(c.body.getPosition());

            if(prev != null)
            {
                score += new Vector2(now).sub(prev).len();
            }
        }
    }

    public void setInputs(float[] in)
    {
        for(int i=0;i<in.length;i++)
        {
            neuro.inputs[i] = in[i];
        }
    }

    public void activate()
    {
        for(int h=0;h<neuro.hidden.length;h++)
        {
            float sum = 0;
            for(int i=0;i<neuro.inputs.length;i++)
            {
                sum += neuro.inputs[i] * neuro.conns[h*i];
            }

            neuro.hidden[h] = (NN.sigmoid(sum) * 2) -1;
        }

        int start = neuro.inputs.length * neuro.hidden.length;

        for(int s=0;s<neuro.secHidden.length;s++)
        {
            float sum = 0;
            for(int i=0;i<neuro.hidden.length;i++)
            {
                sum += neuro.hidden[i] * neuro.conns[start+i*s];
            }
            neuro.secHidden[s] = (NN.sigmoid(sum)*2) - 1;
        }

        start = neuro.inputs.length * neuro.hidden.length + neuro.hidden.length * neuro.secHidden.length;

        for(int o=0;o<neuro.outputs.length;o++)
        {
            float sum =0;
            for(int i=0;i<neuro.secHidden.length;i++)
            {
                sum+=neuro.secHidden[i] * neuro.conns[start+i*o];
            }
            neuro.outputs[o] = (NN.sigmoid(sum) * 2) -1;
        }
    }

    public Genome crossover(Genome b, int num)
    {
        Genome a = new Genome(this);
        for(int i=num;i<b.neuro.conns.length;i++)
        {
            a.neuro.conns[i] = new Float(b.neuro.conns[i]);
        }

        return a;
    }

    public Genome mutate(double chance)
    {
        for(int i=0;i<neuro.conns.length;i++)
        {
            double r = Math.random();
            if (r<chance)
            {
                neuro.conns[i] = ((float)Math.random() * 6) - 3;
            }
            if(r<0.01f)
            {
                neuro.conns[i] = 0.0001f;
            }
        }

        return this;
    }

    public void addScore(float s) {this.score += s;}
    public float getScore() {return score;}

    public void end() {this.running = false;/*this.score -= 5;*/}

    public float[] getOutput()
    {
        return neuro.outputs;
    }

    public void dispose()
    {
        neuro.conns = null;
        neuro.inputs = null;
        neuro.outputs = null;
        neuro.hidden = null;
    }
}
