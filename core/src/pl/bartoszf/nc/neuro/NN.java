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
    //public List<Node> secHidden = new ArrayList<Node>();
    public List<Node> outputs = new ArrayList<Node>();
    public List<Connection> conns = new ArrayList<Connection>();

    public NN(int inputs, int hidden, int secHidden, int outputs)
    {
        for(int i=0;i<inputs;i++)
        {
            this.inputs.add(new Node());
        }
        for(int i=0;i<outputs;i++)
        {
            this.outputs.add(new Node());
        }

        for(int i=0;i<hidden;i++)
        {
            Node n = new Node();
            this.hidden.add(n);

            for(int in=0;in<inputs;in++)
            {
                Connection c = new Connection();
                conns.add(c);

                Node inp = this.inputs.get(in);
                c.left = inp;
                c.right = n;
                inp.outputs.add(c);
                n.inputs.add(c);
            }
            for(int in=0;in<outputs;in++)
            {
                Connection c = new Connection();
                conns.add(c);

                Node inp = this.outputs.get(in);
                c.left = n;
                c.right = inp;
                inp.inputs.add(c);
                n.inputs.add(c);
            }
        }

        for(Connection c : conns)
        {
            c.weight = ((float)(Math.random()) * 6 )-3;
        }
    }

    public NN(NN n)
    {
        this.inputs = new ArrayList<Node>();
        this.outputs = new ArrayList<Node>();
        this.hidden = new ArrayList<Node>();

        for(Node i: n.inputs)
        {
            this.inputs.add(new Node());
        }
        for(Node i: n.hidden)
        {
            this.hidden.add(new Node());
        }
        for(Node i: n.outputs)
        {
            this.outputs.add(new Node());
        }
        //this.secHidden = new ArrayList<Node>(n.secHidden);

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
