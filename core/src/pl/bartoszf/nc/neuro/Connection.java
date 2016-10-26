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
        this.right = right;
        this.weight = weight;
    }

    public Connection(Connection c)
    {
        this.left = c.left;
        this.right = c.right;
        this.weight = new Float(c.weight);
    }

    public void dispose()
    {
        this.left = null;
        this.right = null;
    }
}
