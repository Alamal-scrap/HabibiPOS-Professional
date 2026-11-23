package com.habibi.posprofessional;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {
  private WebView web;
  private byte[] pendingSave;
  private String pendingName;
  private ValueCallback<Uri[]> uploadCallback;
  private static final int SAVE_FILE=501, OPEN_BACKUP=502, CHOOSE_IMAGE=503;
  @Override public void onCreate(Bundle state){super.onCreate(state);
    web=new WebView(this); setContentView(web);
    WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true);
    s.setDefaultTextEncodingName("UTF-8");
    web.setWebViewClient(new WebViewClient());
    web.setWebChromeClient(new WebChromeClient(){
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params){
        uploadCallback=callback; try { Intent i=params.createIntent();startActivityForResult(i,CHOOSE_IMAGE);return true; }
        catch(Exception e){uploadCallback=null;return false;}
      }
    });
    web.addJavascriptInterface(new Bridge(),"AndroidFiles");
    web.loadUrl("file:///android_asset/index.html");
  }
  class Bridge {
    @JavascriptInterface public void save(String filename,String mime,String base64){
      try {pendingSave=Base64.decode(base64,Base64.DEFAULT);pendingName=filename;
        runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType(mime);i.putExtra(Intent.EXTRA_TITLE,filename);startActivityForResult(i,SAVE_FILE);});
      }catch(Exception e){notifyWeb("File export failed");}
    }
    @JavascriptInterface public void importBackup(){runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");startActivityForResult(i,OPEN_BACKUP);});}
  }
  private void notifyWeb(String msg){runOnUiThread(()->web.evaluateJavascript("window.nativeMessage("+org.json.JSONObject.quote(msg)+")",null));}
  @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);
    if(request==CHOOSE_IMAGE){if(uploadCallback!=null){uploadCallback.onReceiveValue(result==RESULT_OK?WebChromeClient.FileChooserParams.parseResult(result,data):null);uploadCallback=null;}return;}
    if(result!=RESULT_OK||data==null||data.getData()==null){if(request==SAVE_FILE){pendingSave=null;notifyWeb("Save cancelled");}return;}
    Uri uri=data.getData();
    if(request==SAVE_FILE){ try(OutputStream os=getContentResolver().openOutputStream(uri)){if(os==null)throw new Exception("No output stream");os.write(pendingSave);notifyWeb("File saved successfully");}catch(Exception e){notifyWeb("File save failed: "+e.getMessage());}finally{pendingSave=null;}}
    if(request==OPEN_BACKUP){try(InputStream is=getContentResolver().openInputStream(uri);ByteArrayOutputStream b=new ByteArrayOutputStream()){
      byte[] buf=new byte[8192];int n;while((n=is.read(buf))!=-1){b.write(buf,0,n);if(b.size()>20_000_000)throw new Exception("Backup too large");}
      String b64=Base64.encodeToString(b.toByteArray(),Base64.NO_WRAP);
      web.evaluateJavascript("window.receiveBackupBase64('"+b64+"')",null);
    }catch(Exception e){notifyWeb("Import failed: "+e.getMessage());}}
  }
}
