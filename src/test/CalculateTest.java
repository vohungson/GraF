package test;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import util.Calculate;

public class CalculateTest {

	@Test
	public void testHeightOfTriangle() {

		assertTrue(Calculate.heightOfTriangle(1, 2, 3, 4, 5, 6) > -1);

	}

	@Test
	public void testGetPoint() {
		Point2D point = Calculate.getPoint(1, 2, 3, 4, 10);
		double x3 = (1 + 3) / 2;
		double y3 = (2 + 4) / 2;
		double d = Math.sqrt((x3 - 3) * (x3 - 3) + (y3 - 4) * (y3 - 4));
		assertTrue(point != null && d < 10);

	}

}
