package pl.bartoszf.nc.neuro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Użytkownik on 2016-10-25.
 */
public class NN {
    public List<Node> inputs = new ArrayList<Node>();
    public List<Node> hidden = new ArrayList<Node>();
    public List<Node> outputs = new ArrayList<Node>();
    public List<Connection> conns = new ArrayList<Connection>();

    public NN(int inputs, int hidden, int outputs)
    {
        for(int i=0;i<inputs;i++)
        {
            this.inputs.add(new Node());
        }
        for(int i=0;i<outputs;i++)
        {
            this.outputs.add(new Node());
        }

        for(int i=0;i<inputs;i++)
        {
            for(int o=0;o<outputs;o++)
            {
                Connection c = new Connection(this.inputs.get(i),this.outputs.get(o),(float)(Math.random() * 6)-3);
                conns.add(c);
            }
        }
    }

    public NN(NN n)
    {
        this.inputs = new ArrayList<Node>();
        this.outputs = new ArrayList<Node>();
        this.hidden = new ArrayList<Node>();

        for(Node i: n.inputs)
        {
            this.inputs.add(new Node(i));
        }
        for(Node i: n.hidden)
        {
            this.hidden.add(new Node(i));
        }
        for(Node i: n.outputs)
        {
            this.outputs.add(new Node(i));
        }

        for(int i=0;i<n.hidden.size();i++)
        {
            Node hi = this.hidden.get(i);

            for(int in=0;in<this.inputs.size();in++)
            {
                Node inp = this.inputs.get(in);
                Connection c = new Connection(inp,hi,0);
                conns.add(c);
            }
            for(int in=0;in<this.outputs.size();in++)
            {
                Node inp = this.outputs.get(in);
                Connection c = new Connection(hi,inp,0);
                conns.add(c);
            }
        }

        for(int i=0;i<this.inputs.size();i++)
        {
            for(int o=0;o<this.outputs.size();o++)
            {
                Connection c = new Connection(this.inputs.get(i),this.outputs.get(o),(float)(Math.random() * 6)-3);
                conns.add(c);
            }
        }

        for(int c = 0;c<n.conns.size();c++)
        {
            this.conns.get(c).weight = new Float(n.conns.get(c).weight);
        }
    }

    public void dispose()
    {
        /*for(Node n:inputs)
        {
            n.dispose();
        }
        for(Node n:hidden)
        {
            n.dispose();
        }
        for(Node n:outputs)
        {
            n.dispose();
        }*/
    }

    @Override
    public String toString()
    {
        String val="";
        val += "Inputs : \n";
        val += inputs;
        val += "\nHidden : \n";
        val += hidden;
        val += "\nOutputs : \n";
        val += outputs;
        val += "\nConns : \n";
        val += conns;
        val += "\nConns length : \n";
        val += conns.size();

        return val;
    }

    public String shortString()
    {
        String val="";
        val += "Inputs : \n";
        val += inputs;
        val += "\nHidden : \n";
        val += hidden;
        val += "\nOutputs : \n";
        val += outputs;

        return val;
    }

    public static float sigmoid(float x) {
        return ((1/( 1 + (float)Math.pow((float)Math.E,(-1*x)))) * 2)-1;
    }
}
