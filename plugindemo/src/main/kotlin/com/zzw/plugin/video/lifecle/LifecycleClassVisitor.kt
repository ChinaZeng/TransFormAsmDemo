package com.zzw.plugin.video.lifecle

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class LifecycleClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("visit")
        println("version=${version}  access=${access}  name=${name}  signature=${signature}  superName=${superName}  interfaces=${interfaces.toString()}")
    }

    override fun visitEnd() {
        super.visitEnd()
        println("visitEnd")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("visitMethod")
        println("access=${access}  name=${name}  desc=${desc}  signature=${signature}  exceptions=${exceptions.toString()}")
        val mv = super.visitMethod(access, name, desc, signature, exceptions)
        if ("onCreate" == name) {
            //处理onCreate
            return LifecycleOnCreateMethodVisitor(mv)
        } else if ("onDestroy" == name) {
            //处理onDestroy
            return LifecycleOnDestroyMethodVisitor(mv)
        }
        return mv
    }
}