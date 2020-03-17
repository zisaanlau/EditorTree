package editortrees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import editortrees.Node.DeleteWrapper;
import editortrees.Node.Wrapper;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private DisplayableBinaryTree display;
	private Node root;
	public static final Node NULL_NODE = new Node();
	private int numberOfRotation = 0;

	/**
	 * MILESTONE 1 Construct an empty tree
	 */
	public EditTree() {
		this.root = NULL_NODE;
		this.display = new DisplayableBinaryTree(this, 0, 0, false);
	}

	/**
	 * MILESTONE 1 Construct a single-node tree whose element is cc
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
		this.display = new DisplayableBinaryTree(this, 0, 0, false);
	}

	/**
	 * MILESTONE 2 Make this tree be a copy of e, with all new nodes, but the
	 * same shape and contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		// make a clone of the whole tree e.
		// Basically, we make a new node each time, and copy all the infomation
		// from node in e.
		if (e.size() == 0) {
			this.root = NULL_NODE;
		}
		Node temp = e.getRoot().copy();
		this.root = temp;

	}

	// use queue to get the level order traversal of a tree.
	public ArrayList<ArrayList<Character>> levelOrder(Node root) {
		ArrayList result = new ArrayList();

		if (root == NULL_NODE) {
			return result;
		}

		Queue<Node> queue = new LinkedList<Node>();
		queue.offer(root);

		while (!queue.isEmpty()) {
			ArrayList<Character> level = new ArrayList<Character>();
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				Node head = queue.poll();

				level.add(head.element);
				if (head.element != '0') {
					if (head.left != NULL_NODE) {
						queue.offer(head.left);
					} else {
						queue.offer(new Node('0'));
					}
					if (head.right != NULL_NODE) {
						queue.offer(head.right);
					} else {
						queue.offer(new Node('0'));
					}
				}
			}
			result.add(level);
		}

		return result;
	}

	/**
	 * MILESTONE 3 Create an EditTree whose toString is s. This can be done in
	 * O(N) time, where N is the length of the tree (repeatedly calling insert()
	 * would be O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {

	}

	/**
	 * MILESTONE 1 returns the total number of rotations done in this tree since
	 * it was created. A double rotation counts as two.
	 *
	 * @return number of rotations since tree was created.
	 */
	public int totalRotationCount() {
		return root.totalRotationCount() + this.numberOfRotation;
	}

	/**
	 * MILESTONE 1 return the string produced by an inorder traversal of this
	 * tree
	 */
	@Override
	public String toString() {
		String result = "";
		if (this.root == NULL_NODE) {
			return result;
		}
		ArrayList<Character> a = toInorderList(this.root);
		for (Character ch : a) {
			result += ch;
		}
		return result;
	}

	private ArrayList<Character> toInorderList(Node node) {
		Stack<Node> stack = new Stack<Node>();
		ArrayList<Character> result = new ArrayList<Character>();
		Node temp = root;
		while (temp != NULL_NODE || !stack.empty()) {
			while (temp != NULL_NODE) {
				stack.add(temp);
				temp = temp.left;
			}
			temp = stack.peek();
			stack.pop();
			result.add(temp.element);
			temp = temp.right;
		}
		return result;
	}

	private ArrayList<Node> toInorderListNode(Node node) {
		Stack<Node> stack = new Stack<Node>();
		ArrayList<Node> result = new ArrayList<Node>();
		Node temp = root;
		while (temp != NULL_NODE || !stack.empty()) {
			while (temp != NULL_NODE) {
				stack.add(temp);
				temp = temp.left;
			}
			temp = stack.peek();
			stack.pop();
			result.add(temp);
			temp = temp.right;
		}
		return result;
	}

	/**
	 * MILESTONE 1 This one asks for more info from each node. You can write it
	 * like the arraylist-based toString() method from the BST assignment.
	 * However, the output isn't just the elements, but the elements, ranks, and
	 * balance codes. Former CSSE230 students recommended that this method,
	 * while making it harder to pass tests initially, saves them time later
	 * since it catches weird errors that occur when you don't update ranks and
	 * balance codes correctly. For the tree with node b and children a and c,
	 * it should return the string: [b1=, a0=, c0=] There are many more examples
	 * in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		String result = "";
		if (this.root == NULL_NODE) {
			return "[" + result + "]";
		}
		ArrayList<Node> a = toPreorderList(this.root);
		for (Node ch : a) {
			result += ch.element;
			result += ch.rank + ch.balance.toString() + "," + " ";
		}
		return "[" + result.substring(0, result.length() - 2) + "]";
	}

	private ArrayList<Node> toPreorderList(Node root) {
		ArrayList<Node> returnList = new ArrayList<Node>();
		if (root == NULL_NODE) {
			return returnList;
		}
		Stack<Node> stack = new Stack<Node>();
		stack.push(root);
		while (!stack.empty()) {
			Node n = stack.pop();
			returnList.add(n);
			if (n.right != NULL_NODE) {
				stack.push(n.right);
			}
			if (n.left != NULL_NODE) {
				stack.push(n.left);
			}
		}
		return returnList;
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch
	 *            character to add to the end of this tree.
	 */
	public void add(char ch) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure that you get this one correct.
		add(ch, this.size());
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             id pos is negative or too large for this tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos > this.size()) {
			throw new IndexOutOfBoundsException();
		}
		Wrapper rootWrapper = this.root.add(ch, pos, this.root);
		this.root = rootWrapper.getNode();

	}

	/**
	 * MILESTONE 1
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos >= this.size() || pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		return this.toString().charAt(pos);
	}

	/**
	 * MILESTONE 1
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		if (this.root == NULL_NODE) {
			return -1;
		}
		return this.root.height();
	}

	/**
	 * MILESTONE 2
	 * 
	 * @return the number of nodes in this tree
	 */
	public int size() {
		if (this.root == NULL_NODE) {
			return 0;
		}
		return this.root.size(); // replace by a real calculation.
	}

	/**
	 * MILESTONE 2
	 * 
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		if (pos < 0 || pos > this.size() - 1) {
			throw new IndexOutOfBoundsException();
		}
		DeleteWrapper output = this.root.delete(pos);
		this.root = output.getReturnNode();
		this.numberOfRotation += output.getDeleteNode().numberOfRotation;
		return output.getDeleteNode().element; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3, EASY This method operates in O(length*log N), where N is the
	 * size of this tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		return "";
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE PAPER REFERENCED IN SPEC FOR ALGORITHM! Append
	 * (in time proportional to the log of the size of the larger tree) the
	 * contents of the other tree to this one. Other should be made empty after
	 * this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {

	}

	/**
	 * MILESTONE 3: DIFFICULT This operation must be done in time proportional
	 * to the height of this tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		return null; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE This method is
	 * provided for you, and should not need to be changed. If split() and
	 * concatenate() are O(log N) operations as required, delete should also be
	 * O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length) throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete" : "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3 Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return -2;
	}

	/**
	 * MILESTONE 3
	 * 
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		return -2;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

	public int slowSize() {
		if (this.root == NULL_NODE) {
			return 0;
		}
		return this.root.size();
	}

	public int slowHeight() {
		if (this.root == NULL_NODE) {
			return -1;
		}
		return this.root.height();
	}

	public void show() {
		if (this.display == null) {
			this.display = new DisplayableBinaryTree(this, 960, 1080, true);
		} else {
			this.display.show(true);
		}
	}

	public void close() {
		if (this.display != null) {
			this.display.close();
		}
	}

}
