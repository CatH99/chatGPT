package com.convert;

// import org.apache.poi.xwpf.usermodel.XWPFDocument;
// import org.apache.poi.xwpf.usermodel.XWPFParagraph;
// import org.apache.poi.xwpf.usermodel.XWPFRun;
// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.pdmodel.PDPage;
// import org.apache.pdfbox.pdmodel.common.PDRectangle;
// import org.apache.pdfbox.pdmodel.PDPageContentStream;
// import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class WordToPDF {

    public static void convertToPDF(String wordPath, String pdfPath) throws IOException {
        try {
            InputStream templateInputStream = new FileInputStream(wordPath);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

            String outputfilepath = pdfPath;
            FileOutputStream os = new FileOutputStream(outputfilepath);
            Docx4J.toPDF(wordMLPackage,os);
            os.flush();
            os.close();
        } catch (Throwable e) {

            e.printStackTrace();
        } 

        System.out.println("Word to PDF conversion completed successfully!");
    }

    // public static void main(String[] args) {
    //     try {
    //         String wordFilePath = "pdftranslate/translate/src/outputFile/Translated PDF 1_JP.docx";
    //         String pdfFilePath = "pdftranslate/translate/src/outputFile/Translated PDF 1_JP.pdf";
    //         convertToPDF(wordFilePath, pdfFilePath);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
