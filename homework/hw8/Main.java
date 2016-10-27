import java.util.Stack;

public class Main {
    public static void main(String[] args) throws Exception {
        // The first output will be the example given:
        //Leaf l = new Leaf("pupper");
        SingleComposite sc = new SingleComposite(new Leaf("X"));
        LinkedComposite lc = new LinkedComposite( new Leaf( "A" ), new Leaf( "B" ), sc);
        ArrayComposite ac = new ArrayComposite( new Leaf( "C" ), lc, new Leaf( "D" ) );
        
        ArrayComposite ac2 = new ArrayComposite(new Leaf("Z"));
        //System.out.println(l);
        
        //Iter it = lc.makeIterator();
        //testIterator(it);
        //it = ac.makeIterator();
        //testIterator(it);
        //it = sc.makeIterator();
        //testIterator(it);
        Iter it = ac2.makePostOrderIter();
        testIterator(it);
        //testIterFunction(it);
        
    }

    public static void testIterator(Iter it) {
        for (it.First(); !it.IsDone(); it.Next()) {
            System.out.println(it.CurrentItem().toString());
            //System.out.println(it.CurrentItem().getInstanceId());
        }
    }
    
    public static void testIterFunction(Iter it) {
        it.First();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        System.out.println(it.CurrentItem().toString());
        it.Next();
        
    }
}

//////////////////////////////////////////////////////////////////////////
// Leaf class -- has no children rip lineage:(
class Leaf extends Component {
    private String tag;
    protected String instanceId = "Leaf";
    public Leaf (String str) {
        tag = str;
        instanceId = "Leaf[" + tag + "]";
    }
    public String toString() {
        return (parent == null) ? instanceId + " is the root" : instanceId + " is the child of " + parent;
    }
    public Iter makeIterator() {
        class NullIterator<T> implements Iter<T> {
            public NullIterator() {}
            public void First() {}
            public void Next() {} 
            public boolean IsDone() {return true;}
            public T CurrentItem() {return null;}
        }
        return (new NullIterator<Component>());
    }
}
// Component class -- doesn't maintain a list of children. It can know it's parent though.
// Thus, add() and remove() ops need to defer to subclasses.
// In order to do this, I'll have add() and remove() call methods defined by subclasses.
class Component {
    protected Component parent = null;
    protected String instanceId = "Component";
    private final Iter<Component> selfIter;
    
    public Component() {
        selfIter = new Iter<Component>() {
            private Component self = Component.this;
            private boolean onSelf = true;
            public void First() {
                onSelf = true;
            }
            public Component CurrentItem() {
                return self;
            }
            public void Next() {
                onSelf = false;
            }
            public boolean IsDone() {
                return !onSelf;
            }
        };
    }
    
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

    public String toString() {
        return (parent == null) ? instanceId + " is the root" : instanceId + " is the child of " + parent;
    }

    public Iter makeIterator() {
        class NullIterator<T> implements Iter<T> {
            public NullIterator() {}
            public void First() {}
            public void Next() {} 
            public boolean IsDone() {return true;}
            public T CurrentItem() {return null;}
        }
        return (new NullIterator<Component>());
    }
    

    public Iter<Component> makePreOrderIter() {
        return new Iter<Component>() {
            private Stack<Iter<Component>> _iters = new Stack<Iter<Component>>();
            
            public void First() {
                selfIter.First();
                _iters.clear();
                _iters.push(selfIter);
            }
            
            public boolean IsDone() {
                return _iters.isEmpty();
            }
            
            public Component CurrentItem() {
                return _iters.peek().CurrentItem();
            }
            
            public void Next() {
                Iter<Component> i = _iters.peek().CurrentItem().makeIterator();
                i.First();
                _iters.peek().Next();
                _iters.push(i);
                
                while(!_iters.isEmpty() && _iters.peek().IsDone()) 
                    _iters.pop();  
            }
        };
    }
    
    public Iter<Component> makePostOrderIter() {
        return new Iter<Component>() {
            private Stack<Iter<Component>> _iters = new Stack<Iter<Component>>();
            
            public void First() {
                // Add the self iterator to the stack first.
                selfIter.First();
                _iters.clear();
                _iters.push(selfIter);
                // If the self iterator's item has any children, make a new iterator.
                Iter<Component> i = _iters.peek().CurrentItem().makeIterator();
                i.First();
                while(!i.IsDone()) {
                    _iters.push(i);
                    i = i.CurrentItem().makeIterator();
                    i.First();
                }
            }
            
            public boolean IsDone() {
                return _iters.isEmpty();
            }
            
            public Component CurrentItem() {
                return _iters.peek().CurrentItem();
            }
            
            public void Next() {
                _iters.peek().Next();
                if (!_iters.peek().IsDone()) {
                    Iter<Component> i = _iters.peek().CurrentItem().makeIterator();
                    i.First();
                    _iters.push(i); 
                } 

                while(!_iters.isEmpty() && _iters.peek().IsDone()) 
                    _iters.pop();  
                
               
            }
        };
    }
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

//////////////////////////////////////////////////////////////////////////
class SingleComposite extends Component {
    private Component child = null; // only one child.
    protected String instanceId = "SingleComposite";
    
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
        return (parent == null) ? instanceId + " is the root" : instanceId + " is the child of " + parent;
    }

    public Iter makeIterator() {
        class SingleIterator<T> implements Iter<T> {
            private T value = null;
            private boolean onValue = true;
            public SingleIterator(T value) {
                this.value = value;
            }
            public void First() {
                this.onValue = true;
            }
            public void Next() {
                this.onValue = false;
            } 
            public boolean IsDone() {
                return !onValue;
            }
            public T CurrentItem() {
                return this.value;
            }
        }
        return (new SingleIterator<Component>(child));
    }
}

//////////////////////////////////////////////////////////////////////////
class ArrayComposite extends Component {
    private Component[] leafs;
    protected String instanceId = "ArrayComposite";
    
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
        return (parent == null) ? instanceId + " is the root" : instanceId + " is the child of " + parent;
    }

    public Iter makeIterator() {
        class ArrayIterator<T> implements Iter<T> {
            private int index = -1;
            private T[] array = null;
            public ArrayIterator(T[] array) {
                this.array = array;
            }
            public void First() {
                index = 0;
            }
            public void Next() {
                index++;
            }
            public boolean IsDone() {
                return (index >= array.length);
            }
            public T CurrentItem() {
                return array[index];
            }
        }
        return (new ArrayIterator<Component>(leafs));
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
    protected String instanceId = "LinkedComposite";
    
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
        return (parent == null) ? instanceId + " is the root" : instanceId + " is the child of " + parent;
    }

    public Iter makeIterator() {
        class LinkedListIterator<T> implements Iter<T> {
            private Node<T> current = null;
            private Node<T> root = null;
            public LinkedListIterator(Node<T> root) {
                this.root = root;
                this.current = this.root;
            }
            public void First() {
                this.current = root;
            }
            public void Next() {
                this.current = this.current.next;
            }
            public boolean IsDone() {
                if (this.current == null) return true;
                return false;
            }
            public T CurrentItem() {
                return this.current.value;
            }
        }
        return (new LinkedListIterator<Component>(root));
    }
}
//////////////////////////////////////////////////////////////////////////
interface Iter<T> {
    public void First();
    public void Next();
    public boolean IsDone();
    public T CurrentItem();
}