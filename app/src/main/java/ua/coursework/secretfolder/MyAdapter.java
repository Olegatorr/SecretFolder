package ua.coursework.secretfolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ImgViewHolder> {

    private List<String> imgList = new ArrayList<>();

    class ImgViewHolder extends RecyclerView.ViewHolder{

        private ImageView userImageView;

            public ImgViewHolder(View itemView){
                super(itemView);
                userImageView = itemView.findViewById(R.id.galleryItem);
            }

            public void bind (Bitmap bitmap){
                userImageView.setImageBitmap(bitmap);
                userImageView.setVisibility(bitmap != null ? View.VISIBLE : View.GONE);
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
        holder.bind(StringToBitMap(imgList.get(position)));
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public void setItems(Collection<String> tweets) {
        imgList.addAll(tweets);
        notifyDataSetChanged();
    }

    public void clearItems() {
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
