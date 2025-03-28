/*
 * Copyright 2020-2023 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.amecs.api.input;

import java.util.LinkedHashSet;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

/**
 * This class allows you to (un-)register {@link InputEventHandler}s
 *
 * @see InputEventHandler#handleInput(MinecraftClient)
 * @see #handleInputEvents
 */
@Environment(EnvType.CLIENT)
public class InputHandlerManager {

	// all methods and fields in this class must be used from main thread only or manual synchronization is required
	private static final LinkedHashSet<InputEventHandler> INPUT_HANDLERS = new LinkedHashSet<>();

	/**
	 * This method is called from MinecraftClient.handleInputEvents()
	 * <br>
	 * It calls all registered InputEventHandler
	 *
	 * @param client
	 */
	@ApiStatus.Internal
	public static void handleInputEvents(MinecraftClient client) {
		for (InputEventHandler handler : INPUT_HANDLERS) {
			handler.handleInput(client);
		}
	}

	public static boolean registerInputEventHandler(InputEventHandler handler) {
		return INPUT_HANDLERS.add(handler);
	}

	public static boolean removeInputEventHandler(InputEventHandler handler) {
		return INPUT_HANDLERS.remove(handler);
	}

}
