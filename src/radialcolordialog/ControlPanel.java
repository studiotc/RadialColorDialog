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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *  The Control panel manages all the numeric inputs for the color channels.  It also hosts
 * the Ok and Cancel buttons.
 * @author Tom
 */
public class ControlPanel extends JPanel implements NumericalControlListener {

    private NumericalControl _rChannelControl;
    private NumericalControl _gChannelControl;
    private NumericalControl _bChannelControl;

    private NumericalControl _hChannelControl;
    private NumericalControl _sChannelControl;
    private NumericalControl _vChannelControl;

    private NumericalControl _aChannelControl;

    private ControlListener _listener;

    private boolean _eventEnabled;

    public ControlPanel(ControlListener listener) {
        super();

        _listener = listener;

        _rChannelControl = new NumericalControl(this, TupleType.RGB, Channel.ChannelA, "Red", 0, 255);
        _gChannelControl = new NumericalControl(this, TupleType.RGB, Channel.ChannelB, "Green", 0, 255);
        _bChannelControl = new NumericalControl(this, TupleType.RGB, Channel.ChannelC, "Blue", 0, 255);

        _hChannelControl = new NumericalControl(this, TupleType.HSB, Channel.ChannelA, "Hue", 0, 360);
        _sChannelControl = new NumericalControl(this, TupleType.HSB, Channel.ChannelB, "Saturation", 0, 100);
        _vChannelControl = new NumericalControl(this, TupleType.HSB, Channel.ChannelC, "Brightness", 0, 100);

        _aChannelControl = new NumericalControl(this, TupleType.RGB, Channel.ChannelA, "Alpha", 0, 255);

        _eventEnabled = true;

        init();

    }

    /**
     * Initialize the dialog control panel.
     */
    private void init() {

        BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(bl);

        this.add(_rChannelControl);
//        this.add(Box.createVerticalGlue());
        this.add(_gChannelControl);
//        this.add(Box.createVerticalGlue());
        this.add(_bChannelControl);
        this.add(Box.createVerticalStrut(20));

        this.add(_hChannelControl);
//        this.add(Box.createVerticalGlue());
        this.add(_sChannelControl);
//        this.add(Box.createVerticalGlue());
        this.add(_vChannelControl);
        this.add(Box.createVerticalStrut(20));
        this.add(_aChannelControl);

        /**
         * * Ok Cancel Buttons **
         */
        Dimension bttnPrefSize = new Dimension(160, 24);
        Dimension bttnMinSize = new Dimension(80, 24);
        Dimension bttnMaxSize = new Dimension(160, 24);

        JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(bttnPrefSize);
        okButton.setMinimumSize(bttnMinSize);
        okButton.setMaximumSize(bttnMaxSize);
        okButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        okButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        ActionListener okAL = (ActionEvent ev) -> {
            closeDialog(true);
        };
        okButton.addActionListener(okAL);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(bttnPrefSize);
        cancelButton.setMinimumSize(bttnMinSize);
        cancelButton.setMaximumSize(bttnMaxSize);
        cancelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        ActionListener cancelAL = (ActionEvent ev) -> {
            closeDialog(false);
        };
        cancelButton.addActionListener(cancelAL);

        //add to panel
        this.add(Box.createVerticalGlue());
        this.add(okButton);
        this.add(cancelButton);

    }

    /**
     * Get the insets for padding around frame.
     *
     * @return The insets for this panel.
     */
    @Override
    public Insets getInsets() {
        return new Insets(10, 10, 10, 10);
    }

    /**
     * Called by the NumericalControl when a spinner has changed.
     *
     * @param type The color type of the control.
     * @param channel The channel of the control.
     * @param value The value of the control.
     */
    @Override
    public void valueChanged(TupleType type, Channel channel, int value) {

        //suppress event when disabled.
        //this suppresss events when updating one set of controls to another
        //otherwise you will get endless loop....
        if (!_eventEnabled) {
            return;
        }

       
        //check the type and update accordingly
        //rgb updates hsb / hsb updates rgb
        switch (type) {

            case RGB:

                int cr = _rChannelControl.getValue();
                int cg = _gChannelControl.getValue();
                int cb = _bChannelControl.getValue();
                
                Color rgbColor = new Color(cr,cg,cb);
                
                //update the HSB controls
                updateHSBControls(rgbColor);
                
                break;

            case HSB:

                float sh = _hChannelControl.getValueScale();
                float ss = _sChannelControl.getValueScale();
                float sv = _vChannelControl.getValueScale();

                //pack values to tuple
                ColorTuple hsbTuple = new ColorTuple(sh, ss, sv);
                //pull the rgb color from the tuple
                Color rgbColorFromHSB = hsbTuple.getColor(TupleType.HSB);
                //update the RGB controls
                updateRGBControls(rgbColorFromHSB);

                break;

            case Alpha:
                
                //noting to do here... No cross talk with alpha
                //onChange() will pick up the change

                break;

        }

        onChange();

    }

    /**
     * Gather the input values and notify listener.
     * Make sure all controls are updated before calling this.
     */
    private void onChange() {

        float sr = _rChannelControl.getValueScale();
        float sg = _gChannelControl.getValueScale();
        float sb = _bChannelControl.getValueScale();

        float sh = _hChannelControl.getValueScale();
        float ss = _sChannelControl.getValueScale();
        float sv = _vChannelControl.getValueScale();
        
        int alpha = _aChannelControl.getValue();

        ColorTuple rgb = new ColorTuple(sr, sg, sb);
        ColorTuple hsb = new ColorTuple(sh, ss, sv);

        //notify the listener
        _listener.colorUpdatedFromInputs(rgb, hsb, alpha);

    }

    /**
     * Ok/Cancel button handler. Notify the ControlListener.
     *
     * @param ok True if ok selected, false if cancel selected.
     */
    private void closeDialog(boolean ok) {

        _listener.closeDialog(ok);

    }

    /**
     * Update the controls from the display panel.
     * This disables events and sets the controls from teh provided colors and alpha.
     *
     * @param rgb The RGB tuple to update from.
     * @param hsb The HSB tuple to update from.
     * @param alpha The alpha value to update from.
     */
    public void updateFromDisplay(ColorTuple rgb, ColorTuple hsb, int alpha) {

        

        _eventEnabled = false;

        _rChannelControl.updateFromTuple(rgb);
        _gChannelControl.updateFromTuple(rgb);
        _bChannelControl.updateFromTuple(rgb);

        _hChannelControl.updateFromTuple(hsb);
        _sChannelControl.updateFromTuple(hsb);
        _vChannelControl.updateFromTuple(hsb);

        _aChannelControl.updateFromInteger(alpha);

        _eventEnabled = true;

    }

    /**
     * Load the initial Dialog Color.
     * 
     * @param color The color loaded into the dialog.
     */
    public void loadInitialColor(Color color) {

 
        _eventEnabled = false;
        int a = color.getAlpha();
        _aChannelControl.updateFromInteger(a);
        _eventEnabled = true;

        updateRGBControls(color);
        updateHSBControls(color);

    }

    /**
     * Update the HSB Controls from an RGB color.
     * This is used to update the controls when an RGB color changes.
     * The RGB color will be converted to HSB and then the controls are updated.
     * @param color The Color to update the controls from.
     */
    private void updateHSBControls(Color color) {


        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        float[] vals;
        vals = Color.RGBtoHSB(r, g, b, null);

        float ht = vals[0];
        float st = vals[1];
        float bt = vals[2];

        ColorTuple hsb = new ColorTuple(ht, st, bt);

        _eventEnabled = false;
        
        _hChannelControl.updateFromTuple(hsb);
        _sChannelControl.updateFromTuple(hsb);
        _vChannelControl.updateFromTuple(hsb);
        
        _eventEnabled = true;

    }
    

    
    /**
     * Update the RGB Controls from an RGB color.
     * This is used to update the controls when an HSB color changes.
     * @param color The color to update the controls from.
     */
    private void updateRGBControls(Color color) {
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        _eventEnabled = false;
        
        _rChannelControl.updateFromInteger(r);
        _gChannelControl.updateFromInteger(g);
        _bChannelControl.updateFromInteger(b);        
        
        _eventEnabled = true;
        
        
        
    }
    

}
