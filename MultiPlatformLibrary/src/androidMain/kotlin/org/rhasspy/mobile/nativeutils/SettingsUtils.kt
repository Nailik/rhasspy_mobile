package org.rhasspy.mobile.nativeutils

import android.app.Activity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.Setting
import org.rhasspy.mobile.viewModels.GlobalData

actual object SettingsUtils {

    actual fun saveSettingsFile() {
        Application.Instance.currentActivity?.createDocument("rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.xml") {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    Application.Instance.contentResolver.openOutputStream(uri)?.also { outputStream ->

                        outputStream.write(GlobalData.getAsJson().toString().encodeToByteArray())

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

                    Application.Instance.contentResolver.openInputStream(uri)?.also { inputStream ->
                        val list = Json.decodeFromStream<List<Setting<Any>>>(inputStream)
                        inputStream.close()

                        ServiceInterface.reloadSettingsFromData(list)
                    }
                }
            }
        }

    }

}