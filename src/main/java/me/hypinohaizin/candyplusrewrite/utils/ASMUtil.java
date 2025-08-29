package me.hypinohaizin.candyplusrewrite.utils;

import org.objectweb.asm.ClassReader;
import java.util.Iterator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMUtil
{
    public static AbstractInsnNode findMethodInsn(final MethodNode mn, final int opcode, final String owner, final String name, final String desc) {
        for (final AbstractInsnNode insn : mn.instructions.toArray()) {
            if (insn instanceof MethodInsnNode) {
                final MethodInsnNode method = (MethodInsnNode)insn;
                if (method.getOpcode() == opcode && method.owner.equals(owner) && method.name.equals(name) && method.desc.equals(desc)) {
                    return insn;
                }
            }
        }
        return null;
    }
    
    public static byte[] toBytes(final ClassNode classNode) {
        final ClassWriter writer = new ClassWriter(3);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    public static MethodNode findMethod(final ClassNode classNode, final String name, final String desc) {
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
                return methodNode;
            }
        }
        return null;
    }
    
    public static ClassNode getNode(final byte[] classBuffer) {
        final ClassNode classNode = new ClassNode();
        final ClassReader reader = new ClassReader(classBuffer);
        reader.accept(classNode, 0);
        return classNode;
    }
}
