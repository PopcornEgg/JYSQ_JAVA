package com.yxkj.jyb;

import java.io.ByteArrayOutputStream;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.ForumDataMgr.ForumPostItem;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoViewer extends Activity implements OnTouchListener {

	private static final String TAG = "PhotoViewer";
	public static final int RESULT_CODE_NOFOUND = 200;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;
	ImageView imgView;
	static public Bitmap sbitmap = null;
	static public String sUrl = "";
	static ForumThreadItem sForumThreadItem = null;
	static ForumPostItem sForumPostItem = null;
	static public int sType = 0;

	/** ��С���ű��� */
	float minScaleR = 1.0f;
	/** ������ű��� */
	static final float MAX_SCALE = 10f;

	/** ��ʼ״̬ */
	static final int NONE = 0;
	/** �϶� */
	static final int DRAG = 1;
	/** ���� */
	static final int ZOOM = 2;

	/** ��ǰģʽ */
	int mode = NONE;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;

	Activity mActivity;
	View answerly = null;
	View oply = null;
	private EditText mEdit = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoviewer);
		mActivity = this;

		// ��ȡͼƬ��Դ
		if (!sUrl.isEmpty()) {
			sbitmap = getBitmapByCache(sUrl);
		}
		if (sbitmap == null)
			return;
		imgView = (ImageView) findViewById(R.id.image);// ��ȡ�ؼ�
		imgView.setImageBitmap(sbitmap);// ���ؼ�
		imgView.setOnTouchListener(this);// ���ô�������

		// View _v = findViewById(R.id.rl_img);
		// LayoutParams ps = (LayoutParams) _v.getLayoutParams();
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
		// minZoom();
		center();
		imgView.setImageMatrix(matrix);

		ImageButton btn_back = (ImageButton) findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mActivity.finish();
			}
		});
		ImageButton btn_ra = (ImageButton) findViewById(R.id.ra);
		btn_ra.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				rotateImg(90);
			}
		});

		answerly = findViewById(R.id.answerly);
		oply = findViewById(R.id.oply);

		if (sType == 1) {
			ImageButton btn_reply = (ImageButton) findViewById(R.id.reply);
			btn_reply.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					answerly.setVisibility(View.VISIBLE);
					oply.setVisibility(View.INVISIBLE);
					GlobalUtility.Func.showSoftInput(mActivity, mEdit);
				}
			});

			findViewById(R.id.photo).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					GlobalUtility.Func.hideSoftInput(mActivity, mEdit);
					if (!UserUtils.DataUtils.isLogined()) {
						UserLogin.showLogin(mActivity, new CallBackInterface() {
							@Override
							public void exectueMethod(Object p) {
								CameraCroperAct.show(mActivity);
							}
						});
					} else
						CameraCroperAct.show(mActivity);
				}
			});

			mEdit = (EditText) findViewById(R.id.editText);
			mEdit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						// �˴�Ϊ�õ�����ʱ�Ĵ�������
					} else {
						// �˴�Ϊʧȥ����ʱ�Ĵ�������
						oply.setVisibility(View.VISIBLE);
						answerly.setVisibility(View.INVISIBLE);
					}
				}
			});

			findViewById(R.id.send).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (!NetWorkStateDetector.isConnectingToInternet()) {
						ShowToast("��ǰ������");
						return;
					}

					if (!UserUtils.DataUtils.isLogined()) {
						UserLogin.showLogin(mActivity, new CallBackInterface() {
							@Override
							public void exectueMethod(Object p) {
							}
						});
					} else {
						String rp = mEdit.getText().toString();
						if (rp.isEmpty()) {
							ShowToast("�ظ����ݲ���Ϊ��");
							return;
						}
						asynPostReply(rp, 0, "");
						mEdit.setText("");
					}
					GlobalUtility.Func.hideSoftInput(mActivity, mEdit);
				}
			});
		} else if (sType == 2) {
			findViewById(R.id.reply).setVisibility(View.INVISIBLE);
		}
	}

	public void SureOnClick(View v) {

	}

	/**
	 * ��������
	 */
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// ���㰴��
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		// ���㰴��
		case MotionEvent.ACTION_POINTER_DOWN:
			dist = spacing(event);
			// �����������������10�����ж�Ϊ���ģʽ
			if (spacing(event) > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			// savedMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
					//savedMatrix.set(matrix);
					// matrix.postRotate(curDegrees, mid.x, mid.y);
				}
			}
			break;
		}
		imgView.setImageMatrix(matrix);
		CheckView();
		return true;
	}

	/**
	 * ���������С���ű������Զ�����
	 */
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
//		if (mode == ZOOM) {
//			if (p[0] < minScaleR) {
//				// Log.d("", "��ǰ���ż���:"+p[0]+",��С���ż���:"+minScaleR);
//				matrix.setScale(minScaleR, minScaleR);
//				matrix.setRotate(curDegrees);
//			}
//			if (p[0] > MAX_SCALE) {
//				// Log.d("", "��ǰ���ż���:"+p[0]+",������ż���:"+MAX_SCALE);
//				matrix.set(savedMatrix);
//				matrix.setRotate(curDegrees);
//			}
//		}
		center();
	}

	/**
	 * ��С���ű��������Ϊ100%
	 */
	private void minZoom() {
		minScaleR = Math.min(
				(float) dm.widthPixels / (float) sbitmap.getWidth(),
				(float) dm.heightPixels / (float) sbitmap.getHeight());
		if (minScaleR < 1.0) {
			matrix.postScale(minScaleR, minScaleR);
		}
	}

	private void center() {
		center(true, true);
	}

	/**
	 * �����������
	 */
	protected void center(boolean horizontal, boolean vertical) {

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, sbitmap.getWidth(), sbitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = imgView.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * ����ľ���
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * ������е�
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private int curDegrees = 0;

	private void rotateImg(int degrees) {

		// ������ת�Ƕ�
		curDegrees += degrees;
		matrix.setRotate(curDegrees);

		// ���»���Bitmap
		// sbitmap = Bitmap.createBitmap(sbitmap, 0, 0,
		// sbitmap.getWidth(),sbitmap.getHeight(), matrix, true);
		// imgView.setImageBitmap(sbitmap);

		// imgView.setImageMatrix( matrix);
		CheckView();
		imgView.setImageMatrix(matrix);
		//savedMatrix.set(matrix);
	}

	private Bitmap getBitmapByCache(String url) {

		ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);// �ȼ��ԭͼ
		if (imginfo != null) {
			return imginfo.bitmap;
		} else {
			String urlscaling = url + "/scaling";
			imginfo = ImageDataMgr.sImageCacheMgr.get(urlscaling);// �ټ������ͼ
			if (imginfo != null) {
				// ����ԭͼ
				ImageDataMgr.sDownLoadMgr.addLoad(url, null, 2, mLoadHandler);
				return imginfo.bitmap;
			}
		}
		return null;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (CameraCroperAct.sOutBitmap != null) {
			uplaod(CameraCroperAct.sOutBitmap);
			CameraCroperAct.sOutBitmap = null;// ����ǵ�����
		}
	}

	private void uplaod(Bitmap bm) {

		if (bm == null) {
			return;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] datas = baos.toByteArray();
		ImageDataMgr.sUpLoadMgr.addLoad(datas, 1, mLoadHandler);
	}

	public LoadHandler mLoadHandler = new LoadHandler();

	class LoadHandler extends Handler {
		/**
		 * �������̴߳��ݵ���Ϣ����
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {
			case 1: {
				int tp = msg.arg1;
				if (tp == 1) {
					String rp = mEdit.getText().toString();
					mEdit.setText("");
					asynPostReply(rp, 2, GlobalUtility.Config.ImageMainUrl
							+ msg.obj.toString());
				}
			}
			case 2: {
				sbitmap = getBitmapByCache(sUrl);
				if (sbitmap != null) {
					imgView.setImageBitmap(sbitmap);// ���ؼ�
					CheckView();
					imgView.setImageMatrix(matrix);
				}
			}
			}
		}
	}

	public void ShowToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@TAInject
	private void asynPostReply(String str, Integer attachment, String url) 
	{
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumPostNewReplyUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("fid", sForumThreadItem.fid);
		params.put("tid", sForumThreadItem.tid);
		params.put("message", str);
		params.put("typeid", "0");
		params.put("tag", "");
		params.put("subject", url);
		params.put("touid", sForumThreadItem.authorid);
		params.put("realname", UserUtils.DataUtils.getNickName());
		params.put("attachment", attachment.toString());

		sForumThreadItem.replies++;
		FragmentPage2.updateThreadReply();
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content) {
				String pid = content.replace("\r\n", "");
				pid = pid.substring(1);
				int ipid = Integer.parseInt(pid);
				if (ipid >= 0) {
					ShowToast("�ظ��ɹ�");
				} else
					ShowToast("�ظ�ʧ��");
			}

			@Override
			public void onFailure(String error) {
				ShowToast("�ظ�ʧ��");
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sbitmap = null;
		sUrl = "";
		sForumThreadItem = null;
		sForumPostItem = null;
	}

	static public void show(Context _c) {
		if (_c != null) {
			Intent intent = new Intent(_c, PhotoViewer.class);
			_c.startActivity(intent);
		}
	}
}