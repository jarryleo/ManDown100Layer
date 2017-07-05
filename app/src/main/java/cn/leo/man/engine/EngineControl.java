package cn.leo.man.engine;

import android.view.MotionEvent;

/**
 * 元素触控事件接口
 * Created by JarryLeo on 2017/1/8.
 */

public interface EngineControl {
    //点击事件
    void onClick(Cell cell);

    //触摸事件
    void onTouch(Cell cell, MotionEvent event);
}
