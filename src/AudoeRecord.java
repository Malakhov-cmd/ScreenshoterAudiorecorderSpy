import javax.sound.sampled.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudoeRecord {
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
   private DataLine.Info info = new DataLine.Info(TargetDataLine.class, getAudioFormat());
    private TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
    private SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public AudoeRecord() throws LineUnavailableException {
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return format;
    }

    public void recordAudio(int milliseconds) {
        String fileName = "Rec_" + dateFormat.format(new Date()) + ".wav";
        File file = new File(fileName);
        start(file);
        stop(milliseconds, file);
    }

    private void start(File file) {
        Thread thread_start = new Thread(() -> {
            try {
                line.open(getAudioFormat());
                line.start();
                AudioInputStream ais = new AudioInputStream(line);
                AudioSystem.write(ais, fileType, file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread_start.start();
    }

    private void stop(int milliseconds, File file) {
        Thread thread_stop = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                line.stop();
                line.close();
                System.out.println("Запись " + file.getName() + " завершена.");
                recordAudio(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread_stop.start();
    }
}
