package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.services.ServiceAction
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.AppSettings
import java.io.File


actual object SettingsUtils {

    actual fun saveSettingsFile() {

        Application.Instance.currentActivity?.createDocument("rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.xml") {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    Application.Instance.contentResolver.openOutputStream(uri)?.also { outputStream ->
                        outputStream.write(
                            File(
                                Application.Instance.filesDir.parent,
                                "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
                            ).readBytes()
                        )
                        outputStream.flush()
                        outputStream.close()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    actual fun restoreSettingsFromFile() {
        Application.Instance.currentActivity?.openDocument(arrayOf("text/xml")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->


                    CoroutineScope(Dispatchers.Default).launch {
                        ServiceInterface.serviceAction(ServiceAction.Stop)
                    }

                    Application.Instance.contentResolver.openInputStream(uri)?.also { inputStream ->
                        val outputStream = File(
                            Application.Instance.filesDir.parent,
                            "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
                        ).outputStream()
                        inputStream.copyTo(outputStream)

                        outputStream.flush()

                        outputStream.close()
                        inputStream.close()

                        Application.Instance.restart()
                    }
                }
            }
        }
    }

    actual fun selectSoundFile(callback: (String) -> Unit) {
        Application.Instance.currentActivity?.openDocument(arrayOf("audio/x-wav")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    var fileName = ""

                    Application.Instance.contentResolver.query(uri, null, null, null, null)?.also { cursor ->
                        if (cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (index != -1) {
                                fileName = cursor.getString(index)
                            } else {
                                //didn't work
                                return@openDocument
                            }
                        } else {
                            //didn't work
                            return@openDocument
                        }
                        cursor.close()
                    }


                    Application.Instance.contentResolver.openInputStream(uri)?.let { inputStream ->
                        File(Application.Instance.filesDir.parent, "files/$fileName").apply {
                            this.outputStream().apply {
                                inputStream.copyTo(this)

                                this.flush()

                                this.close()
                                inputStream.close()
                            }
                        }
                        callback(fileName)
                    } ?: kotlin.run {
                        //didn't work
                    }

                }
            } else {
                //didn't work
            }
        }
    }

}