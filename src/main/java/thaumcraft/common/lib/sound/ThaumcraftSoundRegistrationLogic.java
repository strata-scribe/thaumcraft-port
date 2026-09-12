package thaumcraft.common.lib.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import java.util.Random;

public class ThaumcraftSoundRegistrationLogic {

    public static final String MODID = "thaumcraft";

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CRAFTING = register("crafting");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLING = register("bubbling");
    public static final DeferredHolder<SoundEvent, SoundEvent> ZAP = register("zap");
    public static final DeferredHolder<SoundEvent, SoundEvent> RUNIC_MATRIX_HUM = register("runic_matrix_hum");
    public static final DeferredHolder<SoundEvent, SoundEvent> PAGE_TURN = register("page_turn");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MODID, name)));
    }

    public static float getVolume(float distance, float maxDistance) {
        return SoundAttenuationLogic.calculateVolume(distance, maxDistance);
    }

    public static float getPitch(float basePitch, float variance, Random random) {
        return SoundAttenuationLogic.calculatePitch(basePitch, variance, random);
    }
}
