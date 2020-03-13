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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *  The DisplayPanel manages all the color channel sliders (ColorBands and AlphaSlider),
 *  and is the interactive area for color selection.
 * @author Tom
 */
public class DisplayPanel extends JPanel implements MouseMotionListener, MouseListener, ColorBandListener, AlphaSliderListener {

    private static double CEN_RAD = 80;
    private static final int PANEL_WIDTH = 460;
    private static final int ALPHA_HEIGHT = 32;

    private DisplayListener _listener;
    
    private ColorBand _redBand;
    private ColorBand _greenBand;
    private ColorBand _blueBand;

    private ColorBand _hueBand;
    private ColorBand _saturationBand;
    private ColorBand _brightnessBand;
    
    
    
    private ColorBand _activeBand;
    
    
    private AlphaSlider _alphaSlider;
    private int _currentAlpha;
    
    
    private InteractiveSlider _activeSlider;
    

    private Arc2D _arcNE;
    private Arc2D _arcNW;
    private Arc2D _arcSW;
    private Arc2D _arcSE;

//    private ArrayList<ColorBand> _bands;
    
    private ArrayList<InteractiveSlider> _sliders;

    private AffineTransform _trans;

    private Point2D _center;
    
    private Color _currentColor;
    private Color _dynamicColor;
    
    
    private BufferedImage _backgroundImage;

    public DisplayPanel(DisplayListener listener) {
        super();

        _listener = listener;
        
        _currentColor = Color.BLACK;
        _dynamicColor = Color.BLACK;
        
        
        
        _currentAlpha = 255;
        
        init();
        
        _backgroundImage = null;
        initBackground();

    }

    private void init() {

//        _bands = new ArrayList();
        
        _sliders = new ArrayList();

        int width = PANEL_WIDTH; //460;
        int height = width + ALPHA_HEIGHT; //+ 32;
        double cx = width / 2;
        double cy = width / 2;
        _center = new Point2D.Double(cx, cy + 32);

        Dimension sizeD = new Dimension(width, height);
        this.setMinimumSize(sizeD);
        this.setPreferredSize(sizeD);

        //setup transform
        _trans = new AffineTransform();
        _trans.translate(0, height);
        _trans.scale(1.0, -1.0);

        int bandWidth = 24;

//        int space = 10;
//        int s3 = space * 3;
//        int spaceHalf = space / 2;
//        int rgbSpan = 360 / 3 - space;

        
        //original layout 100b, 130s, 160h, 190(rgb)
        _hueBand = new ColorBand(this, TupleType.HSB, Channel.ChannelA, _center, 160, 0, Math.PI * 2, bandWidth);
        _hueBand.setColors(new ColorTuple(0, 1, 1), new ColorTuple(1, 1, 1));
        
        _saturationBand = new ColorBand(this, TupleType.HSB, Channel.ChannelB, _center, 130, 0, Math.PI * 2, bandWidth);
        _saturationBand.setColors(new ColorTuple(1, 0, 1), new ColorTuple(1, 1, 1));
             
        _brightnessBand = new ColorBand(this, TupleType.HSB, Channel.ChannelC, _center, 100, 0, Math.PI * 2, bandWidth);
        _brightnessBand.setColors(new ColorTuple(1, 1, 0), new ColorTuple(1, 1, 1));
        

        //sweeps
        double rs = Util.dToR(-55);
        double re = Util.dToR(55);
        double gs = Util.dToR(65);
        double ge = Util.dToR(175);
        double bs = Util.dToR(185);
        double be = Util.dToR(295);

        _redBand = new ColorBand(this, TupleType.RGB, Channel.ChannelA ,_center, 190, rs, re, bandWidth);
        _redBand.setColors(new ColorTuple(0, 0, 0), new ColorTuple(1, 0, 0));

        _greenBand = new ColorBand(this, TupleType.RGB, Channel.ChannelB, _center, 190, gs, ge, bandWidth);
        _greenBand.setColors(new ColorTuple(0, 0, 0), new ColorTuple(0, 1, 0));

        _blueBand = new ColorBand(this, TupleType.RGB, Channel.ChannelC, _center, 190, bs, be, bandWidth);
        _blueBand.setColors(new ColorTuple(0, 0, 0), new ColorTuple(0, 0, 1));

        /**
         * * Center Pies **
         */
        double pr = CEN_RAD;
        double pwh = pr * 2;
        double pcx = _center.getX() - pr;
        double pcy = _center.getY() - pr;

        _arcNE = new Arc2D.Double(pcx, pcy, pwh, pwh, 0, 90, Arc2D.PIE);
        _arcNW = new Arc2D.Double(pcx, pcy, pwh, pwh, 90, 90, Arc2D.PIE);
        _arcSW = new Arc2D.Double(pcx, pcy, pwh, pwh, 180, 90, Arc2D.PIE);
        _arcSE = new Arc2D.Double(pcx, pcy, pwh, pwh, 270, 90, Arc2D.PIE);

        /*** Alpha SLider ***/
        int a = 40;
        int ws = width - a - a;
        _alphaSlider = new AlphaSlider(new Rectangle(a,12,ws,24), Color.BLACK, this);
        _alphaSlider.setAlpha(_currentAlpha);
        _activeSlider = null;         
        
        
        
        /**
         * pack sliders list
         */
        
        _sliders.add(_redBand);
        _sliders.add(_greenBand);
        _sliders.add(_blueBand);

        _sliders.add(_brightnessBand);
        _sliders.add(_saturationBand);
        _sliders.add(_hueBand);   
        
        _sliders.add(_alphaSlider);

        //active band (selected)
        _activeBand = null;
        
     
        

        /**
         * * Establish handles **
         */
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

    }
    
    /**
     * Create the background image.  This is a 
     * solid background with the center color circle cut away
     * to expose a checkerboard underneath.  This shows through
     * when alpha is applied to the current color.
     */
    private void initBackground() {
        
        Dimension psize = this.getPreferredSize();
        Color bgColor = this.getBackground();
        
 
        //get the cherboard
        BufferedImage bi = CheckerBoard.makeImage(psize.width, psize.height, 10);
        
        double cx = _center.getX();
        double cy = _center.getY();
        
        //construct center ellipse
        Ellipse2D ellipse = new Ellipse2D.Double();
        ellipse.setFrameFromCenter(cx, cy, cx - CEN_RAD, cy - CEN_RAD);     
        
        //construct the bounds
        Rectangle bounds = new Rectangle(0, 0, psize.width, psize.height);
        
        //ellipse area
        Area ellArea = new Area(ellipse);
        //bounds area
        Area bndsArea = new Area(bounds);
        
        //remove ellipse
        bndsArea.subtract(ellArea);
        
        //get the graphics object
        //Graphics g = bi.getGraphics();
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        
        //apply rendering hints
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);        
        
        //set color
        g2.setColor(bgColor);
        
        //apply current transform
        g2.transform(_trans);
        
        //paint over with the bounds area.
        g2.fill(bndsArea);
        

        //dispose graphics
        g2.dispose();
        
        _backgroundImage = bi;
        
    }
    
    

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //if(true) return;
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        Rectangle bounds = this.getBounds();

        Color bgc = this.getBackground();
        


        g2.drawImage(_backgroundImage, bounds.x, bounds.y, this);
        
//        g2.setPaint(bgc);
//        g2.fill(bounds);
         
         /*** transform to world coordinates ***/
        g2.transform(_trans);


        /*** Render the color bands ***/

        _brightnessBand.render(g2);
        _saturationBand.render(g2);
        _hueBand.render(g2);

        _redBand.render(g2);
        _greenBand.render(g2);
        _blueBand.render(g2);

        
        _alphaSlider.render(g2);
        
        /***  Render the center datum ***/
        g2.setPaint(_dynamicColor);

        g2.fill(_arcNE);
        g2.fill(_arcSW);

        g2.setPaint(_currentColor);

        g2.fill(_arcNW);
        g2.fill(_arcSE);        
        
        
        /*** Render anti-alias masking ***/
         g2.setStroke(new BasicStroke(2.0f));
         g2.setColor( bgc );
       

        renderMaskOutline(g2, -1, CEN_RAD -1);
        renderMaskOutline(g2, _brightnessBand.getInnerRadius(), _brightnessBand.getOuterRadius());
        renderMaskOutline(g2, _saturationBand.getInnerRadius(), _saturationBand.getOuterRadius());
        renderMaskOutline(g2, _hueBand.getInnerRadius(), _hueBand.getOuterRadius());
         //render only one of the color bands since it renders a full circle and not an arc
        renderMaskOutline(g2, _redBand.getInnerRadius(), _redBand.getOuterRadius());
        
         
         /***  Render the color band handles ***/
        
        _brightnessBand.renderHandle(g2);
        _saturationBand.renderHandle(g2);
        _hueBand.renderHandle(g2);

        _redBand.renderHandle(g2);
        _greenBand.renderHandle(g2);
        _blueBand.renderHandle(g2);         


    }

    /**
     * Render the Ellipse Out line to mask the edges of the color band.
     * This is "cheat" to make the UI look nice instead of doing proper anti-aliasing...
     * @param g2  The graphic object to render to.
     * @param radInner  The inner radius of the color band. If this is radius < 0, it will not be drawn.
     * @param radOuter  The outer radius of the color band.  If this is radius < 0, it will not be drawn.
     */
    private void renderMaskOutline(Graphics2D g2, double radInner, double radOuter) {

        double cx = _center.getX();
        double cy = _center.getY();
        
        //mechanism to suppress second mask..
        if(radInner > 0) {
            double rIn = radInner - 1;

            Ellipse2D inEll = new Ellipse2D.Double();
            inEll.setFrameFromCenter(cx, cy, cx - rIn, cy - rIn);
            g2.draw(inEll);            
            
        }

 
        if(radOuter > 0) {
            double rOut = radOuter + 1;
            Ellipse2D outEll = new Ellipse2D.Double();
            outEll.setFrameFromCenter(cx, cy, cx - rOut, cy - rOut);
            g2.draw(outEll);            
        }


    }

//<editor-fold defaultstate="collapsed" desc="Mouse Handling Events">
    /**
     * Convert a mouse point to polar coordinates in world space.
     * The point returned represents polar coordinates (x=distance, y=angle);
     *
     * @param mouseX Screen (mouse) x point.
     * @param mouseY Screen (mouse) y point.
     * @return A point representing polar coordinates with the distance stored in x, and the angle  in radians stored in y.
     */
    private Point2D mouseToPolar(int mouseX, int mouseY) {

        Point2D mPoint = new Point2D.Double(mouseX, mouseY);

        Point2D transPoint = _trans.transform(mPoint, null);

        double theta = Util.anglePointPoint(_center, transPoint);

        //normalize theta to positive value
        if (theta < 0) {
            theta += Math.PI * 2;
        }

        double dist = _center.distance(mPoint);

        return new Point2D.Double(dist, theta);
    }
    
    /**
     * Transform a point from screen to world.  This runs
     * the mouse coordinates through the display's affine transform.
     * @param mouseX  The mouse x coordinate.
     * @param mouseY  The mouse y coordinate.
     * @return The transformed point in world coordinates.
     */
    private Point2D screenToWorld(int mouseX, int mouseY) {
        
        Point2D mPoint = new Point2D.Double(mouseX, mouseY);
        Point2D transPoint = _trans.transform(mPoint, null);        
        
        return transPoint;
        
    }

    /**
     * Check the active color band and update it accordingly.
     * @param e The mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        if(_activeSlider != null) {
            
            Point2D worldPoint = screenToWorld(e.getX(), e.getY());
            _activeSlider.updateFromPoint(worldPoint);
            
            this.repaint();
        }
        
        
    }

    
  
    
    
    /**
     * Not Used.
     * @param e The mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Not Used.
     * @param e The mouse event.
     */    
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Check for mouse down on color band.
     * @param e MouseEvent.
     */    
    @Override
    public void mousePressed(MouseEvent e) {

 
        
        Point2D worldPoint = screenToWorld(e.getX(), e.getY());

        //do hit test
        for (InteractiveSlider sld : _sliders) {

            if (sld.containsPoint(worldPoint)) {
                //make the band active
                _activeSlider = sld;
                //update from point
                sld.updateFromPoint(worldPoint);
                
                
                this.repaint();
                
            } else {
                //System.out.println("no band...");
            }

        }

    }

    
 
    
    
    /**
     * Check for mouse released - release the active band (set to null).
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        

        
        if(_activeBand != null) {
            
            //reset the active band - no selection
            _activeBand = null;            
            
            //set the current display color to the dynamic color
             _currentColor = _dynamicColor;
             this.repaint();
        }
        
        if(_activeSlider != null) {
            
            _activeSlider = null;
            _currentColor = _dynamicColor;
            this.repaint();
            
        }
        

    }

    /**
     * Not Used.
     * @param e The mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Not Used. 
     * @param e The mouse event. 
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

//</editor-fold>

    /**
     * ColorBand change event handler.  This is called when a Color band value changes.
     * @param e The ColorBand event.
     */
    @Override
    public void colorBandChanged(ColorBandEvent e) {
        
        //do something...bubble up...
        TupleType type = e.getTupleType();
        
        switch(type) {
            
            case RGB :
                
                float r = (float)_redBand.getValue();
                float g = (float)_greenBand.getValue();
                float b = (float)_blueBand.getValue();
                
 
                ColorTuple rgbct = new ColorTuple(r,g,b);
                _dynamicColor = rgbct.getColor(type);
                //apply the alpha
                _dynamicColor = applyCurrentAlpha(_dynamicColor);
                
                //update the colors
                updateRGBColors(rgbct);
                
                updateHSBValues(_dynamicColor);
                
                break;
                
            case HSB :
                
                float ah = (float)_hueBand.getValue();
                float bs = (float)_saturationBand.getValue();
                float cb = (float)_brightnessBand.getValue();
                
 
                ColorTuple hsbct = new ColorTuple(ah,bs,cb);
                _dynamicColor = hsbct.getColor(type); 
                //apply the alpha
                _dynamicColor = applyCurrentAlpha(_dynamicColor);
                
                //update the colors
                updateHSBColors(hsbct);
                
                updateRGBValues(_dynamicColor);
                
                break;
                
            case Alpha:
                
                //let onChange pick the change
                //otherwise, nothing to do here.
                
                break;
            
            
        }
        
        //update the alpha slide
        _alphaSlider.setColor(_dynamicColor);
        
        //update the controls
        onChange();
        
        
    }
    
    /**
     * Update the Red, Green, and BLue values from a color.
     * This updates the bands  values with a color generated externally.
     * @param color The color to update from
     */
    private void updateRGBValues(Color color) {
        
        float r = color.getRed();
        float g = color.getGreen();
        float b = color.getBlue();
        
        float rt = r / 255.0f;
        float gt = g / 255.0f;
        float bt = b / 255.0f;
        
        _redBand.update(rt);
        _greenBand.update(gt);
        _blueBand.update(bt);
        
        ColorTuple rgbCT = new ColorTuple(rt, gt, bt);
        updateRGBColors(rgbCT);
        
    }
    
    
    /**
     * Update the Red, Green, and Blue ColorBand bands colors.
     * @param tuple The ColorTuple to update the bands from.
     */
    private void updateRGBColors(ColorTuple tuple) {
        
        float r =tuple.getA();
        float g =tuple.getB();
        float b =tuple.getC();
        
        
        _redBand.setColors(new ColorTuple(0.0f, g, b), new ColorTuple(1.0f, g, b));
        
        _greenBand.setColors(new ColorTuple(r, 0.0f, b), new ColorTuple(r, 1.0f, b));
        
        _blueBand.setColors(new ColorTuple(r, g, 0.0f), new ColorTuple(r, g, 1.0f));
        
    }
    
    /**
     * Update the Hue, Saturation, and Brightness bands values.  This updates
     * the bands values with a color generated externally.
     * @param color The color to update the values
     */
    private void updateHSBValues(Color color) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        float[] vals;
        vals = Color.RGBtoHSB(r, g, b, null);
        
        float ht = vals[0];
        float st = vals[1];
        float bt = vals[2];
        
        _hueBand.update(ht);
        _saturationBand.update(st);
        _brightnessBand.update(bt);
        
        ColorTuple rgbCT = new ColorTuple(ht, st, bt);
        updateHSBColors(rgbCT);
        
    }    
    
    
    /**
     * Update the Hue, Saturation, and Brightness bands colors. 
     * @param tuple The ColorTuple to update the bands from.
     */
    private void updateHSBColors(ColorTuple tuple) {
        
        float h = tuple.getA();
        float s = tuple.getB();
        float b = tuple.getC();
        
        
        _hueBand.setColors(new ColorTuple(0.0f, s, b), new ColorTuple(1.0f, s, b));
        
        _saturationBand.setColors(new ColorTuple(h, 0.0f, b), new ColorTuple(h, 1.0f, b));
        
        _brightnessBand.setColors(new ColorTuple(h, s, 0.0f), new ColorTuple(h, s, 1.0f));
        
    }   
    
    


    /**
     * Called when the Alpha slider changes.
     * 
     */
    @Override
    public void alphaChanged(int alpha) {
       
        
        _currentAlpha = alpha;
        
        if(_activeSlider != null) {
            _dynamicColor = applyCurrentAlpha(_dynamicColor);
        } 
        
        onChange();
        
    }
    
    
    
    /**
     * Apply the current alpha value to a color.  This creates a new
     * color with the alpha applied.
     * @param color  The color to apply the alpha to.
     * @return The color with the alpha applied.
     */
    private Color applyCurrentAlpha(Color color) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        return new Color(r,g,b,_currentAlpha);
        
    }
    
    /**
     * Notify the listener that the color has changed.
     */
    private void onChange() {
        
       float red = (float)_redBand.getValue();
        float blu = (float)_blueBand.getValue();
        float grn = (float)_greenBand.getValue();
        
        float hue = (float)_hueBand.getValue();
        float sat = (float)_saturationBand.getValue();
        float brt = (float)_brightnessBand.getValue();
        
        ColorTuple rgbT = new ColorTuple(red,grn,blu);
        ColorTuple hsbT = new ColorTuple(hue,sat,brt);
        
        //notify listener
        _listener.colorUpdatedFromSliders(rgbT, hsbT, _currentAlpha);        
        
        
        
    }
    
    
    /**
     * Load a Color.  Loads a color into the display.
     * @param color The Color to load.
     */
    public void loadColor(Color color) {
        
        
        
        //set the colors
        _currentColor = color;
        _dynamicColor = color;
 
        //set the alpha
        _currentAlpha = color.getAlpha();        
        
        
        updateRGBValues(color);
        updateHSBValues(color);
        
        //update the alpha slider
        _alphaSlider.setAlpha(_currentAlpha);
        _alphaSlider.setColor(color);
        
        
    }
    
    
    /**
     * Update the display when a color has changed in the controls.
     * @param rgb The RGB tuple.
     * @param hsb The HSB tuple.
     * @param alpha THe alpha value.
     */
    public void updateFromControls(ColorTuple rgb, ColorTuple hsb, int alpha) {
        
        _currentAlpha = alpha;
//        _dynamicColor = applyCurrentAlpha(_dynamicColor);
        Color rgbColor = rgb.getColor(TupleType.RGB);
        rgbColor = applyCurrentAlpha(rgbColor);
        
        loadColor(rgbColor);
        
        this.repaint();
    }
    
}//end class
