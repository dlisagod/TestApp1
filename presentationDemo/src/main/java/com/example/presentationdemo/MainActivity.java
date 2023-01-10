package com.example.presentationdemo;

import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    private static final String PRESENTATION_KEY = "presentation";
    private DisplayManager displayManager;
    private CheckBox show_all_displays;
    private DisplayListAdapter displayAdapter;
    private ListView listView;
    // 定义存储photo的数组
    private int[] photos = new int[] { R.drawable.guild1, R.drawable.guild2,
            R.drawable.guild3, R.drawable.ic_launcher_foreground, R.drawable.puzzle,
            R.drawable.test };
    // 记录下一张照片的Id
    private int nextPhotoNumber = 0;
    /*
     * 利用SparseArray存储所有屏幕的prsentation上显示的内容（尤其是当屏幕不止一个时数组的作用更大）
     * SparseArray：稀疏数组，在存储数据不多的情况下可以大大地节省空间，此时可以代替hasmap使用
     */
    SparseArray<PresentationContents> mSavedPresentationContents;

    // 根据displayId记录当前显示的presentation
    SparseArray<DemoPresentation> mActivePresentations = new SparseArray<DemoPresentation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 首先检查是否存在需要恢复的状态信息
        if (savedInstanceState != null) {
            mSavedPresentationContents = savedInstanceState
                    .getSparseParcelableArray(PRESENTATION_KEY);
        } else {
            mSavedPresentationContents = new SparseArray<PresentationContents>();
        }
        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        show_all_displays = (CheckBox) findViewById(R.id.show_all_presentation);
        show_all_displays.setOnCheckedChangeListener(this);
        listView = (ListView) findViewById(R.id.listView);
        displayAdapter = new DisplayListAdapter(this);
        listView.setAdapter(displayAdapter);
    }

    /**
     * 定义listView的适配器，用于显示所有的屏幕信息
     * 
     * @author aaaa
     *
     */
    private class DisplayListAdapter extends ArrayAdapter<Display> {
        private Context mContext;

        // 构造函数，布局文件为R.layout.list_item
        public DisplayListAdapter(Context context) {
            super(context, R.layout.listview_item);
            mContext = context;
        }

        @SuppressLint("NewApi")
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = null;
            if (convertView == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.listview_item,
                        null);
            } else {
                v = convertView;
            }
            // 找到各个组件
            CheckBox cb_display_presentation = (CheckBox) v
                    .findViewById(R.id.cb_presentation);
            TextView tv_showId = (TextView) v.findViewById(R.id.tv_displayId);
            Button bt_info = (Button) v.findViewById(R.id.bt_info);

            Display display = getItem(position);
            String text = "Display#" + display.getDisplayId()
                    + display.getName();

            tv_showId.setText(text);
            // 将dispaly设置为复选框的标志位，将dispaly与复选框绑定在一起，找到复选框时就可以找到display了
            cb_display_presentation.setTag(display);
            // 如果当前presentation可见（存在于mActivePresentations中）或者mSavedPresentationContents中保存了
            // 当前presentation显示的内容，则复选框为选中状态，否则为非选中状态
            cb_display_presentation.setChecked(mActivePresentations
                    .indexOfKey(display.getDisplayId()) >= 0
                    || mSavedPresentationContents.indexOfKey(display
                            .getDisplayId()) >= 0);
            cb_display_presentation
                    .setOnCheckedChangeListener(MainActivity.this);

            bt_info.setTag(display);
            bt_info.setOnClickListener(mInfoClickListener);
            return v;
        }

        /**
         * 更新listVIew的显示信息
         */
        @SuppressLint("NewApi")
		public void updateListView() {
            // 清空一下数据
            clear();
            String displayCategory = getDisplayCategory();
            if (displayCategory == null) {
                System.out.println("displayCategory is null");
            }
            Display[] displays = displayManager.getDisplays(displayCategory);
            // 将所有的屏幕添加到数组中
            addAll(displays);
        }

        /**
         * 当复选框处于选中状态时列出所有的屏幕信息 处于非选中状态时只列出presentation屏幕信息
         * 
         * @return
         */
        private String getDisplayCategory() {
            System.out.println(show_all_displays.isChecked());
            return show_all_displays.isChecked() ? null
                    : DisplayManager.DISPLAY_CATEGORY_PRESENTATION;
        }

    }

    private OnClickListener mInfoClickListener = new OnClickListener() {
        /**
         * 点击时创建一个对话框，用于显示dispaly的信息
         */
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Builder builder = new Builder(context);
            Display display = (Display) v.getTag();
            String title = "Dispaly #" + display.getDisplayId() + "Info";
            builder.setTitle(title).setMessage(display.toString())
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    };

    /**
     * 同时为两个复选框提供监听服务
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == show_all_displays) {
            // 更新列表框
            displayAdapter.updateListView();
        } else {
            // 从复选框中取出Display
            Display display = (Display) buttonView.getTag();
            if (isChecked) {
                // 显示下一张图片
                PresentationContents contents = new PresentationContents(
                        getNextPhoto());
                showPresentation(display, contents);
            } else {
                hidePresentation(display);
            }
        }
    }

    private int getNextPhoto() {
        int nextPhoto = photos[nextPhotoNumber];
        nextPhotoNumber = (nextPhotoNumber + 1) % photos.length;
        return nextPhoto;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressLint("NewApi")
	private void showPresentation(Display display, PresentationContents contents) {
        int dispalyId = display.getDisplayId();
        // 如果当前resentation已经是显示状态则直接返回
        if (mActivePresentations.get(dispalyId) != null) {
            return;
        }
        // 否则则新建一个presentation，并存储到mActivePresentations当中
        DemoPresentation presentation = new DemoPresentation(this, display,
                contents);
        presentation.show();
        // 设置prsentation的解除监听
        presentation.setOnDismissListener(mPresentationDismissListener);
        mActivePresentations.put(dispalyId, presentation);
    }

    @SuppressLint("NewApi")
	private void hidePresentation(Display display) {
        int dispalyId = display.getDisplayId();
        DemoPresentation presentation = mActivePresentations.get(dispalyId);
        if (presentation != null) {
            presentation.dismiss();
            mActivePresentations.remove(dispalyId);
        }
    }

    private OnDismissListener mPresentationDismissListener = new OnDismissListener() {
        @SuppressLint("NewApi")
		@Override
        public void onDismiss(DialogInterface dialog) {
            DemoPresentation prsentation = (DemoPresentation) dialog;
            int displayId = prsentation.getDisplay().getDisplayId();
            // 从presentation数组当中将当前presentation删除
            mActivePresentations.delete(displayId);
            // 更新listView
            displayAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 当activity恢复时，首先从之前在pause状态中保存的presentation中显示的内容恢复过来
     */
    @SuppressLint("NewApi")
	@Override
    protected void onResume() {
        super.onResume();
        // 更新listView
        displayAdapter.updateListView();
        // 恢复保存的presentationContent
        if (mSavedPresentationContents.size() > 0) {
            for (int i = 0; i < displayAdapter.getCount(); i++) {
                Display display = displayAdapter.getItem(i);
                PresentationContents content = mSavedPresentationContents
                        .get(display.getDisplayId());
                if (content != null) {
                    showPresentation(display, content);
                }
            }
            // 恢复完成后清空mSavedPresentationContents中的内容
            mSavedPresentationContents.clear();
        }
        // 注册屏幕的监听事件
        displayManager.registerDisplayListener(mDisplayListener, null);
    }

    /**
     * 当activity不可见时保存presentation中显示的内容 将所有屏幕的presentation解除
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressLint("NewApi")
	@Override
    protected void onPause() {
        super.onPause();
        // 注销掉对屏幕的监听
        displayManager.unregisterDisplayListener(mDisplayListener);
        // 遍历当前所有可见的presentation
        for (int i = 0; i < mActivePresentations.size(); i++) {
            // 如果当前的presentation是可见的则将共内容保存
            DemoPresentation presentation = mActivePresentations.valueAt(i);
            int displayId = mActivePresentations.keyAt(i);
            if (presentation != null) {
                // 将所有display的显示内容保存
                mSavedPresentationContents.put(displayId,
                        presentation.getmContents());
                presentation.dismiss();
            }
            // 清空所mActivePresentations数组（所有presentation都不可见了）
            mActivePresentations.clear();
        }
    }

    /**
     * 保存presentation的状态信息，用于当activity被非正常杀死时再次调用oncreate方法重建activity时 恢复状态
     */
    @SuppressLint("NewApi")
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSparseParcelableArray(PRESENTATION_KEY,
                mSavedPresentationContents);
    }

    @SuppressLint("NewApi")
	private DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {

        @Override
        public void onDisplayRemoved(int displayId) {
            displayAdapter.updateListView();
        }

        @Override
        public void onDisplayChanged(int displayId) {
            displayAdapter.updateListView();
        }

        @Override
        public void onDisplayAdded(int displayId) {
            displayAdapter.updateListView();
        }
    };
}