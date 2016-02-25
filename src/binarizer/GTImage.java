/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarizer;

import diuf.diva.dia.ms.util.Image;
import diuf.diva.dia.ms.util.ImageDataBlock;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;

/**
 *
 * @author ms
 */
public class GTImage {
    public ImageDataBlock pixels;
    int[][] classNum;
    
    public GTImage(String imName, String gtName) throws IOException {
        try {
            pixels = new ImageDataBlock(new Image(imName));
        } catch (Exception ex) {
            throw new Error("Error while loading "+imName);
        }
        BufferedImage bi = ImageIO.read(new File(gtName));
        
        if (bi.getWidth()!=pixels.getWidth() || bi.getHeight()!=pixels.getHeight()) {
            throw new Error("mismatching dimensions, check dataset order");
        }
        
        classNum = new int[bi.getWidth()][bi.getHeight()];
        int minNum = Integer.MAX_VALUE;
        int maxNum = Integer.MIN_VALUE;
        Set<Integer> values = new TreeSet();
        for (int i=0; i<bi.getWidth(); i++) {
            for (int j=0; j<bi.getHeight(); j++) {
                classNum[i][j] = bi.getRGB(i, j) & 0xFFFFFF;
                
                if (classNum[i][j]<minNum) {
                    minNum = classNum[i][j];
                }
                if (classNum[i][j]>maxNum) {
                    maxNum = classNum[i][j];
                }
                values.add(classNum[i][j]);
            }
        }
        System.out.println("Loaded an image, gt in ["+minNum+" "+maxNum+"]");
        System.out.print("\t");
        for (Integer i : values) {
            System.out.print(i+" ");
        }
        System.out.println();
    }
}
