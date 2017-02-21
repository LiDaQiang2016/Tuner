package ldq.musicguitartunerdome;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Random;

import ldq.musicguitartunerdome.recording.AudioRecorder;
import ldq.musicguitartunerdome.uihelper.UIHelper;

public class MainActivity extends Activity implements View.OnClickListener , UIHelper, ActivityCompat.OnRequestPermissionsResultCallback{
    private DashboardView1 mDashboardView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDashboardView1 = (DashboardView1) findViewById(R.id.dashboard_view_1);
        mDashboardView1.setOnClickListener(this);
//        AudioRecorder.enableNdk();



        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                String text=radioButton.getText().toString();

                if("E4".equals(text)){
                    mDashboardView1.setStandardSize(329);
                    mDashboardView1.setmHeaderText(text);
                }else
                if("B3".equals(text)){
                    mDashboardView1.setStandardSize(246);
                    mDashboardView1.setmHeaderText(text);
                }else
                if("G3".equals(text)){
                    mDashboardView1.setStandardSize(196);
                    mDashboardView1.setmHeaderText(text);
                }else
                if("D3".equals(text)){
                    mDashboardView1.setStandardSize(147);
                    mDashboardView1.setmHeaderText(text);
                }else
                if("A2".equals(text)){
                    mDashboardView1.setStandardSize(110);
                    mDashboardView1.setmHeaderText(text);
                }else
                if("E2".equals(text)){
                    mDashboardView1.setStandardSize(82);
                    mDashboardView1.setmHeaderText(text);
                }
                else
                if("E1".equals(text)){
                    mDashboardView1.setStandardSize(40);
                    mDashboardView1.setmHeaderText(text);
                }
                else
                if("C7".equals(text)){
                    mDashboardView1.setStandardSize(2093);
                    mDashboardView1.setmHeaderText(text);
                }




            }
        });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dashboard_view_1:
                mDashboardView1.setRealTimeValue(new Random().nextInt(100));
                break;
        }
    }







    private Thread audioThread;
    private final int REQUEST_AUDIO_RECORD = 0;
    private boolean recordPermission = false;

    @Override
    public void display(final String note, final double err,final double pitcha) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!note.isEmpty()) {
                    if ((99.5 < err) && (err < 100.5)) {
                        mDashboardView1.setRealTimeValue((int)pitcha);
                    } else {
                        mDashboardView1.setRealTimeValue((int)pitcha);
                    }
                } else {
                    mDashboardView1.setRealTimeValue((int)pitcha);
                }



//                if (!note.isEmpty()) {
//                    if ((99.5 < err) && (err < 100.5)) {
//                        noteTextView.setText(note+"   "+err);
//                        noteTextView.setTextColor(Color.GREEN);
//                    } else {
//                        noteTextView.setText(note+"   "+err);
//                        noteTextView.setTextColor(Color.RED);
//                    }
//                } else {
//                    noteTextView.setText(note+"   "+err);
//                }
            }
        });
    }

    @Override
    protected void onPause() {
        endHook();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startHook();
        super.onResume();
    }

    private void endHook() {
        AudioRecorder.deinit();
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private void startHook() {
        handlePermissions();
        AudioRecorder.init(this);
        launchPitcha();
    }

    private void handlePermissions() {
        switch (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            case PackageManager.PERMISSION_DENIED:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_RECORD);
                break;
            case PackageManager.PERMISSION_GRANTED:
                recordPermission = true;
                break;
        }
    }

    private void launchPitcha() {
        if (recordPermission) {
            audioThread = new Thread(new Runnable() {
                public void run() {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                    AudioRecorder.run();
                }
            });
            audioThread.start();
        } else {
            System.exit(-1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_RECORD: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordPermission = true;
                }
            }
        }
    }



}
