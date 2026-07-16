package com.android.purebilibili.feature.video.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoDomainViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `engagement keeps transient state for same generation and resets for next video`() {
        val viewModel = VideoEngagementViewModel()
        val first = subject("BV1", generation = 1L)
        viewModel.bindSubject(first, VideoEngagementSeed(isLiked = false))
        viewModel.setCoinDialogVisible(true)

        viewModel.bindSubject(first, VideoEngagementSeed(isLiked = true))
        assertTrue(viewModel.uiState.value.coinDialogVisible)
        assertTrue(viewModel.uiState.value.isLiked)

        viewModel.bindSubject(subject("BV2", generation = 2L), VideoEngagementSeed())
        assertFalse(viewModel.uiState.value.coinDialogVisible)
        assertFalse(viewModel.uiState.value.isLiked)
    }

    @Test
    fun `composer drops drafts when subject generation changes`() {
        val viewModel = VideoComposerViewModel()
        val first = subject("BV1", generation = 1L)
        viewModel.bindSubject(first)
        viewModel.updateCommentDraft("draft")
        viewModel.bindSubject(first)
        assertEquals("draft", viewModel.uiState.value.commentDraft)

        viewModel.bindSubject(subject("BV2", generation = 2L))
        assertEquals("", viewModel.uiState.value.commentDraft)
    }

    @Test
    fun `supplement updates payload without replacing subject generation`() {
        val viewModel = VideoSupplementViewModel()
        val subject = subject("BV1", generation = 1L)
        viewModel.bindSubject(subject, VideoSupplementSeed(onlineCount = "1"))
        viewModel.bindSubject(subject, VideoSupplementSeed(onlineCount = "2"))

        assertEquals(subject, viewModel.uiState.value.subject)
        assertEquals("2", viewModel.uiState.value.onlineCount)
    }

    @Test
    fun `supplement discards deferred result after generation changes`() = runTest(dispatcher) {
        val viewModel = VideoSupplementViewModel(
            loader = VideoSupplementLoader { snapshot ->
                VideoSupplementSeed(onlineCount = snapshot.bvid)
            },
            startDelayMs = 100L
        )
        viewModel.bindSubject(subject("BV1", generation = 1L), VideoSupplementSeed())
        viewModel.bindSubject(subject("BV2", generation = 2L), VideoSupplementSeed())

        advanceTimeBy(100L)
        runCurrent()

        assertEquals("BV2", viewModel.uiState.value.onlineCount)
        assertEquals(2L, viewModel.uiState.value.subject?.generation)
    }

    @Test
    fun `supplement cancels deferred task while page is invisible`() = runTest(dispatcher) {
        var loads = 0
        val viewModel = VideoSupplementViewModel(
            loader = VideoSupplementLoader {
                loads += 1
                VideoSupplementSeed(onlineCount = "loaded")
            },
            startDelayMs = 100L
        )
        viewModel.bindSubject(subject("BV1", generation = 1L), VideoSupplementSeed())
        viewModel.setVisible(false)

        advanceTimeBy(100L)
        runCurrent()

        assertEquals(0, loads)
        assertEquals("", viewModel.uiState.value.onlineCount)
    }

    @Test
    fun `composer buffered event survives a temporary collector stop`() = runTest(dispatcher) {
        val viewModel = VideoComposerViewModel()

        viewModel.notifyCommentSent()

        assertEquals(VideoComposerEvent.CommentSent, viewModel.events.first())
    }

    private fun subject(bvid: String, generation: Long) = VideoSubjectSnapshot(
        bvid = bvid,
        cid = generation,
        aid = generation,
        ownerMid = 1L,
        title = bvid,
        coverUrl = "",
        durationMs = 1_000L,
        generation = generation
    )
}
