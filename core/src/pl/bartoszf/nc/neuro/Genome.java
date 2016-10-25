package pl.bartoszf.ncplus.neuro;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import pl.bartoszf.ncplus.car.Car;

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

    public Genome(int in, int hid, int out, World world, Vector2 pos)
    {
        this.neuro = new NN(in, hid, out);
        this.world = world;
        this.pos = pos;
        c = new Car(world,pos,this);
    }

    public Genome(Genome g)
    {
        this.neuro = new NN(g.neuro);
        this.score = new Float(g.score);
        this.running = true;
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

            neuro.hidden[h] = NN.sigmoid(sum);
        }

        int start = neuro.inputs.length * neuro.hidden.length;

        for(int o=0;o<neuro.outputs.length;o++)
        {
            float sum =0;
            for(int i=0;i<neuro.hidden.length;i++)
            {
                sum+=neuro.hidden[i] * neuro.conns[start+i*o];
            }
            neuro.outputs[o] = NN.sigmoid(sum);
        }
    }

    public Genome crossover(Genome b, int num)
    {
        Genome a = new Genome(this);
        for(int i=num;i<b.neuro.conns.length;i++)
        {
            a.neuro.conns[i] = b.neuro.conns[i];
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
                neuro.conns[i] = ((float)Math.random() * 2) - 1;
            }
        }

        return this;
    }

    public void addScore(float s) {this.score += s;}
    public float getScore() {return score;}

    public void end() {this.running = false;}

    public float[] getOutput()
    {
        return neuro.outputs;
    }
}
