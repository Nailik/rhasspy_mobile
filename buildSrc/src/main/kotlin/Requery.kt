import de.fayard.refreshVersions.core.DependencyGroup

object Requery : DependencyGroup(group = "com.github.requery") {

    val sqliteAndroid = module("sqlite-android")

}