<h1 align="center">
  <a href="https://github.com/micycle1/PFLAP">
  <img src="src/data/icon.png" alt="PFLAP"/></a><br>
PFLAP
</h1>

<p align="center"><em>Processing Formal Languages & Automata Package</em></p>

---

**PFLAP** is an interactive graphical tool for constructing and simulating finite automata (machine representations of formal languages), and is my attempt to create a [JFLAP](http://www.jflap.org/) alternative with a more intuitive control scheme and a more pleasant aesthetic using the [Processing](https://processing.org/) library as the graphics backend. I do not intend to implement anywhere near the full functionality present in JFLAP (features such as recursively enumerable languages, transforming context-free languages, etc.).

## Latest Release

:inbox_tray: [PFLAP v.1.0](https://github.com/micycle1/PFLAP/releases)

## Features
* Create and run finite automatons (DFA, DPA, Mealy and Moore machines)
* _Instantaneous_ and _stepping_ simulation modes
* Export machine to .png
* ' ' (space) substitution for λ
* Undo/Redo
* Save/Load
* Colour-customisation of all graphics

## Screenshot
<h1 align="center">
<img src="/assets/screen.PNG"/>
</h1>

## Tasks
- [x] Implement Save/Load of machine configuration.
- [ ] Visualise DPA-machine stack (in stepping mode).
- [x] Collapse multiple transitions between the same pair of states into a single transition.
- [ ] Batch input for fast-run mode.
- [x] Implement Moore and Mealy machines.
- [x] Replace Java AWT GUI elements with JavaFX.

## Libraries
- `processing` [github](https://github.com/processing/processing)
- `controlP5` [github](https://github.com/sojamo/controlp5)
- `dashedlines` [github](https://github.com/garciadelcastillo/-dashed-lines-for-processing-)
- `guava` [github](https://github.com/google/guava)
- `zoompan` [github](https://github.com/gicentre/gicentreutils)
