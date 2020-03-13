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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 *  The ColorBand is a radially aligned slider for color channels (r,g,b, etc.).
 *  This is designed to use the other related color channels for a mixing effect when selecting a color.
 *  I.e. : A change to the red channel will show in the blue and green ColorBands.  The blue and green bands will
 *  mix in the red value.
 *  Color channels are stored in scalar values from 0.0 to 1.0;
 * 
 * 
 * @author Tom
 */
public class ColorBand implements InteractiveSlider {

    
    private ColorBandListener _listener;
    
    
    private double _radius;
    private double _arcBegin;
    private double _arcEnd;
    private double _arcSweep;

    private double _arcWidth;

    private double _arcLength;
    
    private boolean _isCircle;

    private Point2D _center;

    private TupleType _tupleType;
    private Channel _channel;

    private double _value;
    private double _curTheta;

    private ColorRange _colorRange;

    private Path2D _handlePath;
    
    private ControlHandle _controlHandle;

    /**
     * Build the color band from the color and layout specifications
     * @param listener  Listener for changes to the selected channel.
     * @param cType The type of color (RGB or HSB).
     * @param channel The channel of the color (r,g,b; or h,s,b).
     * @param center  The center point to layout from.
     * @param radius The radius of the ColorBand.
     * @param arcBegin  The arc starting angle in radians..
     * @param arcEnd The arc ending angle in radians. 
     * @param width The width of the ColorBand.
     */
    public ColorBand(ColorBandListener listener, TupleType cType, Channel channel, Point2D center, double radius, double arcBegin, double arcEnd, double width) {

        _listener = listener;
        
        _tupleType = cType;
        _channel = channel;
        _center = center;
        _radius = radius;
        _arcBegin = getNormalizedTheta(arcBegin); 
        _arcEnd = getNormalizedTheta(arcEnd); 
        _arcWidth = width;

        //calculate the sweep
        _arcSweep = getArcSweep(_arcBegin, _arcEnd);

        //is this a circle?
        _isCircle = _arcSweep == Math.PI * 2 ? true : false;

        double circumfrance = 2 * Math.PI * radius;
        _arcLength = _arcSweep / Math.PI * 2 * circumfrance;

        _colorRange = new ColorRange();
        _colorRange.setStart(new ColorTuple(1, 0, 0));
        _colorRange.setEnd(new ColorTuple(0, 0, 0));

        //init the handle path
        initHandlePath();
        
        _controlHandle = new ControlHandle(new Point2D.Double(_radius,0),true);

        //current values
        _value = 0;
        _curTheta = _arcBegin;

    }

    /**
     * Initialize the path for the handle.
     */
    private void initHandlePath() {

        _handlePath = new Path2D.Double();
        //outter radius
        double hRad = _radius + (_arcWidth / 2) - 12;

        double xsd = 8; //step dist on x axis
        double ysd = 5; //step dist on y axis

        //layout at 0 degrees (it will be rotated in paint)
        double x = hRad;
        double y = 0;
        _handlePath.moveTo(x, y);
        _handlePath.lineTo(x + xsd, y - ysd);
        _handlePath.lineTo(x + xsd + xsd, y - ysd);
        _handlePath.lineTo(x + xsd + xsd, y + ysd);
        _handlePath.lineTo(x + xsd, y + ysd);
        _handlePath.lineTo(x, y);

    }

    /**
     * Set the start and end colors of the color band.
     *
     * @param start The start color of the color band.
     * @param end The end color of the color band.
     */
    public void setColors(ColorTuple start, ColorTuple end) {

        _colorRange.setStart(start);
        _colorRange.setEnd(end);

    }

    /**
     * Get the outer radius of the Color band.
     *
     * @return The outer radius of the color band.
     */
    public double getOuterRadius() {
        return _radius + (_arcWidth / 2);
    }

    /**
     * Get the inner radius of the Color Band.
     *
     * @return The inner radius of the color band.
     */
    public double getInnerRadius() {
        return _radius - (_arcWidth / 2);
    }

    /**
     * Get the channel of the ColorBand.
     * @return The channel that this ColorBand controls
     */
    public Channel getChannel() {
        return _channel;
    }
    
    /**
     * Get the tuple type of this colorBand (RGB, or HSB).
     * @return The tuple type of this ColorBand.
     */
    public TupleType getTupleType() {
        return _tupleType;
    }
    
    /**
     * Get the value of the ColorBand.
     * This will be in the range of 0.0 to 1.0
     * @return The value of this ColorBand in range of 0.0 to 1.0;
     */
    public double getValue() {
        
        return _value;
        
    }
    
    /**
     * Update the ColorBand with a new value.
     * @param t The new value - range from 0.0 to 1.0
     */
    public void update(final double t) {
        
        _value = t;
        
        //update and normalize current theta
        double newTheta = getNormalizedTheta(_arcBegin + (_arcSweep * t));
        
        _curTheta = newTheta;
        
    }
    
    
    
    /**
     * Update the value from screen interaction.
     * This will fire of the change event.
     * @param theta The theta from the screen to update this ColorBand with.
     */
    public void updateFromScreen(final double theta) {
        
        //normalized screen theta
        double nsTheta = getNormalizedTheta(theta);
        
        //check theta and bring into range if needed
        //is this a circle (no checking needed)?
        if(!_isCircle) {

            if (_arcBegin > _arcEnd) {
                
                //outside range?...
                if(nsTheta < _arcBegin && nsTheta > _arcEnd) {
                    
                    //range
                    double r = _arcBegin - _arcEnd;
                    double hr = r / 2;

                    double deltaB =  _arcBegin - nsTheta;
                    double deltaE = nsTheta - _arcEnd;
                    //whicch one is closer?
                    if(deltaB < deltaE) {
                        nsTheta = _arcBegin;
                    } else {
                        nsTheta = _arcEnd;
                    }
                    
                    
                }

   
            }   else {
                
                //simple range check
                if(nsTheta < _arcBegin) {
                    nsTheta = _arcBegin;
                }
                if(nsTheta > _arcEnd) {
                    nsTheta =_arcEnd;
                }
                
            }//end if else     
            
        }//end if else circle
        
        //set the current theta
        _curTheta = nsTheta;
        
        //update value of 0 to 1
        double inputSweep = getArcSweep(_arcBegin, _curTheta);
       
        _value = inputSweep / _arcSweep;
        
        //notify listener
        _listener.colorBandChanged( new ColorBandEvent(this));
        
        
    }
    
    
    /**
     * See if this ColorBand contains the polar coordinates.
     *
     * @param distance The distance from center.
     * @param theta The angle in radians.
     * @return true if this ColorBand contains the coordinates, false otherwise.
     */
    public boolean containsPolar(final double distance, final double theta) {
        boolean contained = false;

        double hw = _arcWidth / 2.0;
        double r1 = _radius - hw;
        double r2 = _radius + hw;
        double nTheta = getNormalizedTheta(theta);

        if (distance >= r1 && distance <= r2) {

            //is it a full circle?
            if (_arcSweep == Math.PI * 2) {
                //only distance matters
                contained = true;
            } else {

                double cSweep = getArcSweep(_arcBegin, nTheta);

                //sweep crosses 0?
                if (_arcBegin > _arcEnd) {

                    if (nTheta >= _arcBegin && nTheta <= Math.PI * 2.0) {
                        contained = true;
                    } else if (nTheta >= 0 && nTheta <= _arcEnd) {
                        contained = true;
                    }

                } else {
                    //simple range check
                    if (nTheta >= _arcBegin && nTheta <= _arcEnd) {
                        contained = true;
                    }

                }

            }//end if else circle
        }//end if distance range     

        return contained;

    }


    /**
     * Render the Color Band.
     * This renders the band by painting the arc sweep with radially aligned lines.
     * THe colors are generated from the color range. 
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        double lenInc = 3;
        //chop arc length by length increment
        int n = (int) Math.round(_arcLength / lenInc);

        //increment angle
        double thetaInc = _arcSweep / n;
        //start angle
        double theta = _arcBegin;

        //ray lengths
        double lenS = _radius - (_arcWidth / 2);
        double lenE = _radius + (_arcWidth / 2);

        Line2D line = new Line2D.Double();

        for (int i = 0; i < n; i++) {

            double t = _arcBegin + (thetaInc * i);

            Point2D sp = polarFromCenter(lenS, t);
            Point2D ep = polarFromCenter(lenE, t);
            line.setLine(sp, ep);

            float ct = (float) i / (float) (n - 1);


            //get the tuple
            ColorTuple c = _colorRange.getTuple(ct);
            Color cc = c.getColor(_tupleType);
            //get the color
            g2.setColor(cc);

            g2.draw(line);

        }

    }//end render

    /**
     * Render an outline mask for this Color band. This renders a full circle
     * for the inner and outer radii to masks the edges of the color band.
     *
     * @param g2 Th graphics object to render to.
     */
    public void renderOutlineMask(Graphics2D g2) {

        double cx = _center.getX();
        double cy = _center.getY();
        //ray lengths
        double radInner = _radius - (_arcWidth / 2);
        double radOuter = _radius + (_arcWidth / 2);

        Ellipse2D inEll = new Ellipse2D.Double();
        inEll.setFrameFromCenter(cx, cy, cx - radInner, cy - radInner);

        Ellipse2D outEll = new Ellipse2D.Double();
        outEll.setFrameFromCenter(cx, cy, cx - radOuter, cy - radOuter);

        g2.draw(inEll);
        g2.draw(outEll);

    }

    /**
     * Render the color band handle. This is done as a second pass to draw on
     * top of the anti-aliasing masks.
     *
     * @param g2 The graphics object to paint to.
     * 
     */
    public void renderHandle(Graphics2D g2) {

        
        AffineTransform cTrans = g2.getTransform();

        AffineTransform handleTrans = new AffineTransform();
        //move to center
        handleTrans.translate(_center.getX(), _center.getY());
        //rotate based on value
        handleTrans.rotate(_curTheta); //TODO

        //transform for handle
        g2.transform(handleTrans);

        
        _controlHandle.render(g2);

        //restore transform
        g2.setTransform(cTrans);

    }

    /**
     * Polar point from the center.
     *
     * @param dist The distance of the polar point.
     * @param theta The angle in radians of the polar point.
     * @return The polar point from the center.
     */
    protected Point2D polarFromCenter(final double dist, final double theta) {

        double x = _center.getX() + dist * Math.cos(theta);
        double y = _center.getY() + dist * Math.sin(theta);

        return new Point2D.Double(x, y);

    }

    /**
     * Normalize an angle in radians to a range of 0 to 2PI.
     *
     * @param theta The angle in radians to normalize.
     * @return The normalized angle in the range of 0 to 2PI.
     */
    protected double getNormalizedTheta(final double theta) {

        double TAO = Math.PI * 2;
        double sign = Math.signum(theta);
        double nTheta = theta;

        //if 0 or + 2PI
        if (nTheta == 0.0 || nTheta == TAO) {
            return nTheta;
        }
        //negative 2PI?
        if (nTheta == -TAO) {
            return -nTheta;
        }

        //normalize to 0 to 2PI
        if (Math.abs(nTheta) > TAO) {
            nTheta = nTheta % TAO;
        }

        //if less than zero, normalize
        if (nTheta < 0) {
            nTheta += TAO;
        }

        return nTheta;

    }

    /**
     * Get the sweep of an arc from the start angle to the end angle.
     *
     * @param thetaStart The first angle in radians of the arc.
     * @param thetaEnd The ending angle in radians of the arc.
     * @return The sweep of the angle.
     */
    protected double getArcSweep(final double thetaStart, final double thetaEnd) {
        double sweep = 0;
        //normalize values
        double ts = getNormalizedTheta(thetaStart);
        double te = getNormalizedTheta(thetaEnd);

        //if start is ahead of end, go around the circle
        if (ts > te) {
            sweep = (Math.PI * 2) - ts + te;
        } else {
            //simple span
            sweep = te - ts;
        }

        return sweep;

    }
    
    
    
    @Override
    public String toString() {
        return "ColorBand [" + _tupleType.name() + " : " + _channel.name() + "]";
    }

    
    
//<editor-fold defaultstate="collapsed" desc="Interactive Slider Methods">
    
    /**
     * Called from the display to update from a point in world coordinates.
     * @param point Point in world coordinates to update from.
     */
    @Override
    public void updateFromPoint(Point2D point) {
      
        Point2D polarPoint = worldToPolar(point);
        updateFromScreen(polarPoint.getY());
        
        
    }
    
    /**
     * Called by the display to do a hit test on the color band.
     * @param point  The point in world coordinates.
     * @return True if the ColorBand contains the Point, false otherwise.
     */
    @Override
    public boolean containsPoint(Point2D point) {
        
        Point2D polarPoint = worldToPolar(point);
        
        return containsPolar(polarPoint.getX(), polarPoint.getY());
        
    }
    
    
    /**
     * Convert a world point to polar coordinates (in world space).
     * The point returned represents polar coordinates (x=distance, y=angle);
     *
     * @param mouseX World x coordinate.
     * @param mouseY World y coordinate (mouse) y point.
     * @return A point representing polar coordinates with the distance stored in x, and the angle in radians stored in y.
     */
    private Point2D worldToPolar(Point2D point) {

  
        double theta = Util.anglePointPoint(_center, point);

        //normalize theta to positive value
        if (theta < 0) {
            theta += Math.PI * 2;
        }

        double dist = _center.distance(point);

        return new Point2D.Double(dist, theta);
    }    
    
    
    
    
    
    
    
    
    
    
    
    
//</editor-fold>

}
