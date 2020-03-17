package editortrees;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to
	// the
	// "publicly visible" effects

	char element;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	Node parent;
	DisplayableNodeWrapper wrap;
	// Feel free to add other fields that you find useful
	int numberOfRotation = 0;// A field to count the number of rotations at the
								// node
	// You will probably want to add several other methods

	// For the following methods, you should fill in the details so that they
	// work correctly
	public Node() {
		this.element = '\0';
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.rank = 0;
		this.balance = Code.SAME;
		this.wrap = new DisplayableNodeWrapper(this);
	}

	public Node(char ch) {
		this.element = ch;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.rank = 0;
		this.balance = Code.SAME;
		this.wrap = new DisplayableNodeWrapper(this);
	}

	public int height() {
		if (this == EditTree.NULL_NODE) {
			return -1;
		}
		return 1 + Math.max(left.height(), right.height());
	}

	/*
	 * Use rank to implement size make it more efficient.
	 */
	public int size() {
		if (this == EditTree.NULL_NODE) {
			return 0;
		}
		return this.rank + right.size() + 1;
	}

	/**
	 * This is our second add method. We get help from Yiyu Ma and Yuan Zhou. I
	 * use NULL_NODE as the base case to avoid lots of situations of nullpointer
	 * errors. I recurse into the node according to the rank, and refresh the
	 * node's balance and rank. I do the needed rotations during refreshing. I
	 * tried to use the parent field, but I actually did not use it to add. But
	 * I think it might be helpful while doing delete method. So I keep it. I
	 * use a Wrapper class to return both the node and the boolean to see if I
	 * need to keep changing or not.
	 */
	public Wrapper add(char ch, int pos, Node parent) {
		if (this == EditTree.NULL_NODE) {
			Node add = new Node(ch);
			add.parent = parent;
			return new Wrapper(add, true);
		}
		if (pos <= this.rank) {
			this.rank++;
			Wrapper temp = this.left.add(ch, pos, this);
			this.left = temp.getNode();
			Boolean bool = temp.keepChanging;
			if (!bool) {
				return new Wrapper(this, false);
			}
			if (this.balance == Code.SAME) {
				this.balance = Code.LEFT;
				return new Wrapper(this, true);
			} else if (this.balance == Code.RIGHT) {
				this.balance = Code.SAME;
				return new Wrapper(this, false);
			} else if (this.balance == Code.LEFT) {
				if (this.left.balance == Code.LEFT) {
					Node output = this.singleRight(this);
					output.parent = parent;
					return new Wrapper(output, false);
				} else if (this.left.balance == Code.RIGHT) {
					Node output = this.doubleRight(this);
					output.parent = parent;
					return new Wrapper(output, false);
				} else {
					return new Wrapper(this, false);
				}
			}
		} else {
			Wrapper temp = this.right.add(ch, pos - this.rank - 1, this);
			this.right = temp.getNode();
			Boolean bool = temp.keepChanging;
			if (!bool) {
				return new Wrapper(this, false);
			}
			if (this.balance == Code.SAME) {
				this.balance = Code.RIGHT;
				return new Wrapper(this, true);
			} else if (this.balance == Code.LEFT) {
				this.balance = Code.SAME;
				return new Wrapper(this, false);
			} else if (this.balance == Code.RIGHT) {
				if (this.right.balance == Code.RIGHT) {
					Node output = this.singleLeft(this);
					output.parent = parent;
					return new Wrapper(output, false);
				} else if (this.right.balance == Code.LEFT) {
					Node output = this.doubleLeft(this);
					output.parent = parent;
					return new Wrapper(output, false);
				} else {
					return new Wrapper(this, false);
				}
			}
		}
		return new Wrapper(this, false);
	}

	/*
	 * A recursion method to get the total rotation count.
	 */
	public int totalRotationCount() {
		if (this == EditTree.NULL_NODE) {
			return 0;
		}
		return this.numberOfRotation + left.totalRotationCount() + right.totalRotationCount();
	}

	/*
	 * A easy way to getRank. Just used for debug. Same for many methods below.
	 */
	public int getRank() {
		return left.size();
	}

	public DisplayableNodeWrapper getDisplayableNodePart() {
		return wrap;
	}

	public boolean hasLeft() {
		if (this.left != EditTree.NULL_NODE) {
			return true;
		}
		return false;
	}

	public boolean hasRight() {
		if (this.right != EditTree.NULL_NODE) {
			return true;
		}
		return false;
	}

	public boolean hasParent() {
		if (this.parent != EditTree.NULL_NODE) {
			return true;
		}
		return false;
	}

	public Node getParent() {
		return this.parent;
	}

	public Node getLeft() {
		return this.left;
	}

	public Node getRight() {
		return this.right;
	}

	public char getElement() {
		return this.element;
	}

	public Code getBalance() {
		return this.balance;
	}

	/**
	 * The four classes below are rotation classes. We deal with the position,
	 * rank, balance code, rotation times and parent in the classes. We get the
	 * relationship by draw picture by hand and deduct.
	 */
	public Node singleLeft(Node parent) {
		Node child = parent.right;
		child.rank = child.rank + parent.rank + 1;
		parent.right = child.left;
		if (parent.right != EditTree.NULL_NODE) {
			parent.right.parent = parent;
		}
		child.left = parent;
		parent.parent = child;
		this.numberOfRotation++;
		child.balance = Code.SAME;
		parent.balance = Code.SAME;
		return child;
	}

	public Node singleRight(Node parent) {
		Node child = parent.left;
		parent.rank = parent.rank - child.rank - 1;
		parent.left = child.right;
		if (parent.left != EditTree.NULL_NODE) {
			parent.left.parent = parent;
		}
		child.right = parent;
		parent.parent = child;
		this.numberOfRotation++;
		child.balance = Code.SAME;
		parent.balance = Code.SAME;
		return child;
	}

	public Node doubleLeft(Node a) {
		Node c = a.right;
		Node b = c.left;
		if (b.balance == Code.LEFT) {
			a.balance = Code.SAME;
			c.balance = Code.RIGHT;
		} else if (b.balance == Code.RIGHT) {
			a.balance = Code.LEFT;
			c.balance = Code.SAME;
		} else {
			a.balance = Code.SAME;
			c.balance = Code.SAME;
		}
		a.right = b.left;
		a.right.parent = a;
		c.left = b.right;
		c.left.parent = c;
		b.left = a;
		a.parent = b;
		b.right = c;
		c.parent = b;
		c.rank = c.rank - b.rank - 1;
		b.rank += a.rank + 1;
		b.balance = Code.SAME;
		this.numberOfRotation += 2;
		return b;
	}

	public Node doubleRight(Node a) {
		Node c = a.left;
		Node b = c.right;
		if (b.balance == Code.LEFT) {
			c.balance = Code.SAME;
			a.balance = Code.RIGHT;
		} else if (b.balance == Code.RIGHT) {
			c.balance = Code.LEFT;
			a.balance = Code.SAME;
		} else {
			a.balance = Code.SAME;
			c.balance = Code.SAME;
		}
		a.left = b.right;
		a.left.parent = a;
		c.right = b.left;
		c.right.parent = c;
		b.right = a;
		a.parent = b;
		b.left = c;
		c.parent = b;
		a.rank = a.rank - c.rank - b.rank - 2;
		b.rank += c.rank + 1;
		b.balance = Code.SAME;
		this.numberOfRotation += 2;
		return b;
	}

	public DeleteWrapper delete(int pos) {
		if (pos < this.rank) {
			this.rank--;
			DeleteWrapper output = this.left.delete(pos);
			this.left = output.retrunNode;
			output.retrunNode.parent = this;
			if (!output.keepChanging) {
				return new DeleteWrapper(this, output.deleteNode, false);
			}
			if (this.balance == Code.LEFT) {
				this.balance = Code.SAME;
				return new DeleteWrapper(this, output.deleteNode, true);
			} else if (this.balance == Code.SAME) {
				this.balance = Code.RIGHT;
				return new DeleteWrapper(this, output.deleteNode, false);
			} else {
				Node thisParent = this.parent;
				if (this.right.balance == Code.RIGHT) {
					Node rotateNode = singleLeft(this);
					rotateNode.parent = thisParent;
					return new DeleteWrapper(rotateNode, output.deleteNode, true);
				} else if (this.right.balance == Code.LEFT) {
					Node rotateNode = doubleLeft(this);
					rotateNode.parent = thisParent;
					return new DeleteWrapper(rotateNode, output.deleteNode, true);
				} else {
					return new DeleteWrapper(this, output.deleteNode, true);
				}
			}
		} else if (pos > this.rank) {
			DeleteWrapper output = this.right.delete(pos - this.rank - 1);
			this.right = output.retrunNode;
			output.retrunNode.parent = this;
			if (!output.keepChanging) {
				return new DeleteWrapper(this, output.deleteNode, false);
			}
			if (this.balance == Code.RIGHT) {
				this.balance = Code.SAME;
				return new DeleteWrapper(this, output.deleteNode, true);
			} else if (this.balance == Code.SAME) {
				this.balance = Code.LEFT;
				return new DeleteWrapper(this, output.deleteNode, false);
			} else {
				Node thisParent = this.parent;
				if (this.left.balance == Code.LEFT) {
					Node rotateNode = singleRight(this);
					rotateNode.parent = thisParent;
					return new DeleteWrapper(rotateNode, output.deleteNode, true);
				} else if (this.left.balance == Code.RIGHT) {
					Node rotateNode = doubleRight(this);
					rotateNode.parent = thisParent;
					return new DeleteWrapper(rotateNode, output.deleteNode, true);
				} else {
					return new DeleteWrapper(this, output.deleteNode, true);
				}
			}
		} else {
			if (this.left == EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
				return new DeleteWrapper(EditTree.NULL_NODE, this, true);
			} else if (this.left == EditTree.NULL_NODE) {
				return new DeleteWrapper(this.right, this, true);
			} else if (this.right == EditTree.NULL_NODE) {
				return new DeleteWrapper(this.left, this, true);
			} else {
				DeleteWrapper temp = this.right.delete(0);
				this.right=temp.retrunNode;
				Node d = temp.deleteNode;
				d.parent = EditTree.NULL_NODE;
				d.left = this.left;
				if (d.equals(this.right)) {
					d.right = this.right.right;
				} else {
					d.right = this.right;
				}
				d.rank = this.rank;
				int l=d.left.height();
				int r=d.right.height();
				Node dParent=d.parent;
				if (l-r==1){
					d.balance=Code.LEFT;
					if (this.balance==Code.SAME){
						d.parent=dParent;
						return new DeleteWrapper(d, this, false);
					}
				}else if (r-l==1){
					d.balance=Code.RIGHT;
					if (this.balance==Code.SAME){
						d.parent=dParent;
						return new DeleteWrapper(d, this, false);
					}
				}else if (r==l){
					d.balance=Code.SAME;
				}else if (l-r==2){
					if (d.left.balance==Code.LEFT){
						d=singleRight(d);
						
					}else if (d.left.balance==Code.RIGHT){
						d=doubleRight(d);
					}
				}else if (r-l==2){
					if (d.right.balance==Code.RIGHT){
						d=singleLeft(d);
					}else if (d.right.balance==Code.LEFT){
						d=doubleLeft(d);
					}
				}
				
				d.parent=dParent;
				return new DeleteWrapper(d, this, temp.keepChanging);
			}
		}
	}
		
		
		
		
		
		
//		Node deleteNode = this.right;
//		Node refreshNode = deleteNode;
//		while (deleteNode.left != EditTree.NULL_NODE) {
//			deleteNode.rank--;
//			deleteNode = deleteNode.left;
//			if (deleteNode.left == EditTree.NULL_NODE) {
//				refreshNode = deleteNode;
//				deleteNode.parent.left = EditTree.NULL_NODE;
//			}
//		}
//		while (!refreshNode.equals(this.right)) {
//			if (refreshNode.parent.balance == Code.SAME) {
//				refreshNode.parent.balance = Code.RIGHT;
//				Node refreshParent = refreshNode.parent;
//				if (refreshNode.equals(deleteNode)) {
//					refreshParent.left = EditTree.NULL_NODE;
//				}
//				refreshNode = refreshParent;
//				break;
//			} else if (refreshNode.parent.balance == Code.LEFT) {
//				refreshNode.parent.balance = Code.SAME;
//				Node refreshParent = refreshNode.parent;
//				if (refreshNode.equals(deleteNode)) {
//					refreshParent.left = EditTree.NULL_NODE;
//				}
//				refreshNode = refreshParent;
//			} else {
//				Node refreshParent = refreshNode.parent;
//				if (refreshNode.right.balance == Code.RIGHT) {
//					refreshNode = singleLeft(refreshNode);
//				} else if (refreshNode.right.balance == Code.LEFT) {
//					refreshNode = doubleLeft(refreshNode);
//				}
//				refreshNode.parent = refreshParent;
//				refreshNode = refreshParent;
//			}
//		}
//		deleteNode.left = this.left;
//		this.left.parent = deleteNode;
//		deleteNode.rank = this.rank;
//		deleteNode.parent = EditTree.NULL_NODE;
//		if (!deleteNode.equals(this.right)) {
//			deleteNode.right = refreshNode;
//			this.right.parent = deleteNode;
//			if (this.balance == Code.SAME) {
//				return new DeleteWrapper(deleteNode, this, false);
//			} else {
//				return new DeleteWrapper(deleteNode, this, true);
//			}
//		} else {
//			deleteNode.balance = Code.LEFT;
//			if (this.balance == Code.LEFT) {
//				if (this.left.balance == Code.LEFT) {
//					deleteNode = singleRight(deleteNode);
//				} else if (this.left.balance == Code.RIGHT) {
//					deleteNode = doubleRight(deleteNode);
//				}
//				if (this.balance == Code.SAME) {
//					return new DeleteWrapper(deleteNode, this, false);
//				} else {
//					return new DeleteWrapper(deleteNode, this, true);
//				}
//			}
//			return new DeleteWrapper(deleteNode, this, false);
//		}
//	}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	// if (this.right != EditTree.NULL_NODE) {
	// if (this.left == EditTree.NULL_NODE) {
	// if (this.right.balance == Code.RIGHT) {
	// Node t = this.right;
	// if (t.right != EditTree.NULL_NODE) {
	// Node temp = this;
	// temp = singleLeft(this);
	// } else {
	// Node temp = this;
	// temp = doubleLeft(this);
	// }
	// }
	// } else {
	// Node t = this.left;
	// if (t.right != EditTree.NULL_NODE) {
	// if (this.balance == Code.LEFT) {
	// Node temp = t.left;
	// if (temp.left != EditTree.NULL_NODE) {
	//
	// t = singleRight(t);
	// } else {
	//
	// t = doubleRight(t);
	// }
	// }
	// } else {
	// if (this.balance == Code.SAME) {
	// Node temp = t.left;
	// if (temp.left != EditTree.NULL_NODE) {
	//
	// t = singleRight(t);
	// } else {
	//
	// t = doubleRight(t);
	// }
	// }
	// }
	// if (this.balance == Code.SAME) {
	//
	// t = singleLeft(t);
	// }
	// }
	// }
	// }

	/**
	 * This is an inner class to wrap a node and a boolean together. Used for
	 * return in add.
	 */
	class Wrapper {
		private Node node;
		private boolean keepChanging;

		public Wrapper(Node node, boolean heightChanged) {
			this.node = node;
			this.keepChanging = heightChanged;
		}

		public Node getNode() {
			return node;
		}
	}

	class DeleteWrapper {
		private Node retrunNode;
		private Node deleteNode;
		private boolean keepChanging;

		public DeleteWrapper(Node returnNode, Node deleteNode, boolean keepChanging) {
			this.retrunNode = returnNode;
			this.deleteNode = deleteNode;
			this.keepChanging = keepChanging;
		}

		public Node getReturnNode() {
			return this.retrunNode;
		}

		public Node getDeleteNode() {
			return this.deleteNode;
		}
	}

	public Node copy() {
		// use recursion to copy nodes.
		if (this == EditTree.NULL_NODE) {

			return EditTree.NULL_NODE;
		}
		Node temp = new Node();
		temp.left = this.left.copy();
		temp.right = this.right.copy();
		temp.element = this.element;
		temp.rank = this.rank;
		temp.balance = this.getBalance();
		return temp;
	}
}