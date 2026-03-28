package dev.kingtux.tms.mlayout;

import com.mojang.blaze3d.platform.InputConstants;
import dev.kingtux.tms.api.config.ConfigBindings;
import dev.kingtux.tms.api.modifiers.BindingModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.client.KeyMapping;

public interface IKeyBinding {

    void tms$setKeyModifiers(BindingModifiers modifiers);

    ConfigBindings tms$toConfig();

    void tms$fromConfig(ConfigBindings configBindings);

    /**
     * Resets the keybinding to the default value
     *
     * @param resetAlternatives if true, resets the alternatives as well. If this binding is an alternative it is ignored
     */
    void tms$resetBinding(boolean resetAlternatives);

    /**
     * Sets the keybinding to UNKNOWN and clears the modifiers
     *
     * @param clearAlternatives if true, clears the alternatives. If this binding is an alternative it is ignored
     */
    void tms$clearBinding(boolean clearAlternatives);

    BindingModifiers tms$getKeyModifiers();

    int tms$getTimesPressed();

    void tms$incrementTimesPressed();

    void tms$setTimesPressed(int timesPressed);


    short tms$getNextChildId();

    void tms$setNextChildId(short nextChildId);

    boolean tms$hasAlternatives();


    default boolean tms$isAlternative() {
        return tms$getParent() != null;
    }

    @Nullable
    KeyMapping tms$getParent();

    void tms$setParent(KeyMapping binding);

    List<KeyMapping> tms$getAlternatives();

    int tms$getAlternativesCount();

    void tms$removeAlternative(KeyMapping binding);

    void tms$addAlternative(KeyMapping binding);

    int tms$getIndexInParent();

    /// Proper Debug Message for KeyBinding
    String tms$debugString();


    InputConstants.Key tms$getBoundKey();

    void tms$setBoundKey(InputConstants.Key key);

    InputConstants.Key tms$getDefaultKey();

    void tms$reset();

    boolean tms$canBeReset();
}
