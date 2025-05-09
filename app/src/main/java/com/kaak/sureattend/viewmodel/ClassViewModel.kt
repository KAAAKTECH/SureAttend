package com.kaak.sureattend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaak.sureattend.dataclass.Class
import com.kaak.sureattend.model.ClassModel

class ClassViewModel : ViewModel() {

    private val classModel = ClassModel()
    private val _classList = MutableLiveData<List<Class>>()
    val classList: LiveData<List<Class>> get() = _classList

    private val _selectedClasses = MutableLiveData<Set<Class>>(emptySet())
    val selectedClasses: LiveData<Set<Class>> get() = _selectedClasses

    fun startListeningToClasses() {
        classModel.listenToClasses { updatedList ->
            _classList.postValue(updatedList)
        }
    }

    fun createClass(className: String, onResult: (Boolean) -> Unit) {
        classModel.createClass(className, onResult)
    }

    fun toggleSelection(classItem: Class) {
        val currentSet = _selectedClasses.value ?: emptySet()
        _selectedClasses.value = if (currentSet.contains(classItem)) {
            currentSet - classItem
        } else {
            currentSet + classItem
        }
    }

    fun clearSelection() {
        _selectedClasses.value = emptySet()
    }

    override fun onCleared() {
        super.onCleared()
        classModel.stopListening()
    }
}
