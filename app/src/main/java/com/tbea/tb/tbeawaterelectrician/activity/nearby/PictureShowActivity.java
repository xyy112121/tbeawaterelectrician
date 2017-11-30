package com.tbea.tb.tbeawaterelectrician.activity.nearby;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbea.tb.tbeawaterelectrician.R;
import com.tbea.tb.tbeawaterelectrician.activity.TopActivity;
import com.tbea.tb.tbeawaterelectrician.activity.nearby.model.PicturelistBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片查看（网络）
 */

public class PictureShowActivity extends TopActivity implements View.OnClickListener {

    TextView mTopCenterView;

    ViewPager viewPager;
    private List<PicturelistBean> images;

    private int currentPage = 0;//当前展示的页码

    PagerAdapter mAdapter;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture_show);
        currentPage = getIntent().getIntExtra("index", 0);
        //初始化图片资源
        images = (List<PicturelistBean>) getIntent().getSerializableExtra("images");
        viewPager = (ViewPager) findViewById(R.id.viewpager1);
        mTopCenterView = (TextView) findViewById(R.id.top_center);
        initTopbar(currentPage + 1 + "/" + images.size(), "全部图片", this);

        //-----初始化PagerAdapter------
        mAdapter = new MyAdapter();

        viewPager.setAdapter(mAdapter);
        //更改当前tip

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                mTopCenterView.setText(position + 1 + "/" + images.size());
            }
        });

    }

    @Override
    public void onClick(View view) {
        finish();
    }

    public class MyAdapter extends PagerAdapter {
        List<ImageView> mImageViews = new ArrayList<>();

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object o) {
//            ImageView imageView = mImageViews.get(position % mImageViews.size());
//            container.removeView(imageView);
//            ImageLoader.getInstance().displayImage("file://" + images.get(position).getPath(), imageView);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView im = new ImageView(mContext);
            try {
                im.setScaleType(ImageView.ScaleType.CENTER_CROP);
                String url = images.get(position).largepicture;
                if (images.get(position).largepicture == null || "".equals(images.get(position).largepicture)) {
                    url = images.get(position).largepictureurl;
                }
                if ((images.get(position).largepicture == null || "".equals(images.get(position).largepicture)) &&
                        (images.get(position).largepictureurl == null || "".equals(images.get(position).largepictureurl))) {
                    url = images.get(position).picture;
                }
                ImageLoader.getInstance().displayImage(url, im);
                container.addView(im);
                mImageViews.add(im);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return im;

        }

    }
}
