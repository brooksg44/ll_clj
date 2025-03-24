# Ladder Logic Compiler/Decompiler

A Clojure implementation of a Ladder Logic compiler and decompiler.

## Overview

This project provides tools to compile Ladder Logic diagrams into a set of instructions and decompile those instructions back into Ladder Logic diagrams. It's useful for PLC (Programmable Logic Controller) programming and industrial automation.

## Installation

1. Ensure you have [Leiningen](https://leiningen.org/) installed.
2. Clone this repository.
3. Build the project with `lein compile`.

## Usage

### Command Line Interface

You can run the application in interactive mode:

```bash
lein run
```

This will start an interactive CLI with the following commands:
- `help` - Show help message
- `example` - Load example ladder logic
- `compile` - Compile the current ladder logic
- `decompile` - Decompile the current instructions
- `edit` - Edit/set ladder logic input
- `show` - Show the current state
- `quit` - Exit the application

### Process a File

You can also process a ladder logic file directly:

```bash
lein run path/to/your/logic.ll
```

This will compile the ladder logic in the file and then decompile it, displaying both the compiled instructions and the decompiled ladder logic.

### Example

Here's an example of ladder logic that creates a latch with an emergency stop:

```
!! this is an example of a latch with an emergency stop !!
||--[/ESTOP]----[/STOP]----+--[START]--+------(RUN)-----||\n
||                         |           |                ||\n
||                         +---[RUN]---+                ||\n
||                                                      ||\n
||--[RUN]-------------------------------------(MOTOR)---||\n
```

## Ladder Logic Syntax

- `||` marks the beginning and end of each rung
- `--` represents connections
- `[NAME]` represents a normally open contact
- `[/NAME]` represents a normally closed contact
- `(NAME)` represents an output coil
- `+` represents branch connections

## License

MIT License - see LICENSE file for details.
