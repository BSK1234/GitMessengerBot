package com.sungbin.gitkakaobot

import android.app.Application
import android.content.Context
import com.sungbin.androidutils.util.NotificationUtil
import com.sungbin.gitkakaobot.bot.Bot
import com.sungbin.gitkakaobot.bot.rhino.ApiClass
import com.sungbin.gitkakaobot.bot.v8.Api
import com.sungbin.gitkakaobot.bot.v8.UI
import com.sungbin.gitkakaobot.util.BotUtil
import dagger.hilt.android.HiltAndroidApp


/**
 * Created by SungBin on 2020-08-23.
 */

@HiltAndroidApp
class GitMessengerBot : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        Bot.init(applicationContext)
        ApiClass.init(applicationContext)
        BotUtil.init(applicationContext)
        UI.init(applicationContext)
        Api.init(applicationContext)

        NotificationUtil.createChannel(
            applicationContext,
            getString(R.string.app_name),
            getString(R.string.foregroundservice_running_bot)
        )
    }

}