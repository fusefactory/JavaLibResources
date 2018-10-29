package com.fuse.resources.vlc;

// import java.nio.ByteBuffer;
import java.util.List;

import com.sun.jna.Memory;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.AudioOutput;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class VLCPlayer implements BufferFormatCallback, RenderCallback, MediaPlayerEventListener {

	private MediaPlayerFactory mediaPlayerFactory;
	private DirectMediaPlayer mediaPlayer;
	private int width, height;
	private int[] textureBuffer;
	// private ByteBuffer buffer;
	private boolean ready;
	private boolean available;
	private boolean looping;

	public VLCPlayer(String source) {
		initVLC(source);
	}

	private void initVLC(String source) {
		VLCLibraryLoader.loadLibrary();

		mediaPlayerFactory = new MediaPlayerFactory();
		mediaPlayer = mediaPlayerFactory.newDirectMediaPlayer(this, this);
		mediaPlayer.addMediaPlayerEventListener(this);
		mediaPlayer.prepareMedia(source);
		ready = true;
	}

	public boolean ready() {
		return ready;
	}

	public void play() {
		mediaPlayer.setRepeat(false);
		mediaPlayer.play();
	}

	public void loop() {
		mediaPlayer.setRepeat(true);
		mediaPlayer.play();
	}

	public void pause() {
		mediaPlayer.setPause(true);
	}

	public void resume() {
		mediaPlayer.setPause(false);
	}

	public void stop() {
		mediaPlayer.setRepeat(false);
		mediaPlayer.stop();
	}

	public void mute() {
		mediaPlayer.mute(true);
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public boolean isPaused() {
		return !mediaPlayer.isPlaying();
	}

	/**
	 * Go to specified position
	 * @param position 0 (beginning), 1 (end)
	 */
	public void seek(float position) {
		mediaPlayer.setPosition(position);
	}

	public void gotoBeginning() {
		mediaPlayer.setPosition(0);
	}

	public void gotoEnd() {
		mediaPlayer.setPosition(1);
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int[] textureBuffer() {
		return textureBuffer;
	}

//	public ByteBuffer buffer() {
//		return buffer;
//	}

	public boolean available() {
		return available;
	}

	public float position() {
		return mediaPlayer.getPosition();
	}

	public long time() {
		return mediaPlayer.getTime();
	}

	public long duration() {
		return mediaPlayer.getLength();
	}

	@Override
	public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
		return new RV32BufferFormat(sourceWidth, sourceHeight);
	}

	@Override
	public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
		if (width == 0 && mediaPlayer.getVideoDimension() != null)
			width = (int) mediaPlayer.getVideoDimension().getWidth();
		if (height == 0 && mediaPlayer.getVideoDimension() != null)
			height = (int) mediaPlayer.getVideoDimension().getHeight();

		textureBuffer = nativeBuffers[0].getIntArray(0L, width * height);
		available = true;
	}

	/*
	 * VLCJ experimental branch
	 */
//	@Override
//	public void display(DirectMediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
//		if (width == 0 && mediaPlayer.getVideoDimension() != null)
//			width = (int) mediaPlayer.getVideoDimension().getWidth();
//		if (height == 0 && mediaPlayer.getVideoDimension() != null)
//			height = (int) mediaPlayer.getVideoDimension().getHeight();
//
//		buffer = nativeBuffers[0];
//		available = true;
//	}

	public List<AudioOutput> getAudioOutputs() {
		return mediaPlayerFactory.getAudioOutputs();
	}

	public void setAudioOutput(String audioOutputName) {
		mediaPlayer.setAudioOutput(audioOutputName);
	}

	/**
	 * Before setting audio outout device it must be setted the audio output.
	 * @param audioOutputName
	 * @param audioDeviceId
	 */
	public void setAudioOutputDevice(String audioOutputName, String audioDeviceId) {
		mediaPlayer.setAudioOutputDevice(audioOutputName, audioDeviceId);
	}

	public void close() {
		mediaPlayer.release();
		mediaPlayerFactory.release();
	}

	@Override
	public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t, String paramString) {
	}

	@Override
	public void opening(MediaPlayer mediaPlayer) {
	}

	@Override
	public void buffering(MediaPlayer mediaPlayer, float paramFloat) {
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
	}

	@Override
	public void forward(MediaPlayer mediaPlayer) {
	}

	@Override
	public void backward(MediaPlayer mediaPlayer) {
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		if (looping) {
			mediaPlayer.play();
		}
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, long paramLong) {
	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float paramFloat) {
	}

	@Override
	public void seekableChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void pausableChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void titleChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String paramString) {
	}

	@Override
	public void lengthChanged(MediaPlayer mediaPlayer, long paramLong) {
	}

	@Override
	public void videoOutput(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void scrambledChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void elementaryStreamAdded(MediaPlayer mediaPlayer, int paramInt1, int paramInt2) {
	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int paramInt1, int paramInt2) {
	}

	@Override
	public void elementaryStreamSelected(MediaPlayer mediaPlayer, int paramInt1, int paramInt2) {
	}

	@Override
	public void corked(MediaPlayer mediaPlayer, boolean paramBoolean) {
	}

	@Override
	public void muted(MediaPlayer mediaPlayer, boolean paramBoolean) {
	}

	@Override
	public void volumeChanged(MediaPlayer mediaPlayer, float paramFloat) {
	}

	@Override
	public void audioDeviceChanged(MediaPlayer mediaPlayer, String paramString) {
	}

	@Override
	public void chapterChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long paramLong) {
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {
	}

	@Override
	public void newMedia(MediaPlayer mediaPlayer) {
	}

	@Override
	public void subItemPlayed(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void subItemFinished(MediaPlayer mediaPlayer, int paramInt) {
	}

	@Override
	public void endOfSubItems(MediaPlayer mediaPlayer) {
	}
}
