/**
 * The BSTRotation class execute a rotation within a binary search tree which is a local change to
 * the shape of a tree that does not introduce any order violations. It takes two input nodes that
 * are related as parent-child, and flips which of them is the parent and which is the child.
 * <p>
 * If the child is the left child of the parent, it performs a right rotation. If the child is the
 * right child of the parent, it performs a left rotation.
 */
public class BSTRotation<T extends Comparable<T>> extends BinarySearchTree<T> {

    /**
     * A constructor sets root as null.
     */
    public BSTRotation() {
        super();
    }

    /**
     * Performs the rotation operation on the provided nodes within this tree. When the provided
     * child is a left child of the provided parent, this method will perform a right rotation. When
     * the provided child is a right child of the provided parent, this method will perform a left
     * rotation. When the provided nodes are not related in one of these ways, this method will
     * either throw a NullPointerException: when either reference is null, or otherwise will throw
     * an IllegalArgumentException.
     *
     * @param child  is the node being rotated from child to parent position
     * @param parent is the node being rotated from parent to child position
     * @throws NullPointerException     when either passed argument is null
     * @throws IllegalArgumentException when the provided child and parent nodes are not initially
     *                                  (pre-rotation) related that way
     */
    protected void rotate(BinaryTreeNode<T> child, BinaryTreeNode<T> parent)
            throws NullPointerException, IllegalArgumentException {
        if (child == null || parent == null) { // neither child nor parent can be null
            throw new NullPointerException("One of the child and the parent is null.");
        }
        // Child is left child, so executing right rotation
        if (parent.childLeft() == child) {
            // Store the right child of current child for later connection
            BinaryTreeNode<T> childsChildRight = child.childRight();
            // Find grandparent of the current child.
            BinaryTreeNode<T> grandparent = parent.parent();
            child.setParent(grandparent); // move child to parent position (right rotating)
            if (grandparent == null) { // parent was root
                this.root = child;
            } else {
                if (grandparent.childLeft() == parent) {
                    grandparent.setChildLeft(child);
                } else {
                    grandparent.setChildRight(child);
                }
            }
            child.setChildRight(parent);
            parent.setParent(child);
            parent.setChildLeft(childsChildRight); // set to null or childsChildRight
            if (childsChildRight != null) {
                childsChildRight.setParent(parent);
            }
        }
        // child is right child, so executing left rotation
        else if (parent.childRight() == child) {
            BinaryTreeNode<T> childsChildLeft = child.childLeft();
            BinaryTreeNode<T> grandparent = parent.parent();
            child.setParent(grandparent); // set parent to null, or set to grandparent if
            // grandparent exists
            if (grandparent == null) { // parent was root
                this.root = child; // take parent's place
            } else {
                if (grandparent.childLeft() == parent) {
                    grandparent.setChildLeft(child);
                } else {
                    grandparent.setChildRight(child);
                }
            }
            child.setChildLeft(parent);
            parent.setParent(child);
            parent.setChildRight(childsChildLeft); // set childRight to null or childsChildLeft
            if (childsChildLeft != null) {
                childsChildLeft.setParent(parent);
            }
        } else {
            throw new IllegalArgumentException("Inputs are not in parent-child relationship.");
        }
    }

    /**
     * TEST 1: Left rotation at the root 0 shared children around the parent-child pair
     * <p>
     * Returns true if final tree is correct, false otherwise.
     */
    public boolean test1() {
        BinaryTreeNode<T> node1 = new BinaryTreeNode<>((T) (Integer) 1);
        BinaryTreeNode<T> node2 = new BinaryTreeNode<>((T) (Integer) 2);
        node1.setChildRight(node2); // node1 is parent
        node2.setParent(node1); // node 2 is right child of node1
        this.root = node1;
        rotate(node2, node1);
        // Check final tree
        if (this.root != node2) return false; // new root is node2
        if (node2.childLeft() != node1) return false; // node1 is left child of node2
        if (node1.parent() != node2) return false; // node 2 is parent now
        if (node1.childLeft() != null || node1.childRight() != null) return false;
        return true; // pass the test
    }

    /**
     * TEST 2: Right rotation at the root 1 shared subtree around the parent-child pair
     * <p>
     * Returns true if final structure is correct, false otherwise.
     */
    public boolean test2() {
        BinaryTreeNode<T> n3 = new BinaryTreeNode<>((T) (Integer) 3);
        BinaryTreeNode<T> n2 = new BinaryTreeNode<>((T) (Integer) 2);
        BinaryTreeNode<T> n1 = new BinaryTreeNode<>((T) (Integer) 1);
        this.root = n3; // root is n3
        n3.setChildLeft(n2);
        n2.setParent(n3);
        n2.setChildLeft(n1);
        n1.setParent(n2);
        rotate(n2, n3);
        // Check final tree
        if (this.root != n2) return false; // new root is n2
        if (n2.childLeft() != n1) return false; // n1 is left child of n2
        if (n2.childRight() != n3) return false; // n3 is right child of n2
        if (n1.parent() != n2) return false;
        if (n3.parent() != n2) return false;
        // n1 and n3 should not have child
        if (n1.childLeft() != null || n1.childRight() != null) return false;
        if (n3.childLeft() != null || n3.childRight() != null) return false;
        return true;
    }


    /**
     * TEST 3: Rotation that does not include the root 2 shared subtrees around the parent-child
     * pair
     * <p>
     * Returns true if final tree is correct, false otherwise.
     */
    public boolean test3() {
        // initialize nodes based on their height from high to low
        BinaryTreeNode<T> n6 = new BinaryTreeNode<>((T) (Integer) 6);
        BinaryTreeNode<T> n4 = new BinaryTreeNode<>((T) (Integer) 4);
        BinaryTreeNode<T> n8 = new BinaryTreeNode<>((T) (Integer) 8);
        BinaryTreeNode<T> n2 = new BinaryTreeNode<>((T) (Integer) 2);
        BinaryTreeNode<T> n5 = new BinaryTreeNode<>((T) (Integer) 5);
        BinaryTreeNode<T> n1 = new BinaryTreeNode<>((T) (Integer) 1);
        BinaryTreeNode<T> n3 = new BinaryTreeNode<>((T) (Integer) 3);
        this.root = n6;
        n6.setChildLeft(n4);
        n4.setParent(n6);
        n6.setChildRight(n8);
        n8.setParent(n6);
        n4.setChildLeft(n2);
        n2.setParent(n4);
        n4.setChildRight(n5);
        n5.setParent(n4);
        n2.setChildLeft(n1);
        n1.setParent(n2);
        n2.setChildRight(n3);
        n3.setParent(n2);
        rotate(n2, n4);
        // Check final tree
        // root remains 6
        if (this.root != n6) return false;
        // 6's left child is now 2
        if (n6.childLeft() != n2) return false;
        // 2's left child is 1
        if (n2.childLeft() != n1) return false;
        // 2's right child is 4
        if (n2.childRight() != n4) return false;
        // 4's left child is 3
        if (n4.childLeft() != n3) return false;
        // 4's right child is 5
        if (n4.childRight() != n5) return false;
        // 6's right child remains 8
        if (n6.childRight() != n8) return false;
        return true;
    }

    /**
     * TEST 4: Rotation that does not include the root 3 shared subtrees around the parent-child
     * pair
     * <p>
     * Returns true if final tree is correct, false otherwise.
     */
    public boolean test4() {
        // initialize nodes based on their height from high to low
        BinaryTreeNode<T> n6 = new BinaryTreeNode<>((T) (Integer) 6);
        BinaryTreeNode<T> n4 = new BinaryTreeNode<>((T) (Integer) 4);
        BinaryTreeNode<T> n8 = new BinaryTreeNode<>((T) (Integer) 8);
        BinaryTreeNode<T> n2 = new BinaryTreeNode<>((T) (Integer) 2);
        BinaryTreeNode<T> n5 = new BinaryTreeNode<>((T) (Integer) 5);
        BinaryTreeNode<T> n1 = new BinaryTreeNode<>((T) (Integer) 1);
        this.root = n6;
        n6.setChildLeft(n4);
        n4.setParent(n6);
        n6.setChildRight(n8);
        n8.setParent(n6);
        n4.setChildLeft(n2);
        n2.setParent(n4);
        n4.setChildRight(n5);
        n5.setParent(n4);
        n2.setChildLeft(n1);
        n1.setParent(n2);
        rotate(n4, n6);
        // Check final tree
        if (this.root != n4) return false;
        // Children of n4
        if (n4.childLeft() != n2) return false;
        if (n4.childRight() != n6) return false;
        // Children of n2
        if (n2.childLeft() != n1) return false;
        if (n2.childRight() != null) return false; // n2 has no right child in this structure
        // Children of n6
        if (n6.childLeft() != n5) return false;
        if (n6.childRight() != n8) return false;
        // Check parents
        if (n2.parent() != n4) return false;
        if (n6.parent() != n4) return false;
        if (n1.parent() != n2) return false;
        if (n5.parent() != n6) return false;
        if (n8.parent() != n6) return false;
        return true;
    }

    /**
     * A main method to show results of these tests.
     */
    public static void main(String[] args) {
        BSTRotation<Integer> bst = new BSTRotation<>();
        System.out.println("test1() is " + bst.test1());
        System.out.println("test2() is " + bst.test2());
        System.out.println("test3() is " + bst.test3());
        System.out.println("test4() is " + bst.test4());
    }
}
