import java.util.ArrayList;
public class Test {
    public static void main(String[] args) throws Exception {
        ArrayList<MathNode> forest = new ArrayList<MathNode>();
        
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
        forest.add(tree1);
        
        // Test 2 -- Test of all operations.
        // (0+-1) / (-1) / ((4*0.1) / (9-10)) = -2.5
        MathNode tree2 = new DivNode(
            new AddNode(
                new NumNode(0),
                new NumNode(-1)),
            new SubNode(
                new NumNode(-1),
                new NumNode(15)),
            new DivNode(
                new MultNode(
                    new NumNode(4),
                    new NumNode(1)),
                new SubNode(
                    new NumNode(9),
                    new NumNode(10))
            )
        );
        forest.add(tree2);
        
        
        // Test 3 -- simple additive tree.
        MathNode tree3 = new AddNode(
            new NumNode(0),
            new AddNode( new NumNode(1),
                new AddNode( new NumNode(2),
                    new AddNode( new NumNode(3),
                        new AddNode( new NumNode(4),
                            new NumNode(5)
        )))));
        forest.add(tree3);
        
        // Test 4 -- Example of an invalid arithmetic tree.
        try {
            MathNode badtree = new AddNode(
                new NumNode(5),
                new SubNode(
                    new NumNode(5))
            );
        } catch (Exception ex) {
            System.out.println("[========[ sample invalid tree ]========]");
            ex.printStackTrace();
        }
        
        // Test 5 -- Example textual tree given in assignment.
        MathNode tree5 = new AddNode(
            new NumNode(11),
            new MultNode(
                new DivNode(
                    new NumNode(1),
                    new NumNode(2)),
                new SubNode(
                    new AddNode(
                        new NumNode(3),
                        new NumNode(-5)),
                    new NumNode(45))),
            new NumNode(-23)
        );
        forest.add(tree5);
        
        // Test all created trees.
        testTrees(forest);
    }


    public static void testTrees(ArrayList<MathNode> trees) {
        for (MathNode tree : trees) {
            System.out.println("[========[ testing arithmetic tree ]========]");
            
            MathNodeVisitor infix = new InfixVisitor();
            tree.accept(infix);
            System.out.println("Infix:\t" + infix.toString());
            
            MathNodeVisitor lisp = new LispVisitor();
            tree.accept(lisp);
            System.out.println("Lisp:\t" + lisp.toString());
            
            MathNodeVisitor calculator = new ValueVisitor();
            tree.accept(calculator);
            System.out.println("Value:\t" + calculator.toString());
            
            MathNodeVisitor textree = new TextTreeVisitor();
            tree.accept(textree);
            System.out.println("Text-tree:\n" + textree.toString());
        }
    }

}

abstract class MathNodeVisitor {
    // The visit operation is defined by concrete visitors.
    public abstract void visit(MathNode node);
}

// For a given math node, get the infix string representation from it.
class InfixVisitor extends MathNodeVisitor {
    private MathNode m;
    public void visit(MathNode m) { this.m = m; }
    public String toString() { return Infix(this.m); }
    
    private static String Infix(MathNode m) {
        int size = m.children.size();
        if (size == 0) {
            // Bottom of recursion. Return NumNode number.
            return m.toString();
        } else {
            // Print the recursive infix expression.
            String exp = "";
            if (m.parent != null)
                exp += "(";
            for (int i = 0; i < size; i++) {
                exp += Infix(m.children.get(i));
                if (i < size-1) {
                    exp += " " + m.toString() + " ";
                }
            }
            if (m.parent != null)
                exp += ")";
            return exp;
        }
    }
}


class LispVisitor extends MathNodeVisitor {
    private MathNode m;
    public void visit(MathNode m) { this.m = m; }
    public String toString() { return Lisp(this.m); }
    
    private static String Lisp(MathNode m) {
        // Similar to the infix, just print the operation first.
        int size = m.children.size();
        if (size == 0) { return m.toString(); }
        else {
            String exp = "(";
            exp += m.toString() + " ";
            for (int i = 0; i < size; i++) {
                exp += Lisp(m.children.get(i));
                if (i != size-1)
                    exp += " ";
            }
            exp += ")";
            return exp;
        }
    }
    
}


class TextTreeVisitor extends MathNodeVisitor {
    private MathNode m;
    public void visit(MathNode m) { this.m = m; }
    public String toString() { return Treeify(" ", this.m); }
    // Similar to printing the Composite structure in hw8, we need to know the indent.
    // The initial indent is a space to fix allignment with the first bracket.
    private static String Treeify(String indent, MathNode m) {
        int size = m.children.size();
        if (size == 0) {
            return "[" + m.toString() + "]\n";
        } else {
            String stem = "[" + m.toString() + "]\n";
            String branch = "";
            for (int i = 0; i < size; i++) {
                if (i != size-1)
                    // There are still children. Print a pipe.
                    branch += indent + "+---" + Treeify(indent + "|    ", m.children.get(i));
                else
                    // No child underneath me. Remove the pipe.
                    branch += indent + "+---" + Treeify(indent + "     ", m.children.get(i));
            }
            // Readd the new branch to the main stem.
            stem += branch;
            return stem;
        }
    }
}

class ValueVisitor extends MathNodeVisitor {
    private MathNode m;
    private double result;
    public void visit(MathNode m) {
        this.m = m;
    }
    public String toString() { return Double.toString(Eval(this.m)); }
    public double getValue() { return Eval(this.m); }

    private double Eval(MathNode m) {
        int size = m.children.size();
        if (size == 0) {
            // Reached a NumNode. Get the value.
            return ((NumNode)m).value;
        } else {
            double result = Eval(m.children.get(0));
            for (int i = 1; i < size; i++) {
                result = parseOp(m.toString(), result, Eval(m.children.get(i)));
            }
            return result;
        }
    }

    private static double parseOp(String operation, double oldval, double newval) {
        // Parse the operation. Return the result of the operation.
        switch(operation) {
            case "+":
                return oldval + newval;
            case "*":
                return oldval * newval;
            case "-":
                return oldval - newval;
            case "/":
                return oldval / newval;
        }
        return -1; // will never reach this.
    }

}



///////////////////////////////////////////////////////////////////////////////////////

// Abstract base class for AddNode, SubNode, MultNode, and DivNode.
// Also abstract base class for NumNode, which is isomorphic to a Leaf.
abstract class MathNode {
    // All nodes should have children, except NumNodes, which are leaves.
    public MathNode parent = null;
    public ArrayList<MathNode> children = new ArrayList<MathNode>();
    // All operational math nodes call this method to populate their children.
    protected void populate(MathNode... nodes) throws Exception {
        if (nodes.length <= 1)
            throw new Exception("arithmetic operations require at least two operands!");
        for (MathNode node : nodes) {
            node.parent = this;
            this.children.add(node);
        }
    }
    // Visit is abstract -- different per concrete visitor.
    public void accept(MathNodeVisitor visitor) { visitor.visit(this); }
}

// 'Leaf'-like class. NumNode has no children.
class NumNode extends MathNode {
    public int value;
    public NumNode(int value) { this.value = value; }
    public String toString() { return Integer.toString(this.value); }
}
// Operational classes. Have no special members.
class AddNode extends MathNode {
    public AddNode(MathNode... nodes) throws Exception { this.populate(nodes); }
    public String toString() { return "+"; }
}
class MultNode extends MathNode {
    public MultNode(MathNode... nodes) throws Exception { this.populate(nodes); }
    public String toString() { return "*"; }
}
class SubNode extends MathNode {
    public SubNode(MathNode... nodes) throws Exception { this.populate(nodes); }
    public String toString() { return "-"; }
}
class DivNode extends MathNode {
    public DivNode(MathNode... nodes) throws Exception  { this.populate(nodes); }
    public String toString() { return "/"; }
}