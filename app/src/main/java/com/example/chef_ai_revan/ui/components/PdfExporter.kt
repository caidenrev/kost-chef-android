package com.example.chef_ai_revan.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfExporter {
    fun exportRecipePdf(context: Context, title: String, cost: String, description: String, ingredients: List<String>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 Size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.rgb(230, 57, 70) // NeoPrimary #E63946
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val headingPaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        // Draw header branding
        canvas.drawText("MASAKIN by Revan", 40f, 60f, titlePaint)
        canvas.drawText("Asisten Resep Hemat Tanggal Tua Anda", 40f, 85f, textPaint)
        canvas.drawLine(40f, 100f, 555f, 100f, Paint().apply { 
            color = Color.BLACK
            strokeWidth = 3f 
        })

        // Draw Recipe Info
        canvas.drawText("NAMA RESEP: " + title.uppercase(), 40f, 140f, headingPaint)
        canvas.drawText("ESTIMASI HARGA TOTAL: $cost", 40f, 170f, Paint(textPaint).apply { isFakeBoldText = true })

        // Draw Description
        canvas.drawText("CARA MEMASAK & KETERANGAN:", 40f, 210f, headingPaint)
        
        var yPos = 240f
        // Simple word wrapping
        val descLines = description.chunked(60)
        descLines.forEach { line ->
            canvas.drawText(line, 40f, yPos, textPaint)
            yPos += 20f
        }

        yPos += 20f
        canvas.drawText("BAHAN-BAHAN YANG DIBUTUHKAN:", 40f, yPos, headingPaint)
        yPos += 30f

        ingredients.forEach { ing ->
            canvas.drawText("•   $ing", 50f, yPos, textPaint)
            yPos += 22f
        }

        // Draw Footer
        canvas.drawLine(40f, 780f, 555f, 780f, Paint().apply { 
            color = Color.BLACK
            strokeWidth = 1f 
        })
        canvas.drawText("Dibuat oleh MASAKIN by Revan - Makan enak meski akhir bulan!", 40f, 800f, Paint(textPaint).apply { textSize = 10f })

        pdfDocument.finishPage(page)

        // Save and share the document
        sharePdf(context, pdfDocument, "Resep_$title")
    }

    fun exportGroceryPdf(context: Context, items: List<String>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.rgb(0, 220, 102) // Green Accent
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val headingPaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        // Draw header
        canvas.drawText("DAFTAR BELANJA WARUNG", 40f, 60f, titlePaint)
        canvas.drawText("Daftar Belanja Pasar & Warung Kelontong Anda", 40f, 85f, textPaint)
        canvas.drawLine(40f, 100f, 555f, 100f, Paint().apply { 
            color = Color.BLACK
            strokeWidth = 3f 
        })

        canvas.drawText("CATATAN BELANJAAN:", 40f, 145f, headingPaint)
        
        var yPos = 180f
        if (items.isEmpty()) {
            canvas.drawText("Daftar belanja Anda kosong! Tambahkan bahan-bahan resep atau input manual.", 40f, yPos, textPaint)
        } else {
            items.forEach { item ->
                canvas.drawText("[  ]   $item", 50f, yPos, textPaint)
                yPos += 24f
            }
        }

        // Draw Footer
        canvas.drawLine(40f, 780f, 555f, 780f, Paint().apply { 
            color = Color.BLACK
            strokeWidth = 1f 
        })
        canvas.drawText("Dibuat oleh MASAKIN by Revan - Makan enak meski akhir bulan!", 40f, 800f, Paint(textPaint).apply { textSize = 10f })

        pdfDocument.finishPage(page)

        // Save and share the document
        sharePdf(context, pdfDocument, "Daftar_Belanja")
    }

    private fun sharePdf(context: Context, pdfDocument: PdfDocument, filename: String) {
        val sanitizedFilename = filename.replace(" ", "_") + ".pdf"
        val cachePath = File(context.cacheDir, "pdfs")
        cachePath.mkdirs()
        val file = File(cachePath, sanitizedFilename)
        
        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mengekspor PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            pdfDocument.close()
            return
        } finally {
            pdfDocument.close()
        }

        // Share using Android FileProvider
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Ekspor PDF via:"))
    }
}
