package com.example.testapp.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

import com.tandong.switchlayout.BaseEffects;
import com.tandong.switchlayout.SwitchLayout;

public class AnimationUtils {
    public static void setEnterAnimation(Activity context, int key) {
        if (key == -1) {
            return;
        }
        context.overridePendingTransition(0, 0);
        switch (key) {
            case 0:
                //3d翻转
                SwitchLayout.get3DRotateFromLeft(context, false, null);
                // 三个参数分别为（Activity/View，是否关闭Activity，特效（可为空））
                break;
            case 1:
                //底部滑入(常用)
                SwitchLayout.getSlideFromBottom(context, false,
                        BaseEffects.getMoreSlowEffect());
                break;
            case 2:
                //顶部滑入
                SwitchLayout.getSlideFromTop(context, false,
                        BaseEffects.getReScrollEffect());
                break;
            case 3:
                //左侧滑入
                SwitchLayout.getSlideFromLeft(context, false,
                        BaseEffects.getLinearInterEffect());
                break;
            case 4:
                //右侧滑入
                SwitchLayout.getSlideFromRight(context, false, null);
                break;
            case 5:
                //淡入淡出(常用)
                SwitchLayout.getFadingIn(context);
                break;
            case 6:
                //中心缩放(常用)
                SwitchLayout.ScaleBig(context, false, null);
                break;
            case 7:
                //上下翻转(不好使)
                SwitchLayout.FlipUpDown(context, false, BaseEffects.getQuickToSlowEffect());
                break;
            case 8:
                //左上角缩放(常用)
                SwitchLayout.ScaleBigLeftTop(context, false, null);
                break;
            case 9:
                //震动出场
                SwitchLayout.getShakeMode(context, false, null, 1);
                break;
            case 10:
                //从左侧中心旋转
                SwitchLayout.RotateLeftCenterIn(context, false, null);
                break;
            case 11:
                //左上角旋转
                SwitchLayout.RotateLeftTopIn(context, false, null);
                break;
            case 12:
                //中心旋转
                SwitchLayout.RotateCenterIn(context, false, null);
                break;
            case 13:
                //横向转开(好使)
                SwitchLayout.ScaleToBigHorizontalIn(context, false, null);
                break;
            case 14:
                //纵向展开(好使)
                SwitchLayout.ScaleToBigVerticalIn(context, false, null);
                break;
            default:
                break;
        }
    }

    public static void setExitAnimation(Activity context, int key) {
        if (key == -1) {
            return;
        }
        context.overridePendingTransition(0, 0);
        switch (key) {
            case 0:
                SwitchLayout.get3DRotateFromRight(context, true, null);
                break;
            case 1:
                SwitchLayout.getSlideToBottom(context, true,
                        BaseEffects.getMoreSlowEffect());
                break;
            case 2:
                SwitchLayout.getSlideToTop(context, true,
                        BaseEffects.getReScrollEffect());
                break;
            case 3:
                SwitchLayout.getSlideToLeft(context, true,
                        BaseEffects.getLinearInterEffect());
                break;
            case 4:
                SwitchLayout.getSlideToRight(context, true, null);
                break;
            case 5:
                SwitchLayout.getFadingOut(context, true);
                break;
            case 6:
                SwitchLayout.ScaleSmall(context, true, null);
                break;
            case 7:
                SwitchLayout.FlipUpDown(context, true,
                        BaseEffects.getQuickToSlowEffect());
                break;
            case 8:
                SwitchLayout.ScaleSmallLeftTop(context, true, null);
                break;
            case 9:
                SwitchLayout.getShakeMode(context, true, null, 1);
                break;
            case 10:
                SwitchLayout.RotateLeftCenterOut(context, true, null);
                break;
            case 11:
                SwitchLayout.RotateLeftTopOut(context, true, null);
                break;
            case 12:
                SwitchLayout.RotateCenterOut(context, true, null);
                break;
            case 13:
                SwitchLayout.ScaleToBigHorizontalOut(context, true, null);
                break;
            case 14:
                SwitchLayout.ScaleToBigVerticalOut(context, true, null);
                break;
            default:
                break;
        }
    }

    private void fromTop(View view) {
        view.setVisibility(View.INVISIBLE);
        view.post(() -> {
            view.setVisibility(View.VISIBLE);
            view.setTranslationY(-view.getHeight());
            view.animate().translationYBy(view.getHeight()).setDuration(300);
        });
    }

    private void fromBottom(View View) {
        View.setVisibility(android.view.View.INVISIBLE);
        View.post(new Runnable() {
            @Override
            public void run() {
                View.setVisibility(android.view.View.VISIBLE);
                View.setTranslationY(SizeUtils.getScreenHeightNoStatusBar() + View.getHeight());
                View.animate().translationYBy(-View.getHeight())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }
                        })
                        .setDuration(1000);
            }
        });
    }

    private void fromLeft(View view) {
        view.setVisibility(View.INVISIBLE);
        view.post(() -> {
            view.setVisibility(View.VISIBLE);
            view.setTranslationX(-view.getWidth());
            view.animate().translationX(0)
                    .setDuration(1000);
        });
    }

    private void fromRight(View view) {
        view.setVisibility(View.INVISIBLE);
        view.post(() -> {
            view.setVisibility(View.VISIBLE);
            view.setTranslationX(SizeUtils.getScreenWidth());
            view.animate().translationXBy(-view.getWidth()).setDuration(1000);
        });
    }

    private void alphaOut(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(1.0f);
        view.animate().alpha(0.3f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void alphaIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.3f);
        view.animate().alpha(1.0f).setDuration(1000);
    }

    private void scaleOut(View view) {
        view.animate().scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void scaleIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        view.animate().scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(1000);
    }


    private void scaleLarge(View view) {
        view.setVisibility(View.VISIBLE);
        view.post(() -> {
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1.3f);
            view.setScaleY(1.3f);
            view.animate().scaleX(1.0f)
                    .scaleY(1.0f);
        });
    }

}
