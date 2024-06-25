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

package de.siphalor.amecs.impl.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

@Environment(EnvType.CLIENT)
public class AmecsAPIMixinConfig implements IMixinConfigPlugin {
	private final String MOUSE_CLASS_INTERMEDIARY = "net.minecraft.class_312";
	private final String SCREEN_CLASS_INTERMEDIARY = "net.minecraft.class_437";
	private final String ELEMENT_CLASS_INTERMEDIARY = "net.minecraft.class_364";
	private final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
	private String mouseClassRemapped;
	private String screenClassRemappedType;
	private String screenMouseScrolledRemappedType;

	@Override
	public void onLoad(String mixinPackage) {
		mouseClassRemapped = mappingResolver.mapClassName("intermediary", MOUSE_CLASS_INTERMEDIARY);
		screenClassRemappedType = mappingResolver.mapClassName("intermediary", SCREEN_CLASS_INTERMEDIARY).replace('.', '/');
		screenMouseScrolledRemappedType = mappingResolver.mapMethodName("intermediary", ELEMENT_CLASS_INTERMEDIARY, "method_25401", "(DDDD)Z");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.endsWith("MixinNMUKKeyBindingHelper")) {
			return FabricLoader.getInstance().isModLoaded("nmuk");
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if (targetClassName.equals(mouseClassRemapped)) {
			String onMouseScrollRemapped = mappingResolver.mapMethodName("intermediary", MOUSE_CLASS_INTERMEDIARY, "method_1598", "(JDD)V");

			for (MethodNode method : targetClass.methods) {
				if (onMouseScrollRemapped.equals(method.name)) {
					targetClass.methods.remove(method);
					method.accept(new OnMouseScrollTransformer(
							targetClass.visitMethod(method.access, method.name, method.desc, method.signature, method.exceptions.toArray(new String[0])),
							method.access, method.name, method.desc
					));
					break;
				}
			}
		}
	}

	// The purpose of this is to capture the return value of the currentScreen.mouseScrolled call.
	// Other mods also require this so this would lead to @Redirect conflicts when done via mixins.
	private class OnMouseScrollTransformer extends GeneratorAdapter {
		protected OnMouseScrollTransformer(MethodVisitor methodVisitor, int access, String name, String descriptor) {
			super(Opcodes.ASM9, methodVisitor, access, name, descriptor);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
			if (opcode == Opcodes.INVOKEVIRTUAL && screenClassRemappedType.equals(owner) && screenMouseScrolledRemappedType.equals(name)) {
				super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
				super.loadThis();
				super.dupX1();
				super.pop();
				super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, mouseClassRemapped.replace('.', '/'), "amecs$onMouseScrolledScreen", "(Z)Z", false);
				return;
			}
			super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
		}
	}
}
