package projeto.tinywins

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings

class TinyWinsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configuração do Firestore
        val firestore = FirebaseFirestore.getInstance()
        val settings = firestoreSettings {
            // Habilita a persistência em disco para o modo offline
            setLocalCacheSettings(persistentCacheSettings {})
        }
        firestore.firestoreSettings = settings
    }
}