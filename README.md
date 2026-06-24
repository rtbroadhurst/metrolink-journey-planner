# Metrolink Journey Planner

[![CI](https://github.com/rtbroadhurst/metrolink-journey-planner/actions/workflows/ci.yml/badge.svg)](https://github.com/rtbroadhurst/metrolink-journey-planner/actions/workflows/ci.yml)

A Java route planner for the Manchester Metrolink tram network. Given a start and destination, it finds either the route with the **shortest time** or the route with the **fewest line changes**, using a variant of Dijkstra's algorithm over a graph of the network. It runs as either a Swing GUI or a command line tool. This was completed originally as coursework.

![Journey planner GUI](docs/screenshot.png)

## Features

- Two routing objectives: shortest journey time, or fewest line changes
- A 2-minute penalty applied per line change, so "shortest time" better reflects the real cost of changing trams, not just track time
- Bidirectional graph loaded from CSV, so the network can be edited or swapped without touching any code
- A single routing core shared by both the Swing GUI and the scriptable CLI
- JUnit 5 test suite run on every push via GitHub Actions

## Installation

Clone the repository and compile the sources:

```bash
git clone https://github.com/rtbroadhurst/metrolink-journey-planner.git
cd metrolink-journey-planner
javac -d out $(find src -name "*.java")
```

## Usage

Run the GUI:

```bash
java -cp out app.Main
```

Run the CLI:

```bash
java -cp out app.Main cli
```

Example CLI session (Bury to Whitefield, shortest time):

```
*** Shortest Time Route ***
Bury on green line
Radcliffe on green line
Whitefield on green line
Overall Journey Time (mins) = 8.5
Number of Changes = 0
```

The planner picks the green line here, not the yellow line, even though both run Bury to Whitefield: green is the faster of the two parallel routes.

## Project Structure

- `app/Main.java` - Entry point; loads data and dispatches to the GUI or CLI
- `model/Station.java`, `model/Line.java` - Value types with equality by name
- `model/Edge.java` - A timed connection on a single line
- `model/Network.java` - The graph: adjacency list plus station and line lookups
- `parsing/Parser.java` - Reads the CSV into a `Network`
- `routing/DijkstraRouter.java` - The search itself
- `routing/Route.java` - The result and its formatting
- `cli/Cli.java` - Command line interface
- `gui/Gui.java` - Swing interface
- `data/` - CSV network definitions
- `test/` - JUnit 5 tests mirroring the package layout

## How it works

The network is modelled as a weighted graph: stations are nodes, and each station-to-station hop on a given line is a directed edge carrying a travel time. Edges are added in both directions when the data is loaded.

The harder objective is "fewest changes". A naive Dijkstra over stations cannot express it, because the cost of leaving a station depends on *how you arrived* (which line you are currently on). The router solves this with an augmented search state:

1. **State**
   - Search over `(station, line-arrived-on)` rather than over stations alone
   - Arriving at the same station on a different line is a genuinely different state, with its own best-known cost
2. **Change Penalty**
   - Leaving a state on a line different from the one you arrived on incurs the 2-minute penalty
3. **Objectives**
   - Both objectives are supported by swapping the priority-queue comparator
   - Shortest time orders states by cumulative time, breaking ties by changes
   - Fewest changes orders states by cumulative changes, breaking ties by time
4. **Correctness**
   - Every edge weight and the change penalty are non-negative, so Dijkstra's optimality guarantee holds for both objectives

## Data format

Each CSV is an ordered list of rows. A single-value row names a line; the rows beneath it, until the next line name, are that line's consecutive stops:

```
From ,To ,Time (mins)
green,,
Bury,Radcliffe,6
Radcliffe,Whitefield,2.5
```

`data/Metrolink_times_linecolour.csv` is the default network. A second file with alternative timings is included to show the planner is fully data-driven: pointing it at a different file changes the routes without any code change.

## Testing

The project includes a JUnit 5 suite run with `./run-tests.sh`, which compiles the project, fetches the JUnit console-standalone jar on first run (cached in `lib/`), and runs the tests. The same script runs in CI on every push.

The tests cover:

- value semantics of `Station` and `Line` (whitespace trimming, equality, hashing)
- graph construction (bidirectional edges, station de-duplication, lookups)
- CSV parsing, including line-marker detection, blank lines, and rejection of malformed rows
- routing behaviour: unreachable destinations, start equal to end, the change penalty, the difference between the two objectives, choice between parallel lines, and route symmetry
- `Route` accounting (change counting and total time including penalties)

The GUI is intentionally not unit-tested: it is a thin Swing layer over the routing core, which is where the logic and the tests live.

## Design Notes

I prioritised a clear, data-driven core over raw performance. The router builds fresh state objects during the search rather than mutating shared structures, which keeps the logic easy to reason about at the cost of some allocation overhead. For a network this size that tradeoff is invisible, and it made the routing code far easier to test.

A few smaller decisions are worth noting:

- Times are loaded as `double` to match the source data (some hops are fractional minutes), so equality in tests uses a tolerance accordingly.
- The CSV reader splits on commas and assumes no station name contains one, which holds for this dataset. A quoting-aware reader would be the first change needed for arbitrary data.

If I were to continue developing the project, the natural next steps would be exposing the full station-by-station route in the GUI as a styled list rather than plain text, adding waiting and interchange times per station, and supporting disruptions by marking edges as temporarily unavailable.
