package com.haizo.poc.ui.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haizo.generaladapter.model.ListItem
import com.haizo.poc.data.model.ModelType1
import com.haizo.poc.data.model.ModelType2
import com.haizo.poc.data.model.ModelType3
import com.haizo.poc.data.model.StoryListModel
import com.haizo.poc.utils.sampleBackgrounds

class MainViewModel : ViewModel() {

    private val _items = MutableLiveData<MutableList<ListItem>>()

    val items: LiveData<MutableList<ListItem>> = _items

    init {
        _items.value = getDummyItems()
    }

    private fun getDummyItems(): MutableList<ListItem>? {
        return mutableListOf(
            getDummyHorizontalItem(),
            ModelType1(text = "1 Looking for random paragraphs, you\\'ve come to the right place. When a random word or a random sentence isn\\'t quite enough, the next logical step is"),
            ModelType2(image = sampleBackgrounds.random()),
            ModelType3(text = "Hello 1", image = sampleBackgrounds.random()),
            ModelType3(text = "Hello 2", image = sampleBackgrounds.random()),
            ModelType3(text = "Hello 3", image = sampleBackgrounds.random()),
            getDummyHorizontalItem(),
            ModelType1(text = "2 Looking for random paragraphs, you\\'ve come to the right place. When a random word or a random sentence isn\\'t quite enough, the next logical step is"),
            ModelType2(image = sampleBackgrounds.random()),
            ModelType3(text = "Hello 4", image = sampleBackgrounds.random()),
            ModelType3(text = "Hello 5", image = sampleBackgrounds.random()),
            ModelType3(text = "Hello 6", image = sampleBackgrounds.random())

        )
    }

    private fun getDummyHorizontalItem(): StoryListModel {
        return StoryListModel().apply {
            list?.let { for (i in 0..10) it.add(ModelType2(image = sampleBackgrounds.random())) }
        }
    }
}