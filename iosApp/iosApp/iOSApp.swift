import SwiftUI
import UIKit
import shared
import Firebase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
       // FirebaseApp.configure()
       // IosKt.setupKermit()
        //TODO start application (call application on created before showing ui, and show splash screen while loading)
        window = UIWindow(frame: UIScreen.main.bounds)
        let mainViewController = IosKt.MainViewController()
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        return true
    }
}
