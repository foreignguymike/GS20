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
 * Spawn new gem every 0.5 seconds for the first 10 seconds.
 * Then every 0.25 seconds for the last 10 seconds.
 * 5 + 20 + 40 - 1 (last one won't spawn) = 64 gems
 * In order to try to keep scoring fair, I'll have a pre-defined set that is shuffled.
 */
public class GemSpawner {

    private static final Map<PlayScreen.Difficulty, int[]> ORDERS;
    private static final float[] INTERVALS;

    static {
        ORDERS = new HashMap<>() {{
            put(PlayScreen.Difficulty.EASY, new int[]{
                0, 1, 1, 0,
                1, 0, 1, 0,
                0, 1, 0, 0,
                1, 0, 1, 1,
                0, 0, 1, 0,
                1, 0, 1, 1,
                0, 0, 1, 1,
                1, 0, 1, 0,
                1, 1, 0, 0,
                0, 1, 0, 0,
                1, 1, 0, 1,
                1, 0, 0, 0,
                1, 0, 1, 1,
                0, 1, 1, 0,
                1, 1, 0, 0,
                1, 0, 0, 1
            });
            put(PlayScreen.Difficulty.NORMAL, new int[]{
                0, 2, 1, 2,
                0, 1, 2, 0,
                2, 1, 1, 0,
                1, 2, 0, 2,
                0, 0, 1, 2,
                1, 1, 2, 0,
                2, 1, 2, 0,
                1, 2, 0, 1,
                0, 0, 1, 2,
                0, 1, 2, 0,
                2, 1, 1, 0,
                1, 2, 0, 1,
                0, 2, 1, 2,
                0, 1, 2, 0,
                2, 1, 1, 0,
                1, 2, 0, 2
            });
            put(PlayScreen.Difficulty.WEIRD, new int[]{
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
        }};
        INTERVALS = new float[]{
            0f, 0f, 0f, 0f,
            0f, 0.5f, 1f, 1.5f,
            2f, 2.5f, 3f, 3.5f,
            4f, 4.5f, 5f, 5.5f,
            6f, 6.5f, 7f, 7.5f,
            8f, 8.5f, 9f, 9.5f,
            10f, 10.25f, 10.5f, 10.75f,
            11f, 11.25f, 11.5f, 11.75f,
            12f, 12.25f, 12.5f, 12.75f,
            13f, 13.25f, 13.5f, 13.75f,
            14f, 14.25f, 14.5f, 14.75f,
            15f, 15.25f, 15.5f, 15.75f,
            16f, 16.25f, 16.5f, 16.75f,
            17f, 17.25f, 17.5f, 17.75f,
            18f, 18.25f, 18.5f, 18.75f,
            19f, 19.25f, 19.5f, 19.75f,
        };
    }

    private final Context context;

    private final int[] order;
    private final int[] shuffle;
    private final boolean[] taken;
    private float time = 0f;

    public GemSpawner(Context context, PlayScreen.Difficulty difficulty) {
        this.context = context;

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
        for (int i = INTERVALS.length - 1; i >= 0; i--) {
            if (time >= INTERVALS[i] && !taken[i]) {
                taken[i] = true;
                ColorEntity.Type type = ColorEntity.Type.values()[shuffle[order[i]]];
                Gem.Size size = i % 11 == 0 ? Gem.Size.LARGE : i % 5 == 0 ? Gem.Size.MEDIUM : Gem.Size.SMALL;
                return new Gem(context, type, size);
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
