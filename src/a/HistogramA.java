package a;

import java.awt.Color;
import java.awt.Font;

class Canvas {
	int x = 512, y = 512;
	double[] xScale = { 0, 1.0 };
	double[] yScale = { 0, 1.0 };
	Color bgColor = Color.WHITE;
}

class Formats {
	double[] margins = { 0.15, 0.15, 0.1, 0.05 };
	boolean isBarFilled = true;
	Color barFillColor = Color.BLACK;
	boolean hasBarFrame = true;
	Color barFrameColor = Color.BLACK;
	boolean hasBorder = true;
	Color borderColor = Color.BLACK;
	Color leftRulerColor = Color.BLACK;
	Font leftRulerMarkFont = new Font("consolas", Font.PLAIN, 12);
	Color leftRulerMarkColor = Color.BLACK;
	double leftRulerMarkDistence = 0.02;
	double leftRulerMarkAngle = 0;
	boolean hasRightRuler = false;
	Color rightRulerColor = Color.BLACK;
	Font rightRulerMarkFont = new Font("consolas", Font.PLAIN, 12);
	Color rightRulerMarkColor = Color.BLACK;
	double rightRulerMarkDistence = 0.02;
	double rightRulerMarkAngle = 0;
	Font keyFont = new Font("consolas", Font.PLAIN, 12);
	Color keyColor = Color.BLACK;
	double keyDistence = 0.035;
	double keyAngle = 0;
	boolean hasHeader = true;
	Font headerFont = new Font("calibri", Font.PLAIN, 20);
	Color headerColor = Color.BLACK;
	double[] headerPosition = null;
	double headerAngle = 0;
	boolean hasFooter = true;
	Font footerFont = new Font("consolas", Font.BOLD, 16);
	Color footerColor = Color.BLACK;
	double[] footerPosition = null;
	double footerAngle = 0;
}

class HistogramData {
	String header = "";
	String footer = "";
	double minValue = 0.0;
	String[] keys = {};
	double[] values = {};
}

public class HistogramA {
	Canvas c;
	Formats f;
	HistogramData d;
	double[] xScale;
	double[] yScale;
//	double[] yvalue;
	double[] value;
	double[] border;
	int rulerGrade;
	double rulerStep;

	public HistogramA(Canvas c, Formats f, HistogramData d) {
		this.c = c;
		this.f = f;
		this.d = d;
		value = new double[2];
		xScale = new double[2];
		yScale = new double[2];
		border = new double[4];
		setHistogramParameters();
	}

	private void setHistogramParameters() {
		double[] a = d.values;
		double min = d.minValue;
		double max = Double.MIN_VALUE;
		for (double e : a)
			if (e > max)
				max = e;
		int level = (int) Math.floor(Math.log10(max - min));
		int nmin = (int) Math.floor(min / Math.pow(10, level));
		int nmax = (int) Math.ceil(max / Math.pow(10, level));
		double step = 1;
		int ndis = (nmax - nmin) * 10;
		if (ndis % 10 == 0 && ndis / 10 >= 4)
			step = 10;
		else if (ndis % 5 == 0 && ndis / 5 >= 4)
			step = 5;
		else if (ndis * 2 % 5 == 0 && ndis * 2 / 5 >= 4)
			step = 2.5;
		else if (ndis % 4 == 0 && ndis / 4 >= 4)
			step = 4;
		else if (ndis % 3 == 0 && ndis / 3 >= 4)
			step = 3;
		else if (ndis % 2 == 0 && ndis / 2 >= 4)
			step = 2;
		value[MIN] = nmin * Math.pow(10, level);
		while (value[MIN] + step * Math.pow(10, level - 1) <= min)
			value[MIN] += step * Math.pow(10, level - 1);
		value[MAX] = nmax * Math.pow(10, level);
		while (value[MAX] - step * Math.pow(10, level - 1) >= max)
			value[MAX] -= step * Math.pow(10, level - 1);
		step *= Math.pow(10, level - 1);
		rulerStep = step;
		rulerGrade = (int) Math.round(((value[MAX] - value[MIN]) / step));
		/*
		 * yvalue[MIN] = d.minvalue; double max = Double.MIN_value; for (double e : a)
		 * if (e > max) max = e; double span = max - d.minvalue; double factor = 1.0; if
		 * (span >= 10) while (span >= 10) { span /= 10; factor *= 10; } else while
		 * (span < 1) { span *= 10; factor /= 10; } int nSpan = (int) Math.ceil(span);
		 * yvalue[MAX] = yvalue[MIN] + factor * nSpan; switch (nSpan) { case 1:
		 * rulerGrade = 5; rulerStep = factor / 5; break; case 2: case 3: rulerGrade =
		 * nSpan * 2; rulerStep = factor / 2; break; default: rulerGrade = nSpan;
		 * rulerStep = factor; break; }
		 */
	}

	public void draw() {
		setCanvas();
		plotBars();
		plotLeftRuler();
		plotKeys();
		if (f.hasBorder)
			plotBorder();
		if (f.hasRightRuler)
			plotRightRuler();
		if (f.hasHeader)
			plotHeader();
		if (f.hasFooter)
			plotFooter();
	}

	private void setCanvas() {
		StdDraw.setCanvasSize(c.x, c.y);
		StdDraw.setXscale(c.xScale[MIN], c.xScale[MAX]);
		StdDraw.setYscale(c.yScale[MIN], c.yScale[MAX]);
		setBorder();
		StdDraw.clear(c.bgColor);
		// StdDraw.setPenColor(c.color);
	}

	private void setBorder() {
		border[NORTH] = (c.yScale[MAX] - c.yScale[MIN]) * (1 - f.margins[NORTH]);
		border[SOUTH] = (c.yScale[MAX] - c.yScale[MIN]) * f.margins[SOUTH];
		border[EAST] = (c.xScale[MAX] - c.xScale[MIN]) * (1 - f.margins[EAST]);
		border[WEST] = (c.xScale[MAX] - c.xScale[MIN]) * f.margins[WEST];
		if (f.headerPosition == null) {
			f.headerPosition = new double[2];
			f.headerPosition[0] = (c.xScale[MIN] + c.xScale[MAX]) / 2;
			f.headerPosition[1] = (c.yScale[MAX] + border[NORTH]) / 2;
		}
		if (f.footerPosition == null) {
			f.footerPosition = new double[2];
			f.footerPosition[0] = (c.xScale[MIN] + c.xScale[MAX]) / 2;
			f.footerPosition[1] = (c.yScale[MIN] + border[SOUTH]) / 2;
		}

	}

	private void plotBars() {
		double[] barvalues = d.values;
		int n = barvalues.length;
		double[] barHeight = new double[n];

		final double ySpacing = border[NORTH] - border[SOUTH];
		for (int i = barvalues.length - 1; i >= 0; i--)
			barHeight[i] = (barvalues[i] - value[MIN]) / (value[MAX] - value[MIN]) * ySpacing;
		final double xSpacing = border[EAST] - border[WEST];
		final double barWidth = xSpacing / (n + 0.5);
		final double x0 = border[WEST] + barWidth / 2;

		if (f.isBarFilled) {
			StdDraw.setPenColor(f.barFillColor);
			for (int i = 0; i < n; i++)
				StdDraw.filledRectangle(x0 + i * barWidth, border[SOUTH] + barHeight[i] / 2, barWidth / 4,
						barHeight[i] / 2);
		}
		if (f.hasBarFrame) {
			StdDraw.setPenColor(f.barFrameColor);
			for (int i = 0; i < n; i++)
				StdDraw.rectangle(x0 + i * barWidth, border[SOUTH] + barHeight[i] / 2, barWidth / 4, barHeight[i] / 2);
		}
	}

	private void plotLeftRuler() {
		Font font = f.leftRulerMarkFont;
		StdDraw.setFont(font);
		StdDraw.setPenColor(f.leftRulerColor);
		final double x0 = border[WEST] - 0.005, x1 = border[WEST] + 0.005;
		String[] mark = new String[rulerGrade + 1];
		// rulerStepLength is the distence between two rulergrade
		final double rulerStepLength = (border[NORTH] - border[SOUTH]) / rulerGrade;
		for (int i = 0; i <= rulerGrade; i++) {
			double y = border[SOUTH] + i * rulerStepLength;
			StdDraw.line(x0, y, x1, y);
		}
		StdDraw.setPenColor(f.leftRulerMarkColor);
		final double xs = border[WEST] - f.leftRulerMarkDistence;
		for (int i = 0; i <= rulerGrade; i++) {
			double markOri = value[MIN] + i * rulerStep;
			mark[i] = numberForRuler(markOri);
			double y = border[SOUTH] + i * rulerStepLength;
			StdDraw.textRight(xs, y, mark[i]);
		}
	}

	private void plotRightRuler() {
		Font font = f.rightRulerMarkFont;
		StdDraw.setFont(font);
		StdDraw.setPenColor(f.rightRulerColor);
		final double x0 = border[EAST] - 0.005, x1 = border[EAST] + 0.005;
		String[] mark = new String[rulerGrade + 1];
		final double rulerStepLength = (border[NORTH] - border[SOUTH]) / rulerGrade;
		for (int i = 0; i <= rulerGrade; i++) {
			double y = border[SOUTH] + i * rulerStepLength;
			StdDraw.line(x0, y, x1, y);
		}
		StdDraw.setPenColor(f.rightRulerMarkColor);
		final double xs = border[EAST] + f.rightRulerMarkDistence;
		for (int i = 0; i <= rulerGrade; i++) {
			double markOri = value[MIN] + i * rulerStep;
			mark[i] = numberForRuler(markOri);
			double y = border[SOUTH] + i * rulerStepLength;
			StdDraw.textLeft(xs, y, mark[i]);
		}
	}

	private String numberForRuler(double x) {
		if (value[MAX] >= 5 && rulerStep > 1)
			return "" + (int) x;
		if (rulerStep > 0.1)
			return String.format("%.1f", x);
		if (rulerStep > 0.01)
			return String.format("%.2f", x);
		if (rulerStep > 0.001)
			return String.format("%.3f", x);
		if (rulerStep > 0.0001)
			return String.format("%.4f", x);
		if (rulerStep > 0.00001)
			return String.format("%.5f", x);
		return String.format("%g", x);
	}

	private void plotKeys() {
		Font font = f.keyFont;
		StdDraw.setFont(font);
		StdDraw.setPenColor(f.keyColor);
		final double y = border[SOUTH] - f.keyDistence;
		final double xSpacing = border[EAST] - border[WEST];
		final double barWidth = xSpacing / (d.keys.length + 0.5);
		final double x0 = border[WEST] + barWidth / 2;
		for (int i = 0; i < d.keys.length; i++) {
			double x = x0 + i * barWidth;
			StdDraw.text(x, y, d.keys[i], f.keyAngle);
		}
	}

	private void plotBorder() {
		double x = (border[EAST] + border[WEST]) / 2;
		double y = (border[NORTH] + border[SOUTH]) / 2;
		double halfWidth = (border[EAST] - border[WEST]) / 2;
		double halfHeight = (border[NORTH] - border[SOUTH]) / 2;
		StdDraw.setPenColor(f.borderColor);
		StdDraw.rectangle(x, y, halfWidth, halfHeight);
	}

	private void plotHeader() {
		Font font = f.headerFont;
		StdDraw.setFont(font);
		double x = f.headerPosition[0];
		double y = f.headerPosition[1];
		StdDraw.setPenColor(f.headerColor);
		StdDraw.text(x, y, d.header, f.headerAngle);
	}

	private void plotFooter() {
		Font font = f.footerFont;
		StdDraw.setFont(font);
		double x = f.footerPosition[0];
		double y = f.footerPosition[1];
		StdDraw.setPenColor(f.footerColor);
		StdDraw.text(x, y, d.footer, f.footerAngle);
	}

	private final int MAX = 1, MIN = 0;
	private final int NORTH = 0, SOUTH = 1, WEST = 2, EAST = 3;

}
