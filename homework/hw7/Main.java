public class Main {
    public static void main(String[] args) throws Exception {
        // The first output will be the example given:
        System.out.println("<========< Sample Code >========>");
        LinkedComposite lc = new LinkedComposite( new Leaf( "A" ), new Leaf( "B" ) );
        ArrayComposite ac = new ArrayComposite( new Leaf( "C" ), lc, new Leaf( "D" ) );
        System.out.println(ac.toString());
        
        System.out.println("<========< Test of add() >========>");
        // The second output will be a test of additions.
        Leaf a = new Leaf("A"); Leaf b = new Leaf("B");
        Leaf c = new Leaf("C"); Leaf d = new Leaf("D");
        LinkedComposite ll = new LinkedComposite(a);
        SingleComposite sc = new SingleComposite(d);
        ArrayComposite aa = new ArrayComposite(b, c, sc);
        aa.remove(b);
        Leaf bb = new Leaf("BB");
        aa.add(bb);
        ll.add(aa);
        System.out.println(ll.toString());
        
        System.out.println("<========< Test of remove() >========>");
        // The thrid output will be a test of removals.
        // All leaves will be plucked.
        sc.remove(d);
        aa.remove(bb);
        aa.remove(c);
        ll.remove(a);
        System.out.println(ll.toString());
    }
}

//////////////////////////////////////////////////////////////////////////
// Leaf class -- has no children rip lineage:(
class Leaf extends Component {
    private String tag;
    public Leaf (String str) {
        tag = str;
    }
    public String toString() {
        return this.tabTier() + "Leaf " + tag;
    }
}
// Component class -- doesn't maintain a list of children. It can know it's parent though.
// Thus, add() and remove() ops need to defer to subclasses.
// In order to do this, I'll have add() and remove() call methods defined by subclasses.
abstract class Component {
    private Component parent = null;
    
    public final void add(Component comp) throws Exception {
        add_composite(comp); // defer to subclass's implementation.
        comp.parent = this;
    }
    public final void remove(Component comp) throws Exception {
        remove_composite(comp); // defer to subclass's implementation.
        comp.parent = null;
    }
    
    // Not defined here. Defined in subclasses a la TM.
    protected void add_composite(Component comp) throws Exception {}
    protected void remove_composite(Component comp) throws Exception {}
    
    public String tabTier() {
        // Calculate the printing indent by counting the number of parents.
        String tabs = "";
        Component parent = this.parent;
        while (parent != null) {
            tabs += "    "; // spaces > tabs
            parent = parent.parent;
        }
        return tabs;
    }
}

//////////////////////////////////////////////////////////////////////////
class SingleComposite extends Component {
    private Component child = null; // only one child.
    
    public SingleComposite(Component comp) throws Exception {
        this.add(comp);
    }
    
    public void add_composite(Component comp) throws Exception {
        if (this.child == null) {
            this.child = comp; // you are my child.
        } else {
            // can't add another child.
            throw new Exception("Can't add second child to SingleComposite!");
        }
    }
    
    public void remove_composite(Component comp) throws Exception {
        // Remove the child, if one exists.
        // No component needs to be passed. Just inheriting the function from Component class.
        if (this.child == null) {
            throw new Exception("SingleComposite doesn't have a child to remove!");
        } else {
            this.child = null;
        }
    }
    
    public String toString() {
        // need to figure out how many parents before to calculate the tabulation.
        if (this.child == null) {
            return tabTier() + "SingleComposite contains: empty";
        } else {
            return tabTier() + "SingleComposite contains:\n" + child.toString();
        }
    }
}

//////////////////////////////////////////////////////////////////////////
class ArrayComposite extends Component {
    private Component[] leafs;
    
    public ArrayComposite(Component... comps) throws Exception {
        leafs = new Component[comps.length];
        for (Component comp : comps) {
           this.add(comp);
        }
    }
    
    public void add_composite(Component comp) throws Exception {
        boolean result = false;
        for (int i = 0; i < this.leafs.length; i++) {
            if (this.leafs[i] == null) {
                this.leafs[i] = comp;
                result = true;
                break;
            }
        }
        if (result == false) {
            // error. no space left in the array.
            throw new Exception("ArrayComposite is full!");
        }
    }
    
    public void remove_composite(Component comp) throws Exception {
        boolean result = false;
        for (int i = 0; i < this.leafs.length; i++) {
            if (this.leafs[i] == comp) {
                this.leafs[i] = null;
                result = true;
                break;
            }
        }
        if (result == false) {
            throw new Exception("ArrayComposite doesn't contain value to remove!");
        }
    }
    
    public String toString() {
        String leafs_string = "";
        for (int i = 0; i < this.leafs.length; i++) {
            if (this.leafs[i] == null)
                continue;
            leafs_string += leafs[i].toString();
            if (i != this.leafs.length-1 && leafs[i+1] != null)
                leafs_string += "\n";
        }
        if (leafs_string == "") {
            return tabTier() + "ArrayComposite contains: empty";
        } else {
            return tabTier() + "ArrayComposite contains:\n" + leafs_string;
        }
    }
}

//////////////////////////////////////////////////////////////////////////
class Node<T> {
    public Node<T> next = null;
    public T value;
    public Node (T value) {
      this.value = value;
    }
    public String toString() {
        return this.value.toString();
    }
}
class LinkedComposite extends Component {
    private Node<Component> root = null;
    public LinkedComposite(Component... comps) throws Exception {
        for (Component comp : comps) {
            this.add(comp);
        }
    }
    
    public void add_composite(Component comp) {
        // Add the composite to the linked list. Will always succeed.
        if (this.root == null) {
            this.root = new Node<Component>(comp);
        } else {
            Node<Component> node = this.root;
            while (node.next != null)
                node = node.next;
            node.next = new Node<Component>(comp);
        }
    }
    
    public void remove_composite(Component comp) throws Exception {
        // Remove the composite from the linked list. Fails if composite not found.
        boolean result = false;
        Node<Component> node = this.root;
        if (this.root == null)
            throw new Exception("LinkedComposite can't remove when it has no children!");
        // One-off case: the root could be the one to remove.
        if (node.value == comp) {
            this.root = this.root.next;
            result = true;
        } else {
            while (node.next != null) {
                if (node.next.value == comp) {
                    node.next = node.next.next;
                    result = true;
                    break;
                }
                node = node.next;
            }
        }
        if (result == false) {
            // the composite was not found in the linked list.
            throw new Exception("LinkedComposite doesn't contain value to remove!");
        }
    }
    
    public String toString() {
        String node_string = "";
        Node<Component> node = this.root;
        while (node != null) {
            node_string += node.toString();
            if (node.next != null)
                node_string += "\n";
            node = node.next;
        }
        if (node_string == "") {
            return tabTier() + "LinkedComposite contains: empty";
        } else {
            return tabTier() + "LinkedComposite contains:\n" + node_string;
        }
    }
}
//////////////////////////////////////////////////////////////////////////