package com.example.satunetra.helper;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class SpeechHelper {
    SpeechRecognizer speechRecognizer;
    Intent speechIntent;

    public SpeechHelper(Context context, Integer maxDelay){
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, maxDelay);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

    }

    public SpeechRecognizer getSpeechRecognizer() {

        return speechRecognizer;

    }


    public Intent getSpeechIntent() {
        return speechIntent;
    }

}
