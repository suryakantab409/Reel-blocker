package com.example.reelblocker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class ReelBlockerService : AccessibilityService() {

    companion object {
        private const val PREFS_NAME = "reel_blocker_prefs"
        private const val KEY_SWIPE_COUNT = "swipe_count"
        private const val KEY_UNBLOCK_TIME = "unblock_timestamp"

        private const val MAX_SWIPES = 30
        private const val COOLDOWN_MILLIS = 60 * 60 * 1000L // 1 ghanta
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        var unblockTime = prefs.getLong(KEY_UNBLOCK_TIME, 0L)
        var swipeCount = prefs.getInt(KEY_SWIPE_COUNT, 0)

        // 1. Agar 1 ghanta pura ho gaya to counter reset karo
        if (unblockTime != 0L && now >= unblockTime) {
            prefs.edit().putLong(KEY_UNBLOCK_TIME, 0L).putInt(KEY_SWIPE_COUNT, 0).apply()
            unblockTime = 0L
            swipeCount = 0
            Toast.makeText(this, "1 ghanta pura! Reels wapas unblock ho gayi.", Toast.LENGTH_SHORT).show()
        }

        val pkg = event.packageName?.toString() ?: ""
        val isTargetApp = pkg.contains("instagram") || pkg.contains("youtube")

        // 2. Agar abhi 1 ghanta pura nahi hua aur user app khole to bahar phenko
        if (unblockTime != 0L && now < unblockTime && isTargetApp) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            val remainingMins = ((unblockTime - now) / (1000 * 60)) + 1
            Toast.makeText(this, "Blocked! $remainingMins minute baad wapas aana.", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Swipes count karo
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED && isTargetApp) {
            swipeCount++
            prefs.edit().putInt(KEY_SWIPE_COUNT, swipeCount).apply()

            Toast.makeText(this, "Reels count: $swipeCount / $MAX_SWIPES", Toast.LENGTH_SHORT).show()

            if (swipeCount >= MAX_SWIPES) {
                val newBlockTime = now + COOLDOWN_MILLIS
                prefs.edit().putLong(KEY_UNBLOCK_TIME, newBlockTime).apply()
                performGlobalAction(GLOBAL_ACTION_HOME)
                Toast.makeText(this, "30 Reels complete! 1 ghante ke liye block kar diya gaya.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onInterrupt() {}
}
