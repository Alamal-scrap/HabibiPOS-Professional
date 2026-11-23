# Habibi POS Professional — Independent Android Preview

This is a **new, independent** offline-first Android POS prototype. It does not reuse any previous POS source code, and it includes no prior sales data.

## Included in the ZIP

- Landscape touchscreen sales screen, compact product cards, text-only order panel and fast Cash / PayNow confirmation
- Genuine Android launcher icon using the supplied Habibi Dessert emblem
- Cash-register shifts with opening and counted cash; cash expenses and separate cash drops
- Purchases and expense entry. A purchase paid from the cash drawer automatically creates one linked cash expense; the monthly report counts it only once
- Order history and daily sales summaries within monthly reports
- Real multi-sheet `.xlsx` export (daily summary, purchases, drawer movements), with Android's **Choose where to save** dialog
- JSON backup export and restore, product editing and local image upload, order-level fixed discounts
- GitHub Actions build workflow for a debug APK

**Important:** This is an initial testing build, not a completed or certified POS. It has not been tested on your Samsung tablet yet. PayNow is recorded only when you confirm receipt; no bank verification or live QR has been connected. Product names/prices are initial editable examples — confirm your real menu before use. The sales and reports are simple management accounting estimates, not formal profit/tax calculations. No user accounts, multi-device sync, refund controls, secure PINs, or encryption are included yet. Records are stored on this device using WebView local storage and may be erased if app data is cleared, so regularly export JSON backups to a safe off-device location.

## Upload

1. In GitHub Desktop, open the **HabibiPOS-Professional** local repository folder via **Repository → Show in Explorer**.
2. Extract this ZIP, open its extracted folder, select **all files and folders inside**, and paste them directly into the repository folder. Include the hidden `.github` directory. Replace the empty GitHub README if prompted. **Do not move or replace `.git`.**
3. Return to GitHub Desktop, enter `Initial Habibi POS Professional prototype` as Summary, and click **Commit to main**, then **Push origin**.
4. In the repo, go to **Actions → Build Android APK**. When it finishes successfully, download **HabibiPOS-Professional-APK** from **Artifacts** and extract `app-debug.apk`.
5. Install on the Samsung tablet for a **test** run. Verify sales persistence, drawer reconciliation, monthly `.xlsx` export in Excel, backup restore, and PayNow manual confirmation before real business use.

## Technical

Android Gradle Plugin 8.5.2, Gradle 8.7, Java 17, Android SDK 35. GitHub Actions installs Gradle and SDK, and requires no local Android Studio or Gradle wrapper. The launcher icon comes from the image supplied in the conversation. The app HTML and JavaScript are bundled locally for offline use; the only Android bridge provides native file save/import selection.
