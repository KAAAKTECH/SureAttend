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

    fun startListeningToClasses() {
        classModel.listenToClasses { updatedList ->
            _classList.postValue(updatedList)
        }
    }

    fun createClass(className: String, onResult: (Boolean) -> Unit) {
        classModel.createClass(className, onResult)
    }

    override fun onCleared() {
        super.onCleared()
        classModel.stopListening()
    }
}
