/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.world;

import finalforeach.cosmicreach.networking.packets.sounds.PlaySound2DPacket;
import finalforeach.cosmicreach.sounds.GameSound;
import meteordevelopment.meteorclient.events.Cancellable;
//import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends Cancellable {
    private static final PlaySoundEvent INSTANCE = new PlaySoundEvent();

    public GameSound sound;

    public static PlaySoundEvent get(GameSound sound) {
        INSTANCE.setCancelled(false);
        INSTANCE.sound = sound;
        return INSTANCE;
    }
}
