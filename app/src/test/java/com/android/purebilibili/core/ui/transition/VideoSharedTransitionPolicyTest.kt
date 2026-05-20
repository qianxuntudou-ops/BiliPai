package com.android.purebilibili.core.ui.transition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoSharedTransitionPolicyTest {

    @Test
    fun coverSharedTransition_enabled_whenTransitionAndScopesAreReady() {
        assertTrue(
            shouldEnableVideoCoverSharedTransition(
                transitionEnabled = true,
                hasSharedTransitionScope = true,
                hasAnimatedVisibilityScope = true
            )
        )
        assertFalse(
            shouldEnableVideoCoverSharedTransition(
                transitionEnabled = true,
                hasSharedTransitionScope = false,
                hasAnimatedVisibilityScope = true
            )
        )
    }

    @Test
    fun metadataSharedTransition_enabled_byDefault_forUnifiedVideoReturn() {
        assertEquals(VideoSharedTransitionProfile.COVER_AND_METADATA, resolveVideoSharedTransitionProfile())
        assertTrue(
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = false
            )
        )
    }

    @Test
    fun metadataSharedTransition_staysEnabled_evenWhenQuickReturnLimited() {
        assertTrue(
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = true
            )
        )
    }

    @Test
    fun metadataSharedTransition_disabledWhenCardContainerOwnsSharedBounds() {
        assertFalse(
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = false,
                useCardContainerSharedBounds = true
            )
        )
    }

    @Test
    fun homeVideoTransition_usesCoverAsPrimaryAnchor() {
        val policy = resolveVideoSharedTransitionOwnership(
            sourceRoute = "home",
            coverSharedEnabled = true,
            isQuickReturnLimited = false
        )

        assertTrue(policy.useCoverSharedBounds)
        assertFalse(policy.useMetadataSharedBounds)
    }

    @Test
    fun detailContentReveal_usesLightVisibleMotionForHomeSharedTransition() {
        val motion = resolveVideoDetailContentRevealMotion(
            sourceRoute = "home",
            transitionEnabled = true
        )

        assertTrue(motion.enabled)
        assertEquals(40, motion.delayMillis)
        assertEquals(220, motion.durationMillis)
        assertEquals(14, motion.slideOffsetDp)
        assertEquals(0.985f, motion.initialScale, 0.0001f)
    }

    @Test
    fun detailContentReveal_disabledWithoutSharedTransition() {
        val motion = resolveVideoDetailContentRevealMotion(
            sourceRoute = "home",
            transitionEnabled = false
        )

        assertFalse(motion.enabled)
        assertEquals(0, motion.delayMillis)
        assertEquals(0, motion.slideOffsetDp)
        assertEquals(1f, motion.initialScale, 0.0001f)
    }

    @Test
    fun sharedCoverAspectRatio_defaultsToHomeCardSixteenByTen() {
        assertEquals(1.6f, VIDEO_SHARED_COVER_ASPECT_RATIO, 0.0001f)
    }
}
