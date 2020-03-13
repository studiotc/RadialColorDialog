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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *  This is a utility class to generate a checkerboard background image to use
 *  with display components that have alpha values.
 * @author Tom
 */
public class CheckerBoard {
    
    
    /**
     * Make a checker board background icon for display.
     * The width and height should be evenly divisible by the grid size.
     * @param width  The width of the CheckerBoard.
     * @param height The height of the CheckerBoard.
     * @param gridSize  The size of the grid.
     * @return The CheckerBoard image.
     */
    public final static BufferedImage makeImage(int width, int height, int gridSize) {

   
        int grid = gridSize;

        int w = width; 
        int h = height; 

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)bi.getGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);         
        
        //set background to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

 
        //number of rows and olumns
        int row = h / grid;
        int col = w / grid;
        

        //set the color to white
        g.setColor(Color.WHITE);

        
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                int x = j * grid; 
                int y = i * grid;
                
                if(i % 2 == 1 && j % 2 == 0) {
                    g.fillRect(x, y, grid, grid);
                 } else if (i % 2 == 0 && j % 2 == 1) {
                    g.fillRect(x, y, grid, grid); 
                 }
                
            }//end for col
  
        }//end for row        

        //dispose of graphics
        g.dispose();
        
        //init the icon
//        Icon icon = new ImageIcon(bi);
        
        return bi;

    }
    
    
    
    
}
