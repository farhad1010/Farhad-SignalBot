package com.farhad.signalbot.domain

import com.farhad.signalbot.core.model.SignalOutcome
import com.farhad.signalbot.core.model.SignalRecord

data class PerformanceStats(
    val total: Int,
    val wins: Int,
    val losses: Int,
    val pending: Int,
    val cancelled: Int
) {
    val winRate: Double
        get() {
            val completed = wins + losses

            return if (completed == 0) {
                0.0
            } else {
                wins.toDouble() / completed * 100.0
            }
        }

    companion object {

        fun from(
            records: List<SignalRecord>
        ): PerformanceStats {

            return PerformanceStats(
                total = records.size,
                wins = records.count {
                    it.outcome == SignalOutcome.WIN
                },
                losses = records.count {
                    it.outcome == SignalOutcome.LOSS
                },
                pending = records.count {
                    it.outcome == SignalOutcome.PENDING
                },
                cancelled = records.count {
                    it.outcome == SignalOutcome.CANCELLED
                }
            )
        }
    }
}
