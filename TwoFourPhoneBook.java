
import java.util.Arrays;
import java.util.LinkedList;

public class TwoFourPhoneBook extends PhoneBook {

    protected class Node {
        Node parent;
        LinkedList<Node> children;
        /* The value is a linked list containing an array because each contact will be kept in an array,
        *  and the 2-4 tree can contain 1-3 contacts in a single node.
        */
        LinkedList<String[]> value;

        Node(Node parent, LinkedList<String[]> value) {
            this.parent = parent;
            this.value = value;
            children = new LinkedList<>();
            if (value != null || parent == null) children.add(new Node(this, null));
        }

        public boolean isLeaf() {return children.peekFirst().getValue() == null;}

        public void setValue(LinkedList<String[]> new_value) {value = new_value;}
        public LinkedList<String[]> getValue() {return value;}

        public void addChild(LinkedList<String[]> child_value) {
            if (children.size() == 1 && children.get(0).getValue() == null) children.set(0, new Node(this, child_value));
            children.add(new Node(this, child_value));
        }
        public void addChild(Node child_node) {
            if (children.size() == 1 && children.get(0).getValue() == null) children.set(0, child_node);
            else children.add(child_node);
            child_node.parent = this;
        }
        public void addChild(int index, Node new_node) {
            if (children.size() > index && children.get(index).getValue() == null) children.set(index, new_node);
            else children.add(index, new_node);
            new_node.parent = this;
        }

        public void setChild(int index, Node new_node) {
            new_node.parent = this;
            children.set(index, new_node);
        }

        public LinkedList<Node> getChildren() {return children;}
        public boolean hasChildren() {return !isLeaf();}

        public Node getParent() {return parent;}
        public void setParent(Node parent) {
            this.parent = parent;
            if (!parent.hasChildren()) parent.setChild(0, this);
            else parent.addChild(this);
        }

        @Override
        public String toString() {
            if (value == null) return "null";
            return Arrays.deepToString(value.toArray());
        }
    }

    Node root;
    int size;

    TwoFourPhoneBook() {
        root = new Node(null, null);
        size = 1;
    }

    @Override
    public int size() {return size;}
    private boolean isEmpty() {return root.getValue() == null;}
    private boolean isRoot(Node n) {return n == root;}
    
    @Override
    public void insert(String[] element) {
        insert(element, root);
    }

    private void insert(String[] element, Node start) {
        if (isEmpty()) {
            // Inserting the first element to the tree
            LinkedList<String[]> value = new LinkedList<>();
            value.add(element);
            root.setValue(value);
            return;
        }
        if (start.getValue().size() == 3) {
            // If the node we are inserting to contains 3 elements, split it and continue inserting.
            insert(element, split(start));
            return;
        }

        if (start.isLeaf()) {
            // If we reached a leaf node, that means that we can insert.
            for (int i = 0; i < start.getValue().size(); i++) {
                // For every contact in this node...
                String[] contact = start.getValue().get(i);
                int comparison = compareNames(element, contact);
                if (comparison == 0) {
                    // If a contact with the same name already exists in this node, use upgrade contact.
                    int s = start.getValue().get(i).length;
                    start.getValue().set(i, upgradeContact(contact, element));
                    if (start.getValue().get(i).length > s) size++;
                    break;
                }
                else if (comparison == -1) {
                    // If the new contact alphabatically precedes the contact it's compared to, add it before that contact.
                    start.getValue().add(i, element);
                    size++;
                    break;
                }
                else if (i == start.getValue().size() - 1) {
                    // If the new contact comes after all contacts in that node alphabatically, add it after that contact.
                    start.getValue().add(i+1, element);
                    size++;
                    break;
                }
            }
        } else {
            // If this node isn't a leaf node
            for (int i = 0; i < start.getValue().size(); i++) {
                // For every contact in this node...
                String[] contact = start.getValue().get(i);
                int comparison = compareNames(element, contact);
                if (comparison == -1) {
                    // If the new contact alphabatically precedes the contact it's compared to, insert to the left child of that contact.
                    insert(element, start.getChildren().get(i));
                    break;
                } else if (comparison == 0) {
                    // If a contact with the same name already exists in this node, use upgrade contact.
                    int s = start.getValue().get(i).length;
                    start.getValue().set(i, upgradeContact(contact, element));
                    if (start.getValue().get(i).length > s) size++;
                    break;
                } else if (i == start.getValue().size() - 1) {
                    // If the new contact comes after all contacts in that node alphabatically, insert to the right child of that contact.
                    insert(element, start.getChildren().get(i+1));
                    break;
                }
            }
        }
    }

    @Override
    public void delete(String name) {
        delete(name, root);
    }

    private void delete(String name, Node start) {
        for (int i = 0; i < start.getValue().size(); i++) {
            // For every contact in this node...
            String[] contact = start.getValue().get(i);
            int comparison = compareNames(name.split(" "), contact);
            if (comparison == 0) {
                size--;
                // If the name was found...
                if (contact.length > 5) {
                    // If there were 2 contacts carrying the same name, use the choose(contact) method instead of deleting.
                    start.getValue().set(i, choose(contact));
                    return;
                }
                if (!start.isLeaf()) {
                    // If the contact wasn't in a leaf node, swap between it and it's predecessor using the swap() method.
                    // Then change the node and the position we are deleting from to the new position of the contact.
                    start = swap(start, contact);
                    i = start.getValue().size() - 1;
                }

                Node parent = start.getParent();
                // If the node we are deleting from had 2 or more contacts, or was the root node,
                // we can delete from it without any restrictions.
                if (start.getValue().size() >= 2 || isRoot(start)) start.getValue().remove(i);

                // If it had only one contact and its sibling had 2 or more contacts, use the rotate method to delete the contact.
                else if (i != 0 && parent.getChildren().get(i - 1).getValue().size() >= 2)
                rotate(start, parent.getChildren().get(i - 1), false);
                else if (i != parent.getChildren().size()-1 && parent.getChildren().get(i + 1).getValue().size() >= 2)
                rotate(start, parent.getChildren().get(i + 1), true);

                // If it had only one contact and its sibling had 1 contact, use the merge method to delete the contact.
                else if (i != 0 && parent.getChildren().get(i - 1).getValue().size() == 1)
                merge(start, parent.getChildren().get(i - 1), false);
                else if (i != parent.getChildren().size()-1 && parent.getChildren().get(i + 1).getValue().size() == 1)
                merge(start, parent.getChildren().get(i + 1), true);
            }
            else if (comparison == -1 && start.hasChildren()) {
                // If the contact we are deleting alphabatically precedes the contact it's compared to,
                // use the delete method for the left child of that contact.
                delete(name, start.getChildren().get(i));
                break;
            }
            else if (i == start.getValue().size()-1 && start.hasChildren()) {
                // If the contact we are deleting comes after all contacts in that node alphabatically,
                // use the delete method for the right child of that contact.
                delete(name, start.getChildren().get(i + 1));
                break;
            }
        }
    }

    @Override
    public String[] search(String name) {
        return search(name, root);
    }

    private String[] search(String name, Node start) {
        for (int i = 0; i < start.getValue().size(); i++) {
            // For every contact in this node...
            String[] contact = start.getValue().get(i);
            int comparison = compareNames(name.split(" "), new String[] {contact[0], contact[1]});
            if (comparison == 0) return contact; // If the contact was found
            else if (comparison == -1 && start.hasChildren()) return search(name, start.getChildren().get(i));
            else if (i == start.getValue().size()-1 && start.hasChildren()) return search(name, start.getChildren().get(i + 1));
        }
        return name.split(" ");
    }

    private Node split(Node start) {
        if (isRoot(start)) {
            // To split a node, it needs to have a parent. So if that node was the root, we will add a new node as its parent and
            // set the root to be that new node.
            start.setParent(new Node(null, new LinkedList<>()));
            root = start.getParent();
        }

        LinkedList<String[]> x = new LinkedList<>(); // The node's left value
        x.add(start.getValue().removeFirst());
        LinkedList<String[]> y = new LinkedList<>(); // The node's right value
        y.add(start.getValue().removeLast());

        int index = start.getParent().getChildren().indexOf(start); // Index where the mid value will be added in the parent node
        start.getParent().getValue().add(index, start.getValue().getFirst());

        Node a = new Node(start.getParent(), x);
        Node b = new Node(start.getParent(), y);
        if (start.hasChildren()) {
            // If the node we are splitting wasn't a leaf node, this means that it has 4 children.
            // The two children at the left will be added to the left node...
            a.setChild(0, start.getChildren().getFirst());
            a.addChild(start.getChildren().get(1));
            
            // ..and the two at the right to the right node.
            b.setChild(0, start.getChildren().get(2));
            b.addChild(start.getChildren().getLast());
        }
        // Tell the new parent that those are his children. "Son, you need to know the truth, you're adopted." :(
        start.getParent().setChild(index, a);
        start.getParent().addChild(index + 1, b);
        
        return start.getParent();
    }

    private Node swap(Node to_swap, String[] value) {
        /*
        * Swaps a contact which is not in a leaf node with its predecessor
        */
        int index = to_swap.getValue().indexOf(value);
        Node predecessor = to_swap.getChildren().get(index);
        while (!predecessor.isLeaf()) {
            predecessor = predecessor.getChildren().getLast();
        }
        to_swap.getValue().set(index, predecessor.getValue().getLast());
        predecessor.getValue().removeLast();
        predecessor.getValue().addLast(value);
        return predecessor;
    }

    private void rotate(Node to_delete_from, Node to_rotate_from, boolean is_after) {
        Node parent = to_delete_from.getParent();
        int index = parent.getChildren().indexOf(to_delete_from);

        // The boolean is_after indicates if the node to_rotate_from is before or after to_delete_from in the list of values.
        if (is_after) {
            // Replace the contact you want to delete from the node with the contact having the same index in the parent node.
            to_delete_from.getValue().set(0, parent.getValue().get(index));
            // Then replace that contact in the parent node with the first contact in the following child node.
            parent.getValue().set(index, to_rotate_from.getValue().getFirst());
            // Remove from the child node the contact you just added to the parent node.
            to_rotate_from.getValue().removeFirst();
            if (to_rotate_from.hasChildren()) {
                // If the node you rotated from had children, add those children to the end of its left sibling's contact list.
                LinkedList<String[]> value = to_rotate_from.getChildren().removeFirst().getValue();
                value.addFirst(to_delete_from.getValue().getFirst());
                to_delete_from.setValue(value);
            }
        } else {
            // Same thing here as above, but we rotate from the left sibling instead of the right one. Used when we don't
            // have a right sibling.
            to_delete_from.getValue().set(0, parent.getValue().get(index - 1));
            parent.getValue().set(index - 1, to_rotate_from.getValue().getLast());
            to_rotate_from.getValue().removeLast();
            if (to_rotate_from.hasChildren()) {
                LinkedList<String[]> value = to_rotate_from.getChildren().removeLast().getValue();
                value.addLast(to_delete_from.getValue().getFirst());
                to_delete_from.setValue(value);
            }
        }
    }

    private void merge(Node to_delete_from, Node to_merge_from, boolean is_after) {
        Node parent = to_delete_from.getParent();
        if (parent.getValue().size() == 1) {
            // If the parent had only 1 contact, use the shrink method instead.
            shrink(to_delete_from, to_merge_from, is_after);
            return;
        }

        int index = parent.getChildren().indexOf(to_delete_from);
        if (is_after) {
            // Replace the desired contact with the contact from the parent which has the same index as the node.
            to_delete_from.getValue().removeFirst();
            to_delete_from.getValue().add(parent.getValue().remove(index));
            // Remove the right sibling and add the contact in it to this node.
            to_delete_from.getValue().add(to_merge_from.getValue().remove(index + 1));
        } else {
            to_merge_from.getValue().add(parent.getValue().remove(index - 1));
            parent.getChildren().remove(index);
        }
    }

    private void shrink(Node to_delete_from, Node to_merge_from, boolean is_after) {
        Node parent = to_delete_from.getParent();
        // Remove the children nodes and add the merging contact to the parent node.
        if (is_after) {
            parent.getValue().addLast(to_merge_from.getValue().removeLast());
            parent.getChildren().removeFirst();
            parent.getChildren().set(0, new Node(parent, null));
        } else {
            parent.getValue().addFirst(to_merge_from.getValue().removeLast());
            parent.getChildren().removeFirst();
            parent.getChildren().set(0, new Node(parent, null));
        }
    }

    public void breadthFirst() {
        // Was used for testing and debugging purposes.
        LinkedList<Node> lst = new LinkedList<>();
        lst.addLast(root);
        while (!lst.isEmpty()) {
            Node c = lst.removeFirst();
            System.out.println("Parent: " + c.getParent() + "\tChild: " + c);
            LinkedList<Node> children = c.getChildren();
            for (Node i: children) {
                if (i.getValue() != null) lst.addLast(i);
            }
            System.out.println();
        }
    }

    @Override
    public int height() {return height(root);}
    private int height(Node ptr) {
        // Calculates the height of the tree.
        if (!ptr.hasChildren()) return 0;

        int max_height = 0;
        for (int i = 0; i < ptr.getChildren().size(); i++) {
            int height = height(ptr.getChildren().get(i)) + 1;
            if (height > max_height) max_height = height;
        }

        return max_height;
    }
    
    @Override
    public void inOrder() {inOrder(root);}
    int num = 1;
    private void inOrder(Node start){
        // Interversal with Inorder
        if (start.getValue() == null) return;
        else if (!start.hasChildren()) {
            for (int i = 0; i < start.getValue().size(); i++) {
                System.out.println(num + "- " + contactToString(start.getValue().get(i), false));
                num++;
            }
            return;
        }

        for (int i = 0; i < start.getChildren().size(); i++) {
            inOrder(start.getChildren().get(i));
            if (i != start.getChildren().size()-1 || i == 0) {
                System.out.println(num + "- " + contactToString(start.getValue().get(i), false));
                num++;
            }
        }
    }

    public int depth(Node n) {
        int depth = 0;

        while (n.getParent() != null) {
            depth++;
            n = n.getParent();
        }
        return depth;
    }

    @Override
    public void drawTree() {drawTree(root, 0, "———");}
    public void drawTree(Node start, int chs, String str) {
        int depth = depth(start);
        if (depth > 2) return;

        LinkedList<Node> ch = start.getChildren();

        if (ch.size() == 2) {
            drawTree(ch.get(1), chs + 10, ".———");
            String tp = "";
            for (int j = 0; j < chs; j++) {
                tp += " ";
            }
            LinkedList<String> val = new LinkedList<>();
            for (String[] k: start.getValue()) {
                val.add(k[0] + " " + k[1]);
            }
            System.out.println(tp + str + val);
            drawTree(ch.get(0), chs + 10, "`———");
        } else if (ch.size() == 3) {
            drawTree(ch.get(2), chs + 10, ".———");
            String tp = "";
            for (int j = 0; j < chs; j++) {
                tp += " ";
            }
            LinkedList<String> val = new LinkedList<>();
            for (String[] k: start.getValue()) {
                val.add(k[0] + " " + k[1]);
            }
            System.out.println(tp + str + val);
            drawTree(ch.get(1), chs + 10, "`———");
            drawTree(ch.get(0), chs + 10, "`———");
        } else if (ch.size() == 4) {
            drawTree(ch.get(3), chs + 10, ".———");
            drawTree(ch.get(2), chs + 10, ".———");
            String tp = "";
            for (int j = 0; j < chs; j++) {
                tp += " ";
            }
            LinkedList<String> val = new LinkedList<>();
            for (String[] k: start.getValue()) {
                val.add(k[0] + " " + k[1]);
            }
            System.out.println(tp + str + val);
            drawTree(ch.get(1), chs + 10, "`———");
            drawTree(ch.get(0), chs + 10, "`———");
        }
    }
}
