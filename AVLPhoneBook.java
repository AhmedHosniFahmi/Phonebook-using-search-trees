
public class AVLPhoneBook extends PhoneBook{
	class Node{
		String[] value;
		int height = 1;
		Node leftChild;
		Node rightChild;
		
		public Node() {  
			value = null;
			leftChild = null;
			rightChild = null;
		}
		
		public Node(String[] value) {
			this.value = value;
			leftChild = null;
			rightChild = null;
		}
		
		public void setValue(String[] value) {this.value = value;}
		
		public boolean isLeaf() {return leftChild == null && rightChild == null;}
		
		public void setLeft(Node left) {leftChild = left; }
		public void setRight(Node right) {rightChild =right;}
		
		public Node getLeft() {return leftChild;}
	    public Node getRight() {return rightChild;}
	    
	}
	 
	Node root;
	int size = 0;
	public AVLPhoneBook() {
		root = new Node();
	}
	@Override
	public int height() {return height(root);}   // returning the height of the tree
	@Override
	public int size() {return size;}             // returning the num of contacts on the tree
	

	  private int height(Node node) {           // returning the height of a node 
	  if(node == null) {return -1;}             // if node is null retun -1
	  if(node.isLeaf()) {return 0;}             // if node is leaf return 0
	  return 1+Math.max(height(node.leftChild),height(node.rightChild));  // return the greates height from right or left subtree +
	  }                                                                   // the height of the node from them which is 1
	 
	private void updateHeight(Node node) {           // updating the height of the node 
		int leftHeight = height(node.leftChild);
		int rightHeight = height(node.rightChild);
		node.height = Math.max(leftHeight,rightHeight)+1;
	}
	private int balanceFactor(Node node) {        // getting balance factor for rebalnce purposes 
		if(node != null)
		return height(node.rightChild) - height(node.leftChild);
		else 
			return 0;
	}
	
//////////////////////Searching part/////////////////////////
	
	@Override
	public String[] search(String name) {                    //if the node exists return it and if not return null
		String[] temp = name.split(" ");
		
		Node searchedFor = searchRec(root, temp);
		
		if(searchedFor == null) return null;
		else return searchedFor.value;
	}
	
	
	private Node searchRec(Node node, String[] name) {
		if (node == null || node.value == null) {return null;}    //if you reached a null node or an empty one return null
		else {
			if (compareNames(name, node.value) == 0) {            // if we reached the desired value return it 
				return node;
			}else if (compareNames(name, node.value) == -1) {    // if the value we searching for are less than the node's value 
				return searchRec(node.getLeft(), name);          // we reached to ..then we go to it's left child
			}else if (compareNames(name, node.value) == 1) {     // if the value we searching for are greater than the node's value 
				return searchRec(node.getRight(), name);         // we reached to.. then we go to it's right child
			}else
				return null;
		}
	}
	
	
////////////////////////Inserting part ////////////////////////
	
	@Override
	public void insert(String[] contact) {                    // method to call the recursive insert method
		root = insert(root, contact);
	}
	
	
	private Node insert(Node node, String[] contact) {
		if (node == null || node.value == null) {             // if node is null or has no value ..put the contact inside it 
			node = new Node(contact); size++;
		}else if (compareNames(contact, node.value) == 0) {   // if we insert a contact witht the same name 
			int s = node.value.length;                        // call the method which is adding only new informations
			node.setValue(upgradeContact(node.value, contact));
            if (node.value.length > s) size++;                           
		} else if (compareNames(contact, node.value) == 1) {   // if the name inserted is greater than the name in the node 
			node.setRight(insert(node.getRight(), contact));   // insert it in the right child 
		} else if (compareNames(contact, node.value) == -1) {  // if the name iserted is smaller than the name in the node
			node.setLeft(insert(node.getLeft(), contact));     // insert it in the left child 
		}  
		updateHeight(node);
		return rebalance(node);
	}
	
	
//////////////////////////////////////Deletion part ///////////////////////////////
    
	@Override
	public void delete(String name) {
		String[] temp = search(name);
		if (temp != null) {
			root = delete(root, temp);
			System.out.println(name +" has been deleted");
			size--;
		}
	}
	
	
	private Node delete(Node node, String[] contact) {
		if (node == null || node.value == null) {             // case where u deleting a node which is not exist 
			return null;
		}else if (compareNames(contact, node.value) == -1) {  // if name smaller than the parent go to left sub tree
			node.setLeft(delete(node.getLeft(), contact));
		}else if (compareNames(contact, node.value) == 1) {   // if the name is greater than the parent go to right sub tree
			node.setRight(delete(node.getRight(), contact));
		}else {
			int x = 0;
			if (contact.length > 5) {node.setValue(choose(contact)); x = 1;}
			if (x != 1) {
			//one child or leaf case 
			if (node.getLeft() == null) {                // the parent have only right child case
				return node.getRight();
			}else if (node.getRight() == null) {        // the parent have only left child
			 	return node.getLeft();
			   }
			}
			if(x != 1) {
                node.setValue(findMin(node.getRight()).value);       // getting the successor and put it in the deleted node
                node.setRight(delete(node.getRight(), node.value));  // delete the successor node the same way above 
			}
		}
		updateHeight(node);
		return rebalance(node);
	}
	
	
///////////////////////////////////Balancing Part //////////////////////////
	
		
		/*        N                  L
		 *        |                  |
		 *    L        R        LL       N 
		 *    |        |        |        |
		 * LL   LR                    LR   R  
		 */                         
		private Node rotateRight(Node node) {
			Node left = node.leftChild;        // memorize the left child L
			
			node.setLeft(left.getRight());     // N left child become LR
			left.setRight(node);               // N becomes the right child of L
			
			updateHeight(node);
			updateHeight(left);
			
			return left;
		}
		
		
		/*        N                   R
		 *        |                   |
		 *    L       R           N       RR
		 *    |       |           |       |
		 *        RL     RR     L    RL
		 */
		private Node rotateLeft(Node node) {
			Node right = node.rightChild;       // memorize the right child
			
			node.setRight(right.getLeft());     // N right child become RL
			right.setLeft(node);                // N becomes the left child of R
			
			updateHeight(node);
			updateHeight(right);
			
			return right;
		}
		
		
		/*     N
		 *     |
		 * L       R
		 * 
		 * 
		 * there are 4 cases for rebalancing a node
		 * case 1 : bf(node) <-1 and bf(leftChild) <= 0   then we rotateRight(node)
		 * case 2 : bf(node) <-1 and bf(leftChild) >  0   then we rotateLeft(leftChild) then rotateRight(node)
		 * case 3 : bf(node) > 1 and bf(rightChild) >= 0   then we rotateLeft(node)
		 * case 4 : bf(node) > 1 and bf(rigthChild) <  0   then we rotateRight(rightChild) then rotateLeft(node)
		 * 
		 */
		
		private Node rebalance(Node node) {
			int balance = balanceFactor(node);
			if(balance != 0 || balance != 1 || balance != -1) {
				
			// left heavy tree
			if (balance < -1) {                                    
				if (balanceFactor(node.leftChild) > 0) {       // case 2
					// rotate left - right
					node.setLeft(rotateLeft(node.getLeft()));
					node = rotateRight(node);
				} else {                                       // case 1
					// rotate right
					node = rotateRight(node);
				}
			}
			
			// right heavy tree
			else if (balance >1) {
				if (balanceFactor(node.rightChild) < 0) {   // case 4
					//rotate right left
					node.setRight(rotateRight(node.getRight()));
					node = rotateLeft(node);
				} else {                                   // case 3
					//rotate left                   
					node = rotateLeft(node);  
				}
			    }
			}
			return node;
		}

	// a method to get the successor.....  successor : the smallest value in right subtree  {the one we used in deletion}
		public Node findMin(Node node) {  
			while(node.getLeft() != null) {
				node = node.getLeft();
			}
			return node;
		}
	//a method to get the predecessor....... predecessor :heighst value in left subtree 
		public Node findMax(Node node) {
			while(node.getRight() != null) {
				node = node.getRight();
			}
			return node;
		}
		
	
///////////////////////////////PRINTING THE TREE ///////////////////////////

	                                              // used for testing and inform about informations in the tree 
	public void treeInfromation() {               // printing the tree information for every node  
		treeInfromation(root);                    //  parent:  left Child: right Child:
	}
	
	
	private void printA(String[] arr) {    // print array in node 
		System.out.print("[");
		for (int i = 0; i < 2; i++) {
			System.out.print(arr[i]);
			if(i != 1)
				System.out.print(",");
		}
		System.out.println("]");
	}
	
	
	private void treeInfromation(Node node) {      // printing the tree information starting from a node 
		if(node == null) return;                   //  parent:  left Child: right Child:
		else {
			System.out.print("parent : " );
			printA(node.value);
			if(node.leftChild != null) {
				System.out.print("left child : ");
				printA(node.getLeft().value);
			}
			if(node.rightChild != null) {
				System.out.print("right child : ");
				printA(node.getRight().value);
			}
			treeInfromation(node.leftChild);
			treeInfromation(node.rightChild);
		}
	}
	
	@Override
	public void inOrder() {                     // print all the tree in order travers 
		inOrder(root);
	}
	int num = 1;
	private void inOrder(Node node) {          // print the tree starting from a node as a root  <left><root><right>
		if(node == null || node.value == null) return;
		inOrder(node.getLeft());
		System.out.println(num + "- " + contactToString(node.value, false));
		num++;
		inOrder(node.getRight());
	}
	
	
///////////////////// A method to print 2 levels ///////////////////////	

	private String getLeftName(Node node) { // if the node has left child return it printed .. if not return null 
		if(node.getLeft() != null)
			return node.getLeft().value[0] + " " + node.getLeft().value[1];
		else
			return null;
	}
	private String getRightName(Node node) { // if the node has right child return it printed .. if not return null 
		if(node.getRight() != null)
			return node.getRight().value[0] + " " + node.getRight().value[1];
		else
			return null;
	}
	
	
	public void print2leves(Node node) {   // print two levels under any node that we choose
		System.out.println(" \t\t\t\t\t\t\t    " + node.value[0] + " " + node.value[1] + "         ");
		System.out.println(" \t\t\t\t\t\t\t      /          \\");
		System.out.println(" \t\t\t\t\t\t\t     /            \\");
		System.out.println(" \t\t\t\t\t\t\t    /              \\");
		System.out.println(" \t\t\t\t\t\t\t   /                \\");
		System.out.println(" \t\t\t\t\t\t\t  /                  \\");

		
		System.out.println("\t\t\t\t\t"+"     "+getLeftName(node)+"\t\t"+"      "+getRightName(node));
		System.out.print("\t\t\t\t\t    /            \\");System.out.println("\t  "+"           " +"/              \\");
		System.out.print("\t\t\t\t\t   /              \\");System.out.println("\t  "+"          " +"/                \\");
		

		System.out.print("\t\t\t\t     "+getLeftName(node.getLeft())+"  "+getRightName(node.getLeft()));
		System.out.println("  "+getLeftName(node.getRight())+"  "+getRightName(node.getRight()));
	}
	
	
	public void print2levesFromRoot() {// print two levels under the root
		print2leves(root);
	}

	@Override
	public void drawTree() {// print two levels under the root
		print2levesFromRoot();
	}

}
