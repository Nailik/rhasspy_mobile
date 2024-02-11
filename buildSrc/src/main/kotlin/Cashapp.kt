import de.fayard.refreshVersions.core.DependencyGroup

object Cashapp : DependencyGroup(group = "app.cash") {

    object Sqldelight : DependencyGroup(group = "$group.sqldelight") {
        val android = module("android-driver")
        val ios = module("native-driver")
        val coroutines = module("coroutines-extensions")
        val androidTest = module("sqlite-driver")
        val paging = module("androidx-paging3-extensions")
    }

    object Paging : DependencyGroup(group = "$group.paging") {
        val runtime = module("paging-compose-common")
    }

}