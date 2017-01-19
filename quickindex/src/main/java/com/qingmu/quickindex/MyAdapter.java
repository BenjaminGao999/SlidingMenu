package com.qingmu.quickindex;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

	private ArrayList<Person> personsList;

	public MyAdapter(ArrayList<Person> personsList) {
		this.personsList = personsList;
	}

	@Override
	public int getCount() {
		return personsList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(), R.layout.item_man, null);
		}
		
		TextView tv_index = (TextView) convertView.findViewById(R.id.tv_index);
		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		
		// 填充内容
		Person person = personsList.get(position);
		tv_index.setText(String.valueOf(person.getLetter()));
		tv_name.setText(person.getName());
		
		// 隐藏字母栏
		if (position!=0) {
			Person prePerson = personsList.get(position-1);
			if (prePerson.getLetter()==person.getLetter()) {
				// 与上一个字母一样，则隐藏
				tv_index.setVisibility(View.GONE);
			} else {
				tv_index.setVisibility(View.VISIBLE);
			}
		} else {
			tv_index.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

}
