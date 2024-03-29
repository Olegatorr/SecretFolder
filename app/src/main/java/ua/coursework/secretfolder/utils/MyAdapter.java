package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.snatik.storage.Storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.coursework.secretfolder.FullscreenActivity;
import ua.coursework.secretfolder.R;

import static androidx.core.content.ContextCompat.startActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ImgViewHolder> {

    private final List<Bitmap> imgList = new ArrayList<>();
    private final StorageReference mStorageRef;
    AppCompatActivity activity;
    Context context;
    Storage storage;
    private Map<Bitmap, String> mapList = new LinkedHashMap<Bitmap, String>();


    public MyAdapter(AppCompatActivity activity, Context context) {
        this.activity = activity;
        this.context = context;
        storage = new Storage(context);
        mStorageRef = FirebaseStorage.getInstance().getReference();
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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    class ImgViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userImageView;

        public ImgViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.galleryItem);
        }

        public void bind(final Bitmap bitmap) {
            userImageView.setImageBitmap(bitmap);
            userImageView.setVisibility(bitmap != null ? View.VISIBLE : View.GONE);

            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, FullscreenActivity.class);
                    intent.putExtra("ImageURI", mapList.get(bitmap));
                    startActivity(context, intent, null);
                    activity.finish();
                }
            });

            userImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(activity.getResources().getString(R.string.where_delete))
                            .setCancelable(false)
                            .setNegativeButton(activity.getResources().getString(R.string.local), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    storage.deleteFile(mapList.get(bitmap));

                                    userImageView.setVisibility(View.GONE);
                                    imgList.remove(bitmap);
                                    mapList.remove(bitmap);

                                    notifyDataSetChanged();

                                    dialog.dismiss();
                                }
                            })
                            .setNeutralButton(activity.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton(activity.getResources().getString(R.string.server), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    storage.deleteFile(mapList.get(bitmap));
                                    StorageReference temp = mStorageRef.child(PreferencesHandler.getValue(context, "userID", "ND")).child(mapList.get(bitmap).substring(mapList.get(bitmap).lastIndexOf("/") + 1));
                                    temp.delete();

                                    userImageView.setVisibility(View.GONE);
                                    imgList.remove(bitmap);
                                    mapList.remove(bitmap);

                                    notifyDataSetChanged();

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    return false;
                }
            });
        }

    }


}
