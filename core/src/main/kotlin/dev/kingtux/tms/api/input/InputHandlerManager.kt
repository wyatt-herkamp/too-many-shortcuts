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
package dev.kingtux.tms.api.input

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import org.jetbrains.annotations.ApiStatus

/**
 * This class allows you to (un-)register [InputEventHandler]s
 *
 * @see InputEventHandler.handleInput
 * @see .handleInputEvents
 */
@Environment(EnvType.CLIENT)
object InputHandlerManager {
    // all methods and fields in this class must be used from main thread only or manual synchronization is required
    private val INPUT_HANDLERS = LinkedHashSet<InputEventHandler>()

    /**
     * This method is called from MinecraftClient.handleInputEvents()
     * <br></br>
     * It calls all registered InputEventHandler
     *
     * @param client
     */
    @JvmStatic
	@ApiStatus.Internal
    fun handleInputEvents(client: MinecraftClient?) {
        for (handler in INPUT_HANDLERS) {
            handler.handleInput(client)
        }
    }

    fun registerInputEventHandler(handler: InputEventHandler): Boolean {
        return INPUT_HANDLERS.add(handler)
    }

    fun removeInputEventHandler(handler: InputEventHandler): Boolean {
        return INPUT_HANDLERS.remove(handler)
    }
}
