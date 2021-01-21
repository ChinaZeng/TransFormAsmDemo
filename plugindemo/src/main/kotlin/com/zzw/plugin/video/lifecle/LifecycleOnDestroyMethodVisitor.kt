package com.zzw.plugin.video.lifecle

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class LifecycleOnDestroyMethodVisitor(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM9,mv),Opcodes {

    override fun visitInsn(opcode: Int) {
        println("LifecycleOnDestroyMethodVisitor visitInsn opcode:$opcode")

        //判断RETURN
        if (opcode == Opcodes.RETURN) {
            //在这里插入代码
            //方法后面插入
            mv.visitLdcInsn("zzz");
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("-->onDestroy");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP)
        }
        super.visitInsn(opcode)
    }

}