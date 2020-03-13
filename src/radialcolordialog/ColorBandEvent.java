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

/**
 * This is the event class for ColorBand change events.  This packs all the 
 * information used in the change event.
 * @author Tom
 */
public class ColorBandEvent {
    
    private ColorBand _colorBand;
    private TupleType _tupleType;
    private Channel _channel;
    private double _value;
    
    /**
     * Initialize a new event object from a COlorBand.
     * @param colorBand The ColorBand to create event from.
     */
    public ColorBandEvent(ColorBand colorBand) {
        
        _colorBand = colorBand;
        _tupleType = _colorBand.getTupleType();
        _channel =_colorBand.getChannel();
        _value = _colorBand.getValue();
        
    }
    
    /**
     * Get the value of the event ColorBand.
     * This will be in the range of 0.0 to 1.0
     * @return The value of the event ColorBand in range of 0.0 to 1.0;
     */
    public double getValue() {
        return _value;
    }    
    
    
    /**
     * Get the channel of the event ColorBand.
     * @return The channel that the event ColorBand controls
     */
    public Channel getChannel() {
        return _channel;
    }
    
    /**
     * Get the tuple type of the event colorBand (RGB, or HSB).
     * @return The tuple type of the event ColorBand.
     */
    public TupleType getTupleType() {
        return _tupleType;
    }    
    
    
    
}
