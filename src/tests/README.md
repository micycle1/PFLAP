# "Tests"
These tests herein do not test the functionality of actual classes in PFLAP (such as *State* and *Arrow*) - these are not conventional unit tests. Rather, they were created to test machine functionality using a simplified representation of the classes that can be instantiated without a GUI representation.

When an automata machine requires testing, the relevant code is copied from src/machines/*MachineHere* into a Simple*MachineHere* class. It is then adapted to work with *SimpleArrow* and *SimpleState* in place of *Arrow* and *State*. The tests to run the Simple*MachineHere* on the simpler class representations are described in a corresponding Test*MachineHere* class.
