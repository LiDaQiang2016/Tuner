package com.park.tunner.music;


import com.park.tunner.uihelper.UIHelper;

/**
 * Created by sevag on 11/28/14.
 */
public class NotePitchMap {

    private static final double PITCH_LOW_LIMIT = 0.0f;
    private static final double PITCH_HIGH_LIMIT = 40000.0f;

    private static String[] noteNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};

    private static double[] oct0 = {16.35, 17.32, 18.35, 19.45, 20.60, 21.83, 23.12, 24.50, 25.96, 27.50, 29.14, 30.87};
    private static double[] oct1 = {32.70, 34.65, 36.71, 38.89, 41.20, 43.65, 46.25, 49, 51.91, 55, 58.27, 61.74};
    private static double[] oct2 = {65.41, 69.30, 73.42, 77.78, 82.41, 87.31, 92.50, 98, 103.8, 110, 116.5, 123.5};
    private static double[] oct3 = {130.8, 138.6, 146.8, 155.6, 164.8, 174.6, 185.0, 196, 207.7, 220, 233.1, 246.9};
    private static double[] oct4 = {261.6, 277.2, 293.7, 311.1, 329.6, 349.2, 370, 392, 415.3, 440, 466.2, 493.9};
    private static double[] oct5 = {523.3, 554.4, 587.3, 622.3, 659.3, 698.5, 740, 784, 830.6, 880, 932.3, 987.8};
    private static double[] oct6 = {1047, 1109, 1175, 1245, 1319, 1397, 1480, 1568, 1661, 1760, 1865, 1976};
    private static double[] oct7 = {2093, 2217, 2349, 2489, 2637, 2794, 2960, 3136, 3322, 3520, 3729, 3951};
    private static double[] oct8 = {4186, 4435, 4699, 4978, 5274, 5588, 5920, 6272, 6645, 7040, 7459, 7902};

    private static double[][] notes = {oct0, oct1, oct2, oct3, oct4, oct5, oct6, oct7, oct8};

    private static final double ALLOWABLE_ERROR = 1.2; //hz

    public static void displayNoteOf(double pitch, UIHelper uiHelper) {

        double percentCloseness = 0.0f;
        String outputNote = "";

        double[] comparisonOctave;
        double[] octave = null;
        int bestFitOctave = 0;

        if ((pitch < PITCH_LOW_LIMIT) || (pitch > PITCH_HIGH_LIMIT)) {
            uiHelper.display(outputNote, percentCloseness,pitch);
            return;
        } else {
            for (int i = 0; i < notes.length; i++) {
                comparisonOctave = notes[i];
                if ((pitch > (comparisonOctave[0] - ALLOWABLE_ERROR)) &&
                        (pitch < (comparisonOctave[notes[i].length - 1] + ALLOWABLE_ERROR))) {
                    octave = comparisonOctave;
                    bestFitOctave = i;
                    break;
                }
            }
        }

        if (octave == null) {
            uiHelper.display(outputNote, percentCloseness,pitch);
            return;
        }

        double bestDifference = 1000.0;
        int bestFitNoteIndex = -1;
        double bestDifference1 = 1000.0;

        for (int i = 0; i < octave.length; i++) {

            double diff = Math.abs(pitch - octave[i]);
            if (diff < bestDifference) {
                bestFitNoteIndex = i;
                bestDifference = diff;
                bestDifference1=pitch - octave[i];
            }
        }

        percentCloseness = (pitch / octave[bestFitNoteIndex]) * 100;
        outputNote = noteNames[bestFitNoteIndex] + Integer.toString(bestFitOctave);


        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");


        outputNote=outputNote+"    "+df.format(pitch)+"    "+(int)bestDifference1;
        uiHelper.display(outputNote, percentCloseness,pitch);
    }

}
