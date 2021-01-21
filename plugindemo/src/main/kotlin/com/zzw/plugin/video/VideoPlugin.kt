package com.zzw.plugin.video

import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class VideoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw  GradleException("ASM Plugin, Android Application plugin required")
        }

        project.extensions.findByType(AppExtension::class.java)?.apply {
            registerTransform(VideoTransform())
        }
    }
}