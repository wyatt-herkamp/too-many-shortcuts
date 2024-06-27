/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.nmuk.impl;

import dev.kingtux.tms.TooManyShortcuts;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

@ApiStatus.Internal
public class AlternativeKeyBinding extends KeyBinding {
	private static final MethodHandle INCREMENT_TIMES_PRESSED_SUPER;
	static {
		MethodHandle methodHandle;
		try {
			methodHandle = MethodHandles.lookup().unreflectSpecial(KeyBinding.class.getDeclaredMethod("amecs$incrementTimesPressed"), AlternativeKeyBinding.class);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			TooManyShortcuts.INSTANCE.log(Level.ERROR, "Failed to get method handle for KeyBinding#amecs$incrementTimesPressed" + e.getMessage());
			methodHandle = null;
		}
		INCREMENT_TIMES_PRESSED_SUPER = methodHandle;
	}
	public AlternativeKeyBinding(KeyBinding parent, String translationKey, int code, String category) {
		super(translationKey, code, category);
		((IKeyBinding) this).nmuk_setParent(parent);
	}

	public AlternativeKeyBinding(KeyBinding parent, String translationKey, InputUtil.Type type, int code, String category) {
		super(translationKey, type, code, category);
		((IKeyBinding) this).nmuk_setParent(parent);
	}

	@Override
	public boolean isDefault() {
		if (getDefaultKey() == InputUtil.UNKNOWN_KEY) {
			return true;
		}
		return super.isDefault();
	}


	public void amecs$incrementTimesPressed() throws Throwable {
		INCREMENT_TIMES_PRESSED_SUPER.invoke(this);

		KeyBinding parent = ((IKeyBinding) this).nmuk_getParent();
		((de.siphalor.api.impl.duck.IKeyBinding) parent).amecs$incrementTimesPressed();
	}
}
