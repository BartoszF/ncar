package pl.bartoszf.nc.neuro;

import java.util.Arrays;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class NN {
    public float[] inputs;
    public float[] hidden;
    public float[] secHidden;
    public float[] outputs;
    public float[] conns;

    public NN(int inputs, int hidden, int secHidden, int outputs)
    {
        this.inputs = new float[inputs];
        this.hidden = new float[hidden];
        this.secHidden = new float[secHidden];
        this.outputs = new float[outputs];
        this.conns = new float[hidden*inputs + hidden* secHidden + secHidden*outputs];
        for(int i =0; i<hidden*inputs + hidden* secHidden + secHidden*outputs;i++)
        {
            this.conns[i] = ((float)Math.random() * 6) - 3;
        }
    }

    public NN(NN n)
    {
        this.inputs = new float[n.inputs.length];
        for(int i=0;i<inputs.length;i++)
        {
            this.inputs[i] = new Float(n.inputs[i]);
        }
        this.outputs = new float[n.outputs.length];
        for(int i=0;i<outputs.length;i++)
        {
            this.outputs[i] = new Float(n.outputs[i]);
        }
        this.secHidden = new float[n.secHidden.length];
        for(int i=0;i<secHidden.length;i++)
        {
            this.secHidden[i] = new Float(n.secHidden[i]);
        }
        this.hidden = new float[n.hidden.length];
        for(int i=0;i<hidden.length;i++)
        {
            this.hidden[i] = new Float(n.hidden[i]);
        }
        this.conns = new float[n.conns.length];
        for(int i=0;i<conns.length;i++)
        {
            this.conns[i] = new Float(n.conns[i]);
        }
    }

    @Override
    public String toString()
    {
        String val="";
        val += "Inputs : \n";
        val += Arrays.toString(inputs);
        val += "\nHidden : \n";
        val += Arrays.toString(hidden);
        val += "\nOutputs : \n";
        val += Arrays.toString(outputs);
        val += "\nConns : \n";
        val += Arrays.toString(conns);
        val += "\nConns length : \n";
        val += conns.length;

        return val;
    }

    public static float sigmoid(double x) {
        return (1/( 1 + (float)Math.pow((float)Math.E,(-1*x))));
    }
}
