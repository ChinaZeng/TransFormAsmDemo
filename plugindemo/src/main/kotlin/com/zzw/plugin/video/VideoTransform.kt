package com.zzw.plugin.video

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.zzw.plugin.video.lifecle.LifecycleClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class VideoTransform : Transform() {

    init {

    }

    override fun getName(): String = "video"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        transformInvocation?.inputs?.forEach { input ->
            input.jarInputs?.forEach { jarInput ->
                handleJarInput(jarInput, transformInvocation.outputProvider)
            }

            input.directoryInputs?.forEach { directoryInput ->
                handleDirectoryInput(directoryInput, transformInvocation.outputProvider)
            }
        }
    }

    private fun handleJarInput(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider
    ) {

        val jarInputFilePath = jarInput.file.absolutePath
        println("jarInputFilePath=${jarInputFilePath}")
        if (!jarInputFilePath.endsWith(".jar")) {
            println("handleJarInputs   end  ")
            return
        }

        //重名名输出文件,因为可能同名,会覆盖
        var jarInputName = jarInput.name
        println("jarInputName= $jarInputName")

        val tmpFile = File("${jarInput.file.parent}${File.separator}classes_temp.jar")
        println("tmpFile=${tmpFile}")
        //避免上次的缓存被重复插入
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        val jarFile = JarFile(jarInput.file)
        val entryEnumeration = jarFile.entries()
        val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))
        //用于保存
        while (entryEnumeration.hasMoreElements()) {
            val jarEntry = entryEnumeration.nextElement()
            val entryName = jarEntry.name
            println("entryName=${entryName}")
            val zipEntry = ZipEntry(entryName)
            val inputStream = jarFile.getInputStream(jarEntry)
            if (entryName == "androidx/fragment/app/FragmentActivity.class") {
                //class文件处理
                println("----------- deal with jar class file <$entryName> -----------")
                val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                val classVisitor = LifecycleClassVisitor(classWriter)
                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                val code = classWriter.toByteArray()

                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(code)
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
            inputStream.close()
        }
        jarOutputStream.close()
        jarFile.close()

        val md5Name = DigestUtils.md5Hex(jarInputFilePath)
        if (jarInputName.endsWith(".jar")) {
            jarInputName = jarInputName.substring(0, jarInputName.length - 4)
        }
        //处理完输入文件之后，要把输出给下一个任务
        val name = jarInputName + md5Name
        println("name=${name}")
        val dest = outputProvider.getContentLocation(
            name,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        println("dest=${dest.path}")
        FileUtils.copyFile(tmpFile, dest)
        tmpFile.delete()
    }

    private fun handleDirectoryInput(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {

        println("inputdir:${directoryInput.file}")
        if (directoryInput.file.isDirectory) {
            directoryInput.file.all {
                println("file:${it.name}")
                if (it.name == "MainActivity.class") {
                    val classReader = ClassReader(it.readBytes())
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val classVisitor = LifecycleClassVisitor(classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    val writeFile = it.parentFile.absolutePath + File.separator + it.name
                    println("writeFile=${writeFile}")
                    val fos = FileOutputStream(writeFile)
                    fos.write(code)
                    fos.close()
                }
            }
        }
        //传递给下一个
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        println("out:${dest.path}")
        FileUtils.copyDirectory(directoryInput.file, dest)
    }


    private fun File.all(callback: (File) -> Unit) {
        val fs = listFiles() ?: return
        for (f in fs) {
            if (f.isDirectory) //若是目录，则递归打印该目录下的文件
                f.all(callback)
            if (f.isFile) //若是文件，直接触发回调
                callback(f)
        }

    }
}