package com.kelly.controller;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ACAduserEnglishPagerAdapter extends PagerAdapter {
    private List viewList = new ArrayList();
    private List viewTitle = new ArrayList();

    public void addSubView(View subView, String title) {
        this.viewList.add(subView);
        this.viewTitle.add(title);
    }

    public int getCount() {
        return this.viewList.size();
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) this.viewList.get(position));
    }

    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public CharSequence getPageTitle(int position) {
        return (String) this.viewTitle.get(position);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView((View) this.viewList.get(position));
        return this.viewList.get(position);
    }
}
