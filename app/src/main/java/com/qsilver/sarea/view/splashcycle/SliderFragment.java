package com.qsilver.sarea.view.splashcycle;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsilver.sarea.Adapter.SliderAdapter;
import com.qsilver.sarea.R;
import com.qsilver.sarea.view.WhichLoginActivity;
import com.qsilver.sarea.model.SliderItem;


public class SliderFragment extends Fragment {


    public SliderFragment() {
    }


ViewPager viewPager;
    LinearLayout linearLayout;
    TextView fragment_slider_tv_slide_skip;
    TextView fragment_slider_tv_slide_next;
    private int position = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider, container, false);
        viewPager=view.findViewById(R.id.fragment_slider_vp_slider);
        linearLayout=view.findViewById(R.id.layoutSliderIndicators);
        fragment_slider_tv_slide_skip=view.findViewById(R.id.fragment_slider_tv_slide_skip);
        fragment_slider_tv_slide_next=view.findViewById(R.id.fragment_slider_tv_slide_next);
        setDataToSlider();
        setupSliderIndicators(3);


        fragment_slider_tv_slide_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = viewPager.getCurrentItem();
                if (position < 3) {
                    position++;
                    viewPager.setCurrentItem(position);
                }
                if (position == 3) {
                    Intent intent = new Intent(getActivity(),WhichLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        });
        fragment_slider_tv_slide_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),WhichLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                setCurrentSliderIndicators(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }



    private void setDataToSlider() {
        SliderAdapter sliderAdapter = new SliderAdapter(getActivity());
        sliderAdapter.addpage(new SliderItem(R.drawable.slider_f_logo, "نظام سارع لتتبع حافلات المدارس ﺑﺸﻜﻞ ﻣﺒﺎﺷﺮ يساعدك فى معرفة ﻣﻮﻗﻊ اﻟﺤﺎﻓﻠﺔ ﻓﻲ أي وﻗﺖ أﺛﻨﺎء اﻟﺠﻮﻟﺔ اﻟﻤﺪرﺳﻴﺔ "));
        sliderAdapter.addpage(new SliderItem(R.drawable.slider_s_logo, "نظام تتبع الحافلات ﻳﺠﻌﻞ إدارة اﻟﻤﺪرﺳﺔ والاﻫﺎﻟﻲ ﻳﺸﻌﺮان ﺑﺎﻣﺎن اكبر ﺑﻤﺎ ﻳﺨﺺ ﺳﻼﻣﺔ اﻟﻄﻼب حيث يركز تطبيقنا على سلامة وصول الطلاب من و الى المدرسة"));
        sliderAdapter.addpage(new SliderItem(R.drawable.slider_t_logo, "نظام سارع يساعدك فى توفير الوقت والمجهود كما يساعد على متابعة سلوك السائقين و توفير استهلاك الوقود "));
        viewPager.setAdapter(sliderAdapter);
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        //set width and height of view
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //set margins of view
        layoutParams.setMargins(8, 0, 8, 0);
        //set view to xml
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getContext(),
                    R.drawable.tab_indicator_default
            ));
            indicators[i].setLayoutParams(layoutParams);
            linearLayout.addView(indicators[i]);
            setCurrentSliderIndicators(0);
        }
        linearLayout.setVisibility(View.VISIBLE);
    }
    private void setCurrentSliderIndicators(int position) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) linearLayout.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getContext(), R.drawable.tab_indicator_selected)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getContext(), R.drawable.tab_indicator_default)
                );
            }
        }
    }


}