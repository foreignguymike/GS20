package com.distraction.gs20.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioHandler {

    private final Map<String, Music> music;
    private final Map<String, Sound> sounds;

    private String currentMusic;

    public AudioHandler() {
        music = new HashMap<>();

        sounds = new HashMap<>();
        addSound("cdlow", "sfx/countdownlow.wav");
        addSound("cdhigh", "sfx/countdownhigh.wav");
        addSound("hit", "sfx/hit.wav");
        addSound("miss", "sfx/miss.wav");
        addSound("click", "sfx/click.wav");
        addSound("success", "sfx/success.wav");
        addSound("tick", "sfx/tick.wav");
    }

    private void addMusic(String key, String fileName) {
        music.put(key, Gdx.audio.newMusic(Gdx.files.internal(fileName)));
    }

    private void addSound(String key, String fileName) {
        sounds.put(key, Gdx.audio.newSound(Gdx.files.internal(fileName)));
    }

    public void playMusic(String key, float volume, boolean looping) {
        if (Objects.equals(key, currentMusic)) return;
        Music newMusic = music.get(key);
        if (currentMusic != null) {
            stopMusic();
        }
        currentMusic = key;
        music.get(currentMusic).play();
    }

    public void stopMusic() {
        Music m = music.get(currentMusic);
        if (m != null) {
            m.stop();
            currentMusic = null;
        }
    }

    public void playSound(String key) {
        playSound(key, 1);
    }

    public void playSound(String key, float volume) {
        for (Map.Entry<String, Sound> entry : sounds.entrySet()) {
            if (entry.getKey().equals(key)) entry.getValue().play(volume);
        }
    }

}
