The English version is below.

# Vault Guard - 您的密碼管理器

Vault Guard 為免費且開放原始碼的`Android`應用程式，提供使用者密碼管理的功能。

## 簡介

本專案為`Android`應用程式，程式提供使用者產生、儲存、搜尋、備份密碼的功能，同時也提供產生TOTP驗證碼的功能，使用者可以將其作為密碼器使用，或是單純用於兩步驟驗證的驗證器使用。

我們重視使用者的資料安全及隱私，所有資料皆加密儲存於使用者的手機，為了避免意外發生，程式也提供備份功能，不過匯出的檔案不會經過加密，需妥善保存，建議將檔案「離線保存」並多備份，例如: 乾淨的隨身碟、手機內的安全資料夾。

⚠️ 請注意，由於資料儲存於使用者的手機，請使用者務必牢記主密碼，忘記主密碼將導致無法存取密碼庫，需將程式資料刪除或解除安裝後重新安裝，這將導致過去儲存的密碼被刪除，如果擔心忘記主密碼，可使用`匯出密碼庫`的功能，這樣可透過`匯入密碼庫`的功能還原過去儲存的密碼。

## Demo

由於程式目前處於測試階段(預估近期可上架正式版)，請先 [加入測試](https://forms.gle/FRXiX5q3aiSbkp296) 後再從`Google Play`下載程式，或是您也可以從 [Release](https://gitlab.keke125.com/keke125/VaultGuard/-/releases) 找到檔名為`VaultGuard-1.x.x-android`的`APK`檔並安裝，我們建議您安裝最新版本。 

## 技術

### 程式語言、專案管理、資料庫
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?&style=for-the-badge&logo=kotlin&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white) ![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)

### 版本控制
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

### 支援的作業系統
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

## 授權條款
[![Licence](https://img.shields.io/github/license/keke125/pixel-art-filter-web?style=for-the-badge)](LICENSE)

## 官方網站
https://vaultguard.keke125.com/

## 程式截圖

## 主要特色
- 資料安全: 此程式將安全地儲存您的密碼，我們透過256位元的AES(CBC模式)演算法加密資料庫，加密資料庫的金鑰也受到額外的密碼保護，密碼為隨機產生的字串，並使用由安卓系統提供的硬體支援的金鑰庫(Hardware-backed Keystore)產生的金鑰加密儲存密碼，最後存放於您的手機。
- 隱私: 我們尊重您的隱私，您的密碼只會經加密後儲存於您的手機，您可以檢視程式所需的權限，我們不會向您請求任何不必要的權限，只有特定功能需要權限，例如生物辨識解鎖密碼庫需要生物辨識權限及掃瞄QR Code(TOTP驗證碼)需要相機權限。
- 備份: 當您想要更換手機時，可使用我們提供的匯出功能，您可將匯出的檔案重新匯入到您的新手機，由於密碼匯出後並不會經過加密，請您妥善保管匯出的檔案。
- 匯入: 程式支援從Google密碼管理器匯入的功能，您可先於Google密碼管理器匯出密碼，並將密碼匯入到我們的程式。
- 開源: 程式以MIT授權條款開放原始碼，您可以於 https://github.com/keke125/VaultGuard 查看完整的程式碼，並請依照授權條款的規範合理使用程式及程式碼。
- 免費: 使用程式並不需要任何費用，我們也不會加入廣告，請您放心使用。
- 帳號授權: 設定主密碼、鎖定密碼庫(登出)及解鎖密碼庫(登入)功能，如果您的裝置支援生物辨識，您也可以使用生物辨識登入。為了您的安全，程式在您登入後超過1小時會將您登出。
- TOTP驗證碼: 您可將TOTP驗證碼一起存入密碼庫，幫助您進行多因子驗證。
- 搜尋密碼: 您可透過名稱、帳號、網址搜尋密碼。
- 相容性: 我們支援Android 7.0及以上的版本。
- 資料夾: 您可根據您的需求將密碼放入不同的資料夾，方便您分類管理密碼，您也可在資料夾內新增、檢視、更新、刪除及搜尋密碼，也可將資料夾及底下的所有密碼一起刪除。
- 深色主題: 程式原生支援深色模式，您可根據您的需求在淺色及深色模式之間切換。
- 快速設定: 您可在系統選單頁面中新增圖塊(tile)，即可快速開啟程式。
- 清空密碼庫: 刪除所有密碼及資料夾。
- 支援語言: 程式支援繁體中文及英文。

## 加入測試
https://forms.gle/FRXiX5q3aiSbkp296

感謝您使用我們的程式!

# Vault Guard - Your Password Manager

## Official Website
https://vaultguard.keke125.com/

## Feature
- Data Security: This app securely stores your passwords using 256-bit AES (CBC mode) encryption. The database encryption key is also protected by an additional password. The password is a randomly generated string, and the encryption key is stored using a hardware-backed keystore provided by the Android system. This ensures that your passwords are safely stored on your device.
- Privacy: We respect your privacy. Your passwords are only stored on your device in an encrypted form. You can review the permissions required by the app. We do not request any unnecessary permissions. Specific functions may require permissions, such as biometric recognition for unlocking the app and camera access for scanning QR Codes (TOTP verification codes).
- Backup: If you want to switch devices, you can use our export feature. The exported file can be imported to your new device. Since the exported passwords are not encrypted, please keep the exported file secure.
- Import: The app supports importing from Google Password Manager. You can export your passwords from Google Password Manager and then import them into our app.
- Open Source: The app is open-sourced under the MIT license. You can view the complete source code at https://github.com/keke125/VaultGuard and use the app and code according to the license terms.
- Free: Using the app is free of charge, and we will not include any ads. You can use it with confidence.
- Account Authorization: You can set a main password, lock the vault repository (log out), and unlock the vault repository (log in). If your device supports biometric recognition, you can also log in using biometric authentication. For your security, the app will log you out after 1 hour since the last login .
- TOTP Verification Codes: You can store TOTP verification codes to help with multi-factor authentication.
- Search Passwords: You can search for passwords by name, account, or URL.
- Compatibility: We support Android 7.0 and above.
- Folders: You can organize your passwords into different folders for easier management. You can add, view, update, delete, and search for passwords within folders. You can also delete folders along with all the passwords within them.
- Dark Theme: The app natively supports dark mode. You can switch between light and dark modes according to your preference.
- Quick Settings: You can add a tile in the system menu for quick access to the app.
- Clear Password Vault: Delete all passwords and folders.
- Supported Languages: The app supports Traditional Chinese and English.

## Join the test
https://forms.gle/FRXiX5q3aiSbkp296

Thank you for using our app!
