package tw.com.changchinghsiang.sunday_classno_09_mediarecorderaudio;

import static android.Manifest.permission.*;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static final int REQUEST_RECORD_AUDIO = 1;

    private TextView tvShow;
    private MediaRecorder recorder;
    private String storePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findviewer();
        //建立錄音物件
        recorder = new MediaRecorder();
    }

    private void findviewer() {
        tvShow = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Android 6 以上檢查權限
        if (Build.VERSION.SDK_INT >= 23){
            //外存
            int permission_SDCard = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            if (permission_SDCard != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                                                    new String[] {WRITE_EXTERNAL_STORAGE,
                                                                    READ_EXTERNAL_STORAGE},
                                                    REQUEST_EXTERNAL_STORAGE);
            }
            else {
                //權限相同
            }

            //麥克風
            int permission_MIC = ActivityCompat.checkSelfPermission(this, RECORD_AUDIO);
            if (permission_MIC != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                                                    new String[] {RECORD_AUDIO},
                                                    REQUEST_RECORD_AUDIO);
            }
            else {
                //權限相同
            }
        }
    }

    //權限控管處理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限
                    Toast.makeText(this, "\"讀寫外部儲存媒體權限\"已取得！", Toast.LENGTH_SHORT).show();
                } else {
                    //使用者拒絕權限
                    Toast.makeText(this, "您必需同意本APP取得\"讀寫外部儲存媒體權限\"才能繼續使用。", Toast.LENGTH_SHORT).show();
                }
                return;

            case REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限
                    Toast.makeText(this, "\"麥克風控制權限\"已取得！", Toast.LENGTH_SHORT).show();
                }
                else {
                    //使用者拒絕權限
                    Toast.makeText(this, "您必需同意本APP取得\"麥克風控制權限\"才能繼續使用。", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    //開始錄音
    public void StartOnClick(View view) {
        //取得外儲路徑
        String esPath = Environment.getExternalStorageDirectory().getPath();
        //指定錄音檔檔名
        storePath = esPath + "/MediaRecorder.mp4";

        /* === 設定錄音屬性 === */
        //音訊來源
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //設定輸出編碼格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //設定音效品質
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //設定輸出檔路徑
        recorder.setOutputFile(storePath);
        //準備錄音機開始捕獲和編碼數據
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //開始錄音
        recorder.start();

        tvShow.setText("Audio Record Started.");
    }

    //結束錄音
    public void StopOnClick(View view) {
        if (recorder != null){
            recorder.stop();
            recorder.reset();
        }
        tvShow.setText("Audio Record Stopped.\nStore Path:" + storePath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }
}
