package com.sallie.launcher

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class VisualStateWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        // Access ViewModel and trigger autoUpdateVisualState
        SallieViewModel.instance?.autoUpdateVisualState()
        return Result.success()
    }
}
