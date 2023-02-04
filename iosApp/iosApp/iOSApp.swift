import SwiftUI
import UIKit
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        //FirebaseApp.configure() TODO disable acording to settings
        let application = IosApplication()
        window = UIWindow(frame: UIScreen.main.bounds)
        let mainViewController = application.MainViewController()
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        return true
    }
}
