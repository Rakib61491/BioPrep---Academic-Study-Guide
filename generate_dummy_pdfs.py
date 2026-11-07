import os
from fpdf import FPDF

class BioPDF(FPDF):
    def __init__(self, title_text, category_text):
        super().__init__()
        self.doc_title = title_text
        self.doc_category = category_text
        self.set_auto_page_break(auto=True, margin=15)

    def header(self):
        self.set_font('Helvetica', 'I', 8)
        self.set_text_color(128, 128, 128)
        self.cell(0, 8, f'BioPrep Academic Study Guide | {self.doc_category} (Demonstration Dummy Resource)', border=0, new_x='LMARGIN', new_y='NEXT', align='L')
        self.line(10, 18, 200, 18)
        self.ln(5)

    def footer(self):
        self.set_y(-15)
        self.set_font('Helvetica', 'I', 8)
        self.set_text_color(150, 150, 150)
        self.cell(0, 10, f'Page {self.page_no()} of {{nb}} | HSC Biology Learning Platform (Dummy Education Resource)', align='C')

    def chapter_title(self, num, label):
        self.set_font('Helvetica', 'B', 14)
        self.set_text_color(6, 78, 59) # Deep emerald
        self.cell(0, 10, f'{num}. {label}', new_x='LMARGIN', new_y='NEXT', align='L')
        self.ln(2)

    def chapter_body(self, body_text):
        self.set_font('Helvetica', '', 10)
        self.set_text_color(40, 40, 40)
        self.multi_cell(0, 6, body_text)
        self.ln(4)

def build_sample_book():
    pdf = BioPDF("HSC Biology Comprehensive Study Guide", "Reference Books")
    pdf.alias_nb_pages()
    
    # Page 1: Cover & Intro
    pdf.add_page()
    pdf.ln(20)
    pdf.set_font('Helvetica', 'B', 22)
    pdf.set_text_color(16, 185, 129)
    pdf.cell(0, 12, 'HSC BIOLOGY REFERENCE BOOK', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.set_font('Helvetica', '', 14)
    pdf.set_text_color(80, 80, 80)
    pdf.cell(0, 8, 'Botany & Zoology Standard Study Companion', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.ln(10)
    pdf.set_font('Helvetica', 'I', 11)
    pdf.set_text_color(220, 38, 38)
    pdf.cell(0, 8, '[ DEMONSTRATION DUMMY PDF - REPLACEABLE IN PRODUCTION ]', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.ln(15)
    
    pdf.chapter_title(1, "Introduction to HSC Biology")
    pdf.chapter_body(
        "Welcome to the BioPrep Reference Book for Higher Secondary Certificate (HSC) examination. "
        "This sample document demonstrates the integrated multi-page vertical PDF reader inside the application. "
        "In production, this sample document can be substituted with an authorized board textbook or reference textbook.\n\n"
        "HSC Biology is divided into two comprehensive papers:\n"
        "- Biology 1st Paper: Plant Biology (Botany)\n"
        "- Biology 2nd Paper: Animal Biology (Zoology)\n\n"
        "Both papers place significant emphasis on cellular ultrastructure, metabolic pathways, physiology, genetics, and ecology."
    )
    
    # Page 2: Botany Core Concepts
    pdf.add_page()
    pdf.chapter_title(2, "Botany Core Focus - Photosynthesis & Cellular Respiration")
    pdf.chapter_body(
        "Photosynthesis in C3 and C4 plants involves two interconnected stages: light-dependent reactions taking place in the thylakoid membranes, "
        "and the light-independent Calvin cycle operating in the chloroplast stroma.\n\n"
        "Key Enzymatic Actions:\n"
        "1. Ribulose-1,5-bisphosphate carboxylase-oxygenase (RuBisCO): Catalyzes carbon dioxide fixation.\n"
        "2. ATP Synthase (CF0-CF1 complex): Generates ATP through chemiosmosis driven by the proton gradient.\n"
        "3. Phosphoenolpyruvate (PEP) Carboxylase: Primary CO2 acceptor in C4 Hatch-Slack pathway, avoiding photorespiration.\n\n"
        "Cellular Respiration occurs across glycolysis in the cytoplasm, pyruvate decarboxylation, the Krebs citric acid cycle in the mitochondrial matrix, "
        "and oxidative phosphorylation along the inner mitochondrial cristae."
    )
    
    # Page 3: Zoology Core Concepts
    pdf.add_page()
    pdf.chapter_title(3, "Zoology Core Focus - Human Circulatory & Nervous Systems")
    pdf.chapter_body(
        "The human heart is a myogenic four-chambered muscular pump enclosed in pericardium. "
        "Cardiac impulses initiate at the Sinoatrial (SA) node ('the natural pacemaker'), propagate across the atria to the Atrioventricular (AV) node, "
        "and travel swiftly down the Bundle of His and Purkinje fibers to elicit ventricular systole.\n\n"
        "Blood Composition:\n"
        "- Plasma (55%): Transports albumin, fibrinogen, immunoglobulins, electrolytes, and metabolic nutrients.\n"
        "- Formed Elements (45%): Erythrocytes (RBCs with hemoglobin), Leukocytes (granulocytes and agranulocytes), and Thrombocytes (platelets for clotting cascade).\n\n"
        "The central and peripheral nervous systems integrate sensory input through afferent neurons, process information in the brain and spinal cord, "
        "and execute responses via efferent somatic and autonomic pathways."
    )
    
    # Page 4: Board Exam Tips
    pdf.add_page()
    pdf.chapter_title(4, "High-Scoring Guidelines for Creative Questions (CQ) & MCQs")
    pdf.chapter_body(
        "1. Precision in Terminology: Use correct scientific nomenclature (italicized binomial genus and species names).\n"
        "2. Labeled Diagrams: Clearly label cell structures, vascular bundles, heart chambers, and nephrons with sharp pencil lines.\n"
        "3. Comparison Tables: Present contrasting concepts (e.g., Mitosis vs Meiosis, C3 vs C4, Arteries vs Veins) in clear side-by-side matrices.\n"
        "4. Time Management: Allocate exactly 20 minutes per Creative Question and 30 seconds per Multiple Choice Question.\n\n"
        "This concludes the sample Biology Reference Book."
    )
    
    out_path = os.path.join("app", "src", "main", "assets", "resources", "books", "sample_biology_book.pdf")
    pdf.output(out_path)
    print(f"Generated: {out_path} ({pdf.page_no()} pages)")

def build_syllabus():
    pdf = BioPDF("HSC Biology Syllabus 2024-2025", "Syllabus")
    pdf.alias_nb_pages()
    
    # Page 1: Overview
    pdf.add_page()
    pdf.ln(15)
    pdf.set_font('Helvetica', 'B', 20)
    pdf.set_text_color(16, 185, 129)
    pdf.cell(0, 10, 'HSC BIOLOGY CURRICULUM SYLLABUS', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.set_font('Helvetica', '', 12)
    pdf.set_text_color(100, 100, 100)
    pdf.cell(0, 8, 'National Curriculum and Textbook Board (NCTB) Bangladesh', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.ln(8)
    
    pdf.chapter_title(1, "Marks Distribution & Examination Pattern")
    pdf.chapter_body(
        "Each Biology paper carries 100 total marks divided into three distinct segments:\n"
        "1. Creative Questions (CQ) / Theory: 50 Marks (Answer 5 questions out of 8, each worth 10 marks: 1+2+3+4).\n"
        "2. Multiple Choice Questions (MCQ): 25 Marks (25 questions, 1 mark each, 25 minutes duration).\n"
        "3. Practical Assessment: 25 Marks (Specimen dissection/identification, temporary slide preparation, experiment, oral viva, and practical notebook)."
    )
    
    # Page 2: Paper 1 Chapters
    pdf.add_page()
    pdf.chapter_title(2, "Biology 1st Paper (Botany) Chapters")
    pdf.chapter_body(
        "Chapter 1: Cell and Its Structure (Plasma membrane, organelle ultrastructure, chromosome morphology, DNA/RNA replication).\n"
        "Chapter 2: Cell Division (Amitosis, Mitosis stages, Meiosis I/II and genetic crossing over).\n"
        "Chapter 3: Cell Chemistry (Carbohydrates, amino acids, lipids, proteins, and enzyme kinetics).\n"
        "Chapter 4: Microorganisms (Virus structure like T2 and HIV, Bacteria classification, Malarial parasite Plasmodium life cycle).\n"
        "Chapter 5: Algae and Fungi (Spirogyra, Agaricus, Phytophthora, lichen symbiosis).\n"
        "Chapter 6: Bryophyta and Pteridophyta (Riccia morphology, Pteris life cycle and alternation of generations).\n"
        "Chapter 7: Gymnosperms and Angiosperms (Cycas morphology, floral formula, floral diagram of Poaceae and Malvaceae).\n"
        "Chapter 8: Tissue and Tissue System (Meristematic tissue, vascular bundle arrangements, dicot and monocot stem/root anatomy).\n"
        "Chapter 9: Plant Physiology (Water absorption, transpiration, photosynthesis C3/C4, cellular respiration, mineral nutrition).\n"
        "Chapter 10: Plant Reproduction (Microsporogenesis, megasporogenesis, double fertilization, apomixis).\n"
        "Chapter 11: Biotechnology (Tissue culture, recombinant DNA technology, PCR, genetic engineering applications).\n"
        "Chapter 12: Environment, Distribution and Conservation (Biomes, Sundarbans mangrove ecosystem, in-situ and ex-situ conservation)."
    )
    
    # Page 3: Paper 2 Chapters
    pdf.add_page()
    pdf.chapter_title(3, "Biology 2nd Paper (Zoology) Chapters")
    pdf.chapter_body(
        "Chapter 1: Animal Diversity and Classification (Symmetry, coelom, germ layers, Non-Chordata and Chordata phyla characteristics).\n"
        "Chapter 2: Introduction to Animals (Hydra morphology and nematocysts, Periplaneta americana anatomy, Labeo rohita).\n"
        "Chapter 3: Digestion and Absorption (Digestive tract, liver, pancreas, enzymatic breakdown of carbohydrates, proteins, lipids).\n"
        "Chapter 4: Blood Circulation (Heart structure, cardiac cycle, ECG, coronary circulation, baroreceptors, cardiovascular diseases).\n"
        "Chapter 5: Respiration and Breathing (Alveoli histology, oxygen-hemoglobin dissociation curve, carbon dioxide transport, respiratory volumes).\n"
        "Chapter 6: Waste and Excretion (Nephron histology, ultrafiltration in glomerulus, counter-current mechanism, osmoregulation).\n"
        "Chapter 7: Locomotion and Movement (Human skeletal framework, bone histology, muscle sliding filament theory, joints).\n"
        "Chapter 8: Coordination and Control (Brain regions, cranial nerves, endocrine glands and hormonal actions, eye and ear anatomy).\n"
        "Chapter 9: Continuation of Human Life (Gametogenesis, menstrual cycle, fertilization, embryonic development, placenta).\n"
        "Chapter 10: Defence of the Human Body (Innate immunity, humoral and cell-mediated immunity, antibodies, vaccination).\n"
        "Chapter 11: Genetics and Evolution (Mendelian genetics, epistasis, sex-linked disorders, Darwinism and neo-Darwinian evolution).\n"
        "Chapter 12: Animal Behaviour (Innate vs learned behaviour, circadian rhythms, altruism in honeybees, migration in birds)."
    )
    
    out_path = os.path.join("app", "src", "main", "assets", "resources", "syllabus", "hsc_biology_syllabus.pdf")
    pdf.output(out_path)
    print(f"Generated: {out_path} ({pdf.page_no()} pages)")

def build_test_paper_2025():
    pdf = BioPDF("HSC Biology 2025 Model Test Paper", "Test Papers")
    pdf.alias_nb_pages()
    
    # Page 1: Model Paper 1
    pdf.add_page()
    pdf.ln(15)
    pdf.set_font('Helvetica', 'B', 18)
    pdf.set_text_color(16, 185, 129)
    pdf.cell(0, 10, 'HSC EXAMINATION 2025 - MODEL TEST PAPER', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(60, 60, 60)
    pdf.cell(0, 8, 'BIOLOGY FIRST PAPER (BOTANY) - CREATIVE & MCQ', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.ln(6)
    
    pdf.chapter_title(1, "Sample Creative Question 1 (Cell Structure)")
    pdf.chapter_body(
        "Stimulus: A double membrane-bound cell organelle contains flattened thylakoid discs stacked into grana and stroma fluid with circular DNA.\n"
        "Questions:\n"
        "a) What is a ribosome? (1 Mark)\n"
        "b) Explain why mitochondria are considered semi-autonomous organelles. (2 Marks)\n"
        "c) Explain the ultrastructure of the organelle mentioned in the stimulus with a diagram. (3 Marks)\n"
        "d) Evaluate the physiological importance of this organelle in sustaining the biosphere. (4 Marks)"
    )
    
    pdf.chapter_title(2, "Sample Creative Question 2 (Biotechnology)")
    pdf.chapter_body(
        "Stimulus: Scientists transferred the cry1Ac endotoxin gene from Bacillus thuringiensis into Gossypium hirsutum to resist bollworm infestation.\n"
        "Questions:\n"
        "a) What is a plasmid? (1 Mark)\n"
        "b) Distinguish between cohesive and blunt ends generated by restriction endonucleases. (2 Marks)\n"
        "c) Describe the operational steps required to produce the transgenic organism in the stimulus. (3 Marks)\n"
        "d) Analyze the ecological benefits and biosafety concerns associated with this technology. (4 Marks)"
    )
    
    # Page 2: Sample MCQs
    pdf.add_page()
    pdf.chapter_title(3, "Selected High-Probability MCQs (2025 Series)")
    pdf.chapter_body(
        "1. Which organelle is called the 'suicide bag' of the cell?\n"
        "   (A) Peroxisome  (B) Lysosome  (C) Centrosome  (D) Ribosome\n"
        "   Answer: (B) Lysosome contains hydrolytic acid hydrolases.\n\n"
        "2. In which sub-stage of Prophase I does genetic crossing over take place?\n"
        "   (A) Leptotene  (B) Zygotene  (C) Pachytene  (D) Diplotene\n"
        "   Answer: (C) Pachytene stage forms chiasmata.\n\n"
        "3. What is the primary carbon dioxide acceptor in C4 plants?\n"
        "   (A) RuBP  (B) PEP  (C) Oxaloacetate  (D) 3-PGA\n"
        "   Answer: (B) Phosphoenolpyruvate (PEP).\n\n"
        "4. Which bacterium is widely employed as a natural genetic engineer in plant transgenesis?\n"
        "   (A) Escherichia coli  (B) Agrobacterium tumefaciens  (C) Bacillus thuringiensis  (D) Rhizobium leguminosarum\n"
        "   Answer: (B) Agrobacterium tumefaciens via Ti plasmid."
    )
    
    out_path = os.path.join("app", "src", "main", "assets", "resources", "test_papers", "hsc_2025_test_paper.pdf")
    pdf.output(out_path)
    print(f"Generated: {out_path} ({pdf.page_no()} pages)")

def build_test_paper_2024():
    pdf = BioPDF("HSC Biology 2024 Board Questions & Solutions", "Test Papers")
    pdf.alias_nb_pages()
    
    # Page 1: Board Paper Review
    pdf.add_page()
    pdf.ln(15)
    pdf.set_font('Helvetica', 'B', 18)
    pdf.set_text_color(16, 185, 129)
    pdf.cell(0, 10, 'HSC BOARD EXAM 2024 - PAST PAPER & SOLUTION', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(60, 60, 60)
    pdf.cell(0, 8, 'BIOLOGY SECOND PAPER (ZOOLOGY) - SOLVED QUESTIONS', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.ln(6)
    
    pdf.chapter_title(1, "Board Creative Question 1 (Circulatory System)")
    pdf.chapter_body(
        "Stimulus: A 52-year-old individual experienced severe retrosternal chest pain radiating to the left arm. Angiography showed 85% occlusion of the left anterior descending coronary artery.\n"
        "Questions:\n"
        "a) What is angioplasty? (1 Mark)\n"
        "b) Distinguish between systemic circulation and pulmonary circulation. (2 Marks)\n"
        "c) Explain the cardiac cycle events corresponding to heart sound generation (Lub and Dub). (3 Marks)\n"
        "d) Compare coronary stenting and coronary artery bypass graft (CABG) for relieving the condition. (4 Marks)"
    )
    
    # Page 2: Excretion & Movement
    pdf.add_page()
    pdf.chapter_title(2, "Board Creative Question 2 (Renal Excretion)")
    pdf.chapter_body(
        "Stimulus: The renal cortex contains 1 to 1.2 million functional units responsible for selective reabsorption and urine formation.\n"
        "Questions:\n"
        "a) What is podocyte? (1 Mark)\n"
        "b) Explain the role of antidiuretic hormone (ADH) in osmoregulation. (2 Marks)\n"
        "c) Describe the mechanism of glomerular ultrafiltration based on effective filtration pressure. (3 Marks)\n"
        "d) 'Kidney dialysis serves as an artificial life support' - Justify this statement with clinical principles. (4 Marks)"
    )
    
    pdf.chapter_title(3, "Board Multiple Choice Analysis (2024)")
    pdf.chapter_body(
        "1. Which chamber of the human heart has the thickest muscular wall?\n"
        "   (A) Right atrium  (B) Left atrium  (C) Right ventricle  (D) Left ventricle\n"
        "   Answer: (D) Left ventricle pumps systemic blood at high pressure.\n\n"
        "2. Which immunoglobulin is predominantly present in maternal colostrum milk?\n"
        "   (A) IgG  (B) IgA  (C) IgM  (D) IgE\n"
        "   Answer: (B) Secretory IgA provides mucosal neonatal immunity.\n\n"
        "3. What is the phenotypic ratio of dominant epistasis in dihybrid crosses?\n"
        "   (A) 9:3:3:1  (B) 12:3:1  (C) 9:7  (D) 13:3\n"
        "   Answer: (B) 12:3:1."
    )
    
    out_path = os.path.join("app", "src", "main", "assets", "resources", "test_papers", "hsc_2024_test_paper.pdf")
    pdf.output(out_path)
    print(f"Generated: {out_path} ({pdf.page_no()} pages)")

if __name__ == '__main__':
    build_sample_book()
    build_syllabus()
    build_test_paper_2025()
    build_test_paper_2024()
    print("All 4 dummy PDF files generated successfully!")

