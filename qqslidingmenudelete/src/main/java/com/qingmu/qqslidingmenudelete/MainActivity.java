package com.qingmu.qqslidingmenudelete;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ListView mainListView,menuListView;
    ImageView iv_head;
    private SlideMenu slideMenu;
    MyLinearLayout my_linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slideMenu = (SlideMenu) View.inflate(this, R.layout.activity_main, null);
        setContentView(slideMenu);

        mainListView = (ListView) findViewById(R.id.main_listview);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        menuListView = (ListView) findViewById(R.id.menu_listview);
        my_linearlayout = (MyLinearLayout) findViewById(R.id.my_linearlayout);
        //设置SlideMenu
        my_linearlayout.setSlideMenu(slideMenu);

        //填充数据
        mainListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1
            ,Constant.NAMES));
        menuListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1
                ,Constant.sCheeseStrings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });


        //设置拖拽的监听器
        slideMenu.setDragListener(new SlideMenu.OnDragListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this,"open",0).show();
                int i = new Random().nextInt(Constant.sCheeseStrings.length);
                menuListView.smoothScrollToPosition(i);
            }
            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this,"close",0).show();
//                ViewCompat.animate(iv_head)
//                        .setInterpolator(new CycleInterpolator(10))
//                        .translationXBy(25)
//                        .setDuration(1000)
//                        .start();

                ViewCompat.animate(iv_head)
                        .rotationBy(360)
                        .setInterpolator(new OvershootInterpolator(4))
                        .setDuration(1000)
                        .start();
            }
            @Override
            public void onDragging(float fraction) {
//                Log.e(TAG, "onDragging: "+fraction);
//                iv_head.setAlpha(1-fraction);
                iv_head.setRotation(360*fraction);
            }
        });
    }
}
