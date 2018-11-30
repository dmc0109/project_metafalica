package histogramA;

import java.awt.Color;
import java.awt.Font;

class Canvas{
	int x = 860, y=380;
	double[] xScale = {0.0, 1.0};
	double[] yScale = {0.0, 1.0};
	Color bgColor = Color.BLACK;
	Color color = Color.WHITE;
}

class Formats{
	double[] margins = {0.15, 0.15, 0.1, 0.1};
	boolean isBarFilled = true;
	Color barFillColor = new Color(0, 0, 255);
	boolean hasBarFrame = true;
	Color barFrameColor = new Color(255, 0, 0);
	boolean hasBorder = true;
	Color borderColor = new Color(0, 255, 0);
	boolean hasRightRuler = true;
	Color rightRulerColor = new Color(200, 200, 0);
	Color rightRulerMarkColor = new Color(200, 200, 0);
	Font rightRulerMarkFont = new Font("consolas", Font.PLAIN, 12);
	double rightRulerMarkDistance = 0.05;
	double rightRulerMarkAngle = 0;
	boolean hasLeftRuler = true;
	Color leftRulerColor = new Color(200, 200, 0);
	Color leftRulerMarkColor = new Color(200, 200, 0);
	Font leftRulerMarkFont = new Font("consolas", Font.PLAIN, 12);
	double leftRulerMarkDistance = 0.05;
	double leftRulerMarkAngle = 0;
	Color keyColor = new Color(200, 200, 0);
	Font keyFont = new Font("consolas", Font.PLAIN, 12);
	double keyFontDistence = 0.05;
	double keyFontAngle = 0;
	boolean hasHeader = true;
	Color headerColor = Color.YELLOW;
	Font headerFont = new Font("calibri", Font.PLAIN, 20);
	double[] headerPosition = {0.5, 0.2};
	boolean hasFooter = true;
	Color footerColor = new Color(0, 200, 200);
	Font footerFont = new Font("consolas", Font.BOLD, 16);
	double[] footerPosition = {0.5, 0.8};
}
class HistogramData{
	String header = "";
	String footer = "";
	double minValue = 0.0;
	String[] keys = {};
	double[] values = {};
}

public class HistogramA{
	Canvas c;
	Formats f;
	HistogramData d;
	double[] xGraphic;
	double[] yGraphic;
	int rulerGrade;
	double rulerStep;
	public HistogramA(Canvas c, Formats f, HistogramData d) {
		this.c = c;
		this.f = f;
		this.d = d;
	}
	private void setHistogramOarameters() {
		double[] a = d.values;
		double max = Double.MIN_VALUE;
		for(double e:a)
			if(e>max)
				max = e;
		double span = max - d.minValue;
		double factor = 1.0;
		if (span >= 1)
			while (span >= 10) {
				span /= 10;
				factor *= 10;
			}
		else
			while (span < 1) {
				span *= 10;
				factor /= 10;
			}
		int nSpan = (int) Math.ceil(span);
		
	}
}