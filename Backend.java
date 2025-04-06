import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Backend class - CS400 Project 1: iSongly
 * This class implements BackendInterface and manages a collection of songs.
 * It allows reading song data from a CSV file, filtering songs by year and loudness,
 * and retrieving the five most danceable songs.
 */
public class Backend implements BackendInterface {
  private IterableSortedCollection<Song> tree;
  private Integer loudnessThreshold;
  private Integer lowYear;
  private Integer maxYear;

  /**
   * Constructor for Backend class
   * Initializes the backend with a given tree data structure.
   *
   * @param tree an instance of IterableSortedCollection<Song>
   */
  public Backend(IterableSortedCollection<Song> tree) {
    this.tree = tree;
    this.loudnessThreshold = null;
    this.lowYear = null;
    this.maxYear = null;
  }

  Comparator<Song> yearComparator = new Comparator<Song>() {
    @Override
    public int compare(Song song1, Song song2) {
      return Integer.compare(song1.getYear(), song2.getYear());
    }
  };

  Comparator<Song> danceComparator = new Comparator<Song>() {
    @Override
    public int compare(Song song1, Song song2) {
      return Integer.compare(song2.getDanceability(), song1.getDanceability());
    }
  };

  /**
   * Loads data from the .csv file referenced by filename.  You can rely
   * on the exact headers found in the provided songs.csv, but you should
   * not rely on them always being presented in this order or on there
   * not being additional columns describing other song qualities.
   * After reading songs from the file, the songs are inserted into
   * the tree passed to this backend' constructor.  Don't forget to
   * create a Comparator to pass to the constructor for each Song object that
   * you create.  This will be used to store these songs in order within your
   * tree, and to retrieve them by year range in the getRange method.
   * @param filename is the name of the csv file to load data from
   * @throws IOException when there is trouble finding/reading file
   */
  @Override
  public void readData(String filename) throws IOException {
    File file = new File(filename);

    // Check if the file exists before attempting to read it.
    if (!file.exists()) {
      throw new IOException("File " + filename + " does not exist.");
    }
    Scanner scanner = null;

    try {
      scanner = new Scanner(file);
      if (!scanner.hasNextLine()) {
        throw new IOException("CSV file is empty");
      }

      // Read header row and find column indices manually
      String[] headers = scanner.nextLine().split(",");
      int titleI = -1, artistI = -1, genreI = -1, yearI = -1, bpmI = -1;
      int energyI = -1, danceabilityI = -1, loudnessI = -1, livenessI = -1;

      for (int i = 0; i < headers.length; i++) {
        String header = headers[i].trim().toLowerCase();
        if (header.equals("title"))
          titleI = i;
        else if (header.equals("artist"))
          artistI = i;
        else if (header.equals("top genre"))
          genreI = i;
        else if (header.equals("year"))
          yearI = i;
        else if (header.equals("bpm"))
          bpmI = i;
        else if (header.equals("nrgy"))
          energyI = i;
        else if (header.equals("dnce"))
          danceabilityI = i;
        else if (header.equals("db"))
          loudnessI = i;
        else if (header.equals("live"))
          livenessI = i;
      }

      // Check if all required columns can be found
      if (titleI == -1 || artistI == -1 || genreI == -1 || yearI == -1 || bpmI == -1 || energyI == -1 || danceabilityI == -1 || loudnessI == -1 || livenessI == -1) {
        throw new IOException("There are missing columns in CSV file.");
      }

      // Read song data
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] songData = readDataHelper(line);

        try {
          String title = songData[titleI].trim();
          String artist = songData[artistI].trim();
          String genre = songData[genreI].trim();
          int year = Integer.parseInt(songData[yearI].trim());
          int bpm = Integer.parseInt(songData[bpmI].trim());
          int energy = Integer.parseInt(songData[energyI].trim());
          int danceability = Integer.parseInt(songData[danceabilityI].trim());
          int loudness = Integer.parseInt(songData[loudnessI].trim());
          int liveness = Integer.parseInt(songData[livenessI].trim());

          // Create Song object with year-based sorting and insert it into tree
          Song newSong =
              new Song(title, artist, genre, year, bpm, energy, danceability, loudness, liveness,
                  yearComparator);
          tree.insert(newSong);

        } catch (Exception e) {
          throw new IOException("Incorrect format in CSV data: " + line, e);
        }
      }
    } finally {
      if (scanner != null) {
        scanner.close();
      }
    }
  }

    /**
     * Helper method to correctly split CSV lines, handling commas inside quotes.
     * @param line the CSV line to parse
     * @return an array of parsed values
     */
    private String[] readDataHelper (String line){
      List<String> values = new ArrayList<>();
      boolean quotes = false; // If we're inside a quoted field.
      StringBuilder current = new StringBuilder();

      for (char ch : line.toCharArray()) {
        if (ch == '\"') {
        // This helps to identify text inside quotes
        // where commas should not split the field.
          quotes = !quotes;
        } else if (ch == ',' && !quotes) {
          // If a comma is encountered and we are not inside quotes,
          // add the field to the list.
          values.add(current.toString().trim());
          current.setLength(0); // Reset.
        } else {
          // Append the character to the current field.
          current.append(ch);
        }
      }
      // Add the last field if there still has remaining characters.
      if (current.length() > 0) {
        values.add(current.toString().trim());
      }
      return values.toArray(new String[0]);
    }

  /**
   * Retrieves a list of song titles from the tree passed to the contructor.
   * The songs should be ordered by the songs' year, and fall within
   * the specified range of year values.  This year range will
   * also be used by future calls to filterSongs and getFiveMost.
   *
   * If a loudness filter has been set using the filterSongs method
   * below, then only songs that pass that filter should be included in the
   * list of titles returned by this method.
   *
   * When null is passed as either the low or high argument to this method,
   * that end of the range is understood to be unbounded.  For example, a
   * argument for the hight parameter means that there is no maximum
   * year to include in the returned list.
   *
   * @param low is the minimum year of songs in the returned list
   * @param high is the maximum year of songs in the returned list
   * @return List of titles for all songs from low to high that pass any
   *     set filter, or an empty list when no such songs can be found
   */
  @Override
  public List<String> getRange(Integer low, Integer high) {
    List<Song> filteredSongs = new ArrayList<>();
    this.lowYear = low;
    this.maxYear = high;

    // Iterate through all songs in the tree
    for (Song song : tree) {
      int songYear = song.getYear();

      if ((low == null || songYear >= low) && (high == null || songYear <= high)) {
        if (loudnessThreshold == null || song.getLoudness() < loudnessThreshold) {
          filteredSongs.add(song);
        }
      }
    }
    // Sort filtered songs by year
    filteredSongs.sort(yearComparator);

    List<String> yearList = new ArrayList<>();
    for (Song song : filteredSongs) {
      yearList.add(song.getTitle());
    }
    return yearList;
  }

  /**
   * Retrieves a list of song titles that have a loudness that is
   * smaller than the specified threshold.  Similar to the getRange
   * method: this list of song titles should be ordered by the songs'
   * year, and should only include songs that fall within the specified
   * range of year values that was established by the most recent call
   * to getRange.  If getRange has not previously been called, then no low
   * or high year bound should be used.  The filter set by this method
   * will be used by future calls to the getRange and fiveMost methods.
   *
   * When null is passed as the threshold to this method, then no
   * loudness threshold should be used.  This clears the filter.
   *
   * @param threshold filters returned song titles to only include songs that
   *     have a loudness that is smaller than this threshold.
   * @return List of titles for songs that meet this filter requirement and
   *     are within any previously set year range, or an empty list
   *     when no such songs can be found
   */
  @Override
  public List<String> filterSongs(Integer threshold) {
    this.loudnessThreshold = threshold;
    return getRange(lowYear, maxYear);
  }

  /**
   * This method returns a list of song titles representing the five
   * most danceable songs that both fall within any attribute range specified
   * by the most recent call to getRange, and conform to any filter set by
   * the most recent call to filteredSongs.  The order of the song titles
   * in this returned list is up to you.
   *
   * If fewer than five such songs exist, return all of them.  And return an
   * empty list when there are no such songs.
   *
   * @return List of five most danceable song titles
   */
  @Override
  public List<String> fiveMost() {
    List<String> titles = new ArrayList<>();
    List<Song> filteredByDanceability = new ArrayList<>();

      for (Song song : tree) {
        if ((lowYear == null || song.getYear() >= lowYear) && (maxYear == null || song.getYear() <= maxYear) &&
            (loudnessThreshold == null || song.getLoudness() < loudnessThreshold)) {
          filteredByDanceability.add(song);
      }
    }

    // Sort by danceability using danceComparator (descending order)
    filteredByDanceability.sort(danceComparator);

    // Return the top 5
    for (int i = 0; i < Math.min(5, filteredByDanceability.size()); i++) {
      titles.add(filteredByDanceability.get(i).getTitle());
    }
    return titles;
  }
}
