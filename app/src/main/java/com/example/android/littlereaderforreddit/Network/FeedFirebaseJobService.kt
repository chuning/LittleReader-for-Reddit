package com.example.android.littlereaderforreddit.Network

import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class FeedFirebaseJobService: JobService() {
    var asyncTask: AsyncTask<Void, Void, Void>? = null

    override fun onStopJob(job: JobParameters?): Boolean {
        asyncTask?.cancel(true)
        return true
    }

    override fun onStartJob(job: JobParameters): Boolean {
        asyncTask = object: AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                FeedSyncTask.syncFeed(null, this@FeedFirebaseJobService, true)
                jobFinished(job, false)
                return null
            }

            override fun onPostExecute(result: Void?) {
                jobFinished(job, false)
            }
        }
        asyncTask!!.execute();
        return true
    }
}