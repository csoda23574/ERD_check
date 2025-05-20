package org.chefcrew.external.gemini.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GeminiRequest {
    private List<Content> contents = new ArrayList<>();

    public GeminiRequest(String text) {
        Content content = new Content();
        content.setParts(new ArrayList<>());
        content.getParts().add(new TextPart(text));
        this.contents.add(content);
    }

    @Setter
    @Getter
    public static class Content {
        private List<TextPart> parts;
        private String role;

        public Content() {
            this.role = "user";
        }
    }

    @Getter
    public static class TextPart {
        private String text;

        public TextPart(String text) {
            this.text = text;
        }
    }
}