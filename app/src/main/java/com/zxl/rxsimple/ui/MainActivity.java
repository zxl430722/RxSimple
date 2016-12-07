package com.zxl.rxsimple.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zxl.mycommon.base.BaseActivity;
import com.zxl.mycommon.commonutils.LogUtils;
import com.zxl.mycommon.daynightmodeutils.ChangeModeController;
import com.zxl.rxsimple.R;
import com.zxl.rxsimple.app.AppConstant;
import com.zxl.rxsimple.bean.TabEntity;
import com.zxl.rxsimple.ui.fragment.CareFragment;
import com.zxl.rxsimple.ui.fragment.NewsFragment;
import com.zxl.rxsimple.ui.fragment.PhotosFragment;
import com.zxl.rxsimple.ui.fragment.VideosFragment;

import java.util.ArrayList;

import butterknife.Bind;
import rx.functions.Action1;




/**
 * author: ZXL
 * created on: 2016/12/7 16:25
 * description:
 */

public class MainActivity extends BaseActivity{
    @Bind(R.id.tab_layout)
    CommonTabLayout tabLayout;

    private String[] mTitles = {"首页", "美女","视频","关注"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_normal,R.mipmap.ic_girl_normal,R.mipmap.ic_video_normal,R.mipmap.ic_care_normal};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_selected,R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected,R.mipmap.ic_care_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private static int tabLayoutHeight;

    private NewsFragment newsFragment;
    private VideosFragment videosFragment;
    private PhotosFragment photosFragment;
    private CareFragment careFragment;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        //初始化菜单
        initTab();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //切换daynight模式要立即变色的页面
        ChangeModeController.getInstance().init(this,R.attr.class);
        super.onCreate(savedInstanceState);
        initFragment(savedInstanceState);
        tabLayout.measure(0,0);
        tabLayoutHeight=tabLayout.getMeasuredHeight();
        //监听菜单显示或隐藏
        mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }
            @Override
            public void onTabReselect(int position) {
            }
        });
    }


    /**
     * 初始化frament
     * @param savedInstanceState
     */
    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            newsFragment = (NewsFragment) getSupportFragmentManager().findFragmentByTag("newsFragment");
            photosFragment = (PhotosFragment) getSupportFragmentManager().findFragmentByTag("photosFragment");
            videosFragment = (VideosFragment) getSupportFragmentManager().findFragmentByTag("videoFragment");
            careFragment = (CareFragment) getSupportFragmentManager().findFragmentByTag("careFragment");
            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            newsFragment = new NewsFragment();
            photosFragment = new PhotosFragment();
            videosFragment = new VideosFragment();
            careFragment = new CareFragment();

            transaction.add(R.id.fl_body, newsFragment, "newsMainFragment");
            transaction.add(R.id.fl_body, photosFragment, "photosMainFragment");
            transaction.add(R.id.fl_body, videosFragment, "videoMainFragment");
            transaction.add(R.id.fl_body, careFragment, "careMainFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        tabLayout.setCurrentTab(currentTabPosition);
    }

    /**
     * 切换
     */
    private void SwitchTo(int position) {
        LogUtils.logd("主页菜单position" + position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            //首页
            case 0:
                transaction.hide(photosFragment);
                transaction.hide(videosFragment);
                transaction.hide(careFragment);
                transaction.show(newsFragment);
                transaction.commitAllowingStateLoss();
                break;
            //美女
            case 1:
                transaction.hide(newsFragment);
                transaction.hide(videosFragment);
                transaction.hide(careFragment);
                transaction.show(photosFragment);
                transaction.commitAllowingStateLoss();
                break;
            //视频
            case 2:
                transaction.hide(newsFragment);
                transaction.hide(photosFragment);
                transaction.hide(careFragment);
                transaction.show(videosFragment);
                transaction.commitAllowingStateLoss();
                break;
            //关注
            case 3:
                transaction.hide(newsFragment);
                transaction.hide(photosFragment);
                transaction.hide(videosFragment);
                transaction.show(careFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单显示隐藏动画
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide){
        final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if(!showOrHide){
            valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 1, 0);
        }else{
            valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 0, 1);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();
                tabLayout.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator,alpha);
        animatorSet.start();
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        LogUtils.loge("onSaveInstanceState进来了1");
        if (tabLayout != null) {
            LogUtils.loge("onSaveInstanceState进来了2");
            outState.putInt(AppConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

}
