# Metrolink Journey Planner

[![CI](https://github.com/rtbroadhurst/metrolink-journey-planner/actions/workflows/ci.yml/badge.svg)](https://github.com/rtbroadhurst/metrolink-journey-planner/actions/workflows/ci.yml)

A Java route planner for the Manchester Metrolink tram network. Given a start and
destination, it finds either the **shortest-time** route or the route with the
**fewest line changes**, using a variant of Dijkstra's algorithm over a graph built
from real network timing data. It runs as either a Swing GUI or a command-line tool.

![Journey planner GUI](docs/screenshot.png)

## Features

- Two routing objectives: shortest journey time, or fewest line changes.
- A 2-minute penalty applied per line change, so "shortest time" accounts for the
  real-world cost of changing trams, not just track time.
- Bidirectional graph loaded from CSV, so the network can be edited or swapped
  without touching any code.
- Swing GUI and a scriptable CLI sharing the same routing core.
- JUnit 5 test suite run on every push via GitHub Actions.

## How it works

The network is modelled as a weighted graph: stations are nodes, and each
station-to-station hop on a given line is a directed edge carrying a travel time.
Edges are added in both directions when the data is loaded.

The interesting part is "fewest changes". A naive Dijkstra over stations cannot
express it, because the cost of leaving a station depends on *how you arrived* (which
line you are currently on). The router solves this by searching over an augmented
state of `(station, line-arrived-on)` rather than over stations alone. Arriving at
the same station on a different line is a genuinely different state, with its own
best-known cost. Leaving a state on a line different from the one you arrived on
incurs the change penalty.

Two objectives are supported by swapping the priority-queue comparator:

- **Shortest time** orders states by cumulative time, breaking ties by changes.
- **Fewest changes** orders states by cumulative changes, breaking ties by time.

Because every edge weight and the change penalty are non-negative, Dijkstra's
optimality guarantee holds for both objectives. The implementation uses lazy
deletion (stale priority-queue entries are skipped when popped) to avoid the cost of
a decrease-key operation.

## Project structure

```
src/
  app/        Main          entry point; loads data, dispatches to GUI or CLI
  model/      Station,Line  value types (equality by name)
              Edge          a timed connection on a line
              Network       the graph: adjacency list + station/line lookups
  parsing/    Parser        reads the CSV into a Network
  routing/    DijkstraRouter the search; Route, the result and its formatting
  cli/        Cli           text interface
  gui/        Gui           Swing interface
data/         CSV network definitions
test/         JUnit 5 tests mirroring the package layout
```

## Running

Compile:

```bash
javac -d out $(find src -name "*.java")
```

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

Note that the planner picks the green line here, not the yellow line, even though
both run Bury to Whitefield: green is the faster of the two parallel routes.

## Testing

```bash
./run-tests.sh
```

The script compiles the project, fetches the JUnit console-standalone jar on first
run (cached in `lib/`), and runs the suite. The same script runs in CI on every push.

The tests cover:

- value semantics of `Station` and `Line` (whitespace trimming, equality, hashing);
- graph construction (bidirectional edges, station de-duplication, lookups);
- CSV parsing, including line-marker detection, blank lines, and rejection of
  malformed rows;
- routing behaviour: unreachable destinations, start equal to end, the change
  penalty, the difference between the two objectives, choice between parallel lines,
  and route symmetry;
- `Route` accounting (change counting and total time including penalties).

The GUI is intentionally not unit-tested: it is a thin Swing layer over the routing
core, which is where the logic and the tests live.

## Data format

Each CSV is an ordered list of rows. A single-value row names a line; the rows
beneath it, until the next line name, are that line's consecutive stops:

```
From ,To ,Time (mins)
green,,
Bury,Radcliffe,6
Radcliffe,Whitefield,2.5
```

`data/Metrolink_times_linecolour.csv` is the default network. A second file with
alternative timings is included to show the planner is fully data-driven: pointing
it at a different file changes the routes without any code change.

## Design notes and possible extensions

- Times are loaded as `double` to match the source data (some hops are fractional
  minutes). Equality in tests uses a tolerance accordingly.
- The CSV reader splits on commas and assumes no station name contains one, which
  holds for this dataset. A quoting-aware reader would be the first change needed for
  arbitrary data.
- Natural next steps: expose the full station-by-station route in the GUI as a
  styled list rather than plain text, add waiting/interchange times per station, and
  support disruptions by marking edges as temporarily unavailable.
