package com.example.snakegame

class CancellationToken {
    var isCancelled = false
    fun cancel() {
        isCancelled = true
    }
}