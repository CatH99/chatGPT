package com.translate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;


public class ChatGPTtranslate {
    public static void replaceParagraphInDocx(String inputFilePath, String outputFilePath, String prompt) throws IOException {
        // System.out.println("Processing replaceParagraphInDocx ...");
        FileInputStream fis = new FileInputStream(inputFilePath);
        XWPFDocument document = new XWPFDocument(fis);

        for (XWPFParagraph paragraph : document.getParagraphs()) {
        	String textToTranslate = paragraph.getText();
            // System.out.println( "1 ->>" + textToTranslate);
        	if (textToTranslate.isBlank()) {
                // System.out.println("1");
        		markAsTranslated(paragraph);
        	} else if(textToTranslate.equals(" ") || isNumber(textToTranslate) || !containsSpecialCharacters(textToTranslate)) {
        		markAsTranslated(paragraph);
                // System.out.println("2" + textToTranslate);
        	} else if(!textToTranslate.isEmpty() && !isTranslated(paragraph)) {
        		// System.out.println("3");
                String translatedParagraph = handleTranslationError(textToTranslate, prompt);
                if (paragraph.getText().equals(textToTranslate)) {
                    // System.out.println("4");
//                	paragraph.getRuns().get(0).setText(translatedParagraph + "\n", 0);
                	replaceParagraphText(paragraph,translatedParagraph);
                	System.out.println(textToTranslate + " replaced by " + translatedParagraph);
                	markAsTranslated(paragraph);
                }
        	}
            
        }

        FileOutputStream fos = new FileOutputStream(outputFilePath);
        document.write(fos);

        fos.close();
        fis.close();
        document.close();
    }

    public static String ChatGptTranslate(String textToTranslate, String prompt) {
        // System.out.println("Processing ChatGptTranslation ...");
		String OPENAI_TOKEN = "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";
		
		OpenAiService service = new OpenAiService(OPENAI_TOKEN);
		
		List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
        messages.add(systemMessage);

        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),"Translate this: " + textToTranslate);
        messages.add(chatMessage);
        
        
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(messages)
                .build();
        
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        String translatedText = responseMessage.getContent();
        
		return translatedText;
	}

    public static String handleTranslationError(String textToTranslate, String prompt) {
        // System.out.println("Processing handleTranslationError ...");
        int maxRetries = 3;
        int retries = 0;
        String translatedText = null;

        while (retries < maxRetries) {
            try {
                translatedText = ChatGptTranslate(textToTranslate, prompt);
                break; // If successful, exit the loop
            } catch (Exception e) {
                retries++;
                System.err.println("Error occurred while translating. Retrying attempt " + retries + "...");
                try {
                    TimeUnit.SECONDS.sleep(15); // Add a delay between retries
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (translatedText == null) {
            System.err.println("Failed to translate after multiple retries.");
        }

        return translatedText;
    }

    public static void translateTableInDocx(String inputFilePath, String outputFilePath, String prompt) throws IOException {
        // System.out.println("Processing translateTableInDocx ...");
        FileInputStream fis = new FileInputStream(inputFilePath);
        XWPFDocument document = new XWPFDocument(fis);

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    String textToTranslate = cell.getText();
                    if (!textToTranslate.isEmpty()) {
                        String translatedCell = handleTranslationError(textToTranslate, prompt);
                        // Check if the cell contains paragraphs and runs before setting the translated text
                        if (!cell.getParagraphs().isEmpty() && !cell.getParagraphArray(0).getRuns().isEmpty()) {
                            if (cell.getText().equals(textToTranslate)) {
                                cell.getParagraphArray(0).getRuns().get(0).setText(translatedCell, 0);
                                System.out.println(textToTranslate + " replaced by " + translatedCell);
                            }
                        } else {
                            System.out.println("Skipping translation for an empty cell.");
                        }
                    }
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(outputFilePath);
        document.write(fos);

        fos.close();
        fis.close();
        document.close();
    }

    public static void replaceParagraphText(XWPFParagraph paragraph, String newText) {
        // System.out.println("Processing replaceParagraphText ...");
        // Clear all existing runs from the paragraph
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        // Create a new run with the translated text
        XWPFRun run = paragraph.createRun();
        run.setText(newText);
        if (!paragraph.getRuns().isEmpty()) {
        	XWPFRun firstRun = paragraph.getRuns().get(0);
            run.setFontFamily("Arial");
            // run.setFontFamily(firstRun.getFontFamily());
            run.setFontSize(10);
            run.setBold(firstRun.isBold());
            run.setItalic(firstRun.isItalic());
            run.setUnderline(firstRun.getUnderline());
            run.setTextPosition(firstRun.getTextPosition());
            run.setColor(firstRun.getColor());
        }
}

    public static boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean containsSpecialCharacters(String text) {
    	String regex = "[^a-zA-Z0-9\\s]"; // This will match any character that is not a letter, digit, or whitespace.

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(text);

        // Check if the text contains any special characters
        return matcher.find();
    }

    public static Set<String> translatedParagraphs = new HashSet<>();

    public static boolean isTranslated(XWPFParagraph paragraph) {
        return translatedParagraphs.contains(paragraph.getParagraphText());
    }

    public static void markAsTranslated(XWPFParagraph paragraph) {
        translatedParagraphs.add(paragraph.getParagraphText());
    }
}