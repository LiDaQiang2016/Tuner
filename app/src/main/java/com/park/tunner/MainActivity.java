package com.park.tunner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.park.tunner.recording.AudioRecorder;
import com.park.tunner.uihelper.UIHelper;

import java.util.Random;


public class MainActivity extends Activity implements View.OnClickListener, UIHelper {
    private DashboardView mDashboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDashboardView = (DashboardView) findViewById(R.id.tunner_dashboard_view);
        mDashboardView.setOnClickListener(this);
//        AudioRecorder.enableNdk();


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                String text = radioButton.getText().toString();

                if ("E4".equals(text)) {
                    mDashboardView.setStandardSize(329);
                    mDashboardView.setmHeaderText(text);
                } else if ("B3".equals(text)) {
                    mDashboardView.setStandardSize(246);
                    mDashboardView.setmHeaderText(text);
                } else if ("G3".equals(text)) {
                    mDashboardView.setStandardSize(196);
                    mDashboardView.setmHeaderText(text);
                } else if ("D3".equals(text)) {
                    mDashboardView.setStandardSize(147);
                    mDashboardView.setmHeaderText(text);
                } else if ("A2".equals(text)) {
                    mDashboardView.setStandardSize(110);
                    mDashboardView.setmHeaderText(text);
                } else if ("E2".equals(text)) {
                    mDashboardView.setStandardSize(82);
                    mDashboardView.setmHeaderText(text);
                } else if ("E1".equals(text)) {
                    mDashboardView.setStandardSize(40);
                    mDashboardView.setmHeaderText(text);
                } else if ("C7".equals(text)) {
                    mDashboardView.setStandardSize(2093);
                    mDashboardView.setmHeaderText(text);
                }


            }
        });
    }


    @Override
    public void onClick(View v) {
        mDashboardView.setRealTimeValue(new Random().nextInt(100));
    }


    private Thread audioThread;
    private final int REQUEST_AUDIO_RECORD = 0;
    private boolean recordPermission = false;

    @Override
    public void display(final String note, final double err, final double pitcha) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!note.isEmpty()) {
                    if ((99.5 < err) && (err < 100.5)) {
                        mDashboardView.setRealTimeValue((int) pitcha);
                    } else {
                        mDashboardView.setRealTimeValue((int) pitcha);
                    }
                } else {
                    mDashboardView.setRealTimeValue((int) pitcha);
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
        AudioRecorder.init(this,this);
        launchPitcha();
    }

    private void handlePermissions() {
        switch (checkSelfPermission(Manifest.permission.RECORD_AUDIO)) {
            case PackageManager.PERMISSION_DENIED:
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
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
                    Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                    AudioRecorder.run();
                }
            });
            audioThread.start();
        } else {
            System.exit(-1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
