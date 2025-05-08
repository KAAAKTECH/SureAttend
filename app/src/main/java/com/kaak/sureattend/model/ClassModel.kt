package com.kaak.sureattend.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kaak.sureattend.dataclass.Class

class ClassModel {

    private val db = FirebaseFirestore.getInstance()
    private val classesCollection = db.collection("Classes")
    private var listener: ListenerRegistration? = null

    fun createClass(className: String, onComplete: (Boolean) -> Unit) {
        val classID = generateRandomClassID()
        val classData = hashMapOf(
            "className" to className,
            "host" to "HOST123",
            "clients" to emptyList<String>()
        )

        classesCollection.document(classID).set(classData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun listenToClasses(onUpdate: (List<Class>) -> Unit) {
        listener?.remove() // Remove old listener
        listener = classesCollection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && !snapshot.isEmpty) {
                val classList = snapshot.documents.mapNotNull { it.toObject(Class::class.java) }
                onUpdate(classList)
            }
        }
    }

    fun stopListening() {
        listener?.remove()
    }

    private fun generateRandomClassID(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { charset.random() }.joinToString("")
    }
}
