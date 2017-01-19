package com.qingmu.swipedelete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);

        //准备数据
        for (int i=0;i<30;i++){
            list.add(i+"碗拉面");
        }
        listView.setAdapter(new MyAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"哈哈",0).show();
            }
        });
    }

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = View.inflate(MainActivity.this,R.layout.adapter_list,null);
            }
            MyHolder myHolder = MyHolder.getHolder(convertView);

            //绑定数据
            myHolder.tv_name.setText(list.get(position));

            //设置点击事件
            myHolder.swipeLayout.setOnSwipeLayoutClickListener(new SwipeLayout.OnSwipeLayoutClickListener() {
                @Override
                public void onClick() {
                    Toast.makeText(MainActivity.this,"哈哈 - " + position,0).show();
                }
            });

            return convertView;
        }
    }

    static class MyHolder{
        TextView tv_name;
        SwipeLayout swipeLayout;
        public MyHolder(View convertView){
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
        }
        public static MyHolder getHolder(View convertView){
            MyHolder myHolder = (MyHolder) convertView.getTag();
            if(myHolder==null){
                myHolder = new MyHolder(convertView);
                convertView.setTag(myHolder);
            }
            return myHolder;
        }
    }
}
