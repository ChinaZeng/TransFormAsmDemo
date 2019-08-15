package com.zzw.plugin.lifecle

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class LifecycleOnDestroyMethodVisitor extends MethodVisitor implements Opcodes {

    LifecycleOnDestroyMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM6, mv)
    }

    @Override
    void visitCode() {
        super.visitCode()
    }

    @Override
    void visitInsn(int opcode) {

        //判断RETURN
        if (opcode == Opcodes.RETURN) {
            //在这里插入代码
            //方法后面插入
            mv.visitLdcInsn("zzz");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("-->onDestroy");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(POP);
        }

        super.visitInsn(opcode)
    }
}