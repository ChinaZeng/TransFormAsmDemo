package com.zzw.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class FirstTransform extends Transform {
    private Project project

    public FirstTransform(Project project) {
        println("FirstTransform")
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("transform")
        println("Collection<TransformInput>  transformInvocation.getInputs():")
        transformInvocation.getInputs().each { TransformInput input ->
            println(" ${input.toString()}")
            input.jarInputs.each { JarInput jarInput ->
                //传递给下一个
                File dest = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes,
                        jarInput.scopes, Format.JAR)

                FileUtils.copyFile(jarInput.file, dest)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                //传递给下一个
                File dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        println("Collection<TransformInput>  transformInvocation.getReferencedInputs():")
        transformInvocation.getReferencedInputs().each { TransformInput input ->
            println(" ${input.toString()}")
            input.jarInputs.each { JarInput jarInput ->
                //传递给下一个
                File dest =  transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes,
                        jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                //传递给下一个
                File dest =   transformInvocation.outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        println("Collection<SecondaryInput>  transformInvocation.getSecondaryInputs():")
        transformInvocation.getSecondaryInputs().each { SecondaryInput secondaryInput ->
            println(secondaryInput.toString())
        }

        println("TransformOutputProvider transformInvocation.getOutputProvider():")
        println(transformInvocation.outputProvider.toString())


    }


    @Override
    String getName() {
        println("getName")
        return FirstTransform.simpleName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        println("getInputTypes")
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        println("getScopes")
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        println("isIncremental")
        return false
    }


}