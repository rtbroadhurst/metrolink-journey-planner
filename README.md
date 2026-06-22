# metrolink-journey-planner
Java route planner for the Manchester Metrolink.

[![CI](https://github.com/rtbroadhurst/metrolink-journey-planner/actions/workflows/ci.yml/badge.svg)](https://github.com/rtbroadhurst/metrolink-journey-planner/actions/workflows/ci.yml)

![Journey planner GUI](docs/screenshot.png)

## How to run
**Compile:**
```bash
javac -d out $(find src -name "*.java")
```

**Run (GUI):**
```bash
java -cp out app.Main
```

**Run (CLI):**
```bash
java -cp out app.Main cli
```

**Tests:**
```bash
bash run-tests.sh
```
