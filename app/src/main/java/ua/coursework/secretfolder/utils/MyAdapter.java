package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.coursework.secretfolder.FullscreenActivity;
import ua.coursework.secretfolder.R;

import static androidx.core.content.ContextCompat.startActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ImgViewHolder> {

    private List<Bitmap> imgList = new ArrayList<>();
    private Map<Bitmap, String> mapList = new LinkedHashMap<Bitmap, String>();
    AppCompatActivity activity;
    Context context;


    public MyAdapter(AppCompatActivity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    class ImgViewHolder extends RecyclerView.ViewHolder{

        private ImageView userImageView;

            public ImgViewHolder(View itemView){
                super(itemView);
                userImageView = itemView.findViewById(R.id.galleryItem);
            }

            public void bind (final Bitmap bitmap){
                userImageView.setImageBitmap(bitmap);
                userImageView.setVisibility(bitmap != null ? View.VISIBLE : View.GONE);

                userImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // TODO : send bm
                        Intent intent = new Intent(context, FullscreenActivity.class);
                        intent.putExtra("ImageURI", mapList.get(bitmap));
                        startActivity(context, intent, null);
                    }
                });
            }

    }

    @Override
    public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false);
        return new ImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImgViewHolder holder, int position) {
        holder.bind(imgList.get(position));
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public void setItems(Map<Bitmap, String> images) {
        mapList = images;
        imgList.addAll(images.keySet());
        notifyDataSetChanged();
    }

    public void clearItems() {
        mapList.clear();
        imgList.clear();
        notifyDataSetChanged();
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }


}
