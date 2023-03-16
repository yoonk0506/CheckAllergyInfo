package com.yoon.allergyinfo.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedAllergyData() : ViewModel() {
    var sortIndex = 0
    var dataList = ArrayList<AllergyData>()
    private val _allergyData = MutableStateFlow(getAllData(sortIndex))
    val allergy: StateFlow<List<AllergyData>> = _allergyData.asStateFlow()

    private fun getAllData(index: Int): List<AllergyData> {
        when (index) {
            0 -> {
                dataList.sortWith(compareByDescending<AllergyData> { it.level }.thenBy { it.classification }.thenBy { it.name })
                return dataList
            }
            1 -> {
                dataList.sortWith(compareBy<AllergyData> { it.level }.thenBy { it.classification }.thenBy { it.name })
                return dataList
            }
        }
        return emptyList()
    }

    fun getSearchData(searchName: String) {
        if (searchName.isNotEmpty())
            _allergyData.update { getAllData(sortIndex).filter { item -> item.name.contains(searchName) } }
        else
            updateData()
    }

    fun updateData() {
        _allergyData.update { getAllData(sortIndex) }
    }
}