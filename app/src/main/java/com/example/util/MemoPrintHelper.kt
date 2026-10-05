package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import java.io.File
import java.io.FileOutputStream

object MemoPrintHelper {

    /**
     * Renders a 7 inch height x 6 inch width slip at 200 DPI (1200 x 1400 px).
     */
    fun generateMemoBitmap(
        context: Context,
        memo: MemoEntity,
        items: List<MemoItemEntity>
    ): Bitmap {
        val width = 1200
        val height = 1400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Pure white background
        canvas.drawColor(Color.WHITE)

        val borderPaint = Paint().apply {
            color = Color.rgb(0, 137, 163)
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        val innerBorderPaint = Paint().apply {
            color = Color.rgb(200, 230, 240)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        // Draw outer & inner border
        canvas.drawRect(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat(), borderPaint)
        canvas.drawRect(26f, 26f, (width - 26).toFloat(), (height - 26).toFloat(), innerBorderPaint)

        var y = 75f

        // Bismillah in Arabic
        val bismillahPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 100, 120)
            textSize = 34f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("بِسْمِ اللهِ الرَّحْمٰنِ الرَّحِيْمِ", (width / 2).toFloat(), y, bismillahPaint)
        y += 48f

        // Shop Name: "জিহান মৎস্য বীজাগার"
        val shopTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 77, 94)
            textSize = 54f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("জিহান মৎস্য বীজাগার", (width / 2).toFloat(), y, shopTitlePaint)
        y += 38f

        // Address: জগন্নাথপুর (হাজির বাজার), মোকামতলা, বগুড়া-৫৮১০
        val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("ঠিকানা: জগন্নাথপুর (হাজির বাজার), মোকামতলা, বগুড়া-৫৮১০", (width / 2).toFloat(), y, subTextPaint)
        y += 32f

        // Phone Numbers: +8801722736633 +8801711428073
        val phonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(2, 119, 189)
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("ফোন: +8801722736633, +8801711428073", (width / 2).toFloat(), y, phonePaint)
        y += 34f

        // Director Name
        val directorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("পরিচালক: মোঃ ময়নুল সাদিক (জোহা)", (width / 2).toFloat(), y, directorPaint)
        y += 36f

        // 2-line fish breed description:
        // Line 1 (bigger)
        val breedLine1Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 96, 100)
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("এখানে উন্নতমানের রুই, মৃগেল, কাতলা, বাটা, সীলভার, গ্লাসকাপ, স্বরপুটি,", (width / 2).toFloat(), y, breedLine1Paint)
        y += 28f

        // Line 2 (smaller)
        val breedLine2Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(55, 71, 79)
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("পাঙ্গাস, হাংরি, দেশী মাগুর, কৈ, টেংরা, গুলশা, পাবদা মাছের রেনু পোনা পাওয়া যায়", (width / 2).toFloat(), y, breedLine2Paint)
        y += 30f

        // Horizontal dividing line across memo
        val linePaint = Paint().apply {
            color = Color.rgb(0, 137, 163)
            strokeWidth = 3f
        }
        canvas.drawLine(50f, y, (width - 50).toFloat(), y, linePaint)
        y += 36f

        // Serial Number & Date Time
        val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("মেমো নং: #${memo.memoNumber}", 60f, y, metaPaint)

        val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 24f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("তারিখ: ${memo.date} ${memo.time}", (width - 60).toFloat(), y, datePaint)
        y += 38f

        // Customer Details
        val infoTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(38, 50, 56)
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val infoValPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        canvas.drawText("গ্রাহকের নাম: ", 60f, y, infoTitlePaint)
        canvas.drawText(memo.customerName.ifBlank { "নগদ ক্রেতা" }, 210f, y, infoValPaint)
        y += 32f

        canvas.drawText("ঠিকানা: ", 60f, y, infoTitlePaint)
        canvas.drawText(memo.customerAddress.ifBlank { "-" }, 150f, y, infoValPaint)
        y += 32f

        canvas.drawText("মোবাইল: ", 60f, y, infoTitlePaint)
        canvas.drawText(memo.customerPhone.ifBlank { "-" }, 160f, y, infoValPaint)
        y += 35f

        // Table Header
        val headerBgPaint = Paint().apply {
            color = Color.rgb(224, 247, 250)
        }
        canvas.drawRect(50f, y - 24f, (width - 50).toFloat(), y + 16f, headerBgPaint)

        val thPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 96, 100)
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("ক্রম", 65f, y, thPaint)
        canvas.drawText("পণ্যের বিবরণ", 140f, y, thPaint)
        canvas.drawText("পরিমাণ", 620f, y, thPaint)
        canvas.drawText("দর (টাকা)", 800f, y, thPaint)
        canvas.drawText("মোট (টাকা)", 1000f, y, thPaint)
        y += 36f

        val itemTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 22f
        }

        items.take(10).forEachIndexed { index, item ->
            canvas.drawText("${index + 1}", 75f, y, itemTextPaint)
            canvas.drawText(item.productName, 140f, y, itemTextPaint)
            canvas.drawText("${item.quantity} ${item.unit}", 620f, y, itemTextPaint)
            canvas.drawText(String.format("%.0f", item.unitPrice), 820f, y, itemTextPaint)
            canvas.drawText(String.format("%.0f", item.totalPrice), 1020f, y, itemTextPaint)

            y += 28f
            val rowLine = Paint().apply {
                color = Color.rgb(230, 230, 230)
                strokeWidth = 1f
            }
            canvas.drawLine(50f, y - 6f, (width - 50).toFloat(), y - 6f, rowLine)
            y += 8f
        }

        y = y.coerceAtLeast(960f)

        // Total Section
        val totalDivider = Paint().apply {
            color = Color.rgb(0, 137, 163)
            strokeWidth = 2f
        }
        canvas.drawLine(50f, y, (width - 50).toFloat(), y, totalDivider)
        y += 40f

        val totalTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(38, 50, 56)
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val totalValPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 77, 94)
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        canvas.drawText("সর্বমোট মূল্য:", 700f, y, totalTitlePaint)
        canvas.drawText("৳ ${String.format("%.0f", memo.totalAmount)}", (width - 70).toFloat(), y, totalValPaint)
        y += 34f

        canvas.drawText("পরিশোধিত টাকা:", 700f, y, totalTitlePaint)
        canvas.drawText("৳ ${String.format("%.0f", memo.paidAmount)}", (width - 70).toFloat(), y, totalValPaint)
        y += 34f

        val dueValPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (memo.dueAmount > 0) Color.rgb(211, 47, 47) else Color.rgb(46, 125, 50)
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("বকেয়া টাকা:", 700f, y, totalTitlePaint)
        canvas.drawText("৳ ${String.format("%.0f", memo.dueAmount)}", (width - 70).toFloat(), y, dueValPaint)
        y += 65f

        // Bottom Signatures
        val sigLinePaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 1.5f
        }
        canvas.drawLine(80f, y, 320f, y, sigLinePaint)
        canvas.drawLine((width - 340).toFloat(), y, (width - 80).toFloat(), y, sigLinePaint)
        y += 30f

        val sigTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("ক্রেতার স্বাক্ষর", 140f, y, sigTextPaint)
        canvas.drawText("বিক্রেতা স্বাক্ষর", (width - 250).toFloat(), y, sigTextPaint)
        y += 32f

        val adminNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 100, 120)
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val adminDisplayName = memo.adminName.ifBlank { "এডমিন" }
        canvas.drawText("($adminDisplayName)", (width - 190).toFloat(), y, adminNamePaint)
        y += 48f

        // Notice: "এখানে মাছের খাদ্য ও ওষুধ পাওয়া যায়" - Bold & Large
        val footerNoticePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 77, 94)
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val footerBg = Paint().apply {
            color = Color.rgb(238, 250, 252)
        }
        canvas.drawRoundRect(60f, y - 32f, (width - 60).toFloat(), y + 20f, 12f, 12f, footerBg)
        canvas.drawText("★ এখানে মাছের খাদ্য ও ওষুধ পাওয়া যায় ★", (width / 2).toFloat(), y, footerNoticePaint)

        return bitmap
    }

    /**
     * Standard built-in Android PrintManager document adapter.
     */
    fun printMemo(context: Context, memo: MemoEntity, items: List<MemoItemEntity>) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Memo_${memo.memoNumber}_ZihanFish"

        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder("memo_${memo.memoNumber}.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                if (destination == null) {
                    callback?.onWriteFailed("Destination is null")
                    return
                }
                val pdfDocument = PdfDocument()
                try {
                    // Page size: 6 inches x 7 inches (at 72 points/inch: 432 x 504)
                    val pageInfo = PdfDocument.PageInfo.Builder(432, 504, 1).create()
                    val page = pdfDocument.startPage(pageInfo)

                    val bitmap = generateMemoBitmap(context, memo, items)
                    val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
                    val dstRect = Rect(0, 0, 432, 504)
                    page.canvas.drawBitmap(bitmap, srcRect, dstRect, null)

                    pdfDocument.finishPage(page)

                    val outputStream = FileOutputStream(destination.fileDescriptor)
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()

                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    pdfDocument.close()
                }
            }
        }

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A5)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .build()

        printManager.print(jobName, printAdapter, printAttributes)
    }

    /**
     * Shares the formatted memo slip as an image file.
     */
    fun shareMemo(context: Context, memo: MemoEntity, items: List<MemoItemEntity>) {
        try {
            val bitmap = generateMemoBitmap(context, memo, items)
            val cachePath = File(context.cacheDir, "memos")
            cachePath.mkdirs()
            val file = File(cachePath, "memo_${memo.memoNumber}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "মেমো নং #${memo.memoNumber} - জিহান মৎস্য বীজাগার")
                putExtra(Intent.EXTRA_TEXT, "জিহান মৎস্য বীজাগার\nমেমো নং #${memo.memoNumber}\nক্রেতা: ${memo.customerName}\nমোট: ৳${memo.totalAmount}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "মেমো শেয়ার করুন"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback plain text share
            val shareText = """
                জিহান মৎস্য বীজাগার
                ঠিকানা: জগন্নাথপুর (হাজির বাজার), মোকামতলা, বগুড়া-৫৮১০
                ফোন: +8801722736633, +8801711428073
                
                মেমো নং: #${memo.memoNumber}
                তারিখ: ${memo.date} ${memo.time}
                ক্রেতা: ${memo.customerName} (${memo.customerPhone})
                মোট মূল্য: ৳${memo.totalAmount}
                পরিশোধ: ৳${memo.paidAmount}
                বকেয়া: ৳${memo.dueAmount}
            """.trimIndent()
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(textIntent, "মেমো শেয়ার করুন"))
        }
    }
}
