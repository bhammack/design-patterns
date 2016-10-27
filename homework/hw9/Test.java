import java.util.ArrayList;
public class Test {
    public static void main(String[] args) {

        // Test 1 -- Expression given in class.
        MathNode tree1 = new DivNode(
            new NumNode(1), 
            new AddNode(
                new NumNode(3),
                new NumNode(2),
                new MultNode(
                    new NumNode(9),
                    new NumNode(11)
                )
            )
        );
        testTree(tree1);

        // Test 2 -- Example 1.
        MathNode tree2 = new SubNode(
            
        )






    }


    public static void testTree(MathNode tree) {
        System.out.println(tree.evaluate());
    }

}

abstract class MathNodeVisitor {
    public abstract void visit(MathNode node); // defined by concrete visitors.
}


// Abstract base class for AddNode, SubNode, MultNode, and DivNode.
// Also abstract base class for NumNode, which is isomorphic to a Leaf.
abstract class MathNode {
    // All nodes need have a parent except for the root.
    protected MathNode parent = null;
    // All nodes should have children, except NumNodes, which are leaves.
    protected ArrayList<MathNode> children = new ArrayList<MathNode>();
    
    // All operational math nodes call this method to populate their children.
    protected void populate(MathNode... nodes) {
        for (MathNode node : nodes) {
            node.parent = this;
            this.children.add(node);
        }
    }

    // Visit is abstract -- different per concrete visitor.
    public void accept(MathNodeVisitor visitor) { visitor.visit(this); }

    // Evaluate is our abstract operation. Only called from client.
    // Calculate is our specific operation. Left up to concrete node.
    abstract protected double calculate();
    public double evaluate() { return calculate(); }

    public String toString() {
        // TODO: modify this such that you print all children?
        return "";
    }
}

// 'Leaf'-like class. NumNode has no children.
// Using doubles to save a bunch of casting.
class NumNode extends MathNode {
    private double value;
    public NumNode(double value) { this.value = value; }
    public double calculate() { return this.value; }
    public String toString() { return Double.toString(this.value); }
}

// Operational nodes. Have no need for a constructor.
// Only define a handy toString and their calculation operation.

// Associative operations -- order does not matter.
class AddNode extends MathNode {
    public AddNode(MathNode... nodes) { this.populate(nodes); }
    public double calculate() {
        double sum = 0;
        for (MathNode node : this.children)
            sum += node.calculate();
        return sum;
    }
    public String toString() { return "+"; }
}
class MultNode extends MathNode {
    public MultNode(MathNode... nodes) { this.populate(nodes); }
    public double calculate() {
        double product = 1;
        for (MathNode node: this.children)
            product *= node.calculate();
        return product;
    }
    public String toString() { return "*"; }
}

// Non-associative operations -- order matters.
class SubNode extends MathNode {
    public SubNode(MathNode... nodes) { this.populate(nodes); }
    public double calculate() {
        double difference = 0;
        if (this.children.size() == 1)
            difference -= this.children.get(0).calculate();
        if (this.children.size() > 1)
            difference = this.children.get(0).calculate();

        for (int i = 1; i < this.children.size(); i++)
            difference -= this.children.get(i).calculate();
        return difference;
    }
    public String toString() { return "-"; }
}
class DivNode extends MathNode {
    public DivNode(MathNode... nodes) { this.populate(nodes); }
    public double calculate() {
        double quotient = 1;
        if (this.children.size() == 1)
            quotient /= this.children.get(0).calculate();
        if (this.children.size() > 1)
            quotient = this.children.get(0).calculate();

        for (int i = 1; i < this.children.size(); i++)
            quotient /= this.children.get(i).calculate();
        return quotient;
    }
    public String toString() { return "/"; }
}