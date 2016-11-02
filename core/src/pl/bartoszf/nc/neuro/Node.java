package pl.bartoszf.nc.neuro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Użytkownik on 2016-10-26.
 */
public class Node {
    public float val;
    public List<Connection> inputs = new ArrayList<Connection>();
    public List<Connection> outputs = new ArrayList<Connection>();
    public int id;
    public static int lastId = 0;

    public Node()
    {
        this.id = lastId++;
    }

    public Node(Node n)
    {
        this.val = n.val;
        this.id = n.id;
        //this.inputs = new ArrayList<Connection>(n.inputs);
        //this.outputs = new ArrayList<Connection>(n.outputs);
    }

    public void dispose()
    {
        for (Connection c: inputs)
            c.dispose();
        inputs.clear();
        inputs = null;

        for (Connection c: outputs)
            c.dispose();
        outputs.clear();
        outputs = null;
    }

    public void activate()
    {
        float sum = 0;
        for(Connection c: inputs)
        {
            sum += c.left.val * c.weight;
        }

        this.val = NN.sigmoid(sum);
    }
}
