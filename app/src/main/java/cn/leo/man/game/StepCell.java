package cn.leo.man.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Random;

import cn.leo.man.engine.CellAnimation;
import cn.leo.man.engine.LeoEngine;
import cn.leo.man.utils.BitmapUtil;

/**
 * Created by Leo on 2017/7/3.
 */

public class StepCell {
    private Bitmap[] mBitmaps; //台阶图片
    private Step[] stepCells;
    private final Random mRandom;
    private final LeoEngine mLeoEngine;
    private final CellAnimation.LeoAnim mHll; //左滑轮动画
    private final CellAnimation.LeoAnim mHlr;//有滑轮动画
    private final CellAnimation.LeoAnim mTh; //弹簧动画

    public StepCell(Context context, final LeoEngine engine) {
        mLeoEngine = engine;
        String file[] = new String[]{"tb1.gif", "tb2.gif", "dc.gif", "hlz1.gif", "hlz2.gif", "hlr1.gif", "hlr2.gif",
                "th1.gif", "th2.gif", "th3.gif"};
        mBitmaps = new Bitmap[file.length];
        //加载台阶图片资源
        for (int i = 0; i < file.length; i++) {
            mBitmaps[i] = BitmapUtil.getImageFromAssetsFile(context, file[i]);
        }
        //创建动画台阶的动画元素
        mHll = new CellAnimation.LeoAnim(new Bitmap[]{mBitmaps[3], mBitmaps[4]}, new int[]{200, 200}, true);
        mHlr = new CellAnimation.LeoAnim(new Bitmap[]{mBitmaps[5], mBitmaps[6]}, new int[]{200, 200}, true);
        mTh = new CellAnimation.LeoAnim(
                new Bitmap[]{mBitmaps[7], mBitmaps[8], mBitmaps[7], mBitmaps[9], mBitmaps[7]},
                new int[]{100, 100, 100, 100, 100}, false);


        //创建6个台阶
        stepCells = new Step[6];
        mRandom = new Random();
        for (int i = 0; i < stepCells.length; i++) {
            stepCells[i] = new Step(i);
        }
    }

    public Step[] getCells() {
        return stepCells;
    }


    //重置台阶位置
    public void reset() {
        for (int i = 0; i < stepCells.length; i++) {
            stepCells[i].setData(i);
        }
    }

    //台阶类
    public class Step extends CellAnimation {
        private int index; //台阶序号
        private int mType; //台阶类型
        private boolean hideDelay; //延时消失
        private int hideTime;


        public Step(int index) {
            this.index = index;
            setData(index);
        }

        public int getType() { //获取台阶类型
            return mType;
        }

        public void setData(int index) {
            if (index == 0) {
                mType = 0;
                setAnim(mBitmaps[0]);
                setVisable(true);
                y = mLeoEngine.getHeight();
                x = mLeoEngine.getWidth() / 2 - width / 2;
                setTag(1);
            } else {
                initData();
            }
        }

        public void initStep() { //随机载入一种台阶
            mType = mRandom.nextInt(6);
            if (mType < 3) {
                setAnim(mBitmaps[mType]);
            } else if (mType == 3) {
                setAnim(mHll);
            } else if (mType == 4) {
                setAnim(mHlr);
            } else if (mType == 5) {
                setAnim(mBitmaps[7]);
            }
            setVisable(true);
        }

        public void playAnim() {
            if (mType == 5) {//播放弹簧动画
                setAnim(mTh).setFillAfter(true);
                setCorner(CORNER_BOTTOM_LEFT);
            } else if (mType == 1) { //泥土台阶1秒消失
                hideTime = mLeoEngine.getFps();
                hideDelay = true;
            }
        }


        @Override
        public void event() { //引擎每帧会调用的事件，不能有耗时操作，否则卡顿
            //每个台阶事件处理
            y -= 5;
            if (y + height < 0) {
                initData();
            }
            if (hideDelay && visable) {
                hideTime--;
                if (hideTime <= 0) {
                    setVisable(false);
                    hideDelay = false;
                }
            }
        }

        private void initData() { //重置台阶位置和类型
            hideDelay = false;
            setTag(1);
            int pre = index - 1;
            if (pre < 0) {
                pre = stepCells.length - 1;
            }
            x = mRandom.nextInt(mLeoEngine.getScreenWidth() - mBitmaps[index].getWidth());
            y = stepCells[pre].getY() + 300;
            initStep();
        }
    }
}
