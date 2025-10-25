package com.worken.backend.http;

import com.worken.backend.job.Job;
import com.worken.backend.job.JobRequest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class JsonUtil {

    private static final Pattern STRING_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern NUMBER_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

    private JsonUtil() {
    }

    static String toJson(Job job) {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
                .append("\"id\":").append(job.getId()).append(',')
                .append("\"title\":\"").append(escape(job.getTitle())).append('\"').append(',')
                .append("\"description\":\"").append(escape(job.getDescription())).append('\"').append(',')
                .append("\"category\":\"").append(escape(job.getCategory())).append('\"').append(',')
                .append("\"city\":\"").append(escape(job.getCity())).append('\"').append(',')
                .append("\"payment\":").append(String.format(Locale.US, "%.2f", job.getPayment())).append(',')
                .append("\"contactPhone\":\"").append(escape(job.getContactPhone())).append('\"').append(',')
                .append("\"contactEmail\":\"").append(escape(job.getContactEmail())).append('\"').append(',')
                .append("\"publishedAt\":\"").append(job.getPublishedAt()).append('\"')
                .append('}');
        return sb.toString();
    }

    static String toJson(List<Job> jobs) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < jobs.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(toJson(jobs.get(i)));
        }
        sb.append(']');
        return sb.toString();
    }

    static String error(String message) {
        return '{' + "\"error\":\"" + escape(message) + "\"}";
    }

    static JobRequest parseJobRequest(String body) {
        Map<String, String> values = new HashMap<>();
        Matcher stringMatcher = STRING_FIELD.matcher(body);
        while (stringMatcher.find()) {
            values.put(stringMatcher.group(1), stringMatcher.group(2));
        }
        Matcher numberMatcher = NUMBER_FIELD.matcher(body);
        while (numberMatcher.find()) {
            values.put(numberMatcher.group(1), numberMatcher.group(2));
        }
        String title = values.get("title");
        String description = values.get("description");
        String category = values.get("category");
        String city = values.get("city");
        double payment = parseDouble(values.get("payment"));
        String contactPhone = values.getOrDefault("contactPhone", "");
        String contactEmail = values.getOrDefault("contactEmail", "");
        LocalDate publishedAt = parseDate(values.get("publishedAt"));
        return new JobRequest(title, description, category, city, payment, contactPhone, contactEmail, publishedAt);
    }

    private static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
