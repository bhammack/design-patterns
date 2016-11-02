public class Currency extends Magnitude
{
    protected double amount;
    public Currency (double amount)
    {
        // Simple constructor.
        this.amount = amount;
    }

    public boolean lessThan(Magnitude m)
    {
        // Overriding the parent class's method.
        // According to the assignment, its safe to assume
        // that (m) won't be a CartPoint.
        return this.amount < ((Currency)m).amount;
    }
}