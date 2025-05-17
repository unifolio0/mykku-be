package com.example.mykku

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MykkuApplication

fun main(args: Array<String>) {
    runApplication<MykkuApplication>(*args)
}
