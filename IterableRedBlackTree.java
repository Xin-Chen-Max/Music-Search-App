import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class extends RedBlackTree into a tree that supports iterating over the values it stores in
 * sorted, ascending order.
 */
public class IterableRedBlackTree<T extends Comparable<T>>
        extends RedBlackTree<T> implements IterableSortedCollection<T> {
    private Comparable<T> maximum = null;
    private Comparable<T> minimum = null;

    /**
     * Allows setting the start (minimum) value of the iterator. When this method is called, every
     * iterator created after it will use the minimum set by this method until this method is called
     * again to set a new minimum value.
     *
     * @param min the minimum for iterators created for this tree, or null for no minimum
     */
    public void setIteratorMin(Comparable<T> min) {
        this.minimum = min;
    }

    /**
     * Allows setting the stop (maximum) value of the iterator. When this method is called, every
     * iterator created after it will use the maximum set by this method until this method is called
     * again to set a new maximum value.
     *
     * @param max the maximum for iterators created for this tree, or null for no maximum
     */
    public void setIteratorMax(Comparable<T> max) {
        this.maximum = max;
    }

    /**
     * Returns an iterator over the values stored in this tree. The iterator uses the start
     * (minimum) value set by a previous call to setIteratorMin, and the stop (maximum) value set by
     * a previous call to setIteratorMax. If setIteratorMin has not been called before, or if it was
     * called with a null argument, the iterator uses no minimum value and starts with the lowest
     * value that exists in the tree. If setIteratorMax has not been called before, or if it was
     * called with a null argument, the iterator uses no maximum value and finishes with the highest
     * value that exists in the tree.
     */
    public Iterator<T> iterator() {
        return new RBTIterator<>(this.root, minimum, maximum);
    }

    /**
     * Nested class for Iterator objects created for this tree and returned by the iterator method.
     * This iterator follows an in-order traversal of the tree and returns the values in sorted,
     * ascending order.
     */
    protected static class RBTIterator<R> implements Iterator<R> {

        // stores the start point (minimum) for the iterator
        Comparable<R> min = null;
        // stores the stop point (maximum) for the iterator
        Comparable<R> max = null;
        // stores the stack that keeps track of the inorder traversal
        Stack<BinaryTreeNode<R>> stack = null;

        /**
         * Constructor for a new iterator if the tree with root as its root node, and min as the
         * start (minimum) value (or null if no start value) and max as the stop (maximum) value (or
         * null if no stop value) of the new iterator.
         *
         * @param root root node of the tree to traverse
         * @param min  the minimum value that the iterator will return
         * @param max  the maximum value that the iterator will return
         */
        public RBTIterator(BinaryTreeNode<R> root, Comparable<R> min, Comparable<R> max) {
            this.min = min;
            this.max = max;
            this.stack = new Stack<>();
            buildStackHelper(root);
        }

        /**
         * Helper method for initializing and updating the stack. This method both - finds the next
         * data value stored in the tree (or subtree) that is between start(minimum) and
         * stop(maximum) point (including start and stop points themselves), and - builds up the
         * stack of ancestor nodes that contain values between start(minimum) and stop(maximum)
         * values (including start and stop values themselves) so that those nodes can be visited in
         * the future.
         *
         * @param node the root node of the subtree to process
         */
        private void buildStackHelper(BinaryTreeNode<R> node) {
            if (node == null)
                return;
            // If a min is set and the current node's value is less than min, skip its left
            // subtree, because all nodes on the left subtree can only be smaller
            if (min != null && ((Comparable) node.getData()).compareTo(min) < 0) {
                buildStackHelper(node.childRight());
            } else {
                // push this node to the stack and continue with its left subtree
                stack.push(node);
                buildStackHelper(node.childLeft());
            }
        }

        /**
         * Returns true if the iterator has another value to return, and false otherwise.
         */
        public boolean hasNext() {
            if (stack.isEmpty()) {
                return false;
            }
            // If there is a max being set, check if the next node's value exceeds it.
            if (max != null) {
                BinaryTreeNode<R> nextNode = stack.peek();
                if (((Comparable) nextNode.getData()).compareTo(max) > 0) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns the next value of the iterator.
         *
         * @throws NoSuchElementException if the iterator has no more values to return
         */
        public R next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            BinaryTreeNode<R> current = stack.pop();
            // deal with the right subtree of current node
            buildStackHelper(current.childRight());
            return current.getData();
        }
    }

    /**
     * Test a tree without duplicates (Integer) (with a specified stop point)
     */
    @Test
    public void testOnlyMax() {
        IterableRedBlackTree<Integer> tree = new IterableRedBlackTree<>();
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(2);
        tree.insert(4);
        tree.insert(6);
        tree.insert(8);
        tree.setIteratorMax(5); // max = 5
        StringBuilder result = new StringBuilder();
        Iterator<Integer> iterator = tree.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next()).append(", ");
        }
        // check the result
        assertEquals("2, 3, 4, 5, ", result.toString());
    }

    /**
     * Test a tree without duplicates (String) (with a specified start point)
     */
    @Test
    public void testOnlyMin() {
        IterableRedBlackTree<String> tree = new IterableRedBlackTree<>();
        tree.insert("E");
        tree.insert("B");
        tree.insert("A");
        tree.insert("D");
        tree.insert("C");
        tree.insert("F");
        tree.setIteratorMin("C"); // min = "C"
        StringBuilder result = new StringBuilder();
        Iterator<String> iterator = tree.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next()).append(", ");
        }
        // check the result
        assertEquals("C, D, E, F, ", result.toString());
    }

    /**
     * Test a tree containing duplicates (Integer) (with both a specified start point and a
     * specified stop point)
     *
     * Don’t skip duplicated values in the iterator if they are within the min and max range
     */
    @Test
    public void testMinAndMax() {
        IterableRedBlackTree<Integer> tree = new IterableRedBlackTree<>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        tree.insert(5);  // duplicate
        tree.insert(20);
        tree.insert(10); // duplicate
        tree.insert(8);
        tree.setIteratorMin(5); // min = 5
        tree.setIteratorMax(15); // max = 15
        StringBuilder result = new StringBuilder();
        Iterator<Integer> iterator = tree.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next()).append(", ");
        }
        // check the result
        assertEquals("5, 5, 8, 10, 10, 15, ", result.toString());
    }
}
