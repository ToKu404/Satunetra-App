package com.example.satunetra.activities.local.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class SpeechHelper {
    SpeechRecognizer speechRecognizer;
    Intent speechIntent;

    public SpeechHelper(Context context){
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Text");

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    public void setSpeechRecognizer(SpeechRecognizer speechRecognizer) {
        this.speechRecognizer = speechRecognizer;
    }

    public Intent getSpeechIntent() {
        return speechIntent;
    }

    public void setSpeechIntent(Intent speechIntent) {
        this.speechIntent = speechIntent;
    }
}
