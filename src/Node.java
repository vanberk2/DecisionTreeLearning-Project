import java.util.ArrayList;
import java.util.List;

public class Node {
    private ArrayList<Node> children;
    private Node next;
    private String value;

    public Node () {
        children = new ArrayList<>();
        next = null;
        value = null;
    }

    public boolean hasChild () {
        if (!children.isEmpty())
            return true;
        return false;
    }

    public String getValue () {
        return value;
    }

    public Node getNext () { return next; }

    public ArrayList<Node> getChildren () {
        return children;
    }

    public void setValue (String s) {
        this.value = s;
    }

    public void addChild(Node c) {
        this.children.add(c);
    }

    public void  setNext(Node c) {
        this.next = c;
    }
}
