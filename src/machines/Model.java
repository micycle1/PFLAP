package machines;

import com.google.common.graph.MutableNetwork;

import p5.State;
import transitionView.LogicalTransition;

public class Model {
	
	Machine m;
	
	public MutableNetwork<State, LogicalTransition> transitionGraph; // logical transitions

}
