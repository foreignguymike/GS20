package com.distraction.gs20.utils;

import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.Gem;
import com.distraction.gs20.screens.PlayScreen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 5 gems at the start.
 * In order to try to keep scoring fair, I'll have a pre-defined set that is shuffled.
 */
public class GemSpawner {

    private static final Map<PlayScreen.Difficulty, int[]> ORDERS;
    private static final Map<PlayScreen.Difficulty, float[]> INTERVALS;

    static {
        ORDERS = new HashMap<>();
        ORDERS.put(PlayScreen.Difficulty.EASY, new int[]{
            0, 1, 1, 0,
            1, 0, 1, 0,
            0, 1, 0, 0,
            1, 0, 1, 1,
            0, 0, 1, 0,
            1, 1, 0, 1,
            0, 1, 0, 0,
            1, 0, 1, 0
        });
        ORDERS.put(PlayScreen.Difficulty.NORMAL, new int[]{
            0, 2, 1, 2,
            0, 1, 2, 0,
            2, 0, 1, 0,
            1, 2, 0, 1,
            2, 1, 0, 2,
            1, 0, 2, 0,
            2, 1, 2, 0,
            1, 2, 0, 1,
            0, 1, 2, 1,
            0, 2, 1, 0,
            2, 1, 2, 0,
            1, 2, 0, 1,
            1, 2, 0, 1,
            2, 1, 0, 1,
            2, 0, 1, 2
        });
        ORDERS.put(PlayScreen.Difficulty.CHALLENGE, new int[]{
            0, 2, 1, 3,
            2, 1, 3, 0,
            3, 1, 2, 0,
            1, 3, 0, 2,
            0, 2, 1, 3,
            2, 1, 3, 0,
            3, 1, 2, 0,
            1, 3, 0, 2,
            0, 2, 1, 3,
            2, 1, 3, 0,
            3, 1, 2, 0,
            1, 3, 0, 2,
            0, 2, 1, 3,
            2, 1, 3, 0,
            3, 1, 2, 0,
            1, 3, 0, 2
        });
        INTERVALS = new HashMap<>();
        INTERVALS.put(PlayScreen.Difficulty.EASY, new float[]{
            0f, 0f, 0f, 0f,
            0f, 1f, 2f, 2f,
            4f, 4f, 5f, 6f,
            6f, 7f, 8f, 8f,
            9f, 10f, 10f, 11f,
            12f, 12f, 13f, 14f,
            14f, 15f, 16f, 16f,
            17f, 18f, 18f, 19f
        });
        INTERVALS.put(PlayScreen.Difficulty.NORMAL, new float[]{
            0f, 0f, 0f, 0f,
            0f, 1f, 2f, 2f,
            3f, 4f, 4f, 5f,
            6f, 6f, 7f, 8f,
            8f, 9f, 10f, 10f, 10.5f,
            11f, 11.5f, 12f, 12f, 12.5f,
            13f, 13.5f, 14f, 14f, 14.5f,
            15f, 15.5f, 16f, 16f, 16.5f,
            17f, 17.5f, 18f, 18f, 18.5f,
            19f, 19.5f
        });
        INTERVALS.put(PlayScreen.Difficulty.CHALLENGE, new float[]{
            0f, 0f, 0f, 0f,
            0f, 1f, 2f, 2f,
            3f, 4f, 4f, 5f,
            5f, 6f, 6f, 7f,
            7f, 8f, 8f, 9f,
            9f, 9f, 10f, 10f,
            10.4f, 10.8f, 11.2f, 11.6f,
            12f, 12.4f, 12.8f, 13.2f,
            13.6f, 14f, 14.4f, 14.8f,
            15.2f, 15.6f, 16f, 16.4f,
            16.8f, 17.2f, 17.6f, 18f,
            18.4f, 18.8f, 19.2f, 19.6f
        });
    }

    private final Context context;
    private final PlayScreen.Difficulty difficulty;
    private final Gem.GemListener listener;

    private final int[] order;
    private final float[] interval;
    private final int[] shuffle;
    private final boolean[] taken;
    private float time = 0f;

    public GemSpawner(Context context, PlayScreen.Difficulty difficulty, Gem.GemListener listener) {
        this.context = context;
        this.difficulty = difficulty;
        this.listener = listener;

        if (difficulty == PlayScreen.Difficulty.EASY) {
            shuffle = new int[]{1, 3};
        } else if (difficulty == PlayScreen.Difficulty.NORMAL) {
            shuffle = new int[]{0, 1, 3};
        } else {
            shuffle = new int[]{0, 1, 2, 3};
        }
        shuffleArray(shuffle);
        shuffleArray(shuffle);
        shuffleArray(shuffle);

        order = ORDERS.get(difficulty);
        interval = INTERVALS.get(difficulty);
        taken = new boolean[order.length];
        Arrays.fill(taken, false);
    }

    public Gem[] init() {
        return new Gem[]{
            take(0f),
            take(0f),
            take(0f),
            take(0f),
            take(0f),
        };
    }

    public Gem take(float dt) {
        time += dt;
        for (int i = interval.length - 1; i >= 0; i--) {
            if (time >= interval[i] && !taken[i]) {
                taken[i] = true;
                ColorEntity.Type type = ColorEntity.Type.values()[shuffle[order[i]]];
                Gem.Size size;
                if (i == 0) size = Gem.Size.SMALL;
                else if (i % 9 == 0) size = Gem.Size.LARGE;
                else if (i % 4 == 0) size = Gem.Size.MEDIUM;
                else size = Gem.Size.SMALL;
                return new Gem(context, type, size, difficulty, listener);
            }
        }
        return null;
    }

    private static void shuffleArray(int[] ar) {
        Random random = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
