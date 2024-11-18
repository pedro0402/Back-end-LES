package br.com.les.file_storage_example_les.service;

public class TextFormatter {

    public static String formatText(String text) {
        String formattedText = text.replaceAll("\\s+", " ").trim();

        if (formattedText.length() > 0) {
            formattedText = formattedText.replaceAll("[,\\s]*$", "");
        }

        if (!formattedText.endsWith(".")) {
            formattedText += ".";
        }

        return formattedText;
    }
}

