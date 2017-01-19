package com.qingmu.quickindex;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.qingmu.quickindex.view.QuickIndex;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {


	
	private Handler handler = new Handler();
	private ArrayList<Person> personsList;
	private ListView listView;
	private TextView tv_hint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 设置快速检索条监听
		QuickIndex quickIndex = (QuickIndex) findViewById(R.id.quickindex);
		quickIndex.setOnLetterChangedListener(new QuickIndex.OnLetterChangedListener() {

			@Override
			public void onLetterChanged(char letter) {
				Util.showToast(getApplication(), String.valueOf(letter));
				// 滚动到第一个letter字母处
				for (int i = 0; i < personsList.size(); i++) {
					char personLetter = personsList.get(i).getLetter();
					if (personLetter == letter) {
						listView.setSelection(i);
						break;
					}
				}
				
				// 显示字母提示
				handler.removeCallbacksAndMessages(null);
				tv_hint.setVisibility(View.VISIBLE);
				tv_hint.setText(String.valueOf(letter));
				
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						tv_hint.setVisibility(View.GONE);
					}
				}, 1000);
			}
		});
		
		// 生成用户列表
		personsList = new ArrayList<Person>();
		for (int i = 0; i < Cheeses.NAMES.length; i++) {
			String name = Cheeses.NAMES[i];
			char letter = StringUtils.getPinYin(name).charAt(0);
			Person person = new Person(name, letter);
			personsList.add(person);
		}
		
		Collections.sort(personsList);
		System.out.println("MainActivity.onCreate,list="+personsList);
		
		// 填充列表
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(new MyAdapter(personsList));
		
		// 提示框
		tv_hint = (TextView) findViewById(R.id.tv_hint);
	}

}
