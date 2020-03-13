/*
 * The MIT License
 *
 * Copyright 2020 Tom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package radialcolordialog;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 *  A collection of general utility functions.
 * @author Tom
 */
public class Util {
    
    /**
     * Degrees to Radians.  Convert an angle in degrees to radians.
     *
     * @param deg The angle in degrees.
     * @return The angle converted to radians.
     */
    public static double dToR(double deg) {

        return Math.PI * deg / 180.0;

    }   
    
    
    /**
     * Radians to Degrees.  Convert an angle in radians to degrees.
     *
     * @param rad The angle in radians.
     * @return The angle converted to degrees.
     */
    public static double rToD(double rad) {

        return rad * (180.0 / Math.PI);

    }    
    
    
    /**
     * Angle from start point to end point.
     * This finds the angle in radians from the start point to the end point.
     *
     * @param start Start point to find angle from.
     * @param end End point to find angle from.
     * @return Angle from start point to end point in radians.
     */
    public static double anglePointPoint(Point2D start, Point2D end) {

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return Math.atan2(dy, dx);
    }    
    
    
    /**
     * Compose a simple RGB color with the specified Alpha.  This creates a new
     * color with the alpha applied.
     * @param color  The color to apply the alpha to.
     * @param alpha  The alpha to apply to the color.
     * @return The color with the alpha applied.
     */
    public static Color composeColorWithAlpha(Color color, int alpha) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        return new Color(r,g,b, alpha);
        
    }     
    
    
    
}
