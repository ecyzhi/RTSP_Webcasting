package com.mercy.rtsp.rtp.packets;

import com.mercy.rtsp.rtsp.RtpFrame;

/**
 * Created by pedro on 7/11/18.
 */

public interface AudioPacketCallback {
  void onAudioFrameCreated(RtpFrame rtpFrame);
}
