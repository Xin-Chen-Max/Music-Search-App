import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

/**
 * This class contains JUnit tests for the Backend class.
 * It ensures that all backend methods function correctly with Tree_Placeholder.
 */
public class BackendTests {

  /**
   * Test the readData method to check if songs are correctly inserted into the tree.
   * Since Backend does not expose its internal data, we validate by checking
   * the last added song in Tree_Placeholder.
   */
  @Test
  public void backendTest1() {
    Tree_Placeholder tree = new Tree_Placeholder();
    Backend backend = new Backend(tree);
    try {
      backend.readData("songs.csv");

      // Ensure that the tree is not empty (Tree_Placeholder always returns size > 0)
      Assertions.assertTrue(tree.size() > 0, "Tree should >0 after reading data.");

      // Check if last added song matches expectations
      Song lastAdded = tree.lastAddedSong;
      Assertions.assertNotNull(lastAdded, "Last added song can't be null.");
      Assertions.assertEquals("Kills You Slowly", lastAdded.getTitle(), "Last added song should be 'Kills You Slowly'.");
      Assertions.assertEquals("The Chainsmokers", lastAdded.getArtist(), "Artist should be 'The Chainsmokers'.");
      Assertions.assertEquals(2019, lastAdded.getYear(), "Year should be 2019.");
    } catch (IOException e) {
      Assertions.fail("IOException can't occur if file exists.");
    }
  }


  /**
   * Test getRange method to ensure it correctly filters songs by year.
   */
  @Test
  public void backendTest2() {
    Tree_Placeholder tree = new Tree_Placeholder();
    Backend backend = new Backend(tree);

    List<String> titles = backend.getRange(2015, 2017);
    Assertions.assertEquals(3, titles.size(), "There should be 3 songs within the year range 2015-2017.");
    Assertions.assertTrue(titles.contains("A L I E N S"), "Expected 'A L I E N S' in the list.");
    Assertions.assertTrue(titles.contains("BO$$"), "Expected 'BO$$' in the list.");
    Assertions.assertTrue(titles.contains("Cake By The Ocean"), "Expected 'Cake By The Ocean' in the list.");

    List<String> sameYear = backend.getRange(2016, 2016);
    Assertions.assertEquals(1, sameYear.size(), "There should be 1 song from the year 2016.");
    Assertions.assertTrue(sameYear.contains("Cake By The Ocean"), "Expected 'Cake By The Ocean' for the year 2016.");
  }

  /**
   * Test filterSongs method independently to verify that it correctly filters
   * songs based on loudness while maintaining order.
   */
  @Test
  public void backendTest3() {
    Tree_Placeholder tree = new Tree_Placeholder();
    Backend backend = new Backend(tree);

    backend.getRange(2015, 2017);

    // Apply a loudness filter that excludes all songs (threshold -7)
    List<String> filteredSongs = backend.filterSongs(-7);
    Assertions.assertTrue(filteredSongs.isEmpty(), "No songs should remain when filtering at threshold -7.");

    // Applying a loudness filter that allows all songs (threshold -4)
    List<String> allSongs = backend.filterSongs(-4);
    List<String> expected = List.of("A L I E N S", "BO$$", "Cake By The Ocean");

    // Check that the number of songs is as expected
    Assertions.assertEquals(expected.size(), allSongs.size(), "There should be " + expected.size() + " songs for threshold -4.");

    // Verify that each expected song is contained in the result
    for (String song : expected) {
      Assertions.assertTrue(allSongs.contains(song), "Expected '" + song + "' in the songs list for threshold -4.");
    }

    // Reset filter with null and perform the same checks
    List<String> resetSongs = backend.filterSongs(null);
    Assertions.assertEquals(expected.size(), resetSongs.size(), "There should be " + expected.size() + " songs when filter is reset.");
    for (String song : expected) {
      Assertions.assertTrue(resetSongs.contains(song), "Expected '" + song + "' in the songs list after resetting filter.");
    }
  }

  /**
   * Test fiveMost method to ensure it correctly returns the most danceable songs
   * in descending order and respects prior filtering.
   */
  @Test
  public void backendTest4() {
    Tree_Placeholder tree = new Tree_Placeholder();
    Backend backend = new Backend(tree);

    backend.getRange(2015, 2017);

    // Without any loudness filter, fiveMost should return top danceable songs
    List<String> mostDanceable = backend.fiveMost();
    List<String> expected3 = List.of("BO$$", "Cake By The Ocean", "A L I E N S");
    Assertions.assertEquals(expected3, mostDanceable, "fiveMost should return top danceable songs.");

    // Apply a loudness filter that should not exclude any songs
    backend.filterSongs(-4);
    List<String> filteredSame = backend.fiveMost();
    Assertions.assertEquals(expected3, filteredSame, "fiveMost should return the same songs when filtering at -5.");

    // Apply a loudness filter that excludes all songs
    backend.filterSongs(-7);
    List<String> emptyDanceable = backend.fiveMost();
    Assertions.assertTrue(emptyDanceable.isEmpty(), "fiveMost should return an empty list.");
  }
}
