package com.example.bioprep.data;

import com.example.bioprep.models.Chapter;
import com.example.bioprep.models.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local offline repository of HSC Biology sample chapters, MCQs, Active Recall drills,
 * Flashcards, and Reference Resources.
 * All dummy items are clearly structured so real curriculum content can be added easily.
 */
public final class SampleBiologyData {

    public static final String PAPER_BIOLOGY_1 = "BIOLOGY_1"; // 1st Paper: Botany (উদ্ভিদবিজ্ঞান)
    public static final String PAPER_BIOLOGY_2 = "BIOLOGY_2"; // 2nd Paper: Zoology (প্রাণিবিজ্ঞান)

    private static final Map<String, List<Chapter>> CHAPTERS_BY_PAPER = new LinkedHashMap<>();
    private static final Map<String, List<Question>> QUESTIONS_BY_CHAPTER = new LinkedHashMap<>();

    // Data models for Active Recall, Flashcards, and Resources
    public static class ActiveRecallItem {
        public final String id;
        public final String chapterName;
        public final String promptEn;
        public final String promptBn;
        public final String answerEn;
        public final String answerBn;
        public final String keywords;

        public ActiveRecallItem(String id, String chapterName, String promptEn, String promptBn,
                                String answerEn, String answerBn, String keywords) {
            this.id = id;
            this.chapterName = chapterName;
            this.promptEn = promptEn;
            this.promptBn = promptBn;
            this.answerEn = answerEn;
            this.answerBn = answerBn;
            this.keywords = keywords;
        }
    }

    public static class FlashcardItem {
        public final String id;
        public final String chapterName;
        public final String questionEn;
        public final String questionBn;
        public final String answerEn;
        public final String answerBn;
        public final String medicalTip;

        public FlashcardItem(String id, String chapterName, String questionEn, String questionBn,
                             String answerEn, String answerBn, String medicalTip) {
            this.id = id;
            this.chapterName = chapterName;
            this.questionEn = questionEn;
            this.questionBn = questionBn;
            this.answerEn = answerEn;
            this.answerBn = answerBn;
            this.medicalTip = medicalTip;
        }
    }

    public static class ResourceItem {
        public final String id;
        public final String title;
        public final String category; // "NCTB", "Guide", "Syllabus", "PastPaper", "Video"
        public final String subtitle;
        public final String fileSize;
        public final String url;

        public ResourceItem(String id, String title, String category, String subtitle, String fileSize, String url) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.subtitle = subtitle;
            this.fileSize = fileSize;
            this.url = url;
        }
    }

    private static final List<ActiveRecallItem> DUMMY_RECALL_ITEMS = new ArrayList<>();
    private static final List<FlashcardItem> DUMMY_FLASHCARDS = new ArrayList<>();
    private static final List<ResourceItem> DUMMY_RESOURCES = new ArrayList<>();

    static {
        // ==========================================
        // 1. All 12 Botany Chapters (Biology 1st Paper) - Exact Requirement 8
        // ==========================================
        List<Chapter> paper1Chapters = new ArrayList<>();
        paper1Chapters.add(new Chapter("botany_ch01", PAPER_BIOLOGY_1, "Chapter 1: Cell and Its Structure (কোষ ও এর গঠন)", 1));
        paper1Chapters.add(new Chapter("botany_ch02", PAPER_BIOLOGY_1, "Chapter 2: Cell Division (কোষ বিভাজন)", 2));
        paper1Chapters.add(new Chapter("botany_ch03", PAPER_BIOLOGY_1, "Chapter 3: Cell Chemistry (কোষ রসায়ন)", 3));
        paper1Chapters.add(new Chapter("botany_ch04", PAPER_BIOLOGY_1, "Chapter 4: Microorganisms (অণুজীব)", 4));
        paper1Chapters.add(new Chapter("botany_ch05", PAPER_BIOLOGY_1, "Chapter 5: Algae and Fungi (শৈবাল ও ছত্রাক)", 5));
        paper1Chapters.add(new Chapter("botany_ch06", PAPER_BIOLOGY_1, "Chapter 6: Bryophyta and Pteridophyta (ব্রায়োফাইটা ও টেরিডোফাইটা)", 6));
        paper1Chapters.add(new Chapter("botany_ch07", PAPER_BIOLOGY_1, "Chapter 7: Gymnosperms and Angiosperms (নগ্নবীজী ও আবৃতবীজী উদ্ভিদ)", 7));
        paper1Chapters.add(new Chapter("botany_ch08", PAPER_BIOLOGY_1, "Chapter 8: Tissue and Tissue System (টিস্যু ও টিস্যুতন্ত্র)", 8));
        paper1Chapters.add(new Chapter("botany_ch09", PAPER_BIOLOGY_1, "Chapter 9: Plant Physiology (উদ্ভিদ শারীরতত্ত্ব)", 9));
        paper1Chapters.add(new Chapter("botany_ch10", PAPER_BIOLOGY_1, "Chapter 10: Plant Reproduction (উদ্ভিদ প্রজনন)", 10));
        paper1Chapters.add(new Chapter("botany_ch11", PAPER_BIOLOGY_1, "Chapter 11: Biotechnology (জীবপ্রযুক্তি)", 11));
        paper1Chapters.add(new Chapter("botany_ch12", PAPER_BIOLOGY_1, "Chapter 12: Environment, Distribution and Conservation of Organisms (জীবের পরিবেশ, বিস্তার ও সংরক্ষণ)", 12));
        CHAPTERS_BY_PAPER.put(PAPER_BIOLOGY_1, Collections.unmodifiableList(paper1Chapters));

        // ==========================================
        // 2. All 12 Zoology Chapters (Biology 2nd Paper) - Exact Requirement 8
        // ==========================================
        List<Chapter> paper2Chapters = new ArrayList<>();
        paper2Chapters.add(new Chapter("zoology_ch01", PAPER_BIOLOGY_2, "Chapter 1: Animal Diversity and Classification (প্রাণীর বিভিন্নতা ও শ্রেণিবিন্যাস)", 1));
        paper2Chapters.add(new Chapter("zoology_ch02", PAPER_BIOLOGY_2, "Chapter 2: Introduction to Animals (প্রাণীর পরিচিতি)", 2));
        paper2Chapters.add(new Chapter("zoology_ch03", PAPER_BIOLOGY_2, "Chapter 3: Digestion and Absorption (পরিপাক ও শোষণ)", 3));
        paper2Chapters.add(new Chapter("zoology_ch04", PAPER_BIOLOGY_2, "Chapter 4: Blood Circulation (রক্ত সঞ্চালন)", 4));
        paper2Chapters.add(new Chapter("zoology_ch05", PAPER_BIOLOGY_2, "Chapter 5: Respiration and Breathing (শ্বাসক্রিয়া ও শ্বসন)", 5));
        paper2Chapters.add(new Chapter("zoology_ch06", PAPER_BIOLOGY_2, "Chapter 6: Waste and Excretion (বর্জ্য ও নিষ্কাশন)", 6));
        paper2Chapters.add(new Chapter("zoology_ch07", PAPER_BIOLOGY_2, "Chapter 7: Locomotion and Movement (চলন ও অঙ্গচালনা)", 7));
        paper2Chapters.add(new Chapter("zoology_ch08", PAPER_BIOLOGY_2, "Chapter 8: Coordination and Control (সমন্বয় ও নিয়ন্ত্রণ)", 8));
        paper2Chapters.add(new Chapter("zoology_ch09", PAPER_BIOLOGY_2, "Chapter 9: Continuation of Human Life (মানব জীবনের ধারাবাহিকতা)", 9));
        paper2Chapters.add(new Chapter("zoology_ch10", PAPER_BIOLOGY_2, "Chapter 10: Defence of the Human Body (মানবদেহের প্রতিরক্ষা)", 10));
        paper2Chapters.add(new Chapter("zoology_ch11", PAPER_BIOLOGY_2, "Chapter 11: Genetics and Evolution (জিনতত্ত্ব ও বিবর্তন)", 11));
        paper2Chapters.add(new Chapter("zoology_ch12", PAPER_BIOLOGY_2, "Chapter 12: Animal Behaviour (প্রাণীর আচরণ)", 12));
        CHAPTERS_BY_PAPER.put(PAPER_BIOLOGY_2, Collections.unmodifiableList(paper2Chapters));

        // ==========================================
        // 3. Dummy MCQs for Botany Chapters
        // ==========================================
        addQuestion("c1_p1", PAPER_BIOLOGY_1, "q_c1p1_1", "Cell Organelles",
                "Which organelle is responsible for cellular respiration and energy production in eukaryotic cells?",
                Arrays.asList("Ribosome (রাইবোজোম)", "Chloroplast (ক্লোরোপ্লাস্ট)", "Mitochondria (মাইটোকনড্রিয়া)", "Golgi Apparatus (গলজি বস্তু)"),
                2,
                "Mitochondria generate most of the chemical energy in the form of ATP through oxidative phosphorylation.",
                "Sample");

        addQuestion("c1_p1", PAPER_BIOLOGY_1, "q_c1p1_2", "Cell Wall",
                "What is the primary structural carbohydrate found in plant cell walls?",
                Arrays.asList("Peptidoglycan", "Chitin", "Cellulose (সেলুলোজ)", "Glycogen"),
                2,
                "Plant cell walls are made predominantly of insoluble cellulose fibers and hemicellulose.",
                "Sample");

        addQuestion("c1_p1", PAPER_BIOLOGY_1, "q_c1p1_3", "Plastids",
                "Which type of leucoplast is specialized for starch storage in plant cells?",
                Arrays.asList("Chloroplast", "Chromoplast", "Amyloplast (অ্যামাইলোপ্লাস্ট)", "Elaioplast"),
                2,
                "Amyloplasts are non-pigmented plastids that synthesize and store starch.",
                "Sample");

        addQuestion("c2_p1", PAPER_BIOLOGY_1, "q_c2p1_1", "Meiosis",
                "In which sub-stage of Prophase I does genetic crossing over take place?",
                Arrays.asList("Leptotene", "Zygotene", "Pachytene (প্যাকাইটিন)", "Diplotene"),
                2,
                "Crossing over between non-sister homologous chromatids occurs in the Pachytene stage.",
                "Sample");

        addQuestion("c2_p1", PAPER_BIOLOGY_1, "q_c2p1_2", "Mitosis",
                "During which mitotic stage do sister chromatids separate toward opposite poles?",
                Arrays.asList("Prophase", "Metaphase", "Anaphase (অ্যানাফেজ)", "Telophase"),
                2,
                "Centromeres divide and daughter chromosomes migrate to opposite poles in Anaphase.",
                "Sample");

        addQuestion("c3_p1", PAPER_BIOLOGY_1, "q_c3p1_1", "Enzymes",
                "Which model best describes enzyme-substrate specificity and dynamic conformational change?",
                Arrays.asList("Lock and Key Model", "Induced Fit Model (ইনডিউসড ফিট মডেল)", "Fluid Mosaic Model", "Sliding Filament"),
                1,
                "Daniel Koshland proposed the Induced Fit Model where active site conforms upon substrate binding.",
                "Sample");

        addQuestion("c4_p1", PAPER_BIOLOGY_1, "q_c4p1_1", "Microbiology",
                "Which genetic material is present inside the T2 bacteriophage?",
                Arrays.asList("Single-stranded RNA", "Double-stranded DNA (দ্বিসূত্রক DNA)", "Single-stranded DNA", "Double-stranded RNA"),
                1,
                "T2 bacteriophage has linear double-stranded DNA inside its hexagonal protein capsid.",
                "Sample");

        addQuestion("c9_p1", PAPER_BIOLOGY_1, "q_c9p1_1", "Bioenergetics",
                "In the Calvin Cycle of photosynthesis, which enzyme catalyzes carbon fixation with RuBP?",
                Arrays.asList("Pepcase", "RuBisCO (রুবিসকো)", "ATP Synthase", "Carbonic Anhydrase"),
                1,
                "RuBisCO catalyzes the carboxylation of ribulose-1,5-bisphosphate (RuBP) to produce 3-PGA.",
                "Sample");

        // ==========================================
        // 4. Dummy MCQs for Zoology Chapters
        // ==========================================
        addQuestion("c1_p2", PAPER_BIOLOGY_2, "q_c1p2_1", "Classification",
                "Which phylum is distinguished by a unique water vascular (ambulacral) system?",
                Arrays.asList("Cnidaria", "Mollusca", "Echinodermata (একাইনোডার্মাটা)", "Annelida"),
                2,
                "Echinoderms rely on a hydraulic water vascular system for locomotion and gas exchange.",
                "Sample");

        addQuestion("c1_p2", PAPER_BIOLOGY_2, "q_c1p2_2", "Coelom",
                "Which phylum is characterized by having a pseudocoelom (false body cavity)?",
                Arrays.asList("Platyhelminthes", "Nematoda (নেমাটোডা)", "Annelida", "Arthropoda"),
                1,
                "Roundworms in phylum Nematoda possess a pseudocoelom derived from the blastocoel.",
                "Sample");

        addQuestion("c2_p2", PAPER_BIOLOGY_2, "q_c2p2_1", "Animal Identity",
                "Which specialized stinging cells are used by Hydra for prey capture and defense?",
                Arrays.asList("Choanocytes", "Cnidocytes / Nematocysts (নিডোসাইট)", "Flame cells", "Nephridia"),
                1,
                "Cnidocytes contain stinging capsules called nematocysts characteristic of Cnidarians.",
                "Sample");

        addQuestion("c3_p2", PAPER_BIOLOGY_2, "q_c3p2_1", "Human Digestion",
                "Which enzyme in gastric juice initiates protein digestion into peptones in the human stomach?",
                Arrays.asList("Amylase", "Pepsin (পেপসিন)", "Lipase", "Trypsin"),
                1,
                "Pepsinogen is activated by hydrochloric acid (HCl) into active pepsin to digest proteins.",
                "Sample");

        addQuestion("c4_p2", PAPER_BIOLOGY_2, "q_c4p2_1", "Human Circulation",
                "Which node serves as the natural electrical pacemaker of the human heart?",
                Arrays.asList("AV node", "SA node (এসএ নোড)", "Bundle of His", "Purkinje fibers"),
                1,
                "The Sinoatrial (SA) node in the right atrium initiates rhythmic electrical impulses.",
                "Sample");

        addQuestion("c11_p2", PAPER_BIOLOGY_2, "q_c11p2_1", "Genetics",
                "What is the classic dihybrid phenotypic ratio observed according to Mendel's Second Law?",
                Arrays.asList("3:1", "1:2:1", "9:3:3:1", "9:7"),
                2,
                "Mendel's Law of Independent Assortment yields a 9:3:3:1 phenotypic ratio in F2 generation.",
                "Sample");

        // Ensure every chapter in Paper 1 has at least 3 questions for thorough practice
        for (Chapter c : paper1Chapters) {
            List<Question> existing = QUESTIONS_BY_CHAPTER.get(c.getId());
            int currentCount = (existing != null) ? existing.size() : 0;
            for (int i = currentCount + 1; i <= 3; i++) {
                addQuestion(c.getId(), PAPER_BIOLOGY_1, "q_" + c.getId() + "_sample_" + i, c.getTitle(),
                        "HSC Practice Question " + i + " for " + c.getTitle() + ": Which statement is factually accurate?",
                        Arrays.asList("Option A (Concept Overview)", "Option B (Key Mechanism)", "Option C (Correct Concept)", "Option D (Alternative Explanation)"),
                        2,
                        "Explanation: Option C reflects standard HSC curriculum facts for " + c.getTitle(),
                        "Sample");
            }
        }

        // Ensure every chapter in Paper 2 has at least 3 questions for thorough practice
        for (Chapter c : paper2Chapters) {
            List<Question> existing = QUESTIONS_BY_CHAPTER.get(c.getId());
            int currentCount = (existing != null) ? existing.size() : 0;
            for (int i = currentCount + 1; i <= 3; i++) {
                addQuestion(c.getId(), PAPER_BIOLOGY_2, "q_" + c.getId() + "_sample_" + i, c.getTitle(),
                        "HSC Practice Question " + i + " for " + c.getTitle() + ": Which statement is factually accurate?",
                        Arrays.asList("Option A (Concept Overview)", "Option B (Key Mechanism)", "Option C (Correct Concept)", "Option D (Alternative Explanation)"),
                        2,
                        "Explanation: Option C reflects standard HSC curriculum facts for " + c.getTitle(),
                        "Sample");
            }
        }

        // ==========================================
        // 5. Dummy Active Recall Items (Bilingual)
        // ==========================================
        DUMMY_RECALL_ITEMS.add(new ActiveRecallItem(
                "rec_1", "উদ্ভিদ শারীরতত্ত্ব (Bioenergetics)",
                "Q1: Without looking at your textbook, explain the major stages of the Calvin Cycle. Describe carbon fixation, reduction, and regeneration of RuBP.",
                "(ক্যালভিন চক্রের ৩টি প্রধান ধাপ এবং RuBisCO, ATP ও NADPH এর ভূমিকা নিজের ভাষায় রিকল করুন)",
                "The Calvin Cycle consists of: 1) Carbon fixation catalyzed by RuBisCO, 2) Reduction using ATP and NADPH to yield G3P, and 3) Regeneration of RuBP with ATP.",
                "ক্যালভিন চক্রের ৩টি ধাপ: ১) রুবিসকো দ্বারা কার্বন সংবন্ধন, ২) ATP ও NADPH দ্বারা বিজারন এবং ৩) RuBP এর পুনরুৎপত্তি।",
                "RuBisCO, 3-PGA, G3P, RuBP, NADPH"
        ));

        DUMMY_RECALL_ITEMS.add(new ActiveRecallItem(
                "rec_2", "কোষ বিভাজন (Mitosis & Meiosis)",
                "Q2: Explain the significance of crossing over in Pachytene and how it generates genetic variation.",
                "(মিয়োসিস ১ এর প্যাকাইটিনে ক্রসিং ওভার কীভাবে নতুন প্রকরণ সৃষ্টি করে ব্যাখ্যা করুন)",
                "Non-sister chromatids of homologous chromosomes break and rejoin at chiasmata, producing recombinant chromosomes with unique allele combinations.",
                "হোমোলোগাস ক্রোমোজোমের নন-সিস্টার ক্রোমাটিডের মধ্যে খণ্ড বিনিময় কায়াজমার মাধ্যমে নতুন অ্যালিল সমন্বয় ও প্রকরণ ঘটায়।",
                "Homologous, Non-sister, Chiasma, Recombination"
        ));

        DUMMY_RECALL_ITEMS.add(new ActiveRecallItem(
                "rec_3", "রক্ত ও সঞ্চালন (Cardiovascular)",
                "Q3: Trace the flow of blood through the four chambers of the human heart and mention the valves involved.",
                "(মানব হৃৎপিণ্ডের ৪টি প্রকোষ্ঠ এবং কপাটিকার মধ্য দিয়ে রক্ত সংবহন পথ বর্ণনা করুন)",
                "Deoxygenated blood enters right atrium via vena cava -> Tricuspid valve -> Right ventricle -> Pulmonary artery. Oxygenated returns to left atrium -> Bicuspid -> Left ventricle -> Aorta.",
                "কার্বন-ডাই-অক্সাইডযুক্ত রক্ত ডান অলিন্দ -> ট্রাইকাস্পিড -> ডান নিলয় -> ফুসফুসীয় ধমনি। অক্সিজেনযুক্ত রক্ত বাম অলিন্দ -> বাইকাস্পিড -> বাম নিলয় -> মহাধমনি।",
                "Tricuspid, Bicuspid, SA Node, Pulmonary Vein"
        ));

        // ==========================================
        // 6. Dummy Flashcards (Front & Back)
        // ==========================================
        DUMMY_FLASHCARDS.add(new FlashcardItem(
                "fc_1", "কোষ ও এর গঠন",
                "What is the powerhouse of the cell, and what is its primary biological function?",
                "কোষের শক্তিঘর কোনটি এবং এর প্রধান কাজ কী?",
                "Mitochondria (মাইটোকন্ড্রিয়া)",
                "Mitochondria produce ATP through oxidative phosphorylation and aerobic respiration for cellular work.",
                "Medical Tip: Krebs cycle enzymes reside in the mitochondrial matrix."
        ));

        DUMMY_FLASHCARDS.add(new FlashcardItem(
                "fc_2", "কোষ রসায়ন",
                "What are the building blocks of proteins, and what bond links them?",
                "প্রোটিনের গঠনগত একক কী এবং কোন বন্ধন এদের যুক্ত করে?",
                "Amino Acids linked by Peptide Bonds (পেপটাইড বন্ধন)",
                "20 standard amino acids link through covalent peptide bonds (-CONH-) formed by condensation.",
                "Medical Tip: Essential amino acids must be acquired through human diet."
        ));

        DUMMY_FLASHCARDS.add(new FlashcardItem(
                "fc_3", "প্রাণীর বিভিন্নতা",
                "Which phylum possesses water vascular system and tube feet?",
                "কোন পর্বের প্রাণীদের পানি সংবহন তন্ত্র ও নালিকা পদ রয়েছে?",
                "Phylum Echinodermata (একাইনোডার্মাটা)",
                "Exclusively marine deuterostomes with radial symmetry as adults and calcareous endoskeleton.",
                "Medical Tip: Starfish (Asterias) and Sea Urchin belong to Echinodermata."
        ));

        DUMMY_FLASHCARDS.add(new FlashcardItem(
                "fc_4", "জিনতত্ত্ব ও বিবর্তন",
                "What is the phenotypic ratio of Mendel's dihybrid test cross?",
                "মেন্ডেলের দ্বিসংকর টেস্ট ক্রসের ফিনোটাইপিক অনুপাত কী?",
                "1:1:1:1 Ratio",
                "Test crossing a dihybrid (RrYy) with a double homozygous recessive (rryy) produces four equal phenotypes.",
                "Medical Tip: Deviation from 1:1:1:1 indicates genetic linkage."
        ));

        // ==========================================
        // 7. Dummy Resources (NCTB, Guides, Past Papers, Links)
        // ==========================================
        DUMMY_RESOURCES.add(new ResourceItem("res_1", "জীববিজ্ঞান ১ম পত্র (Botany)", "NCTB", "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড অনুমোদিত", "14.8 MB", "http://www.nctb.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_2", "জীববিজ্ঞান ২য় পত্র (Zoology)", "NCTB", "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড অনুমোদিত", "16.2 MB", "http://www.nctb.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_3", "পাঞ্জেরী জীববিজ্ঞান ১ম পত্র", "Guide", "অধ্যায়ভিত্তিক সৃজনশীল ও MCQ গাইড", "18.5 MB", "https://panjeree.com"));
        DUMMY_RESOURCES.add(new ResourceItem("res_4", "লেকচার সৃজনশীল ১ম পত্র", "Guide", "বোর্ড প্রশ্ন বিশ্লেষণ ও সমাধান", "21.0 MB", "https://lecturepublications.com"));
        DUMMY_RESOURCES.add(new ResourceItem("res_5", "ব্যবহারিক জীববিজ্ঞান নোটবুক", "Guide", "১ম ও ২য় পত্র প্র্যাকটিক্যাল নির্দেশিকা", "9.4 MB", "https://aksharpatra.com"));
        DUMMY_RESOURCES.add(new ResourceItem("res_6", "রয়েল জীববিজ্ঞান সহায়িকা", "Guide", "মেডিকেল ভর্তি প্রস্তুতি ও শর্ট ট্রিকস", "12.0 MB", "https://royalguide.com"));
        DUMMY_RESOURCES.add(new ResourceItem("res_7", "HSC Biology Complete Syllabus", "Syllabus", "Revised curriculum & marks distribution", "1.4 MB", "https://dhakaeducationboard.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_8", "Botany Chapter-wise Guide", "Syllabus", "কোষ থেকে জীবপ্রযুক্তি সারাংশ", "2.8 MB", "https://dhakaeducationboard.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_9", "Zoology Chapter-wise Guide", "Syllabus", "প্রাণীর বিভিন্নতা ও শারীরতত্ত্ব", "3.1 MB", "https://dhakaeducationboard.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_10", "2025 Model & Mock Board Exam", "PastPaper", "All Bangladesh Education Boards", "1.2 MB", "https://dhakaeducationboard.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_11", "2024 HSC Board Exam Paper", "PastPaper", "All Boards Official Questions with Solution", "1.3 MB", "https://dhakaeducationboard.gov.bd"));
        DUMMY_RESOURCES.add(new ResourceItem("res_12", "3D Bio Animation Portal", "Video", "DNA Replication & Meiosis in 3D", "Web Link", "https://www.youtube.com/results?search_query=biology+3d+animation"));
        DUMMY_RESOURCES.add(new ResourceItem("res_13", "HSC Crash Course Playlist", "Video", "YouTube Video Lessons by Top Mentors", "Web Link", "https://www.youtube.com/results?search_query=hsc+biology+crash+course"));
    }

    private static void addQuestion(String chapterId, String paperId, String qId, String topic,
                                    String text, List<String> options, int correctIndex, String explanation, String source) {
        List<Question> list = QUESTIONS_BY_CHAPTER.computeIfAbsent(chapterId, k -> new ArrayList<>());
        list.add(new Question(qId, paperId, chapterId, topic, text, options, correctIndex, explanation, source, null));
    }

    private SampleBiologyData() {
        // Utility class
    }

    public static List<Chapter> getChapters(String paperId) {
        if (paperId == null) return Collections.emptyList();
        List<Chapter> chapters = CHAPTERS_BY_PAPER.get(paperId.trim());
        return chapters != null ? chapters : Collections.emptyList();
    }

    public static List<Question> getQuestions(String paperId, String chapterId) {
        if (chapterId == null) return Collections.emptyList();
        List<Question> questions = QUESTIONS_BY_CHAPTER.get(chapterId.trim());
        if (questions == null) return Collections.emptyList();
        if (paperId != null && !paperId.trim().isEmpty()) {
            List<Question> filtered = new ArrayList<>();
            for (Question q : questions) {
                if (paperId.trim().equalsIgnoreCase(q.getPaper())) {
                    filtered.add(q);
                }
            }
            return Collections.unmodifiableList(filtered);
        }
        return Collections.unmodifiableList(questions);
    }

    public static List<Question> getAllQuestions() {
        List<Question> all = new ArrayList<>();
        for (List<Question> list : QUESTIONS_BY_CHAPTER.values()) {
            all.addAll(list);
        }
        return Collections.unmodifiableList(all);
    }

    public static Chapter getChapter(String paperId, String chapterId) {
        List<Chapter> chapters = getChapters(paperId);
        for (Chapter c : chapters) {
            if (c.getId().equalsIgnoreCase(chapterId)) {
                return c;
            }
        }
        return null;
    }

    public static List<ActiveRecallItem> getActiveRecallItems() {
        return Collections.unmodifiableList(DUMMY_RECALL_ITEMS);
    }

    public static List<FlashcardItem> getFlashcards() {
        return Collections.unmodifiableList(DUMMY_FLASHCARDS);
    }

    public static List<ResourceItem> getResources() {
        return Collections.unmodifiableList(DUMMY_RESOURCES);
    }
}
