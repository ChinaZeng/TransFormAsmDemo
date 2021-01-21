package com.zzw.plugin.video.lifecle

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class LifecycleOnCreateMethodVisitor(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM9,mv),Opcodes {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        println("LifecycleOnCreateMethodVisitor visitMethodInsn")
        println("opcode=${opcode}  owner=${owner}  name=${name}  descriptor=${descriptor}  isInterface=${isInterface}}")
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }


    override fun visitParameter(name: String?, access: Int) {
        println("LifecycleOnCreateMethodVisitor visitParameter")
        println("name=${name}  access=${access}}")

        super.visitParameter(name, access)
    }

    override fun visitCode() {
        println("LifecycleOnCreateMethodVisitor visitCode")
        //方法前面插入
//        mv.visitLdcInsn("zzz");
//        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
//        mv.visitInsn(Opcodes.DUP);
//        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
//        mv.visitVarInsn(Opcodes.ALOAD, 0);
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
//        mv.visitLdcInsn("-->onCreate");
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
//        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
//        mv.visitInsn(Opcodes.POP);


        mv.visitLdcInsn("zzz");
        mv.visitLdcInsn("-->onCreate");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);

        super.visitCode()
    }
}