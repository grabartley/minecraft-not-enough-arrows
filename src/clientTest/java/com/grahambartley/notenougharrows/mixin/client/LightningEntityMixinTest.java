package com.grahambartley.notenougharrows.mixin.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.LightningEntity;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.injection.ModifyArg;

class LightningEntityMixinTest {

  @Test
  void itsTargetIsCalledByVanillaTickExactlyAsOftenAsTheMixinRequires() throws IOException {
    final ModifyArg swap = soundSwap();

    final List<String> calls = callsInVanillaTick();

    assertEquals(
        swap.require(),
        calls.stream().filter(swap.at().target()::equals).count(),
        "LightningEntity.tick() calls " + calls);
    assertEquals(swap.require(), swap.allow());
  }

  @Test
  void itsArgumentIndexPointsAtTheSoundEventInVanillasSignature() {
    final ModifyArg swap = soundSwap();
    final String target = swap.at().target();
    final Type[] arguments = Type.getArgumentTypes(target.substring(target.indexOf('(')));

    assertEquals("net.minecraft.sound.SoundEvent", arguments[swap.index()].getClassName(), target);
  }

  private static ModifyArg soundSwap() {
    final List<ModifyArg> swaps =
        Arrays.stream(LightningEntityMixin.class.getDeclaredMethods())
            .map(method -> method.getAnnotation(ModifyArg.class))
            .filter(annotation -> annotation != null)
            .toList();
    assertEquals(1, swaps.size(), "LightningEntityMixin should declare one sound swap");
    return swaps.get(0);
  }

  private static List<String> callsInVanillaTick() throws IOException {
    final String resource = LightningEntity.class.getName().replace('.', '/') + ".class";
    try (InputStream bytes = LightningEntity.class.getClassLoader().getResourceAsStream(resource)) {
      assertNotNull(bytes, resource);
      final List<String> calls = new ArrayList<>();
      new ClassReader(bytes)
          .accept(
              new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions) {
                  if (!"tick".equals(name) || !"()V".equals(descriptor)) {
                    return null;
                  }
                  return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitMethodInsn(
                        final int opcode,
                        final String owner,
                        final String callee,
                        final String calleeDescriptor,
                        final boolean isInterface) {
                      calls.add("L" + owner + ";" + callee + calleeDescriptor);
                    }
                  };
                }
              },
              ClassReader.SKIP_DEBUG);
      return calls;
    }
  }
}
