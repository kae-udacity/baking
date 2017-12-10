package com.example.android.baking.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.android.baking.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepVideoFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = StepVideoFragment.class.getSimpleName();
    private static final String POSITION_MILLISECONDS = "position_milliseconds";
    private static final String PLAY_WHEN_READY = "state_playing";

    @BindView(R.id.player_view_step)
    SimpleExoPlayerView playerView;

    private static MediaSessionCompat mediaSession;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder stateBuilder;
    private String url;
    private long playbackPosition;
    private boolean playWhenReady = true;

    public StepVideoFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment StepVideoFragment.
     */
    public static StepVideoFragment newInstance(Bundle bundle) {
        StepVideoFragment stepVideoFragment = new StepVideoFragment();
        stepVideoFragment.setArguments(bundle);
        return stepVideoFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_video, container, false);
        ButterKnife.bind(this, view);
        initializeMediaSession(getContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(getString(R.string.video_url));
        }

        if (!TextUtils.isEmpty(url)) {

            Configuration configuration = getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                AppCompatActivity activity = ((AppCompatActivity) getActivity());
                if (activity != null && !TextUtils.isEmpty(url)) {
                    ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.hide();
                    }
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }
        }

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(POSITION_MILLISECONDS);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (exoPlayer != null) {
            outState.putLong(POSITION_MILLISECONDS, exoPlayer.getCurrentPosition());
            outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer(Uri.parse(url), getContext());
        exoPlayer.seekTo(playbackPosition);
        exoPlayer.setPlayWhenReady(playWhenReady);
    }

    @Override
    public void onPause() {
        super.onPause();
        playbackPosition = exoPlayer.getCurrentPosition();
        playWhenReady = exoPlayer.getPlayWhenReady();
        exoPlayer.setPlayWhenReady(false);
    }

    /**
     * Releases the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mediaSession.setActive(false);
    }

    /**
     * Releases ExoPlayer.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void initializeMediaSession(Context context) {
        mediaSession = new MediaSessionCompat(context, TAG);
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        mediaSession.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE
                );

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setActive(true);

    }

    private void initializePlayer(Uri uri, Context context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(context),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            playerView.setPlayer(exoPlayer);
            exoPlayer.addListener(this);

            exoPlayer.setPlayWhenReady(true);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer.prepare(mediaSource, true, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory(getString(R.string.app_name)),
                new DefaultExtractorsFactory(), null, null);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            stateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(),
                    1f
            );
        } else if((playbackState == ExoPlayer.STATE_READY)){
            stateBuilder.setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(),
                    1f
            );
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }

    }
}