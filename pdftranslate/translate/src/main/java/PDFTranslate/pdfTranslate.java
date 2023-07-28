package PDFTranslate;

import java.io.File;
import java.io.IOException;

import com.convert.PdfToWord;
import com.convert.WordToPDF;


import com.translate.prompt;
import com.translate.ChatGPTtranslate;

import com.FileProcess.GetFileName;
// import com.FileProcess.DeleteFile;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;




public class pdfTranslate {

    // private static final Logger LOGGER = LoggerFactory.getLogger(pdfTranslate.class);
    public static void main(String[] args) {

        String UntranslatePdfPath = "pdftranslate/translate/src/inputFile/PDF 1_JP.pdf";
        String filename = GetFileName.extractFilenameFromPath(UntranslatePdfPath);

        // Generate the modified file paths
        String UntranslateDocxPath = "pdftranslate/translate/src/outputFile/" + filename + ".docx";  
        String TranslateDocxPath = "pdftranslate/translate/src/outputFile/Translated " + filename + ".docx";
        String TranslatePdfPath = "pdftranslate/translate/src/outputFile/Translated " + filename + ".pdf";
        String From_lang = "Japanese";
        String To_lang = "Vietnamese";
        String System_prompt = prompt.GeneratePrompt(From_lang,To_lang);
        // System.out.println(System_prompt);
        // System.out.println(filename);

        File UntranslateDocxFile = new File(UntranslateDocxPath);
        if (!UntranslateDocxFile.exists()) {
        	try {
            PdfToWord.convertToWord(UntranslatePdfPath, UntranslateDocxPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        	System.out.println("PDF file is converted to Docx before");
        }
        File TranslateDocxFile = new File(TranslateDocxPath);
        if (!TranslateDocxFile.exists()) {
            try {
                System.out.println("Translating...");
                ChatGPTtranslate.replaceParagraphInDocx(UntranslateDocxPath, TranslateDocxPath, System_prompt);
                System.out.println("Text replaced successfully.");
                
                ChatGPTtranslate.translateTableInDocx(TranslateDocxPath, TranslateDocxPath, System_prompt);
                System.out.println("Table translated successfully.");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Docx file is translated before");
        }

        File TranslatePdfFile = new File(TranslatePdfPath);
        if (!TranslatePdfFile.exists()) {
            try {
            WordToPDF.convertToPDF(TranslateDocxPath, TranslatePdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        } else {
            System.out.println("Translated PDF file is exsist");
        }
        


    }
    
}
