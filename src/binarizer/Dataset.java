/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ms
 */
public class Dataset implements Collection<GTImage> {
    public List<GTImage> images = new ArrayList();
    
    public Dataset(String imPath, String gtPath) throws IOException {
        this(imPath, gtPath, Integer.MAX_VALUE);
    }
    
    public Dataset(String imPath, String gtPath, int maxSize) throws IOException {
        File imFile = new File(imPath);
        File gtFile = new File(gtPath);
        
        if (!imFile.exists()) {
            throw new Error(imPath+" does not exist");
        }
        if (!gtFile.exists()) {
            throw new Error(gtPath+" does not exist");
        }
        if (!gtFile.isDirectory()) {
            throw new Error(gtPath+" is not a directory");
        }
        if (!imFile.isDirectory()) {
            throw new Error(imPath+" is not a directory");
        }
        
        load(imFile, gtFile, maxSize);
    }
    
    
    @Override
    public int size() {
        return images.size();
    }

    @Override
    public boolean isEmpty() {
        return images.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return images.contains(o);
    }

    @Override
    public Iterator<GTImage> iterator() {
        return images.iterator();
    }

    @Override
    public Object[] toArray() {
        return images.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return images.toArray(a);
    }

    @Override
    public boolean add(GTImage e) {
        return images.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return images.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return images.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends GTImage> c) {
        return images.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return images.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return images.retainAll(c);
    }

    @Override
    public void clear() {
        images.clear();
    }

    private void load(File imFile, File gtFile, int maxRemaining) throws IOException {
        String[] imNames = imFile.list();
        String[] gtNames = gtFile.list();
        if (imNames.length!=gtNames.length) {
            throw new Error("Dataset dimension problem");
        }
        
        Arrays.sort(imNames);
        Arrays.sort(gtNames);
        
        for (int i=0; i<imNames.length && maxRemaining-->0; i++) {
            System.out.println("Loading "+imNames[i]+"...");
            String imName = imFile.getAbsolutePath()+"/"+imNames[i];
            String gtName = gtFile.getAbsolutePath()+"/"+gtNames[i];
            GTImage gti = new GTImage(imName, gtName);
            add(gti);
        }
    }
    
    public void shuffle() {
        Collections.shuffle(images);
    }
}
