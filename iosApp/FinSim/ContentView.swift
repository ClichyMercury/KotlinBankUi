import SwiftUI
import UIKit
import shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "APIBaseURL") as? String
            ?? "http://localhost:8080"
        #if DEBUG
        let isDebug = true
        #else
        let isDebug = false
        #endif
        return MainViewControllerKt.MainViewController(baseUrl: baseUrl, debug: isDebug)
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
