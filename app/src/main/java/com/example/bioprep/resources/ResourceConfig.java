package com.example.bioprep.resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized educational resource configuration (Requirement 6 & 7).
 * Easily updated with production URLs, board links, and YouTube video IDs.
 */
public class ResourceConfig {

    public static class WebResource {
        public final String id;
        public final String title;
        public final String category; // "WEBSITE" or "YOUTUBE"
        public final String description;
        public final String url;
        public final String actionLabel;

        public WebResource(String id, String title, String category, String description, String url, String actionLabel) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.description = description;
            this.url = url;
            this.actionLabel = actionLabel;
        }

        public boolean isYouTube() {
            return "YOUTUBE".equalsIgnoreCase(category) || url.contains("youtube.com") || url.contains("youtu.be");
        }
    }

    public static List<WebResource> getUsefulWebsites() {
        List<WebResource> list = new ArrayList<>();
        list.add(new WebResource(
                "web_nctb",
                "NCTB Bangladesh Official Textbook Portal",
                "WEBSITE",
                "Access national board textbooks, official syllabi, and curricular guidelines directly from the National Curriculum and Textbook Board.",
                "http://www.nctb.gov.bd",
                "Open Website"
        ));
        list.add(new WebResource(
                "web_board",
                "Dhaka Education Board Examination Portal",
                "WEBSITE",
                "Official notices, HSC board question archives, answer sheets, and grading rubrics published by the Inter-Board Coordination Committee.",
                "https://dhakaeducationboard.gov.bd",
                "Open Website"
        ));
        list.add(new WebResource(
                "web_cell_bio",
                "3D Cellular Biology Visual Explorer",
                "WEBSITE",
                "Interactive 3D microscopic visualization of organelle ultrastructure, eukaryotic membranes, and active transport proteins.",
                "https://en.wikipedia.org/wiki/Cell_biology",
                "Open Website"
        ));
        return list;
    }

    public static List<WebResource> getRecommendedResources() {
        List<WebResource> list = new ArrayList<>();
        list.add(new WebResource(
                "yt_lectures",
                "HSC Biology Video Lectures & CQ Solving",
                "YOUTUBE",
                "Comprehensive Bangla video lecture tutorials covering high-difficulty Botany & Zoology creative questions, mnemonics, and medical admission drills.",
                "https://www.youtube.com/results?search_query=HSC+Biology+Classes+Bangla",
                "Watch on YouTube"
        ));
        list.add(new WebResource(
                "yt_diagrams",
                "Biology Diagram Drawing & Practical Lab Guides",
                "YOUTUBE",
                "Step-by-step masterclasses on sketching China rose floral diagrams, heart longitudinal sections, nephrons, and stomatal apparatus.",
                "https://www.youtube.com/results?search_query=HSC+Biology+Diagram+Drawing+Guide",
                "Watch on YouTube"
        ));
        list.add(new WebResource(
                "web_campbell",
                "Campbell Biology International Study Portal",
                "WEBSITE",
                "Worldwide benchmark reference modules for cellular genetics, comparative animal physiology, and bioenergetics.",
                "https://en.wikipedia.org/wiki/Campbell_Biology",
                "Open Website"
        ));
        return list;
    }
}

