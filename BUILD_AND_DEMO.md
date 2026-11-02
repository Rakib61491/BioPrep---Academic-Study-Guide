# BioPrep — Build and Demonstration Guide

**Project:** BioPrep — HSC Biology Study Companion (Advanced Programming Lab Edition)  
**Platform:** Android (Java + XML Views)  
**Package:** `com.example.bioprep`  
**Lab Assignment:** CSE 2200 — Advanced Programming Lab  

---

## 1. Environment & Requirements

- **Android Studio:** Android Studio Ladybug (2024.2.1+) or newer
- **JDK / JBR:** JDK 17 or JDK 21+ (Compatible with Android Studio JBR: `C:\Program Files\Android\Android Studio\jbr`)
- **Android SDK:**
  - `compileSdk`: 37
  - `targetSdk`: 37
  - `minSdk`: 24 (Android 7.0 Nougat or higher)
- **Gradle Version:** Gradle 9.6 (configured in `gradle-wrapper.properties`)
- **Android Gradle Plugin (AGP):** 8.13.0
- **External Dependencies:**
  - `androidx.appcompat:appcompat`
  - `androidx.constraintlayout:constraintlayout`
  - `androidx.recyclerview:recyclerview`
  - `com.google.android.material:material`
  - `testImplementation org.json:json:20240303` (for JVM unit tests)
- **Firebase Status:** **DEFERRED / NOT INCLUDED**. Zero Firebase dependencies, Google services plugins, or JSON configurations are present.

---

## 2. Opening and Building the Project

### In Android Studio
1. Launch Android Studio.
2. Select **File > Open** and select the repository root directory:
   `BioPrepAcademicStudyGuide`
3. Allow Gradle to sync dependencies.
4. Select a build target (e.g., `app`).

### From PowerShell / Terminal
Ensure `JAVA_HOME` points to Android Studio's bundled JBR:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug
```

Run unit tests:
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat testDebugUnitTest
```

The compiled APK will be located at:
`app/build/intermediates/apk/debug/app-debug.apk`

---

## 3. Running on Emulator or Physical Device

1. Start an Android Virtual Device (AVD) running API 24 or newer (API 33+ recommended for testing Android 13 runtime notification permission).
2. Install the APK via ADB or Android Studio:
   ```powershell
   adb install -r app/build/intermediates/apk/debug/app-debug.apk
   ```
3. Tap the **BioPrep** icon from the launcher to start `WelcomeActivity`.

---

## 4. Screen-by-Screen Demonstration Script

| Screen | Layout / Key Architecture | Demonstrated Features |
|---|---|---|
| **1. Welcome Screen** (`WelcomeActivity`) | `RelativeLayout` | • App branding & description<br>• Explicit `Intent` navigation to `HomeActivity`<br>• Activity lifecycle logging with tag `BIOPREP_LIFECYCLE` |
| **2. Home Dashboard** (`HomeActivity`) | `ConstraintLayout` | • Biology 1st Paper card (Botany)<br>• Biology 2nd Paper card (Zoology)<br>• Online Science Challenge navigation<br>• Passes paper identifier extras (`BIOLOGY_1`, `BIOLOGY_2`) |
| **3. Chapter List** (`ChapterActivity`) | `ListView` + `ChapterAdapter` | • Displays chapter titles and numbers using custom `BaseAdapter`<br>• Empty state message handling<br>• Clicking a chapter passes paper and chapter IDs to `QuizActivity` |
| **4. Quiz Screen** (`QuizActivity`) | `ConstraintLayout` + `RecyclerView` + `StudyTimerService` | • MCQ cards displayed in `RecyclerView` using `QuestionAdapter`<br>• RadioGroup answer selection mapped to stable question IDs (immune to view recycling bugs)<br>• Previous / Next card scrolling<br>• Unanswered question confirmation dialog before submission<br>• Bound Service countdown timer with Start, Pause, Resume, and Stop<br>• Monotonic time (`SystemClock.elapsedRealtime`) prevents timer drift<br>• Dynamically registered, non-exported `TimerUpdateReceiver`<br>• Runtime `POST_NOTIFICATIONS` permission prompt on API 33+<br>• Notification posted via `NotificationHelper` upon countdown completion<br>• State preservation (`onSaveInstanceState`) across screen rotations |
| **5. Results Screen** (`ResultActivity`) | `ConstraintLayout` | • Summary card displaying score percentage and badge<br>• Breakdown of Total Questions, Correct, Incorrect, and Unanswered counts<br>• Single-top Back to Home navigation |
| **6. Online Science Challenge** (`OnlineQuizActivity`) | `ScrollView` + `HttpRequestManager` + `RemoteQuestionParser` | • Asynchronous HTTP GET using `HttpURLConnection` on background `ExecutorService`<br>• Real-time loading indicator and error handling (offline, timeout, API code)<br>• JSON parsing of Open Trivia Database questions<br>• HTML entity decoding via `HtmlCompat`<br>• Answer option randomization (shuffling) with dynamic correct-index tracking<br>• Instant in-app answer checking with visual feedback<br>• Retry button upon failure |

---

## 5. Architectural Component Directory

For review and grading, the key components are organized as follows:

| Component | Class Path | Responsibility |
|---|---|---|
| **ListView Adapter** | [`ChapterAdapter`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/adapters/ChapterAdapter.java) | Custom `BaseAdapter` with row recycling and ViewHolder pattern for chapter listing. |
| **RecyclerView Adapter** | [`QuestionAdapter`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/adapters/QuestionAdapter.java) | `RecyclerView.Adapter` with recycling protection (clearing check listener before state binding) and selection mapped by question ID. |
| **Scoring Logic** | [`QuizScorer`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/utils/QuizScorer.java) | Pure Java scoring engine separating UI from score calculation. |
| **Bound Service** | [`StudyTimerService`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/services/StudyTimerService.java) | Bound Service implementing study countdown with monotonic deadline calculations and internal broadcast dispatches. |
| **BroadcastReceiver** | [`TimerUpdateReceiver`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/receivers/TimerUpdateReceiver.java) | Non-exported broadcast receiver registered in `QuizActivity.onStart()` and unregistered in `QuizActivity.onStop()`. |
| **Notification Helper** | [`NotificationHelper`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/utils/NotificationHelper.java) | Manages `NotificationChannel` (API 26+) and posts `NotificationCompat` with permission checks on API 33+. |
| **HTTP Manager** | [`HttpRequestManager`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/network/HttpRequestManager.java) | Manages `HttpURLConnection` on an `ExecutorService` background thread with main-thread callback posting. |
| **JSON Parser** | [`RemoteQuestionParser`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/network/RemoteQuestionParser.java) | Validates and parses OpenTDB JSON responses, decodes HTML entities, and shuffles options. |
| **Sample Data** | [`SampleBiologyData`](file:///c:/Users/User/Documents/2-2/Lab/CSE%202200_Advanced%20Programming/Lab%20Project/BioPrepAcademicStudyGuide/app/src/main/java/com/example/bioprep/data/SampleBiologyData.java) | Original offline sample question bank for Botany and Zoology. |

---

## 6. Remote HTTP Endpoint & Disclaimer

- **Configured Endpoint:**  
  `https://opentdb.com/api.php?amount=1&category=17&type=multiple`  
  (Defined in `res/values/strings.xml` as `opentdb_endpoint`)
- **Source Label:** `OpenTDB Science & Nature`
- **Content Disclaimer:**  
  *The Open Trivia Database Category 17 represents general Science & Nature knowledge for demonstrating Android HTTP GET and JSON parsing. It does not represent official Bangladesh HSC Board examination questions.*

---

## 7. Known Architectural Behaviors & Limitations

1. **Activity-Scoped Timer Behavior:**  
   The study timer is designed to accompany active quiz-taking. When the user navigates away from `QuizActivity` (triggering `onStop()`), the timer automatically pauses and stores the remaining duration. When returning, the timer resumes from the saved duration. It does not run as an unkillable background or foreground service.
2. **Notification Graceful Degradation:**  
   If the user denies `POST_NOTIFICATIONS` permission on Android 13+ (API 33+), the app gracefully logs a warning, skips the system notification, and continues to provide in-app countdown updates and completion toast messages without crashing.
3. **Local Question Scope:**  
   All local question data is bundled within `SampleBiologyData.java` for offline demonstration without requiring a database server.

