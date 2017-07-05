package cn.leo.man.engine;

import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Leo on 2017/7/3.
 */

public class CellString extends Cell {
    private String str; //要绘制的文字信息
    private TextPaint mTextPaint;
    private int LineNums = 200; //行宽像素，自动换行用

    @Override
    public int getCellType() {
        return Cell.TYPE_STRING;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getLineNums() {
        return LineNums;
    }

    public void setLineNums(int lineNums) {
        LineNums = lineNums;
    }

    public TextPaint getTextPaint() {
        return mTextPaint;
    }

    public void setTextPaint(TextPaint textPaint) {
        mTextPaint = textPaint;
    }

    /**
     * 绘制文字,传入画笔,画笔可以设置文字颜色和大小
     *
     * @param str
     * @param x
     * @param y
     * @param textPaint
     */
    public CellString(String str, float x, float y, TextPaint textPaint) {
        this.str = str;
        this.x = x;
        this.y = y;
        this.mTextPaint = textPaint;
    }

    /**
     * 绘制文字,传入位置,大小和颜色
     *
     * @param str
     * @param x
     * @param y
     * @param size
     * @param color
     */
    public CellString(String str, float x, float y, int size, int color) {
        this.str = str;
        this.x = x;
        this.y = y;
        if (this.mTextPaint == null) {
            this.mTextPaint = new TextPaint();
        }
        this.mTextPaint.setColor(color);
        this.mTextPaint.setTextSize(size);
    }

    /**
     * 引擎调用绘制方法
     */

    public void draw(Canvas canvas) {
        super.draw(canvas);
        StaticLayout layout = new StaticLayout(str, mTextPaint, LineNums, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        canvas.save();
        canvas.translate(x, y);
        layout.draw(canvas);
        canvas.restore();
    }
}
