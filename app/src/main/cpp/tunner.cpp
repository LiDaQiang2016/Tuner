#include <jni.h>


#ifndef _Included_ldq_musicguitartunerdome_recording_AudioRecorder
#define _Included_ldq_musicguitartunerdome_recording_AudioRecorder
#ifdef __cplusplus
extern "C" {
#endif
#undef ldq_musicguitartunerdome_recording_AudioRecorder_SAMPLES
#define ldq_musicguitartunerdome_recording_AudioRecorder_SAMPLES 1024L
/*
 * Class:     ldq_musicguitartunerdome_recording_AudioRecorder
 * Method:    get_pitch_from_short
 * Signature: ([SI)D
 */

JNIEXPORT jdouble JNICALL Java_ldq_musicguitartunerdome_recording_AudioRecorder_get_1pitch_1from_1short
        (JNIEnv *, jclass, jshortArray, jint);

#ifdef __cplusplus
}
#endif
#endif


extern "C"
JNIEXPORT jdouble JNICALL
Java_com_park_tunner_recording_AudioRecorder_get_1pitch_1from_1short(JNIEnv *env, jclass clazz,
                                                                     jshortArray data,
                                                                     jint sample_rate) {
    Java_ldq_musicguitartunerdome_recording_AudioRecorder_get_1pitch_1from_1short(env, clazz, data, sample_rate);
}