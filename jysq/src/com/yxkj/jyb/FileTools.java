package com.yxkj.jyb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileTools {
	public static Context sContext = null;
	static public void init(Context _c) {
		sContext = _c;
		TabsMgr.init();
	}
	static public class BaseReader {
		public static String FLAG_ENCODING = "UTF-8";
	    public static String FLAG_COMMENT = "#";
	    public static String FLAG_FIELD = "$";
	    public static String SPLIT_LINE = "\r\n";
	    public static String SPLIT_CHAR = "\t";
	    public static String SPLIT_TOKEN = ",";

	    private List<String[]> m_listRecord = new ArrayList<String[]>();
	    private Map<String, Integer> m_dictField = new HashMap<String, Integer>();
	    public String mFileName = "";
	
	    public void Reader(InputStream fis){
	    	if(fis == null)
	    		return;
            try {
                 //将指定输入流包装成BufferReader
                 BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				 String line = null;
                 while((line = br.readLine()) != null){
				    line = line.trim(); 
				    if (line.startsWith(FLAG_COMMENT) || line.isEmpty())
				        continue;
				    else if (line.startsWith(FLAG_FIELD))
				    {
				        line = line.substring(1);
				        String[] itemList = line.split(SPLIT_CHAR, 0);
				        for (int i = 0; i < itemList.length; ++i){
				            String item = itemList[i];
				            if (item.isEmpty())
				                continue;

				            if (m_dictField.containsKey(item)){
				                Log.i("LocalReader",String.format("[%s]出现了相同的Key值:%s", mFileName, item));
				                continue;
				            }
				            m_dictField.put(item, i);
				        }
				    }
				    else
				    {
				    	String[] itemList = line.split(SPLIT_CHAR, 0);
				        m_listRecord.add(itemList);
				    }
				}
                 br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	    }
	    public int recordCount() {  return m_listRecord.size();  }

	    public String getString(int Idx, String _field)
	    {
	    	if(Idx >= 0 && Idx < m_listRecord.size())
	    		return (m_listRecord.get(Idx))[m_dictField.get(_field)];
	    	return "";
	    }

	    public boolean getBoolean(int itemIdx, String fieldName)
	    {
	        String value = getString(itemIdx, fieldName);
	        if (value.equals("T")  || value.equals("t") || value.equals("Y") || value.equals("y") )
	            return true;
	        else if (value.equals("F") || value.equals("f") || value.equals("N") || value.equals("n") || value.isEmpty())
	            return false;
	       
	        return false;
	    }

	    public float getFloat(int itemIdx, String fieldName)
	    {
	        String value = getString(itemIdx, fieldName);
	        if (value.isEmpty())
	            return 0;
	        return Float.parseFloat(value);
	    }

	    public int getInt(int itemIdx, String fieldName)
	    {
	        String value = getString(itemIdx, fieldName);
	        return  Integer.parseInt(value);
	    }

	    public long getLong(int itemIdx, String fieldName)
	    {
	        String value = getString(itemIdx, fieldName);
	        if (value.isEmpty())
	            return 0;
	        return Long.parseLong(value);
	    }
	}
	//从resource的raw中读取文件数据
	static public class RawReader extends BaseReader{
		public RawReader(Integer resid)
	    {
			mFileName = "R.raw." + resid.toString();
	    	if(sContext == null)
	    		return;
	    	try{ 
	    		InputStream fis = sContext.getResources().openRawResource(resid);
	    		Reader(fis);
	            fis.close();   
            } catch(Exception e){ 
	            e.printStackTrace(); 
            } 
    	}
	}
	//从resource的asset中读取文件数据
	static public class AssetReader extends BaseReader{
		public AssetReader(String fileName)
	    {
			mFileName = fileName;
	    	if(sContext == null)
	    		return;
	    	try{ 
	    		InputStream fis = sContext.getResources().getAssets().open(fileName);
	    		Reader(fis);
	            fis.close();   
            } catch(Exception e){ 
	            e.printStackTrace(); 
            } 
    	}
	}
	//读写/data/data/<应用程序名>目录上的文件    读写SD卡中的文件。也就是/mnt/sdcard/目录下面的文件
	static public class LocalReader extends BaseReader{
		//读
		public LocalReader(String filename)
	    {
			if(sContext == null)
	    		return;
			mFileName = filename;
			
			FileInputStream fis = null;
			try {
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
					File file=new File(GlobalUtility.Config.baseSDPath + filename);
		        	fis = new FileInputStream(file);
		        } 
		        else{
		    		fis = sContext.openFileInput(filename); 
	            }
	        }
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Reader(fis);
			
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		//写
		static public void write(String fileName,String writestr)
		{ 
			 //判断SDCard是否存在  
	        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	        	saveToSDCard(fileName, writestr);  
	        }else{  
	        	saveToApp(fileName, writestr);
	        }  
		} 
		static public void saveToApp(String fileName,String writestr)
		{ 
			try{ 
		        FileOutputStream fout = sContext.openFileOutput(fileName, Activity.MODE_PRIVATE); 
		        byte [] bytes = writestr.getBytes(); 
		        fout.write(bytes); 
		        fout.close(); 
			} catch(Exception e){ 
				e.printStackTrace(); 
			} 
		} 
		static public void saveToSDCard(String filename,String content) {  
			try{
				//Environment.getExternalStorageDirectory()表示找到sdcarf目录  
				 File file =new File(GlobalUtility.Config.baseSDPath, filename);  
				 FileOutputStream outStream =new FileOutputStream(file);  
				 outStream.write(content.getBytes());  
				 outStream.close();  
			} catch(Exception e){ 
				e.printStackTrace(); 
			} 
		}  
		static public boolean exists(String filename ) {
			 //判断SDCard是否存在  
	        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	        	File file =new File(GlobalUtility.Config.baseSDPath , filename);  
        		return file.exists();
	        } 
	        else{
	        	File file=new File(GlobalUtility.Config.baseAppPath , filename);
        		return file.exists();
	        }
		}
	}
}
