public class CartPoint extends Magnitude
{
    protected double distance, x, y;

    public CartPoint(double x, double y)
    {
        this.x = x;
        this.y = y;
        // Magnitude in this case is distance formula. Need Math
        this.distance = Math.sqrt(x*x + y*y);
    }

    public boolean lessThan(Magnitude m)
    {
        return this.distance < ((CartPoint)m).distance;
    }
}