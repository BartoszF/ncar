package pl.bartoszf.nc.neuro;

/**
 * Created by Użytkownik on 2016-10-26.
 */
public class Connection {
    public Node left, right;
    public float weight;

    public Connection()
    {

    }

    public Connection(float weight)
    {
        this.weight = weight;
    }

    public Connection(Node left, Node right, float weight)
    {
        this.left = left;
        this.left.outputs.add(this);
        this.right = right;
        this.right.inputs.add(this);
        this.weight = weight;
    }

    public Connection(Connection c)
    {
        this.left = new Node(c.left);
        this.left.outputs.add(this);
        this.right = new Node(c.right);
        this.right.inputs.add(this);
        this.weight = new Float(c.weight);
    }

    public void dispose()
    {
        //this.left = null;
        //this.right = null;
    }
}
