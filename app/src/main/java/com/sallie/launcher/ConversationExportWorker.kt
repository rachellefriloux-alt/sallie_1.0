package com.sallie.launcher

import android.app.Application
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class ConversationExportWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        return try {
            val app = applicationContext as Application
            val vm = SallieViewModel(app)
            val csv = vm.exportConversationCsv()
            val file = File(applicationContext.cacheDir, "conversation_auto_export.csv")
            file.writeText(csv)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
