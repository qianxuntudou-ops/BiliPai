package com.android.purebilibili.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.math.abs

class HomeHeroCarouselPolicyTest {

    @Test
    fun `carousel only shows on recommend page with items when enabled`() {
        assertTrue(
            shouldShowHomeHeroCarousel(
                enabled = true,
                category = HomeCategory.RECOMMEND,
                itemCount = 1
            )
        )
        assertFalse(
            shouldShowHomeHeroCarousel(
                enabled = false,
                category = HomeCategory.RECOMMEND,
                itemCount = 1
            )
        )
        assertFalse(
            shouldShowHomeHeroCarousel(
                enabled = true,
                category = HomeCategory.POPULAR,
                itemCount = 1
            )
        )
        assertFalse(
            shouldShowHomeHeroCarousel(
                enabled = true,
                category = HomeCategory.RECOMMEND,
                itemCount = 0
            )
        )
    }

    @Test
    fun `carousel uses bounded leading feed items`() {
        assertEquals(
            listOf(1, 2, 3),
            selectHomeHeroCarouselItems(listOf(1, 2, 3), maxItems = 8)
        )
        assertEquals(
            (1..8).toList(),
            selectHomeHeroCarouselItems((1..20).toList(), maxItems = 8)
        )
        assertEquals(
            emptyList(),
            selectHomeHeroCarouselItems((1..20).toList(), maxItems = 0)
        )
    }

    @Test
    fun `carousel feed removes visible hero items from regular grid`() {
        val items = listOf("a", "b", "c", "d")
        val carouselItems = listOf("a", "b")

        assertEquals(
            listOf("c", "d"),
            excludeHomeHeroCarouselItems(items, carouselItems) { it }
        )
    }

    @Test
    fun `carousel feed keeps regular grid untouched when carousel is empty`() {
        val items = listOf("a", "b", "c")

        assertEquals(
            items,
            excludeHomeHeroCarouselItems(items, emptyList()) { it }
        )
    }

    @Test
    fun `carousel uses no resting side peek so centered cover hides neighbors`() {
        assertEquals(0f, HOME_HERO_CAROUSEL_SIDE_PEEK_DP)
    }

    @Test
    fun `carousel transform rotates outgoing card away from drag direction`() {
        val centered = resolveHomeHeroCarouselCardTransform(0f)
        assertTrue(abs(centered.rotationY) < 0.001f)
        assertTrue(abs(centered.scale - 1f) < 0.001f)
        assertTrue(abs(centered.alpha - 1f) < 0.001f)
        assertTrue(abs(centered.translationXFraction) < 0.001f)
        assertTrue(abs(centered.pivotFractionX - 0.5f) < 0.001f)
        assertTrue(abs(centered.contentParallaxFraction) < 0.001f)
        assertTrue(abs(centered.contentScale - 1f) < 0.001f)
        assertTrue(abs(centered.edgeShadeAlpha) < 0.001f)
        assertTrue(abs(centered.shadowElevationFraction) < 0.001f)
        assertTrue(abs(centered.rotationZ) < 0.001f)
        assertTrue(centered.zIndex >= 1f)

        val left = resolveHomeHeroCarouselCardTransform(-1f)
        val right = resolveHomeHeroCarouselCardTransform(1f)
        assertTrue(left.rotationY > 0f)
        assertTrue(right.rotationY < 0f)
        assertTrue(abs(left.translationXFraction) < 0.001f)
        assertTrue(abs(right.translationXFraction) < 0.001f)
        assertTrue(abs(left.rotationZ) < 0.001f)
        assertTrue(abs(right.rotationZ) < 0.001f)
        assertEquals(0f, left.pivotFractionX)
        assertEquals(1f, right.pivotFractionX)
        assertEquals(left.scale, right.scale)
        assertEquals(left.alpha, right.alpha)
        assertTrue(left.scale < centered.scale)
        assertTrue(left.alpha < centered.alpha)
        assertTrue(left.contentParallaxFraction > 0f)
        assertTrue(right.contentParallaxFraction < 0f)
        assertEquals(left.contentScale, right.contentScale)
        assertTrue(left.contentScale > centered.contentScale)
        assertTrue(left.edgeShadeAlpha > 0.2f)
        assertEquals(left.edgeShadeAlpha, right.edgeShadeAlpha)
        assertTrue(left.edgeShadeStartFromLeft)
        assertFalse(right.edgeShadeStartFromLeft)
        assertTrue(left.shadowElevationFraction > centered.shadowElevationFraction)

        val draggingLeft = resolveHomeHeroCarouselCardTransform(-0.5f)
        val draggingRight = resolveHomeHeroCarouselCardTransform(0.5f)
        assertTrue(draggingLeft.translationXFraction < 0f)
        assertTrue(draggingRight.translationXFraction > 0f)
        assertTrue(draggingLeft.rotationZ < 0f)
        assertTrue(draggingRight.rotationZ > 0f)
        assertTrue(abs(draggingLeft.rotationY) < abs(left.rotationY))
        assertTrue(abs(draggingLeft.rotationY) > abs(centered.rotationY))
        assertTrue(draggingLeft.zIndex > centered.zIndex)
        assertTrue(draggingRight.zIndex > centered.zIndex)
    }

    @Test
    fun `carousel preview stays hidden until first frame is rendered`() {
        assertEquals(0f, resolveHomeHeroCarouselPreviewAlpha(hasRenderedFirstFrame = false))
        assertEquals(1f, resolveHomeHeroCarouselPreviewAlpha(hasRenderedFirstFrame = true))
    }
}
