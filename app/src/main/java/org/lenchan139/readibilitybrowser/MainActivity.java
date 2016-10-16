package org.lenchan139.readibilitybrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.lenchan139.readibilitybrowser.Class.ClearableEditText;
import org.lenchan139.readibilitybrowser.Class.CommonStrings;
import org.lenchan139.readibilitybrowser.Class.Page;
import org.lenchan139.readibilitybrowser.Class.Tab;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
WebView webView;
    Button btnGo,btnBack,btnForward;
    ClearableEditText editText;
    String latestUrl = "https://ddg.gg/";
    SharedPreferences settings;
    Tab tab = new Tab(new Page("",latestUrl));
    CommonStrings commonStrings = new CommonStrings();
    final String TAG_HOME = "homePageUrl";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        webView = (WebView) findViewById(R.id.webView);
        btnGo = (Button) findViewById(R.id.btnGo);
        editText = (ClearableEditText) findViewById(R.id.editText);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnForward = (Button) findViewById(R.id.btnForward);
        settings = getSharedPreferences("settings",0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        if(settings.getString("Home",null) == null){
            settings.edit().putString(TAG_HOME,"https://ddg.gg");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(fab.INVISIBLE);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrlFromEditText();
                hideKeybord();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String get = tab.moveToPervious().getUrl();
                if(get == null){
                    Toast.makeText(MainActivity.this, "No more page!", Toast.LENGTH_SHORT).show();
                }else {
                    webView.loadUrl(get);
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String get = tab.moveToNext().getUrl();
                if(get == null){
                    Toast.makeText(MainActivity.this, "No more page!", Toast.LENGTH_SHORT).show();
                }else {
                    webView.loadUrl(get);
                }
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {


            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    loadUrlFromEditText();
                    hideKeybord();
                    return true;
                }
                return false;
            }
        });
        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                editText.setText(url);

            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // handle different requests for different type of files
                // this example handles downloads requests for .apk and .mp3 files
                // everything else the webview can handle normally

                    tab.addPage(new Page(url,"Page"));

                latestUrl = url;
                    view.loadUrl(url);

                return true;
            }
        });
hideKeybord();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        webView.loadUrl(latestUrl);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.menu_home){

        }else if(id == R.id.menu_share){

        }else if(id == R.id.menu_moblize){

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    public void hideKeybord(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void loadUrlFromEditText(){
        String temp = editText.getText().toString().trim();
        if(temp.indexOf("ddg.gg") >=0 || temp.indexOf("duckduckgo.com") >= 0){
            webView.loadUrl(temp);
        }else if (temp.indexOf("https://") == 0 || temp.indexOf("http://") == 0) {
            webView.loadUrl( temp);
        } else if(!temp.contains(".")){
            webView.loadUrl(  commonStrings.searchHeader() + temp);
        }
        else{
            webView.loadUrl( "http://" + temp);
        }
    }
}
