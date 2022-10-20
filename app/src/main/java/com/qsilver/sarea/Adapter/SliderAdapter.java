package com.qsilver.sarea.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;


import com.qsilver.sarea.R;
import com.qsilver.sarea.model.SliderItem;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    public List<SliderItem>sliderItem=new ArrayList<>();

    public SliderAdapter(Context context){
        this.context=context;
    }
    public void addpage(SliderItem sliderItem){
        this.sliderItem.add(sliderItem);
    }

    @Override
    public int getCount() {
        return this.sliderItem.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view== (ConstraintLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemview=inflater.inflate(R.layout.splash_item_slider,container,false);
        ImageView item_slide_iv_imge=itemview.findViewById(R.id.slider_item_iv_image);
        TextView slide_item_tv_text=itemview.findViewById(R.id.slide_item_tv_text);

        item_slide_iv_imge.setImageResource(sliderItem.get(position).getBackGround());
        slide_item_tv_text.setText(sliderItem.get(position).getDesc());
        container.addView(itemview);
        return itemview;


    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }


}
