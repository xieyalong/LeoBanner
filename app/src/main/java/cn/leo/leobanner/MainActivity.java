package cn.leo.leobanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.leo.banner.AniamtionDecoration;
import cn.leo.banner.InfiniteLayoutManager;
import cn.leo.banner.LeoBanner;

/**
 * @author Leo
 */
public class MainActivity extends AppCompatActivity {

    private LeoBanner mBanner;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBanner = findViewById(R.id.banner);
        List<String> images = new ArrayList<>();
        images.add("https://b-ssl.duitang.com/uploads/item/201405/13/20140513232544_CGzh5.thumb.1400_0.jpeg");
        images.add("https://b-ssl.duitang.com/uploads/item/201703/23/20170323093222_n8w4y.thumb.1000_0.jpeg");
        images.add("https://b-ssl.duitang.com/uploads/item/201703/23/20170323093213_8MEie.thumb.1000_0.jpeg");
        mBanner.setImages(images, new LeoBanner.ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String imagePath) {
                Glide.with(MainActivity.this).load(imagePath).into(imageView);
            }
        });

        mRecyclerView = findViewById(R.id.rvTest);
        mRecyclerView.setLayoutManager(new InfiniteLayoutManager());
        mRecyclerView.setAdapter(new RvAdapter());
        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new AniamtionDecoration());

        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(defaultItemAnimator);

    }
}
