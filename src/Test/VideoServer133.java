/**
 * @author Ben Davenport
 * 
 * This class is a simple example for broadcasting a video capture device (ie, webcam) and an audio capture device (ie, microphone)
 * using an FFmpegFrameRecorder. 
 * 
 * FFmpegFrameRecorder allows the output destination to be either a FILE or an RTMP endpoint (Wowza, FMS, et al)
 * 
 * IMPORTANT: There are potential timing issues with audio/video synchronicity across threads, I am working on finding a solution, but
 * chime in if you can fig it out :o)
 */
package Test;
import javacv150.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import java.nio.channels.*;
import org.bytedeco.javacv.FrameGrabber;
public class VideoServer133
{
    final private static int WEBCAM_DEVICE_INDEX = 0;
    final private static int AUDIO_DEVICE_INDEX = 4;

    final private static int FRAME_RATE = 25;
    final private static int GOP_LENGTH_IN_FRAMES = 50;

    private static long startTime = 0;
    private static long videoTS = 0;

    public static void main(String[] args) 
    {
        int captureWidth = 1280;
        int captureHeight = 720;

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        //OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("rtsp://admin:kiosk123@192.168.68.108");
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        try {
            grabber.start();
            
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ServerSocket ss=null;
        Socket scc=null;
        OutputStream os=null;
        try {
            ss=new ServerSocket(8888);
            System.out.println("Waiting for client...");
            scc=ss.accept();
            System.out.println("A client is just connected! Start streaming...");
            os=scc.getOutputStream();
            
        } catch (IOException ex) {
            Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            // org.bytedeco.javacv.FFmpegFrameRecorder.FFmpegFrameRecorder(String
            // filename, int imageWidth, int imageHeight, int audioChannels)
            // For each param, we're passing in...
            // filename = either a path to a local file we wish to create, or an
            // RTMP url to an FMS / Wowza server
            // imageWidth = width we specified for the grabber
            // imageHeight = height we specified for the grabber
            // audioChannels = 2, because we like stereo
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
        //        "rtmp://my-streaming-server/app_name_here/instance_name/stream_name",
                //"/home/kiosk01/test02oct.mp4",
                os,
                captureWidth, captureHeight, 1);
        recorder.setInterleaved(true);

        // decrease "startup" latency in FFMPEG (see:
        // https://trac.ffmpeg.org/wiki/StreamingGuide)
        recorder.setVideoOption("tune", "zerolatency");
        // tradeoff between quality and encode speed
        // possible values are ultrafast,superfast, veryfast, faster, fast,
        // medium, slow, slower, veryslow
        // ultrafast offers us the least amount of compression (lower encoder
        // CPU) at the cost of a larger stream size
        // at the other end, veryslow provides the best compression (high
        // encoder CPU) while lowering the stream size
        // (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
        recorder.setVideoOption("preset", "ultrafast");
        // Constant Rate Factor (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
        recorder.setVideoOption("crf", "28");
        // 2000 kb/s, reasonable "sane" area for 720
        recorder.setVideoBitrate(2000000);
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H265);
        recorder.setFormat("mpegts");
        // FPS (frames per second)
        recorder.setFrameRate(FRAME_RATE);
        // Key frame interval, in our case every 2 seconds -> 30 (fps) * 2 = 60
        // (gop length)
        recorder.setGopSize(GOP_LENGTH_IN_FRAMES);

        // We don't want variable bitrate audio
        recorder.setAudioOption("crf", "0");
        // Highest quality
        recorder.setAudioQuality(0);
        // 192 Kbps
        recorder.setAudioBitrate(44000);
        recorder.setSampleRate(22050);
        recorder.setAudioChannels(1);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

        try {
            // Jack 'n coke... do it...
            recorder.start();
        } catch (Exception ex) {
            Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Thread for audio capture, this could be in a nested private class if you prefer...
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                // Pick a format...
                // NOTE: It is better to enumerate the formats that the system supports,
                // because getLine() can error out with any particular format...
                // For us: 44.1 sample rate, 16 bits, stereo, signed, little endian
                AudioFormat audioFormat = new AudioFormat(22050.0F, 16, 1, true, false);

                // Get TargetDataLine with that format
                Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
                Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

                try
                {
                    // Open and start capturing audio
                    // It's possible to have more control over the chosen audio device with this line:
                    // TargetDataLine line = (TargetDataLine)mixer.getLine(dataLineInfo);
                    TargetDataLine line = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
                    line.open(audioFormat);
                    line.start();

                    int sampleRate = (int) audioFormat.getSampleRate();
                    int numChannels = audioFormat.getChannels();

                    // Let's initialize our audio buffer...
                    int audioBufferSize = sampleRate * numChannels;
                    byte[] audioBytes = new byte[audioBufferSize];

                    // Using a ScheduledThreadPoolExecutor vs a while loop with
                    // a Thread.sleep will allow
                    // us to get around some OS specific timing issues, and keep
                    // to a more precise
                    // clock as the fixed rate accounts for garbage collection
                    // time, etc
                    // a similar approach could be used for the webcam capture
                    // as well, if you wish
                    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
                    exec.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run()
                        {
                            try
                            {
                                // Read from the line... non-blocking
                                int nBytesRead = line.read(audioBytes, 0, line.available());

                                // Since we specified 16 bits in the AudioFormat,
                                // we need to convert our read byte[] to short[]
                                // (see source from FFmpegFrameRecorder.recordSamples for AV_SAMPLE_FMT_S16)
                                // Let's initialize our short[] array
                                int nSamplesRead = nBytesRead / 2;
                                short[] samples = new short[nSamplesRead];

                                // Let's wrap our short[] into a ShortBuffer and
                                // pass it to recordSamples
                                ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                                ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                                // recorder is instance of
                                // org.bytedeco.javacv.FFmpegFrameRecorder
                                recorder.recordSamples(sampleRate, numChannels, sBuff);
                            } 
                            catch (org.bytedeco.javacv.FrameRecorder.Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, 0, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
                } 
                catch (LineUnavailableException e1)
                {
                    e1.printStackTrace();
                }
            }
        }).start();

        // A really nice hardware accelerated component for our preview...
        CanvasFrame cFrame = new CanvasFrame("Capture Preview", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        Frame capturedFrame = null;

        // While we are capturing...
        while (true)
        {
            try {
                if((capturedFrame = grabber.grab()) == null)
                    break;
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (cFrame.isVisible())
            {
                // Show our frame in the preview
                cFrame.showImage(capturedFrame);
            }

            // Let's define our start time...
            // This needs to be initialized as close to when we'll use it as
            // possible,
            // as the delta from assignment to computed time could be too high
            if (startTime == 0)
                startTime = System.currentTimeMillis();

            // Create timestamp for this frame
            videoTS = 1000 * (System.currentTimeMillis() - startTime);

            // Check for AV drift
            if (videoTS > recorder.getTimestamp())
            {
                System.out.println(
                        "Lip-flap correction: " 
                        + videoTS + " : "
                        + recorder.getTimestamp() + " -> "
                        + (videoTS - recorder.getTimestamp()));

                // We tell the recorder to write this frame at this timestamp
                recorder.setTimestamp(videoTS);
            }

            try {
                // Send the frame to the org.bytedeco.javacv.FFmpegFrameRecorder
                recorder.record(capturedFrame);
            } catch (Exception ex) {
                System.err.println("Cannot write, check network connection");
                //Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }

        
        try {
            cFrame.dispose();
            recorder.stop();
            grabber.stop();
        } catch (Exception ex) {
            Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
