package com.yxkj.jyb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentPage3 extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_3, null);
		
		TextView tv = (TextView)view.findViewById(R.id.searchsend_title);
		 
		
	     // ���һ��html�ı�־  
        String html = "<font color='red'>���ı�����</font><br><br><br>";  
        html += "<font color='#0000ff'><big><i>���˽ڿ��֣�����</i></big></font><p>";
        html += "<font color='#000000'><i>����������һ����ɹ���</i></font><p>";
        html += "<big><a href='http://www.baidu.com'>�ٶ�</a></big><br>";
        html += "<h3>This is a header</h3><p>This is a paragraph.</p>";
        html += "<p>����һ</p><p>�����</p>";
        html +="<strong>����<strong>";
        html +="<s>����</s>";
        html +="<em>����</em>";
        html +="<i>����</i>";
        html +="<h1>�������ǩ</h1>";
        html +="<h2>�����ǩ</h2>";
        html +="<h3>�����ǩ</h3>";
        html +="<h3>�����ǩ</h3>";
        html +="<font color='#0000FF'><h3>�����ǩ</h3></font>";
        html +="<h3>�����ǩ</h3>";
        html +="<h3>�����ǩ</h3>";
        html +="<h3>�����ǩ</h3>";
        html +="<h3>�����ǩ</h3>";
        html +="<h3>�����ǩ</h3>";
        html +="<h3>�����ǩ</h3>";
        
		html +="<h1>���ı���</h1>";
		html +="<h6>���ı���</h6>";
		html +="<p><b>�������ı�</b> </p>";
		html +="<p><i>б�����ı�</i> </p>";
		html +="<p><u>�¼�һ�����ı�</u> </p>";
		html +="<p><tt>���ֻ������ı�</tt></p>";
		html +="<p><cite>���÷�ʽ���ı�</cite></p>";
		html +="<p><em>ǿ�����ı�</em></p>";
		html +="<p><strong>���ص��ı�</strong></p>";
		html +="<p><font size='+1' color='red'>sizeȡֵ'+1'��colorȡֵ'red'ʱ���ı�</font></p>";
        
        
        CharSequence charSequence = Html.fromHtml(html);  
        tv.setText(charSequence);  
        tv.setMovementMethod(LinkMovementMethod.getInstance());// �����ʱ���������
        

		return view;
	}	
}