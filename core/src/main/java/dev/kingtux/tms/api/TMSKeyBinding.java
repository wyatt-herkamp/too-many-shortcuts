package dev.kingtux.tms.api;

import dev.kingtux.tms.api.modifiers.BindingModifiers;
import dev.kingtux.tms.mlayout.IKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;


public class TMSKeyBinding extends KeyBinding {
    private final BindingModifiers defaultModifiers;

    /**
     * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
     * <br>
     * See {@link TMSKeyBindingUtils#unregisterKeyBinding(KeyBinding)} for how to unregister it
     * If you want to set the key's translationKey directly use {@link #TMSKeyBinding(String, net.minecraft.client.util.InputUtil.Type, int, String, BindingModifiers)} instead
     *
     * @param id               the id to use
     * @param type             the input type which triggers this keybinding
     * @param code             the default key code
     * @param category         the id of the category which should include this keybinding
     * @param defaultModifiers the default modifiers
     */
    public TMSKeyBinding(Identifier id, InputUtil.Type type, int code, String category, BindingModifiers defaultModifiers) {
        this("key." + id.getNamespace() + "." + id.getPath(), type, code, category, defaultModifiers);
    }

    /**
     * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
     * <br>
     * See {@link TMSKeyBindingUtils#unregisterKeyBinding(KeyBinding)} for how to unregister it
     *
     * @param id               the id to use
     * @param type             the input type which triggers this keybinding
     * @param code             the default key code
     * @param category         the id of the category which should include this keybinding
     * @param defaultModifiers the default modifiers
     */
    public TMSKeyBinding(String id, InputUtil.Type type, int code, String category, BindingModifiers defaultModifiers) {
        super(id, type, code, category);
        if (defaultModifiers == null) {
            defaultModifiers = new BindingModifiers(); // the modifiable version of: KeyModifiers.NO_MODIFIERS
        }
        this.defaultModifiers = defaultModifiers;
        ((IKeyBinding) this).tms$getKeyModifiers().set(this.defaultModifiers);
    }

    public TMSKeyBinding(KeyBinding parent, String translationKey, int code, String category, BindingModifiers defaultModifiers) {
        super(translationKey, code, category);
        this.defaultModifiers = defaultModifiers;
        ((IKeyBinding) this).tms$setParent(parent);
    }

    public TMSKeyBinding(KeyBinding parent, String translationKey, InputUtil.Type type, int code, String category, BindingModifiers defaultModifiers) {
        super(translationKey, type, code, category);
        this.defaultModifiers = defaultModifiers;
        ((IKeyBinding) this).tms$setParent(parent);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (pressed) {
            onPressed();
        } else {
            onReleased();
        }
    }

    /**
     * A convenience method which gets fired when the keybinding is used
     */
    public void onPressed() {
    }

    /**
     * A convenience method which gets fired when the keybinding is stopped being used
     */
    public void onReleased() {
    }

    /**
     * Resets this keybinding (triggered when the user clicks on the "Reset" button).
     */
    public void resetKeyBinding() {
        ((IKeyBinding) this).tms$getKeyModifiers().set(defaultModifiers);
    }


    public BindingModifiers getDefaultModifiers() {
        return defaultModifiers;
    }

    @Override
    public boolean isDefault() {
        if (getDefaultKey() == InputUtil.UNKNOWN_KEY) {
            return true;
        }
        return super.isDefault();
    }


}
