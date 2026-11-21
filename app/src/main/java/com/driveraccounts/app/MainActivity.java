package com.driveraccounts.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.JavascriptInterface;
import android.view.View;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.graphics.Paint;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
  private WebView web;
  private ValueCallback<Uri[]> filePathCallback;
  private static final int FILE_CHOOSER_REQUEST = 731;
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.rgb(16,42,67));
    getWindow().getDecorView().setSystemUiVisibility(0);
    web = new WebView(this);
    web.setBackgroundColor(Color.WHITE);
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(true);
    web.addJavascriptInterface(new ShareBridge(), "AndroidBridge");
    web.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        new android.app.AlertDialog.Builder(MainActivity.this)
          .setMessage(message)
          .setPositiveButton("Delete", (dialog, which) -> result.confirm())
          .setNegativeButton("Cancel", (dialog, which) -> result.cancel())
          .setOnCancelListener(dialog -> result.cancel())
          .show();
        return true;
      }
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
        if (filePathCallback != null) filePathCallback.onReceiveValue(null);
        filePathCallback = callback;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        try { startActivityForResult(Intent.createChooser(intent, "Choose profile picture"), FILE_CHOOSER_REQUEST); }
        catch (ActivityNotFoundException e) { filePathCallback = null; return false; }
        return true;
      }
    });
    web.setOverScrollMode(View.OVER_SCROLL_NEVER);
    setContentView(web);
    web.loadUrl("file:///android_asset/index.html");
  }
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FILE_CHOOSER_REQUEST && filePathCallback != null) {
      Uri[] results = null;
      if (resultCode == RESULT_OK && data != null && data.getData() != null) results = new Uri[]{data.getData()};
      filePathCallback.onReceiveValue(results);
      filePathCallback = null;
    }
  }
  private class ShareBridge {
    @JavascriptInterface public void shareText(String text) {
      runOnUiThread(() -> {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain"); i.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(i, "Share Driver Accounts"));
      });
    }
    @JavascriptInterface public void sharePdf(String title, String content) {
      try {
        File dir = new File(getCacheDir(), "shared"); if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "DriverAccounts_Report.pdf");
        PdfDocument pdf = new PdfDocument(); Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(25,55,45)); paint.setTextSize(16f); paint.setFakeBoldText(true);
        int pageNo=1, y=48; PdfDocument.Page page=pdf.startPage(new PdfDocument.PageInfo.Builder(595,842,pageNo).create());
        page.getCanvas().drawText(title, 40, y, paint); paint.setFakeBoldText(false); paint.setTextSize(11f); y+=32;
        for(String line: content.split("\\n")) {
          if(y>800){pdf.finishPage(page);pageNo++;page=pdf.startPage(new PdfDocument.PageInfo.Builder(595,842,pageNo).create());y=48;}
          String remaining=line; while(remaining.length()>85){page.getCanvas().drawText(remaining.substring(0,85),40,y,paint);remaining=remaining.substring(85);y+=18;}
          page.getCanvas().drawText(remaining,40,y,paint);y+=20;
        }
        pdf.finishPage(page); try(FileOutputStream out=new FileOutputStream(file)){pdf.writeTo(out);} pdf.close();
        Uri uri=FileProvider.getUriForFile(MainActivity.this,getPackageName()+".provider",file);
        Intent i=new Intent(Intent.ACTION_SEND);i.setType("application/pdf");i.putExtra(Intent.EXTRA_STREAM,uri);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(i,"Share PDF report"));
      } catch(Exception e) { runOnUiThread(() -> android.widget.Toast.makeText(MainActivity.this,"PDF share failed: "+e.getMessage(),android.widget.Toast.LENGTH_LONG).show()); }
    }
  }
}
