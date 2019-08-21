package model;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Vertex extends Circle{
	private int index;
	private double x;
	private double y;
	private int r = 20;
	private int rPoint = 10;
	private Paint color;
	private Text label = new Text();
	private boolean intermediatePoint = false;
	
	public Vertex() {
		
	}

	public Vertex(double x, double y, int r, Paint color) {
		super();
		this.x = x;
		this.y = y;
		this.r = r;
		this.color = color;
		super.setCenterX(x);
		super.setCenterY(y);
		super.setRadius(r);
		super.setFill(color);
	}
	
	

	public Vertex(double x, double y, int r) {
		super();
		this.x = x;
		this.y = y;
		this.r = r;
		super.setCenterX(x);
		super.setCenterY(y);
		super.setRadius(r);
	}

	public Vertex(double x, double y) {//set Intermediate Point with x, y
		super();
		this.x = x;
		this.y = y;
		this.r = 10;
		this.color = Color.DARKGOLDENROD;
		super.setCenterX(x);
		super.setCenterY(y);
		super.setRadius(rPoint);
		super.setFill(color);
		this.intermediatePoint = true;
	}
	
	public boolean isIntermediatePoint() {
		return intermediatePoint;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		super.setCenterX(x);
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		super.setCenterY(y);
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
		super.setRadius(r);
	}

	public Paint getColor() {
		return color;
	}

	public void setColor(Paint color) {
		this.color = color;
		super.setFill(color);
	}

	public Text getLabel() {
		return label;
	}

	public void setLabel (double xText, double yText, Integer index, String color) {
		this.label.setX(xText);
		this.label.setY(yText);
		this.label.setText(Integer.toString(index));
		this.label.setStyle(color+ ";"+" -fx-font-weight: bold;");
	}
	
	public void setLabel (double xText, double yText, String value, String color) {
		this.label.setX(xText);
		this.label.setY(yText);
		this.label.setText(value);
		this.label.setStyle(color+ ";"+" -fx-font-weight: bold;");
	}
	
	

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLabel(Text label) {
		this.label = label;
	}

	
}
