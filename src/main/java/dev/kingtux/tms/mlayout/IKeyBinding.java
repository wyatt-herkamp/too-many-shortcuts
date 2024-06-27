package dev.kingtux.tms.mlayout;

import dev.kingtux.tms.api.modifiers.BindingModifiers;
import dev.kingtux.tms.api.modifiers.KeyModifier;
import dev.kingtux.tms.config.ConfigBindings;
import dev.kingtux.tms.config.ConfigKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IKeyBinding {

    void tms$setKeyModifiers(BindingModifiers modifiers);

    ConfigBindings tms$toConfig();

    void tms$fromConfig(ConfigBindings configBindings);
    //void tms$reset();
    
    BindingModifiers tms$getKeyModifiers();
    
    int tms$getTimesPressed();
    
    void tms$incrementTimesPressed();
    
    void tms$setTimesPressed(int timesPressed);
    
    InputUtil.Key tms$getBoundKey();

    short tms$getNextChildId();

    void tms$setNextChildId(short nextChildId);

    default boolean tms$isAlternative(){
        return tms$getParent() != null;
    }
    @Nullable  KeyBinding tms$getParent();

    void tms$setParent(KeyBinding binding);

    List<KeyBinding> tms$getAlternatives();

    int tms$getAlternativesCount();

    void tms$removeAlternative(KeyBinding binding);

    void tms$addAlternative(KeyBinding binding);

    int tms$getIndexInParent();

    void tms$setTranslationKey(String key);
    void tms$setCategory(String category);


}
