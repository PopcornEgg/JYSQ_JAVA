package com.yxkj.jyb.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Region;

public class CropImageView extends View {

	// ��touch��Ҫ�õ��ĵ㣬
	private float mX_1 = 0;
	private float mY_1 = 0;
	// �����¼��ж�
	private final int STATUS_SINGLE = 1;
	private final int STATUS_MULTI_START = 2;
	private final int STATUS_MULTI_TOUCHING = 3;
	// ��ǰ״̬
	private int mStatus = STATUS_SINGLE;
	// Ĭ�ϲü��Ŀ��
	private int cropWidth;
	private int cropHeight;
	// ����Drawable���ĸ���
	private final int EDGE_LT = 1;
	private final int EDGE_RT = 2;
	private final int EDGE_LB = 3;
	private final int EDGE_RB = 4;
	private final int EDGE_MOVE_IN = 5;
	private final int EDGE_MOVE_OUT = 6;
	private final int EDGE_NONE = 7;

	public int currentEdge = EDGE_NONE;

	protected float oriRationWH = 0;
	protected final float maxZoomOut = 5.0f;
	protected final float minZoomIn = 0.333333f;

	protected Drawable mDrawable;
	protected FloatDrawable mFloatDrawable;

	protected Rect mDrawableSrc = new Rect();// ͼƬRect�任ʱ��Rect
	protected Rect mDrawableDst = new Rect();// ͼƬRect
	protected Rect mDrawableFloat = new Rect();// �����Rect
	protected boolean isFrist = true;
	private boolean isTouchInSquare = true;

	protected Context mContext;

	public CropImageView(Context context) {
		super(context);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	@SuppressLint("NewApi")
	private void init(Context context) {
		this.mContext = context;
		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				this.setLayerType(LAYER_TYPE_SOFTWARE, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mFloatDrawable = new FloatDrawable(context);
	}

	public void setDrawable(Drawable mDrawable, int cropWidth, int cropHeight) {
		this.mDrawable = mDrawable;
		this.mDrawable.setBounds(0, 0, cropWidth, cropHeight);
		this.cropWidth = cropWidth;
		this.cropHeight = cropHeight;
		this.isFrist = true;
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getPointerCount() > 1) {
			if (mStatus == STATUS_SINGLE) {
				mStatus = STATUS_MULTI_START;
			} else if (mStatus == STATUS_MULTI_START) {
				mStatus = STATUS_MULTI_TOUCHING;
			}
		} else {
			if (mStatus == STATUS_MULTI_START
					|| mStatus == STATUS_MULTI_TOUCHING) {
				mX_1 = event.getX();
				mY_1 = event.getY();
			}

			mStatus = STATUS_SINGLE;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mX_1 = event.getX();
			mY_1 = event.getY();
			currentEdge = getTouch((int) mX_1, (int) mY_1);
			isTouchInSquare = mDrawableFloat.contains((int) event.getX(), (int) event.getY());
			break;
		case MotionEvent.ACTION_UP:
			checkBounds();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			currentEdge = EDGE_NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mStatus == STATUS_MULTI_TOUCHING) {

			} else if (mStatus == STATUS_SINGLE) {
				int dx = (int) (event.getX() - mX_1);
				int dy = (int) (event.getY() - mY_1);

				mX_1 = event.getX();
				mY_1 = event.getY();
				// �����õ�����һ���ǣ����ұ任Rect
				if (!(dx == 0 && dy == 0)) {
					switch (currentEdge) {
					case EDGE_LT:
						mDrawableFloat.set(mDrawableFloat.left + dx,
								mDrawableFloat.top + dy, mDrawableFloat.right,
								mDrawableFloat.bottom);
						break;

					case EDGE_RT:
						mDrawableFloat.set(mDrawableFloat.left,
								mDrawableFloat.top + dy, mDrawableFloat.right
										+ dx, mDrawableFloat.bottom);
						break;

					case EDGE_LB:
						mDrawableFloat.set(mDrawableFloat.left + dx,
								mDrawableFloat.top, mDrawableFloat.right,
								mDrawableFloat.bottom + dy);
						break;

					case EDGE_RB:
						mDrawableFloat.set(mDrawableFloat.left,
								mDrawableFloat.top, mDrawableFloat.right + dx,
								mDrawableFloat.bottom + dy);
						break;

					case EDGE_MOVE_IN:
						if (isTouchInSquare) {
							mDrawableFloat.offset((int) dx, (int) dy);
						}
						break;

					case EDGE_MOVE_OUT:
						break;
					}
					mDrawableFloat.sort();
					invalidate();
				}
			}
			break;
		}

		return true;
	}

	// ���ݳ��������ж��Ǵ�����Rect��һ����
	public int getTouch(int eventX, int eventY) {
		if (mFloatDrawable.getBounds().left <= eventX
				&& eventX < (mFloatDrawable.getBounds().left + mFloatDrawable
						.getBorderWidth())
				&& mFloatDrawable.getBounds().top <= eventY
				&& eventY < (mFloatDrawable.getBounds().top + mFloatDrawable
						.getBorderHeight())) {
			return EDGE_LT;
		} else if ((mFloatDrawable.getBounds().right - mFloatDrawable
				.getBorderWidth()) <= eventX
				&& eventX < mFloatDrawable.getBounds().right
				&& mFloatDrawable.getBounds().top <= eventY
				&& eventY < (mFloatDrawable.getBounds().top + mFloatDrawable
						.getBorderHeight())) {
			return EDGE_RT;
		} else if (mFloatDrawable.getBounds().left <= eventX
				&& eventX < (mFloatDrawable.getBounds().left + mFloatDrawable
						.getBorderWidth())
				&& (mFloatDrawable.getBounds().bottom - mFloatDrawable
						.getBorderHeight()) <= eventY
				&& eventY < mFloatDrawable.getBounds().bottom) {
			return EDGE_LB;
		} else if ((mFloatDrawable.getBounds().right - mFloatDrawable
				.getBorderWidth()) <= eventX
				&& eventX < mFloatDrawable.getBounds().right
				&& (mFloatDrawable.getBounds().bottom - mFloatDrawable
						.getBorderHeight()) <= eventY
				&& eventY < mFloatDrawable.getBounds().bottom) {
			return EDGE_RB;
		} else if (mFloatDrawable.getBounds().contains(eventX, eventY)) {
			return EDGE_MOVE_IN;
		}
		return EDGE_MOVE_OUT;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (mDrawable == null) {
			return;
		}

		if (mDrawable.getIntrinsicWidth() == 0
				|| mDrawable.getIntrinsicHeight() == 0) {
			return;
		}

		configureBounds();
		// �ڻ����ϻ�ͼƬ
		mDrawable.draw(canvas);
		canvas.save();
		// �ڻ����ϻ�����FloatDrawable,Region.Op.DIFFERENCE�Ǳ�ʾRect�����Ĳ���
		canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
		// �ڽ����Ĳ����ϻ��ϻ�ɫ��������
		canvas.drawColor(Color.parseColor("#a0000000"));
		canvas.restore();
		// ������
		mFloatDrawable.draw(canvas);
	}

	protected void configureBounds() {
		// configureBounds��onDraw�����е���
		// isFirst��Ŀ���������mDrawableSrc��mDrawableFloatֻ��ʼ��һ�Σ�
		// ֮��ı仯�Ǹ���touch�¼����仯�ģ�������ÿ��ִ�����¶�mDrawableSrc��mDrawableFloat��������
		if (isFrist) {
			oriRationWH = ((float) mDrawable.getIntrinsicWidth())
					/ ((float) mDrawable.getIntrinsicHeight());

			final float scale = mContext.getResources().getDisplayMetrics().density;
			int w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth()
					* scale + 0.5f));
			int h = (int) (w / oriRationWH);

			int left = (getWidth() - w) / 2;
			int top = (getHeight() - h) / 2;
			int right = left + w;
			int bottom = top + h;

			mDrawableSrc.set(left, top, right, bottom);
			mDrawableDst.set(mDrawableSrc);
			int floatHeight = getHeight() / 3;
			mDrawableFloat.set(10, floatHeight, getWidth() - 10,floatHeight * 2);

			isFrist = false;
		}

		mDrawable.setBounds(mDrawableDst);
		mFloatDrawable.setBounds(mDrawableFloat);
	}

	// ��up�¼��е����˸÷�����Ŀ���Ǽ���Ƿ�Ѹ����ϳ�����Ļ
	protected void checkBounds() {
		int newLeft = mDrawableFloat.left;
		int newTop = mDrawableFloat.top;

		boolean isChange = false;
		if (mDrawableFloat.left < 0) {
			newLeft = 0;
			isChange = true;
		}

		if (mDrawableFloat.top < 0) {
			newTop = 0;
			isChange = true;
		}
		
		if (mDrawableFloat.right > getWidth()) {
			newLeft = getWidth() - mDrawableFloat.width();
			isChange = true;
		}

		if (mDrawableFloat.bottom > getHeight()) {
			newTop = getHeight() - mDrawableFloat.height();
			isChange = true;
		}

		if(isChange)
			mDrawableFloat.offsetTo(newLeft, newTop);
		
		boolean isNewRect = false;
		int newRight = mDrawableFloat.right;
		int newBottom = mDrawableFloat.bottom;
		if(mDrawableFloat.width() > getWidth()){
			newLeft = 0;
			newRight = getWidth();
			isNewRect = true;
		}
		if(mDrawableFloat.height() > getHeight()){
			newTop = 0;
			newBottom = getHeight();
			isNewRect = true;
		}
		if(isNewRect){
			mDrawableFloat.set(newLeft, newTop, newRight, newBottom);
		}
		
		if (isNewRect || isChange) {
			invalidate();
		}
	}

	// ����ͼƬ�Ĳü�����ν�Ĳü����Ǹ���Drawable���µ������ڻ����ϴ���һ���µ�ͼƬ
	public Bitmap getCropImage() {
		Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.RGB_565);
		Canvas canvas = new Canvas(tmpBitmap);
		mDrawable.draw(canvas);

		Matrix matrix = new Matrix();
		float scale = (float) (mDrawableSrc.width())
				/ (float) (mDrawableDst.width());
		matrix.postScale(scale, scale);

		Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left,
				mDrawableFloat.top, mDrawableFloat.width(),
				mDrawableFloat.height(), matrix, true);
		tmpBitmap.recycle();
		tmpBitmap = null;

		return ret;
	}

	public int dipTopx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
