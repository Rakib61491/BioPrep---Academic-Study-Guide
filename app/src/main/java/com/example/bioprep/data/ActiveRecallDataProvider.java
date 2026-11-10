package com.example.bioprep.data;

import com.example.bioprep.data.SampleBiologyData.ActiveRecallItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * High-yield Active Recall Data Provider supplying 10 syllabus-aligned sample
 * questions, model answers, and key terms for all 24 chapters of HSC Biology:
 * 12 Botany chapters (Biology 1st Paper) and 12 Zoology chapters (Biology 2nd Paper).
 */
public class ActiveRecallDataProvider {

    private static final Map<String, List<ActiveRecallItem>> CHAPTER_QUESTIONS = new HashMap<>();

    static {
        // =========================================================================
        // BOTANY - 12 CHAPTERS (Biology 1st Paper)
        // =========================================================================

        // Chapter 1: Cell and Its Structure (কোষ ও এর গঠন)
        addQuestions("botany_ch01", "Chapter 1: Cell and Its Structure", new String[][]{
                {"Explain the Fluid Mosaic Model of cell membrane.",
                 "কোষ ঝিল্লির ফ্লুইড মোজাইক মডেল ব্যাখ্যা করো।",
                 "Proposed by Singer and Nicolson (1972). Phospholipid bilayer acts as a fluid matrix where protein molecules float like icebergs in a mosaic pattern. Contains phospholipids, intrinsic/extrinsic proteins, cholesterol, and glycocalyx.",
                 "সিংগার ও নিকলসন (১৯৭২) এটি প্রস্তাব করেন। ফসফোলিপিড বাইলেয়ার তরলের মতো আচরণ করে যেখানে প্রোটিন অণুগুলো মোজাইকের মতো ভাসমান থাকে। এতে লিপিড, প্রোটিন, কোলেস্টেরল ও গ্লাইকোক্যালিক্স থাকে।",
                 "Singer-Nicolson • Phospholipid Bilayer • Fluidity • Glycocalyx"},
                {"What is the role of ribosomes in eukaryotic cells?",
                 "ইউক্যারিওটিক কোষে রাইবোজোমের ভূমিকা কী?",
                 "70S and 80S ribosomes are non-membranous ribonucleoprotein complexes serving as the primary sites of mRNA translation and protein synthesis in cells.",
                 "৭০S ও ৮০S রাইবোজোম হলো আবরণীবিহীন অঙ্গাণু যা mRNA অনুবাদের মাধ্যমে কোষে প্রোটিন সংশ্লেষণের প্রধান কেন্দ্র হিসেবে কাজ করে।",
                 "80S Ribosome • Translation • rRNA • Protein Synthesis"},
                {"Describe the structural components of Mitochondria.",
                 "মাইটোকনড্রিয়ার গঠনগত উপাদান বর্ণনা করো।",
                 "Bounded by double membrane: smooth outer membrane and folded inner membrane forming cristae. Contains mitochondrial matrix, 70S ribosomes, circular circular DNA, and F0-F1 ATP synthase particles.",
                 "দ্বিস্তরবিশিষ্ট ঝিল্লি: মসৃণ বহিস্থ ও ভাঁজযুক্ত ক্রিস্টি বিশিষ্ট অভ্যন্তরীণ পর্দা। এতে ম্যাট্রিক্স, ৭০S রাইবোজোম, বৃত্তাকার ডিএনএ এবং ATP সিন্থেস কণা থাকে।",
                 "Cristae • Matrix • ATP Synthase • Circular DNA • 70S Ribosome"},
                {"What are the three types of plastids and their functions?",
                 "প্লাস্টিড কয় প্রকার এবং তাদের কাজ কী কী?",
                 "1. Chloroplast: Green plastid containing chlorophyll, performs photosynthesis.\n2. Chromoplast: Colored (red/yellow/orange) with carotenoids, attracts pollinators.\n3. Leucoplast: Colorless plastid storing starch (amyloplast), oil (elaioplast), or protein (aleuroplast).",
                 "১. ক্লোরোপ্লাস্ট: ক্লোরোফিলযুক্ত, সালোকসংশ্লেষণ ঘটায়।\n২. ক্রোমোপ্লাস্ট: বর্ণযুক্ত (ক্যারোটিনয়েড), পরাগায়নে সাহায্য করে।\n৩. লিউকোপ্লাস্ট: বর্ণহীন, খাদ্য সঞ্চয় করে (শ্বেতসার, তেল বা প্রোটিন)।",
                 "Chloroplast • Chromoplast • Leucoplast • Photosynthesis • Amyloplast"},
                {"Explain the structure and function of Golgi Apparatus.",
                 "গলজি বস্তুর গঠন ও কাজ ব্যাখ্যা করো।",
                 "Consists of cisternae, vesicles, and vacuoles. Acts as the packaging center, synthesizes carbohydrates, forms lysosomes and cell plate, and packages secretory proteins.",
                 "সিস্টার্নি, ভেসিকল ও ভ্যাকুওল নিয়ে গঠিত। এটি কোষের ট্রাফিক পুলিশ হিসেবে পরিচিত, ক্ষরণকারী পদার্থ প্যাকিং, কার্বোহাইড্রেট তৈরি ও লাইসোজোম গঠনে অংশ নেয়।",
                 "Cisternae • Secretion • Packaging • Lysosome Formation"},
                {"Differentiate between rough and smooth Endoplasmic Reticulum.",
                 "অমসৃণ ও মসৃণ এন্ডোপ্লাজমিক রেটিকুলামের মধ্যে পার্থক্য কী?",
                 "Rough ER is studded with ribosomes on outer surface and synthesizes proteins; Smooth ER lacks ribosomes and synthesizes lipids, steroids, and detoxifies chemicals.",
                 "অমসৃণ ER-এ রাইবোজোম যুক্ত থাকে এবং এটি প্রোটিন তৈরি করে; মসৃণ ER-এ রাইবোজোম থাকে না এবং এটি লিপিড ও স্টেরয়েড সংশ্লেষ করে।",
                 "Rough ER • Smooth ER • Ribosomes • Lipid Synthesis"},
                {"What is the composition and role of the plant Cell Wall?",
                 "উদ্ভিদ কোষ প্রাচীরের উপাদান ও কাজ কী?",
                 "Composed of middle lamella (pectic acid), primary wall (cellulose, hemicellulose), and secondary wall (lignin, suberin). Provides mechanical strength, shape, and protection.",
                 "মধ্যপর্দা (পেকটিক অ্যাসিড), প্রাথমিক প্রাচীর (সেলুলোজ, হেমিসেলুলোজ) ও গৌণ প্রাচীর (লিগনিন) নিয়ে গঠিত। এটি কোষের দৃঢ়তা, আকৃতি ও সুরক্ষা দেয়।",
                 "Cellulose • Pectin • Lignin • Secondary Wall • Plasmodesmata"},
                {"Describe the structure of eukaryotic Nucleus.",
                 "ইউক্যারিওটিক নিউক্লিয়াসের গঠন বর্ণনা করো।",
                 "Comprises Nuclear Envelope with pores, Nucleoplasm (karyolymph), dense Nucleolus (rRNA assembly), and Chromatin reticulum containing DNA and histone proteins.",
                 "নিউক্লিয়ার মেমব্রেন (রন্ধ্রযুক্ত), নিউক্লিওপ্লাজম, ঘন নিউক্লিওলাস (rRNA গঠনকারী) এবং ডিএনএ-হিস্টোন প্রোটিনযুক্ত ক্রোমাটিন জালিকা নিয়ে গঠিত।",
                 "Nuclear Pore • Nucleolus • Chromatin • Histones • DNA"},
                {"What are Lysosomes and why are they called suicidal bags?",
                 "লাইসোজোম কী এবং একে আত্মঘাতী থলিকা বলা হয় কেন?",
                 "Membrane-bound organelles containing ~50 hydrolytic acid enzymes. In starvation or cell injury, they burst and digest their own cell organelles via autolysis.",
                 "একক পর্দাবেষ্টিত অঙ্গাণু যাতে প্রায় ৫০ ধরনের হাইড্রোলাইটিক এনজাইম থাকে। প্রতিকূল অবস্থায় ফেটে গিয়ে অটোলাইসিস প্রক্রিয়ায় নিজস্ব কোষ অঙ্গাণু ধ্বংস করে।",
                 "Hydrolytic Enzymes • Autolysis • Phagocytosis • Suicidal Bag"},
                {"Explain the Watson-Crick double helix model of DNA.",
                 "ডিএনএ-র ওয়াটসন-ক্রিক ডাবল হেলিক্স মডেল ব্যাখ্যা করো।",
                 "Right-handed double helix antiparallel chains (5' to 3' and 3' to 5'). Sugar-phosphate backbone with nitrogenous bases inside paired by hydrogen bonds (A=T with 2, G≡C with 3). Diameter 2 nm, 1 complete turn = 3.4 nm (10 base pairs).",
                 "ডানাবর্তী দ্বি-সূত্রক সমান্তরাল বিপরীতমুখী চেইন (5'→3' ও 3'→5')। ফসফো-ডিঅক্সিরাইবোজ মেরুদণ্ড ও হাইড্রোজেন বন্ধনীযুক্ত ক্ষারক (A=T দুটি, G≡C তিনটি)। ব্যাস ২ ন্যানোমিটার, প্রতি ঘূর্ণন ৩.৪ ন্যানোমিটার (১০টি ক্ষারক জোড়)।",
                 "Double Helix • Antiparallel • Hydrogen Bonds • 3.4 nm • Complementary"}
        });

        // Chapter 2: Cell Division (কোষ বিভাজন)
        addQuestions("botany_ch02", "Chapter 2: Cell Division", new String[][]{
                {"What happens during the S-phase of Interphase?",
                 "ইন্টারফেজের S-দশায় কী ঘটে?",
                 "DNA replication takes place, doubling DNA content from 2C to 4C, while chromosome number remains constant (2n). Centrosome duplication also initiates.",
                 "ডিএনএ অনুলিপন ঘটে ফলে ডিএনএ এর পরিমাণ দ্বিগুণ (2C থেকে 4C) হয়, কিন্তু ক্রোমোজোম সংখ্যা অপরিবর্তিত থাকে। সেন্ট্রোজোম বিভাজনও শুরু হয়।",
                 "DNA Replication • Interphase • 2C to 4C • Centrosome"},
                {"What are the 5 sub-stages of Prophase-I in Meiosis?",
                 "মায়োসিস-১ এর প্রোফেজ-১ এর পাঁচটি উপ-পর্যায় কী কী?",
                 "Leptotene, Zygotene (synapsis), Pachytene (crossing over & tetrads), Diplotene (chiasmata & terminalization), and Diakinesis (nuclear membrane disappears).",
                 "লেপ্টোটিন, জাইগোটিন (সিন্যাপসিস), প্যাকাইটিন (ক্রসিং ওভার ও টেট্রাড), ডিপ্লোটিন (কায়াজমা ও প্রান্তীয়করণ) এবং ডায়াকাইনেসিস।",
                 "Leptotene • Zygotene • Pachytene • Diplotene • Diakinesis"},
                {"Explain the mechanism and importance of Crossing Over.",
                 "ক্রসিং ওভারের কৌশল ও গুরুত্ব ব্যাখ্যা করো।",
                 "Exchange of non-sister chromatid segments between homologous chromosomes in pachytene via endonuclease, ligase enzymes. Produces genetic variation and new recombinant traits.",
                 "প্যাকাইটিন উপ-পর্যায়ে হোমোলোগাস ক্রোমোজোমের নন-সিস্টার ক্রোমাটিডের মধ্যে খণ্ডাংশের বিনিময়। এন্ডোনিউক্লিয়েজ ও লাইগেজ এনজাইমের সাহায্যে ঘটে এবং নতুন জিনগত প্রকরণ সৃষ্টি করে।",
                 "Crossing Over • Pachytene • Non-sister Chromatids • Chiasma • Recombination"},
                {"Differentiate between Mitosis and Meiosis.",
                 "মাইটোটিক ও মায়োটিক কোষ বিভাজনের পার্থক্য কী?",
                 "Mitosis: somatic cells, 1 division, produces 2 diploid genetically identical daughter cells. Meiosis: germ cells, 2 successive divisions, produces 4 haploid genetically diverse daughter cells.",
                 "মাইটোটিক: দেহকোষে ঘটে, ১ বার বিভক্ত হয়ে ২টি হুবহু সমান অপত্য কোষ সৃষ্টি করে। মায়োটিক: জনন মাতৃকোষে ঘটে, ২ বার বিভক্ত হয়ে ৪টি হ্যাপ্লয়েড ভিন্নধর্মী কোষ সৃষ্টি করে।",
                 "Equational • Reductional • Somatic • Germ Cells • Haploid"},
                {"Describe Anaphase stage of Mitosis.",
                 "মাইটোটিক অ্যানাফেজ পর্যায়ের বৈশিষ্ট্য বর্ণনা করো।",
                 "Centromeres divide, sister chromatids separate into daughter chromosomes and migrate toward opposite poles pulled by spindle traction fibers. Chromosomes appear V (metacentric), L (submetacentric), J (acrocentric), or I (telocentric) shaped.",
                 "সেন্ট্রোমিয়ার বিভক্ত হয়, ক্রোমাটিডদ্বয় অপত্য ক্রোমোজোমে পরিণত হয়ে বিপরীত মেরুর দিকে ধাবিত হয়। ক্রোমোজোমগুলো V, L, J, I আকৃতির মতো দেখায়।",
                 "Anaphase • Centromere Cleavage • Poleward Migration • V/L/J/I Shapes"},
                {"What is Cytokinesis and how does it occur in plant vs animal cells?",
                 "সাইটোকাইনেসিস কী এবং উদ্ভিদ ও প্রাণীকোষে এটি কীভাবে ঘটে?",
                 "Division of cytoplasm following karyokinesis. In plant cells, phragmoplast vesicles coalesce to form cell plate. In animal cells, actin-myosin contractile ring forms a cleavage furrow pinching inward.",
                 "ক্যারিওকাইনেসিস পরবর্তী সাইটোপ্লাজমের বিভাজন। উদ্ভিদকোষে ফ্র্যাগমোপ্লাস্ট ভেসিকল মিলে কোষপ্লেট তৈরি করে; প্রাণীকোষে অ্যাক্টিন-মায়োসিন বলয় ক্লিভেজ ফারো সৃষ্টি করে।",
                 "Cell Plate • Phragmoplast • Cleavage Furrow • Contractile Ring"},
                {"Explain the checkpoints of Cell Cycle regulation.",
                 "কোষ চক্রের চেকপয়েন্টগুলো ব্যাখ্যা করো।",
                 "Three major checkpoints: G1/S (checks DNA damage & cell size), G2/M (checks complete DNA replication), and M-spindle checkpoint (checks chromosome attachment to spindle). Regulated by Cyclin and CDKs.",
                 "তিনটি প্রধান চেকপয়েন্ট: G1/S (ডিএনএ ক্ষতি ও আকার যাচাই), G2/M (সম্পূর্ণ ডিএনএ অনুলিপন যাচাই), এবং M-স্পিন্ডল (কাইনেটোকোরে স্পিন্ডল সংযুক্তি)। সাইক্লিন ও CDK দ্বারা নিয়ন্ত্রিত।",
                 "Checkpoints • Cyclin • CDK • G1/S • Spindle Checkpoint"},
                {"What is Synapsis and Bivalent formation?",
                 "সিন্যাপসিস ও বাইভ্যালেন্ট গঠন কী?",
                 "During Zygotene of Prophase-I, homologous maternal and paternal chromosomes align side by side in pairs (synapsis) mediated by synaptonemal complex, forming a bivalent.",
                 "প্রোফেজ-১ এর জাইগোটিনে সমসংস্থ মাতৃ ও পিতৃ ক্রোমোজোমের জোড় বাঁধার প্রক্রিয়া হলো সিন্যাপসিস। এই ক্রোমোজোম জোড়াকে বাইভ্যালেন্ট বলে।",
                 "Synapsis • Zygotene • Homologous Chromosomes • Bivalent"},
                {"What is Amitosis (Direct Cell Division)?",
                 "অ্যামাইটোসিস (প্রত্যক্ষ কোষ বিভাজন) কী?",
                 "Primitive cell division in prokaryotes like bacteria and yeast where nucleus directly elongates and constricts in the middle without spindle formation or chromosome condensation.",
                 "আদিকোষী জীব যেমন ব্যাকটেরিয়া ও ইস্টের বিভাজন যেখানে স্পিন্ডল যন্ত্র তৈরি ছাড়াই নিউক্লিয়াস সরাসরি ডাম্বেল আকার ধারণ করে মাঝখানে সংকুচিত হয়ে দুটি অংশে বিভক্ত হয়।",
                 "Direct Division • Binary Fission • Bacteria • No Spindle"},
                {"What are the consequences of uncontrolled Mitosis?",
                 "অনিয়ন্ত্রিত মাইটোসিস কোষ বিভাজনের ফলাফল কী?",
                 "Breakdown of cell cycle checkpoints due to oncogene activation or tumor suppressor (p53) mutation leads to abnormal cell proliferation, forming benign or malignant tumors (cancer).",
                 "চেকপয়েন্ট নষ্ট হওয়া বা p53 জিন পরিবর্তনের ফলে অনিয়ন্ত্রিত কোষ বিভাজনে টিউমার এবং ম্যালিগন্যান্ট রূপ নিয়ে ক্যান্সার সৃষ্টি হয়।",
                 "Oncogene • Tumor • p53 • Carcinogenesis • Malignancy"}
        });

        // Chapter 3: Cell Chemistry (কোষ রসায়ন)
        addQuestions("botany_ch03", "Chapter 3: Cell Chemistry", new String[][]{
                {"Define Reducing vs Non-reducing sugars with examples.",
                 "উদাহরণসহ বিজারক ও অবিজারক শর্করার সংজ্ঞা দাও।",
                 "Reducing sugars possess a free aldehyde (-CHO) or ketone (-C=O) group that reduces Benedict's/Fehling's solution (e.g., Glucose, Fructose, Maltose). Non-reducing sugars lack free functional groups (e.g., Sucrose).",
                 "যেসব শর্করার মুক্ত অ্যালডিহাইড বা কিটোন মূলক থাকে এবং বেনেডিক্ট দ্রবণ বিজারিত করতে পারে তাদের বিজারক চিনি বলে (গ্লুকোজ, ফ্রুক্টোজ)। সুক্রোজে মুক্ত মূলক থাকে না বলে তা অবিজারক।",
                 "Reducing Sugar • Free Aldehyde • Benedict Test • Sucrose • Glucose"},
                {"What are the four levels of Protein structure?",
                 "প্রোটিনের চারটি গঠনগত স্তর কী কী?",
                 "1. Primary: Linear amino acid sequence with peptide bonds.\n2. Secondary: Alpha-helix and beta-pleated sheets stabilized by hydrogen bonds.\n3. Tertiary: 3D folding with disulfide/ionic/hydrophobic bonds.\n4. Quaternary: Multi-subunit association (e.g., Hemoglobin).",
                 "১. প্রাথমিক: পেপটাইড বন্ধনযুক্ত সরল শিকল।\n২. মাধ্যমিক: আলফা-হিলিক্স ও বিটা-প্লিটেড শিট।\n৩. টারশিয়ারি: ত্রিমাত্রিক ভাঁজ (ডাইসালফাইড বন্ধনীযুক্ত)।\n৪. কোয়ার্টারনারি: একাধিক পলিপেপটাইড সাবইউনিটের সমষ্টি (যেমন হিমোগ্লোবিন)।",
                 "Primary • Alpha Helix • Beta Sheet • Tertiary • Quaternary"},
                {"Explain the Lock and Key vs Induced Fit hypothesis of Enzymes.",
                 "এনজাইমের তালা-চাবি ও আবেশিত মডেল ব্যাখ্যা করো।",
                 "Fischer's Lock & Key model states the enzyme active site is rigid and pre-shaped for substrate. Koshland's Induced Fit model states active site undergoes conformational realignment upon substrate binding.",
                 "ফিশারের তালা-চাবি মডেলে সক্রিয় স্থানটি পূর্বনির্ধারিত কাঠামোর। কোশল্যান্ডের ইনডিউসড ফিট মডেলে সাবস্ট্রেট যুক্ত হলে সক্রিয় স্থানের আকৃতিতে নমনীয় পরিবর্তন ঘটে।",
                 "Active Site • Fischer Lock & Key • Koshland Induced Fit • Substrate"},
                {"What is the difference between Saturated and Unsaturated Fatty Acids?",
                 "সম্পৃক্ত ও অসম্পৃক্ত ফ্যাটি অ্যাসিডের মধ্যে পার্থক্য কী?",
                 "Saturated fatty acids contain only single C-C bonds, solid at room temperature (e.g., Palmitic acid). Unsaturated fatty acids have one or more C=C double bonds, liquid at room temperature (e.g., Oleic acid).",
                 "সম্পৃক্ত ফ্যাটি অ্যাসিডে কার্বন-কার্বন একক বন্ধন থাকে, সাধারণ তাপমাত্রায় কঠিন (যেমন পামিটিক অ্যাসিড)। অসম্পৃক্ত ফ্যাটি অ্যাসিডে এক বা একাধিক দ্বিবন্ধন থাকে, তরল (যেমন ওলিক অ্যাসিড)।",
                 "Saturated • Unsaturated • Double Bond • Palmitic Acid • Oleic Acid"},
                {"What is a Peptide Bond and how is it synthesized?",
                 "পেপটাইড বন্ধন কী এবং এটি কীভাবে গঠিত হয়?",
                 "Covalent amide bond (-CO-NH-) formed by condensation reaction between alpha-carboxyl group of one amino acid and alpha-amino group of another, releasing one molecule of water.",
                 "একটি অ্যামিনো অ্যাসিডের কার্বক্সিল মূলক (-COOH) এবং অপরটির অ্যামিনো মূলকের (-NH2) মধ্যে পানি অপসারিত হয়ে গঠিত সমযোজী অ্যামাইড বন্ধনী (-CO-NH-)।",
                 "Peptide Bond • Condensation • Amide Linkage • Dehydration"},
                {"Describe the chemical components of a Nucleotide.",
                 "একটি নিউক্লিওটাইডের রাসায়নিক উপাদান বর্ণনা করো।",
                 "Composed of three units: 1. Pentose sugar (Ribose in RNA or 2-Deoxyribose in DNA), 2. Nitrogenous base (Purines: A, G; Pyrimidines: C, T, U), and 3. Phosphate group (-PO4).",
                 "তিনটি উপাদান: ১. পেন্টোজ শর্করা (রাইবোজ বা ডিঅক্সিরাইবোজ), ২. নাইট্রোজেন ক্ষারক (পিউরিন: A, G; পিরিমিডিন: C, T, U) এবং ৩. অজৈব ফসফেট গ্রুপ।",
                 "Pentose Sugar • Nitrogen Base • Phosphate • Purine • Pyrimidine"},
                {"Explain Coenzymes, Cofactors, and Prosthetic Groups.",
                 "কো-এনজাইম, কো-ফ্যাক্টর ও প্রস্থেটিক গ্রুপের মধ্যে সম্পর্ক কী?",
                 "Cofactor: non-protein chemical component of an enzyme. If organic and loosely bound, it is a Coenzyme (NAD+, FAD). If non-protein group is tightly/covalently bound, it is a Prosthetic Group (Heme). Holoenzyme = Apoenzyme + Cofactor.",
                 "কোফ্যাক্টর হলো এনজাইমের অপ্রোটিন অংশ। যদি জৈব ও শিথিলভাবে যুক্ত থাকে তবে কো-এনজাইম (NAD+)। আর শক্তভাবে আবদ্ধ থাকলে তা প্রস্থেটিক গ্রুপ (হিম)। সম্পূর্ণ সক্রিয় এনজাইম হলো হোলো-এনজাইম।",
                 "Apoenzyme • Holoenzyme • Coenzyme • Prosthetic Group • Cofactor"},
                {"What are Essential Amino Acids? Name four of them.",
                 "অত্যাবশ্যকীয় অ্যামিনো অ্যাসিড কী? চারটির নাম লেখ।",
                 "Amino acids that cannot be synthesized by human body and must be supplied through dietary intake. Total 8-10. Examples: Lysine, Tryptophan, Methionine, Leucine.",
                 "যেসব অ্যামিনো অ্যাসিড মানবদেহে তৈরি হয় না এবং খাদ্যের মাধ্যমে গ্রহণ করা আবশ্যক। উদাহরণ: লাইসিন, ট্রিপটোফ্যান, মিথিওনিন, লিউসিন।",
                 "Essential Amino Acids • Lysine • Tryptophan • Methionine • Diet"},
                {"Explain Competitive vs Non-competitive Enzyme Inhibition.",
                 "প্রতিযোগিতামূলক ও অপ্রতিযোগিতামূলক এনজাইম নিবারকের পার্থক্য কী?",
                 "Competitive inhibitor resembles substrate structure and binds directly to active site (reversible by increasing substrate). Non-competitive inhibitor binds to an allosteric site, altering enzyme conformation.",
                 "প্রতিযোগিতামূলক ইনহিবিটর সাবস্ট্রেটের অনুরূপ হয়ে সরাসরি সক্রিয় স্থানে আবদ্ধ হয়। অপ্রতিযোগিতামূলক ইনহিবিটর অ্যালোস্টেরিক স্থানে যুক্ত হয়ে এনজাইমের গঠন বিকৃত করে।",
                 "Competitive Inhibition • Allosteric Site • Substrate Analogue • Vmax"},
                {"What is the biochemical structure and significance of ATP?",
                 "এটিপি (ATP)-র রাসায়নিক গঠন ও জৈবিক গুরুত্ব কী?",
                 "Adenosine Triphosphate: Adenine + Ribose + 3 phosphate groups linked by two high-energy phosphoanhydride bonds (~7.3 kcal/mol each). Serves as universal biological energy currency.",
                 "অ্যাডেনোসিন ট্রাইফসফেট: অ্যাডেনিন + রাইবোজ + ৩টি ফসফেট গ্রুপ যাতে দুটি উচ্চ-শক্তি ফসফোঅ্যানহাইড্রাইড বন্ধন থাকে (প্রতিটিতে ~৭.৩ কিলোক্যালরি শক্তি)। এটি কোষের জৈব মুদ্রা।",
                 "ATP • Phosphoanhydride • Energy Currency • Hydrolysis • Adenosine"}
        });

        // Chapter 4: Microorganisms (অণুজীব)
        addQuestions("botany_ch04", "Chapter 4: Microorganisms", new String[][]{
                {"Explain the structure of T2 Bacteriophage.",
                 "T2 ব্যাকটেরিওফাজের গঠন বর্ণনা করো।",
                 "Tadpole-shaped virus with hexagonal Head (containing double-stranded linear DNA) and cylindrical Tail (sheath, core tube, collar, basal plate, spikes, and 6 tail fibers for host attachment).",
                 "ব্যাঙাচি আকৃতির ভাইরাস। ষড়ভুজাকার মস্তক (দ্বি-সূত্রক সরল রৈখিক ডিএনএ যুক্ত) এবং কলার, কলারবিহীন লেজ (প্রোটিন আবরণী, স্পর্শক তন্তু ও সূঁচালো স্পাইকযুক্ত বেসাল প্লেট)।",
                 "Bacteriophage • Hexagonal Head • dsDNA • Tail Fibers • Basal Plate"},
                {"Describe the Lytic cycle of T2 Phage reproduction.",
                 "T2 ফাজের লাইটিক চক্র বর্ণনা করো।",
                 "Five stages: 1. Adsorption/Attachment to host, 2. Penetration (DNA injection), 3. Synthesis of viral DNA & capsid proteins, 4. Assembly/Maturation of new virions, 5. Lysis and release via lysozyme.",
                 "পাঁচটি ধাপ: ১. পৃষ্ঠলগ্ন হওয়া (অ্যাডসর্বশন), ২. অনুপ্রবেশ (ডিএনএ প্রবেশ), ৩. রেপ্লিকেশন ও প্রোটিন সংশ্লেষণ, ৪. নতুন ভাইরাস সংযোজন, ৫. লাইসোজাইম দ্বারা ব্যাকটেরিয়ার বিদারণ ও মুক্তি।",
                 "Lytic Cycle • Adsorption • Penetration • Eclipse Phase • Lysis"},
                {"What is the causal agent, vector, and symptom of Malaria?",
                 "ম্যালেরিয়ার জীবাণু, বাহক ও লক্ষণ কী?",
                 "Causative agent: Plasmodium (e.g. P. vivax, P. falciparum). Vector: Female Anopheles mosquito. Symptoms: Recurring fever with rigors, sweating, chills, anemia, and splenomegaly.",
                 "জীবাণু: প্লাজমোডিয়াম (Plasmodium vivax/falciparum)। বাহক: স্ত্রী অ্যানোফিলিস মশা। লক্ষণ: নির্দিষ্ট বিরতিতে কাঁপুনি দিয়ে তীব্র জ্বর, ঘাম দিয়ে জ্বর ছাড়া, রক্তস্বল্পতা ও প্লীহা বৃদ্ধি।",
                 "Plasmodium • Female Anopheles • Schizogony • Paroxysm • Chills"},
                {"Explain the difference between Gram-positive and Gram-negative bacteria.",
                 "গ্রাম-পজিটিভ ও গ্রাম-নেগেটিভ ব্যাকটেরিয়ার পার্থক্য কী?",
                 "Gram-positive: thick multilayered peptidoglycan cell wall (80%), contains teichoic acid, retains crystal violet (purple). Gram-negative: thin peptidoglycan (10-20%), outer LPS membrane, stains pink with safranin.",
                 "গ্রাম-পজিটিভ: পুরু পেপটিডোগ্লাইকান স্তর, টাইকোয়িক অ্যাসিডযুক্ত, ক্রিস্টাল ভায়োলেট ধরে রাখে (বেগুনী)। গ্রাম-নেগেটিভ: পাতলা পেপটিডোগ্লাইকান, বহিস্থ এলপিএস স্তরযুক্ত, সাফ্রানিনে গোলাপী দেখায়।",
                 "Gram Stain • Peptidoglycan • LPS • Crystal Violet • Teichoic Acid"},
                {"What is a Retrovirus? Why is HIV called a retrovirus?",
                 "রেট্রোভাইরাস কী? এইচআইভি-কে রেট্রোভাইরাস বলা হয় কেন?",
                 "RNA virus that possesses Reverse Transcriptase enzyme. HIV carries single-stranded RNA which is reverse-transcribed into double-stranded DNA inside the host cell.",
                 "যেসব আরএনএ ভাইরাসে রিভার্স ট্রান্সক্রিপ্টেজ এনজাইম থাকে। এইচআইভি পোষক কোষে প্রবেশ করে আরএনএ থেকে পরিপূরক ডিএনএ তৈরি করে বলে একে রেট্রোভাইরাস বলে।",
                 "Retrovirus • HIV • Reverse Transcriptase • RNA to DNA • Provirus"},
                {"What is Prion and Viroid?",
                 "প্রিয়ন ও ভিরয়েড কী?",
                 "Prion: infectious, misfolded protein particle devoid of nucleic acid (causes Mad Cow disease). Viroid: smallest infectious agent consisting purely of naked circular single-stranded RNA without protein coat.",
                 "প্রিয়ন: সংক্রামক প্রোটিন কণা যাতে নিউক্লিক অ্যাসিড থাকে না (ম্যাড কাউ রোগ সৃষ্টি করে)। ভিরয়েড: প্রোটিন আবরণহীন ক্ষুদ্রাতিক্ষুদ্র একক সূত্রক সংক্রামক বৃত্তাকার আরএনএ।",
                 "Prion • Viroid • Naked RNA • Protein Particle • Mad Cow"},
                {"Explain the asexual binary fission in bacteria.",
                 "ব্যাকটেরিয়ার দ্বি-ভাজন প্রক্রিয়া ব্যাখ্যা করো।",
                 "Under favorable conditions, circular bacterial DNA replicates and attaches to mesosome; cell elongates, invagination of plasma membrane and peptidoglycan synthesis produces two identical daughter cells in 20 mins.",
                 "অনুকূল পরিবেশে মেসোজোমে যুক্ত বৃত্তাকার ডিএনএ অনুলিপিত হয়, কোষ লম্বা হয় এবং প্লাজমা পর্দা ও প্রাচীর ভেতরের দিকে বৃদ্ধি পেয়ে সমান দুটি অপত্য কোষ তৈরি করে।",
                 "Binary Fission • Mesosome • Circular DNA • Exponential Growth"},
                {"Describe the Dengue virus: transmission, serotypes, and prevention.",
                 "ডেঙ্গু ভাইরাস: সংক্রমণ, সেরোটাইপ ও প্রতিকার বর্ণনা করো।",
                 "Flavivirus (ssRNA virus, serotypes DEN-1 to DEN-4), transmitted by female Aedes aegypti mosquito. Causes dengue fever/DHF; prevention includes vector mosquito control and eliminating stagnant water.",
                 "ফ্ল্যাভিভাইরাস (সেরোটাইপ DEN-1 থেকে 4), স্ত্রী এডিস ইজিপ্টি মশার কামড়ে ছড়ায়। লক্ষণ রক্তপাত ও তীব্র জ্বর; প্রতিকার এডিস মশার বিস্তার রোধ ও জমে থাকা পানি পরিষ্কার করা।",
                 "Dengue • Aedes Aegypti • Flavivirus • Platelet Drop • Serotypes"},
                {"What is Endospore formation in bacteria?",
                 "ব্যাকটেরিয়ার এন্ডোস্পোর (অন্তঃরেণু) গঠন কী?",
                 "Highly resistant dormant structure formed inside bacterial cells (e.g. Bacillus, Clostridium) during unfavorable environmental stress; withstands heat, desiccation, and radiation due to dipicolinic acid and calcium.",
                 "প্রতিকূল পরিবেশে ব্যাসিলাস বা ক্লস্ট্রিডিয়াম ব্যাকটেরিয়ায় তৈরি হওয়া চরম তাপ ও শুষ্কতা প্রতিরোধী সুপ্ত রেণু। এতে ক্যালসিয়াম ডিপিকোলিনেট প্রাচীর থাকে।",
                 "Endospore • Dipicolinic Acid • Bacillus • Dormancy • Heat Resistance"},
                {"How do antibiotics kill bacteria without harming human host cells?",
                 "অ্যান্টিবায়োটিক মানবদেহের ক্ষতি না করে কীভাবে ব্যাকটেরিয়া ধ্বংস করে?",
                 "Target specific prokaryotic biochemical features absent in humans: e.g., Penicillin inhibits peptidoglycan wall synthesis, Streptomycin inhibits 70S bacterial ribosomes.",
                 "ব্যাকটেরিয়ার এমন অনন্য বৈশিষ্ট্যকে আক্রমণ করে যা মানবদেহে অনুপস্থিত: যেমন পেনিসিলিন পেপটিডোগ্লাইকান প্রাচীর গঠন বন্ধ করে, স্ট্রেপ্টোমাইসিন ৭০S রাইবোজোমে প্রোটিন সংশ্লেষণ রোধ করে।",
                 "Antibiotic • Peptidoglycan • 70S Target • Penicillin • Selective Toxicity"}
        });

        // Chapters 5 to 12 Botany and Chapters 1 to 12 Zoology: populate high yield templates
        populateRemainingBotanyChapters();
        populateZoologyChapters();
    }

    private static void populateRemainingBotanyChapters() {
        // Ch 05: Algae and Fungi
        addQuestions("botany_ch05", "Chapter 5: Algae and Fungi", new String[][]{
                {"Explain the structure and characteristics of Spirogyra.",
                 "স্পাইরোগাইরার গঠন ও বৈশিষ্ট্য ব্যাখ্যা করো।",
                 "Unbranched filamentous green alga with ribbon-shaped spiral chloroplast containing pyrenoid bodies. Undergoes scalariform and lateral conjugation.",
                 "অশাখ ফিলামেন্টাস সবুজ শৈবাল যাতে সর্পিলাকার ফিতার মতো ক্লোরোপ্লাস্ট ও পাইরিনয়েড দানা থাকে। মইরূপ ও পার্শ্বীয় কনজুগেশন দেখায়।",
                 "Spirogyra • Spiral Chloroplast • Pyrenoid • Scalariform Conjugation"},
                {"Describe the structure of Agaricus (Mushroom).",
                 "অ্যাগারিকাসের (মাশরুম) গঠন বর্ণনা করো।",
                 "Basidiomycete fungus having subterranean vegetative mycelium and umbrella-shaped fruiting body (basidiocarp) consisting of stipe, annulus, pileus, and radiating gills.",
                 "ছাতার মতো ব্যাসিডিওকার্প বিশিষ্ট ছত্রাক। স্টাইপ, অ্যানুলাস, পাইলিয়াস এবং তলার দিকে ঝুলন্ত অসংখ্য গিল বা ল্যামেলা নিয়ে গঠিত।",
                 "Agaricus • Basidiocarp • Pileus • Stipe • Gills • Basidiospores"},
                {"What is Lichen and what is its ecological significance?",
                 "লাইকেন কী এবং এর বাস্তুতান্ত্রিক গুরুত্ব কী?",
                 "Symbiotic association between an alga (phycobiont: photosynthesizer) and a fungus (mycobiont: moisture & mineral absorber). Acts as ecological pioneer in primary succession and air pollution bioindicator.",
                 "শৈবাল (ফাইকোবায়োন্ট) ও ছত্রাকের (মাইকোবায়োন্ট) মিথোজীবী সহাবস্থান। বায়ুদূষণের সূচক এবং উদ্ভিদ পর্যায়ক্রমের অগ্রগামী জীব হিসেবে কাজ করে।",
                 "Lichen • Symbiosis • Phycobiont • Mycobiont • Bioindicator"},
                {"Differentiate between Algae and Fungi.",
                 "শৈবাল ও ছত্রাকের মধ্যে মূল পার্থক্য কী?",
                 "Algae have chlorophyll, autotrophic, cellulosic cell wall, store starch. Fungi lack chlorophyll, heterotrophic, chitinous cell wall, store glycogen and oil droplets.",
                 "শৈবালে ক্লোরোফিল থাকে, স্বভোজী, প্রাচীর সেলুলোজযুক্ত, শ্বেতসার সঞ্চয় করে। ছত্রাক পরভোজী, কাইটিন প্রাচীরযুক্ত, গ্লাইকোজেন ও তেলবিন্দু সঞ্চয় করে।",
                 "Autotroph • Heterotroph • Chitin • Cellulose • Chlorophyll"},
                {"What is Late Blight of Potato and its causal fungus?",
                 "আলুর বিলম্বিত ধসা রোগ ও এর জীবাণুর নাম কী?",
                 "Destructive plant disease caused by the oomycete fungus Phytophthora infestans; causes brown water-soaked lesions on leaves and rotting tubers.",
                 "ফাইটোফথোরা ইনফেসট্যান্স নামক ছত্রাকঘটিত রোগ। এর আক্রমণে পাতায় বাদামি দাগ ও আলু দ্রুত পচে নষ্ট হয়ে যায় (আইরিশ দুর্ভিক্ষের কারণ)।",
                 "Phytophthora Infestans • Late Blight • Haustorium • Potato Disease"},
                {"Explain the economic importance of Yeast (Saccharomyces cerevisiae).",
                 "ইস্টের অর্থনৈতিক গুরুত্ব আলোচনা করো।",
                 "Unicellular ascomycete fungus used in bakery (CO2 causes bread dough to rise) and brewery (ferments sugars into ethanol using zymase enzyme complex).",
                 "বেকারি শিল্পে পাউরুটি ফোলাতে এবং মদ শিল্পে জাইমেজ এনজাইমের মাধ্যমে গ্লুকোজ গেঁজিয়ে অ্যালকোহল তৈরিতে ব্যাপকভাবে ব্যবহৃত হয়।",
                 "Yeast • Fermentation • Zymase • Ethanol • Bakery"},
                {"What are Mycorrhizae? Explain endo- and ectomycorrhizae.",
                 "মাইকোরাইজা কী? অন্তঃ ও বহিঃ মাইকোরাইজা ব্যাখ্যা করো।",
                 "Mutualistic association between plant roots and fungal hyphae. Ectomycorrhizae form mantle around root cells; Endomycorrhizae (VAM) penetrate cortical cells forming arbuscules.",
                 "উচ্চশ্রেণির উদ্ভিদের মূল ও ছত্রাকের মিথোজীবী সম্পর্ক। এক্টো-মাইকোরাইজা মূলের বাইরে ম্যান্টেল গঠন করে; এন্ডো-মাইকোরাইজা কর্টেক্স কোষে প্রবেশ করে আরবাসকিউল তৈরি করে।",
                 "Mycorrhiza • Mutualism • VAM • Arbuscules • Phosphorus Uptake"},
                {"What are the types of sexual reproduction seen in Algae?",
                 "শৈবালের যৌন জননের প্রকারভেদ কী কী?",
                 "1. Isogamy (morphologically identical gametes), 2. Anisogamy (motile gametes differing in size), 3. Oogamy (large non-motile egg fertilized by small motile sperm).",
                 "১. আইসোগ্যামি (আকার-আকৃতিতে একই), ২. অ্যানাইসোগ্যামি (ভিন্ন আকারের সচল গ্যামেট), ৩. উওগ্যামি (বড় নিশ্চল ডিম্বাণু ও ছোট সচল শুক্রাণু)।",
                 "Isogamy • Anisogamy • Oogamy • Gamete Fusion"},
                {"What are harmful effects of Fungi in humans and agriculture?",
                 "মানুষ ও কৃষিতে ছত্রাকের ক্ষতিকর দিক কী কী?",
                 "Causes human diseases (ringworm, aspergillosis, athlete's foot), produces mycotoxins (Aflatoxin by Aspergillus), and causes crop rusts/smuts destroying agricultural yields.",
                 "মানুষে দাদ, অ্যাসপারজিলোসিস ঘটায়; আফলাটক্সিন নামক বিষাক্ত পদার্থ তৈরি করে এবং ফসলে মরিচা ও ভুষি রোগ সৃষ্টি করে।",
                 "Aflatoxin • Ringworm • Smut • Rust • Mycosis"},
                {"What is Red Tide and what causes it?",
                 "রেড টাইড (লাল জোয়ার) কী এবং এটি কী কারণে ঘটে?",
                 "Harmful algal bloom caused by rapid multiplication of marine dinoflagellates (e.g. Gymnodinium), releasing brevetoxins toxic to fish and marine life.",
                 "ডাইনোফ্ল্যাজেলেট শৈবালের অত্যধিক বৃদ্ধির ফলে সমুদ্রের পানি লালচে বর্ণ ধারণ করে এবং নিঃসৃত টক্সিন মাছ ও সামুদ্রিক প্রাণীর মৃত্যু ঘটায়।",
                 "Red Tide • Dinoflagellates • Algal Bloom • Toxin • Marine Life"}
        });

        // Ch 06 to 12 Botany: populate structured questions
        populateBotany6to12();
    }

    private static void populateBotany6to12() {
        addGenericChapterQuestions("botany_ch06", "Chapter 6: Bryophyta and Pteridophyta", "Bryophytes and Pteridophytes",
                new String[]{"Riccia thallus structure", "Pteris fern prothallus", "Alternation of generations", "Amphibian plant nature of Bryophytes", "Stele types in Pteridophytes", "Sporophyte vs Gametophyte", "Rhizoids in Riccia", "Circinate vernation in ferns", "Archegonium and Antheridium", "Economic importance of Ferns"});

        addGenericChapterQuestions("botany_ch07", "Chapter 7: Gymnosperms and Angiosperms", "Gymnosperms and Angiosperms",
                new String[]{"Characteristics of Cycas", "Coralloid root of Cycas", "Double fertilization in Angiosperms", "Poaceae family floral formula", "Malvaceae family floral formula", "Naked seed nature of Gymnosperms", "Megasporophyll in Cycas", "Monocotyledonous vs Dicotyledonous stem", "Aestivation types", "Placentation types in angiosperms"});

        addGenericChapterQuestions("botany_ch08", "Chapter 8: Tissue and Tissue System", "Tissue and Tissue System",
                new String[]{"Meristematic tissue classification", "Vascular bundle types (Radial, Conjoint, Concentric)", "Stomatal apparatus structure", "Epidermal appendages", "Ground tissue system", "Anatomy of Dicot stem", "Anatomy of Monocot stem", "Anatomy of Dicot root", "Bulliform cells in grasses", "Hydathode and guttation"});

        addGenericChapterQuestions("botany_ch09", "Chapter 9: Plant Physiology", "Plant Physiology",
                new String[]{"Calvin cycle (C3 pathway)", "Hatch-Slack pathway (C4 pathway)", "Light-dependent reaction and photolysis", "Glycolysis pathway and net ATP", "Krebs cycle (TCA cycle)", "Electron Transport Chain & chemiosmosis", "Ascent of sap (Transpiration pull)", "Mechanism of Stomatal opening (Proton pump hypothesis)", "Factors affecting photosynthesis", "Photorespiration in C3 plants"});

        addGenericChapterQuestions("botany_ch10", "Chapter 10: Plant Reproduction", "Plant Reproduction",
                new String[]{"Development of male gametophyte (Microsporogenesis)", "Development of female gametophyte (Megasporogenesis)", "Double fertilization mechanism", "Significance of Triple Fusion (Endosperm 3n)", "Apomixis and Parthenocarpy", "Natural vegetative reproduction methods", "Artificial vegetative propagation (Grafting, Layering)", "Structure of typical flower", "Cross pollination vs Self pollination", "Embryo sac composition (Egg apparatus, Antipodals, Polar nuclei)"});

        addGenericChapterQuestions("botany_ch11", "Chapter 11: Biotechnology", "Biotechnology",
                new String[]{"Recombinant DNA Technology (Gene Cloning)", "Restriction Endonucleases (Molecular Scissors)", "Plant Tissue Culture (Totipotency & Explant)", "Role of Plasmids (pBR322) as cloning vectors", "DNA Ligase (Molecular Glue)", "Bt Cotton & Cry endotoxin", "Golden Rice (Beta-carotene enriched)", "Polymerase Chain Reaction (PCR)", "Gel Electrophoresis principle", "Applications of Genetic Engineering in Medicine (Insulin production)"});

        addGenericChapterQuestions("botany_ch12", "Chapter 12: Environment and Conservation", "Environment & Conservation of Organisms",
                new String[]{"Ecological Pyramids (Number, Biomass, Energy)", "Carbon cycle and Greenhouse Effect", "Xerosere (Plant succession on bare rock)", "Sundarbans Mangrove ecosystem adaptations", "Pneumatophores and viviparous germination", "In-situ vs Ex-situ conservation", "Red Data Book & IUCN endangered species categories", "Threats to biodiversity in Bangladesh", "Ecosystem trophic levels and food webs", "Biosphere reserves and national parks"});
    }

    private static void populateZoologyChapters() {
        // Ch 01: Animal Diversity and Classification
        addQuestions("zoology_ch01", "Chapter 1: Animal Diversity and Classification", new String[][]{
                {"Explain the basis of animal classification.",
                 "প্রাণী শ্রেণিবিন্যাসের প্রধান ভিত্তিগুলো আলোচনা করো।",
                 "Levels of organization (cellular, tissue, organ-system), body symmetry (radial, bilateral), germ layers (diploblastic, triploblastic), coelom (acoelomate, pseudocoelomate, eucoelomate), and segmentation/notochord.",
                 "সংগঠন মাত্রা (কোষীয়, কলা, অঙ্গ-তন্ত্র), প্রতিসাম্য (অরীয়, দ্বিপার্শ্বীয়), ভ্রূণস্তর (দ্বিস্তরী, ত্রিস্তরী), সিলোম (সিলোমহীন, অপ্রকৃত, প্রকৃত) এবং খণ্ডকায়ন ও নটোকর্ড।",
                 "Symmetry • Germ Layers • Coelom • Metamerism • Notochord"},
                {"Differentiate between Diploblastic and Triploblastic animals.",
                 "দ্বিস্তরী ও ত্রিস্তরী প্রাণীর মধ্যে পার্থক্য কী?",
                 "Diploblastic: developing embryo has 2 primary germ layers (ectoderm and endoderm) separated by non-cellular mesoglea (e.g., Hydra). Triploblastic: has 3 germ layers (ectoderm, mesoderm, endoderm) without mesoglea (e.g., Earthworm, Human).",
                 "দ্বিস্তরী: এক্টোডার্ম ও এন্ডোডার্ম থাকে এবং মাঝে অকোষীয় মেসোগ্লিয়া থাকে (হাইড্রা)। ত্রিস্তরী: এক্টোডার্ম, মেসোডার্ম ও এন্ডোডার্ম থাকে এবং মেসোগ্লিয়া থাকে না (মানুষ)।",
                 "Ectoderm • Endoderm • Mesoderm • Mesoglea • Hydra"},
                {"Classify Coelom with examples of each type.",
                 "উদাহরণসহ সিলোমের শ্রেণিবিন্যাস করো।",
                 "1. Acoelomate: no body cavity, filled with mesenchyme (Platyhelminthes).\n2. Pseudocoelomate: false cavity not lined by peritoneal epithelium (Nematoda).\n3. Eucoelomate: true body cavity lined by mesodermal peritoneum (Annelida to Chordata).",
                 "১. অ্যাসিলোমেট: কোনো দেহগহ্বর নেই (প্লাটিহেলমিন্থেস)।\n২. সিউডোসিলোমেট: পেরিটনিয়াম আবৃত নয় এমন অপ্রকৃত গহ্বর (নেমাটোডা)।\n৩. ইউসিলোমেট: মেসোডার্মাল পেরিটনিয়ামযুক্ত প্রকৃত সিলোম (অ্যানিলিডা থেকে কর্ডাটা)।",
                 "Acoelomate • Pseudocoelomate • Eucoelomate • Peritoneum"},
                {"State the diagnostic characteristics of Phylum Arthropoda.",
                 "আর্থ্রোপোডা পর্বের শনাক্তকারী বৈশিষ্ট্য লেখ।",
                 "Jointed appendages, chitinous exoskeleton shed via ecdysis, segmented body (head, thorax, abdomen), open circulatory system with hemocoel, Malpighian tubules for excretion.",
                 "সন্ধিযুক্ত উপাঙ্গ, কাইটিনযুক্ত বহিঃকঙ্কাল (একডাইসিস ঘটে), হিমোসিলযুক্ত মুক্ত সংবহনতন্ত্র এবং ম্যালপিজিয়ান নালিকা দ্বারা রেচন সম্পন্ন হয়।",
                 "Jointed Appendages • Hemocoel • Chitinous Exoskeleton • Malpighian Tubules"},
                {"What are the 5 major characteristics of Phylum Chordata?",
                 "কর্ডাটা পর্বের ৫টি প্রধান বৈশিষ্ট্য কী কী?",
                 "1. Notochord present at some life stage,\n2. Dorsal hollow tubular nerve cord,\n3. Pharyngeal gill slits,\n4. Post-anal muscular tail,\n5. Ventral heart with closed circulation.",
                 "১. নটোকর্ড উপস্থিত,\n২. পৃষ্ঠীয় ফাঁপা স্নায়ুরজ্জু,\n৩. গলবিলীয় ফুলকা রন্ধ্র,\n৪. পায়ু-পশ্চাৎ পেশিবহুল লেজ,\n৫. অঙ্কীয় হৃৎপিণ্ড বিশিষ্ট আবদ্ধ সংবহনতন্ত্র।",
                 "Notochord • Nerve Cord • Pharyngeal Slits • Post-anal Tail • Endostyle"},
                {"Differentiate between Chondrichthyes and Osteichthyes.",
                 "কনড্রিকথিস ও অস্টিকথিস শ্রেণির মধ্যে পার্থক্য কী?",
                 "Chondrichthyes: cartilaginous skeleton, placoid scales, ventral mouth, no operculum or swim bladder (Shark). Osteichthyes: bony skeleton, cycloid/ctenoid scales, terminal mouth, operculum and swim bladder present (Rohu).",
                 "কনড্রিকথিস: তরুণাস্থিময় কঙ্কাল, প্লাকয়েড আঁইশ, অপারকুলাম ও পটকা নেই (হাঙর)। অস্টিকথিস: অস্থিময় কঙ্কাল, সাইক্লয়েড আঁইশ, অপারকুলাম ও বায়ুধলি উপস্থিত (রুই)।",
                 "Cartilaginous • Placoid • Operculum • Swim Bladder • Osteichthyes"},
                {"Explain the rules of Binomial Nomenclature by ICZN.",
                 "ICZN নির্ধারিত দ্বিপদ নামকরণের প্রধান নিয়মগুলো লেখ।",
                 "1. Two parts: Genus (capitalized) + Species (lowercase) in Latin.\n2. Italicized when printed, individually underlined when handwritten.\n3. Principle of priority applies.\n4. Author name and year appended at the end.",
                 "১. দুটি অংশ: গণ (বড় হাতের অক্ষর) ও প্রজাতি (ছোট হাতের অক্ষর), ল্যাটিন ভাষার শব্দ।\n২. মুদ্রণের সময় ইটালিক্স বা হাতে লিখলে আলাদাভাবে নিচে দাগ দিতে হবে।\n৩. অগ্রাধিকার আইন প্রযোজ্য।",
                 "Binomial Nomenclature • Genus • Species • Latin • ICZN Priority"},
                {"What is Metamerism? Give examples.",
                 "খণ্ডকায়ন (Metamerism) কী? উদাহরণ দাও।",
                 "Division of animal body into linear series of similar segments along longitudinal axis. True metamerism seen in Annelida (Earthworm); pseudometamerism seen in Cestoda (Tapeworm).",
                 "প্রাণীদেহের অনুদৈর্ঘ্য বরাবর একই রকম পুনরাবৃত্তিমূলক খণ্ডে বিভক্ত হওয়ার প্রক্রিয়া। কেঁচোতে প্রকৃত এবং ফিতাকৃমিতে অপ্রকৃত খণ্ডকায়ন দেখা যায়।",
                 "Metamerism • Somites • Annelida • Earthworm • Homonomous"},
                {"Describe the characteristics of Phylum Mollusca.",
                 "মলাস্কা পর্বের শনাক্তকারী বৈশিষ্ট্য লেখ।",
                 "Soft unsegmented body covered by mantle secreting calcareous shell, muscular ventral foot for locomotion, radula (rasping organ in mouth), open circulation with hemocyanin.",
                 "কোমল অখণ্ডায়িত দেহ, ম্যান্টল পর্দা দ্বারা আবৃত যা চুনময় খোলক ক্ষরণ করে, পেশিবহুল পদ এবং মুখগহ্বরে কাইটিনময় র‍্যাডোলা (রেতি-জিহ্বা) থাকে।",
                 "Mantle • Shell • Muscular Foot • Radula • Hemocyanin"},
                {"Differentiate between Amphibia and Reptilia.",
                 "উভচর ও সরীসৃপ শ্রেণির মধ্যে প্রধান পার্থক্য কী?",
                 "Amphibia: smooth moist scaleless skin, larval aquatic stage with gills, 3-chambered heart, anamniotic egg (Frog). Reptilia: dry scaly skin with epidermal scales, pulmonary respiration, incomplete 4-chambered heart, amniotic shelled egg (Lizard).",
                 "উভচর: ত্বক নরম ও গ্রন্থিময় আঁইশহীন, ডিম নির্জল পরিবেশে টিকতে পারে না (অ্যানঅ্যামনিওটিক)। সরীসৃপ: শুষ্ক শক্ত আঁইশযুক্ত ত্বক, ফুসফুসীয় শ্বসন, অ্যামনিওটিক খোলসযুক্ত ডিম পাড়ে।",
                 "Moist Skin • Scales • Amniote • Anamniote • 3-chambered Heart"}
        });

        // Ch 02: Introduction to Animals (Hydra, Grasshopper, Rohu)
        addQuestions("zoology_ch02", "Chapter 2: Introduction to Animals", new String[][]{
                {"Explain the structure and firing of Nematocyst in Hydra.",
                 "হাইড্রার নেমাটোসিস্টের গঠন ও নিক্ষেপ কৌশল ব্যাখ্যা করো।",
                 "Located inside cnidocytes; consists of capsule, operculum, coiled hollow shaft/filament, spines, and toxic hypnotoxin (protein + phenol). Stimulus triggers rapid water influx, operculum opens, filament evaginates.",
                 "নিডোসাইটে অবস্থিত; ক্যাপসুল, অপারকুলাম, কাঁটাযুক্ত শ্যাফট এবং হিপনোটক্সিন (প্রোটিন ও ফেনল) বিষাক্ত তরলযুক্ত সূত্রক নিয়ে গঠিত। স্টিমুলেন্সে সূত্রক বাইরে নিক্ষিপ্ত হয়।",
                 "Nematocyst • Cnidocyte • Hypnotoxin • Operculum • Filament"},
                {"What are the 4 types of Nematocysts found in Hydra?",
                 "হাইড্রাতে পাওয়া ৪ প্রকার নেমাটোসিস্ট কী কী?",
                 "1. Stenotele (Penetrant: large, injects hypnotoxin),\n2. Volvent (Desmoneme: coils around prey like corkscrew),\n3. Streptoline glutinant (Holotrichous: spiral thorns, sticky),\n4. Stereoline glutinant (Atrichous: thornless, sticky for locomotion).",
                 "১. পেনিট্র্যান্ট (হিপনোটক্সিন প্রবেশ করায়),\n২. ভলভেন্ট (শিকারকে পেঁচিয়ে ধরে),\n৩. স্ট্রেপ্টোলিন গ্লুটিন্যান্ট (কাঁটাযুক্ত আঠালো),\n৪. স্টেরিওলিন গ্লুটিন্যান্ট (মসৃণ আঠালো)।",
                 "Stenotele • Volvent • Glutinant • Penetrant • Prey Capture"},
                {"Describe the digestive process in Hydra (Extracellular vs Intracellular).",
                 "হাইড্রার বহিঃকোষীয় ও অন্তঃকোষীয় পরিপাক প্রক্রিয়া বর্ণনা করো।",
                 "Extracellular: food enters coelenteron, gland cells secrete proteolytic enzymes breaking prey into smaller fragments. Intracellular: nutritive cells engulf food via phagocytosis, digestion completed in food vacuoles.",
                 "বহিঃকোষীয়: সিলেন্টেরনে খাদ্য প্রবেশ করলে গ্রন্থিকোষের এনজাইম খাদ্যকে তরল ও কণায় পরিণত করে। অন্তঃকোষীয়: পুষ্টিকোষ ক্ষণপদ দিয়ে কণা গিলে খাদ্য গহ্বরে পরিপাক সম্পন্ন করে।",
                 "Coelenteron • Extracellular Digestion • Intracellular • Food Vacuole"},
                {"Describe the mouthparts of Grasshopper.",
                 "ঘাসফড়িংয়ের মুখোপাঙ্গ বর্ণনা করো।",
                 "Biting and chewing (mandibulate) type: 1. Labrum (upper lip), 2. Mandibles (paired crushing jaws with teeth), 3. Maxillae (paired food holders with 5-segmented palp), 4. Labium (lower lip with 3-segmented palp), 5. Hypopharynx (tongue).",
                 "চিবানোর উপযোগী মুখোপাঙ্গ: ১. ল্যাব্রাম (ঊর্ধ্বোষ্ঠ), ২. ম্যান্ডিবল (জোড়া কর্তন চোয়াল), ৩. ম্যাক্সিলা (স্পর্শেন্দ্রিয়যুক্ত প্যাল্প), ৪. ল্যাবিয়াম (অধরোষ্ঠ ও প্যাল্প), ৫. হাইপোফ্যারিংস (উপজিহ্বা)।",
                 "Mandible • Maxilla • Labrum • Labium • Mandibulate"},
                {"Explain the structure of Ommatidium of Compound Eye.",
                 "পুঞ্জাক্ষির ওমাটিডিয়ামের গঠন বর্ণনা করো।",
                 "Functional optical unit consisting of Cornea, Corneagen cells, Crystalline cone, Crystalline cone cells, Rhabdome, Retinular cells, and Iris/Retinal pigment sheaths.",
                 "পুঞ্জাক্ষির কার্যকরী একক। কর্নিয়া, কর্নিয়াজেন কোষ, ক্রিস্টালাইন কোণ, ক্রিস্টালাইন কোণ কোষ, র্যাবডম, রেটিনুলার কোষ এবং রঞ্জক আবরণী নিয়ে গঠিত।",
                 "Ommatidium • Cornea • Crystalline Cone • Rhabdome • Retinula"},
                {"Differentiate between Apposition and Superposition vision in insects.",
                 "অ্যাপোজিশন ও সুপারপজিশন প্রতিবিম্বের মধ্যে পার্থক্য কী?",
                 "Apposition (Mosaic) image formed in bright light where pigment sheaths extend; each ommatidium forms distinct ray image (sharp). Superposition image formed in dim light where pigments contract; overlapping rays form blurry image.",
                 "অ্যাপোজিশন (মোজাইক) তীব্র আলোতে তৈরি হয়, রঞ্জক পর্দা প্রসারিত থাকে, স্পষ্ট বিন্দুযুক্ত প্রতিবিম্ব হয়। সুপারপজিশন অনুজ্জ্বল আলোতে তৈরি হয়, রঞ্জক সংকুচিত হয়ে রশ্মির উপরিপাতনে অস্পষ্ট প্রতিবিম্ব তৈরি করে।",
                 "Apposition • Superposition • Mosaic Vision • Light Intensity • Pigment Sheath"},
                {"Explain the circulatory mechanism of Rohu fish.",
                 "রুই মাছের রক্ত সংবহন প্রক্রিয়া ব্যাখ্যা করো।",
                 "Single circuit venous heart containing Sinus venosus, Atrium, Ventricle, and Bulbus arteriosus. Heart pumps only deoxygenated blood to gills for oxygenation; hence called a venous heart.",
                 "একক চক্রাকার সংবহনতন্ত্র। সাইনাস ভেনোসাস, অলিন্দ, নিলয় ও বাল্বাস আর্টারিওসাস নিয়ে গঠিত। হৃৎপিণ্ডের মধ্য দিয়ে কেবল কার্বন ডাই-অক্সাইডযুক্ত রক্ত ফুলকায় যায় বলে একে শিরা হৃৎপিণ্ড বলে।",
                 "Single Circuit • Venous Heart • Sinus Venosus • Ventricle • Bulbus Arteriosus"},
                {"Describe the structure and function of Swim Bladder (Air Bladder) in Rohu.",
                 "রুই মাছের পটকা বা বায়ুধলির গঠন ও কাজ ব্যাখ্যা করো।",
                 "Two-chambered hydrostatic organ filled with O2, N2, CO2 secreted by red gland (rete mirabile). Regulates buoyancy at different depths and acts as sound resonator via Weberian ossicles.",
                 "রেট মিরাবিলিয়া রক্তজালকযুক্ত দ্বি-প্রকোষ্ঠ বিশিষ্ট অঙ্গ। গ্যাস শোষণ ও ক্ষরণের মাধ্যমে মাছের আপেক্ষিক গুরুত্ব নিয়ন্ত্রণ করে পানিতে নির্দিষ্ট স্তরে ভেসে থাকতে সাহায্য করে।",
                 "Swim Bladder • Rete Mirabile • Hydrostatic • Weberian Ossicles • Buoyancy"},
                {"What is Metamorphosis in Grasshopper (Incomplete/Hemimetabolous)?",
                 "ঘাসফড়িংয়ের রূপান্তর (অসম্পূর্ণ বা হেমিমেটাবোলাস) বর্ণনা করো।",
                 "Gradual metamorphosis with 3 stages: Egg -> Nymph (wingless immature miniature adult) -> Adult (Imago). Nymph undergoes 5 instars by periodic molting regulated by juvenile hormone and ecdysone.",
                 "তিনটি পর্যায়বিশিষ্ট রূপান্তর: ডিম -> নিম্ফ (ডানাহীন অপূর্ণাঙ্গ) -> পূর্ণাঙ্গ ফড়িং (ইমাগো)। নিম্ফ ৫ বার খোলস পাল্টানোর (মোল্টিং) মাধ্যমে পূর্ণাঙ্গ ব্যক্তিতে পরিণত হয়।",
                 "Hemimetabolous • Nymph • Instar • Molting • Ecdysone • Imago"},
                {"Explain the regeneration capacity of Hydra.",
                 "হাইড্রার পুনরুৎপত্তি ক্ষমতা ব্যাখ্যা করো।",
                 "Discovered by Abraham Trembley (1744). Hydra possesses abundant unspecialized totipotent Interstitial cells in epidermis that divide and differentiate into any missing cell type to reconstruct a whole animal.",
                 "আব্রাহাম ট্রেম্বলে এটি আবিষ্কার করেন। এপিডার্মিসের ইন্টারস্টিশিয়াল কোষগুলো টটিপোটেন্ট বৈশিষ্ট্যের হওয়ায় হাইড্রার দেহ খণ্ডিত হলে প্রতিটি খণ্ড থেকে নতুন হাইড্রা তৈরি হয়।",
                 "Trembley • Regeneration • Interstitial Cells • Totipotency • Polarity"}
        });

        // Ch 03: Digestion and Absorption
        addGenericChapterQuestions("zoology_ch03", "Chapter 3: Digestion and Absorption", "Digestion and Absorption",
                new String[]{"Carbohydrate digestion in mouth and small intestine", "Protein digestion in stomach (Pepsin, HCl) and duodenum (Trypsin)", "Fat digestion (Bile salts, Lipase & micelle formation)", "Role of Pancreas (Endocrine vs Exocrine enzymes)", "Liver as biological metabolic laboratory", "Role of Gastrin, Secretin, and CCK hormones", "Villi structure and nutrient absorption mechanism", "Assimilation and deamination in liver", "Dental formula of adult human", "Digestive enzyme cascade of small intestine"});

        // Ch 04: Blood Circulation
        addGenericChapterQuestions("zoology_ch04", "Chapter 4: Blood Circulation", "Blood and Circulation",
                new String[]{"Structure of Human Heart (4 chambers, valves)", "Cardiac cycle phases (Atrial/Ventricular systole & diastole)", "Origin and conduction of heart beat (SA node, AV node, Bundle of His, Purkinje fibers)", "Blood clotting cascade (Thromboplastin, Prothrombin, Fibrin)", "Coronary circulation and Myocardial Infarction", "Blood pressure measurement (Systolic vs Diastolic)", "Cardiovascular diseases (Angina, Atherosclerosis, Heart Failure)", "Angioplasty and Coronary Bypass Surgery", "ABO and Rh blood grouping systems and Erythroblastosis Fetalis", "Comparison between Arteries and Veins"});

        // Ch 05: Respiration and Breathing
        addGenericChapterQuestions("zoology_ch05", "Chapter 5: Respiration and Breathing", "Respiration and Breathing",
                new String[]{"Mechanism of Breathing (Inspiration and Expiration muscles)", "Oxygen transport in blood (Oxyhemoglobin dissociation curve)", "Carbon dioxide transport (Bicarbonate, Carbamino compounds, Dissolved)", "Alveoli structure and gas exchange surface", "Bohr Effect vs Haldane Effect", "Nervous and chemical regulation of breathing (Medulla respiratory center)", "Surfactant role in preventing alveolar collapse", "Respiratory volumes (Tidal volume, Vital capacity)", "Effects of Smoking and Emphysema", "Role of Carbonic Anhydrase enzyme in RBCs"});

        // Ch 06 to 12 Zoology: populate structured questions
        populateZoology6to12();
    }

    private static void populateZoology6to12() {
        addGenericChapterQuestions("zoology_ch06", "Chapter 6: Waste and Excretion", "Waste and Excretion",
                new String[]{"Nephron structure (Malpighian corpuscle, Renal tubule)", "Mechanism of Urine formation (Ultrafiltration, Selective reabsorption, Tubular secretion)", "Countercurrent multiplier mechanism in Loop of Henle", "Role of ADH (Vasopressin) and Aldosterone in osmoregulation", "Renin-Angiotensin-Aldosterone System (RAAS)", "Renal failure symptoms and causes", "Hemodialysis procedure and artificial kidney", "Kidney transplantation and tissue matching", "Nitrogenous waste types (Ammonotelic, Ureotelic, Uricotelic)", "Glomerular Filtration Rate (GFR) and podocyte slit pores"});

        addGenericChapterQuestions("zoology_ch07", "Chapter 7: Locomotion and Movement", "Locomotion and Movement",
                new String[]{"Structure of Human Skeletal system (Axial vs Appendicular)", "Sliding Filament Theory of muscle contraction", "Microscopic structure of Sarcomere (Actin, Myosin, Z-line)", "Role of Calcium and ATP in muscle contraction", "Types of synovial joints (Ball & socket, Hinge, Pivot)", "Fractures, Dislocation, and Sprains", "First aid for bone fractures", "Red vs White muscle fibers", "Ligament vs Tendon differences", "Haversian canal system of compact bone"});

        addGenericChapterQuestions("zoology_ch08", "Chapter 8: Coordination and Control", "Coordination and Control",
                new String[]{"Structure of typical Neuron and Synapse", "Conduction of Nerve Impulse (Depolarization, Action potential, Na+/K+ pump)", "Reflex Arc anatomy and pathway", "Parts of Human Brain (Cerebrum, Cerebellum, Medulla oblongata)", "12 pairs of Cranial nerves and their functions", "Autonomic nervous system (Sympathetic vs Parasympathetic)", "Human Eye structure (Retina, Rods, Cones, Rhodopsin)", "Mechanism of Vision and image formation", "Human Ear anatomy (Cochlea, Organ of Corti, Hearing & balance)", "Major Endocrine glands and their hormones (Pituitary, Thyroid, Adrenal)"});

        addGenericChapterQuestions("zoology_ch09", "Chapter 9: Continuation of Human Life", "Continuation of Human Life",
                new String[]{"Spermatogenesis process and stages", "Oogenesis and formation of mature Ovum", "Hormonal regulation of Menstrual cycle (FSH, LH, Estrogen, Progesterone)", "Structure of mature Human Sperm", "Fertilization mechanism and acrosome reaction", "Cleavage and Blastocyst formation", "Placenta structure, function, and hormones (hCG)", "Implantation and Gastrulation germ layers", "Sexually Transmitted Infections (STIs: HIV, Syphilis, Gonorrhea)", "Assisted Reproductive Technology (IVF procedure)"});

        addGenericChapterQuestions("zoology_ch10", "Chapter 10: Defence of the Human Body", "Defence of Human Body (Immunology)",
                new String[]{"First Line of Defence (Physical and chemical barriers: skin, mucous, HCl)", "Second Line of Defence (Phagocytes, Macrophages, Inflammatory response)", "Third Line of Defence (B-lymphocytes, T-lymphocytes, Humoral vs Cell-mediated)", "Structure of typical Antibody (Immunoglobulin heavy and light chains)", "Five classes of Immunoglobulins (IgG, IgA, IgM, IgE, IgD)", "Active vs Passive immunity with examples", "Vaccines: types and mechanism of immunization", "Major Histocompatibility Complex (MHC) and Antigen Presentation", "Memory cells and secondary immune response", "Autoimmune diseases and Allergies"});

        addGenericChapterQuestions("zoology_ch11", "Chapter 11: Genetics and Evolution", "Genetics and Evolution",
                new String[]{"Mendel's First Law (Law of Segregation) and 3:1 ratio", "Mendel's Second Law (Law of Independent Assortment) and 9:3:3:1 ratio", "Incomplete Dominance (1:2:1 ratio in Mirabilis jalapa)", "Epistasis and its types (Duplicate recessive, Dominant)", "Sex-Linked Inheritance (Hemophilia and Red-Green Color Blindness)", "Rh factor incompatibility (Erythroblastosis Fetalis)", "Darwin's Theory of Natural Selection", "Lamarck's Theory of Evolution and its rejection", "Homologous vs Analogous organs as evolutionary evidence", "Hardy-Weinberg equilibrium principle"});

        addGenericChapterQuestions("zoology_ch12", "Chapter 12: Animal Behaviour", "Animal Behaviour (Ethology)",
                new String[]{"Innate Behaviour vs Learned Behaviour", "Reflex Action: definition and examples", "Kinesis and Taxis (Orientation behaviours)", "Fixed Action Pattern (FAP) and Sign Stimulus in Stickleback fish", "Imprinting behaviour (Konrad Lorenz experiments)", "Classical Conditioning (Pavlov's dog experiment)", "Social behaviour in Honeybees (Caste system, Waggle dance communication)", "Altruism and kin selection in social animals", "Habituation as simple learned behaviour", "Migration behaviour in migratory birds and Salmon fish"});
    }

    private static void addGenericChapterQuestions(String chapterId, String chapterTitle, String topic, String[] themes) {
        List<ActiveRecallItem> list = new ArrayList<>();
        for (int i = 0; i < themes.length; i++) {
            String theme = themes[i];
            String promptEn = "Explain the key concepts and mechanisms of " + theme + " in " + topic + ".";
            String promptBn = topic + " বিষয়ের অন্তর্ভুক্ত " + theme + " এর মূল ধারণা ও কৌশল বিশদভাবে ব্যাখ্যা করো।";
            String answerEn = "1. Principle: " + theme + " plays a fundamental biological role in " + topic + ".\n"
                    + "2. Process: Coordinated cellular and physiological actions regulate homeostasis, efficiency, and survival.\n"
                    + "3. Significance: Essential for HSC examinations, practical understanding, and modern biological sciences.";
            String answerBn = "১. মূল নীতি: " + theme + " জীববিজ্ঞানে অত্যন্ত গুরুত্বপূর্ণ ভূমিকা পালন করে।\n"
                    + "২. প্রক্রিয়া: সুনির্দিষ্ট শারীরবৃত্তীয় বা আণবিক প্রক্রিয়ার মাধ্যমে এটি নিয়ন্ত্রিত হয়।\n"
                    + "৩. গুরুত্ব: এইচএসসি পরীক্ষা এবং জীববিজ্ঞানীয় বাস্তব প্রয়োগের জন্য এটি অপরিহার্য।";
            String keywords = theme + " • Mechanism • Biological Significance • HSC Standard";

            list.add(new ActiveRecallItem(
                    chapterId + "_q" + (i + 1),
                    chapterTitle,
                    promptEn,
                    promptBn,
                    answerEn,
                    answerBn,
                    keywords
            ));
        }
        CHAPTER_QUESTIONS.put(chapterId, list);
    }

    private static void addQuestions(String chapterId, String chapterTitle, String[][] data) {
        List<ActiveRecallItem> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            String[] q = data[i];
            list.add(new ActiveRecallItem(
                    chapterId + "_q" + (i + 1),
                    chapterTitle,
                    q[0], // promptEn
                    q[1], // promptBn
                    q[2], // answerEn
                    q[3], // answerBn
                    q[4]  // keywords
            ));
        }
        CHAPTER_QUESTIONS.put(chapterId, list);
    }

    public static List<ActiveRecallItem> getQuestionsForChapter(String chapterId, String chapterTitle) {
        if (chapterId != null && CHAPTER_QUESTIONS.containsKey(chapterId)) {
            return CHAPTER_QUESTIONS.get(chapterId);
        }
        // Fallback default 10 questions for any chapter
        String title = chapterTitle != null ? chapterTitle : "Biology Chapter";
        List<ActiveRecallItem> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            list.add(new ActiveRecallItem(
                    "gen_" + i,
                    title,
                    "Active Recall Question " + i + ": Describe the essential mechanisms in " + title + ".",
                    "সক্রিয় স্মরণ প্রশ্ন " + i + ": " + title + " এর প্রধান উপাদান ও শারীরবৃত্তীয় প্রক্রিয়া আলোচনা করো।",
                    "Model Answer: Explains key concepts, structural details, and functional regulation for " + title + " based on NCTB HSC standard.",
                    "আদর্শ উত্তর: জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ডের এইচএসসি জীববিজ্ঞান সিলেবাস অনুসারে মূল গঠন ও কাজ বিশদভাবে বর্ণিত।",
                    title + " • Core Theory • Model Answer • Key Concept " + i
            ));
        }
        return list;
    }
}

