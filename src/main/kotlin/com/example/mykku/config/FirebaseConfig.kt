package com.example.mykku.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.FileInputStream

@Configuration
@Profile("!test & !local")
class FirebaseConfig(
    @Value("\${firebase.service-account-key-path}")
    private val serviceAccountKeyPath: String
) {

    @PostConstruct
    fun initialize() {
        if (FirebaseApp.getApps().isNotEmpty()) {
            return
        }
        if (serviceAccountKeyPath.isBlank()) {
            return
        }

        val inputStream = if (serviceAccountKeyPath.startsWith("classpath:")) {
            val path = serviceAccountKeyPath.removePrefix("classpath:")
            this::class.java.classLoader.getResourceAsStream(path)
                ?: throw IllegalStateException("Firebase service account key not found: $serviceAccountKeyPath")
        } else {
            FileInputStream(serviceAccountKeyPath)
        }

        inputStream.use { serviceAccount ->
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)
        }
    }
}
