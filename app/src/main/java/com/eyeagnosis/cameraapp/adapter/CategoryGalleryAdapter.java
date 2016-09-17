package com.eyeagnosis.cameraapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eyeagnosis.cameraapp.R;
import com.eyeagnosis.cameraapp.model.Image;

import java.util.List;

public class CategoryGalleryAdapter extends RecyclerView.Adapter<CategoryGalleryAdapter.MyViewHolder> {

    private List<Image> images;
    private Context mContext;
    private TextView titleText;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public CategoryGalleryAdapter(Context context, List<Image> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String eyeOn = "Eye OnSite";
        String ladem = "Lademz Collections";
        String tlw = "TLW (The Leading Woman)";
        String equipHall = "Equipment Hall Limited";
        String ayaba = "Ayaba";
        String ylf = "YLF, Ikeja";
        String sentinel = "Sentinel Consult";
        String makin = "Makintouch Consulting";
        String konga = "Konga Design Challenge";
        String teeD = "Tees Design";
        String yoa = "YOA Insurance";
        String gui = "OYO";

        Image image = images.get(position);

//        // setup Glide request without the into() method
//        DrawableRequestBuilder<String> thumbnailRequest = Glide
//                .with(mContext)
//                .load(image.getMedium());

        // pass the request as a a parameter to the thumbnail request
        Glide.with(mContext).load(image.getPicture())
                .error(R.drawable.loader)
                .thumbnail(0.3f)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

//        if (image.getSerial().equalsIgnoreCase("1")) {
//            titleText.setText(eyeOn);
//        } else {
//            titleText.setText(gui);
//        }

    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CategoryGalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CategoryGalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
