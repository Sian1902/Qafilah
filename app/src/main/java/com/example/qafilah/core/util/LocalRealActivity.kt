package com.example.qafilah.core.util

import android.app.Activity
import androidx.compose.runtime.staticCompositionLocalOf


val LocalRealActivity = staticCompositionLocalOf<Activity> {
    error("LocalRealActivity not provided — make sure MainActivity supplies it via CompositionLocalProvider before any locale/context wrapping.")
}