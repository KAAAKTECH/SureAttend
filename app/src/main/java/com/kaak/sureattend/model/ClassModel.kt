package com.kaak.sureattend.model

import android.util.Log
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

    fun deleteClasses(classIDs: Set<String>, onComplete: (Boolean) -> Unit) {
        val batch = db.batch()
        for (id in classIDs) {
            if (id.isNotBlank()) {
                batch.delete(classesCollection.document(id))
            }
        }

        batch.commit()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun listenToClasses(onUpdate: (List<Class>) -> Unit) {
        listener?.remove()
        listener = classesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firestore", "Listen failed.", error)
                return@addSnapshotListener
            }

            val classList = snapshot?.documents?.mapNotNull { doc ->
                try {
                    val classObj = doc.toObject(Class::class.java)
                    classObj?.copy(classID = doc.id)
                } catch (e: Exception) {
                    Log.e("MappingError", "Invalid doc: ${doc.id}", e)
                    null
                }
            } ?: emptyList()

            onUpdate(classList)
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
