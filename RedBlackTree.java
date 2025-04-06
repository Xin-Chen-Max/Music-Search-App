import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * This class implements a Red-Black Tree.
 *
 * @param <T> the type of data stored in the RedBlackTree
 */
public class RedBlackTree<T extends Comparable<T>> extends BSTRotation<T> {

    /**
     * A no-args constructor.
     */
    public RedBlackTree() {
        super();
    }

    /**
     * Checks if a new red node in the RedBlackTree causes a red property violation by having a red
     * parent. If this is not the case, the method terminates without making any changes to the
     * tree. If a red property violation is detected, then the method repairs this violation and any
     * additional red property violations that are generated as a result of the applied repair
     * operation.
     *
     * @param newNode a newly inserted red node, or a node turned red by previous repair
     */
    protected void ensureRedProperty(RBTNode<T> newNode) {
        // if newNode is the root or its parent is black, no violation.
        if (newNode == root || !newNode.parent().isRed) {
            return;
        }
        RBTNode<T> parent = newNode.parent();
        RBTNode<T> grandparent = parent.parent();
        if (grandparent == null) return; // Safety check

        RBTNode<T> uncle = (parent == grandparent.childLeft()) ? grandparent.childRight() : grandparent.childLeft();

        // When uncle is red, execute recoloring
        if (uncle != null && uncle.isRed) {
            parent.isRed = false;
            uncle.isRed = false;
            grandparent.isRed = true;
            // Recursive call, check if it causes any violations
            ensureRedProperty(grandparent);
        } else {
            // If the uncle is black or null, execute rotation
            if (parent == grandparent.childLeft()) {
                if (newNode == parent.childRight()) {
                    // first, perform a left rotation on parent
                    rotate(parent.childRight(), parent);
                    parent = newNode; // newNode is parent now
                }
                // Fix colors and rotate the grandparent right
                parent.isRed = false;
                grandparent.isRed = true;
                rotate(grandparent.childLeft(), grandparent);
            } else {
                // When newNode is right child of parent
                if (newNode == parent.childLeft()) {
                    // First, perform a right rotation on parent
                    rotate(parent.childLeft(), parent);
                    parent = newNode; // newNode becomes parent
                }
                // fix color and rotate the grandparent left
                parent.isRed = false;
                grandparent.isRed = true;
                rotate(grandparent.childRight(), grandparent);
            }
        }
    }

    /**
     * Insert a new value into the RedBlackTree. New nodes are always inserted as red nodes. After
     * inserting, the tree is repaired by ensureRedProperty method.
     *
     * @param data the new value being inserted
     * @throws NullPointerException if data argument is null, we do not allow null values to be
     *                              stored within a RedBlackTree
     */
    @Override
    public void insert(T data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("Null can not be inserted into the tree.");
        }
        RBTNode<T> newNode = new RBTNode<>(data);
        // if there is no root, add it to root directly
        if (root == null) {
            root = newNode;
        } else {
            insertHelper(newNode, root);
        }
        ensureRedProperty(newNode);
        // Always ensure the root is black
        ((RBTNode<T>) root).isRed = false;
    }

    /**
     * Test case for insertion causing a recoloring (from Q03)
     */
    @Test
    public void testRBT1() {
        RedBlackTree<String> tree = new RedBlackTree<>();
        tree.insert("M"); // root
        tree.insert("F");
        tree.insert("S");
        tree.insert("C");
        tree.insert("I");
        tree.insert("P");
        tree.insert("X");
        tree.insert("H");
        tree.insert("J");
        // cause a recoloring
        tree.insert("O");
        // check "O"
        assertEquals("O", tree.root.childRight().childLeft().childLeft().getData());
        assertTrue(((RBTNode<String>) tree.root).childRight().childLeft().childLeft().isRed());
        // check "O" parent
        assertEquals("P", tree.root.childRight().childLeft().getData());
        assertFalse(((RBTNode<String>) tree.root).childRight().childLeft().isRed());
        // check "O" uncle
        assertEquals("X", tree.root.childRight().childRight().getData());
        assertFalse(((RBTNode<String>) tree.root).childRight().childRight().isRed());
    }

    /**
     * Test case for parent is red, uncle is black(rightRotate and swap color)
     */
    @Test
    public void testRBT2() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.insert(7); // Root
        tree.insert(4);  // Left child
        tree.insert(10); // Right child
        tree.insert(2);
        tree.insert(1);
        // check root
        assertEquals(7, (int) tree.root.getData());
        assertFalse(((RBTNode<Integer>) tree.root).isRed()); // root is black
        //check leftChild
        assertEquals(2, (int) tree.root.childLeft().getData());
        assertFalse(((RBTNode<Integer>) tree.root).childLeft().isRed());
        // check rightChild
        assertEquals(10, (int) tree.root.childRight().getData());
        assertFalse(((RBTNode<Integer>) tree.root).childRight().isRed());
        // check newNode
        assertEquals(1, (int) tree.root.childLeft().childLeft().getData());
        assertTrue(((RBTNode<Integer>) tree.root).childLeft().childLeft().isRed());
        // check newNode's sibling
        assertEquals(4, (int) tree.root.childLeft().childRight().getData());
        assertTrue(((RBTNode<Integer>) tree.root).childLeft().childRight().isRed());
    }

    /**
     * Test case for insertion requiring a right rotation.
     */
    @Test
    public void testRBT3() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.insert(2); // Root
        tree.insert(4); // Right child
        tree.insert(1);  // Left child
        // insert 3 to cause a right Rotaion
        tree.insert(3);
        // check root
        assertEquals(2, (int) tree.root.getData());
        assertFalse(((RBTNode<Integer>) tree.root).isRed()); // root is black
        //check leftChild
        assertEquals(1, (int) tree.root.childLeft().getData());
        assertFalse(((RBTNode<Integer>) tree.root).childLeft().isRed());
        // check rightChild
        assertEquals(4, (int) tree.root.childRight().getData());
        assertFalse(((RBTNode<Integer>) tree.root).childRight().isRed());
        // check newNode
        assertEquals(3, (int) tree.root.childRight().childLeft().getData());
        assertTrue(((RBTNode<Integer>) tree.root).childRight().childLeft().isRed()); // 3 should turn to black
    }
}
