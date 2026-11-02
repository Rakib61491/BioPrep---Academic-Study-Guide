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
 * Local offline repository of HSC Biology sample chapters and MCQs.
 * All questions are original samples authored for this lab project.
 */
public final class SampleBiologyData {

    public static final String PAPER_BIOLOGY_1 = "BIOLOGY_1";
    public static final String PAPER_BIOLOGY_2 = "BIOLOGY_2";

    private static final Map<String, List<Chapter>> CHAPTERS_BY_PAPER = new LinkedHashMap<>();
    private static final Map<String, List<Question>> QUESTIONS_BY_CHAPTER = new LinkedHashMap<>();

    static {
        // Initialize Chapters for Biology 1st Paper (Botany)
        List<Chapter> paper1Chapters = new ArrayList<>();
        paper1Chapters.add(new Chapter("c1_p1", PAPER_BIOLOGY_1, "Cell & Its Structure", 1));
        paper1Chapters.add(new Chapter("c2_p1", PAPER_BIOLOGY_1, "Cell Division", 2));
        CHAPTERS_BY_PAPER.put(PAPER_BIOLOGY_1, Collections.unmodifiableList(paper1Chapters));

        // Initialize Chapters for Biology 2nd Paper (Zoology)
        List<Chapter> paper2Chapters = new ArrayList<>();
        paper2Chapters.add(new Chapter("c1_p2", PAPER_BIOLOGY_2, "Animal Diversity & Classification", 1));
        paper2Chapters.add(new Chapter("c2_p2", PAPER_BIOLOGY_2, "Human Physiology: Circulation", 2));
        CHAPTERS_BY_PAPER.put(PAPER_BIOLOGY_2, Collections.unmodifiableList(paper2Chapters));

        // Questions for Paper 1, Chapter 1: Cell & Its Structure
        List<Question> c1p1Questions = new ArrayList<>();
        c1p1Questions.add(new Question(
                "q_c1p1_1", PAPER_BIOLOGY_1, "c1_p1", "Cell Organelles",
                "Which organelle is widely known as the powerhouse of the cell?",
                Arrays.asList("Ribosome", "Mitochondria", "Lysosome", "Centrosome"),
                1,
                "Mitochondria generate cellular energy in the form of ATP.",
                "Sample", null));
        c1p1Questions.add(new Question(
                "q_c1p1_2", PAPER_BIOLOGY_1, "c1_p1", "Cell Wall",
                "What is the primary structural carbohydrate found in plant cell walls?",
                Arrays.asList("Peptidoglycan", "Chitin", "Cellulose", "Glycogen"),
                2,
                "Plant cell walls are made predominantly of insoluble cellulose fibers.",
                "Sample", null));
        c1p1Questions.add(new Question(
                "q_c1p1_3", PAPER_BIOLOGY_1, "c1_p1", "Plastids",
                "Which type of leucoplast is specialized for starch storage in plant cells?",
                Arrays.asList("Chloroplast", "Chromoplast", "Amyloplast", "Elaioplast"),
                2,
                "Amyloplasts are non-pigmented plastids that synthesize and store starch.",
                "Sample", null));
        QUESTIONS_BY_CHAPTER.put("c1_p1", Collections.unmodifiableList(c1p1Questions));

        // Questions for Paper 1, Chapter 2: Cell Division
        List<Question> c2p1Questions = new ArrayList<>();
        c2p1Questions.add(new Question(
                "q_c2p1_1", PAPER_BIOLOGY_1, "c2_p1", "Meiosis",
                "In which sub-stage of Prophase I does genetic crossing over take place?",
                Arrays.asList("Leptotene", "Zygotene", "Pachytene", "Diplotene"),
                2,
                "Crossing over between non-sister homologous chromatids occurs in Pachytene.",
                "Sample", null));
        c2p1Questions.add(new Question(
                "q_c2p1_2", PAPER_BIOLOGY_1, "c2_p1", "Mitosis",
                "During which mitotic stage do sister chromatids separate toward opposite poles?",
                Arrays.asList("Prophase", "Metaphase", "Anaphase", "Telophase"),
                2,
                "Centromeres split and daughter chromosomes migrate toward poles in Anaphase.",
                "Sample", null));
        c2p1Questions.add(new Question(
                "q_c2p1_3", PAPER_BIOLOGY_1, "c2_p1", "Cell Cycle",
                "What is the specialized cell division that produces haploid gametes?",
                Arrays.asList("Amitosis", "Mitosis", "Meiosis", "Binary Fission"),
                2,
                "Meiosis reduces the chromosome count by half to yield haploid cells.",
                "Sample", null));
        QUESTIONS_BY_CHAPTER.put("c2_p1", Collections.unmodifiableList(c2p1Questions));

        // Questions for Paper 2, Chapter 1: Animal Diversity & Classification
        List<Question> c1p2Questions = new ArrayList<>();
        c1p2Questions.add(new Question(
                "q_c1p2_1", PAPER_BIOLOGY_2, "c1_p2", "Phylum Echinodermata",
                "Which phylum is distinguished by a unique water vascular (ambulacral) system?",
                Arrays.asList("Cnidaria", "Mollusca", "Echinodermata", "Annelida"),
                2,
                "Echinoderms rely on a water vascular system for locomotion and respiration.",
                "Sample", null));
        c1p2Questions.add(new Question(
                "q_c1p2_2", PAPER_BIOLOGY_2, "c1_p2", "Symmetry",
                "Which of the following organisms exhibits bilateral symmetry?",
                Arrays.asList("Hydra", "Sea Anemone", "Tapeworm (Taenia)", "Sycon sponge"),
                2,
                "Platyhelminthes like Taenia exhibit triploblastic bilateral symmetry.",
                "Sample", null));
        c1p2Questions.add(new Question(
                "q_c1p2_3", PAPER_BIOLOGY_2, "c1_p2", "Coelom",
                "Which phylum is characterized by having a pseudocoelom (false body cavity)?",
                Arrays.asList("Platyhelminthes", "Nematoda", "Annelida", "Arthropoda"),
                1,
                "Roundworms in phylum Nematoda possess a pseudocoelom.",
                "Sample", null));
        QUESTIONS_BY_CHAPTER.put("c1_p2", Collections.unmodifiableList(c1p2Questions));

        // Questions for Paper 2, Chapter 2: Human Physiology: Circulation
        List<Question> c2p2Questions = new ArrayList<>();
        c2p2Questions.add(new Question(
                "q_c2p2_1", PAPER_BIOLOGY_2, "c2_p2", "Cardiac Conduction",
                "Which node serves as the primary pacemaker of the human heart?",
                Arrays.asList("AV node", "SA node", "Bundle of His", "Purkinje fibers"),
                1,
                "The Sinoatrial (SA) node initiates rhythmic electrical impulses in the heart.",
                "Sample", null));
        c2p2Questions.add(new Question(
                "q_c2p2_2", PAPER_BIOLOGY_2, "c2_p2", "Blood Vessels",
                "Which blood vessel delivers oxygenated blood from lungs to the left atrium?",
                Arrays.asList("Pulmonary artery", "Superior vena cava", "Pulmonary vein", "Aorta"),
                2,
                "Pulmonary veins uniquely carry oxygenated blood to the left atrium.",
                "Sample", null));
        c2p2Questions.add(new Question(
                "q_c2p2_3", PAPER_BIOLOGY_2, "c2_p2", "Blood Physiology",
                "Which cellular component of blood plays the primary role in hemostasis and clotting?",
                Arrays.asList("Erythrocytes", "Leukocytes", "Thrombocytes", "Blood plasma"),
                2,
                "Thrombocytes (platelets) aggregate and release thromboplastin for clotting.",
                "Sample", null));
        QUESTIONS_BY_CHAPTER.put("c2_p2", Collections.unmodifiableList(c2p2Questions));
    }

    private SampleBiologyData() {
        // Utility repository
    }

    /**
     * Returns an unmodifiable list of chapters for the specified paper.
     */
    public static List<Chapter> getChapters(String paperId) {
        if (paperId == null) {
            return Collections.emptyList();
        }
        List<Chapter> chapters = CHAPTERS_BY_PAPER.get(paperId.trim());
        return chapters != null ? chapters : Collections.emptyList();
    }

    /**
     * Returns an unmodifiable list of questions for the specified paper and chapter.
     */
    public static List<Question> getQuestions(String paperId, String chapterId) {
        if (chapterId == null) {
            return Collections.emptyList();
        }
        List<Question> questions = QUESTIONS_BY_CHAPTER.get(chapterId.trim());
        if (questions == null) {
            return Collections.emptyList();
        }
        // Verify paper alignment
        if (paperId != null && !paperId.trim().isEmpty()) {
            List<Question> filtered = new ArrayList<>();
            for (Question q : questions) {
                if (paperId.trim().equalsIgnoreCase(q.getPaper())) {
                    filtered.add(q);
                }
            }
            return Collections.unmodifiableList(filtered);
        }
        return questions;
    }

    /**
     * Finds a Chapter by paper and chapter ID.
     */
    public static Chapter getChapter(String paperId, String chapterId) {
        List<Chapter> chapters = getChapters(paperId);
        for (Chapter c : chapters) {
            if (c.getId().equalsIgnoreCase(chapterId)) {
                return c;
            }
        }
        return null;
    }
}

