# BioPrep Resource Guide: Production PDF Management

This guide documents the resource structure, dummy PDF locations, and step-by-step instructions for replacing sample documents with real production PDFs.

---

## 1. Directory Structure

All bundled educational downloadable resources are stored in the application assets under:
```
app/src/main/assets/resources/
├── books/
│   └── sample_biology_book.pdf       # Sample Biology Textbooks / Reference Manual
├── syllabus/
│   └── hsc_biology_syllabus.pdf       # NCTB HSC Biology Official Curriculum Syllabus
└── test_papers/
    ├── hsc_2025_test_paper.pdf        # HSC 2025 Model Test Paper & Solution
    └── hsc_2024_test_paper.pdf        # HSC 2024 Board Questions & Solution
```

---

## 2. Resource Mapping & Metadata

The application registers and manages these items through `com.example.bioprep.resources.ResourceCatalog` and Room Database entity `ResourceEntity`:

| Resource ID | Resource Title | Category | Current Asset Path | Target Production File |
| :--- | :--- | :--- | :--- | :--- |
| `res_book_sample` | **Sample Biology Books** | Reference Books | `resources/books/sample_biology_book.pdf` | Gazi Azmal / Abul Hasan HSC Biology Reference Book |
| `res_syllabus_hsc` | **Biology Syllabus** | Official Syllabus | `resources/syllabus/hsc_biology_syllabus.pdf` | NCTB HSC Biology Syllabus (1st & 2nd Paper) |
| `res_test_2025` | **2025 Test Papers** | Board Questions | `resources/test_papers/hsc_2025_test_paper.pdf` | Complete HSC 2025 Board Model Test Papers & CQ/MCQ Solutions |
| `res_test_2024` | **2024 Test Papers** | Board Questions | `resources/test_papers/hsc_2024_test_paper.pdf` | Complete HSC 2024 All Board Biology Questions & Marking Schemes |

---

## 3. How to Replace Dummy PDFs with Real Production PDFs

### Direct Drop-in Replacement (Zero Code Changes)
If you keep the exact filenames and locations, **no code updates are needed**:
1. Copy your real PDF file over the corresponding file in `app/src/main/assets/resources/...`.
   * For textbooks: replace `app/src/main/assets/resources/books/sample_biology_book.pdf`.
   * For syllabus: replace `app/src/main/assets/resources/syllabus/hsc_biology_syllabus.pdf`.
   * For 2025 test papers: replace `app/src/main/assets/resources/test_papers/hsc_2025_test_paper.pdf`.
   * For 2024 test papers: replace `app/src/main/assets/resources/test_papers/hsc_2024_test_paper.pdf`.
2. Clean and rebuild the APK / app:
   ```bash
   ./gradlew clean assembleDebug
   ```

### Using Different Filenames or Adding New Resources
If you choose to use new filenames or expand the library with additional books:
1. Place the new PDF inside the appropriate subdirectory under `app/src/main/assets/resources/`.
2. Open `com.example.bioprep.resources.ResourceCatalog.java`.
3. Update the `assetPath` string for that resource ID, or register a new resource item:
   ```java
   new ResourceItem(
       "res_book_gazi_botany",
       "Gazi Azmal Botany 1st Paper",
       "Reference Books",
       "resources/books/gazi_azmal_botany_1st.pdf",
       "3.4 MB"
   )
   ```
4. Rebuild the app.

---

## 4. PDF Reader Verification
The built-in PDF Reader (`PdfReaderActivity`) uses Android's native `android.graphics.pdf.PdfRenderer`:
- Validates the PDF file descriptor.
- Renders pages dynamically and asynchronously.
- Caches rendered page bitmaps for vertical scrolling and zoom performance.
- Supports arbitrary page counts without loading the entire document into heap memory.

