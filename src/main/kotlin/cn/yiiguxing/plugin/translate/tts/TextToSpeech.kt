package cn.yiiguxing.plugin.translate.tts

import cn.yiiguxing.plugin.translate.trans.Lang
import cn.yiiguxing.plugin.translate.util.checkDispatchThread
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

/**
 * Text to speech.
 */
class TextToSpeech private constructor() {

    private var currentPlayer: TTSPlayer? = null

    /**
     * Text to speech.
     *
     * @param project the project.
     * @param text the text.
     * @param lang the language.
     */
    fun speak(project: Project?, text: String, lang: Lang): Disposable {
        checkThread()
        currentPlayer?.stop()

        return GoogleTTSPlayer(project, text, lang) { player ->
            if (player === currentPlayer) {
                currentPlayer = null
            }
        }.run {
            currentPlayer = this
            start()

            disposable
        }
    }

    /**
     * Returns `true` if the [language][lang] is supported.
     */
    fun isSupportLanguage(lang: Lang): Boolean = GoogleTTSPlayer.SUPPORTED_LANGUAGES.contains(lang)

    @Suppress("unused")
    fun stop() {
        checkThread()
        currentPlayer?.stop()
    }

    companion object {
        val instance: TextToSpeech
            get() = ServiceManager.getService(TextToSpeech::class.java)

        private fun checkThread() = checkDispatchThread(TextToSpeech::class.java)
    }
}