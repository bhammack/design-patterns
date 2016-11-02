public abstract class Magnitude
{
    // Currency and CartPoint will extend this method.
    protected abstract boolean lessThan(Magnitude m);

    // Since the subclasses will override lessThan, the only way the
    // other methods can be implemented is in terms of lessThan.
    // This feels kind of cheaty, but it works.    
    protected final boolean lessThanEqualTo(Magnitude m)
    {
        return (this.lessThan(m) || this.equalTo(m));
    }

    protected final boolean equalTo(Magnitude m)
    {
        // If this and m are equal, this is not less than m,
        // and m is not less than this.
        return !(this.lessThan(m)) && !(m.lessThan(this));
    }

    protected final boolean greaterThanEqualTo(Magnitude m)
    {
        return (this.greaterThan(m) || this.equalTo(m));
    }

    protected final boolean greaterThan(Magnitude m)
    {
        // If this is not less than, and it is not equal, it is greater.
        return (!this.lessThan(m) && this.notEqual(m));
    }

    protected final boolean notEqual(Magnitude m)
    {
        return !(this.equalTo(m));
    }
}