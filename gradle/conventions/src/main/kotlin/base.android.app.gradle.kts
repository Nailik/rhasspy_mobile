import com.android.build.api.dsl.ApplicationExtension
import org.rhasspy.mobile.configureAndroidCommon

apply(plugin = "com.android.application")
configureAndroidCommon<ApplicationExtension>()