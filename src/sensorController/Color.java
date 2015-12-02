package sensorController;

import java.util.ArrayList;

/*
 * Created by evanknox on 2015-10-18.
 */

/**
 * 
 * 
 *
 */
public class Color {
	private double R;
	private double G;
	private double B;
	
	
	// following color detection methods: red, blue, yellow, white and light blue
	//use proportion of RGB values to find block color
	public boolean isSampleRed() {
		if (this.R > this.G && this.R > this.B && (this.B/this.R)*100<=30) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSampleWhite() {
		if (!isSampleRed()) {
			if ((this.R / this.G) * 100 >= 85 && (this.R / this.G) * 100 <= 140) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isSampleYellow() {
		if (!isSampleBlue()) {
			if (this.R > this.B && this.R > this.G && !isSampleWhite()
					&& ((this.G / this.R) * 100 >= 50)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isSampleBlue() {
		if (this.R < this.B && this.G < this.B) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSampleLightBlue() {
		if (this.R<this.G && (this.B / this.G) * 100 >= 40 && (this.B / this.G)*100 <=90 
				&&  !isSampleWhite()) {
			return true;
		} else {
			return false;
		}
	}


	public boolean isBadSample() {
		if (this.R < 2 && this.B < 2 && this.G < 2) {
			return true;
		} else {
			return false;
		}
	}
	
	public int colorValue(ArrayList <Color> colorPings){
		// get average of each value
				double rVal = 0;
				double gVal = 0;
				double bVal = 0;
				Color color = new Color();
				for (Color i : colorPings) {
					rVal += i.getR();
					gVal += i.getG();
					bVal += i.getB();
				}
				rVal /= 10.0;
				gVal /= 10.0;
				bVal /= 10.0;
				color.setR(rVal);
				color.setG(gVal);
				color.setB(bVal);

				if (color.isSampleBlue()) {
					return 0;
				} else if (color.isSampleYellow()) {
					return 1;
				} else if (color.isSampleWhite()) {
					return 2;
				} else if (color.isSampleRed()) {
					return 3;
				} else if (color.isSampleLightBlue()) {
					return 4;
				} else {
					// bad sample was taken
					return -1;
				}
	}

	public double getB() {
		return B;
	}

	public void setB(double b) {
		B = b;
	}

	public double getR() {
		return R;
	}

	public void setR(double r) {
		R = r;
	}

	public double getG() {
		return G;
	}

	public void setG(double g) {
		G = g;
	}
}
