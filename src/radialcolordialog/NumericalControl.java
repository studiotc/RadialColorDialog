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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This is the class for numerical controls for a Color Channel (r,g,b, etc.).
 * The control is composed of a JLabel and JSpinner to display and edit
 * the color channel value.  The editor is designed for integer values with a step of 1.
 * @author Tom
 */
public class NumericalControl extends JPanel implements ChangeListener {
    
    private final static int VAL_MIN = 0;
    private final static int VAL_MAX  = 255;    
    
    private TupleType _tupleType;
    private Channel _channel;
    
    private int _value;
    
    private String _name;
    private JLabel _label;
    private JSpinner _spinner;
    private SpinnerNumberModel _numberModel;
    
    private NumericalControlListener _listener;
    
    private boolean _internUpdate;
    
    /**
     * Construct the control and attach the listener.
     * @param listener  The listener for change notification.
     * @param type  The type of color this control's channel is a member of.
     * @param channel  The color channel this control is responsible for.
     * @param name  The name of the channel (for display).
     * @param min  The minimum value for the editing range.
     * @param max  The maximum value for the editing range.
     */
    public NumericalControl(NumericalControlListener listener, TupleType type, Channel channel, String name, int min, int max) {
        super();
        
        _listener = listener;
        _tupleType = type;
        _channel = channel;
        _name = name;
        
        _value = 0;
        
        //number model for spinner
        _numberModel = new SpinnerNumberModel(new Integer(min),new Integer(min),new Integer(max), new Integer(1));
        
        _internUpdate = false;
        
        init();
        
    }
    
    /**
     * Initialize the control and components.
     */
    private void init() {
        
    
        Dimension panelDim = new Dimension(160,24);
        
        this.setPreferredSize(panelDim);
        this.setMinimumSize(panelDim);
        this.setMaximumSize(panelDim);
        
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        

        BoxLayout bl = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(bl);
        
        _numberModel.setValue(_value);
        
        _label = new JLabel(_name);
        _spinner = new JSpinner(_numberModel);
        _spinner.addChangeListener(this);
        
        Dimension spinDim = new Dimension(80,28);
        
        _label.setAlignmentX(LEFT_ALIGNMENT);
        _spinner.setAlignmentX(RIGHT_ALIGNMENT);
        
        _spinner.setPreferredSize(spinDim);
        _spinner.setMinimumSize(spinDim);
        _spinner.setMaximumSize(spinDim);        
        
        
        this.add(_label);
        this.add(Box.createHorizontalGlue());
        this.add(_spinner);
        
    }
    
    /**
     * Get the value of the control.  This is the raw integer value.
     * @return The value of the control.
     */
    public int getValue() {
        return (int)_numberModel.getValue();
    }

    
    /**
     * Get the scale of the current value based on the min, max, and current value of the control.
     * This transforms the value into a decimal of 0.0 to 1.0. 
     * @return The scale of the value form 0.0 to 1.0.
     */
    public float getValueScale() {
        
        
        //cast from int and into a float...
        float max = (int)_numberModel.getMaximum();
        float min = (int)_numberModel.getMinimum();
        float val = (int)_numberModel.getValue();
        
        //int rng = max - min;
        
        float rng = max - min;
        float scale = 0;
        
        //shouldn't happen, but do check anyways.. x/0
        if(rng != 0.0f) {
            scale = val / rng;
        }
        
        return scale;
        
    }
    
    
    /**
     * ChangeListener event call.  
     * This is called when the spinner changes.
     * @param e The change event.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
       
        
        //check for internal update when changed from outside
        if(!_internUpdate) {
            
            _internUpdate = true;
            
            int spinnerValue = (int)_numberModel.getValue();
            
            _value = spinnerValue;
            
            //notify listenr
            onChange();
        
            _internUpdate = false;
            
        } else {
            //System.out.println("state changed rejected in numeric control");
        }
        
        
    }
    
    /**
     * Update the spinner value from a ColorTuple.
     * @param tuple The tuple to update from.
     */
    public void updateFromTuple(ColorTuple tuple) {
        
        int max = (int)_numberModel.getMaximum();
        int min = (int)_numberModel.getMinimum();
        
        int rng = max - min;
        
        int value = tuple.getChannelAsInt(_channel, rng);
        
        //not really needed here, but in principle...
        value += min;
        
        _internUpdate = true;
        _numberModel.setValue(value);
        _internUpdate = false;
        
        
    }
    
    /**
     * Update the control from the slider in the display panel.
     * @param value The new value for the spinner.
     */
    public void updateFromInteger(int value) {
        
        
        _value = value;
       
        
        if(!_internUpdate) {
            
            _internUpdate = true;
            _numberModel.setValue(value);
            _internUpdate = false;
            
        }        
        
        
        
    }
    
    /**
     * Call when the values changes to notify listener.
     */
    private void onChange() {
        
        _listener.valueChanged(_tupleType, _channel, _value);
        
    }
    
    
}
