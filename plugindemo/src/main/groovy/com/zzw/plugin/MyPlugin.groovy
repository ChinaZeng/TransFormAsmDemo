package com.zzw.plugin

import com.zzw.plugin.lifecle.LifeTransform
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class MyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("========================")
        System.out.println("hello gradle plugin!")
        System.out.println("========================")

        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('ASM Plugin, Android Application plugin required')
        }

        def android = project.extensions.findByType(AppExtension.class)

//        android.registerTransform(new FirstTransform(project))
        android.registerTransform(new LifeTransform(project))
    }
}