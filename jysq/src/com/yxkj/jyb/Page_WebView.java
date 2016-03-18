package com.yxkj.jyb;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Page_WebView extends Activity{
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.page_webview);

        WebView view = (WebView)findViewById(R.id.webView1);
        
        view.setWebViewClient(new WebViewClient()
        {
        	public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                    view.loadUrl(url);
                    return true;
            }
        });
        
        view.loadUrl("http://mp.weixin.qq.com/s?__biz=MjM5MzIxNTQ2MA==&mid=215594418&idx=1&sn=8107d8abbb0b51e031cdcc59fcda4e30&scene=0&key=dffc561732c22651a6846992ca87fd5abdec37129d094bf70b5e261ea650026ffed1f43c857063a21a541b41b4d92318&ascene=1&uin=MTE3NDAyODQxNg%3D%3D&devicetype=Windows+7&version=6102002a&pass_ticket=YrFdEiWr%2FmsAjuqfVK7N6YyKufn0ErU5R6tAdhdg%2B0rxRVbsCpit8NZD9rUwaihZ");
    }
}