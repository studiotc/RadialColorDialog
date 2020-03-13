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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * This is  a color selection dialog box where the sliders are arc shaped bands with the exception of the alpha slider.
 * RGB Color values are presented as arcs on the outer band, while Hue, Saturation, and Brightness are given full circular arcs.
 * There is a preview "swatch" in the center of the selection bands.  
 * 
 * This was created as an alternate presentation for color selection instead of using
 * more traditional interfaces that rely on linear sliders in rows.  It was also intended to
 * allow for more intuitive color mixing by presenting the RGB and HSB models together.
 * 
 * @author Tom
 */
public class RadialColorDialog extends JDialog implements DisplayListener, ControlListener {

    private Color _dialogColor;
    
    private boolean _okSelected;
    
    private DisplayPanel _display;
    
    private ControlPanel _controls;
    
    private JFrame _owner;
    
    /**
     * Used for Development and Testing.
     * @param args the command line arguments (ignored).
     */
    public static void main(String[] args) {
            java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               RadialColorDialog rcd = new RadialColorDialog(null);
               Color editColor = new Color(255,0,128,128);
               boolean ok = rcd.showDialog(editColor, "Select Color");
              
               if(ok) {
                   Color color = rcd.getColor();
                   System.out.println(">>Dialog selected color: " + color);
               } else {
                   System.out.println(">>Dialog Canceled.");
               }
               
               
               rcd.dispose();
            }
        });
    }
    
    /**
     * Construct the dialog window and set the owner of the dialog.
     * @param frame The modal owner of the dialog.
     */
    public RadialColorDialog(JFrame frame) {
        super(frame, true);
  
        _okSelected = false;
        _dialogColor = Color.BLACK;
        
        _owner = frame;
        
        init();
    }
    

    /**
     * Initialize the dialog.
     * 
     */
    private void init() {
        this.setTitle("Select Color");
        
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE); 


        BorderLayout bLayout = new BorderLayout();
        this.setLayout(bLayout);
        
        _display = new DisplayPanel(this);
        
        _controls = new ControlPanel(this);
        

            
        this.add(_display, BorderLayout.CENTER);
        
        this.add(_controls, BorderLayout.LINE_END);
        
        
        this.pack();
 
        this.setResizable(false);
        
    }//end init
    
    
    /**
     * Show the dialog with the specified color..
     * @param color The Color to load in the dialog.
     * @param title  The title for the dialog.
     * @return True if the user selected a new color, false if the dialog was canceled.
     */
    public boolean showDialog(Color color, String title) {
        
        //reset flag
        _okSelected = false;
        
        this.setTitle(title);
        
        //store the color
        _dialogColor = color;
        
        _display.loadColor(color);
        
        _controls.loadInitialColor(color);
        
        //resolve the position on screen to display at
        resolvePosition();
        
        this.setVisible(true);
        
       return _okSelected;
        
    }
    
    /**
     * Resolve the dialog position.  If the dialog has an owner frame
     * then it will be centered on that frame.
     */
    private void resolvePosition() {
        
        
        //set elative location if not null
        if(_owner != null) {
            
            Rectangle fb = _owner.getBounds();
            Rectangle cb = this.getBounds();
            
            double fcx  = fb.getCenterX();
            double fcy  = fb.getCenterY();
 
            double cw = cb.getWidth() / 2;
            double ch = cb.getHeight() / 2;
            
            int x = (int)Math.round(fcx - cw );
            int y = (int)Math.round(fcy - ch);            
            
            this.setLocation(x,y);
           
        }       
        
        
        
    }
    
    
  
    /**
     * Retrieves the color from the dialog.  This is the selected
     * color when the dialog returns ok.
     * @return The color selected in the dialog.
     */
    public Color getColor() {
        
        return _dialogColor;
        
    }

//<editor-fold defaultstate="collapsed" desc="Display Listener Section">

    /**
     * Called when a color is updated from the slides in the display panel.
     * @param rgb The tuple containing the RGB values.
     * @param hsb The tuple containing the HSB values.
     * @param alpha The Alpha value.
     */
    @Override
    public void colorUpdatedFromSliders(ColorTuple rgb, ColorTuple hsb, int alpha) {
        
        
        //set the current dialog color
        Color rgbColor = rgb.getRGB();
        _dialogColor = Util.composeColorWithAlpha(rgbColor, alpha);
        
        //update control panel
        _controls.updateFromDisplay(rgb, hsb, alpha);
        
    }
    
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Controls Listener Section">
    
    /**
     * Called when a color is updated from the control panel.
     * 
     */
    @Override
    public void colorUpdatedFromInputs(ColorTuple rgb, ColorTuple hsb, int alpha) {
        
        //set the current dialog color
        Color rgbColor = rgb.getRGB();
        _dialogColor = Util.composeColorWithAlpha(rgbColor, alpha);
        
        //update teh display
        _display.updateFromControls(rgb,hsb,alpha);
        
    }
    
    /**
     * Called to close the dialog from the controls where 
     * the ok/cancel buttons are.
     * @param ok True if ok is selected, false if cancel is selected.
     */
    @Override
    public void closeDialog(boolean ok) {
        
        _okSelected = ok;
        this.setVisible(false);
        
    }
    
    
    
//</editor-fold>
    
    
   
    
    
    
}
