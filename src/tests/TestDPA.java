package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public final class TestDPA {
	
	SimpleDPA DPA;
	SimpleState s1, s2, s3, s4, s5;
	SimpleArrow a1, a2, a3, a4;

	@Before
	public void setUp() throws Exception {
		
		DPA = new SimpleDPA();
		
		s1 = new SimpleState(0);
		s2 = new SimpleState(1);
		s3 = new SimpleState(2);
		s4 = new SimpleState(3);
		s5 = new SimpleState(4);
		
		s4.setAccepting(true);
		s5.setAccepting(true);
		s2.setAccepting(true);
		
		DPA.addNode(s1);
		DPA.addNode(s2);
		DPA.addNode(s3);
		DPA.addNode(s4);
		DPA.addNode(s5);
		
		DPA.setInitialState(s1);
		DPA.setInitialStackSymbol('0');
		
		a1 = new SimpleArrow(s1, s2,'a', '0', '1');
		a2 = new SimpleArrow(s2, s2,'a', '1', ' ');
//		a3 = new SimpleArrow(s2, s3,'a', '1', '1');
		
		DPA.addTransition(a1);
		DPA.addTransition(a2);
//		DPA.addTransition(a3);
	}

	@Test
	public void test_1() {
		/**
		 * General Tests
		 */
		assertEquals(false, DPA.run("a"));
		assertEquals(false, DPA.run("b"));
		assertEquals(false, DPA.run(""));
		assertEquals(true, DPA.run("aa"));
		assertEquals(false, DPA.run("ab"));
		assertEquals(false, DPA.run("aba"));
		assertEquals(true, DPA.run("a "));
		assertEquals(DPA.run(" "), false);

	}
	
	@Test
	public void test_2() {
		/**
		 * Test Accept by Empty Stack
		 */
//		DPA.removeTransition(a1);
//		DPA.removeTransition(a2);
//		DPA.removeTransition(a3);
//		
//		a4 = new SimpleArrow(s1, s5, 'b', '0', '2');
//		DPA.addTransition(a4);
//
//		DPA.setInitialStackSymbol('0');
//		assertEquals(DPA.run("b"), true);
//		assertEquals(DPA.run("a"), false);
	}

}