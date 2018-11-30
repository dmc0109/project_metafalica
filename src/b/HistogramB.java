package b;

import java.awt.Color;
import java.awt.Font;

import a.StdDraw;


class Canvas{
	int x=512, y=512;
	double[] scale = {1, -1, -1, 1};
	Color bgColor = Color.WHITE;
}

class Formats{
	public final int GROUPED = 0, STACKED = 1;
	int type = GROUPED;
	
	double border[] = {0.8, -0.8, -0.8, 0.8};
	
	boolean[] isBarFilled = {true};
	Color[] barFillColor = {Color.BLACK};
	boolean[] hasBarFrame = {true};
	Color[] barFrameColor = {Color.BLACK};
	
	boolean hasBorder = true;
	Color borderColor = Color.BLACK;
	
	boolean hasLeftRuler = true;
	Color leftRulerColor = Color.BLACK;
	Font leftRulerMarkFont = new Font("consolas", Font.PLAIN, 12);
	Color leftRulerMarkColor = Color.BLACK;
	double leftRulerMarkDistence = 0.02;
	double leftRulerMarkAngle = 0;
	
	boolean hasRightRuler = true;
	Color rightRulerColor = Color.BLACK;
	Font rightRulerMarkFont = new Font("consolas", Font.PLAIN, 12);
	Color rightRulerMarkColor = Color.BLACK;
	double rightRulerMarkDistence = 0.02;
	double rightRulerMarkAngle = 0;
	
	Font keyFont = new Font("consolas", Font.PLAIN, 12);
	Color keyColor = Color.BLACK;
	double keyDistance = 0.035;
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
	
	boolean hasLengends = true;
	double[] lengendsPosition = {0.8, 0.8};
	double[] lengendsSize = {0.1, 0.1};
	boolean isLengendsFilled = true;
	Color lengendsBgcolor = Color.WHITE;
	boolean hasLengendsBorder = true;
	Color lengendsBorderColor = Color.BLACK;
	Font lengendsMarkFont = new Font ("consolas", Font.PLAIN, 12);
	Color lengendsMarkColor = Color.BLACK;
}

class HistogramData {
	String header = "";
	String footer = "";
	double minValue = 0.0;
	String[] keys = {};
	double[][] values = {};
}

public class HistogramB{
	Canvas c;
	Formats f;
	HistogramData d;
	double[] scales;
	double[] value;
	int rulerGrade;
	double rulerStep;
	
	public HistogramB(Canvas c, Formats f, HistogramData d) {
		this.c = c;
		this.f = f;
		this.d = d;
		scales = new double[4];
		value = new double[2];
		setHistogramParameters();
	}
	
	private void setHistogramParameters() {
		double[][] a = d.values;
		double min = d.minValue;
		double max = Double.MIN_VALUE;
		if(f.type==f.GROUPED)
			for(double[] e:a)
				for(double f:e)
					if(f>max)
						max = f;
		if(f.type==f.STACKED)
			for(int i=0; i<a[0].length; i++) {
				double sum = 0;
				for(int j=0; j<a.length; j++)
					sum+=a[j][i];
				if(sum>max)
					max = sum;
			}
		int level = (int) Math.floor(Math.log10(max - min));
		int nmin = (int) Math.floor(min / Math.pow(10, level));
		int nmax = (int) Math.ceil(max / Math.pow(10, level));
		double step = 1;
		int ndis = (nmax - nmin) * 10;
		if (ndis % 10 == 0 && ndis / 10 >= 5)
			step = 10;
		else if (ndis % 5 == 0 && ndis / 5 >= 5)
			step = 5;
		else if (ndis * 2 % 5 == 0 && ndis * 2 / 5 >= 5)
			step = 2.5;
		else if (ndis % 4 == 0 && ndis / 4 >= 5)
			step = 4;
		else if (ndis % 3 == 0 && ndis / 3 >= 5)
			step = 3;
		else if (ndis % 2 == 0 && ndis / 2 >= 5)
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
	}
	
	public void draw() {
		setCanvas();
		plotBars();
		plotKeys();
		if(f.hasLeftRuler)
			plotLeftRuler();
		if(f.hasBorder)
			plotBorder();
		if(f.hasRightRuler)
			plotRightRuler();
		if(f.hasHeader)
			plotHeader();
		if(f.hasFooter)
			plotFooter();
//		if(f.hasLengends)
		//	plotLengends();
	}
	
	private void plotBars() {
		double[][] barValues = d.values;
		int n = barValues[0].length;
		final double xSpacing = f.border[EAST]-f.border[WEST];
		final double ySpacing = f.border[NORTH] - f.border[SOUTH];
		double[][] barHeight = new double[barValues.length][barValues[0].length];
		for(int i=barValues.length-1; i>=0; i--)
			for(int j=barValues[0].length-1; j>=0; j--)
				barHeight[i][j] = (barValues[i][j]-value[MIN]) / (value[MAX]-value[MIN])*ySpacing;
		if(f.type==f.GROUPED) { 
			final double groupWidth = xSpacing/(n+0.5);
			final double barWidth = groupWidth*0.8/barValues.length;
			final double x0 = f.border[WEST] + groupWidth/2;
			for(int i=0; i<barValues[0].length; i++)
				for(int j=0; j<barValues.length; j++) {
					double xfix = (double)j-(double)(barValues.length-1)/2.0;
					xfix*=barWidth;
					if(f.isBarFilled[j]) {
						StdDraw.setPenColor(f.barFillColor[j]);
						StdDraw.filledRectangle(x0+i*groupWidth+xfix, f.border[SOUTH]+barHeight[j][i]/2, barWidth/3, barHeight[j][i]/2);
					}
					if(f.hasBarFrame[j]) {
						StdDraw.setPenColor(f.barFrameColor[j]);
						StdDraw.rectangle(x0+i*groupWidth+xfix, f.border[SOUTH]+barHeight[j][i]/2, barWidth/3, barHeight[j][i]/2);
					}				
				}		
		}
		else if(f.type==f.STACKED) {
			final double barWidth = xSpacing /(n+0.5);
			final double x0 = f.border[WEST] + barWidth/2;
			for(int i=0; i<barValues[0].length; i++) {
				double y0 = f.border[SOUTH];
				for(int j=0; j<barValues.length; j++) {
					if(f.isBarFilled[j]) {
						StdDraw.setPenColor(f.barFillColor[j]);
						StdDraw.filledRectangle(x0+i*barWidth, y0+barHeight[j][i]/2, barWidth/4, barHeight[j][i]/2);
					}
					if(f.hasBarFrame[j]) {
						StdDraw.setPenColor(f.barFrameColor[j]);
						StdDraw.rectangle(x0+i*barWidth, y0+barHeight[j][i]/2, barWidth/4, barHeight[j][i]/2);
					}
					y0+=barHeight[j][i];
				}
			}
		}
	}
	
	private void setCanvas() {
		StdDraw.setCanvasSize(c.x, c.y);
		StdDraw.setXscale(c.scale[WEST], c.scale[EAST]);
		StdDraw.setYscale(c.scale[SOUTH], c.scale[NORTH]);
		if (f.headerPosition == null) {
			f.headerPosition = new double[2];
			f.headerPosition[0] = (c.scale[WEST] + c.scale[EAST]) / 2;
			f.headerPosition[1] = (c.scale[NORTH] + f.border[NORTH]) / 2;
		}
		if (f.footerPosition == null) {
			f.footerPosition = new double[2];
			f.footerPosition[0] = (c.scale[WEST] + c.scale[EAST]) / 2;
			f.footerPosition[1] = (c.scale[SOUTH] + f.border[SOUTH]) / 2;
		}
		StdDraw.clear(c.bgColor);
	}
	
	private void plotLeftRuler() {
		Font font = f.leftRulerMarkFont;
		StdDraw.setFont(font);
		StdDraw.setPenColor(f.leftRulerColor);
		final double x0 = f.border[WEST] - 0.005, x1 = f.border[WEST] + 0.005;
		String[] mark = new String[rulerGrade + 1];
		// rulerStepLength is the distence between two rulerline
		final double rulerStepLength = (f.border[NORTH] - f.border[SOUTH]) / rulerGrade;
		for (int i = 0; i <= rulerGrade; i++) {
			double y = f.border[SOUTH] + i * rulerStepLength;
			StdDraw.line(x0, y, x1, y);
		}
		StdDraw.setPenColor(f.leftRulerMarkColor);
		final double xs = f.border[WEST] - f.leftRulerMarkDistence;
		for (int i = 0; i <= rulerGrade; i++) {
			double markOri = value[MIN] + i * rulerStep;
			mark[i] = numberForRuler(markOri);
			double y = f.border[SOUTH] + i * rulerStepLength;
			StdDraw.textRight(xs, y, mark[i]);
		}
	}

	private void plotRightRuler() {
		Font font = f.rightRulerMarkFont;
		StdDraw.setFont(font);
		StdDraw.setPenColor(f.rightRulerColor);
		final double x0 = f.border[EAST] - 0.005, x1 = f.border[EAST] + 0.005;
		String[] mark = new String[rulerGrade + 1];
		final double rulerStepLength = (f.border[NORTH] - f.border[SOUTH]) / rulerGrade;
		for (int i = 0; i <= rulerGrade; i++) {
			double y = f.border[SOUTH] + i * rulerStepLength;
			StdDraw.line(x0, y, x1, y);
		}
		StdDraw.setPenColor(f.rightRulerMarkColor);
		final double xs =f. border[EAST] + f.rightRulerMarkDistence;
		for (int i = 0; i <= rulerGrade; i++) {
			double markOri = value[MIN] + i * rulerStep;
			mark[i] = numberForRuler(markOri);
			double y = f.border[SOUTH] + i * rulerStepLength;
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
		final double y = f.border[SOUTH] - f.keyDistance;
		final double xSpacing = f.border[EAST] - f.border[WEST];
		final double barWidth = xSpacing / (d.keys.length + 0.5);
		final double x0 = f.border[WEST] + barWidth / 2;
		for (int i = 0; i < d.keys.length; i++) {
			double x = x0 + i * barWidth;
			StdDraw.text(x, y, d.keys[i], f.keyAngle);
		}
	}

	private void plotBorder() {
		double x = (f.border[EAST] + f.border[WEST]) / 2;
		double y = (f.border[NORTH] + f.border[SOUTH]) / 2;
		double halfWidth = (f.border[EAST] - f.border[WEST]) / 2;
		double halfHeight = (f.border[NORTH] - f.border[SOUTH]) / 2;
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
	
	private final int NORTH=0, SOUTH=1, WEST=2, EAST=3;
	private final int MIN = 0, MAX = 1;
}