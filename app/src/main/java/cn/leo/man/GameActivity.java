package cn.leo.man;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import cn.leo.man.engine.Cell;
import cn.leo.man.engine.CellImage;
import cn.leo.man.engine.CellString;
import cn.leo.man.engine.EngineControl;
import cn.leo.man.engine.EngineListener;
import cn.leo.man.engine.EngineThread;
import cn.leo.man.engine.LeoEngine;
import cn.leo.man.game.ManCell;
import cn.leo.man.game.StepCell;
import cn.leo.man.utils.BitmapUtil;

/**
 * Created by Leo on 2017/7/3.
 */

public class GameActivity extends Activity {
    private LeoEngine mLeEngine;
    private ManCell mManCell;
    private CellImage mTcCell;
    private StepCell.Step[] mStepCells;
    private StepCell mStep;
    private CellString mScoreCell;
    private CellString mLifeCell;
    private int score; //积分
    private int life; //生命

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        mLeEngine = (LeoEngine) findViewById(R.id.lge_game);
        mLeEngine.startEngine();
        mLeEngine.setEngineListener(new EngineListener() {
            @Override
            public void onEngineReady(LeoEngine engine) {
                initCell(); //初始化cell最好写在引擎准备完成后，否则调用引擎画面宽高获取不正确
                initData();
                initEvent();
                check();
            }
        });
    }

    private void initCell() {
        //添加背景
        Bitmap bg = BitmapUtil.getImageFromAssetsFile(this, "bg.jpg");
        mLeEngine.addCell(new CellImage(bg));
        //添加上面的刺
        Bitmap tc = BitmapUtil.getImageFromAssetsFile(this, "tc.gif");
        mTcCell = new CellImage(tc, new Rect(0, 0, tc.getWidth(), tc.getHeight()),
                new Rect(0, 0, mLeEngine.getScreenWidth(), tc.getHeight()));
        mTcCell.setZ(1000); //刺显示在最外层
        mLeEngine.addCell(mTcCell);
        //添加台阶
        mStep = new StepCell(this, mLeEngine);
        mStepCells = mStep.getCells();
        for (int i = 0; i < mStepCells.length; i++) {
            mLeEngine.addCell(mStepCells[i]);
        }
        //添加人物
        mManCell = new ManCell(this, mLeEngine);
        mLeEngine.addCell(mManCell);
        //添加积分和生命显示
        mScoreCell = new CellString("score:" + score, 10, 15, 30, Color.GREEN);
        mLeEngine.addCell(mScoreCell);
        mLifeCell = new CellString("life:" + life, 10, 50, 30, Color.RED);
        mLeEngine.addCell(mLifeCell);

    }


    //初始化元素位置和积分数据等
    private void initData() {
        score = 0;
        life = 3;
        mLifeCell.setStr("life:" + life);
        mScoreCell.setStr("score:" + score);
        //人物复位
        mManCell.reset();
        //台阶复位
        mStep.reset();
    }

    //往引擎添加一条子线程，检测人物和台阶位置
    private void check() {
        mLeEngine.addThread(
                new EngineThread() {
                    @Override
                    public void run() {
                        if (mLeEngine.hit(mManCell, mTcCell, 0)) { //人物碰到天花板的刺游戏结束
                            gameOver();
                        }

                        if (mManCell.getY() > mLeEngine.getGameHeight()) { //人物掉落显示范围外，游戏结束
                            gameOver();
                        }
                        boolean touchFlag = false;
                        for (int i = 0; i < mStepCells.length; i++) { //人物碰到台阶
                            if (mStepCells[i].isVisable() &&
                                    mLeEngine.hit(mManCell, mStepCells[i], 2)) {
                                //mManCell.setY(mStepCells[i].getY() - mManCell.getHeight());

                                touchFlag = true;
                                int type = mStepCells[i].getType();
                                if (mManCell.getState() == ManCell.STATE_DOWN) {
                                    mManCell.setState(ManCell.STATE_NORMAL);
                                    mManCell.setStandStep(mStepCells[i]);
                                    switch (type) {
                                        case 0: //普通岩石台阶

                                            break;
                                        case 1: //1秒消失的泥土台阶
                                            mStepCells[i].playAnim(); //播放动画
                                            break;
                                        case 2: //带刺的台阶，踩上掉血1点
                                            life--;
                                            mLifeCell.setStr("life:" + life);
                                            if (life <= 0) {
                                                gameOver();
                                            }
                                            break;
                                    }
                                }
                                switch (type) {
                                    case 3: //左滑台阶，站上去人物自动左移
                                        mManCell.setX(mManCell.getX() - 5);
                                        if (mManCell.getX() < 0) {
                                            mManCell.setX(0);
                                        }
                                        break;
                                    case 4: //右滑台阶，站上去人物自动右移
                                        mManCell.setX(mManCell.getX() + 5);
                                        if (mManCell.getX() > mLeEngine.getGameWidth() - mManCell.getWidth()) {
                                            mManCell.setX(mLeEngine.getGameWidth() - mManCell.getWidth());
                                        }
                                        break;
                                    case 5: //弹簧台阶，站上去会弹跳
                                        mManCell.setState(ManCell.STATE_UP);
                                        mStepCells[i].playAnim(); //台阶播放动画
                                        break;
                                }
                            }

                            if (mStepCells[i].getY() < mManCell.getY()) {
                                int tag = (int) mStepCells[i].getTag();
                                if (tag > 0) {
                                    score += tag;
                                    mScoreCell.setStr("score:" + score);
                                    mStepCells[i].setTag(0);
                                }
                            }
                        }
                        if (!touchFlag && mManCell.getState() != ManCell.STATE_UP) {
                            mManCell.setState(ManCell.STATE_DOWN);
                        }
                    }
                }
        );

    }
    //事件处理

    private void initEvent() {
        mLeEngine.setControl(new EngineControl() {
            @Override
            public void onClick(Cell cell) {

            }

            @Override
            public void onTouch(Cell cell, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        float x = event.getX();
                        if (mManCell.getState() < 3) {
                            if (x < mLeEngine.getScreenWidth() / 2) {
                                mManCell.setState(ManCell.STATE_LEFT); //人物左移
                            } else {
                                mManCell.setState(ManCell.STATE_RIGHT);//人物右移
                            }
                        } else {
                            if (x < mLeEngine.getScreenWidth() / 2) {
                                mManCell.setMove_state(ManCell.MOVE_LEFT); //人物左移
                            } else {
                                mManCell.setMove_state(ManCell.MOVE_RIGHT);//人物右移
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mManCell.setMove_state(ManCell.MOVE_MID);
                        if (mManCell.getState() < 3)
                            mManCell.setState(ManCell.STATE_NORMAL);
                        break;
                }

            }
        });
    }

    //游戏结束
    private void gameOver() {
        mLeEngine.pauseEngine(); //暂停引擎画面
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("游戏结束");
        builder.setMessage("您的积分:" + score);
        builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initData();
                mLeEngine.reStart();
            }
        });
        builder.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });

    }
}
