package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import util.ValidateInput;

public class ValidateInputTest {

	@Test
	public void testIsNullOrEmpty() {
		String actual1 = "";
		String actual2 = null;

		assertTrue(actual1, ValidateInput.isNullOrEmpty(actual1));
		assertTrue(actual2, ValidateInput.isNullOrEmpty(actual2));

	}

	@Test
	public void userWantToCreate() {
		String type = "directed";
		String weight = "5";

		assertTrue(ValidateInput.userWantToCreate(type, weight));

	}

}
