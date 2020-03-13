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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The AlphaSlider is a linear slider for the Alpha Channel.
 * This is implemented as a linear slider to use built-in mechanisms
 * to display the alpha cleanly.
 * 
 * 
 *
 * @author Tom
 */
public class AlphaSlider implements InteractiveSlider{

    private Color _color1;
    private Color _color2;

    private AlphaSliderListener _listener;

    private ControlHandle _controlHandle;

    private double _handleLocation;

    private final static int VAL_MIN = 0;
    private final static int VAL_MAX = 255;
    
    
    private Rectangle2D _bounds;
    
    private Image _bgImage;

    private int _value;

    /**
     * Build the slider from the bounds and default color.  
     * The bounds are described in world space coordinates.
     * 
     * @param bounds  The bounds of the slider.
     * @param color  The default color for the slider.
     * @param listener The listener for changes.
     */
    public AlphaSlider(Rectangle2D bounds, Color color,  AlphaSliderListener listener) {
        super();


        _bounds = bounds;
        
        int bgw = (int)Math.round(_bounds.getWidth());
        int bgh = (int)Math.round(_bounds.getHeight());
        
        _bgImage = CheckerBoard.makeImage(bgw, bgh, 8);
        
        _controlHandle = new ControlHandle(new Point2D.Double(_bounds.getX(),_bounds.getCenterY()), false);

        _color1 = Color.BLACK; 
        _color2 = Color.BLACK; 
        //set the color - sets the two colors with alpha
        setColor(color);

        _handleLocation = _bounds.getX();

        _value = VAL_MIN;

        _listener = listener;

  

    }

    /**
     * Call when slider has changed to notify the listener.
     */
    private void onChange() {

        if (_listener != null) {
            _listener.alphaChanged(_value);
        }

    }

    /**
     * Get the Value of the slider (0-255)
     *
     * @return The current value (0-255)
     */
    public int getValue() {
        return _value;
    }

    /**
     * Set the alpha value of the slider.
     *
     * @param value The value of the alpha channel..
     */
    public void setAlpha(int value) {


        int newValue = Math.min(VAL_MAX, value); //constrain upper range

        newValue = Math.max(VAL_MIN, newValue);//constrian lower range

        _value = newValue;

        double scale = value / 255.0;
        
        double lx = _bounds.getWidth() * scale;
        //handle location is a transform - so this works
        //no min x needed as graphics is already transformed
        _handleLocation = lx;

    }






    /**
     * Set the display color.  This is amended with the current alpha.
     * @param color The color to use for display.
     */
    public final void setColor(Color color) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        _color1 = new Color(r,g,b,0);
        _color2 = new Color(r,g,b,255);
        
    }

 



 
    /**
     * Render the AlphaSlider.
     * 
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {


        int imgX = (int)Math.round(_bounds.getMinX());
        int imgY = (int)Math.round(_bounds.getMinY());
        
        //draw th checkerboard underneath
        g2.drawImage(_bgImage, imgX, imgY, null);
 

        float rx = (float)_bounds.getMinX();
        float ry = (float)_bounds.getCenterY();
        float rw = (float)_bounds.getWidth();

        Point2D start = new Point2D.Float(rx, ry);
        Point2D end = new Point2D.Float(rx + rw, ry);
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {_color1, _color2};
        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);

        //set the paint
        g2.setPaint(lgp);
        //fill with gradient
        g2.fill(_bounds);

        
        /***  Render the Handle ***/
   
        //draw handle on top
       //get the current transform
        AffineTransform cTrans = g2.getTransform();
        AffineTransform handleTrans = new AffineTransform();
        //move to handle location
        handleTrans.translate(_handleLocation, 0);

        //transform for handle
        g2.transform(handleTrans);        
        //render the handle 
        _controlHandle.render(g2);
        
        //restore transform
        g2.setTransform(cTrans);         
        


    }

    
    
    
//<editor-fold defaultstate="collapsed" desc="Interactive Slider methods">
    
    
    /**
     * Update the slider from a point in world coordinates.
     * @param point The point n world coordinates.
     */
    @Override
    public void updateFromPoint(Point2D point) {
       
        
        double x = point.getX();
        
        double maxX = _bounds.getMaxX();
        double minX = _bounds.getMinX();
        
        if(x > maxX) x = maxX;
        if(x < minX) x = minX;
        
        double localX = x - minX;
        
        double range = maxX - minX;
        
        double scale = localX / range;
        

        //use normalized location for handle
        _handleLocation = localX;

        //update the value
        double alpha = 255.0 * scale;
        _value = (int)Math.round(alpha);
        
        //notify the listener
        onChange();        
        
        
    }
    
    /**
     * Called by the display panel to do a hit test.
     * @param point  The point in world coordinates.
     * @return True if the AlphaSlider contains the point, false otherwise.
     */
    @Override
    public boolean containsPoint(Point2D point) {
       
        return _bounds.contains(point);

    }
//</editor-fold>
    
    
  

 
}
