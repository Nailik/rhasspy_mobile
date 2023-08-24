import de.fayard.refreshVersions.core.DependencyGroup

object CashApp {

    object Sqldelight : DependencyGroup(group = "app.cash.sqldelight") {
        val android = module("android-driver")
        val ios = module("native-driver")
        val coroutines = module("coroutines-extensions")
        val androidTest = module("sqlite-driver")
        val paging = module("androidx-paging3-extensions")
    }

    object Paging : DependencyGroup(group = "app.cash.paging") {
        val runtime = module("paging-compose-common")
    }

}