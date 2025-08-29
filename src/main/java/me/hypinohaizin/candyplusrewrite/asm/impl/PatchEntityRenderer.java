package me.hypinohaizin.candyplusrewrite.asm.impl;

import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.InsnList;
import me.hypinohaizin.candyplusrewrite.utils.ASMUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.MethodNode;
import me.hypinohaizin.candyplusrewrite.asm.api.MappingName;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import me.hypinohaizin.candyplusrewrite.asm.api.ClassPatch;

public class PatchEntityRenderer extends ClassPatch
{
    public PatchEntityRenderer() {
        super("net.minecraft.client.renderer.EntityRenderer", "buq");
    }
    
    @Override
    public byte[] transform(final byte[] bytes) {
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        final MappingName method = new MappingName("func_181560_a", "a", "updateCameraAndRender");
        final String desc = "(FJ)V";
        for (final MethodNode it : classNode.methods) {
            if (method.equalName(it.name) && it.desc.equals(desc)) {
                patchUpdateCameraAndRender(it);
            }
        }
        final ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    public void patchUpdateCameraAndRender(final MethodNode methodNode) {
        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, 182, "biq", "a", "(F)V");
        if (target != null) {
            final InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(23, 1));
            insnList.add(new MethodInsnNode(184, Type.getInternalName(getClass()), "updateCameraAndRenderHook", "(F)V", false));
            methodNode.instructions.insert(target, insnList);
        }
    }
    
    public static void updateCameraAndRenderHook(final float partialTicks) {
    }
}
