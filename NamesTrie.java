
import java.util.LinkedList;

public class NamesTrie {
    protected class Node {
        Node parent;
        char value;
        LinkedList<Node> children;

        // Used when deleting or searching for names.
        // What if you were searching for the name Ali and the name Aliaa is present in the Trie?
        // You will need to know that a word ends at the letter 'i'
        // The variable word_end defines the number of names ending in this node.
        int word_end;

        Node(Node parent, char value, boolean word_end) {
            this.parent = parent;
            this.value = value;
            if (word_end) this.word_end = 1;
            else this.word_end = 0;
            children = new LinkedList<>();
            if (value != '*' || parent == null) children.add(new Node(this, '*', false));
        }

        public boolean isLeaf() {return (children.size() == 1 && children.peekFirst().getValue() == '*');}
        public boolean isWordEnd() {
            if (word_end == 0) return false;
            else return true;
        }
        public boolean isWordsEnd() {
            if (word_end > 1) return true;
            else return false;
        }
        public void stopTheEnd() {word_end--;} // Used in deletion
        public void extendTheEnd() {word_end++;} // Used in insertion

        public Node getParent() {return parent;}
        public char getValue() {return value;}
        public void setValue(char new_value) {value = new_value;}

        public void addChild(int index, char value, boolean end) {
            if (children.size() > index && children.get(index).getValue() == '*') children.set(index, new Node(this, value, end));
            else children.add(index, new Node(this, value, end));
        }

        public void setChild(int index, char new_value) {children.get(index).setValue(new_value);}
        public void setChild(int index, Node new_node) {
            new_node.parent = this;
            children.set(index, new_node);
        }

        public LinkedList<Node> getChildren() {return children;}
        
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    Node root;

    NamesTrie() {
        root = new Node(null, '*', false);
    }

    public void insert(String[] value) {
        String to_insert = value[0] + " " + value[1];
        insert(to_insert, root);
    }

    private void insert(String to_insert, Node start) {
        char value = to_insert.charAt(0); // Gets the first character of the name you want to insert
        String next;  // Gets the rest of the string without the first character. Set to null if the string had only 1 character.
        if (to_insert.length() > 1) next = to_insert.substring(1);
        else next = null;

        LinkedList<Node> children = start.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            if (value < n.getValue() || n.getValue() == '*') {
                // If the character we want to insert is smaller than the character we are comparing to, add it before that
                // character and if there were characters remaining to insert, recur using the new node as the start node.
                if (to_insert.length() > 1) {
                    start.addChild(i, value, false);
                    insert(next, start.getChildren().get(i));
                } else start.addChild(i, value, true);
                break;
            } else if (value == n.getValue()) {
                // If the character we want to insert is already there, recur using the node containing it as the start node.
                if (to_insert.length() > 1) insert(next, start.getChildren().get(i));
                else n.extendTheEnd();
                break;
            } else if (i == children.size() - 1) {
                // If the character we want to insert is greater than all other charcters in the same level, add it to the end
                // of the list and if there were characters remaining to insert, recur using the new node as the start node.
                if (to_insert.length() > 1) {
                    start.addChild(i + 1, value, false);
                    insert(next, start.getChildren().get(i + 1));
                } else start.addChild(i + 1, value, true);
            }
        }
    }

    public void delete(String value) {
        delete(value, root);
    }

    private void delete(String to_delete, Node start) {
        char value = to_delete.charAt(0);
        String next;
        if (to_delete.length() > 1) next = to_delete.substring(1);
        else next = null;

        LinkedList<Node> children = start.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            if (value == n.getValue() && next != null) {
                // If the character we are searching for was found, recur using it's node as the new node.
                delete(next, start.getChildren().get(i));
            } else if (value == n.getValue() && next == null && n.isWordEnd() && (!n.isLeaf() || n.isWordsEnd())) {
                // If we find the name we want to delete but it is part of another name (Ali & Aliaa), or more than one
                // person owns this name, use stopTheEnd() method.
                n.stopTheEnd();
            } else if (value == n.getValue() && next == null && n.isLeaf()) {
                // If we find the name we want to delete and it is not part of another name, use removeWord() method and pass
                // to it the node containing the last letter of the name.
                removeWord(n);
            }
        }
    }

    private void removeWord(Node endOfWord) {
        Node parent = endOfWord.getParent();
        // If the parent is only part of this name and not any other name, then no need to keep it so we will delete it to, so
        // recur using the parent node.
        if (parent.getChildren().size() == 1 && parent != root && !parent.isWordEnd()) removeWord(parent);
        // If the parent is the root or a character that is also part of another name, delete the endOfWord node.
        else parent.getChildren().remove(endOfWord);
    }

    public LinkedList<String> search(String value) {
        return search(value, root, 0);
    }

    private LinkedList<String> search(String to_search, Node start, int letter_index) {
        LinkedList<String> to_return = new LinkedList<>();  // A list that should contain all names starting with to_search.

        LinkedList<Node> children = start.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            if (n.getValue() > to_search.charAt(letter_index)) return to_return;  // Not found.
            else if (n.getValue() == to_search.charAt(letter_index) && letter_index == to_search.length() - 1) {
                // When we reach the end of to_search, if it was a leaf then just add it to the list and we got only 1 name,
                // but if it wasn't a leaf then we got more than one name so we use the getAllNames method to get them.
                if (n.isLeaf()) to_return.add(to_search);
                else {
                    if (n.isWordEnd()) to_return.add(to_search.substring(0, letter_index+1));
                    to_return = getAllNames(n, to_search.substring(0, to_search.length()-1), to_return);
                }
                return to_return;
            }
            else if (n.getValue() == to_search.charAt(letter_index) && !n.isLeaf()) {
                return search(to_search, n, letter_index + 1);
            }
        }
        return to_return;
    }

    public void breadthFirst() {
        // Was just used for testing and debugging.
        LinkedList<Node> lst = new LinkedList<>();
        lst.addLast(root);

        while (!lst.isEmpty()) {
            Node c = lst.removeFirst();
            System.out.println("Parent: " + c.getParent() + "\tChild: " + c);
            LinkedList<Node> children = c.getChildren();
            for (Node i: children) {
                if (i.getValue() != '*') lst.addLast(i);
            }
            System.out.println();
        }
    }

    public void getAllNames() {System.out.println(getAllNames(root, "", new LinkedList<>()));}

    private LinkedList<String> getAllNames(Node start, String continuing, LinkedList<String> names) {
        if (start != root) continuing += start.toString(); // The part of the name that we are trying to get its rest.
        if (!start.isLeaf()) {
            // If it isn't a leaf node, then first check if it is word end. If so, add it to the list. Then recur for every
            // child using it as the start node.
            if (start.isWordEnd()) names.add(continuing);
            for (Node i: start.getChildren()) {
                names = getAllNames(i, continuing, names);
            }
        }
        // If this is a leaf node, we got a name! :)
        if (start.isLeaf()) names.add(continuing);
        return names;
    }
}
