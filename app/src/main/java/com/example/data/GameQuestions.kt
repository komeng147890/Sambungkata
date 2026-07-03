package com.example.data

data class LevelData(
    val id: Int,
    val title: String,
    val subtitle: String,
    val animalName: String,
    val animalEmoji: String,
    val animalColor: String, // Hex color for the animal theme
    val description: String,
    val instructions: String
)

data class WordQuiz(
    val id: Int,
    val startWord: String,       // e.g., "BUKU"
    val startSyllable: String,   // e.g., "BU - KU"
    val targetSyllable: String,  // e.g., "KU"
    val options: List<QuizOption>,
    val correctIndex: Int,
    val hint: String
)

data class QuizOption(
    val word: String,            // e.g., "KUDA"
    val syllables: String        // e.g., "KU - DA"
)

data class SentenceQuiz(
    val id: Int,
    val sentence: String,            // e.g., "Budi pergi ke ___ untuk belajar."
    val options: List<String>,       // e.g., ["SEKOLAH", "DAPUR", "KOLAM"]
    val correctIndex: Int,
    val hint: String
)

data class StoryQuiz(
    val id: Int,
    val story: String,               // Short narrative for children
    val question: String,            // Reading comprehension question
    val options: List<String>,       // Multiple choice answers
    val correctIndex: Int,
    val hint: String
)


object GameQuestions {
    val levels = listOf(
        LevelData(
            id = 1,
            title = "Kiko si Kucing",
            subtitle = "Suku Kata Awal",
            animalName = "Kiko si Kucing 🐱",
            animalEmoji = "🐱",
            animalColor = "#FFB74D", // Orange
            description = "Bantu Kiko menyambung kata berdasarkan suku kata terakhir!",
            instructions = "Pilihlah kata yang dimulai dengan suku kata terakhir dari kata sebelumnya. Contoh: BU-KU -> KU-DA."
        ),
        LevelData(
            id = 2,
            title = "Cici si Kelinci",
            subtitle = "Lengkapi Kata",
            animalName = "Cici si Kelinci 🐰",
            animalEmoji = "🐰",
            animalColor = "#F06292", // Pink
            description = "Bantu Cici melengkapi suku kata yang hilang untuk membuat rantai kata!",
            instructions = "Pilihlah suku kata yang tepat untuk melengkapi kata kosong agar tersambung! Contoh: BO-LA -> LA-RI."
        ),
        LevelData(
            id = 3,
            title = "Momo si Monyet",
            subtitle = "Rantai Gelembung",
            animalName = "Momo si Monyet 🐵",
            animalEmoji = "🐵",
            animalColor = "#A1887F", // Brown
            description = "Pecahkan gelembung sabun kata untuk membentuk rantai kata terpanjang!",
            instructions = "Pecahkan gelembung kata secara berurutan agar membentuk rantai kata yang benar!"
        ),
        LevelData(
            id = 4,
            title = "Leo si Singa",
            subtitle = "Tantangan Kilat Raja Kata",
            animalName = "Leo si Singa 🦁",
            animalEmoji = "🦁",
            animalColor = "#FFD54F", // Yellow/Gold
            description = "Uji kecepatanmu menyambung kata sebelum waktu habis!",
            instructions = "Sambung kata secepat mungkin dari kartu pilihan untuk mendapatkan skor tertinggi!"
        ),
        LevelData(
            id = 5,
            title = "Riko si Orangutan",
            subtitle = "Suku Kata Kompleks (3 Suku Kata)",
            animalName = "Riko si Orangutan 🦧",
            animalEmoji = "🦧",
            animalColor = "#D84315", // Rust Orange
            description = "Bantu Riko menyambung kata dengan suku kata kompleks dan lebih panjang (3 suku kata)!",
            instructions = "Pilihlah kata yang dimulai dengan suku kata terakhir dari kata 3-suku-kata sebelumnya. Contoh: KA-ME-RA -> RA-KSE-SA."
        ),
        LevelData(
            id = 6,
            title = "Boni si Beruang",
            subtitle = "Lengkapi Kata Kompleks (3-4 Suku Kata)",
            animalName = "Boni si Beruang 🐻",
            animalEmoji = "🐻",
            animalColor = "#8D6E63", // Rich Brown
            description = "Bantu Boni melengkapi suku kata yang hilang dari kata-kata panjang yang sulit!",
            instructions = "Pilihlah suku kata yang tepat untuk melengkapi kata kosong panjang (3-4 suku kata). Contoh: SE-PA-TU -> TU-LI-SAN."
        ),
        LevelData(
            id = 7,
            title = "Poli si Panda",
            subtitle = "Lengkapi Kalimat rumpang",
            animalName = "Poli si Panda 🐼",
            animalEmoji = "🐼",
            animalColor = "#37474F", // Slate/Grey
            description = "Bantu Poli melengkapi kalimat rumpang dengan kata yang paling tepat!",
            instructions = "Pilihlah satu kata dari pilihan yang tersedia untuk melengkapi kalimat kosong agar memiliki makna yang benar!"
        ),
        LevelData(
            id = 8,
            title = "Tito si Tupai",
            subtitle = "Soal Cerita Pendek",
            animalName = "Tito si Tupai 🐿️",
            animalEmoji = "🐿️",
            animalColor = "#A1887F", // Light Brown
            description = "Bantu Tito menjawab pertanyaan dari cerita pendek anak yang seru!",
            instructions = "Bacalah cerita pendek dengan saksama, lalu pilihlah jawaban yang paling benar untuk menjawab pertanyaan!"
        )
    )

    // Level 1: Match ending syllable to starting syllable of next word
    val level1Quizzes = listOf(
        WordQuiz(
            id = 1,
            startWord = "BUKU",
            startSyllable = "BU - KU",
            targetSyllable = "KU",
            options = listOf(
                QuizOption("KUDA", "KU - DA"),
                QuizOption("BATU", "BA - TU"),
                QuizOption("PITA", "PI - TA")
            ),
            correctIndex = 0,
            hint = "Suku kata akhir BU-KU adalah KU. Cari kata yang berawalan KU!"
        ),
        WordQuiz(
            id = 2,
            startWord = "KUDA",
            startSyllable = "KU - DA",
            targetSyllable = "DA",
            options = listOf(
                QuizOption("SAPI", "SA - PI"),
                QuizOption("DASI", "DA - SI"),
                QuizOption("MEJA", "ME - JA")
            ),
            correctIndex = 1,
            hint = "Suku kata akhir KU-DA adalah DA. Cari kata yang berawalan DA!"
        ),
        WordQuiz(
            id = 3,
            startWord = "DASI",
            startSyllable = "DA - SI",
            targetSyllable = "SI",
            options = listOf(
                QuizOption("KADO", "KA - DO"),
                QuizOption("PIPI", "PI - PI"),
                QuizOption("SIKU", "SI - KU")
            ),
            correctIndex = 2,
            hint = "Suku kata akhir DA-SI adalah SI. Cari kata yang berawalan SI!"
        ),
        WordQuiz(
            id = 4,
            startWord = "SIKU",
            startSyllable = "SI - SIK - U", // wait, "SI-KU" is "SI-KU"
            targetSyllable = "KU",
            options = listOf(
                QuizOption("KUKU", "KU - KU"),
                QuizOption("BOLA", "BO - LA"),
                QuizOption("PADI", "PA - DI")
            ),
            correctIndex = 0,
            hint = "Suku kata akhir SI-KU adalah KU. Cari kata yang berawalan KU!"
        ),
        WordQuiz(
            id = 5,
            startWord = "KUKU",
            startSyllable = "KU - KU",
            targetSyllable = "KU",
            options = listOf(
                QuizOption("ROTI", "RO - TI"),
                QuizOption("LARI", "LA - RI"),
                QuizOption("KURA", "KU - RA")
            ),
            correctIndex = 2,
            hint = "Suku kata akhir KU-KU adalah KU. Cari kata yang berawalan KU!"
        )
    )

    // Level 2: Fill in the missing syllable to complete the chain
    // format: word1 -> word2 (with blank)
    // E.g., BOLA -> LA-RI. We ask: BOLA -> LA-[...] ?
    val level2Quizzes = listOf(
        WordQuiz(
            id = 1,
            startWord = "BOLA",
            startSyllable = "BO - LA",
            targetSyllable = "LA",
            options = listOf(
                QuizOption("RI (LARI)", "LA - RI"),
                QuizOption("PU (LAPU)", "LA - PU"),
                QuizOption("SA (LASA)", "LA - SA")
            ),
            correctIndex = 0,
            hint = "BO-LA dihubungkan dengan LA-RI. Suku kata yang hilang adalah RI!"
        ),
        WordQuiz(
            id = 2,
            startWord = "MEJA",
            startSyllable = "ME - JA",
            targetSyllable = "JA",
            options = listOf(
                QuizOption("LAN (JALAN)", "JA - LAN"),
                QuizOption("TI (JATI)", "JA - TI"),
                QuizOption("MO (JAMO)", "JA - MO")
            ),
            correctIndex = 1,
            hint = "ME-JA dihubungkan dengan JA-TI (kayu jati). Suku kata yang hilang adalah TI!"
        ),
        WordQuiz(
            id = 3,
            startWord = "PITA",
            startSyllable = "PI - TA",
            targetSyllable = "TA",
            options = listOf(
                QuizOption("RU (TARU)", "TA - RU"),
                QuizOption("PE (TAPE)", "TA - PE"),
                QuizOption("LI (TALI)", "TA - LI")
            ),
            correctIndex = 2,
            hint = "PI-TA dihubungkan dengan TA-LI. Suku kata yang hilang adalah LI!"
        ),
        WordQuiz(
            id = 4,
            startWord = "PADI",
            startSyllable = "PA - DI",
            targetSyllable = "DI",
            options = listOf(
                QuizOption("RI (DIRI)", "DI - RI"),
                QuizOption("KO (DIKO)", "DI - KO"),
                QuizOption("SA (DISA)", "DI - SA")
            ),
            correctIndex = 0,
            hint = "PA-DI dihubungkan dengan DI-RI. Suku kata yang hilang adalah RI!"
        ),
        WordQuiz(
            id = 5,
            startWord = "ROTI",
            startSyllable = "RO - TI",
            targetSyllable = "TI",
            options = listOf(
                QuizOption("GA (TIGA)", "TI - GA"),
                QuizOption("LU (TILU)", "TI - LU"),
                QuizOption("BO (TIBO)", "TI - BO")
            ),
            correctIndex = 0,
            hint = "RO-TI dihubungkan dengan TI-GA. Suku kata yang hilang adalah GA!"
        )
    )

    // Level 3: Bubble pop chain words
    // We display a starter word, and there are bubbles on the screen.
    // The player must select 2 words in a row that form a valid chain.
    // E.g. starter: MADU. Bubbles: DURI, RODA, SUSU, BOLA.
    // Order should be MADU -> DURI -> RODA.
    data class BubbleQuiz(
        val id: Int,
        val startWord: String,
        val startSyllable: String,
        val correctChain: List<String>, // e.g. ["DURI", "RODA"]
        val allBubbles: List<String>    // e.g. ["DURI", "RODA", "SUSU", "BOLA", "PITA", "KADO"]
    )

    val level3Quizzes = listOf(
        BubbleQuiz(
            id = 1,
            startWord = "MADU",
            startSyllable = "MA - DU",
            correctChain = listOf("DURI", "RODA"),
            allBubbles = listOf("DURI", "RODA", "SUSU", "BOLA", "KADO", "NAGA")
        ),
        BubbleQuiz(
            id = 2,
            startWord = "KADO",
            startSyllable = "KA - DO",
            correctChain = listOf("DOMBA", "BARU"),
            allBubbles = listOf("DOMBA", "BARU", "SAPI", "PADI", "LARI", "PITA")
        ),
        BubbleQuiz(
            id = 3,
            startWord = "SAPI",
            startSyllable = "SA - PI",
            correctChain = listOf("PITA", "TALI"),
            allBubbles = listOf("PITA", "TALI", "ROTI", "JAHE", "MADU", "DASI")
        ),
        BubbleQuiz(
            id = 4,
            startWord = "NAGA",
            startSyllable = "NA - GA",
            correctChain = listOf("GAJAH", "JAHIT"),
            allBubbles = listOf("GAJAH", "JAHIT", "KUKU", "SISI", "BUMI", "KERA")
        ),
        BubbleQuiz(
            id = 5,
            startWord = "ROTI",
            startSyllable = "RO - TI",
            correctChain = listOf("TIKUS", "KURA"),
            allBubbles = listOf("TIKUS", "KURA", "MADU", "BOLA", "LARI", "BATU")
        )
    )

    // Level 4: Lightning Round
    // Start word is presented, and they have 6 card options.
    // When they click the correct one, it immediately appends and becomes the next start word!
    // We need a rich dictionary of valid chains for SD 1 & 2 level
    val level4Words = listOf(
        // Easy 2-syllable Indonesian nouns and verbs
        "BUKU", "KUDA", "DASI", "SIKU", "KUKU", "KURA", "RASA", "SAPI", "PITA", "TALI",
        "LIMA", "MATA", "TARI", "RIBU", "BUMI", "MINUM", "NUMPANG", "PAGI", "GILA", "LARI",
        "RIAS", "ASLI", "LIMA", "MANIS", "NISAN", "SANTAI", "TAHU", "HUTAN", "TANAH", "NAFAS"
    )

    // Level 5: 3-Suku Kata Word Chains
    val level5Quizzes = listOf(
        WordQuiz(
            id = 1,
            startWord = "KAMERA",
            startSyllable = "KA - ME - RA",
            targetSyllable = "RA",
            options = listOf(
                QuizOption("RAKSESA", "RAK - SE - SA"),
                QuizOption("SEPATU", "SE - PA - TU"),
                QuizOption("DONAT", "DO - NAT")
            ),
            correctIndex = 0,
            hint = "Suku kata akhir KA-ME-RA adalah RA. Cari kata yang berawalan RA!"
        ),
        WordQuiz(
            id = 2,
            startWord = "RAKSESA",
            startSyllable = "RAK - SE - SA",
            targetSyllable = "SA",
            options = listOf(
                QuizOption("KELAPA", "KE - LA - PA"),
                QuizOption("SAMUDRA", "SA - MU - DRA"),
                QuizOption("KERETA", "KE - RE - TA")
            ),
            correctIndex = 1,
            hint = "Suku kata akhir RAK-SE-SA adalah SA. Cari kata yang berawalan SA!"
        ),
        WordQuiz(
            id = 3,
            startWord = "SAMUDRA",
            startSyllable = "SA - MU - DRA",
            targetSyllable = "DRA",
            options = listOf(
                QuizOption("DRAMAGA", "DRA - MA - GA"),
                QuizOption("DELAPAN" , "DE - LA - PAN"),
                QuizOption("SEMEDA", "SE - ME - DA")
            ),
            correctIndex = 0,
            hint = "Suku kata akhir SA-MU-DRA adalah DRA. Cari kata yang berawalan DRA!"
        ),
        WordQuiz(
            id = 4,
            startWord = "DRAMAGA",
            startSyllable = "DRA - MA - GA",
            targetSyllable = "GA",
            options = listOf(
                QuizOption("PISANG", "PI - SANG"),
                QuizOption("GARUDA", "GA - RU - DA"),
                QuizOption("MAKANAN", "MA - KA - NAN")
            ),
            correctIndex = 1,
            hint = "Suku kata akhir DRA-MA-GA adalah GA. Cari kata yang berawalan GA!"
        ),
        WordQuiz(
            id = 5,
            startWord = "GARUDA",
            startSyllable = "GA - RU - DA",
            targetSyllable = "DA",
            options = listOf(
                QuizOption("DANAU", "DA - NA - U"),
                QuizOption("RUMAH", "RU - MAH"),
                QuizOption("BALON", "BA - LON")
            ),
            correctIndex = 0,
            hint = "Suku kata akhir GA-RU-DA adalah DA. Cari kata yang berawalan DA!"
        )
    )

    // Level 6: 3-4 Suku Kata Fill-In-The-Blank Chains
    val level6Quizzes = listOf(
        WordQuiz(
            id = 1,
            startWord = "SEPATU",
            startSyllable = "SE - PA - TU",
            targetSyllable = "TU",
            options = listOf(
                QuizOption("LI (TULISAN)", "TU - LI - SAN"),
                QuizOption("JU (TUJUAN)", "TU - JU - AN"),
                QuizOption("MA (TUMAN)", "TU - MAN")
            ),
            correctIndex = 0,
            hint = "SE-PA-TU dihubungkan dengan TU-LI-SAN. Suku kata yang hilang adalah LI!"
        ),
        WordQuiz(
            id = 2,
            startWord = "TULISAN",
            startSyllable = "TU - LI - SAN",
            targetSyllable = "SAN",
            options = listOf(
                QuizOption("TA (SANTAPAN)", "SAN - TA - PAN"),
                QuizOption("BI (SANBI)", "SAN - BI"),
                QuizOption("KA (SANKA)", "SAN - KA")
            ),
            correctIndex = 0,
            hint = "TU-LI-SAN dihubungkan dengan SAN-TA-PAN. Suku kata yang hilang adalah TA!"
        ),
        WordQuiz(
            id = 3,
            startWord = "SANTAPAN",
            startSyllable = "SAN - TA - PAN",
            targetSyllable = "PAN",
            options = listOf(
                QuizOption("TA (PANTAI)", "PAN - TAI"),
                QuizOption("TANG (PANTANGAN)", "PAN - TA - NGAN"),
                QuizOption("CI (PANCI)", "PAN - CI")
            ),
            correctIndex = 1,
            hint = "SANTAPAN dihubungkan dengan PANTANGAN. Suku kata yang hilang adalah TANG!"
        ),
        WordQuiz(
            id = 4,
            startWord = "PANTANGAN",
            startSyllable = "PAN - TA - NGAN",
            targetSyllable = "NGAN",
            options = listOf(
                QuizOption("TUK (NGANTUK)", "NGAN - TUK"),
                QuizOption("DAP (NGANDAP)", "NGAN - DAP"),
                QuizOption("TET (NGANTET)", "NGAN - TET")
            ),
            correctIndex = 0,
            hint = "PANTANGAN dihubungkan dengan NGANTUK. Suku kata yang hilang adalah TUK!"
        ),
        WordQuiz(
            id = 5,
            startWord = "NGANTUK",
            startSyllable = "NGAN - TUK",
            targetSyllable = "TUK",
            options = listOf(
                QuizOption("KA (TUKARAN)", "TU - KA - RAN"),
                QuizOption("RU (TURU)", "TU - RU"),
                QuizOption("GA (TUGAS)", "TU - GAS")
            ),
            correctIndex = 0,
            hint = "NGANTUK dihubungkan dengan TUKARAN. Suku kata yang hilang adalah KA!"
        )
    )

    // Level 7: Sentence completion quizzes
    val level7Quizzes = listOf(
        SentenceQuiz(
            id = 1,
            sentence = "Budi menggosok ___ sebelum tidur agar giginya bersih dan sehat.",
            options = listOf("GIGI", "MATA", "KAKI"),
            correctIndex = 0,
            hint = "Budi menggosok apa agar gigi bersih? Jawabannya adalah GIGI!"
        ),
        SentenceQuiz(
            id = 2,
            sentence = "Siti selalu mencuci ___ dengan sabun hingga bersih sebelum makan.",
            options = listOf("RAMBUT", "TANGAN", "SEPATU"),
            correctIndex = 1,
            hint = "Sebelum makan, kita harus mencuci tangan agar terbebas dari kuman."
        ),
        SentenceQuiz(
            id = 3,
            sentence = "Ibu sedang memasak nasi goreng yang sangat lezat di ___.",
            options = listOf("KAMAR", "GARASI", "DAPUR"),
            correctIndex = 2,
            hint = "Dapur adalah tempat di rumah yang digunakan untuk memasak makanan."
        ),
        SentenceQuiz(
            id = 4,
            sentence = "Adit segera memakai ___ karena di luar sedang hujan deras.",
            options = listOf("PAYUNG", "TOPI", "KACAMATA"),
            correctIndex = 0,
            hint = "Benda yang kita pakai agar tidak kehujanan saat di luar adalah PAYUNG."
        ),
        SentenceQuiz(
            id = 5,
            sentence = "Rani merapikan ___ setelah bangun tidur di pagi hari.",
            options = listOf("TAS", "KASUR", "PIRING"),
            correctIndex = 1,
            hint = "Merapikan kasur/tempat tidur adalah kebiasaan baik setelah bangun tidur."
        )
    )

    // Level 8: Story quizzes
    val level8Quizzes = listOf(
        StoryQuiz(
            id = 1,
            story = "Koko adalah anak yang rajin. Setiap sore, Koko suka bermain layang-layang di lapangan bersama teman-temannya. Ketika angin bertiup kencang, layang-layang Koko terbang sangat tinggi.",
            question = "Di mana Koko suka bermain layang-layang bersama temannya?",
            options = listOf("Di lapangan", "Di sekolah", "Di kamar tidur"),
            correctIndex = 0,
            hint = "Perhatikan kalimat kedua: 'Koko suka bermain layang-layang di lapangan...'"
        ),
        StoryQuiz(
            id = 2,
            story = "Mimi si kucing peliharaan Ani sangat lucu dan manja. Bulunya berwarna putih bersih seperti salju yang lembut. Setiap pagi, Ani selalu memberi Mimi segelas susu segar dalam mangkuk merah muda kesukaannya.",
            question = "Apa warna bulu Mimi si kucing peliharaan Ani?",
            options = listOf("Hitam pekat", "Putih bersih", "Cokelat muda"),
            correctIndex = 1,
            hint = "Perhatikan kalimat kedua: 'Bulunya berwarna putih bersih seperti salju...'"
        ),
        StoryQuiz(
            id = 3,
            story = "Kiki pergi tamasya ke pantai bersama ayah dan ibu pada hari Minggu. Di pantai, Kiki membuat istana pasir yang besar sekali. Ayah membantu Kiki mencari kerang-kerang kecil yang indah di tepi air.",
            question = "Kapan Kiki pergi tamasya ke pantai bersama keluarganya?",
            options = listOf("Hari Senin", "Hari Sabtu", "Hari Minggu"),
            correctIndex = 2,
            hint = "Perhatikan kalimat pertama: 'Kiki pergi tamasya ke pantai... pada hari Minggu.'"
        ),
        StoryQuiz(
            id = 4,
            story = "Deni mendapatkan hadiah sepeda baru dari kakek. Sepeda itu berwarna biru cerah dengan bel kecil yang berbunyi krincit-krincit. Deni sangat senang dan selalu rajin mengelap sepedanya setiap pagi agar selalu bersih.",
            question = "Hadiah apa yang didapatkan Deni dari kakek kesayangannya?",
            options = listOf("Sepeda baru", "Buku gambar", "Baju kaos"),
            correctIndex = 0,
            hint = "Perhatikan kalimat pertama: 'Deni mendapatkan hadiah sepeda baru...'"
        ),
        StoryQuiz(
            id = 5,
            story = "Rara suka sekali menanam bunga mawar. Di kebun kecil belakang rumahnya, ada mawar merah dan kuning yang sedang mekar indah. Rara selalu rajin menyiram bunganya dua kali sehari, yaitu pada pagi dan sore hari.",
            question = "Berapa kali sehari Rara rajin menyiram bunga mawarnya?",
            options = listOf("Satu kali", "Dua kali", "Tiga kali"),
            correctIndex = 1,
            hint = "Perhatikan kalimat terakhir: 'Rara selalu rajin menyiram bunganya dua kali sehari...'"
        )
    )
}
