package com.example.pri.popularmovies_i;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Pri on 1/27/2017.
 */
public class GridItem extends ImageView {
private String img;

    public GridItem(Context context) {
        super(context);
    }
    public String getImage() {
        return img;
    }
}
