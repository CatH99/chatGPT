package com.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.poi.xwpf.usermodel.*;


// import com.spire.pdf.*;


public class PdfToWord {
    // public static void convertToWord(String PDFPath, String WordPath) throws IOException {
        
    //     PdfDocument doc = new PdfDocument();
    //     //Load the sample PDF file
    //     doc.loadFromFile(PDFPath);


    //     //Save as. docx file
    //     doc.saveToFile(WordPath,FileFormat.DOCX);
    //     doc.close();
    //     }
    // }

    public static void convertToWord(String PDFPath, String WordPath) throws IOException {
        // Create a wrapper object to hold XWPFDocument
        class DocumentWrapper {
            XWPFDocument doc = new XWPFDocument();
        }

        final DocumentWrapper docWrapper = new DocumentWrapper(); // Make it effectively final

        try (PDDocument pdfDocument = PDDocument.load(new File(PDFPath))) {
            PDFTextStripper stripper = new PDFTextStripper() {
                StringBuilder line = new StringBuilder();
                XWPFParagraph paragraph = docWrapper.doc.createParagraph();

                protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                    line.append(text);
                }

                protected void writeLineSeparator() throws IOException {
                    XWPFRun run = paragraph.createRun();
                    run.setText(line.toString());
                    line.setLength(0);
                    paragraph = docWrapper.doc.createParagraph();
                    paragraph.setSpacingAfter(10); // Adjust the spacing between paragraphs as needed
                }
            };

            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(pdfDocument.getNumberOfPages());

            stripper.writeText(pdfDocument, new NullWriter()); // PDFTextStripper writes to System.out by default, redirect it to NullWriter to suppress output

            try (FileOutputStream out = new FileOutputStream(WordPath)) {
                docWrapper.doc.write(out);
            }
        } catch (IOException e) {
            // Handle the exception
        }

        System.out.println("PDF to Word conversion completed successfully!");
        }
        
        private static class NullWriter extends java.io.Writer {
            public void write(char[] cbuf, int off, int len) throws IOException {
            }

            public void flush() throws IOException {
            }

            public void close() throws IOException {
            }
        }
}
