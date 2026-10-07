# Music Search App

A Java command-line application for exploring a song catalog. It loads song metadata from CSV, filters songs by release year and loudness, and returns the five most danceable songs that match the active filters.

The source identifies the project as **CS400 Project 1: iSongly**. Its implementation demonstrates tree data structures, interface-based frontend/backend integration, CSV parsing, and automated testing.

## Features

- **CSV ingestion:** discovers required columns by header name and parses quoted fields containing commas.
- **Year filtering:** supports an upper year bound or an inclusive year range.
- **Loudness filtering:** returns songs with loudness strictly below the specified threshold.
- **Danceability ranking:** orders matching songs by descending danceability and returns up to five results.
- **Interactive commands:** loads data, updates filters, displays results, and reports invalid input.
- **JUnit tests:** covers backend behavior, frontend commands, tree balancing, and bounded iteration. Some backend and frontend tests use placeholder components; integration tests use the real backend and tree.

## How the application is organized

```text
App
  -> Frontend (commands and console output)
      -> BackendInterface
          -> Backend (CSV loading, filtering, ranking)
              -> IterableRedBlackTree<Song>
                  -> RedBlackTree -> BSTRotation -> BinarySearchTree
```

| Files | Purpose |
| --- | --- |
| `App.java` | Connects the real frontend, backend, scanner, and iterable red-black tree |
| `Frontend.java`, `FrontendInterface.java` | Command parsing and user interaction |
| `Backend.java`, `BackendInterface.java` | Data loading, filters, and ranking |
| `Song.java` | Song attributes and comparison behavior |
| Tree classes and interfaces | Storage, rotations, balancing, and iteration |
| `BackendTests.java`, `FrontendTests.java` | Backend, frontend, and integration tests |
| Placeholder classes and `TextUITester.java` | Isolated testing helpers |

## Run locally

### Prerequisites

- JDK 17 or a compatible JDK.
- A JUnit Platform Console Standalone JAR that includes JUnit Jupiter, saved in the repository root as `junit-platform-console-standalone.jar`. It is needed during compilation because two tree implementation files also contain JUnit tests. Obtain it from the [official JUnit instructions](https://docs.junit.org/current/running-tests/console-launcher.html).

Clone this repository, enter its directory, and place the JAR beside the Java files.

**Windows PowerShell**

```powershell
$sourceFiles = Get-ChildItem -Filter *.java | ForEach-Object FullName
javac -cp junit-platform-console-standalone.jar $sourceFiles
java -cp ".;junit-platform-console-standalone.jar" App
```

**macOS or Linux**

```bash
javac -cp junit-platform-console-standalone.jar *.java
java -cp ".:junit-platform-console-standalone.jar" App
```

### Example session

Enter these commands at the application prompt:

```text
load sample-songs.csv
year 2015 to 2017
loudness -6
show most danceable
quit
```

The supplied synthetic data should produce `Night, Drive` followed by `Harbor Lights` for this combination of filters. This is an expected result derived from the sample data and implementation, not a recorded execution transcript.

Supported commands:

```text
load FILEPATH
year MAX
year MIN to MAX
loudness MAX
show MAX_COUNT
show most danceable
help
quit
```

Currently, `show MAX_COUNT` selects from the result of `fiveMost()`, so it displays at most five songs even if a larger count is requested.

## CSV format

Required headers are `title`, `artist`, `top genre`, `year`, `bpm`, `nrgy`, `dnce`, `dB`, and `live`. Column order may vary; header matching is case-insensitive. Numeric columns must contain integers.

`sample-songs.csv` contains six fictional records with synthetic attributes, including a quoted title containing a comma. It is intended for a small demonstration, not performance benchmarking or a substitute for the original course dataset.

## Tests

After compiling, run the tree tests with a Console Launcher that supports the `execute` subcommand:

```text
java -jar junit-platform-console-standalone.jar execute --class-path . --select-class RedBlackTree --select-class IterableRedBlackTree
```

The backend tests reference `songs.csv`, while frontend integration tests reference `SongsTest.csv`. Those original fixtures must be available in the working directory to run the corresponding tests with their existing assertions. The synthetic sample does not reproduce those fixtures.

This documentation does not claim a test pass rate, benchmark, or measured runtime. The commands require a compatible local JDK and JUnit installation.
