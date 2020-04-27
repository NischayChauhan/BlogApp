package com.example.nischay.blogapp;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Nischay on 4/27/2020.
 */

public class Cache_Manager {

    private static Cache_Manager instance;
    private LruCache<String, Bitmap> memoryCache;

    private Cache_Manager(){
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    public static Cache_Manager getInstance() {
        if (instance == null) {
            instance = new Cache_Manager();
        }
        return instance;
    }

    public LruCache<String, Bitmap> getLru() {
        return memoryCache;
    }
}
