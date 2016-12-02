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
		tv_title.setText("��������");	
		
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(MyInfoActivity.this));//imageloader�����ʼ��
		myLocationManager=new MyLocationManager(this, userManager);  //��ȡ�û��Ķ�λ��Ϣ���ϴ���bmob
		
		layout_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAvatarPop();    //�޸�ͷ��
			}
		});
		layout_nick.setOnClickListener(new OnClickListener() {//�޸��ǳ�			
			@Override
			public void onClick(View v) { //�޸��ǳ�
				Intent intentNickChange=new Intent(MyInfoActivity.this,NickChange.class);
				startActivity(intentNickChange);
			}
		});
		layout_gender.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showSexChooseDialog();  //�޸��Ա�
			}
		});
		
		layout_change_pwd.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intentPwd=new Intent(MyInfoActivity.this,PwdChangeActivity.class);
				startActivity(intentPwd);				
			}
		});

		//�˳�����
		btn_exit_login.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.d("TOUCH", "�˳�����");
				SharePreferenceUtils.putBoolean(getApplicationContext(), "is_login", false);
				final ProgressDialog progress = new ProgressDialog(
						MyInfoActivity.this);
				progress.setMessage("�����˳�����...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						BmobUser.logOut(MyApplication.getContext());//��������û�����						
						Intent intent=new Intent(MyInfoActivity.this,LoginActivity.class);
						startActivity(intent);
						progress.dismiss();
						finish();
					}
				}, 1000);
				
				
				
			}
		});
		
		
	}

	//��ʼ���ҵ�����
	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		Log.d("TOUCH","hight = "+user.getHight()+",sex= "+user.getSex()+"avar="+user.getAvatar());
		updateUser(user);  //�����ҵ�����
		
		SharePreferenceUtils.putBoolean(MyInfoActivity.this, "is_login", true);
		SharePreferenceUtils.putString(MyInfoActivity.this, "user_nick", user.getNick());
		SharePreferenceUtils.putString(MyInfoActivity.this, "user_account", user.getUsername());		
		SharePreferenceUtils.putString(MyInfoActivity.this, "avatar", user.getAvatar());
		
	}
	
	//�����ҵ�����
	private void updateUser(User user) {
		// ����
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");		
	}
	@Override
	public void onResume() {
		super.onResume();
			initMeData();
	}
	
	/**
	 * ����ͷ��
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
			public void onClick(View v) { // ����				
				Log.d("TOUCH", "����click");
				File dir = new File(GlobalContants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// ԭͼ
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
				filePath = file.getAbsolutePath();// ��ȡ��Ƭ�ı���·��
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, GlobalContants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		// �������ѡ��
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				Log.d("TOUCH", "���click");
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, GlobalContants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});
		//PopupWindow���������ʵ��һ�������򣬿���ʹ�����Ⲽ�ֵ�View��Ϊ�����ݣ�����������������ڵ�ǰactivity֮�ϵġ�
		avatorPop = new PopupWindow(view, mScreenWidth, 600);
		avatorPop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatorPop.dismiss();
					return true; // �����������true�Ļ���touch�¼���������
	                // ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
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
		// ����Ч�� �ӵײ�����
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// ����������ת
	int degree = 0;

	@Override // activity�ص�
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case GlobalContants.REQUESTCODE_UPLOADAVATAR_CAMERA:// �����޸�ͷ��
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "���պ�ĽǶȣ�" + degree);
				startImageAction(Uri.fromFile(file), 200, 200, GlobalContants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case GlobalContants.REQUESTCODE_UPLOADAVATAR_LOCATION:// �����޸�ͷ��
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200, GlobalContants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("��Ƭ��ȡʧ��");
			}

			break;
		case GlobalContants.REQUESTCODE_UPLOADAVATAR_CROP:// �ü�ͷ�񷵻�
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "ȡ��ѡ��", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// ��ʼ���ļ�·��
			filePath = "";
			// �ϴ�ͷ��
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	private void uploadAvatar() {
		BmobLog.i("ͷ���ַ��" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String url = bmobFile.getFileUrl(MyInfoActivity.this);
				// ����BmobUser����
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("ͷ���ϴ�ʧ�ܣ�" + msg);
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
				ShowToast("ͷ����³ɹ���");
				// ����ͷ��
				// refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("ͷ�����ʧ�ܣ�" + msg);
			}
		});
	}

	String path;

	/**
	 * ����ü���ͷ��
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
				// ����ͼƬ
				String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
				path = GlobalContants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(GlobalContants.MyAvatarDir, filename, bitmap, true);
				// �ϴ�ͷ��
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

	// �����û�����
	private void updateUserData(User user, UpdateListener listener) {
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}

	/**
	 * ����ͷ�� refreshAvatar
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
	 
	String[] sexs = new String[]{ "��", "Ů" };
	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
		.setTitle("��ѡ��")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(sexs, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						BmobLog.i("�������"+sexs[which]);
						updateInfo(which);
						dialog.dismiss();
					}
				})
		.setNegativeButton("ȡ��", null)
		.show();
	}
	/** �޸�����
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
				ShowToast("�޸ĳɹ�");
				tv_set_gender.setText(u.getSex() == true ? "��" : "Ů");
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
