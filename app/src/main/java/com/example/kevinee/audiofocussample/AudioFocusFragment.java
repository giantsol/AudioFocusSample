package com.example.kevinee.audiofocussample;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class AudioFocusFragment extends Fragment implements View.OnClickListener, AudioManager.OnAudioFocusChangeListener {
    private static final String STATE_IDLE = "Idle";
    private static final String STATE_PLAYING = "Playing...";
    private static final String STATE_PLAYING_DUCKED = "Playing quietly...";

    private static final String STR_AUDIO_FOCUS_GAIN = "AUDIO FOCUS GAIN";
    private static final String STR_AUDIO_FOCUS_LOSS = "AUDIO FOCUS LOSS";
    private static final String STR_AUDIO_FOCUS_LOSS_TRANSIENT = "AUDIO FOCUS LOSS TRANSIENT";
    private static final String STR_AUDIO_FOCUS_LOSS_TRANSIENT_MAY_DUCK = "AUDIO FOCUS LOSS TRANSIENT MAY DUCK";
    private static final String STR_UNKNOWN = "UNKNOWN";

    private TextView audioStateTextView;
    private AudioManager audioManager;

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        ((TextView)view.findViewById(R.id.title)).setText(getTitle());
        view.findViewById(R.id.gain_focus).setOnClickListener(this);
        view.findViewById(R.id.gain_focus_transient).setOnClickListener(this);
        view.findViewById(R.id.gain_focus_may_duck).setOnClickListener(this);
        view.findViewById(R.id.abandon_focus).setOnClickListener(this);
        audioStateTextView = view.findViewById(R.id.audio_state);
        audioStateTextView.setText(STATE_IDLE);

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return view;
    }

    protected abstract String getTitle();

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.gain_focus:
                onGainFocusClicked();
                break;
            case R.id.gain_focus_transient:
                onGainFocusTransientClicked();
                break;
            case R.id.gain_focus_may_duck:
                onGainFocusMayDuckClicked();
                break;
            case R.id.abandon_focus:
                onAbandonFocusClicked();
                break;
        }
    }

    private void onGainFocusClicked() {
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioStateTextView.setText(STATE_PLAYING);
        }
    }

    private void onGainFocusTransientClicked() {
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioStateTextView.setText(STATE_PLAYING);
        }
    }

    private void onGainFocusMayDuckClicked() {
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioStateTextView.setText(STATE_PLAYING);
        }
    }

    private void onAbandonFocusClicked() {
        audioManager.abandonAudioFocus(this);
        audioStateTextView.setText(STATE_IDLE);
    }

    @Override
    public final void onAudioFocusChange(int focusChange) {
        Log.d("hello", getTitle() + ": " + parseAudioFocusInteger(focusChange));
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                audioStateTextView.setText(STATE_PLAYING);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                audioStateTextView.setText(STATE_IDLE);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                audioStateTextView.setText(STATE_IDLE);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                audioStateTextView.setText(STATE_PLAYING_DUCKED);
                break;
        }
    }

    private static String parseAudioFocusInteger(int audioFocusInteger) {
        switch (audioFocusInteger) {
            case AudioManager.AUDIOFOCUS_GAIN:
                return STR_AUDIO_FOCUS_GAIN;
            case AudioManager.AUDIOFOCUS_LOSS:
                return STR_AUDIO_FOCUS_LOSS;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                return STR_AUDIO_FOCUS_LOSS_TRANSIENT;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                return STR_AUDIO_FOCUS_LOSS_TRANSIENT_MAY_DUCK;
            default:
                return STR_UNKNOWN;
        }
    }
}
