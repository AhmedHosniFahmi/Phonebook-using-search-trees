
import java.util.LinkedList;

// _____________________Min Heap_______________________ //

public class HeapPhoneBook extends PhoneBook {
    public class Node {
        String[] value;
        Node parent;
        Node left_child;
        Node right_child;

        public Node(Node parent, String[] value) {
            this.parent = parent;
            this.value = value;
            this.left_child = null;
            this.right_child = null;
        }

        public void setvalue(String[] new_value) {this.value = new_value;}
        public String[] getValue() {return value;}
        public boolean isLeaf() {return (left_child == null && right_child == null);}
        public Node getParent() {return parent;}

        public boolean hasLeftChild() {return left_child != null;}
        public void setLeftChild(String[] ch_value) {this.left_child = new Node(this, ch_value);}
        public Node getLeftChild() {return left_child;}
        public boolean isLeftChild() {return this == this.parent.left_child;}

        public boolean hasRightChild() {return right_child != null;}
        public void setRightChild(String[] ch_value) {this.right_child = new Node(this, ch_value);}
        public Node getRightChild() {return right_child;}
        public boolean isRightChild() {return this == this.parent.right_child;}
    }

    Node root = new Node(null, null);
    Node to_up_bubble = null; boolean inserting = false;
    Node to_down_bubble = null; boolean deleting = false;
    int size = 0;


    private void insertionBubbel(Node n) {
        if (n.getParent() == null) return;
        if (compareNames(n.getValue(), n.getParent().getValue()) == -1) {
            String[] temp = n.getValue();
            n.setvalue(n.getParent().getValue());
            n.getParent().setvalue(temp);
            insertionBubbel(n.getParent());
        }
    }

    private void deletionBubble(Node n) {
        if (n.hasLeftChild() && n.hasRightChild()) {
            int f = compareNames(n.getValue(), n.getLeftChild().getValue());
            int s = compareNames(n.getValue(), n.getRightChild().getValue());
            int t = compareNames(n.getLeftChild().getValue(), n.getRightChild().getValue());

            if (f == 1 || s == 1) {
                if (t == -1) {
                    String[] temp = n.getLeftChild().getValue();
                    n.getLeftChild().setvalue(n.getValue());
                    n.setvalue(temp);
                    deletionBubble(n.getLeftChild());
                } else if (t == 1) {
                    String[] temp = n.getRightChild().getValue();
                    n.getRightChild().setvalue(n.getValue());
                    n.setvalue(temp);
                    deletionBubble(n.getRightChild());
                }
            }
        } else if ( n.hasLeftChild()) {
            int f = compareNames(n.getValue(), n.getLeftChild().getValue());
            if (f == 1) {
                String[] temp = n.getLeftChild().getValue();
                n.getLeftChild().setvalue(n.getValue());
                n.setvalue(temp);
            }
        }
    }

    @Override
    public void insert(String[] element) {
        inserting = true;
        insertNode(element, root, height());
        size++;
        insertionBubbel(to_up_bubble);
    }
    private void insertNode(String[] element, Node start, int height) {
        if (root.getValue() == null) {
            root.setvalue(element);
            to_up_bubble = root;
            inserting = false;
            return;
        } else if (height() == rightHeight()) {
            while (start.hasLeftChild()) {start = start.getLeftChild();}
            start.setLeftChild(element);
            to_up_bubble = start.getLeftChild();
            inserting = false;
            return;
        }

        if (start.hasLeftChild()) insertNode(element, start.getLeftChild(), height - 1);
        else if (height != 0 && inserting) {
            start.setLeftChild(element);
            to_up_bubble = start.getLeftChild();
            inserting = false;
            return;
        }
        else return;

        if (start.hasRightChild()) insertNode(element, start.getRightChild(), height - 1);
        else if (inserting) {
            start.setRightChild(element);
            to_up_bubble = start.getRightChild();
            inserting = false;
        }
    }

    @Override
    public void delete(String name) {
        String[] new_name = name.split(" ");
        deleting = true;
        delete(new_name, root);
        if (to_down_bubble != null) deletionBubble(to_down_bubble);
    }

    private void delete(String[] name, Node start) {
        int comparison = compareNames(name, start.getValue());

        if (comparison == 0) {
            Node minimal = getMinimalKey();
            start.setvalue(minimal.getValue());
            to_down_bubble = start;
            size--;
            deleting = false;
            if (minimal.isLeftChild()) minimal.getParent().setLeftChild(null);
            else if (minimal.isRightChild()) minimal.getParent().setRightChild(null);
        }
        else if (comparison == -1) return;
        else {
            if (start.hasLeftChild() && deleting) delete(name, start.getLeftChild());
            if (start.hasRightChild() && deleting) delete(name, start.getRightChild());
        }
    }

    private Node getMinimalKey() {return to_up_bubble;}

    @Override
    public void inOrder() {in(root);}
    int num = 1;
    private void in(Node n){
        if (n.hasLeftChild()) in(n.getLeftChild());
        System.out.println(num + "- " + contactToString(n.getValue(), false));
        num++;
        if (n.hasRightChild()) in(n.getRightChild());
    }

    @Override
    public int height() {return height(root);}
    private int height(Node start) {
        int h = 0;
        while (start.hasLeftChild()) {
            start = start.getLeftChild();
            h++;
        }
        return h;
    }

    private int rightHeight() {return rightHeight(root);}
    private int rightHeight(Node start) {
        int h = 0;
        while (start.hasRightChild()) {
            start = start.getRightChild();
            h++;
        }
        return h;
    }

    @Override
    public int size() {return size;}

    @Override
    public String[] search(String name) {
        String[] new_name = name.split(" ");
        return search(new_name, root);
    }
    private String[] search(String[] name, Node start) {
        LinkedList<Node> lst = new LinkedList<>();
        lst.addLast(root);
        while (!lst.isEmpty()) {
            Node c = lst.removeFirst();
            int comparison = compareNames(name, c.getValue());
            if (comparison == 0) return c.getValue();
            else if (comparison == -1) continue;
            else {
                if (c.hasLeftChild()) lst.addLast(c.getLeftChild());
                if (c.hasRightChild()) lst.addLast(c.getRightChild());
            }
        }
        return name;
    }

    public void breadthFirst() {
        // Was used for testing and debugging purposes.
        LinkedList<Node> lst = new LinkedList<>();
        lst.addLast(root);
        while (!lst.isEmpty()) {
            Node c = lst.removeFirst();
            // System.out.println("Parent: " + c.getParent() + "\tChild: " + c);
            if (c.hasLeftChild()) lst.addLast(c.getLeftChild());
            if (c.hasRightChild()) lst.addLast(c.getRightChild());
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


    //            .--- 15
    //       --- 9
    //      |     `--- 7
    // 5 ---
    //      |     .--- 4
    //       --- 2
    //            `--- 1

    // if leaf: (6 * n) + (4 * (n - 1)) = (10 * n) - 4
    // else: (4 * n) + (6 * n) = 10 * n

    //                .——— 15     
    //           .——— 9 
    //           |    `——— 8
    //      .——— 5
    //      |    |    .——— 4
    //      |    `——— 3
    //      |         `——— 2
    //  ——— 6    
    //      |         .——— 15
    //      |    .——— 9
    //      |    |    `——— 7
    //      `——— 5
    //           |    .——— 4
    //           `——— 3
    //                `——— 2
               
    @Override
    public void drawTree() {drawTree(root, 0, false);}
    public void drawTree(Node start, int chs, boolean right) {
        int depth = depth(start);
        if (depth > 3) return;

        if (start.hasRightChild()) drawTree(start.getRightChild(), chs + 10, true);

        String tp = "";
        for (int i = 0; i < chs; i++) {
            tp += " ";
        }
        if (!(start.getParent() == null) && start.isLeftChild()) System.out.println(tp + "`——— " + start.getValue()[0] + " " + start.getValue()[1]);
        else if (!(start.getParent() == null) && start.isRightChild()) System.out.println(tp + ".——— " + start.getValue()[0] + " " + start.getValue()[1]);
        else System.out.println(tp + " ——— " + start.getValue()[0] + " " + start.getValue()[1]);

        if (start.hasLeftChild()) drawTree(start.getLeftChild(), chs + 10, false);
    }
    
}
