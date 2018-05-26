package com.example.hitesh.dexfileexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.util.List;


public class files_adapter extends BaseAdapter {
    private Context context;
    private List<fileItem> list;
    ImageView imageView;

    public files_adapter(Context context, List<fileItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi=View.inflate(context,R.layout.files_adapter,null);
        Log.i("type",String.valueOf(list.get(i).getType()));
        TextView name=vi.findViewById(R.id.name);
        TextView more=vi.findViewById(R.id.more_info);
         imageView=vi.findViewById(R.id.icon);

        name.setText(list.get(i).getName());
        more.setText(list.get(i).getData()+" | "+list.get(i).getDate());
        String uri="drawable/"+list.get(i).getImage();
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable image = context.getResources().getDrawable(imageResource);
        imageView.setImageDrawable(image);
        try {
            String[] types=list.get(i).getType().split("/");
            Uri file=Uri.fromFile(new File(list.get(i).getPath()));
            if (types[0].equals("image")){
                Glide.with(vi).load(file).into(imageView);
                Log.i("image :",String.valueOf(file));
            }
            else if (types[0].equals("video")){
                Glide.with(vi).load(file).into(imageView);
            }
            else if (types[1].equals("pdf")){
                imageView.setImageResource(R.drawable.pdf_icon);
            }
            else if (types[0].equals("text")){
                imageView.setImageResource(R.drawable.text_file);
            }
            else if (types[1].equals("vnd.ms-powerpoint")){
                imageView.setImageResource(R.drawable.ppt_icon);
            }
        }catch (NullPointerException e)
        {
            System.out.print(e);
        }
        return vi;
    }
}
