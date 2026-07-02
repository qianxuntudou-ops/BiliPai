package com.android.purebilibili.core.ui.transition.native

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class NativeVideoCardTransitionControllerStructureTest {

    @Test
    fun openTransitionAnimatesHomeBackgroundBeforeCommittingNavigation() {
        val source = loadSource()
        val startOpenBlock = source
            .substringAfter("fun startOpen(")
            .substringBefore("fun startClose(")
        val validSourceBlock = startOpenBlock.substringAfter("if (isRunning) return")

        val animateIndex = validSourceBlock.indexOf("animate(")
        val navigateIndex = validSourceBlock.indexOf("navigateAction()")

        assertTrue(animateIndex >= 0)
        assertTrue(navigateIndex > animateIndex)
        assertTrue(validSourceBlock.contains("onEnd = {"))
    }

    private fun loadSource(): String {
        return listOf(
            File("app/src/main/java/com/android/purebilibili/core/ui/transition/native/NativeVideoCardTransitionController.kt"),
            File("src/main/java/com/android/purebilibili/core/ui/transition/native/NativeVideoCardTransitionController.kt")
        ).first { it.exists() }.readText()
    }
}
