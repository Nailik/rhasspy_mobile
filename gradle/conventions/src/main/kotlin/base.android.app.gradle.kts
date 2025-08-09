import com.android.build.api.dsl.ApplicationExtension
import org.rhasspy.mobile.configureAndroidCommon
import org.rhasspy.mobile.configureKotlinCommon

apply(plugin = "com.android.application")
configureAndroidCommon<ApplicationExtension>()
configureKotlinCommon()