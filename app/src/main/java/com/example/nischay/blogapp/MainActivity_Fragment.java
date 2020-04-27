package com.example.nischay.blogapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Nischay on 4/26/2020.
 */

public class MainActivity_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        String heading = getArguments().getString("heading");
        String story = getArguments().getString("story");
        String image = getArguments().getString("image");

        ViewGroup page = (ViewGroup) inflater.inflate(R.layout.act_main_1,container,false);
        ImageView im_view = (ImageView) page.findViewById(R.id.pager_image);
        TextView im_view_text  =(TextView) page.findViewById(R.id.pager_image_text);
        ProgressBar im_view_loader = (ProgressBar) page.findViewById(R.id.pager_image_loader);


        if(!image.isEmpty())
            (new MainActivity_ImageLoadTask(image,im_view,im_view_text,im_view_loader)).execute();
        else{
            im_view_text.setVisibility(View.VISIBLE);
            im_view_loader.setVisibility(View.GONE);
        }


        ((TextView) page.findViewById(R.id.pager_heading)).setText(heading);
        ((TextView) page.findViewById(R.id.pager_content)).setText(story);

        return page;
    }

}
