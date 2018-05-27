package DFA;

import java.util.ArrayList;

public class Machine {
	
	public static ArrayList<Node> nodes = new ArrayList<>();
	public static Node start;
	
	public static void run(String sequence) {
		
	}
	
	public static void addNodes(Node... nodes) {
		//Machine.nodes.addAll(nodes);
	}
	
	public static void setStart(Node n) {
		start = n;
	}
	
}

//class DFA():
//    
//    def __str__(self):
//        return "DFA '" + self.name + "' has " + str(len(self.nodes)) + " nodes."
//
//    def __iter__(self):
//        self.i = 0
//        return self
//
//    def __next__(self):
//        if self.i < len(self.nodes)-1:
//            self.i += 1
//            return self.nodes[self.i]
//        else:
//            raise StopIteration()
//        
//    def __init__(self, name):
//        self.name = name
//        self.nodes = []
//        self.startNode = None
//
//    def addNode(self, node):
//        if isinstance(node, Node):
//            self.nodes.append(node)
//        raise TypeError("Attempted to add non-node object to machine.")
//
//    def addNodes(self, *nodes):
//        for node in nodes:
//            if isinstance(node, Node):
//                self.nodes.append(node)
//            else:
//                raise TypeError("Attempted to add non-node object to machine.")
//
//    def setStart(self, node):
//        if node in self.nodes:
//            self.startNode = node
//        else:
//            raise TypeError("Starting node must be present in machine's nodes")
//
//    def run(self, sequence):
//        if self.startNode is None:
//            raise AttributeError("Specifiy a starting node first")
//        else:
//            self.startNode.start(sequence)
//
//    def totalTransitions(self):
//        return [list(n) for n in self.nodes]
//        
//
//
//# Init Nodes
//S0 = Node("S0")
//S1 = Node("S1")
//S2 = Node("S2")
//
//# Create node transitions (node, transition token)
//S0.addTransition((S1,"1"))
//S0.addTransition((S0,"0"))
//
//S1.addTransition((S0,"1"))
//S1.addTransition((S2,"0"))
//
//S2.addTransition((S1,"0"))
//S2.addTransition((S2,"1"))
//
//# Finally
//S0.setAccepting()
//S1.setAccepting()
//S2.setAccepting()
//#S0.start(s1)
//
//machine1 = DFA("Simple Machine")
//machine1.addNodes(S0,S1,S2)
//machine1.setStart(S0)
//machine1.run(s1)