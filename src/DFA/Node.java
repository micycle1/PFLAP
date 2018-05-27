package DFA;

import java.util.HashMap;

public class Node {
	
	int ID;
	
	public HashMap<Character, Node> transitions;
	
	protected Node(int ID) {
		this.ID = ID;
	}
	
	void addTransition(Node n, char symbol) {
		
	}

}

//class Node():
//    def __init__(self, identifier):
//        self.name = identifier
//        self.transitions = []
//        self.accepting = False
//
//    def __str__(self):
//        return self.name + " has " + str(len(self.transitions)+1) + " transitions."
//
//    def __iter__(self):
//        self.i = 0
//        return self
//
//    def __next__(self):
//        if self.i <= len(self.transitions) - 1:
//            self.i += 1
//            return (self.transitions[self.i-1][0].name,self.transitions[self.i-1][1])
//        else:
//            raise StopIteration()
//         
//    def addTransition(self, transition):
//        if isinstance(transition, tuple) and len(transition) == 2 and isinstance(transition[0], Node):
//            if transition in self.transitions:
//                raise ValueError('Transition Repeat')
//            else:
//                self.transitions.append(transition)
//        else:
//             raise TypeError("Transition must be a tuple of size 2: (Node,Token}.")
//
//    def setAccepting(self):
//        self.accepting = True
//            
//    def start(self, initial_string):
//        self.update(initial_string)
//
//    def update(self, string_in):
//        if string_in == "":
//            if self.accepting:
//                print("Input fully accepted. Final State: " + self.name)
//            else:
//                print("Input fully consumed but did not terminate on accepting state ("+self.name+").")
//        
//        else:
//            print(self.name + " received " + string_in)
//            for (node,token) in self.transitions:
//                if token == string_in[0]:
//                    node.update(string_in[1:])
//                    return
//            print("Input rejected at State " + self.name + ". Remaining: " + string_in)