package com.zzw.plugin.lifecle

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class LifeTransform extends Transform {
    private Project project


    public LifeTransform(Project project) {
        println("LifeTransform")
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("transform")

        println '--------------- LifecyclePlugin visit start --------------- '
        def startTime = System.currentTimeMillis()
        Collection<TransformInput> inputs = transformInvocation.inputs
        println(inputs.toString())
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //删除之前的输出
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }
        //遍历inputs
        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //处理directoryInputs
                println("handleDirectoryInput   start  ")
                handleDirectoryInput(directoryInput, outputProvider)
                println("handleDirectoryInput   end  ")
            }

            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                //处理jarInputs
                println("handleJarInputs   start  ")
                handleJarInputs(jarInput, outputProvider)
                println("handleJarInputs   end  ")
            }
        }

        def cost = (System.currentTimeMillis() - startTime) / 1000
        println '--------------- LifecyclePlugin visit end --------------- '
        println "LifecyclePlugin cost ： $cost s"

    }


    private static void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {

        //如果是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                println("dPath=${file.path}")
                if (name == "FragmentActivity.class") {
                    println '----------- deal with "class" file <' + name + '> -----------'
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor classVisitor = new LifecycleClassVisitor(classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    def writeFile = file.parentFile.absolutePath + File.separator + name
                    println("writeFile=${writeFile}")
                    FileOutputStream fos = new FileOutputStream(writeFile)
                    fos.write(code)
                    fos.close()
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        println("dest=${dest.path}")
        FileUtils.copyDirectory(directoryInput.file, dest)

    }

    private static void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider) {

        def jarInputFilePath = jarInput.file.getAbsolutePath()

        println("jarInputFilePath=${jarInputFilePath}")
        if (!jarInputFilePath.endsWith(".jar")) {
            println("handleJarInputs   end  ")
            return
        }

        //重名名输出文件,因为可能同名,会覆盖
        def jarInputName = jarInput.name
        println("jarInputName=${jarInputName}")
        def md5Name = DigestUtils.md5Hex(jarInputFilePath)
        println("jarInputFilePathMd5=${md5Name}")
        if (jarInputName.endsWith(".jar")) {
            jarInputName = jarInputName.substring(0, jarInputName.length() - 4)
        }


        File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
        //避免上次的缓存被重复插入
        if (tmpFile.exists()) {
            tmpFile.delete()
        }

        JarFile jarFile = new JarFile(jarInput.file)
        Enumeration<JarEntry> entryEnumeration = jarFile.entries()

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
        //用于保存
        while (entryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = entryEnumeration.nextElement()
            String entryName = jarEntry.getName()
            println("entryName=${entryName}")

            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            if (entryName == "androidx/fragment/app/FragmentActivity.class") {
                //class文件处理
                println '----------- deal with "jar" class file <' + entryName + '> -----------'
                jarOutputStream.putNextEntry(zipEntry)
                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor classVisitor = new LifecycleClassVisitor(classWriter)
                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                byte[] code = classWriter.toByteArray()
                jarOutputStream.write(code)
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()

        }
        jarOutputStream.close()
        jarFile.close()
        //处理完输入文件之后，要把输出给下一个任务
        def name = jarInputName + md5Name
        File dest = outputProvider.getContentLocation(name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)
        println("dest=${dest.path}")
        FileUtils.copyFile(tmpFile, dest)
        tmpFile.delete()
    }

    @Override
    String getName() {
        println("getName")
        return LifeTransform.simpleName
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