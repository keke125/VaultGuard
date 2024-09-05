The English version is below.

# Vault Guard - 您的密碼管理器

**Vault Guard** 為免費且開放原始碼的`Android`應用程式，提供使用者密碼管理的功能。

## 簡介

本專案為`Android`應用程式，程式提供使用者產生、儲存、搜尋、備份密碼的功能，同時也提供產生TOTP驗證碼的功能，使用者可以將其作為密碼器使用，或是單純用於兩步驟驗證的驗證器使用。

我們重視使用者的資料安全及隱私，所有資料皆加密儲存於使用者的手機，為了避免意外發生，程式也提供備份功能，不過匯出的檔案不會經過加密，需妥善保存，建議將檔案「離線保存」並多備份，例如: 乾淨的隨身碟、手機內的安全資料夾。

⚠️ 請注意，由於資料儲存於使用者的手機，請使用者務必牢記主密碼，忘記主密碼將導致無法存取密碼庫，需將程式資料刪除或解除安裝後重新安裝，這將導致過去儲存的密碼被刪除，如果擔心忘記主密碼，可使用`匯出密碼庫`的功能，這樣可透過`匯入密碼庫`的功能還原過去儲存的密碼。

## Demo

由於程式目前處於測試階段(預估近期可上架正式版)，請先 [加入測試](https://forms.gle/FRXiX5q3aiSbkp296) 後再從`Google Play`下載程式，或是您也可以從 [Release](https://github.com/keke125/VaultGuard/releases) 找到檔名為`VaultGuard-1.x.x-android`的`APK`檔並安裝，我們建議您安裝最新版本。 

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

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_212754.png" alt=""><figcaption><p>新增密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_213046.png" alt=""><figcaption><p>編輯密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_213214.png" alt=""><figcaption><p>檢視密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_213508.png" alt=""><figcaption><p>搜尋密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_213615.png" alt=""><figcaption><p>檢視資料夾內的密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_213720.png" alt=""><figcaption><p>編輯及刪除資料夾</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_213828.png" alt=""><figcaption><p>密碼產生器</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_214012.png" alt=""><figcaption><p>匯入密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_214141.png" alt=""><figcaption><p>匯出密碼</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_214252.png" alt=""><figcaption><p>如裝置未設定生物辨識，將引導使用者註冊生物辨識</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_214537.png" alt=""><figcaption><p>使用生物辨識解鎖密碼庫</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_214733.png" alt=""><figcaption><p>可新增圖塊，以便快速開啟密碼庫</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240610_214933.png" alt=""><figcaption><p>掃描QR Code，以便取得TOTP驗證碼</p></figcaption></figure>

## 主要特色
- 資料安全: 您的資料將加密並只會儲存於您的手機，他人（包含開發者）皆無法透過網路存取您的密碼庫，請放心使用。

- 隱私: 我們不會收集關於您的任何資訊，程式並沒有存取網路的權限，另外，只有在您需要使用特定功能時，我們才會跟您索取相關權限，例如生物辨識解鎖密碼庫需要生物辨識權限、掃瞄QR Code(TOTP驗證碼)需要相機權限。

- 備份及還原（匯出及匯入密碼庫）: 當您想要更換手機或是定期備份時，可使用「匯出密碼庫」功能，之後，您可使用「匯入密碼庫」功能將匯出的檔案重新匯入到您的新手機，由於密碼匯出後並不會經過加密，請您妥善保管匯出的檔案。

- 從Google密碼管理器匯入密碼: 程式支援從Google密碼管理器匯入的功能，您可先於Google密碼管理器匯出密碼，並將密碼匯入到我們的程式。

- 開源: 程式以MIT授權條款開放原始碼，您可以於 https://github.com/keke125/VaultGuard 查看完整的程式碼。

- 免費: 使用程式並不需要任何費用，我們也不會加入廣告。

- 鎖定密碼庫: 為了您的安全，程式將在您登入後超過1小時，自動鎖定密碼庫，請輸入主密碼解鎖，您也可啟用生物辨識功能來解鎖。

- 多因子（兩步驟）驗證: 您可將TOTP驗證碼存入密碼庫，幫助您進行多因子（兩步驟）驗證。

- 相容性: 我們支援Android 7.0及以上的版本。

- 資料夾: 您可將密碼放入不同的資料夾管理，您也可在資料夾內新增、檢視、更新、刪除及搜尋密碼，也可將資料夾及底下的密碼一起刪除。

- 快速設定: 您可在系統選單頁面中新增圖塊(tile)，即可快速開啟程式。

- 支援語言: 程式支援繁體中文及英文。

感謝您使用我們的程式!

## Q & A

- 忘記主密碼怎麼辦?  
由於加密金鑰及資料皆儲存於您的手機，我們無法幫您重設主密碼，您需將程式資料刪除或解除安裝後重新安裝，但這將導致過去儲存的密碼被刪除，如果您擔心忘記主密碼，您可定期使用`匯出密碼庫`的功能來備份密碼庫，這樣可透過`匯入密碼庫`的功能來還原之前儲存的密碼。


## 加入測試
https://forms.gle/FRXiX5q3aiSbkp296

# Vault Guard - Your Password Manager

**Vault Guard** is a free and open-source Android application that provides users with password management features.

## Introduction

This project is an Android application that provides users with functions to generate, save, search, and back up passwords. It also offers a feature to generate TOTP (Time-Based One-Time Password) codes, allowing users to use the app either as a password manager or solely as an authenticator for two-step verification.

We value the security and privacy of user data; therefore, all data is encrypted and stored on the user's device. To prevent data loss due to unexpected incidents, the app also provides a backup function. However, please note that the exported files are not encrypted, so they must be stored securely. We recommend "offline storage" and multiple backups, such as on a clean USB drive or a secure folder within the device.

⚠️ Please note that since data is stored on the user's device, it is crucial for users to remember their main password. Forgetting the main password will result in the inability to access the vault, requiring users to delete the app data or uninstall and reinstall the app, which will cause all previously saved passwords to be deleted. If you are worried about forgetting your main password, you can use the `Export Vault` feature, which allows you to restore previously saved passwords through the `Import Vault` feature.

## Demo
Since the app is currently in the testing phase (with an estimated release of the official version soon), please [join the testing program](https://forms.gle/FRXiX5q3aiSbkp296) first and then download the app from Google Play. Alternatively, you can find the `APK` file named `VaultGuard-1.x.x-android` in the [Release](https://github.com/keke125/VaultGuard/releases) and install it. We recommend installing the latest version.

## Technology

### Programming Language, Project Management, Database
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?&style=for-the-badge&logo=kotlin&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white) ![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)

### Version Control
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

### Supported Operating System
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

## License
[![Licence](https://img.shields.io/github/license/keke125/pixel-art-filter-web?style=for-the-badge)](LICENSE)

## Official Website
https://vaultguard.keke125.com/

## App Screenshots

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240902_214112.png" alt=""><figcaption><p>Add Password</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_160508.png" alt=""><figcaption><p>Edit Password</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_160557.png" alt=""><figcaption><p>View Password Details</p></figcaption></figure>

<figure><img src=".gitbook/assets/Screenshot_20240903_160740.png" alt=""><figcaption><p>Search Password</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_161750.png" alt=""><figcaption><p>View Passwords in the Folder</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_161927.png" alt=""><figcaption><p>Edit and Remove Folder</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_162115.png" alt=""><figcaption><p>Password Generator</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_162243.png" alt=""><figcaption><p>Import Vault</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_162345.png" alt=""><figcaption><p>Export Vault</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_162506.png" alt=""><figcaption><p>If the device does not have biometric authentication set up, the user will be prompted to register for biometric authentication.</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_162732.png" alt=""><figcaption><p>Unlock the Vault Using Biometric Authentication.</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_163132.png" alt=""><figcaption><p>You can add a tile for quick access to the vault.</p></figcaption></figure>

<figure><img src="https://gitlab.keke125.com/keke125/vault-guard-website/-/raw/main/.gitbook/assets/Screenshot_20240903_163313.png" alt=""><figcaption><p>Scan a QR code to obtain a TOTP verification code.</p></figcaption></figure>

## Feature
- Data Security: Your data will be encrypted and stored only on your device; others (including the developers) will not be able to access your vault via the internet. Please use it with confidence.

- Privacy: We do not collect any information about you. The app does not have network access permissions. Additionally, we only request permissions when you need to use specific features, such as biometric authentication for unlocking the vault or camera access for scanning QR codes (TOTP codes).

- Backup and Restore (Export and Import Vault): When you want to switch phones or back up regularly, you can use the "Export Vault" feature. Later, you can use the "Import Vault" feature to import the exported file to your new device. Since the exported file is not encrypted, please handle it with care.

- Import Passwords from Google Password Manager: The app supports importing passwords from Google Password Manager. You can first export your passwords from Google Password Manager and then import them into our app.

- Open Source: The app is open source under the MIT License. You can view the full source code at https://github.com/keke125/VaultGuard.

- Free: Using the app is free of charge, and we do not include ads.

- Locking the Vault: For your safety, the app will automatically lock the vault after you log in for more than 1 hour. Please enter your main password to unlock it. You can also enable biometric authentication to unlock it.

- Multi-Factor (Two-Step) Authentication: You can store TOTP verification codes in the vault to assist with multi-factor (two-step) authentication.

- Compatibility: We support Android 7.0 and above.

- Folders: You can manage passwords in different folders. You can also add, view, update, delete, and search for passwords within folders, as well as delete folders and their contained passwords.

- Quick Setup: You can add a tile in the system menu page to quickly open the app.

- Supported Languages: The app supports Traditional Chinese and English.

Thank you for using our app!

## Q & A

- What to do if you forget your main password?  
  Since the encryption key and data are stored on your device, we cannot help you reset your main password. You will need to delete the app data or uninstall and reinstall the app, but this will result in the deletion of all previously saved passwords. If you are worried about forgetting your main password, you can regularly use the `Export Vault` feature to back up your vault. This way, you can restore your previously saved passwords using the `Import Vault` feature.

## Join the test
https://forms.gle/FRXiX5q3aiSbkp296
