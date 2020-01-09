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
package ServerVideo;

import javacv150.*;
import java.io.IOException;
import java.io.InputStream;
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
public class VideoAudioWriterWebcam implements Runnable
{
    final private  int WEBCAM_DEVICE_INDEX = 0;
    final private  int AUDIO_DEVICE_INDEX = 4;

    final private  int FRAME_RATE = 25;
    final private  int GOP_LENGTH_IN_FRAMES = 50;

    private long startTime = 0;
    private long videoTS = 0;
    private OutputStream os=null;
    private InputStream is=null;
    private int audioChannels=1;
    private OpenCVFrameGrabber grabber;
    private FFmpegFrameRecorder recorder;
    long t1;
    private AudioWriter aw;
    private Thread audioThread;
    public VideoAudioWriterWebcam()
    {
        
    }
    public VideoAudioWriterWebcam(OutputStream mos, int webcam, int maudioChannels)
    {
        os=mos;
        audioChannels=maudioChannels;
        int captureWidth = 480;
        int captureHeight = 320;
        grabber = new OpenCVFrameGrabber(webcam);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        try {
            grabber.start();
            
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(VideoAudioWriterWebcam.class.getName()).log(Level.SEVERE, null, ex);
        }
        recorder = new FFmpegFrameRecorder(
                os,
                captureWidth, captureHeight, audioChannels);
        
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mpegts");
        // FPS (frames per second)
        recorder.setFrameRate(FRAME_RATE);
        recorder.setGopSize(GOP_LENGTH_IN_FRAMES);
        //recorder.setAudioOption("crf", "0");
        
        // Highest quality
        if(audioChannels>0)
        {
            recorder.setAudioQuality(0);
            recorder.setAudioBitrate(44000);
            recorder.setSampleRate(22050);
            recorder.setAudioChannels(audioChannels);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        }
        try {
            recorder.start();
            //aw=new AudioWriter(recorder);
            //audioThread=new Thread(aw);
        } catch (Exception ex) {
            Logger.getLogger(VideoAudioWriterWebcam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() 
    {
        
        
        // A really nice hardware accelerated component for our preview...
        CanvasFrame cFrame = new CanvasFrame("Capture Preview", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        Frame capturedFrame = null;
        // While we are capturing...
        //audioThread.start();
        while (true)
        {
            try {
                if((capturedFrame = grabber.grab()) == null)
                    break;
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(VideoAudioWriterWebcam.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (cFrame.isVisible())
            {
                cFrame.showImage(capturedFrame);
            }
            if (startTime == 0)
                startTime = System.currentTimeMillis();
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
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
            //audioThread.stop();
        } catch (Exception ex) {
            Logger.getLogger(VideoAudioWriterWebcam.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(VideoAudioWriterWebcam.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
