import { GoogleGenerativeAI } from "@google/generative-ai";

export default async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ message: 'Cuma boleh POST ya!' });
  }

  const { budget, ingredients } = req.body;
  
  // Ambil API Key dari Environment Variable Vercel
  const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
  const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

  const prompt = `
    Anda adalah Chef Revan, asisten kuliner khusus anak kos di Indonesia.
    User punya budget: Rp ${budget}.
    Bahan yang tersedia/diminta: ${ingredients || "bebas, carikan yang murah"}.
    Berikan 1 resep kreatif yang bisa dimasak pakai rice cooker atau alat kos sederhana.
    Penting: Total biaya bahan harus di bawah Rp ${budget}.
    Berikan respon dalam format JSON MURNI tanpa markdown:
    {
      "name": "Nama Masakan",
      "cost": total_biaya_angka_saja,
      "steps": "Langkah-langkah pendek dan jelas"
    }
  `;

  try {
    const result = await model.generateContent(prompt);
    const response = await result.response;
    const text = response.text();
    
    // Membersihkan format markdown jika AI bandel ngasih ```json ... ```
    const cleanJson = text.replace(/```json/g, "").replace(/```/g, "").trim();
    const recipe = JSON.parse(cleanJson);

    res.status(200).json(recipe);
  } catch (error) {
    console.error(error);
    res.status(500).json({ 
        name: "Resep Darurat", 
        cost: 0, 
        steps: "Gagal panggil AI, coba cek koneksi atau API Key." 
    });
  }
}