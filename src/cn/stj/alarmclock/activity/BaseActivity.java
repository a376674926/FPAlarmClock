package cn.stj.alarmclock.activity;

import cn.stj.alarmclock.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.location.GpsStatus.Listener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 界面的基类
 * 
 * @author jackey
 * 
 */
public abstract class BaseActivity extends Activity implements
		View.OnClickListener {

	private static final String TAG = BaseActivity.class.getSimpleName();

	private View mRootView;
	protected ViewStub mAboveViewStub;
	protected ViewStub mMiddleViewStub;
	protected ViewStub mBelowViewStub;

	private Button mLeftBtn;
	private Button mMidBtn;
	private Button mRightBtn;
	private TextView mTopTitle;

	private View mInitTopTitle;
	private BottomKeyClickListener mBottomKeyClickListener;

	/**
	 * 显示Toast提示框
	 * 
	 * @param msg
	 *            显示提示的字符串
	 */
	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 *            启动类的类型
	 */
	protected void launchActivity(Class<?> pClass) {
		launchActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 *            启动类的类型
	 * @param pBundle
	 *            封装的数据
	 */
	protected void launchActivity(Class<?> pClass, Bundle pBundle) {
		launchActivity(pClass, pBundle, -1);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 *            启动类的类型
	 * @param pBundle
	 *            封装的数据
	 */
	protected void launchActivity(Class<?> pClass, Bundle pBundle,
			int intentFlag) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		if (intentFlag != -1) {
			intent.addFlags(intentFlag);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity
	 * 
	 * @param pAction
	 */
	protected void launchActivity(String pAction) {
		launchActivity(pAction, null);
	}

	/**
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void launchActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		mRootView = LayoutInflater.from(this).inflate(R.layout.activity_base,
				null);
		setContentView(mRootView);

		mAboveViewStub = (ViewStub) findViewById(R.id.middle_list_above_viewstub);
		mMiddleViewStub = (ViewStub) findViewById(R.id.middle_list_middle_viewstub);
		mBelowViewStub = (ViewStub) findViewById(R.id.middle_list_below_viewstub);
		buildButtons();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	protected void setActivityBgDrawable(Drawable drawable) {
		mRootView.setBackgroundDrawable(drawable);
	}

	protected void setActivityBgResource(int resid) {
		mRootView.setBackgroundResource(resid);
	}

	protected void setTopTitleDrawable(Drawable drawable) {
		if (mInitTopTitle != null) {
			mInitTopTitle.setBackgroundDrawable(drawable);
		}
	}

	protected void setTopTitleBgResource(int resid) {
		if (mInitTopTitle != null) {
			mInitTopTitle.setBackgroundResource(resid);
		}
	}

	protected void setBottomButtonsDrawable(Drawable drawable) {
		RelativeLayout layout = (RelativeLayout) mRootView
				.findViewById(R.id.bottom_layout);
		layout.setBackgroundDrawable(drawable);
	}

	protected void setBottomButtonsResource(int resid) {
		RelativeLayout layout = (RelativeLayout) mRootView
				.findViewById(R.id.bottom_layout);
		layout.setBackgroundResource(resid);
	}

	private void buildButtons() {
		mLeftBtn = (Button) findViewById(R.id.bottom_left_button);
		mMidBtn = (Button) findViewById(R.id.bottom_middle_button);
		mRightBtn = (Button) findViewById(R.id.bottom_right_button);

		mLeftBtn.setOnClickListener(this);
		mMidBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mTopTitle = (TextView) findViewById(R.id.top_title);

	}

	protected void setLeftBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mLeftBtn.setVisibility(View.GONE);
		} else {
			mLeftBtn.setVisibility(View.VISIBLE);
			mLeftBtn.setText(text);
		}

	}

	protected void setMidBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mMidBtn.setVisibility(View.GONE);
		} else {
			mMidBtn.setVisibility(View.VISIBLE);
			mMidBtn.setText(text);
		}
	}

	protected void setRightText(String text) {
		if (TextUtils.isEmpty(text)) {
			mRightBtn.setVisibility(View.GONE);
		} else {
			mRightBtn.setVisibility(View.VISIBLE);
			mRightBtn.setText(text);
		}
	}

	protected void setTopTitleText(String text) {
		if (TextUtils.isEmpty(text)) {
			mTopTitle.setVisibility(View.GONE);
		} else {
			mTopTitle.setVisibility(View.VISIBLE);
			mTopTitle.setText(text);
		}

	}

	@Override
	public void onClick(View v) {
		if (mBottomKeyClickListener == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.bottom_left_button:
			mBottomKeyClickListener.onLeftKeyPress();
			break;
		case R.id.bottom_middle_button:
			mBottomKeyClickListener.onMiddleKeyPress();
			break;
		case R.id.bottom_right_button:
			mBottomKeyClickListener.onRightKeyPress();
			break;
		default:
			break;
		}
	}

	public interface BottomKeyClickListener {

		public void onLeftKeyPress();

		public void onMiddleKeyPress();

		public void onRightKeyPress();
	}

	public void setBottomKeyClickListener(BottomKeyClickListener l) {
		if (l != null) {
			mBottomKeyClickListener = l;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Log.d(TAG, "onKeyDown--->>KEYCODE_BACK");
			// 物理返回键
			mBottomKeyClickListener.onRightKeyPress();
			break;
		case KeyEvent.KEYCODE_MENU:
			Log.d(TAG, "onKeyDown--->>KEYCODE_MENU");
			// 菜单键
			mBottomKeyClickListener.onLeftKeyPress();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
