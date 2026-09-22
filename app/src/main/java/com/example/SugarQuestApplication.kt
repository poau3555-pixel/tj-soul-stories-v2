package com.example

import android.app.Application
import android.system.Os

class SugarQuestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        configureEnvironment()
    }

    companion object {
        init {
            configureEnvironment()
        }

        fun configureEnvironment() {
            try {
                Os.setenv("MESA_NO_ERROR", "1", true)
                Os.setenv("MESA_LOG_FILE", "/dev/null", true)
                Os.setenv("LIBGL_DEBUG", "quiet", true)
                Os.unsetenv("MESA_LOADER_DRIVER_OVERRIDE")
                Os.unsetenv("DRI_PRIME")
                Os.unsetenv("LIBGL_ALWAYS_SOFTWARE")
            } catch (_: Throwable) {}
        }
    }
}

