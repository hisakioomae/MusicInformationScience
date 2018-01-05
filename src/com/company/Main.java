package com.company;

import processing.core.*;
import ddf.minim.*;
import ddf.minim.analysis.*;

import java.awt.*;

public class Main extends PApplet {

    private final static int WINDOW_WIDTH = 960;
    private final static int WINDOW_HEIGHT = 600;

    private Minim m;
    private AudioPlayer s;
    private AudioMetaData d;
    private FFT f;

    @Override
    public void settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public void setup() {
        m = new Minim(this);
        s = m.loadFile("input/hoge.wav", 1024);
        s.play();
        f = new FFT(s.bufferSize(), s.sampleRate());
        d = s.getMetaData();
    }

    @Override
    public void draw() {
        background(5, 5, 20);
        setAudioRecordButton();
        setLeo();
        setShootingStar(calcColor(getMostFrequentFrequency()));
    }

    public void mousePressed() {
        // 停止
        if (mouseX > WINDOW_WIDTH - 300 && mouseX < WINDOW_WIDTH - 250 && mouseY > WINDOW_HEIGHT - 50 && mouseY < WINDOW_HEIGHT) {
            s.pause();
        }
        // 再生
        else if (mouseX > WINDOW_WIDTH - 200 && mouseX < WINDOW_WIDTH - 150 && mouseY > WINDOW_HEIGHT - 50 && mouseY < WINDOW_HEIGHT) {
            s.play();
        }
        // 音量UP
        else if (mouseX > WINDOW_WIDTH - 120 && mouseX < WINDOW_WIDTH - 70 && mouseY > WINDOW_HEIGHT - 50 && mouseY < WINDOW_HEIGHT) {
            s.setGain(s.getGain() + 5f);
        }
        // 音量DOWN
        else if (mouseX > WINDOW_WIDTH - 60 && mouseX < WINDOW_WIDTH - 10 && mouseY > WINDOW_HEIGHT - 50 && mouseY < WINDOW_HEIGHT) {
            s.setGain(s.getGain() - 5f);
        }
    }

    public static void main(String[] args) {
        PApplet.main("com.company.Main");
    }

    /**
     * 獅子座を反映する関数
     */
    private void setLeo() {
        final int adjustX = 50;
        final int width = 3;
        final int height = 3;
        stroke(255, 255, 255);
        fill(255, 255, 255);
        ellipse(624 + adjustX, 67, width, height); // A
        ellipse(540 + adjustX, 24, width, height); // B
        ellipse(460 + adjustX, 105, width, height); // C
        ellipse(471 + adjustX, 186, width, height); // D
        ellipse(615 + adjustX, 242, width, height); // E
        ellipse(678 + adjustX, 377, width, height); // F
        ellipse(363 + adjustX, 400, width, height); // G
        ellipse(157 + adjustX, 481, width, height); // H
        ellipse(199 + adjustX, 267, width, height); // I
    }

    /**
     * 流れ星を流す関数
     * 正規化された音響信号が閾値を超えた時カウントする
     * カウントの数によって流れる星の数を変化させる
     */
    private void setShootingStar(Color color) {
        stroke(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        int count = 0;
        for (int i = 0; i < s.bufferSize() - 1; i++) {
            if (abs(s.mix.get(i)) > 0.60) {
                count++;
            }
        }
        if (0 < count && count < 10) {
            float randomX = random(0, WINDOW_WIDTH);
            float randomY = random(0, WINDOW_HEIGHT);
            for (int i = 0; i < 50; i++) {
                ellipse(i + randomX, i + randomY, 3, 3);
            }
        } else if (10 < count && count < 100) {
            float randomX = random(0, WINDOW_WIDTH);
            float randomY = random(0, WINDOW_HEIGHT);
            for (int i = 0; i < 100; i++) {
                ellipse(i + randomX, i + randomY, 3, 3);
            }
        } else if (100 < count && count < 200) {
            float randomX = random(0, WINDOW_WIDTH);
            float randomY = random(0, WINDOW_HEIGHT);
            for (int i = 0; i < 100; i++) {
                ellipse(i + randomX, i + randomY, 3, 3);
            }
            randomX = random(0, WINDOW_WIDTH);
            randomY = random(0, WINDOW_HEIGHT);
            for (int i = 0; i < 100; i++) {
                ellipse(i + randomX, i + randomY, 3, 3);
            }
        } else if (200 < count) {
            float randomX = random(0, WINDOW_WIDTH);
            float randomY = random(0, WINDOW_HEIGHT);
            for (int i = 0; i < 150; i++) {
                ellipse(i + randomX, i + randomY, 5, 5);
            }
            randomX = random(0, WINDOW_WIDTH);
            randomY = random(0, WINDOW_HEIGHT);
            for (int i = 0; i < 150; i++) {
                ellipse(i + randomX, i + randomY, 5, 5);
            }
        }
    }

    /**
     * スペクトルパワーの最頻出値を返す関数
     *
     * @return スペクトルのパワーの最頻出
     */
    private int getMostFrequentFrequency() {
        f.forward(s.mix);
        float maxFreq = 0;
        float calcFreq;
        int result = 0;
        for (int i = 0; i < f.specSize(); i++) {
            calcFreq = f.getBand(i);
            if (maxFreq < calcFreq) {
                maxFreq = calcFreq;
                result = i;
            }
        }
        return result;
    }

    /**
     * 最頻出周波数によって色を返す関数
     * 最頻出周波数が高いほどどんどん白く
     * 100以上と高い時は，背景と補色の関係である黄色を採用
     *
     * @param freq 最頻出周波数
     * @return 色情報
     */
    private Color calcColor(int freq) {
        Color color;
        if (0 < freq && freq < 10) {
            color = new Color(32, 192, 192, 128);
            return color;
        } else if (10 < freq && freq < 20) {
            color = new Color(96, 192, 192, 192);
            return color;
        } else if (20 < freq && freq < 30) {
            color = new Color(255, 255, 255, 192);
            return color;
        } else if (30 < freq && freq < 100) {
            color = new Color(255, 255, 255, 255);
            return color;
        } else if (100 < freq) {
            color = new Color(255, 255, 0, 255);
            return color;
        }
        return new Color(5, 5, 20);
    }

    /**
     * 停止ボタン
     * 再生ボタン
     * 音量ボタン
     * の描画する関数
     */
    private void setAudioRecordButton() {
        stroke(255, 0, 0);
        fill(255, 0, 0);
        rect(WINDOW_WIDTH - 300, WINDOW_HEIGHT - 50, 50, 50);  //停止ボタンを描画
        stroke(0, 255, 0);
        fill(0, 255, 0);
        triangle(WINDOW_WIDTH - 200, WINDOW_HEIGHT - 50, WINDOW_WIDTH - 200, WINDOW_HEIGHT, WINDOW_WIDTH - 150, WINDOW_HEIGHT - 25);

        stroke(255, 255, 255);
        fill(255, 255, 255);
        rect(WINDOW_WIDTH - 120, WINDOW_HEIGHT - 50, 50, 50);  //音量UP
        rect(WINDOW_WIDTH - 60, WINDOW_HEIGHT - 50, 50, 50);  //音量DOWN
        fill(255, 0, 0);
        text("UP", WINDOW_WIDTH - 103, WINDOW_HEIGHT - 20);
        fill(0, 0, 255);
        text("DOWN", WINDOW_WIDTH - 55, WINDOW_HEIGHT - 20);
    }
}
