/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Used to communicate between screens.
 */
class MainViewModel : ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    //----------Below code added for OpenAI integration-------------
    var uiState = ConversationUiState(
        initialMessages = listOf(Message("bot", "Welcome to JetchatGPT!", "8:07 pm")),
        channelName = "#jetchatgpt",
        channelMembers = 2
    )
    private var openAIWrapper = OpenAIWrapper()

    fun onMessageSent(content: String) {
        // add user message to chat history
        addMessage("me", content)

        // fetch openai response and add to chat history
        viewModelScope.launch {
            addMessage("bot", openAIWrapper.chat(content))
        }
    }

    private fun addMessage(author: String, content: String) {
        // calculate message timestamp
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeNow = dateFormat.format(currentTime)

        val message = Message(
            author = author,
            content = content,
            timestamp = timeNow
        )

        uiState.addMessage(message)
    }
}
