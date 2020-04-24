package pt.mobilesword.silentupdate.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.*

internal object ContextCenter {
	private lateinit var applicationContext: WeakReference<Context>
	private val activityStack = Stack<WeakReference<Activity?>>()

	internal fun getTopActivity(): Activity? {
		var targetActivity: Activity? = null
		try {
			targetActivity = activityStack.peek().get()
		} catch (e: Exception) {
			//do nothing
		}
		return targetActivity
	}

	internal fun getAppContext() = applicationContext.get()!!

	internal fun init(context: Application) {
		applicationContext = WeakReference(context.applicationContext)
		//Register activity
		activityStack.clear()
		context.registerActivityLifecycleCallbacks(object : ActivityLifeListener() {

			override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
				activityStack.add(WeakReference(activity))
			}

			override fun onActivityDestroyed(activity: Activity?) {
				activityStack.remove(WeakReference(activity))
			}
		})
	}
}
