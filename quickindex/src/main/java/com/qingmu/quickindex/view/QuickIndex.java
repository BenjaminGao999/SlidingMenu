package com.qingmu.quickindex.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qingmu.quickindex.Cheeses;

public class QuickIndex extends View {

	public interface OnLetterChangedListener{
		void onLetterChanged(char letter);
	}
	
	private OnLetterChangedListener onLetterChangedListener;
	
	public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
		this.onLetterChangedListener = onLetterChangedListener;
	}
	
	private char[] LETTERS = Cheeses.LETTERS;
	private Paint paint;
	private int mWidth;
	private int mLineH;
	private int index = -1;

	public QuickIndex(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public QuickIndex(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public QuickIndex(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		paint = new Paint();
		paint.setAntiAlias(true);// 设置抗锯齿
		paint.setTextSize(18);// 设置文本大小
		paint.setColor(Color.WHITE);// 设置文本颜色
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mLineH = h / LETTERS.length;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawAllLine(canvas);
	}

	private void drawAllLine(Canvas canvas) {
		// 获取第一个字母的Y坐标
		// Y = 单元格高度一半 + 文字高度的一半
		String text = "A";
		// 获取文本宽高
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		// 计算文字的绘制坐标
		float startY = mLineH / 2 + bounds.height() / 2;
		
		for (int i = 0; i < LETTERS.length; i++) {
			if (i==index) {
				// 选中的字母，需要高亮
				paint.setColor(Color.BLACK);
			} else {
				// 不是选中的字母，白色显示
				paint.setColor(Color.WHITE);
			}
			
			String drawLetter = String.valueOf(LETTERS[i]);
			// 获取文本的宽高
			paint.getTextBounds(drawLetter, 0, drawLetter.length(), bounds);
			float drawX = mWidth / 2 - bounds.width() / 2;
			// 在第一个字母的位置上偏移不同的Y高度
			float drawY = startY + mLineH * i;
			canvas.drawText(drawLetter, drawX, drawY, paint);
		}
	}

	// 绘制单行文本
	private void drawSingleLine(Canvas canvas) {
		// X = 控件宽度的一半 - 文字宽度的一半
		// Y = 单元格高度一半 + 文字高度的一半
		String text = "A";
		
		// 获取文本宽高
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		
		// 计算文字的绘制坐标
		float drawX = mWidth / 2 - bounds.width() / 2;
		float drawY = mLineH / 2 + bounds.height() / 2;
		canvas.drawText(text , drawX, drawY, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			// 计算选中的字母序号
			int preIndex = index;// 记录旧的序列号
			index = (int) (event.getY() / mLineH); // 计算新的序列号
			index = fixIndex(index);
			
			// 如果字母没有发生变化，则不需要刷新
			if (preIndex == index) {
				break;
			}
			
			System.out.println("QuickIndex.onTouchEvent,index="+index+";letter="+LETTERS[index]);
			// 字母发生变更，刷新界面
			if (onLetterChangedListener!=null) {
				onLetterChangedListener.onLetterChanged(LETTERS[index]);
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			// 去除掉字母的高亮
			index = -1;
			invalidate();
			break;
		default:
			break;
		}
		
		return true;
	}

	// 如果index 越界，则需要进行修正
	private int fixIndex(int index) {
		if (index < 0) {
			return 0;
		}
		
		if (index > LETTERS.length-1) {
			return LETTERS.length - 1;
		}
		
		return index;
	}
}
