package cn.leo.man.game;

import android.content.Context;
import android.graphics.Bitmap;

import cn.leo.man.engine.Cell;
import cn.leo.man.engine.CellAnimation;
import cn.leo.man.engine.LeoEngine;
import cn.leo.man.utils.BitmapUtil;

/**
 * Created by Leo on 2017/7/3.
 */

public class ManCell extends CellAnimation {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_LEFT = 1;
    public static final int STATE_RIGHT = 2;
    public static final int STATE_UP = 3;
    public static final int STATE_DOWN = 4;
    public static final int MOVE_LEFT = 5;
    public static final int MOVE_RIGHT = 6;
    public static final int MOVE_MID = 7;

    private int state = STATE_NORMAL;
    private int move_state = MOVE_MID;
    private int speed = 0; //下降速度
    private int G = 2; //加速度
    private Bitmap[] mans;
    private final CellAnimation.LeoAnim mManLeft;
    private final CellAnimation.LeoAnim mManRight;
    private final LeoEngine mLeoEngine;
    private Cell standStep;

    public ManCell(Context context, LeoEngine engine) {
        mLeoEngine = engine;
        mans = new Bitmap[7];
        //加载人物图片资源
        for (int i = 0; i < mans.length; i++) {
            mans[i] = BitmapUtil.getImageFromAssetsFile(context, "man" + (i + 1) + ".gif");
        }
        //人物左右移动动画
        mManLeft = new CellAnimation.LeoAnim(new Bitmap[]{mans[3], mans[4]}, new int[]{100, 100}, true);
        mManRight = new CellAnimation.LeoAnim(new Bitmap[]{mans[5], mans[6]}, new int[]{100, 100}, true);

        freshState();
        setVisable(true);
        setLoop(true);
        reset();
    }

    //人物位置重置
    public void reset() {
        setState(STATE_DOWN);
        speed = 0;
        setX(mLeoEngine.getWidth() / 2 - width / 2);
        setY(300);
    }

    //更新人物状态
    public void freshState() {

        switch (state) {
            case STATE_NORMAL:
                setAnim(mans[0]);
                speed = 0;
                break;
            case STATE_LEFT:
                setAnim(mManLeft);
                break;
            case STATE_RIGHT:
                setAnim(mManRight);
                break;
            case STATE_UP:
                setAnim(mans[2]);
                speed = -30;
                break;
            case STATE_DOWN:
                setAnim(mans[1]);
                break;
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            freshState();
        }
    }

    public void setMove_state(int move_state) {
        this.move_state = move_state;
    }

    public void setStandStep(Cell standStep) {
        this.standStep = standStep;
    }

    @Override
    public void event() {  //引擎每帧调用人物事件
        switch (state) {
            case STATE_NORMAL:
                break;
            case STATE_LEFT:
                leftMove();
                break;
            case STATE_RIGHT:
                rightMove();
                break;
            case STATE_UP:
            case STATE_DOWN:
                speed += G;
                y += speed;
                break;
        }

        if (speed > 0) {
            setState(STATE_DOWN);
        }
        if (state < 3) {
            y = standStep.getY() - height;
        } else {
            switch (move_state) {
                case MOVE_LEFT:
                    leftMove();
                    break;
                case MOVE_RIGHT:
                    rightMove();
                    break;
            }
        }
    }

    public void rightMove() {
        x += 10;
        if (x > (mLeoEngine.getWidth() - width)) {
            x = mLeoEngine.getWidth() - width;
        }
    }

    public void leftMove() {
        x -= 10;
        if (x < 0) x = 0;
        return;
    }

}
