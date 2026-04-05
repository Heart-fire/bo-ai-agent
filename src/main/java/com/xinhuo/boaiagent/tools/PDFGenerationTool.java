package com.xinhuo.boaiagent.tools;


import cn.hutool.core.io.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.xinhuo.boaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PDF 生成工具
 */
@Component
public class PDFGenerationTool {

    @Tool(description = """
        Generate a PDF report from the provided text content.
        Use this tool when the task requires creating a final document or report.
        """, returnDirect = false)
    public String generatePDF(

            @ToolParam(description = "The name of the PDF file, e.g. report.pdf")
            String fileName,

            @ToolParam(description = "The main text content to include in the PDF report")
            String content
    ) {

        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;

        try {

            FileUtil.mkdir(fileDir);

            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // 设置字体
                PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/simsun.ttc,1", PdfEncodings.IDENTITY_H);
                document.setFont(font);

                Paragraph paragraph = new Paragraph(content);

                document.add(paragraph);
            }

            return "PDF generated successfully. Download URL: /api/ai/files/" + fileName;

        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}