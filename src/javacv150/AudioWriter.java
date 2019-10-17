/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacv150;

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
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;

/**
 *
 * @author kiosk01
 */
public class AudioWriter implements Runnable{
    private FFmpegFrameRecorder recorder;
    final private  int AUDIO_DEVICE_INDEX = 4;
    final private  int FRAME_RATE = 25;
    private int audioChannels=1;
    DataLine.Info dataLineInfo;
    TargetDataLine line;
    int sampleRate;
    int numChannels;
    int audioBufferSize;
    byte[] audioBytes;
    public boolean isRunning=true;
    public AudioWriter()
    {
        
    }
    public AudioWriter(FFmpegFrameRecorder mrecorder)
    {
        try {
            recorder=mrecorder;
            AudioFormat audioFormat = new AudioFormat(22050.0F, 16, audioChannels, true, false);
            // Get TargetDataLine with that format
            Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
            dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            line = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
            line.open(audioFormat);
            line.start();
            sampleRate = (int) audioFormat.getSampleRate();
            numChannels = audioFormat.getChannels();
            // Let's initialize our audio buffer...
            audioBufferSize = sampleRate * numChannels;
            audioBytes = new byte[audioBufferSize];
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void recAudio()
    {
        long t1=System.currentTimeMillis();
        int nBytesRead = line.read(audioBytes, 0, line.available());
        int nSamplesRead = nBytesRead / 2;
        short[] samples = new short[nSamplesRead];
        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
        ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
        if(audioChannels>0)
            try {
                //recorder.recordSamples(sampleRate, numChannels, sBuff);
                //recorder.recordSamples(sampleRate, audioChannels, sBuff);
                recorder.recordSamples(sBuff);
        } catch (FrameRecorder.Exception ex) {
            Logger.getLogger(AudioWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Audio time:"+(System.currentTimeMillis()-t1));
    }
    @Override
    public void run() {
        while(isRunning)
        {
            recAudio();
            try {
                Thread.sleep(1000/FRAME_RATE);
            } catch (InterruptedException ex) {
                Logger.getLogger(AudioWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
