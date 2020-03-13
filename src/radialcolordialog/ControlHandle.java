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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 *  This a class for the graphic representation of control handles used by the sliders.
 *  This keeps the drawing routine for the handles in one place to ensure visual continuity.
 * @author Tom
 */
public class ControlHandle {
    //length step
    private static int L_STEP = 8;
    //width step
    private static int W_STEP = 5;
    
    private Path2D _handlePath;
    
    private BasicStroke _stroke;
    private Color _colorFill;
    private Color _colorStroke;
    
    /**
     * Construct the ControlHandle from the base point and horizontal specification.
     * Use horizontal mode for radial controls, and vertical (false) for horizontal sliders.
     * @param point  The base point of the handle.
     * @param horizontal True if the layout of handle is horizontal, false for vertical layout.
     */
    public ControlHandle(Point2D point, boolean horizontal) {
        
        _handlePath = new Path2D.Double();
        
        _stroke = new BasicStroke(1.0f);
        _colorFill = Color.WHITE;
        _colorStroke = Color.BLACK;
        
        //layout the handle
        layoutHandle(point, horizontal);
    }
    
    /**
     * Layout the handle either vertical or horizontal.
     * @param point  The base Point of the handle.
     * @param horizontal True to layout on the horizontal axis (for rotation), false to layout vertically (linear slider).
     *     
     */
    private void layoutHandle(Point2D point, boolean horizontal) {
        
        _handlePath.reset();
        double x = point.getX();
        double y = point.getY();
        
        double xs = L_STEP;
        double ys = W_STEP;
        

        if(horizontal) {
            
            xs = L_STEP;
            ys = W_STEP;            
            
            //layout left to right - arrow points to 0,0
            _handlePath.moveTo(x, y);
            _handlePath.lineTo(x + xs, y - ys);
            _handlePath.lineTo(x + xs + xs, y - ys);
            _handlePath.lineTo(x + xs + xs, y + ys);
            _handlePath.lineTo(x + xs, y + ys);
            _handlePath.lineTo(x, y);             
            
            
        } else {
            
            xs = W_STEP;
            ys = L_STEP;            
            
            //layout 
            _handlePath.moveTo(x, y);
            _handlePath.lineTo(x + xs, y + ys);
            _handlePath.lineTo(x + xs , y + ys + ys);
            _handlePath.lineTo(x - xs , y + ys + ys);
            _handlePath.lineTo(x - xs, y + ys);
            _handlePath.lineTo(x, y);            
            
            
        }
         
    }
    
    /**
     * Render the ControlHandle.
     * @param g2 The graphics object to render to.
     */
   public void render(Graphics2D g2) {
       
         //setup for fill
         g2.setColor(_colorFill);
         //fill the path
         g2.fill(_handlePath);
         
         //setup for outline
         g2.setStroke(_stroke);
         g2.setColor(_colorStroke);
         //stroke the path
         g2.draw(_handlePath);
       
       
   }
    
}
