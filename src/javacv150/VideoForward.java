/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacv150;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

/**
 *
 * @author kiosk01
 */
public class VideoForward implements Runnable{
    final private  int WEBCAM_DEVICE_INDEX = 0;
    final private  int AUDIO_DEVICE_INDEX = 4;

    final private  int FRAME_RATE = 10;
    final private  int GOP_LENGTH_IN_FRAMES = 50;
    OutputStream os;
    InputStream is;
    int audioChannels;
    
    public VideoForward(OutputStream mos,InputStream mis,int maudioChannels)
    {
        os=mos;
        is=mis;
        audioChannels=maudioChannels;
    }
    @Override
    public void run() {
        FFmpegFrameGrabber grabber=new FFmpegFrameGrabber(is);
        int captureWidth = 480;
        int captureHeight = 320;
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                os,
                captureWidth, captureHeight, audioChannels);
        
//        recorder.setInterleaved(true);
//        recorder.setVideoOption("tune", "zerolatency");
//        recorder.setVideoOption("preset", "ultrafast");
//        recorder.setVideoOption("crf", "28");
//        recorder.setVideoBitrate(2000000);
        
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H265);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
        recorder.setFormat("mpegts");
        // FPS (frames per second)
        recorder.setFrameRate(FRAME_RATE);
        recorder.setGopSize(GOP_LENGTH_IN_FRAMES);
        recorder.setAudioOption("crf", "0");
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
            grabber.start();
            recorder.start();
            Frame f;
            while((f=grabber.grab())!=null)
            {
                recorder.record(f);
            }
            recorder.close();
            grabber.close();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(VideoForward.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FrameRecorder.Exception ex) {
            Logger.getLogger(VideoForward.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
