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

        // Test 2 -- Test of all operations.
        // (0+-1) / (-1) / ((4*0.1) / (9-10)) = -2.5
        MathNode tree2 = new DivNode(
            new AddNode(
                new NumNode(0),
                new NumNode(-1)),
            new SubNode(
                new NumNode(1)),
            new DivNode(
                new MultNode(
                    new NumNode(4),
                    new NumNode(0.1)),
                new SubNode(
                    new NumNode(9),
                    new NumNode(10))
            )
        );
        //testTree(tree2);

        /*
        MathNode simple = new AddNode(
            new NumNode(1),
            new NumNode(2),
            new NumNode(3)
        );
        testTree(simple);
        */



    }


    public static void testTree(MathNode tree) {
        //System.out.println(tree.evaluate());
        MathNodeVisitor v = new InfixVisitor();
        tree.accept(v);
        System.out.println(v.toString());
    }

}

abstract class MathNodeVisitor {
    public abstract void visit(MathNode node); // defined by concrete visitors.
    public abstract String toString(); // all conc visitors define this?
}

// For a given math node, get the infix string representation from it.
class InfixVisitor extends MathNodeVisitor {
    private MathNode m;
    public void visit(MathNode m) { this.m = m; }
    public String toString() { return Infix(this.m); } // Calls the recursive Infix String maker.
    
    private static String Infix(MathNode m) {
        int size = m.children.size();
        if (size == 0) {
            // Bottom of recursion. Return NumNode number.
            return m.toString();
        } else if (size == 1) {
            // Some nodes can have one element. Not sure what to do here...
            // I mean, they're called binary operations for a reason.
            // Add the identity element? Na I don't want to edit the visitor.
            
            
            
        } else if (size >= 2) {
            // Print the recursive infix expression.
            String exp = "(";
            for (int i = 0; i < size; i++) {
                exp += Infix(m.children.get(i));
                if (i < size-1) {
                    exp += " " + m.toString() + " ";
                }
            }
            exp += ")";
            return exp;
        }
        return null;
    }
}





///////////////////////////////////////////////////////////////////////////////////////

// Abstract base class for AddNode, SubNode, MultNode, and DivNode.
// Also abstract base class for NumNode, which is isomorphic to a Leaf.
abstract class MathNode {
    // All nodes need have a parent except for the root.
    public MathNode parent = null;
    // All nodes should have children, except NumNodes, which are leaves.
    public ArrayList<MathNode> children = new ArrayList<MathNode>();
    // The identity element if an operative node, the value if a NumNode.
    public double value;
    
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
    //private double value;
    public NumNode(double value) { this.value = value; }
    public double calculate() { return this.value; }
    public String toString() {
        if ((this.value % 1) == 0)
            return Integer.toString((int)this.value);
        else
            return Double.toString(this.value);
    }
}

// Operational nodes. Have no need for a constructor.
// Only define a handy toString and their calculation operation.

// Associative operations -- order does not matter.
class AddNode extends MathNode {
    public AddNode(MathNode... nodes) {
        this.populate(nodes);
        this.value = 0;
    }
    public double calculate() {
        double sum = 0;
        for (MathNode node : this.children)
            sum += node.calculate();
        return sum;
    }
    public String toString() { return "+"; }
}
class MultNode extends MathNode {
    public MultNode(MathNode... nodes) {
        this.populate(nodes);
        this.value = 1;
    }
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
    public SubNode(MathNode... nodes) {
        this.populate(nodes);
        this.value = 0;
    }
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
    public DivNode(MathNode... nodes) {
        this.populate(nodes);
        this.value = 1;
    }
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