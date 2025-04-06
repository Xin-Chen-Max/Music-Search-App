//////////////// FILE HEADER (INCLUDE IN EVERY FILE) //////////////////////////
//
// Title:    P09 Leaderboard
// Course:   CS 400 Spring 2025
//
// Author:   Xin Chen
// Email:    xchen2232@wisc.edu
// Lecturer: Professor Florian
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name:    None
// Partner Email:   None
// Partner Lecturer's Name:  None
//
// VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
//   ___ Write-up states that pair programming is allowed for this assignment.
//   ___ We have both read and understand the course Pair Programming Policy.
//   ___ We have registered our team prior to the team registration deadline.
//
//////////////////////// ASSISTANCE/HELP CITATIONS ////////////////////////////

/**
 * This class represents a binary search tree is a binary tree that maintains the sorted ordering of
 * its contents. It’s order property ensures that each node’s value is greater than or equal to all
 * values stored in that node’s left subtree, and is strictly less than all values stored in its
 * right subtree.
 *
 * @param <T> the type of data contained in the SortedCollection, which must be Comparable
 */
public class BinarySearchTree<T extends Comparable<T>> implements SortedCollection<T> {

    protected BinaryTreeNode<T> root = null;

    /**
     * Inserts a new data value into the sorted collection.
     *
     * @param data the new value being inserted
     * @throws NullPointerException if data argument is null, we do not allow null values to be
     *                              stored within a SortedCollection
     */
    @Override
    public void insert(T data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("We do not allow null values to be stored within a " +
                    "SortedCollection.");
        }
        BinaryTreeNode<T> newNode = new BinaryTreeNode<>(data);
        if (root == null) { // Newnode becomes the root
            root = newNode;
        } else {
            insertHelper(newNode, root);
        }
    }

    /**
     * Performs the naive binary search tree insert algorithm to recursively insert the provided
     * newNode (which has already been initialized with a data value) into the provided
     * tree/subtree.  When the provided subtree is null, this method does nothing.
     *
     * @param newNode the node to insert
     * @param subtree the subtree to insert into
     */
    protected void insertHelper(BinaryTreeNode<T> newNode, BinaryTreeNode<T> subtree) {
        int compared = newNode.getData().compareTo(subtree.getData());
        if (compared < 0 || compared == 0) { // newNode.data <= subtree.data, go left. accept
            // duplicate values for insertion, and store those values in the left subtree of a
            // parent with an equal value
            if (subtree.childLeft() == null) { // base case 1: insert to left when left if empty
                // and newNode <= subtree
                subtree.setChildLeft(newNode);
                newNode.setParent(subtree);
            } else {
                insertHelper(newNode, subtree.childLeft()); // recursive call: go left because
                // newNode <= subtree
            }
        } else { // newNode.data > subtree.data, go right
            if (subtree.childRight() == null) { // base case 2: insert to right when right if
                // empty and newNode > subtree
                subtree.setChildRight(newNode);
                newNode.setParent(subtree);
            } else {
                insertHelper(newNode, subtree.childRight()); // recursive call: go right because
                // newNode > subtree
            }
        }
    }

    /**
     * Check whether data is stored in the tree.
     *
     * @param data the value to check for in the collection
     * @return true if the collection contains data one or more times, and false otherwise
     */
    @Override
    public boolean contains(Comparable<T> data) {
        if (data == null) { // Null is not allowed in the tree
            return false;
        }
        return containsHelper(data, root);
    }

    /**
     * helper method to recursively check the existence of data
     *
     * @param subtree the current subtree being searched
     * @param data    the data value to search for
     * @return true if the data exists, false otherwise
     */
    private boolean containsHelper(Comparable<T> data, BinaryTreeNode<T> subtree) {
        if (subtree == null) { // base case: traverse to the end, do not find target data
            return false;
        }
        int compared = data.compareTo(subtree.getData());
        if (compared == 0) { // found
            return true;
        } else if (compared < 0) { // recursive call: go left
            return containsHelper(data, subtree.childLeft());
        } else { // recursive call: go right
            return containsHelper(data, subtree.childRight());
        }
    }

    /**
     * Counts the number of values in the collection, with each duplicate value being counted
     * separately within the value returned.
     *
     * @return the number of values in the collection, including duplicates
     */
    @Override
    public int size() {
        return sizeHelper(root);
    }

    /**
     * Protected helper method to recursively count the number of nodes in the subtree.
     *
     * @param subtree the current subtree being counted
     * @return the number of nodes in the subtree
     */
    private int sizeHelper(BinaryTreeNode<T> subtree) {
        if (subtree == null) { // base case, the subtree ends
            return 0;
        }
        return 1 + sizeHelper(subtree.childLeft()) + sizeHelper(subtree.childRight());
        // recursive call: parent(1) + size of left subtree + size of right subtree
    }

    /**
     * Checks if the collection is empty.
     *
     * @return true if the collection contains 0 values, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return root== null;
    }

    /**
     * Removes all values and duplicates from the collection.
     */
    @Override
    public void clear() {
        root = null;
    }

    /**
     * Test Method 1: Inserts integer values in 2 different orders to create differently shaped
     * trees.
     *
     * @return true if the test passes, false otherwise
     */
    public boolean test1() {
        BinarySearchTree<Integer> tree1 = new BinarySearchTree<>();
        int[] insertOrder1 = {1, 2, 3, 4, 5, 6, 7};
        for (int x : insertOrder1) {
            tree1.insert(x);
        }
        String expectedOrder = "[ 1, 2, 3, 4, 5, 6, 7 ]";
        boolean compared1 = tree1.root.toInOrderString().equals(expectedOrder);
        boolean expectedSize1 = tree1.size() == 7;
        BinarySearchTree<Integer> tree2 = new BinarySearchTree<>();
        // insert random nums to the tree
        int[] insertOrder2 = {4, 2, 6, 1, 3, 5, 7};
        for (int x : insertOrder2) {
            tree2.insert(x);
        }
        boolean compared2 = tree2.root.toInOrderString().equals(expectedOrder);
        boolean expectedSize2 = tree2.size() == 7;
        // test contain method
        boolean contains4 = tree2.contains(4);
        boolean contains1 = tree2.contains(1);
        boolean contains7 = tree2.contains(7);
        return compared1 && compared2 && expectedSize1 && expectedSize2 && contains4 && contains1 && contains7;
    }

    /**
     * Test2: Inserts multiple String values in 2 different orders to create differently shaped
     * trees.
     *
     * @return true if the test passes, false otherwise
     */
    public boolean test2() {
        BinarySearchTree<String> tree1 = new BinarySearchTree<>();
        String[] insertOrder1 = {"A", "B", "C", "D", "E", "F", "G"};
        for (String val : insertOrder1) {
            tree1.insert(val);
        }
        String expectedInOrder = "[ A, B, C, D, E, F, G ]";
        boolean bst1InOrder = tree1.root.toInOrderString().equals(expectedInOrder);
        boolean bst1Size = tree1.size() == 7;
        BinarySearchTree<String> tree2 = new BinarySearchTree<>();
        // insert random String
        String[] values = {"D", "B", "F", "A", "C", "E", "G"};
        for (String val : values) {
            tree2.insert(val);
        }
        String expectedOrder = "[ A, B, C, D, E, F, G ]";
        boolean compared = tree2.root.toInOrderString().equals(expectedOrder);
        boolean expectedSize = tree2.size() == 7;

        // test contain method
        boolean containsD1 = tree1.contains("D");
        boolean containsA1 = tree1.contains("A");
        boolean containsG1 = tree1.contains("G");
        boolean containsD2 = tree2.contains("D");
        boolean containsA2 = tree2.contains("A");
        boolean containsG2 = tree2.contains("G");
        return compared && expectedSize && containsD1 && containsA1 && containsG1 && containsD2 && containsA2 && containsG2;
    }

    /**
     * Test3: Tests the contains, size, and clear methods by inserting duplicate values.
     *
     * @return true if the test passes, false otherwise
     */
    public boolean test3() {
        BinarySearchTree<Integer> intTree = new BinarySearchTree<>();
        // insert random Integers
        int[] valuesInt = {3, 1, 4, 2, 5, 3, 6, 2};
        for (int x : valuesInt) {
            intTree.insert(x);
        }
        String expectedOrderInteger = "[ 1, 2, 2, 3, 3, 4, 5, 6 ]";
        boolean comparedInt = intTree.root.toInOrderString().equals(expectedOrderInteger);
        boolean expectedSize1 = intTree.size() == 8;
        // test contain method
        boolean contains3 = intTree.contains(3);
        boolean contains2 = intTree.contains(2);
        boolean contains6 = intTree.contains(6);
        boolean contains7 = !intTree.contains(7);
        //test clear method
        intTree.clear();
        boolean isEmptyInt = intTree.isEmpty() && intTree.size() == 0;
        // duplicate test
        BinarySearchTree<String> stringTree = new BinarySearchTree<>();
        String[] expectedOrderString = {"C", "A", "E", "B", "D", "F", "B", "A"};
        for (String x : expectedOrderString) {
            stringTree.insert(x);
        }
        String expectedInOrderStr = "[ A, A, B, B, C, D, E, F ]";
        boolean comparedString = stringTree.root.toInOrderString().equals(expectedInOrderStr);
        boolean expectedSize2 = stringTree.size() == 8;
        // test contain method
        boolean containsC = stringTree.contains("C");
        boolean containsA = stringTree.contains("A");
        boolean containsF = stringTree.contains("F");
        boolean containsG = !stringTree.contains("G");
        // test clear method
        stringTree.clear();
        boolean testClear = stringTree.isEmpty() && stringTree.size() == 0;
        return comparedInt && expectedSize1 && contains3 && contains2 && contains6 && contains7
                && isEmptyInt && comparedString && expectedSize2 && containsC &&
                containsA && containsF && containsG && testClear;
    }

    /**
     * Main method to run all the tests.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        BinarySearchTree<?> bstInstance = new BinarySearchTree<>();
        // Test 1
        boolean result1 = bstInstance.test1();
        System.out.println("Test1: " + (result1 ? "Pass" : "Fail"));
        // Test 2
        boolean result2 = bstInstance.test2();
        System.out.println("Test2: " + (result2 ? "Pass" : "Fail"));
        // Test 3
        boolean result3 = bstInstance.test3();
        System.out.println("Test3: " + (result3 ? "Pass" : "Fail"));
    }
}

