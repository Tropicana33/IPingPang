package com.tropicana.ipingpang;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tropicana.ipingpang.application.MyApplication;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.location.MyLocationManager;
import com.tropicana.ipingpang.utils.ImageLoadOptions;
import com.tropicana.ipingpang.utils.LogUtils;
import com.tropicana.ipingpang.utils.PhotoUtil;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MyInfoActivity extends BaseActivity {

	private View view;
	private TextView tv_set_nick, tv_set_name, tv_set_gender;
	private ImageView iv_set_avator;
	private RelativeLayout layout_head, layout_nick, layout_gender,layout_change_pwd;
	private Button btn_exit_login;
	LinearLayout layout_all;
    private ImageLoader imageLoader;
    private MyLocationManager myLocationManager;
    

	@Override
	public void initData() {
		view = View.inflate(getApplicationContext(), R.layout.activity_my_info, null);
		tv_set_nick = (TextView) view.findViewById(R.id.tv_set_nick);
		tv_set_name = (TextView) view.findViewById(R.id.tv_set_name);
		tv_set_gender = (TextView) view.findViewById(R.id.tv_set_gender);
		iv_set_avator = (ImageView) view.findViewById(R.id.iv_set_avator);
		layout_head = (RelativeLayout) view.findViewById(R.id.layout_head);
		layout_nick = (RelativeLayout) view.findViewById(R.id.layout_nick);
		layout_gender = (RelativeLayout) view.findViewById(R.id.layout_gender);
		layout_all = (LinearLayout) view.findViewById(R.id.layout_all);
		layout_change_pwd=(RelativeLayout) view.findViewById(R.id.layout_change_pwd);
		btn_exit_login=(Button) view.findViewById(R.id.btn_exit_login);
		fl_base.addView(view);
		tv_title.setText("个人资料");	
		
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(MyInfoActivity.this));//imageloader必须初始化
		myLocationManager=new MyLocationManager(this, userManager);  //获取用户的定位信息并上传到bmob
		
		layout_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAvatarPop();    //修改头像
			}
		});
		layout_nick.setOnClickListener(new OnClickListener() {//修改昵称			
			@Override
			public void onClick(View v) { //修改昵称
				Intent intentNickChange=new Intent(MyInfoActivity.this,NickChange.class);
				startActivity(intentNickChange);
			}
		});
		layout_gender.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showSexChooseDialog();  //修改性别
			}
		});
		
		layout_change_pwd.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intentPwd=new Intent(MyInfoActivity.this,PwdChangeActivity.class);
				startActivity(intentPwd);				
			}
		});

		//退出登入
		btn_exit_login.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.d("TOUCH", "退出登入");
				SharePreferenceUtils.putBoolean(getApplicationContext(), "is_login", false);
				final ProgressDialog progress = new ProgressDialog(
						MyInfoActivity.this);
				progress.setMessage("正在退出登入...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						BmobUser.logOut(MyApplication.getContext());//清除缓存用户对象						
						Intent intent=new Intent(MyInfoActivity.this,LoginActivity.class);
						startActivity(intent);
						progress.dismiss();
						finish();
					}
				}, 1000);
				
				
				
			}
		});
		
		
	}

	//初始化我的资料
	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		Log.d("TOUCH","hight = "+user.getHight()+",sex= "+user.getSex()+"avar="+user.getAvatar());
		updateUser(user);  //更新我的资料
		
		SharePreferenceUtils.putBoolean(MyInfoActivity.this, "is_login", true);
		SharePreferenceUtils.putString(MyInfoActivity.this, "user_nick", user.getNick());
		SharePreferenceUtils.putString(MyInfoActivity.this, "user_account", user.getUsername());		
		SharePreferenceUtils.putString(MyInfoActivity.this, "avatar", user.getAvatar());
		
	}
	
	//更新我的资料
	private void updateUser(User user) {
		// 更改
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");		
	}
	@Override
	public void onResume() {
		super.onResume();
			initMeData();
	}
	
	/**
	 * 设置头像
	 */
	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;
	public String filePath = "";

	private void showAvatarPop() {
		Log.d("TOUCH", "showAvatarPop");
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		layout_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { // 拍照				
				Log.d("TOUCH", "拍照click");
				File dir = new File(GlobalContants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 原图
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
				filePath = file.getAbsolutePath();// 获取相片的保存路径
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, GlobalContants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		// 从相册中选择
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				Log.d("TOUCH", "相册click");
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, GlobalContants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});
		//PopupWindow这个类用来实现一个弹出框，可以使用任意布局的View作为其内容，这个弹出框是悬浮在当前activity之上的。
		avatorPop = new PopupWindow(view, mScreenWidth, 600);
		avatorPop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatorPop.dismiss();
					return true; // 这里如果返回true的话，touch事件将被拦截
	                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
				}
				return false;
			}
		});

		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override // activity回调
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case GlobalContants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200, GlobalContants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case GlobalContants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200, GlobalContants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case GlobalContants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String url = bmobFile.getFileUrl(MyInfoActivity.this);
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	private void updateUserAvatar(final String url) {
		User u = new User();
		u.setAvatar(url);
		updateUserData(u, new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("头像更新成功！");
				// 更新头像
				// refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}

	String path;

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
				path = GlobalContants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(GlobalContants.MyAvatarDir, filename, bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	/**
	 * @Title: startImageAction @return void @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); 
		startActivityForResult(intent, requestCode);
	}

	// 更新用户数据
	private void updateUserData(User user, UpdateListener listener) {
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}

	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
					ImageLoadOptions.getOptions()); 
		} else {
			iv_set_avator.setImageResource(R.drawable.default_head);
		}
	}
	 
	String[] sexs = new String[]{ "男", "女" };
	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
		.setTitle("单选框")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(sexs, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						BmobLog.i("点击的是"+sexs[which]);
						updateInfo(which);
						dialog.dismiss();
					}
				})
		.setNegativeButton("取消", null)
		.show();
	}
	/** 修改资料
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(int which) {
		final User u = new User();
		if(which==0){
			u.setSex(true);
		}else{
			u.setSex(false);
		}
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				ShowToast("修改成功");
				tv_set_gender.setText(u.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}
	
	@Override
	public void back() {
		finish();
	}
	
}
