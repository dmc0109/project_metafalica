package b;

import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.Scanner;

import javax.json.*;

public class HistogramBTestBak {
	public static void main(String[] args) {
		Scanner inp = new Scanner(System.in);
		args[0] = inp.nextLine();
		inp.close();
		HistogramB h = createHistogramBFrom(args[0]);
		h.draw();
	}

	private static HistogramB createHistogramBFrom(String fileName) {
		HistogramB h = null;
		try (InputStream is = new FileInputStream(new File(fileName)); JsonReader rdr = Json.createReader(is)) {
			JsonObject obj = rdr.readObject().getJsonObject("histograma");
			Canvas canvas = getCanvasFrom(obj.getJsonObject("canvas"));
			Formats fmts = getFormatsFrom(obj.getJsonObject("formats"));
			HistogramData data = getDataFrom(obj.getJsonObject("data"));
			h = new HistogramB(canvas, fmts, data);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		;
		return h;
	}

	private static Canvas getCanvasFrom(JsonObject obj) {
		Canvas canvas = new Canvas();

		JsonArray szArray = obj.getJsonArray("size");
		if (szArray != null) { // otherwise, use the default size
			int[] size = toIntArray(szArray);
			canvas.x = size[0];
			canvas.y = size[1];
		}

		JsonArray sArray = obj.getJsonArray("scale");
		if (sArray != null) // otherwise, use the default xScale
			canvas.scale = toDoubleArray(sArray);


		JsonArray bgcArray = obj.getJsonArray("bgcolor");
		if (bgcArray != null) // otherwise, use the default bgColor
			canvas.bgColor = getColorFrom(bgcArray);

		// JsonArray cArray = obj.getJsonArray("color");
		// if (cArray != null) // otherwise, use the default color
		// canvas.color = getColorFrom(cArray);

		return canvas;
	}

	private static int[] toIntArray(JsonArray jsa) {
		int[] a = new int[jsa.size()];
		for (int i = 0; i < jsa.size(); i++)
			a[i] = jsa.getInt(i);
		return a;
	}

	private static double[] toDoubleArray(JsonArray jsa) {
		double[] a = new double[jsa.size()];
		for (int i = 0; i < jsa.size(); i++)
			a[i] = jsa.getJsonNumber(i).doubleValue();
		return a;
	}

	private static String[] toStringArray(JsonArray jsa) {
		String[] s = new String[jsa.size()];
		for (int i = 0; i < jsa.size(); i++)
			s[i] = jsa.getString(i);
		return s;
	}

	private static Color getColorFrom(JsonArray jsa) {
		int[] c = toIntArray(jsa);
		return new Color(c[0], c[1], c[2]);
	}

	private static Font getFontFrom(JsonObject jso) {
		String family = jso.getString("fontfamily");
		int size = 12;
		if (jso.containsKey("fontsize"))
			size = jso.getInt("fontsize");
		int style = Font.PLAIN;
		if (jso.containsKey("isbold"))
			if (jso.getBoolean("isbold"))
				style += Font.BOLD;
		if (jso.containsKey("isitalic"))
			if (jso.getBoolean("isitalic"))
				style += Font.ITALIC;
		return new Font(family, style, size);
	}

	private static Formats getFormatsFrom(JsonObject obj) { // TODO for default values
		Formats fmts = new Formats();
	//	if (obj.containsKey("margins"))
//			fmts.margins = toDoubleArray(obj.getJsonArray("margins"));
		if (obj.containsKey("isbarfilled")) {
			fmts.isBarFilled = new boolean[2];
			fmts.isBarFilled[0] = obj.getBoolean("isbarfilled");
			fmts.isBarFilled[1] = obj.getBoolean("isbarfilled");
		}
		if (obj.containsKey("barfillcolor")) {
			fmts.barFillColor = new Color[2];
			fmts.barFillColor[0] = getColorFrom(obj.getJsonArray("barfillcolor"));
			fmts.barFillColor[1] = Color.RED;
		}
		if (obj.containsKey("hasbarframe")) {
			fmts.hasBarFrame = new boolean[2];
			fmts.hasBarFrame[0] = obj.getBoolean("hasbarframe");
			fmts.hasBarFrame[1] = obj.getBoolean("hasbarframe");
		}
		if (obj.containsKey("barframecolor")) {
			fmts.barFrameColor = new Color[2];
			fmts.barFrameColor[0] = getColorFrom(obj.getJsonArray("barframecolor"));
			fmts.barFrameColor[1] = Color.BLACK;
		}
		if (obj.containsKey("hasborder"))
			fmts.hasBorder = obj.getBoolean("hasborder");
		if (obj.containsKey("bordercolor"))
			fmts.borderColor = getColorFrom(obj.getJsonArray("bordercolor"));
		if (obj.containsKey("rulercolor"))
			fmts.leftRulerColor = getColorFrom(obj.getJsonArray("rulercolor"));
		if (obj.containsKey("rulermarkcolor"))
			fmts.leftRulerMarkColor = getColorFrom(obj.getJsonArray("rulermarkcolor"));
		if (obj.containsKey("rulerfont"))
			fmts.leftRulerMarkFont = getFontFrom(obj.getJsonObject("rulerfont"));
		if (obj.containsKey("hasrightruler"))
			fmts.hasRightRuler = obj.getBoolean("hasrightruler");
		if (obj.containsKey("rightrulercolor"))
			fmts.rightRulerColor = getColorFrom(obj.getJsonArray("rightrulercolor"));
		if (obj.containsKey("rightrulermarkcolor"))
			fmts.rightRulerMarkColor = getColorFrom(obj.getJsonArray("rightrulermarkcolor"));
		if (obj.containsKey("rightrulerfont"))
			fmts.rightRulerMarkFont = getFontFrom(obj.getJsonObject("rightrulerfont"));
		if (obj.containsKey("keycolor"))
			fmts.keyColor = getColorFrom(obj.getJsonArray("keycolor"));
		if (obj.containsKey("keyfont"))
			fmts.keyFont = getFontFrom(obj.getJsonObject("keyfont"));
		if (obj.containsKey("keydistence"))
			fmts.keyDistance = obj.getJsonNumber("keydistence").doubleValue();
		if (obj.containsKey("keyangle"))
			fmts.keyAngle = obj.getJsonNumber("keyangle").doubleValue();
		if (obj.containsKey("hasheader"))
			fmts.hasHeader = obj.getBoolean("hasheader");
		if (obj.containsKey("headerfont"))
			fmts.headerFont = getFontFrom(obj.getJsonObject("headerfont"));
		if (obj.containsKey("headercolor"))
			fmts.headerColor = getColorFrom(obj.getJsonArray("headercolor"));
		if (obj.containsKey("headerposition")) {
			JsonArray array = obj.getJsonArray("headerposition");
			double[] position = new double[2];
			position[0] = array.getJsonNumber(0).doubleValue();
			position[1] = array.getJsonNumber(1).doubleValue();
			fmts.headerPosition = position;
		}
		if (obj.containsKey("hasfooter"))
			fmts.hasFooter = obj.getBoolean("hasfooter");
		if (obj.containsKey("footerfont"))
			fmts.footerFont = getFontFrom(obj.getJsonObject("footerfont"));
		if (obj.containsKey("footercolor"))
			fmts.footerColor = getColorFrom(obj.getJsonArray("footercolor"));
		if (obj.containsKey("footerposition")) {
			JsonArray array = obj.getJsonArray("footerposition");
			double[] position = new double[2];
			position[0] = array.getJsonNumber(0).doubleValue();
			position[1] = array.getJsonNumber(1).doubleValue();
			fmts.footerPosition = position;
		}
		fmts.type = fmts.STACKED;
		return fmts;
	}

	private static HistogramData getDataFrom(JsonObject obj) {
		HistogramData data = new HistogramData();
		data.header = obj.getString("header", "");
		data.footer = obj.getString("footer", "");
		if (obj.containsKey("minvalue"))
			data.minValue = obj.getJsonNumber("minvalue").doubleValue(); // TODO for default value
		data.keys = toStringArray(obj.getJsonArray("keys"));
		data.values = new double[2][];
		data.values[0] = toDoubleArray(obj.getJsonArray("values"));
		data.values[1] = toDoubleArray(obj.getJsonArray("values"));
		return data;
	}
}
