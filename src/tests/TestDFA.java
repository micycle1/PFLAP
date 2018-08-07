package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TestDFA {

	SimpleDFA DFA;
	SimpleState s1, s2, s3, s4, s5;
	SimpleArrow a1, a2, a3, a4;

	@Before
	public void setUp() throws Exception {

		DFA = new SimpleDFA();

		s1 = new SimpleState(0);
		s2 = new SimpleState(1);
		s3 = new SimpleState(2);
		s3.setAccepting(true);

		s4 = new SimpleState(3);
		s4.setAccepting(true);

		a1 = new SimpleArrow(s1, s2, 'a', ' ', ' ');
		a2 = new SimpleArrow(s2, s3, 'b', ' ', ' ');
		a3 = new SimpleArrow(s1, s4, 'c', ' ', ' ');

		DFA.addTransition(a1);
		DFA.addTransition(a2);
		DFA.addTransition(a3);

		DFA.setInitialSimpleState(s1);

	}

	@Test
	public void test() {
		assertEquals(false, DFA.run("aa"));
		assertEquals(true, DFA.run("ab"));

		s3.setAccepting(false);
		assertEquals(false, DFA.run("ab"));

		assertEquals(true, DFA.run("c"));

		DFA.removeTransition(a3);
		assertEquals(false, DFA.run("c"));
	}

}
