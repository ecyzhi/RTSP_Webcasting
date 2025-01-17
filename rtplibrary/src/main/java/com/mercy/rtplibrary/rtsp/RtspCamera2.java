package com.mercy.rtplibrary.rtsp;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.SurfaceView;
import android.view.TextureView;

import com.mercy.encoder.utils.CodecUtil;
import com.mercy.rtplibrary.base.Camera2Base;
import com.mercy.rtplibrary.view.LightOpenGlView;
import com.mercy.rtplibrary.view.OpenGlView;
import com.mercy.rtsp.rtsp.Protocol;
import com.mercy.rtsp.rtsp.RtspClient;
import com.mercy.rtsp.rtsp.VideoCodec;
import com.mercy.rtsp.utils.ConnectCheckerRtsp;

import java.nio.ByteBuffer;

/**
 * More documentation see:
 * {@link Camera2Base}
 *
 * Created by pedro on 4/06/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RtspCamera2 extends Camera2Base {

  private RtspClient rtspClient;

  public RtspCamera2(SurfaceView surfaceView, ConnectCheckerRtsp connectCheckerRtsp) {
    super(surfaceView);
    rtspClient = new RtspClient(connectCheckerRtsp);
  }

  public RtspCamera2(TextureView textureView, ConnectCheckerRtsp connectCheckerRtsp) {
    super(textureView);
    rtspClient = new RtspClient(connectCheckerRtsp);
  }

  public RtspCamera2(OpenGlView openGlView, ConnectCheckerRtsp connectCheckerRtsp) {
    super(openGlView);
    rtspClient = new RtspClient(connectCheckerRtsp);
  }

  public RtspCamera2(LightOpenGlView lightOpenGlView, ConnectCheckerRtsp connectCheckerRtsp) {
    super(lightOpenGlView);
    rtspClient = new RtspClient(connectCheckerRtsp);
  }

  public RtspCamera2(Context context, boolean useOpengl, ConnectCheckerRtsp connectCheckerRtsp) {
    super(context, useOpengl);
    rtspClient = new RtspClient(connectCheckerRtsp);
  }

  /**
   * Internet protocol used.
   *
   * @param protocol Could be Protocol.TCP or Protocol.UDP.
   */
  public void setProtocol(Protocol protocol) {
    rtspClient.setProtocol(protocol);
  }

  @Override
  public void resizeCache(int newSize) throws RuntimeException {
    rtspClient.resizeCache(newSize);
  }

  @Override
  public int getCacheSize() {
    return rtspClient.getCacheSize();
  }

  @Override
  public long getSentAudioFrames() {
    return rtspClient.getSentAudioFrames();
  }

  @Override
  public long getSentVideoFrames() {
    return rtspClient.getSentVideoFrames();
  }

  @Override
  public long getDroppedAudioFrames() {
    return rtspClient.getDroppedAudioFrames();
  }

  @Override
  public long getDroppedVideoFrames() {
    return rtspClient.getDroppedVideoFrames();
  }

  @Override
  public void resetSentAudioFrames() {
    rtspClient.resetSentAudioFrames();
  }

  @Override
  public void resetSentVideoFrames() {
    rtspClient.resetSentVideoFrames();
  }

  @Override
  public void resetDroppedAudioFrames() {
    rtspClient.resetDroppedAudioFrames();
  }

  @Override
  public void resetDroppedVideoFrames() {
    rtspClient.resetDroppedVideoFrames();
  }

  public void setVideoCodec(VideoCodec videoCodec) {
    videoEncoder.setType(videoCodec == VideoCodec.H265 ? CodecUtil.H265_MIME : CodecUtil.H264_MIME);
  }

  @Override
  public void setAuthorization(String user, String password) {
    rtspClient.setAuthorization(user, password);
  }

  @Override
  protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
    rtspClient.setIsStereo(isStereo);
    rtspClient.setSampleRate(sampleRate);
  }

  @Override
  protected void startStreamRtp(String url) {
    rtspClient.setUrl(url);
  }

  @Override
  protected void stopStreamRtp() {
    rtspClient.disconnect();
  }

  @Override
  public void setReTries(int reTries) {
    rtspClient.setReTries(reTries);
  }

  @Override
  public boolean shouldRetry(String reason) {
    return rtspClient.shouldRetry(reason);
  }

  @Override
  public void reConnect(long delay) {
    rtspClient.reConnect(delay);
  }

  @Override
  protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
    rtspClient.sendAudio(aacBuffer, info);
  }

  @Override
  protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
    ByteBuffer newSps = sps.duplicate();
    ByteBuffer newPps = pps.duplicate();
    ByteBuffer newVps = vps != null ? vps.duplicate() : null;
    rtspClient.setSPSandPPS(newSps, newPps, newVps);
    rtspClient.connect();
  }

  @Override
  protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
    rtspClient.sendVideo(h264Buffer, info);
  }
}

