import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        // Full-bleed like Android: backgrounds run under the bars, and screens inset their
        // content with safeDrawingPadding(). Compose handles the keyboard itself.
        ComposeView()
                .ignoresSafeArea()
                // Every screen is dark, so ask for light status-bar icons.
                .preferredColorScheme(.dark)
    }
}



