package com.yxkj.jyb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tencent.download.Downloader.DownloadListener;
import com.tencent.download.Downloader.Priority;
import com.tencent.download.core.DownloadResult;
import com.tencent.upload.Const.FileType;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.ITask.TaskState;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.FileStatTask;
import com.tencent.upload.task.impl.PhotoUploadTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageDataMgr {

	static public ImageCacheMgr sImageCacheMgr = new ImageCacheMgr();
	static public DownLoadMgr sDownLoadMgr = new DownLoadMgr();
	static public UpLoadMgr sUpLoadMgr = new UpLoadMgr();
	
	// 资源管理
	// ////////////////////////////////////////////////////////////////////////////////
	public static class ImageItem {
		public String url = "";
		public Bitmap bitmap = null;
		public Object tag = null;// 扩展参数，一般不用
		public long usedtime = 0;

		public ImageItem() {
		}

		public ImageItem(String _u, Bitmap _bm, Object _t) {
			this.url = _u;
			this.bitmap = _bm;
			this.tag = _t;
		}
	}

	public static class ImageCacheMgr {
		static Integer MAX_CACHE_NUMBER = 20;
		Map<String, ImageItem> dicImageItems = new HashMap<String, ImageItem>();

		public ImageItem add(String _u, Bitmap _bm, Object _t) {
			isFullDel();
			_u = _u.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			ImageItem item = null;
			if (dicImageItems.containsKey(_u)) {
				item = dicImageItems.get(_u);
				item.bitmap = _bm;
				item.tag = _t;
			} else {
				item = new ImageItem(_u, _bm, _t);
				dicImageItems.put(_u, item);
			}
			return item;
		}

		public ImageItem add(String _u, Bitmap _bm) {
			isFullDel();
			_u = _u.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			ImageItem item = null;
			if (dicImageItems.containsKey(_u)) {
				item = dicImageItems.get(_u);
				item.bitmap = _bm;
			} else {
				item = new ImageItem(_u, _bm, null);
				dicImageItems.put(_u, item);
			}
			return item;
		}

		public void isFullDel() {
			if (dicImageItems.size() < MAX_CACHE_NUMBER)
				return;
			String tag = "";
			long minusedtime = Long.MAX_VALUE;
			for (Entry<String, ImageItem> item : dicImageItems.entrySet()) {
				ImageItem imgitem = item.getValue();
				if (minusedtime > imgitem.usedtime) {
					minusedtime = imgitem.usedtime;
					tag = item.getKey();
				}
			}
			if (!tag.isEmpty()) {
				// 内存释放？
				//dicImageItems.remove(tag);
			}
		}

		public void add(String url, ImageItem item) {
			url = url.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			if (item == null)
				return;

			dicImageItems.put(url, item);
		}

		public ImageItem get(String url) {
			String urlops = url.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			if (dicImageItems.containsKey(urlops)) {
				ImageItem item = dicImageItems.get(urlops);
				item.usedtime = System.currentTimeMillis();
				return item;
			} else {
				// 尝试从本地读取
				if (MainTabActivity.sDownloader.hasCache(url)) {
					File file = MainTabActivity.sDownloader.getCacheFile(url);
					if (file != null && file.exists()) {
						try {
							FileInputStream fis = new FileInputStream(file);
							byte[] buf = new byte[(int) file.length()];
							fis.read(buf);
							fis.close();
							Bitmap bm = BitmapFactory.decodeByteArray(buf, 0,buf.length);
							ImageItem imgitem = ImageDataMgr.sImageCacheMgr.add(url, bm);
							imgitem.usedtime = System.currentTimeMillis();
							return imgitem;
						} catch (IOException e) {
							e.printStackTrace();
							Log.i("jiyibang","下载成功;加载到cache失败 : " + e.toString());
						}
					}
				}
			}
			return null;
		}
	}

	// 下载管理
	// ////////////////////////////////////////////////////////////////////////////////
	public static class DLItem {
		public String url = "";
		public int what = -1;
		public Object tag = null;
		public Handler handler = null;

		public DLItem() {
		}

		public DLItem(String _u, int _w, Object _t, Handler _h) {
			this.url = _u;
			this.what = _w;
			this.tag = _t;
			this.handler = _h;
		}
	}

	static public class DownLoadMgr {
		Map<String, DLItem> dicDownLoadItems = new HashMap<String, DLItem>();

		private void addItem(String url, DLItem item) {
			url = url.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			if (item == null)
				return;

			dicDownLoadItems.put(url, item);
		}

		private DLItem getItem(String url) {
			url = url.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			if (dicDownLoadItems.containsKey(url))
				return dicDownLoadItems.get(url);
			else
				return null;
		}

		public void remove(String url) {
			url = url.replace(GlobalUtility.Config.ImageMainUrl, "");// url太长，不利于map查询，缩短
			dicDownLoadItems.remove(url);
		}

		public void addLoad(String url, Object tag, int what, Handler handler) {
			DownloadListener listener = new DownloadListener() {
				@Override
				public void onDownloadSucceed(String url, DownloadResult result) {

					Log.i("jiyibang", "下载成功: " + result.getPath());

					// 下载成功读取到ImageCacheMgr
					if (MainTabActivity.sDownloader.hasCache(url)) {
						File file = MainTabActivity.sDownloader
								.getCacheFile(url);
						if (file != null && file.exists()) {
							try {
								FileInputStream fis = new FileInputStream(file);
								byte[] buf = new byte[(int) file.length()];
								fis.read(buf);
								fis.close();
								Bitmap bm = BitmapFactory.decodeByteArray(buf,
										0, buf.length);
								ImageDataMgr.sImageCacheMgr.add(url, bm);
							} catch (IOException e) {
								e.printStackTrace();
								Log.i("jiyibang",
										"下载成功;加载到cache失败 : " + e.toString());
							}
						}
					}
					// 通知相关UI
					DLItem dlitem = getItem(url);
					if (dlitem != null) {
						Handler handler = dlitem.handler;
						Message msg = new Message();
						msg.obj = dlitem;
						msg.what = dlitem.what;
						if (handler != null && msg != null)
							handler.sendMessage(msg);
					}
				}

				@Override
				public void onDownloadProgress(String url, long totalSize,
						float progress) {
					long nProgress = (int) (progress * 100);
					Log.i("jiyibang", "下载进度: " + nProgress + "%");
				}

				@Override
				public void onDownloadFailed(String url, DownloadResult result) {
					Log.i("jiyibang", "下载失败: " + result.getErrorCode());
				}

				@Override
				public void onDownloadCanceled(String url) {
					Log.i("jiyibang", "下载任务被取消");
				}
			};

			addItem(url, new DLItem(url, what, tag, handler));
			MainTabActivity.sDownloader.download(url, listener);
		}
	}

	// 上传管理(同时只能进行一个上传任务)
	// ////////////////////////////////////////////////////////////////////////////////
	public static class ULItem {
		public String uuid = "";
		public int what = -1;
		public byte[] datas = null;
		public Handler handler = null;

		public ULItem() {
		}

		public ULItem( String _uu, byte[] _ds, int _w, Handler _h) {
			this.uuid = _uu;
			this.datas = _ds;
			this.what = _w;
			this.handler = _h;
		}
	}

	static public class UpLoadMgr {
		List< ULItem> listUpLoadItems = new ArrayList<ULItem>();
		ULItem curULItem = null;
		private void addItem( ULItem item) {
			if (item == null)
				return;

			listUpLoadItems.add(item);
		}

		private void setNextItem() {
			if(listUpLoadItems.size() > 0)
				curULItem = listUpLoadItems.remove(0);
			else
				curULItem = null;
		}

		public void addLoad(byte[] datas, int what, Handler handler) {
			
			String uuid = "jyb_fid_" + GlobalUtility.Func.nextUID();
			addItem(new ULItem(uuid, datas, what, handler));
			if(curULItem == null){//当前无上传任务
				upLoad();
			}
		}
		public void upLoad() {
			setNextItem();
			if(curULItem == null)
				return;
			PhotoUploadTask task = new PhotoUploadTask(curULItem.datas, new IUploadTaskListener() {
				@Override
				public void onUploadSucceed(final FileInfo result) {
					Log.i("jiyibang_UpLoadMgr_addLoad", "上传成功 ");
					
					// 相关通知
					if (curULItem != null) {
						Handler handler = curULItem.handler;
						Message msg = new Message();
						msg.obj = curULItem.uuid;
						msg.arg1 = 1;
						msg.what = curULItem.what;
						if (handler != null && msg != null)
							handler.sendMessage(msg);
						
						upLoad();//开始下一个任务
					}
				}

				@Override
				public void onUploadStateChange(TaskState state) {
				
				}

				@Override
				public void onUploadProgress(long totalSize,long sendSize) {
					Log.i("jiyibang_UpLoadMgr_addLoad", "上传成功 ");
					int p = (int) ((float) sendSize / (float) totalSize * 100.0f);
					
					// 相关通知
					if (curULItem != null) {
						Handler handler = curULItem.handler;
						Message msg = new Message();
						msg.obj = curULItem.uuid;
						msg.arg1 = 2;
						msg.arg2 = p;
						msg.what = curULItem.what;
						if (handler != null && msg != null)
							handler.sendMessage(msg);
					}
				}

				@Override
				public void onUploadFailed(final int errorCode,final String errorMsg) {
					Log.i("jiyibang_UpLoadMgr_addLoad", "上传结果:失败! ret:" + errorCode + " msg:"+ errorMsg);
				}
			});

			task.setBucket(GlobalUtility.Config.imagemyqcloudbucket);
			task.setFileId(curULItem.uuid); // 为图片自定义FileID(可选)
			MainTabActivity.sUploadManager.upload(task);
		}
		private void checkImage(){
	    	 FileStatTask filetask = new FileStatTask(curULItem.uuid, FileType.Photo, GlobalUtility.Config.imagemyqcloudbucket, 
			      new FileStatTask.IListener() {
			         @Override
			         public void onSuccess(final FileInfo result) {
			              String ret = "URL:" + result.url  + 
			            		  "MD5:" + result.extendInfo.get("file_md5") 
			            		  + "\nWidth : " + result.extendInfo.get("photo_width") 
			            		  + "\nHeight: " + result.extendInfo.get("photo_height");
			              
			              Log.i("jiyibang_UpLoadMgr_addLoad", ret);
			         } 
			         @Override
			         public void onFailure(final int ret, final String msg) {
			             Log.e("Demo", "查询结果:失败! ret:" + ret + " msg:" + msg);
				 }
			  });
	    	 MainTabActivity.sUploadManager.sendCommand(filetask);
	    }
	}
	
}