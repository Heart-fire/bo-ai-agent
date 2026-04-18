package com.xinhuo.boaiagent.tools;


import cn.hutool.core.io.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.xinhuo.boaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF 生成工具 - 政府报告级排版
 */
@Component
public class PDFGenerationTool {

    // ── 颜色常量 ──
    private static final DeviceRgb RED_ACCENT = new DeviceRgb(0xC4, 0x1E, 0x3A);   // 政务红
    private static final DeviceRgb GOLD_ACCENT = new DeviceRgb(0xD4, 0xA0, 0x1E);   // 金色装饰线
    private static final DeviceRgb DARK_TEXT = new DeviceRgb(0x1A, 0x1A, 0x2E);      // 正文深色
    private static final DeviceRgb HEADING_COLOR = new DeviceRgb(0x8B, 0x00, 0x00);  // 标题深红
    private static final DeviceRgb LINK_COLOR = new DeviceRgb(0x1A, 0x56, 0xDB);     // 链接蓝
    private static final DeviceRgb SUBTITLE_COLOR = new DeviceRgb(0x4A, 0x4A, 0x5A); // 副标题灰
    private static final DeviceRgb LIGHT_BG = new DeviceRgb(0xF8, 0xF5, 0xF0);      // 浅米色背景
    private static final DeviceRgb BORDER_LINE = new DeviceRgb(0xD4, 0xA0, 0x1E);    // 金色边线

    // ── URL 正则 ──
    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[^\\s,，)）】\\]]+");
    private static final Pattern HEADING_PATTERN = Pattern.compile(
            "^(#{1,3})\\s+(.+)$");
    private static final Pattern NUMBERED_HEADING_PATTERN = Pattern.compile(
            "^(\\d{1,2}[、.．])\\s*(.+)$");
    private static final Pattern LINK_REF_PATTERN = Pattern.compile(
            "((?:来源|参考|引用|链接|出处|原文)[：:]\\s*)(https?://[^\\s,，)）】\\]]+)");

    /**
     * 生成 PDF 报告
     */
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
                 PdfDocument pdf = new PdfDocument(writer)) {

                // ── 字体 ──
                PdfFont songFont = PdfFontFactory.createFont("C:/Windows/Fonts/simsun.ttc,1", PdfEncodings.IDENTITY_H);
                PdfFont heiFont = PdfFontFactory.createFont("C:/Windows/Fonts/simhei.ttf", PdfEncodings.IDENTITY_H);
                PdfFont kaiFont = PdfFontFactory.createFont("C:/Windows/Fonts/simkai.ttf", PdfEncodings.IDENTITY_H);

                Document document = new Document(pdf, PageSize.A4);
                document.setMargins(36, 36, 40, 36); // 上右下左

                // ── 页脚事件（页码） ──
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler(songFont));

                // ── 提取标题 ──
                String reportTitle = extractTitle(content);

                // ── 封面页 ──
                buildCoverPage(document, reportTitle, songFont, heiFont, kaiFont);

                // ── 正文页 ──
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                buildContentPages(document, content, songFont, heiFont, kaiFont);

                document.close();
            }

            return "PDF generated successfully. Download URL: /api/ai/files/" + fileName;

        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }


    //  封面页
    private void buildCoverPage(Document document, String title, PdfFont songFont, PdfFont heiFont, PdfFont kaiFont) {
        // 顶部金色装饰线
        addDecorativeLine(document);

        document.add(new Paragraph("\n\n\n\n\n"));

        // 主标题
        Paragraph titlePara = new Paragraph(title)
                .setFont(heiFont)
                .setFontSize(26)
                .setFontColor(RED_ACCENT)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(8);
        document.add(titlePara);

        // 副标题装饰线
        Paragraph subLine = new Paragraph("—— 研 究 报 告 ——")
                .setFont(kaiFont)
                .setFontSize(14)
                .setFontColor(GOLD_ACCENT)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
        document.add(subLine);

        document.add(new Paragraph("\n\n\n"));

        // 报告信息框
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日"));

        Table infoTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(60))
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                .setMarginTop(20);

        infoTable.addCell(createInfoCell("报告日期：" + date, songFont));
        infoTable.addCell(createInfoCell("编制单位：政策通智能研究平台", songFont));
        infoTable.addCell(createInfoCell("报告性质：公开信息整理报告", songFont));

        document.add(infoTable);

        document.add(new Paragraph("\n\n\n\n\n\n\n"));

        // 底部声明
        Paragraph disclaimer = new Paragraph("本报告基于互联网公开信息整理，仅供参考")
                .setFont(songFont)
                .setFontSize(9)
                .setFontColor(SUBTITLE_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(disclaimer);

        // 底部金色装饰线
        addDecorativeLine(document);
    }

    private Cell createInfoCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11).setFontColor(DARK_TEXT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPaddingLeft(20)
                .setPaddingTop(6)
                .setPaddingBottom(6);
    }

    private void addDecorativeLine(Document document) {
        // 金色装饰横线（使用文字符号绘制，兼容性好）
        Paragraph line = new Paragraph("━".repeat(40))
                .setFontColor(GOLD_ACCENT)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(line);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  正文页
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    private void buildContentPages(Document document, String content, PdfFont songFont, PdfFont heiFont, PdfFont kaiFont) {
        String[] lines = content.split("\n");
        boolean inLinkSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // 检测标题行
            Matcher headingMatcher = HEADING_PATTERN.matcher(trimmed);
            if (headingMatcher.find()) {
                String level = headingMatcher.group(1); // #, ##, ###
                String title = headingMatcher.group(2).trim();
                inLinkSection = isLinkSection(title);
                addSectionHeading(document, title, level.length(), heiFont);
                continue;
            }

            // 检测数字标题行（一、xxx 或 1. xxx）
            Matcher numHeadingMatcher = NUMBERED_HEADING_PATTERN.matcher(trimmed);
            if (numHeadingMatcher.find()) {
                String num = numHeadingMatcher.group(1);
                String title = numHeadingMatcher.group(2).trim();
                inLinkSection = isLinkSection(title);
                addNumberedHeading(document, num, title, heiFont);
                continue;
            }

            // 链接行（在"来源"章节内的 URL 行）
            if (inLinkSection && isUrlLine(trimmed)) {
                addLinkEntry(document, trimmed, songFont, kaiFont);
                continue;
            }

            // 包含"来源：URL"引用的段落
            if (LINK_REF_PATTERN.matcher(trimmed).find()) {
                addRichParagraph(document, trimmed, songFont,heiFont,kaiFont);
                continue;
            }

            // 普通段落（可能包含 **粗体** 和 URL）
            addRichParagraph(document, trimmed, songFont,heiFont, kaiFont);
        }
    }

    /**
     * 添加章节标题（# 格式）
     */
    private void addSectionHeading(Document document, String title, int level, PdfFont heiFont) {
        float fontSize;
        float marginTop;
        float marginBottom;

        switch (level) {
            case 1:
                fontSize = 16;        // ↓ 更正式
                marginTop = 16;       // ↓ 收紧
                marginBottom = 8;
                break;
            case 2:
                fontSize = 14;
                marginTop = 12;
                marginBottom = 6;
                break;
            default:
                fontSize = 12;
                marginTop = 10;
                marginBottom = 4;
                break;
        }

        // 一级标题前加金色装饰线
        if (level == 1) {
            Paragraph line = new Paragraph("━".repeat(50))
                    .setFontColor(GOLD_ACCENT)
                    .setFontSize(6)
                    .setMarginTop(10)
                    .setMarginBottom(4);
            document.add(line);
        }

        Paragraph heading = new Paragraph(title)
                .setFont(heiFont)
                .setFontSize(fontSize)
                .setFontColor(HEADING_COLOR)
                .setMarginTop(marginTop)
                .setMarginBottom(marginBottom);
        document.add(heading);
    }

    /**
     * 添加数字编号标题
     */
    private void addNumberedHeading(Document document, String num, String title, PdfFont heiFont) {
        Paragraph line = new Paragraph("━".repeat(50))
                .setFontColor(GOLD_ACCENT)
                .setFontSize(6)
                .setMarginTop(16)
                .setMarginBottom(6);
        document.add(line);

        Paragraph heading = new Paragraph(num + " " + title)
                .setFont(heiFont)
                .setFontSize(14)
                .setMarginTop(10)
                .setMarginBottom(4)
                .setFontColor(HEADING_COLOR);
        document.add(heading);
    }

    /**
     * 添加富文本段落（解析 **粗体** 和 URL 链接）
     */
    private void addRichParagraph(Document document, String text, PdfFont songFont,PdfFont heiFont, PdfFont kaiFont) {
        Paragraph para = new Paragraph()
                .setFont(songFont)
                .setFontSize(10.5f)
                .setFontColor(DARK_TEXT)
                .setMarginTop(2)
                .setMarginBottom(2)
                .setMultipliedLeading(1.3f)
                .setFirstLineIndent(22);

        // 解析内联元素
        List<TextSegment> segments = parseInlineElements(text);
        for (TextSegment seg : segments) {
            Text textElement = new Text(seg.content);
            switch (seg.type) {
                case BOLD:
                    textElement.setFont(heiFont).setFontSize(11);
                    break;
                case URL:
                    textElement.setFont(kaiFont)
                            .setFontSize(10)
                            .setFontColor(LINK_COLOR)
                            .setUnderline()
                            .setHorizontalScaling(0.95f);
                    break;
                case LINK_REF:
                    textElement.setFont(kaiFont)
                            .setFontSize(10)
                            .setFontColor(LINK_COLOR)
                            .setUnderline();
                    break;
                default:
                    textElement.setFont(songFont).setFontSize(11);
                    break;
            }
            para.add(textElement);
        }

        document.add(para);
    }

    /**
     * 添加链接条目（来源章节中的 URL）
     */
    private void addLinkEntry(Document document, String line, PdfFont songFont, PdfFont kaiFont) {
        // 提取可能的编号和 URL
        String displayText = line;
        String url = "";

        Matcher urlMatcher = URL_PATTERN.matcher(line);
        if (urlMatcher.find()) {
            url = urlMatcher.group();
            displayText = line.replace(url, "").trim();
            // 清理多余标点
            displayText = displayText.replaceAll("^[\\s:：\\-—.\\d]+", "").trim();
        }

        if (displayText.isEmpty()) {
            displayText = "参考链接";
        }

        // 链接条目用缩进 + 特殊样式
        Paragraph linkPara = new Paragraph()
                .setMarginLeft(24)
                .setMarginTop(3)
                .setMarginBottom(3);

        // 编号圆点
        Text bullet = new Text("●  ")
                .setFont(songFont)
                .setFontSize(8)
                .setFontColor(GOLD_ACCENT);

        // 标题文字
        Text titleText = new Text(displayText + "  ")
                .setFont(songFont)
                .setFontSize(10)
                .setFontColor(DARK_TEXT);

        linkPara.add(bullet);
        linkPara.add(titleText);

        // URL 部分
        if (!url.isEmpty()) {
            Text urlText = new Text(url)
                    .setFont(kaiFont)
                    .setFontSize(9)
                    .setFontColor(LINK_COLOR)
                    .setHorizontalScaling(0.9f);
            linkPara.add(urlText);
        }

        document.add(linkPara);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  文本解析辅助
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    private enum SegmentType {
        NORMAL, BOLD, URL, LINK_REF
    }

    private static class TextSegment {
        SegmentType type;
        String content;

        TextSegment(SegmentType type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    /**
     * 解析行内元素：**粗体** 和 URL
     */
    private List<TextSegment> parseInlineElements(String text) {
        List<TextSegment> result = new ArrayList<>();

        // 先处理 "来源：URL" 模式
        Matcher linkRefMatcher = LINK_REF_PATTERN.matcher(text);
        int lastEnd = 0;

        while (linkRefMatcher.find()) {
            // 添加前面的内容
            if (linkRefMatcher.start() > lastEnd) {
                String before = text.substring(lastEnd, linkRefMatcher.start());
                result.addAll(parseBoldAndUrl(before));
            }
            // 添加"来源："文字
            result.add(new TextSegment(SegmentType.NORMAL, linkRefMatcher.group(1)));
            // 添加 URL
            result.add(new TextSegment(SegmentType.LINK_REF, linkRefMatcher.group(2)));

            lastEnd = linkRefMatcher.end();
        }

        // 处理剩余部分
        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd);
            result.addAll(parseBoldAndUrl(remaining));
        }

        return result;
    }

    /**
     * 解析 **粗体** 和裸 URL
     */
    private List<TextSegment> parseBoldAndUrl(String text) {
        List<TextSegment> result = new ArrayList<>();

        // 合并匹配：粗体或URL
        Pattern combined = Pattern.compile("(\\*\\*.+?\\*\\*)|(https?://[^\\s,，)）】\\]]+)");
        Matcher m = combined.matcher(text);
        int lastEnd = 0;

        while (m.find()) {
            // 前面的普通文字
            if (m.start() > lastEnd) {
                result.add(new TextSegment(SegmentType.NORMAL, text.substring(lastEnd, m.start())));
            }

            if (m.group(1) != null) {
                // 粗体
                String boldContent = m.group(1).replace("**", "");
                result.add(new TextSegment(SegmentType.BOLD, boldContent));
            } else if (m.group(2) != null) {
                // URL
                result.add(new TextSegment(SegmentType.URL, m.group(2)));
            }

            lastEnd = m.end();
        }

        // 尾部普通文字
        if (lastEnd < text.length()) {
            result.add(new TextSegment(SegmentType.NORMAL, text.substring(lastEnd)));
        }

        return result;
    }

    /**
     * 从内容中提取报告标题
     */
    private String extractTitle(String content) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            Matcher m = HEADING_PATTERN.matcher(trimmed);
            if (m.find()) {
                return m.group(2).trim();
            }
            // 数字编号标题
            if (trimmed.length() > 2 && trimmed.length() < 40
                    && !trimmed.startsWith("http")) {
                Matcher nm = NUMBERED_HEADING_PATTERN.matcher(trimmed);
                if (nm.find()) {
                    return nm.group(2).trim();
                }
            }
        }
        return "政策研究报告";
    }

    /**
     * 判断是否为链接/来源章节
     */
    private boolean isLinkSection(String title) {
        if (title == null) return false;
        String lower = title.toLowerCase();
        return lower.contains("来源") || lower.contains("链接") || lower.contains("参考")
                || lower.contains("出处") || lower.contains("引用") || lower.contains("reference")
                || lower.contains("source");
    }

    /**
     * 判断该行是否为 URL 行
     */
    private boolean isUrlLine(String line) {
        return line.startsWith("http://") || line.startsWith("https://")
                || URL_PATTERN.matcher(line).find();
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    //  页脚事件处理器
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    private static class FooterEventHandler extends AbstractPdfDocumentEventHandler {

        private final PdfFont font;

        FooterEventHandler(PdfFont font) {
            this.font = font;
        }

        @Override
        protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;

            PdfPage page = docEvent.getPage();
            int pageNum = docEvent.getDocument().getPageNumber(page);
            Rectangle pageSize = page.getPageSize();

            if (pageNum <= 1) return;

            PdfCanvas canvas = new PdfCanvas(
                    page.newContentStreamBefore(),
                    page.getResources(),
                    docEvent.getDocument()
            );

            float y = 52;

            canvas.setStrokeColor(GOLD_ACCENT)
                    .setLineWidth(0.5f)
                    .moveTo(pageSize.getLeft() + 60, y)
                    .lineTo(pageSize.getRight() - 60, y)
                    .stroke();

            float centerX = (pageSize.getLeft() + pageSize.getRight()) / 2;

            canvas.beginText()
                    .setFontAndSize(font, 9)
                    .moveText(centerX - 10, y - 18)
                    .showText("— " + (pageNum - 1) + " —")
                    .endText();

            canvas.release();
        }
    }
}
