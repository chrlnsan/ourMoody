package com.example.ourmoody;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.ourmoody.util.ScreenItem;

import java.util.List;

public class StartViewPagerAdapter extends PagerAdapter {
    Context omContext;
    List<ScreenItem> omListScreen;

    public StartViewPagerAdapter(Context omContext, List<ScreenItem> omListScreen){
        this.omContext = omContext;
        this.omListScreen = omListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater)
                omContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen, null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.start_img);
        TextView titel = layoutScreen.findViewById(R.id.start_titel);
        TextView beschreibung = layoutScreen.findViewById(R.id.start_beschreibung);

        titel.setText(omListScreen.get(position).getTitle());
        beschreibung.setText(omListScreen.get(position).getDescription());
        imgSlide.setImageResource(omListScreen.get(position).getScreenImg());

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
