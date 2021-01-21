package com.zzw.plugin.lifecle

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class LifecycleClassVisitor extends ClassVisitor {

    private String mClassName

    public LifecycleClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        println("LifecycleClassVisitor : visit -----> started ：" + name)
        this.mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
//        version=51  access=33  name=androidx/fragment/app/FragmentActivity  signature=null  superName=androidx/core/app/ComponentActivity  interfaces=[androidx/lifecycle/ViewModelStoreOwner, androidx/core/app/ActivityCompat$OnRequestPermissionsResultCallback, androidx/core/app/ActivityCompat$RequestPermissionsRequestCodeValidator]
        println("version=${version}  access=${access}  name=${name}  signature=${signature}  superName=${superName}  interfaces=${interfaces.toString()}")
    }

    @Override
    void visitEnd() {
        println("LifecycleClassVisitor : visit -----> end")
        super.visitEnd()
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //name=onCreate  desc=(Landroid/os/Bundle;)V  signature=null  exceptions=null
        println("access=${access}  name=${name}  desc=${desc}  signature=${signature}  exceptions=${exceptions.toString()}")
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        if ("onCreate" == name) {
            //处理onCreate
            return new LifecycleOnCreateMethodVisitor(mv)
        } else if ("onDestroy" == name) {
            //处理onDestroy
            return new LifecycleOnDestroyMethodVisitor(mv)
        }
        return mv
    }

}


