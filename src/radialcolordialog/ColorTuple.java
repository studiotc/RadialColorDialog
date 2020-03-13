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

/**
 *  Color Tuple describes either a RGB Color or HSB Color with an Alpha channel.
 * The color is described as ABCD for: RGBA ~ Red, Green, Blue, Alpha or HSBA Hue, Saturation, Brightness, Alpha.
 * @author Tom
 */
public class ColorTuple {

    private float _a;
    private float _b;
    private float _c;


    /**
     * Basic constructor. The values are 0, and it is in RGB mode.
     */
    public ColorTuple() {

        _a = 0;
        _b = 0;
        _c = 0;


    }

    /**
     * Constructor with values.  The values should be in the range of 0.0 to 1.0.
     * @param a The a value (0.0 to 1.0).
     * @param b The b value (0.0 to 1.0).
     * @param c The c value (0.0 to 1.0).
     */
    public ColorTuple(float a, float b, float c) {
        super();

        _a = checkDomain(a);
        _b = checkDomain(b);
        _c = checkDomain(c);
 

    }
    
    
    public static ColorTuple fromRGB(final Color color) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        
        float cr = checkDomainRGB(r);
        float cg = checkDomainRGB(g);
        float cb = checkDomainRGB(b);
        
        float sr = cr / 255.0f;
        float sg = cg / 255.0f;
        float sb = cb / 255.0f;
        
        return new ColorTuple(sr,sg,sb);
    }


    /**
     * Get the 'a' value of the tuple.
     *
     * @return The 'a' value;
     */
    public float getA() {
        return _a;
    }

    /**
     * Set the 'a' value of the tuple.
     *
     * @param a The value of the 'a' tuple.
     */
    public void setA(float a) {
        _a = checkDomain(a);
    }

    /**
     * Get the 'b' value of the tuple.
     *
     * @return The 'b' value;
     */
    public float getB() {
        return _b;
    }

    /**
     * Set the 'b' value of the tuple.
     *
     * @param b The value of the 'a' tuple.
     */
    public void setB(float b) {
        _b = checkDomain(b);
    }

    /**
     * Get the 'c' value of the tuple.
     *
     * @return The 'c' value;
     */
    public float getC() {
        return _c;
    }

    /**
     * Set the 'c' value of the tuple.
     *
     * @param c The value of the 'a' tuple.
     */
    public void setC(float c) {
        _c = checkDomain(c);
    }

    
    /**
     * Get a channel value converted to an integer from the provided range (channel * range).
     * @param channel  The channel value to use as a base value.
     * @param range  The range to scale the channel by.
     * @return The integer value of the channel.
     */
    public int getChannelAsInt(Channel channel, int range) {
        
        float cVal = 1.0f;
        
        switch(channel) {
            case ChannelA:
                cVal = _a;
                break;
            case ChannelB:
                cVal = _b;
                break;
            case ChannelC:
                cVal = _c;
                break;
            
            
        }
        
        int conv = (int)Math.round(cVal * range);
        
        return conv;
        
    }
    
    
    
//    
//    public void setMode(boolean rgb) {
//        _isRGB = rgb;
//    }
    /**
     * Get a Color from this tuple treating the values as R,G,B (A,B,C).
     *
     * @return The color generated from the values as R,G,B.
     */
    public Color getRGB() {
        return new Color(_a, _b, _c);
    }

    /**
     * Get a color from this tuple treating the values as H,S,B (A,B,C);
     *
     * @return The color generated from the values as H,S,B.
     */
    public Color getHSB() {
        return Color.getHSBColor(_a, _b, _c);
 
    }

    /**
     * Get the standard color from the ColorTuple.
     * This converts the values to an RGB color with the default alpha value.
     * @param type Type of tuple to use as basis for conversion (RGB or HSB)
     * @return The color converted from the tuple.
     */
    public Color getColor(TupleType type) {

        switch (type) {

            case RGB:
                return getRGB();

            case HSB:
                return getHSB();
                

            default:
                return Color.RED;

        }

    }

    
    
    /**
     * Check the domain of 't', must be in range of 0.0 to 1.0.
     *
     * @param t The number to check the bounds on.
     * @return The value constrained to the domain of 0.0 to 1.0.
     */
    private float checkDomain(final float t) {

        float nt = t;

        if (nt > 1.0f) {
            nt = 1.0f;
        }
        if (nt < 0.0f) {
            nt = 0.0f;
        }

        return nt;

    }
    
    /**
     * Check the domain of an RGB value. The value should be 0-255.
     * @param c The channel value of the RGB.
     * @return The value constrained to 0-255.
     */
    private static int checkDomainRGB(final int c) {
        
        int nc = c;
        
        if(nc < 0) nc = 0;
        if(nc>255) nc = 255;
        
        return nc;
        
        
    }

    
    
    
    
}
