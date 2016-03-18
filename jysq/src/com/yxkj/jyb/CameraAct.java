package com.yxkj.jyb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.yxkj.jyb.FragmentPage2.LoadHandler;
import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.Utils.CameraUtils;
import com.yxkj.jyb.ui.CropImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.OrientationEventListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CameraAct extends Activity {

	Camera mCamera;
	ImageButton snap;
	Button switchCamera;
	SurfaceView surfaceView;
	int camera_id = 0;
	IOrientationEventListener iOriListener;
	final int SUCCESS = 233;
	final int GOTO_CROPER = 1;
	SnapHandler handler = new SnapHandler();
	int camera_direction = CameraInfo.CAMERA_FACING_BACK; // 摄像头方向
	private boolean mOpencroper = false;
	static private byte[] curCameraBytes = null;
	
	public void switchCamera() {
		if (camera_direction == CameraInfo.CAMERA_FACING_BACK) {
			camera_direction = CameraInfo.CAMERA_FACING_FRONT;
		} else {
			camera_direction = CameraInfo.CAMERA_FACING_BACK;
		}
		int mNumberOfCameras = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == camera_direction) {
				camera_id = i;
			}
		}
		if (null != mCamera) {
			mCamera.stopPreview();
			mCamera.release();
		}
		mCamera = Camera.open(camera_id);
		try {
			mCamera.setPreviewDisplay(surfaceView.getHolder());
			mCamera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setCameraAndDisplay(surfaceView.getWidth(), surfaceView.getHeight());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 显示界面
		setContentView(R.layout.cameraact);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		switchCamera = (Button) this.findViewById(R.id.switch_btn);
		switchCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchCamera();
			}
		});
		snap = (ImageButton) this.findViewById(R.id.snap);
		snap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.takePicture(null, null, new PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						
						curCameraBytes = data;
						handler.sendEmptyMessage(GOTO_CROPER);
						
						// TODO Auto-generated method stub
						/*final byte[] tempdata = data;
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								File dir = new File("mnt/sdcard/testcamera");
								if (!dir.exists()) {
									dir.mkdirs();// 创建文件夹
								}
								String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance()) + ".jpg";
								File f = new File("mnt/sdcard/testcamera/" + name);
								if (!f.exists()) {
									try {
										f.createNewFile();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								FileOutputStream outputStream;
								try {
									outputStream = new FileOutputStream(f);
									outputStream.write(tempdata); // 写入sd卡中
									outputStream.close(); // 关闭输出流
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} // 文件输出流
								catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Log.v("TestCameraActivityTag", "store success");
								handler.sendEmptyMessage(SUCCESS);
							}
						});
						// 启动存储照片的线程
						thread.start();*/
					}
				});
			}
		});

		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		surfaceView.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				int mNumberOfCameras = Camera.getNumberOfCameras();

				// Find the ID of the default camera
				CameraInfo cameraInfo = new CameraInfo();
				for (int i = 0; i < mNumberOfCameras; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
						camera_id = i;
					}
				}
				mCamera = Camera.open(camera_id);
				try {
					mCamera.setPreviewDisplay(holder);
					mCamera.startPreview(); // 开始预览

					iOriListener.enable();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				setCameraAndDisplay(width, height);
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if (null != mCamera) {
					mCamera.release();
					mCamera = null;
				}
			}
		});// 为SurfaceView的句柄添加一个回调函数

		iOriListener = new IOrientationEventListener(this);

		this.findViewById(R.id.camera_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraAct.this.finish();
			}
		});
		
		Intent intent = this.getIntent();
		mOpencroper = intent.getBooleanExtra("opencroper", false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.iOriListener.disable();
		
		if(mOpencroper){
		}
	}

	public class IOrientationEventListener extends OrientationEventListener {

		public IOrientationEventListener(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onOrientationChanged(int orientation) {
			// TODO Auto-generated method stub
			if (ORIENTATION_UNKNOWN == orientation) {
				return;
			}
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(camera_id, info);
			orientation = (orientation + 45) / 90 * 90;
			int rotation = 0;
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				rotation = (info.orientation - orientation + 360) % 360;
			} else {
				rotation = (info.orientation + orientation) % 360;
			}
			if (null != mCamera) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setRotation(rotation);
				mCamera.setParameters(parameters);
			}
		}
	}

	public void setCameraAndDisplay(int width, int height) {
		Camera.Parameters parameters = mCamera.getParameters();
		/* 获取摄像头支持的PictureSize列表 */
		List<Camera.Size> pictureSizeList = parameters
				.getSupportedPictureSizes();
		/* 从列表中选取合适的分辨率 */
		Size picSize = CameraUtils.getProperSize(pictureSizeList,
				((float) width) / height);
		if (null != picSize) {
			parameters.setPictureSize(picSize.width, picSize.height);
		} else {
			picSize = parameters.getPictureSize();
		}
		/* 获取摄像头支持的PreviewSize列表 */
		List<Camera.Size> previewSizeList = parameters
				.getSupportedPreviewSizes();
		Size preSize = CameraUtils.getProperSize(previewSizeList,
				((float) width) / height);
		if (null != preSize) {
			Log.v("TestCameraActivityTag", preSize.width + "," + preSize.height);
			parameters.setPreviewSize(preSize.width, preSize.height);
		}

		/* 根据选出的PictureSize重新设置SurfaceView大小 */
		float w = picSize.width;
		float h = picSize.height;
		surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
				(int) (height * (w / h)), height));

		parameters.setJpegQuality(100); // 设置照片质量

		// 先判断是否支持，否则会报错
		if (parameters.getSupportedFocusModes().contains(
				Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			parameters
					.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		}
		mCamera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
		mCamera.setDisplayOrientation(0);
		mCamera.setParameters(parameters);
	}

	class SnapHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == SUCCESS) {
				Toast.makeText(CameraAct.this, "照片存储至testcamera文件夹",
						Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == GOTO_CROPER) {
				if(curCameraBytes != null && curCameraBytes.length > 0){
					
					try {
						mCamera.setPreviewDisplay(surfaceView.getHolder());
						mCamera.startPreview();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mCamera.setPreviewCallback(null) ;
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
					
					CameraAct.this.finish();
				}
			}
			
//			try {
//				mCamera.setPreviewDisplay(surfaceView.getHolder());
//				mCamera.startPreview();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	static public void show(Context _c, boolean opencroper){
		if(_c != null){
			Intent intent = new Intent(_c, CameraAct.class);
			intent.putExtra("opencroper", opencroper);
			_c.startActivity(intent);
		}
	}
}
