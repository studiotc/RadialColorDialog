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
 * This is a helper class for interpolating between two ColorTuples.
 * This is used by the ColorBand to interpolate colors when painting/rendering.
 * @author Tom
 */
public class ColorRange {
    
    private ColorTuple _cTupleStart;
    private ColorTuple _cTupleEnd;
    
    /**
     * Initialize a new ColorRange with default ColorTuples (where channel a = 1.0 to 0.0).
     */
    public ColorRange() {
        
       _cTupleStart = new ColorTuple(1,0,0);
       _cTupleEnd = new ColorTuple(0,0,0);        
        
        
    }
    
    /**
     * Set the start tuple of the range.
     * @param tuple The tuple to use as the start of the range.
     */
    public void setStart(ColorTuple tuple) {
        _cTupleStart= tuple;
    }
    
    
    /**
     * Set the end tuple of the range.
     * @param tuple The tuple to use as the end of the range.
     */    
   public void setEnd(ColorTuple tuple) {
        _cTupleEnd = tuple;
    }    
    
   /**
    * Get the tuple of the 't' value.  A 't' value of 0 will
    * return the start tuple, where 1 will return the end tuple.
    * Any other value will scale between the two.
    * @param t The 't' value or scale of the range (0.0 to 1.0);
    * @return The color tuple in the range based on 't';
    */
    public ColorTuple getTuple(float t) {
        
        float sa = _cTupleStart.getA();
        float sb = _cTupleStart.getB();
        float sc = _cTupleStart.getC();
 
        
        float ea = _cTupleEnd.getA();
        float eb = _cTupleEnd.getB();
        float ec = _cTupleEnd.getC();     

        
        
        float ca = sa + ((ea - sa) * t);
        float cb = sb + ((eb - sb) * t);
        float cc = sc + ((ec - sc) * t);

        
        return new ColorTuple(ca,cb,cc);
        
    }
    
    
}
