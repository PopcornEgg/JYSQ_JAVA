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
		 
		
	     // 添加一段html的标志  
        String html = "<font color='red'>富文本测试</font><br><br><br>";  
        html += "<font color='#0000ff'><big><i>情人节快乐！！！</i></big></font><p>";
        html += "<font color='#000000'><i>记忆神器，一定会成功！</i></font><p>";
        html += "<big><a href='http://www.baidu.com'>百度</a></big><br>";
        html += "<h3>This is a header</h3><p>This is a paragraph.</p>";
        html += "<p>段落一</p><p>段落二</p>";
        html +="<strong>内容<strong>";
        html +="<s>内容</s>";
        html +="<em>内容</em>";
        html +="<i>内容</i>";
        html +="<h1>最大标题标签</h1>";
        html +="<h2>标题标签</h2>";
        html +="<h3>标题标签</h3>";
        html +="<h3>标题标签</h3>";
        html +="<font color='#0000FF'><h3>标题标签</h3></font>";
        html +="<h3>标题标签</h3>";
        html +="<h3>标题标签</h3>";
        html +="<h3>标题标签</h3>";
        html +="<h3>标题标签</h3>";
        html +="<h3>标题标签</h3>";
        html +="<h3>标题标签</h3>";
        
		html +="<h1>最大的标题</h1>";
		html +="<h6>最大的标题</h6>";
		html +="<p><b>黑体字文本</b> </p>";
		html +="<p><i>斜体字文本</i> </p>";
		html +="<p><u>下加一划线文本</u> </p>";
		html +="<p><tt>打字机风格的文本</tt></p>";
		html +="<p><cite>引用方式的文本</cite></p>";
		html +="<p><em>强调的文本</em></p>";
		html +="<p><strong>加重的文本</strong></p>";
		html +="<p><font size='+1' color='red'>size取值'+1'、color取值'red'时的文本</font></p>";
        
        
        CharSequence charSequence = Html.fromHtml(html);  
        tv.setText(charSequence);  
        tv.setMovementMethod(LinkMovementMethod.getInstance());// 点击的时候产生超链
        

		return view;
	}	
}