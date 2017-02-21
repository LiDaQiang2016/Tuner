package ldq.musicguitartunerdome.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import ldq.musicguitartunerdome.dsp.MPM;
import ldq.musicguitartunerdome.music.NotePitchMap;
import ldq.musicguitartunerdome.uihelper.UIHelper;


/**
 * Created by sevag on 11/25/14.
 */
public class AudioRecorder {

    static {
        System.loadLibrary("mpm");
    }

    public static native double get_pitch_from_short(short[] data, int sampleRate);

    private static AudioRecord recorder;
    private static short[] data;
    private static final int SAMPLES = 1024;
    private static boolean shouldStop = false;
    private static UIHelper uiHelper;
    private static MPM mpm;
    private static int sampleRate;
    private static int useNdk = 0;

    public AudioRecorder() {
    }

    private static int getMaxValidSampleRate() {
        int maxRate = 0;
//        for (int rate : new int[] {8000, 11025, 16000, 22050, 44100, 48000}) {


        //rate的大小跟最终频率的获取范围有关,rate越大最小值和最大值频率都变大
        for (int rate : new int[] {8000, 11025, 16000, 22050}) {
            int bufferSize = AudioRecord.getMinBufferSize(rate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                maxRate = rate;
            }
        }
        return maxRate;
    }

    public static void init(UIHelper paramUiHelper) {
        sampleRate = getMaxValidSampleRate();
        mpm = new MPM(sampleRate, SAMPLES, 0.93);

        int N = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        Log.e("ldq","sampleRate = "+sampleRate +"    N= "+N+"     smalHZ="+(sampleRate/N));
        data = new short[SAMPLES];
        uiHelper = paramUiHelper;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                N * 10);
//                N );
        shouldStop = false;
        recorder.startRecording();
    }

    public static void run() {
        while ((!shouldStop)) {
            try {
                recorder.read(data, 0, data.length);
                double pitch = 0.0;
                switch (useNdk) {
                    case 0:
                        pitch = mpm.getPitchFromShort(data);
                        break;
                    case 1:
                        pitch = get_pitch_from_short(data, sampleRate);
                        break;
                }

//                Log.e("ldq", "pitch====  "+pitch);
                NotePitchMap.displayNoteOf(pitch, uiHelper);
            } catch (Throwable x) {
                x.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void deinit() {
        shouldStop = true;
        recorder.stop();
        recorder.release();
    }

    public static void enableNdk() {
       useNdk = 1;
    }

    public static void disableNdk() {
       useNdk = 0;
    }
}