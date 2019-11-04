package com.mercy.rtsp.rtp.sockets;

import com.mercy.rtsp.rtsp.Protocol;
import com.mercy.rtsp.rtsp.RtpFrame;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by pedro on 7/11/18.
 */

public abstract class BaseRtpSocket {

  protected final static String TAG = "BaseRtpSocket";

  public static BaseRtpSocket getInstance(Protocol protocol, int videoSourcePort,
      int audioSourcePort) {
    return protocol == Protocol.TCP ? new RtpSocketTcp()
        : new RtpSocketUdp(videoSourcePort, audioSourcePort);
  }

  public abstract void setDataStream(OutputStream outputStream, String host);

  public abstract void sendFrame(RtpFrame rtpFrame) throws IOException;

  public abstract void close();
}
